package plagiarism;

import com.wcohen.ss.Levenstein;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class Plagiarism {

    public static Levenstein lev = new Levenstein();
    public static ConcurrentHashMap<String, ArrayList<String[]>> dictionaryMap1gram = new ConcurrentHashMap<String, ArrayList<String[]>>();
    // Key is term
    // Value is array list (postings list) of string arrays (postings) of the form [docName, frequency, termFrequency (tf), tF-idf weight, match type, text]
    public static ArrayList<String> tokens1grams;
    public static ConcurrentHashMap<String, Double[]> inverseDocFreqMap1gram = new ConcurrentHashMap<String, Double[]>();
    // Key is term
    // Value is double array containing [no. of documents in which this term occurs, inverse document frequency (idf)]
    //public static ArrayList<String> al = new ArrayList<String>();
    public static ConcurrentHashMap<String, ArrayList<String[]>> dictionaryMap2gram = new ConcurrentHashMap<String, ArrayList<String[]>>();
    public static ConcurrentHashMap<String, Double[]> inverseDocFreqMap2gram = new ConcurrentHashMap<String, Double[]>();
    public static ArrayList<String> tokens2grams;
    public static ConcurrentHashMap<String, ArrayList<String[]>> dictionaryMap3gram = new ConcurrentHashMap<String, ArrayList<String[]>>();
    public static ConcurrentHashMap<String, Double[]> inverseDocFreqMap3gram = new ConcurrentHashMap<String, Double[]>();
    public static ArrayList<String> tokens3grams;
    public static ConcurrentHashMap<String, ArrayList<String[]>> dictionaryMap4gram = new ConcurrentHashMap<String, ArrayList<String[]>>();
    public static ConcurrentHashMap<String, Double[]> inverseDocFreqMap4gram = new ConcurrentHashMap<String, Double[]>();
    public static ArrayList<String> tokens4grams;
    public static ConcurrentHashMap<String, ArrayList<String[]>> dictionaryMap2gramBasic = new ConcurrentHashMap<String, ArrayList<String[]>>();
    public static ConcurrentHashMap<String, Double[]> inverseDocFreqMap2gramBasic = new ConcurrentHashMap<String, Double[]>();
    public static ArrayList<String> tokens2gramsBasic;
    public static ConcurrentHashMap<String, ArrayList<String[]>> dictionaryMapWhiteSpace = new ConcurrentHashMap<String, ArrayList<String[]>>();
    public static ConcurrentHashMap<String, Double[]> inverseDocFreqMapWhiteSpace = new ConcurrentHashMap<String, Double[]>();
    public static ArrayList<String> tokensWhiteSpace;
    public static ConcurrentHashMap<String, ArrayList<String[]>> dictionaryMapJava = new ConcurrentHashMap<String, ArrayList<String[]>>();
    public static ConcurrentHashMap<String, Double[]> inverseDocFreqMapJava = new ConcurrentHashMap<String, Double[]>();
    public static ArrayList<String> tokensJava;
    public static TreeMap<String, Integer> matchingTokensBetweenDocPairs = new TreeMap<String, Integer>();
    public static String directory = "";
    public static ArrayList<String> matchingTokens = new ArrayList<String>();
    //CONSTANTS
    final static int DOC_COUNT = 0;
    final static int IDF = 1;
    final static int DOC_NAME = 0;
    final static int FREQ = 1;
    final static int TERM_FREQ = 2; //the frequency divided by the document length
    final static int TFIDF_WEIGHT = 3; // log_e(Total number of documents / Number of documents with term t in it)
    final static int MATCH_TYPE = 4;
    final static int TEXT = 5;

    public Plagiarism() {
        
    }

    public static void generateTokens(int nGramSize, boolean includeApproximateMatches) {
        System.out.println("generateTokens running");
        //read all files in a directory
        //http://stackoverflow.com/questions/4917326/how-to-iterate-over-the-files-of-a-certain-directory-in-java
        directory = "C:\\Users\\David\\Desktop\\code";
        //C:\Users\c1343067.X7054D28FCA47.000\Desktop\code3
        //File path = new File("C:\\Users\\c1343067.X7054D28FCA47.000\\Desktop\\code");
        //File path = new File("C:\\Users\\c1343067.X7054D28FCAE8\\Desktop\\code3");
        File path = new File(directory);
        File[] files = path.listFiles();
        int collectionSize = files.length; //total number of docs in collection

        ReadFile rf = new ReadFile();
        TokenGenerator generator = new TokenGenerator();
        ArrayList<String> al = new ArrayList<String>();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                String docName = files[i].getName();
                try {
                    String docContents = rf.readFile(files[i].toString());
                    StringTokenizer st = new StringTokenizer(docContents);
                    double docLength = st.countTokens();
                    if (nGramSize == 1) {
                        tokens1grams = generator.generateOneGrams(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokens1grams, docLength, dictionaryMap1gram, inverseDocFreqMap1gram);

                    } else if (nGramSize == 2) {
                        tokens2grams = generator.generateTwoGrams(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokens2grams, docLength, dictionaryMap2gram, inverseDocFreqMap2gram);
                    } else if (nGramSize == 3) {
                        tokens3grams = generator.generateThreeGrams(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokens3grams, docLength, dictionaryMap3gram, inverseDocFreqMap3gram);
                    } else if (nGramSize == 4) {
                        tokens4grams = generator.generateFourGrams(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokens4grams, docLength, dictionaryMap4gram, inverseDocFreqMap4gram);
                    } else if (nGramSize == 5) {
                        tokens2gramsBasic = generator.generateTwoGramsBasic(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokens2gramsBasic, docLength, dictionaryMap2gramBasic, inverseDocFreqMap2gramBasic);
                    } else if (nGramSize == 6) {
                        tokensWhiteSpace = generator.generateSpaces(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokensWhiteSpace, docLength, dictionaryMapWhiteSpace, inverseDocFreqMapWhiteSpace);
                    } else if (nGramSize == 7) {
                        tokensJava = generator.splitOnJavaSyntax(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokensJava, docLength, dictionaryMapJava, inverseDocFreqMapJava);
                    }
                } catch (IOException ioe) {
                    System.out.println("error");
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
                        if (nGramSize == 1) {
                            tokens1grams = generator.generateOneGrams(docContents);
                            reCalculateTFandIDFwithApproxValues(docContents, docName, collectionSize, tokens1grams, docLength, dictionaryMap1gram, inverseDocFreqMap1gram);

                        } else if (nGramSize == 2) {
                            tokens2grams = generator.generateTwoGrams(docContents);
                            reCalculateTFandIDFwithApproxValues(docContents, docName, collectionSize, tokens2grams, docLength, dictionaryMap2gram, inverseDocFreqMap2gram);
                        } else if (nGramSize == 3) {
                            tokens3grams = generator.generateThreeGrams(docContents);
                            reCalculateTFandIDFwithApproxValues(docContents, docName, collectionSize, tokens3grams, docLength, dictionaryMap3gram, inverseDocFreqMap3gram);
                        } else if (nGramSize == 4) {
                            tokens4grams = generator.generateFourGrams(docContents);
                            reCalculateTFandIDFwithApproxValues(docContents, docName, collectionSize, tokens4grams, docLength, dictionaryMap4gram, inverseDocFreqMap4gram);
                        } else if (nGramSize == 5) {
                            tokens2gramsBasic = generator.generateTwoGramsBasic(docContents);
                            reCalculateTFandIDFwithApproxValues(docContents, docName, collectionSize, tokens2gramsBasic, docLength, dictionaryMap2gramBasic, inverseDocFreqMap2gramBasic);
                        } else if (nGramSize == 6) {
                            tokensWhiteSpace = generator.generateSpaces(docContents);
                            reCalculateTFandIDFwithApproxValues(docContents, docName, collectionSize, tokensWhiteSpace, docLength, dictionaryMapWhiteSpace, inverseDocFreqMapWhiteSpace);
                        } else if (nGramSize == 7) {
                            tokensJava = generator.generateSpaces(docContents);
                            reCalculateTFandIDFwithApproxValues(docContents, docName, collectionSize, tokensJava, docLength, dictionaryMapJava, inverseDocFreqMapJava);
                        }
                        long endTime = System.nanoTime();
                        System.out.println("that took :" + (endTime - startTime));
                    } catch (IOException ioe) {
                        System.out.println("error" + ioe);
                    }
                }
            }
        }

        if (nGramSize == 1) {
            calculateTFIDF(dictionaryMap1gram, inverseDocFreqMap1gram);
        } else if (nGramSize == 2) {
            calculateTFIDF(dictionaryMap2gram, inverseDocFreqMap2gram);
        } else if (nGramSize == 3) {
            calculateTFIDF(dictionaryMap3gram, inverseDocFreqMap3gram);
        } else if (nGramSize == 4) {
            calculateTFIDF(dictionaryMap4gram, inverseDocFreqMap4gram);
        } else if (nGramSize == 5) {
            calculateTFIDF(dictionaryMap2gramBasic, inverseDocFreqMap2gramBasic);
        } else if (nGramSize == 6) {
            calculateTFIDF(dictionaryMapWhiteSpace, inverseDocFreqMapWhiteSpace);
        } else if (nGramSize == 7) {
            calculateTFIDF(dictionaryMapJava, inverseDocFreqMapJava);

        }
    }

    public static void calculateTFandIDF(String docContents, String docName, int collectionSize,
            ArrayList tokens, double docLength, ConcurrentHashMap dictionaryMap, ConcurrentHashMap inverseDocFreqMap) {

        ArrayList<String[]> currentPostingsList;
        String[] posting = new String[6];

        int position = 0;
        boolean documentMatch = false;

        for (int j = 0; j < tokens.size(); j++) {
            String currentToken = tokens.get(j).toString();
            if (dictionaryMap.isEmpty()) {

                Double[] doubleArray = {1.0, Math.log(collectionSize / 1.0)}; //1.0 because this is the first occurance
                inverseDocFreqMap.put(currentToken, doubleArray);

                double newTermFrequency = 1.0 / docLength;
                posting = new String[]{docName, "1", String.valueOf(newTermFrequency), "0", "Exact Match", ""};
                ArrayList<String[]> newPostingsList = new ArrayList<String[]>();
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
                    ArrayList<String[]> newPostingsList = new ArrayList<String[]>();
                    newPostingsList.add(newPosting);
                    dictionaryMap.put(currentToken, newPostingsList);
                }
            }
        }
    }

    public static void calculateTFandIDFapproxNEW(String docContents, String docName, int collectionSize,
            ArrayList tokens, double docLength, ConcurrentHashMap dictionaryMap, ConcurrentHashMap inverseDocFreqMap) {
        ArrayList<String[]> currentPostingsList;
        String[] posting = new String[6];

        int position = 0;
        boolean sameDoc = false;

        for (int j = 0; j < tokens.size(); j++) {
            String currentToken = tokens.get(j).toString();
            System.out.println("new token: " + currentToken);
            if (dictionaryMap.isEmpty()) { //add first term
                System.out.println("adding first token: " + currentToken);
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
                    System.out.println("exact match found: " + currentToken);
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
                    System.out.println("adding new token");

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
            System.out.println("checking for approximate match");

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
    
    public static void generateInvertedIndex(String nGramSize, boolean includeApproximateMatches) { //NB Change nGramSize to tokenizationType
        System.out.println("generateTokens running");
        //read all files in a directory
        //http://stackoverflow.com/questions/4917326/how-to-iterate-over-the-files-of-a-certain-directory-in-java
        directory = "C:\\Users\\David\\Desktop\\code";
        //C:\Users\c1343067.X7054D28FCA47.000\Desktop\code3
        //File path = new File("C:\\Users\\c1343067.X7054D28FCA47.000\\Desktop\\code");
        //File path = new File("C:\\Users\\c1343067.X7054D28FCAE8\\Desktop\\code3");
        File path = new File(directory);
        File[] files = path.listFiles();
        int collectionSize = files.length; //total number of docs in collection

        ReadFile rf = new ReadFile();
        TokenGenerator generator = new TokenGenerator();
        ArrayList<String> al = new ArrayList<String>();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                String docName = files[i].getName();
                try {
                    String docContents = rf.readFile(files[i].toString());
                    StringTokenizer st = new StringTokenizer(docContents);
                    double docLength = st.countTokens();
                    
                    
                    
                    if (nGramSize.equals("1-Grams")) {
                        tokens1grams = generator.generateOneGrams(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokens1grams, docLength, dictionaryMap1gram, inverseDocFreqMap1gram);

                    } else if (nGramSize.equals("Bi-Grams")) {
                        tokens2grams = generator.generateTwoGrams(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokens2grams, docLength, dictionaryMap2gram, inverseDocFreqMap2gram);
                    } else if (nGramSize.equals("3-Grams")) {
                        tokens3grams = generator.generateThreeGrams(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokens3grams, docLength, dictionaryMap3gram, inverseDocFreqMap3gram);
                    } else if (nGramSize.equals("4-Grams")) {
                        tokens4grams = generator.generateFourGrams(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokens4grams, docLength, dictionaryMap4gram, inverseDocFreqMap4gram);
                    } else if (nGramSize.equals("Basic 2-Grams")) {
                        tokens2gramsBasic = generator.generateTwoGramsBasic(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokens2gramsBasic, docLength, dictionaryMap2gramBasic, inverseDocFreqMap2gramBasic);
                    } else if (nGramSize.equals("White Space")) {
                        tokensWhiteSpace = generator.generateSpaces(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokensWhiteSpace, docLength, dictionaryMapWhiteSpace, inverseDocFreqMapWhiteSpace);
                    } else if (nGramSize.equals("Java Syntax")) {
                        tokensJava = generator.splitOnJavaSyntax(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokensJava, docLength, dictionaryMapJava, inverseDocFreqMapJava);
                    }
                } catch (IOException ioe) {
                    System.out.println("error");
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
                        if (nGramSize.equals("1-Grams")) {
                            tokens1grams = generator.generateOneGrams(docContents);
                            reCalculateTFandIDFwithApproxValues(docContents, docName, collectionSize, tokens1grams, docLength, dictionaryMap1gram, inverseDocFreqMap1gram);

                        } else if (nGramSize.equals("2-Grams")) {
                            tokens2grams = generator.generateTwoGrams(docContents);
                            reCalculateTFandIDFwithApproxValues(docContents, docName, collectionSize, tokens2grams, docLength, dictionaryMap2gram, inverseDocFreqMap2gram);
                        } else if (nGramSize.equals("3-Grams")) {
                            tokens3grams = generator.generateThreeGrams(docContents);
                            reCalculateTFandIDFwithApproxValues(docContents, docName, collectionSize, tokens3grams, docLength, dictionaryMap3gram, inverseDocFreqMap3gram);
                        } else if (nGramSize.equals("4-Grams")) {
                            tokens4grams = generator.generateFourGrams(docContents);
                            reCalculateTFandIDFwithApproxValues(docContents, docName, collectionSize, tokens4grams, docLength, dictionaryMap4gram, inverseDocFreqMap4gram);
                        } else if (nGramSize.equals("Basic 2-Grams")) {
                            tokens2gramsBasic = generator.generateTwoGramsBasic(docContents);
                            reCalculateTFandIDFwithApproxValues(docContents, docName, collectionSize, tokens2gramsBasic, docLength, dictionaryMap2gramBasic, inverseDocFreqMap2gramBasic);
                        } else if (nGramSize.equals("White Space")) {
                            tokensWhiteSpace = generator.generateSpaces(docContents);
                            reCalculateTFandIDFwithApproxValues(docContents, docName, collectionSize, tokensWhiteSpace, docLength, dictionaryMapWhiteSpace, inverseDocFreqMapWhiteSpace);
                        } else if (nGramSize.equals("Java Terminators")) {
                            tokensJava = generator.generateSpaces(docContents);
                            reCalculateTFandIDFwithApproxValues(docContents, docName, collectionSize, tokensJava, docLength, dictionaryMapJava, inverseDocFreqMapJava);
                        }
                        long endTime = System.nanoTime();
                        System.out.println("that took :" + (endTime - startTime));
                    } catch (IOException ioe) {
                        System.out.println("error" + ioe);
                    }
                }
            }
        }

        if (nGramSize.equals("1-Grams")) {
            calculateTFIDF(dictionaryMap1gram, inverseDocFreqMap1gram);
        } else if (nGramSize.equals("2-Grams")) {
            calculateTFIDF(dictionaryMap2gram, inverseDocFreqMap2gram);
        } else if (nGramSize.equals("3-Grams")) {
            calculateTFIDF(dictionaryMap3gram, inverseDocFreqMap3gram);
        } else if (nGramSize.equals("4-Grams")) {
            calculateTFIDF(dictionaryMap4gram, inverseDocFreqMap4gram);
        } else if (nGramSize.equals("Basic 2-Grams")) {
            calculateTFIDF(dictionaryMap2gramBasic, inverseDocFreqMap2gramBasic);
        } else if (nGramSize.equals("White Space")) {
            calculateTFIDF(dictionaryMapWhiteSpace, inverseDocFreqMapWhiteSpace);
        } else if (nGramSize.equals("Java Terminators")) {
            calculateTFIDF(dictionaryMapJava, inverseDocFreqMapJava);

        }
    }

    public static void reCalculateTFandIDFwithApproxValues(String docContents, String docName, int collectionSize,
            ArrayList tokens, double docLength, ConcurrentHashMap dictionaryMap, ConcurrentHashMap inverseDocFreqMap) {

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

    public static void calculateTFIDF(ConcurrentHashMap dictionaryMap, ConcurrentHashMap inverseDocFreqMap) {

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

    public static String getMatches(double minimumTFIDF, int quantityOfPostings, ConcurrentHashMap dictionaryMap, ConcurrentHashMap inverseDocFreqMap) {

        String output = "";
        Iterator it2 = dictionaryMap.entrySet().iterator();

        System.out.println("Total tokens: " + dictionaryMap.size());

        System.out.println(" - - - - - tFiDF > " + minimumTFIDF + " - - - - - -");
        System.out.println(" ");

        while (it2.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it2.next();
            String key = (String) termEntry.getKey();
            ArrayList<String[]> postingsList = (ArrayList) termEntry.getValue();
            Double[] algo = (Double[]) inverseDocFreqMap.get(key);
            double IDF = algo[1];
            int documentName = 0;
            int frequency = 1;
            int termFrequency = 2; //the frequency divided by the document length
            int tFiDF = 3;

            for (int i = 0; i < postingsList.size(); i++) {
                if ((Double.parseDouble(postingsList.get(i)[tFiDF]) >= minimumTFIDF) && (postingsList.size() >= 2) && (postingsList.size() <= quantityOfPostings)) {
                    System.out.print("'" + key + "'");
                    output = output + "Key: '" + key + "'\n";
                    System.out.print(", tFiDF: " + postingsList.get(i)[tFiDF]);
                    output = output + "tFiDF: " + postingsList.get(i)[tFiDF];
                    System.out.print(", Term Frequency: " + postingsList.get(i)[termFrequency]);
                    output = output + ", Term Frequency: " + postingsList.get(i)[termFrequency];
                    System.out.print(", iDF: " + IDF);
                    output = output + ", iDF: " + IDF;
                    System.out.print(", Document: " + postingsList.get(i)[documentName]);
                    output = output + ", Document: " + postingsList.get(i)[documentName];
                    System.out.print(", Document Frequency: " + postingsList.get(i)[frequency]);
                    output = output + ", Document Frequency: " + postingsList.get(i)[frequency];
                    System.out.println(", Posting list size: " + postingsList.size());
                    output = output + ", Posting list size: " + postingsList.size() + "\n";

                    for (int j = 0; j < postingsList.size(); j++) {
                        System.out.print(postingsList.get(j)[documentName] + ", ");
                        output = output + postingsList.get(j)[documentName] + ", ";
                        System.out.print(postingsList.get(j)[4] + ", ");
                        output = output + postingsList.get(j)[4] + ", ";
                    }
                    System.out.println(" ");
                    System.out.println(" ");
                    output = output + "\n\n";
                }
            }
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
    public static void aggregate(ConcurrentHashMap dictionaryMap, ConcurrentHashMap inverseDocFreqMap, double minIDF) {
        System.out.println("'aggregate' running");
        matchingTokensBetweenDocPairs.clear();
        Iterator it2 = dictionaryMap.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it2.next();
            String token = (String) termEntry.getKey();
            ArrayList<String[]> postingsList = (ArrayList) termEntry.getValue();
            for (int i = 0; i < postingsList.size() - 1; i++) {
                for (int j = i + 1; j < postingsList.size(); j++) {
                    String combinedName = postingsList.get(i)[DOC_NAME] + "?" + postingsList.get(j)[DOC_NAME];

                    Double[] currentIDFArray = (Double[]) inverseDocFreqMap.get(token);

                    if (currentIDFArray[IDF] > minIDF) {
                        //if ((Double.parseDouble(postingsList.get(i)[TFIDF_WEIGHT])) > 0.01 || (Double.parseDouble(postingsList.get(i)[TFIDF_WEIGHT])) > 0.01) {

                        if (matchingTokensBetweenDocPairs.containsKey(combinedName)) {
                            int count = matchingTokensBetweenDocPairs.get(combinedName);
                            count++;
                            matchingTokensBetweenDocPairs.put(combinedName, count);
                        } else {
                            matchingTokensBetweenDocPairs.put(combinedName, 1);
                        }
                    }
                }
            }
        }
    }

    public static void aggregateWeighted(ConcurrentHashMap dictionaryMap, ConcurrentHashMap inverseDocFreqMap, double minIDF) {
        System.out.println("'aggregate' running");
        matchingTokensBetweenDocPairs.clear();
        Iterator it2 = dictionaryMap.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it2.next();
            String token = (String) termEntry.getKey();
            ArrayList<String[]> postingsList = (ArrayList) termEntry.getValue();
            for (int i = 0; i < postingsList.size() - 1; i++) {
                for (int j = i + 1; j < postingsList.size(); j++) {
                    String combinedName = postingsList.get(i)[DOC_NAME] + postingsList.get(j)[DOC_NAME];

                    Double[] currentIDFArray = (Double[]) inverseDocFreqMap.get(token);

                    if (currentIDFArray[IDF] > minIDF) {
                        //if ((Double.parseDouble(postingsList.get(i)[TFIDF_WEIGHT])) > 0.01 || (Double.parseDouble(postingsList.get(i)[TFIDF_WEIGHT])) > 0.01) {
                        if (currentIDFArray[IDF] > (minIDF * 2)) {
                            if (matchingTokensBetweenDocPairs.containsKey(combinedName)) {
                                int count = matchingTokensBetweenDocPairs.get(combinedName);
                                count = count + 2;
                                matchingTokensBetweenDocPairs.put(combinedName, count);
                            } else {
                                matchingTokensBetweenDocPairs.put(combinedName, 2);
                            }
                        } else {
                            if (matchingTokensBetweenDocPairs.containsKey(combinedName)) {
                                int count = matchingTokensBetweenDocPairs.get(combinedName);
                                count++;
                                matchingTokensBetweenDocPairs.put(combinedName, count);
                            } else {
                                matchingTokensBetweenDocPairs.put(combinedName, 1);
                            }
                        }
                    }
                }
            }
        }
    }

    public static String printTally(TreeMap matchingTokensBetweenDocPairs, ConcurrentHashMap inverseDocFreqMap, int noOfMatches) {

        String output = "";
        
        //matchingTokensBetweenDocPairs.
        Iterator it = matchingTokensBetweenDocPairs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it.next();

            String token = (String) termEntry.getKey();
            //System.out.println("1");
            //Double[] currentIDFArray = (Double[]) inverseDocFreqMap.get(token);
            //Double idfValue = currentIDFArray[IDF];

            int count = Integer.parseInt(termEntry.getValue().toString());
            if (count >= noOfMatches) {
                //System.out.println(termEntry.getKey().toString() + " " + termEntry.getValue().toString());
                output = output + termEntry.getKey().toString() + " " + termEntry.getValue().toString() + "\n";
            }
        }
        calculatePrecisionandRecall(noOfMatches, 0, 0);
        
        return output;
    }

    public static void calculatePrecisionandRecall(int noOfMatches, int minPrecision, int minRecall) {

        String[] plagiarisedPairsA = {"40.java47.java", "40.java76.java", "40.java86.java", "40.java89.java",
            "47.java76.java", "47.java86.java", "47.java89.java", "76.java86.java",
            "76.java89.java", "86.java89.java", "31.java87.java"};

        String[] plagiarisedPairsB = {"19.java28.java", "7.java22.java", "38.java64.java", "5.java38.java",
            "5.java38.java", "38.java43.java", "43.java64.java", "3.java25.java", "25.java62.java", "25.java45.java",
            "25.java41.java", "45.java62.java", "3.java62.java", "41.java62.java", "3.java45.java", "41.java45.java",
            "3.java41.java", "15.java26.java", "5.java64.java", "5.java43.java", "43.java64.java"};

        double relevantDocumentsRetrieved = 0;
        double totalRetrievedDocuments = 0;

        Iterator it = matchingTokensBetweenDocPairs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it.next();
            int count = Integer.parseInt(termEntry.getValue().toString());
            if (count >= noOfMatches) {
                totalRetrievedDocuments++;
                for (int i = 0; i < plagiarisedPairsA.length; i++) {
                    if (termEntry.getKey().equals(plagiarisedPairsA[i])) {
                        relevantDocumentsRetrieved++;
                    }
                }
            }
        }
        //double relevantDocumentsNotRetrieved = plagiarisedPairs.length - relevantDocumentsRetrieved;

        double precision = ((relevantDocumentsRetrieved / totalRetrievedDocuments) * 100);
        double recall = ((relevantDocumentsRetrieved / plagiarisedPairsA.length) * 100);
        if (recall >= minRecall && precision >= minPrecision) {
            System.out.print(noOfMatches);
            //System.out.print("relevantDocsRetrieved: " + relevantDocumentsRetrieved);
            //System.out.println(", totalDocsRetrieved: " + totalRetrievedDocuments);

            System.out.print(": Precision: " + precision);
            System.out.println(", Recall:" + recall);
        }

    }

    public static void getMatchesBetween2docs(ConcurrentHashMap dictionaryMap, ConcurrentHashMap inverseDocFreqMap, String docA, String docB) {

        boolean containsA = false;
        boolean containsB = false;
        
        matchingTokens.clear();
        //matchingTokens = new ArrayList<String>();

        Iterator iterator = dictionaryMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry termEntry = (Map.Entry) iterator.next();
            String token = (String) termEntry.getKey();
            ArrayList<String[]> postingsList = (ArrayList) termEntry.getValue();
            for (int i = 0; i < postingsList.size() - 1; i++) {
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
                System.out.println(" ");
                System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - ");
                System.out.println(" ");
//                    System.out.print(", tFiDF: " + postingsList.get(i)[tFiDF]);
//                    System.out.print(", Term Frequency: " + postingsList.get(i)[termFrequency]);
//                    System.out.print(", iDF: " + IDF);
//                    System.out.print(", Document: " + postingsList.get(i)[documentName]);
//                    System.out.print(", Document Frequency: " + postingsList.get(i)[frequency]);
//                    System.out.println(", Posting list size: " + postingsList.size());
                containsA = false;
                containsB = false;
            }
        }
        
    }

    public static void testPandR(ConcurrentHashMap dictionaryMap, ConcurrentHashMap inverseDocFreqMap, int minPrecision, int minRecall) {

        for (double i = 0; i < 5; i = i + 0.2) {
            System.out.println("");
            System.out.println("MIN IDF: " + i);
            System.out.println("");

            aggregate(dictionaryMap, inverseDocFreqMap, i);
            for (int j = 0; j < 300; j++) {

                calculatePrecisionandRecall(j, minPrecision, minRecall);
            }
        }
    }

    public static void splitDocNames(String docPair) {
        String[] docNames = docPair.split("//?");
    }

    public static String getDocContents(String docName) {
        ReadFile rf = new ReadFile();
        String docContents = "";
        try {
            docContents = rf.readFile((directory + "//" + docName).toString());
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
        return docContents;
    }
    
    public static void highlightTokens(JTextArea resultsPane, ArrayList<String> tokens){
        Highlighter highlighter = resultsPane.getHighlighter();
            for(int j = 0; j<tokens.size(); j++){
            String textFromPane = resultsPane.getText();
            for (int i = 0; i <= textFromPane.length(); i++) {
                if (textFromPane.regionMatches(i, tokens.get(j), 0, tokens.get(j).length())) {
                    try {
                        highlighter.addHighlight(i, i + tokens.get(j).length(), DefaultHighlighter.DefaultPainter);
                    } catch (BadLocationException ble) {
                        System.out.println(ble);
                    }
                }}

            }
    }
    

}
