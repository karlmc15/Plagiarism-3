package plagiarism;

import com.wcohen.ss.Levenstein;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class Plagiarism {

    public static Levenstein lev = new Levenstein();

    // Key is term
    // Value is array list (postings list) of string arrays (postings) of the form [docName, frequency, termFrequency (tf), tF-idf weight, match type, text]  
    // Key is term
    // Value is double array containing [no. of documents in which this term occurs, inverse document frequency (idf)]
    public static ConcurrentHashMap<String, Integer> allDocPairs = new ConcurrentHashMap<String, Integer>();
    public static ConcurrentHashMap<String, Integer> resultsDocPairs = new ConcurrentHashMap<String, Integer>();
    public static String directory = "";
    public static ArrayList<String> matchingTokens = new ArrayList<String>();
    public static ArrayList<String> knownPlagiarism = new ArrayList<String>();
    public static ConcurrentHashMap<String, ArrayList<String[]>> dictionaryMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Double[]> inverseDocFreqMap = new ConcurrentHashMap<>();
    public static ArrayList<String> tokens = new ArrayList<>();

    public static DecimalFormat df = new DecimalFormat("#.###");

    //CONSTANTS
    final static int DOC_COUNT = 0;
    final static int IDF = 1;
    final static int DOC_NAME = 0;
    final static int FREQ = 1;
    final static int TERM_FREQ = 2; //the frequency divided by the document length
    final static int TFIDF_WEIGHT = 3; // log_e(Total number of documents / Number of documents with term t in it)
    final static int MATCH_TYPE = 4;
    final static int TEXT = 5;
    final static int PRECISION = 0;
    final static int RECALL = 1;

    public Plagiarism() {
    }

    public static String displayResults(double minIDF, double minTFIDF, int minMatchingTokens, String tokenizationType,
            boolean includeApproximateMatches, File directory) {

        String output = "";

        generateInvertedIndex(includeApproximateMatches, tokenizationType, directory);
        countMatches(minIDF, minTFIDF);
        generateResults(allDocPairs, minMatchingTokens);
        output = output + getResults();

        return output;
    }

    public static void generateInvertedIndex(boolean includeApproximateMatches, String tokenizationType, File directory) { //NB Change nGramSize to tokenizationType

        dictionaryMap.clear();
        inverseDocFreqMap.clear();

        //read all files in a directory
        //http://stackoverflow.com/questions/4917326/how-to-iterate-over-the-files-of-a-certain-directory-in-java 
        File[] files = directory.listFiles();
        int collectionSize = files.length; //total number of docs in collection

        ReadFile rf = new ReadFile();
        TokenGenerator generator = new TokenGenerator();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                String docName = files[i].getName();
                try {
                    String docContents = rf.readFile(files[i].toString());
                    StringTokenizer st = new StringTokenizer(docContents);
                    double docLength = st.countTokens();

                    tokens = generator.generateTokens(docContents, tokenizationType);
                    calculateTFandIDF(docContents, docName, collectionSize, docLength);

                } catch (IOException ioe) {
                    System.out.println(ioe);
                }
            }
        }
        //re-calculate with approximate values
        if (includeApproximateMatches) {
            int count = 0;
            for (int i = 0; i < files.length; i++) {
                count++;
                if (files[i].isFile()) {
                    String docName = files[i].getName();

                    try {

                        String docContents = rf.readFile(files[i].toString());
                        StringTokenizer tokenizer = new StringTokenizer(docContents);
                        double docLength = tokenizer.countTokens();
                        System.out.println("new doc: " + docName + " count: " + count);
                        long startTime = System.nanoTime();

                        tokens = generator.generateTokens(docContents, tokenizationType);
                        reCalculateTFandIDFwithApproxValues(docContents, docName, collectionSize, docLength);

                        long endTime = System.nanoTime();
                        System.out.println("that took :" + (endTime - startTime));
                    } catch (IOException ioe) {
                        System.out.println("error" + ioe);
                    }
                }
            }
        }

        calculateTFIDF();

    }

    public static void calculateTFandIDF(String docContents, String docName, int collectionSize,
            double docLength) {
        ArrayList<String[]> currentPostingsList;
        String[] posting = new String[6];

        int position = 0;
        boolean documentMatch = false;

        for (int j = 0; j < tokens.size(); j++) {
            String currentToken = tokens.get(j);
            if (dictionaryMap.isEmpty()) {

                Double[] doubleArray = {1.0, Math.log(collectionSize / 1.0)}; //1.0 because this is the first occurance
                inverseDocFreqMap.put(currentToken, doubleArray);

                double newTermFrequency = 1.0 / docLength;
                posting = new String[]{docName, "1", String.valueOf(newTermFrequency), "0", "Exact Match", ""};
                ArrayList<String[]> newPostingsList = new ArrayList<>();
                newPostingsList.add(posting);
                dictionaryMap.put(currentToken, newPostingsList);

            } else {
                if (dictionaryMap.containsKey(currentToken)) { //word is in hashmap - 2 possibilities: in this doc, or not
                    currentPostingsList = (ArrayList) dictionaryMap.get(currentToken); //get the arraylist

                    for (int i = 0; i < currentPostingsList.size(); i++) {
                        if (currentPostingsList.get(i)[DOC_NAME].equals(docName)) { //if word already in this document
                            documentMatch = true;
                            posting = currentPostingsList.get(i);
                            position = i;
                            break;
                        }
                    }
                    if (documentMatch) {

                        //iDF doesn't change; update TF
                        int newFrequency = (Integer.parseInt(posting[FREQ]) + 1);
                        double newTermFrequency = newFrequency / docLength;
                        posting[FREQ] = String.valueOf(newFrequency);
                        posting[TERM_FREQ] = String.valueOf(newTermFrequency);
                        currentPostingsList.set(position, posting);
                        dictionaryMap.put(currentToken, currentPostingsList);
                        documentMatch = false;

                    } else {

                        // 1) update inverseDocFreqMap
                        Double[] idFArray = (Double[]) inverseDocFreqMap.get(currentToken);
                        Double noOfDocuments = idFArray[DOC_COUNT] + 1.0;
                        idFArray[DOC_COUNT] = noOfDocuments;
                        idFArray[1] = Math.log(collectionSize / noOfDocuments); //recalculate IDF
                        inverseDocFreqMap.put(currentToken, idFArray);

                        // 2) add new posting(array) 
                        double newTermFrequency = 1.0 / docLength;
                        double newTFIDFweight = idFArray[IDF] * newTermFrequency;
                        String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), String.valueOf(newTFIDFweight), "Exact Match", ""};
                        currentPostingsList.add(newPosting);
                    }

                } else { //word not in hashmap - add new array and arraylist

                    Double[] doubleArray = {1.0, Math.log(collectionSize / 1.0)};
                    inverseDocFreqMap.put(currentToken, doubleArray);

                    double newTermFrequency = 1.0 / docLength;
                    String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), "0", "Exact Match", ""};
                    ArrayList<String[]> newPostingsList = new ArrayList<>();
                    newPostingsList.add(newPosting);
                    dictionaryMap.put(currentToken, newPostingsList);
                }
            }
        }
    }

    public static void calculateTFandIDFapproxNEW(String docContents, String docName, int collectionSize,
            double docLength) {

        System.out.println("calculateTFandIDFapproxNEW running");
        ArrayList<String[]> currentPostingsList;
        String[] posting = new String[6];

        int position = 0;
        boolean sameDoc = false;

        for (int j = 0; j < tokens.size(); j++) {
            String currentToken = tokens.get(j).toString();
            if (dictionaryMap.isEmpty()) { //add first term
                //add this occurence to the inverseDocFreqMap
                Double[] doubleArray = {1.0, Math.log(collectionSize / 1.0)}; //1.0 because this is the first occurance
                inverseDocFreqMap.put(currentToken, doubleArray);
                //add this occurence to the dictionaryMap
                double newTermFrequency = 1.0 / docLength;
                String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), "0", "Exact Match", ""};
                ArrayList<String[]> newPostingsList = new ArrayList<String[]>();
                newPostingsList.add(newPosting);
                dictionaryMap.put(currentToken, newPostingsList);

            } else {
                if (dictionaryMap.containsKey(currentToken)) { //word is in hashmap - 2 possibilities: in this doc, or not
                    currentPostingsList = (ArrayList) dictionaryMap.get(currentToken); //get the arraylist

                    for (int i = 0; i < currentPostingsList.size(); i++) {
                        if (currentPostingsList.get(i)[DOC_NAME].equals(docName)) { //if word already in this document

                            sameDoc = true;
                            posting = currentPostingsList.get(i);
                            position = i;
                            break;
                        }
                    }
                    if (sameDoc) {

                        //iDF doesn't change, just need to change TF
                        int newFrequency = (Integer.parseInt(posting[FREQ])); //add one to the frequency
                        newFrequency = newFrequency + 1;
                        double newTermFrequency = newFrequency / docLength; // calculate a new TF
                        posting[FREQ] = String.valueOf(newFrequency); //update the posting with the new frequency
                        posting[TERM_FREQ] = String.valueOf(newTermFrequency);
                        currentPostingsList.set(position, posting); //update arraylist
                        dictionaryMap.put(currentToken, currentPostingsList);
                        sameDoc = false;

                    } else { //not in this doc: 

                        // 1) update inverseDocFreqMap
                        Double[] temp = (Double[]) inverseDocFreqMap.get(currentToken);
                        Double x = temp[0] + 1.0; //add 1 to no. of docs
                        temp[0] = x;
                        temp[1] = Math.log(collectionSize / x); //recalculate IDF
                        inverseDocFreqMap.put(currentToken, temp);

                        // 2) add new posting(array) 
                        double newTermFrequency = 1.0 / docLength;
                        double newTFIDFweight = temp[1] * newTermFrequency;
                        posting = new String[]{docName, "1", String.valueOf(newTermFrequency), String.valueOf(newTFIDFweight), "Exact Match", ""};
                        currentPostingsList.add(posting);
                    }
                } else { //word not in hashmap - add new array and arraylist

                    //add new entry to inverseDocFreqMap, frequency is one
                    Double[] doubleArray = {1.0, Math.log(collectionSize / 1.0)};
                    inverseDocFreqMap.put(currentToken, doubleArray);

                    //add new posting and postingsList to dictionaryMap
                    double newTermFrequency = 1.0 / docLength;
                    String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), "0", "Exact Match", ""};
                    ArrayList<String[]> newPostingsList = new ArrayList<String[]>();
                    newPostingsList.add(newPosting);
                    dictionaryMap.put(currentToken, newPostingsList);
                }
            }

            //FINISHED CHECKING FOR EXACT MATCH, NOW CHECK FOR APPROXIMATE
            Iterator it = dictionaryMap.entrySet().iterator();
            while (it.hasNext()) {
                boolean noMatchFound = true;
                Map.Entry termEntry = (Map.Entry) it.next();
                String approximateToken = (String) termEntry.getKey();
                Double levScore = lev.score(currentToken, approximateToken);
                if (levScore.equals(-1.0)) { //we have an approximate match -

                    currentPostingsList = (ArrayList) dictionaryMap.get(currentToken);

                    //cycle through postings list to check if docNames match
                    for (int i = 0; i < currentPostingsList.size(); i++) {
                        if (currentPostingsList.get(i)[DOC_NAME].equals(docName)) {
                            noMatchFound = true;
                            posting = currentPostingsList.get(i);
                            position = i;

                            //found a matching doc, need to check if text is the same
                            if (posting[4].equals("Exact Match")) {
                                //do nothing for now
                            } else if (posting[5].equals(approximateToken) && posting[4].equals("Approximate Match")) {
                                //text is the same - increment and break
                                System.out.println("approx text is the same " + currentToken + " " + approximateToken);
                                noMatchFound = false;
                                //iDF doesn't change, just need to change TF
                                int newFrequency = (Integer.parseInt(posting[FREQ])); //add one to the frequency
                                newFrequency = newFrequency + 1;
                                System.out.println(" new frequency: " + newFrequency);
                                double newTermFrequency = newFrequency / docLength; // calculate a new TF
                                posting[FREQ] = String.valueOf(newFrequency); //update the posting with the new frequency
                                posting[TERM_FREQ] = String.valueOf(newTermFrequency);
                                currentPostingsList.set(position, posting); //update arraylist
                                dictionaryMap.put(currentToken, currentPostingsList);
                                break;

                            } else if (posting[MATCH_TYPE].equals("Appropximate Match") && !posting[5].equals(approximateToken)) { //do nothing for now
                            }

                        }
                    }
                    // reached end of postings list, if noMatchFound, add new posting (i.e. new approximate match)
                    if (noMatchFound) {
                        //noMatchFound = true;

                        // 1) update inverseDocFreqMap
                        Double[] temp = (Double[]) inverseDocFreqMap.get(currentToken);
                        Double x = temp[0] + 1.0; //add 1 to no. of docs
                        temp[0] = x;
                        temp[1] = Math.log(collectionSize / x); //recalculate IDF
                        inverseDocFreqMap.put(currentToken, temp);

                        // 2) add new posting(array) 
                        double newTermFrequency = 1.0 / docLength;
                        double newTFIDFweight = temp[1] * newTermFrequency;
                        String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), String.valueOf(newTFIDFweight), "Approximate Match", approximateToken};
                        currentPostingsList.add(newPosting);
                    }
                }
            }
        }

        for (int k = 0; k < tokens.size(); k++) {

            String comparisonToken = tokens.get(k).toString();

            Iterator iterator = dictionaryMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry termEntry = (Map.Entry) iterator.next();
                String existingToken = (String) termEntry.getKey();
                Double levScore = lev.score(existingToken, comparisonToken);
                if (levScore.equals(-1.0)) { //we have an approximate match -

                    currentPostingsList = (ArrayList) dictionaryMap.get(existingToken);

                    //check if docNames match
                    for (int i = 0; i < currentPostingsList.size(); i++) {
                        Boolean matchRecorded = false;
                        if (currentPostingsList.get(i)[DOC_NAME].equals(docName)) {
                            posting = currentPostingsList.get(i);
                            position = i;

                            if (posting[MATCH_TYPE].equals("Exact Match")) {
                                //this will have already been recorded
                            } else if (posting[MATCH_TYPE].equals("Approximate Match") && posting[TEXT].equals(comparisonToken)) {
                                //text is the same - increment and break

                                //iDF doesn't change, just need to change TF
                                int newFrequency = (Integer.parseInt(posting[FREQ])); //add one to the frequency
                                newFrequency = newFrequency + 1;
                                double newTermFrequency = newFrequency / docLength; // calculate a new TF
                                posting[FREQ] = String.valueOf(newFrequency); //update the posting with the new frequency
                                posting[TERM_FREQ] = String.valueOf(newTermFrequency);
                                currentPostingsList.set(position, posting); //update arraylist
                                dictionaryMap.put(existingToken, currentPostingsList);

                                matchRecorded = true;
                            }
                        }

                        // reached end of postings list, if noMatchRecorded, add new posting (i.e. new approximate match)
                        if (!matchRecorded) {

                            // 1) update inverseDocFreqMap for approximate Matches - just get the IDF to calculate TFIDF 
                            Double[] idFArray = (Double[]) inverseDocFreqMap.get(existingToken);

                            // 2) add new posting(array) 
                            double newTermFrequency = 1.0 / docLength;
                            double newTFIDFweight = idFArray[IDF] * newTermFrequency;
                            String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), String.valueOf(newTFIDFweight), "Approximate Match", comparisonToken};
                            currentPostingsList.add(newPosting);
                        }
                    }
                }
            }

        }
    }

    public static void reCalculateTFandIDFwithApproxValues(String docContents, String docName, int collectionSize,
            double docLength) {
        System.out.println("reCalculateTFandIDFwithApproxValues running");
        ArrayList<String[]> currentPostingsList;
        String[] posting;

        for (int k = 0; k < tokens.size(); k++) {

            String comparisonToken = tokens.get(k).toString();

            Iterator iterator = dictionaryMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry termEntry = (Map.Entry) iterator.next();
                String existingToken = (String) termEntry.getKey();
                Double levScore = lev.score(existingToken, comparisonToken);
                if (levScore.equals(-1.0)) { //we have an approximate match -

                    currentPostingsList = (ArrayList) dictionaryMap.get(existingToken);

                    //check if docNames match
                    Boolean matchRecorded = false;
                    for (int i = 0; i < currentPostingsList.size(); i++) {

                        if (currentPostingsList.get(i)[DOC_NAME].equals(docName)) {
                            posting = currentPostingsList.get(i);
                            int position = i;

                            if (posting[MATCH_TYPE].equals("Approximate Match") && posting[TEXT].equals(comparisonToken)) {
                                //text is the same - increment and break

                                //iDF doesn't change, just need to change TF
                                int newFrequency = (Integer.parseInt(posting[FREQ])); //add one to the frequency
                                newFrequency = newFrequency + 1;
                                double newTermFrequency = newFrequency / docLength; // calculate a new TF
                                posting[FREQ] = String.valueOf(newFrequency); //update the posting with the new frequency
                                posting[TERM_FREQ] = String.valueOf(newTermFrequency);
                                currentPostingsList.set(position, posting); //update arraylist
                                dictionaryMap.put(existingToken, currentPostingsList);

                                matchRecorded = true;
                            }
                        }
                    }
                    // reached end of postings list, if noMatchRecorded, add new posting (i.e. new approximate match)
                    if (!matchRecorded) {
                        // 1) don´t update inverseDocFreqMap for approximate Matches - just get the IDF to calculate TFIDF 
                        Double[] idFArray = (Double[]) inverseDocFreqMap.get(existingToken);

                        // 2) add new posting(array) 
                        double newTermFrequency = 1.0 / docLength;
                        double newTFIDFweight = idFArray[IDF] * newTermFrequency;
                        String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), String.valueOf(newTFIDFweight), "Approximate Match", comparisonToken};
                        currentPostingsList.add(newPosting);
                        matchRecorded = true;

                    }
                }
            }
        }
    }

    public static void calculateTFIDF() {

        System.out.println("calculateTFIDF running");
        Iterator iterator = dictionaryMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry termEntry = (Map.Entry) iterator.next();
            String token = (String) termEntry.getKey();
            ArrayList<String[]> postingsList = (ArrayList) termEntry.getValue();

            Double[] idFArray = (Double[]) inverseDocFreqMap.get(token);
            double inverseDocFreq = idFArray[IDF];

            for (int i = 0; i < postingsList.size(); i++) {

                String[] posting = postingsList.get(i);
                double termFrequency = Double.parseDouble(posting[TERM_FREQ]);
                double newTFIDF = inverseDocFreq * termFrequency;
                posting[TFIDF_WEIGHT] = String.valueOf(newTFIDF);
                postingsList.set(i, posting);
            }
            dictionaryMap.put(token, postingsList);
        }
    }

    public static String displayTokens(double minimumTFIDF, int quantityOfPostings) {

        String output = "";
        
        Iterator iterator = dictionaryMap.entrySet().iterator();
        output = output + "Total tokens: " + dictionaryMap.size() + "\n\n";

        while (iterator.hasNext()) {
            Map.Entry termEntry = (Map.Entry) iterator.next();
            String key = (String) termEntry.getKey();
            ArrayList<String[]> postingsList = (ArrayList) termEntry.getValue();
            Double[] iDFArray = (Double[]) inverseDocFreqMap.get(key);
            double IDFValue = iDFArray[IDF];

            output = output + "Token: '" + key + "'\n";
            output = output + "iDF: " + df.format(IDFValue) + "\n";
            for (int i = 0; i < postingsList.size(); i++) {
                output = output + postingsList.get(i)[DOC_NAME];
                output = output + "; tf: " + df.format(Double.parseDouble(postingsList.get(i)[TERM_FREQ]));
                output = output + "; tf-idf: " + df.format(Double.parseDouble(postingsList.get(i)[TFIDF_WEIGHT])) + "\n";
 
            }
            output = output + "\n";
        }
        return output;
    }

    /**
     * Counts the number of links between two documents for a given token-choice
     * and records them in the docPairs
     *
     * @param dictionaryMap
     * @param inverseDocFreqMap
     */
    public static void countMatches(double minIDF, double minTFIDF) {
        System.out.println("'countMatches' running");
        allDocPairs.clear();
        Iterator it2 = dictionaryMap.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it2.next();
            String token = (String) termEntry.getKey();
            ArrayList<String[]> postingsList = (ArrayList) termEntry.getValue();
            for (int i = 0; i < postingsList.size() - 1; i++) {
                for (int j = i + 1; j < postingsList.size(); j++) {
                    String combinedName = postingsList.get(i)[DOC_NAME] + "_" + postingsList.get(j)[DOC_NAME];

                    Double[] currentIDFArray = (Double[]) inverseDocFreqMap.get(token);

                    if (((Double.parseDouble(postingsList.get(i)[TFIDF_WEIGHT])) > minTFIDF
                            || (Double.parseDouble(postingsList.get(j)[TFIDF_WEIGHT])) > minTFIDF)
                            && currentIDFArray[IDF] > minIDF) {

                        if (allDocPairs.containsKey(combinedName)) {
                            int count = allDocPairs.get(combinedName);
                            count++;
                            allDocPairs.put(combinedName, count);
                        } else {
                            allDocPairs.put(combinedName, 1);
                        }
                    }
                }
            }
        }
    }

    public static void countMatchesWeighted(double minIDF, double minTFIDF) {
        System.out.println("countMatchesWeighted running");
        allDocPairs.clear();
        Iterator it2 = dictionaryMap.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it2.next();
            String token = (String) termEntry.getKey();
            ArrayList<String[]> postingsList = (ArrayList) termEntry.getValue();
            for (int i = 0; i < postingsList.size() - 1; i++) {
                for (int j = i + 1; j < postingsList.size(); j++) {
                    String combinedName = postingsList.get(i)[DOC_NAME] + "_" + postingsList.get(j)[DOC_NAME];

                    Double[] currentIDFArray = (Double[]) inverseDocFreqMap.get(token);

                    if (((Double.parseDouble(postingsList.get(i)[TFIDF_WEIGHT])) > minTFIDF
                            || (Double.parseDouble(postingsList.get(j)[TFIDF_WEIGHT])) > minTFIDF)
                            && currentIDFArray[IDF] > minIDF) {

                        if (currentIDFArray[IDF] > (minIDF + 1.0)) {
                            if (allDocPairs.containsKey(combinedName)) {
                                int count = allDocPairs.get(combinedName);
                                count = count + 2;
                                allDocPairs.put(combinedName, count);
                            } else {
                                allDocPairs.put(combinedName, 2);
                            }
                        } else {
                            if (allDocPairs.containsKey(combinedName)) {
                                int count = allDocPairs.get(combinedName);
                                count++;
                                allDocPairs.put(combinedName, count);
                            } else {
                                allDocPairs.put(combinedName, 1);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void generateResults(ConcurrentHashMap allDocPairs, int minMatchingTokens) {

        resultsDocPairs.clear();

        Iterator it = allDocPairs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it.next();
            int matchingTokens = Integer.parseInt(termEntry.getValue().toString());
            if (matchingTokens >= minMatchingTokens) {
                resultsDocPairs.put(termEntry.getKey().toString(), Integer.parseInt(termEntry.getValue().toString()));

            }
        }
    }

    public static String getResults() {
        System.out.println("getResults running");
        String output = "";
        Iterator iterator = resultsDocPairs.entrySet().iterator();
        while (iterator.hasNext()) {

            Map.Entry termEntry = (Map.Entry) iterator.next();
            output = output + termEntry.getKey().toString() + " " + termEntry.getValue().toString() + "\n";
        }
        return output;
    }

    public static Double[] calculatePrecisionAndRecall() {

        Double[] precisionAndRecall = new Double[2];

        double relevantDocumentsRetrieved = 0;
        double totalRetrievedDocuments = resultsDocPairs.size();

        Iterator it = resultsDocPairs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it.next();
            for (int i = 0; i < knownPlagiarism.size(); i++) {

                if (termEntry.getKey().equals(knownPlagiarism.get(i))) {
                    relevantDocumentsRetrieved++;
                }
            }
        }

        double precision = ((relevantDocumentsRetrieved / totalRetrievedDocuments) * 100);
        double recall = ((relevantDocumentsRetrieved / knownPlagiarism.size()) * 100);

        precisionAndRecall[PRECISION] = precision;
        precisionAndRecall[RECALL] = recall;

        return precisionAndRecall;

    }

    public static String getPrecisionAndRecall(Double[] precisionAndRecall) {
        String output = "Precision: " + precisionAndRecall[PRECISION] + ", Recall: " + precisionAndRecall[RECALL];
        return output;
    }

    public static String getPrecision(Double[] precisionAndRecall) {
        String output = String.valueOf(precisionAndRecall[PRECISION]);
        return output;
    }

    public static String getRecall(Double[] precisionAndRecall) {
        String output = String.valueOf(precisionAndRecall[RECALL]);
        return output;
    }

    public static String displayPrecisionAndRecall() {
        String output = "";
        output = getPrecisionAndRecall(calculatePrecisionAndRecall());
        return output;
    }

    public static void testPandR(ConcurrentHashMap dictionaryMap, ConcurrentHashMap inverseDocFreqMap, int minPrecision, int minRecall) {

        FileWriter writer;
        String csv = "";

        try {
            writer = new FileWriter("C:\\Users\\Dave\\Desktop\\output.csv");

            for (double minTFIDF = 0; minTFIDF <= 0.003; minTFIDF = minTFIDF + 0.001) {
                for (double minIDF = 0.2; minIDF <= 1.8; minIDF = minIDF + 0.4) {

                    System.out.println("");
                    System.out.println("MIN IDF: " + minIDF + ", MIN TFIDF: " + minTFIDF);
                    System.out.println("");

                    countMatchesWeighted(minIDF, minTFIDF);

                    for (int minMatchingTokens = 100; minMatchingTokens <= 275; minMatchingTokens++) {
                        generateResults(allDocPairs, minMatchingTokens);
                        Double[] precisionAndRecall = calculatePrecisionAndRecall();
                        //if (precisionAndRecall[PRECISION] >= minPrecision && precisionAndRecall[RECALL] >= minRecall) {

                        System.out.print("Minimum Matching tokens: " + minMatchingTokens + ", " + getPrecisionAndRecall(precisionAndRecall) + "\n");

                        writer.append(String.valueOf(minTFIDF));
                        writer.append(";");
                        writer.append(String.valueOf(minIDF));
                        writer.append(";");
                        writer.append(String.valueOf(minMatchingTokens));
                        writer.append(";");
                        writer.append(String.valueOf(df.format(precisionAndRecall[PRECISION])));
                        writer.append(";");
                        writer.append(String.valueOf(df.format(precisionAndRecall[RECALL])));
                        writer.append("\n");
                        //}
                    }
                    writer.append("\n");
                    writer.append("\n");
                }

            }
            writer.flush();
            writer.close();
            System.out.println("finished");
        } catch (IOException ioe) {
            System.out.println();
        }
    }

    public static void generateCSV(HashMap newMap) {

        String csv = "";
        Iterator it = newMap.entrySet().iterator();

        try {
            FileWriter writer = new FileWriter("C:\\Users\\David\\Desktop\\output.csv");
            while (it.hasNext()) {
                Map.Entry termEntry = (Map.Entry) it.next();
                writer.append(termEntry.getKey().toString());
                writer.append(",");
                writer.append(termEntry.getValue().toString());
                writer.append("\n");

            }
            writer.flush();
            writer.close();

        } catch (IOException ioe) {
            System.out.println(ioe);
        }

    }

    public static void splitDocNames(String docPair) {
        String[] docNames = docPair.split("//_");
    }

    public static String getMatchesBetween2docs(String docA, String docB) {

        String output = "";
        boolean containsA = false;
        boolean containsB = false;

        matchingTokens.clear();
        Iterator iterator = dictionaryMap.entrySet().iterator();
        while (iterator.hasNext()) {
            containsA = false;
            containsB = false;
            Map.Entry termEntry = (Map.Entry) iterator.next();
            String token = (String) termEntry.getKey();
            ArrayList<String[]> postingsList = (ArrayList) termEntry.getValue();
            for (int i = 0; i < postingsList.size(); i++) {
                if (postingsList.get(i)[DOC_NAME].equals(docA)) {
                    containsA = true;
                }
                if (postingsList.get(i)[DOC_NAME].equals(docB)) {
                    containsB = true;
                }
            }
            if (containsA && containsB) {

                matchingTokens.add(token);

                System.out.println("'" + token + "'");
                output = output + "'" + token + "'" + "\n";
                System.out.println(" ");
                System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - ");
                output = output + "- - - - - - - - - - - - - - - - - - - - - - - - - - - \n";
                System.out.println(" ");
//                    System.out.print(", tFiDF: " + postingsList.get(i)[tFiDF]);
//                    System.out.print(", Term Frequency: " + postingsList.get(i)[termFrequency]);
//                    System.out.print(", iDF: " + IDF);
//                    System.out.print(", Document: " + postingsList.get(i)[documentName]);
//                    System.out.print(", Document Frequency: " + postingsList.get(i)[frequency]);
//                    System.out.println(", Posting list size: " + postingsList.size());

            }

        }
        return output;

    }

    public static String getDocContents(String docName, File directory) {
        ReadFile rf = new ReadFile();
        String docContents = "";
        try {
            docContents = rf.readFile((directory + "//" + docName));
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
        return docContents;
    }

    public static void highlightTokens(JTextArea resultsPane, ArrayList<String> tokens) {
        Highlighter highlighter = resultsPane.getHighlighter();
        for (int j = 0; j < tokens.size(); j++) {
            String textFromPane = resultsPane.getText();
            for (int i = 0; i <= textFromPane.length(); i++) {
                if (textFromPane.regionMatches(i, tokens.get(j), 0, tokens.get(j).length())) {
                    try {
                        highlighter.addHighlight(i, i + tokens.get(j).length(), DefaultHighlighter.DefaultPainter);
                    } catch (BadLocationException ble) {
                        System.out.println(ble);
                    }
                }
            }

        }
    }

    public static void createKnownPlagiarismList(File filepath) {

        try {
            Scanner s = new Scanner(filepath);
            while (s.hasNext()) {
                knownPlagiarism.add(s.next());
            }
            s.close();
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf);
        }

    }

    public static String getKnownPlagiarism() {
        String output = "";
        for (int i = 0; i < knownPlagiarism.size(); i++) {
            output = output + knownPlagiarism.get(i) + "\n";
        }
        return output;
    }

    public static void setKnownPlagiarism(File filepath) {

        createKnownPlagiarismList(filepath);
    }

}
