package plagiarism;

import com.wcohen.ss.Levenstein;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public class Plagiarism {

    public static Levenstein lev = new Levenstein();

    public static ConcurrentHashMap<String, ArrayList<String[]>> dictionaryMap1gram = new ConcurrentHashMap<String, ArrayList<String[]>>();
    // Key is term
    // Value is array list (postings list) of string arrays (postings) of the form [docName, frequency, termFrequency (tf), tF-idf weight]
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

    public static ConcurrentHashMap<String, Integer> tallyChart = new ConcurrentHashMap<String, Integer>();
    
    public static String tempString;

    public Plagiarism() {
    }

    public static void generateTokens(int k) {
        System.out.println("generateTokens running");
        //read all files in a directory
        //http://stackoverflow.com/questions/4917326/how-to-iterate-over-the-files-of-a-certain-directory-in-java
        File path = new File("C:\\Users\\David\\Desktop\\code20");
        //File path = new File("C:\\Users\\c1343067\\Desktop\\code");
        //File path = new File("C:\\Users\\c1343067.X7054D28FCAE8\\Desktop\\code");
        File[] files = path.listFiles();
        int collectionSize = files.length; //total number of docs in collection

        ReadFile rf = new ReadFile();
        NGramGenerator ngg = new NGramGenerator();
        ArrayList<String> al = new ArrayList<String>();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                String docName = files[i].getName();
                try {
                    String docContents = rf.readFile(files[i].toString());
                    StringTokenizer st = new StringTokenizer(docContents);
                    float docLength = st.countTokens();
                    if (k == 1) {
                        tokens1grams = ngg.generateOneGrams(docContents);
                        calculateTFandIDFapprox(docContents, docName, collectionSize, tokens1grams, docLength, dictionaryMap1gram, inverseDocFreqMap1gram);
                    } else if (k == 2) {
                        tokens2grams = ngg.generateTwoGrams(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokens2grams, docLength, dictionaryMap2gram, inverseDocFreqMap2gram);
                    } else if (k == 3) {
                        tokens3grams = ngg.generateThreeGrams(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokens3grams, docLength, dictionaryMap3gram, inverseDocFreqMap3gram);
                    } else if (k == 4) {
                        tokens4grams = ngg.generateFourGrams(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokens4grams, docLength, dictionaryMap4gram, inverseDocFreqMap4gram);
                    } else if (k == 5) {
                        tokens2gramsBasic = ngg.generateTwoGramsBasic(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokens2gramsBasic, docLength, dictionaryMap2gramBasic, inverseDocFreqMap2gramBasic);
                    } else if (k == 6) {
                        tokensWhiteSpace = ngg.generateSpaces(docContents);
                        calculateTFandIDF(docContents, docName, collectionSize, tokensWhiteSpace, docLength, dictionaryMapWhiteSpace, inverseDocFreqMapWhiteSpace);
                    }
                } catch (IOException ioe) {
                    System.out.println("error");
                }
            }
        }
        if (k == 1) {
            calculateTFIDF(dictionaryMap1gram, inverseDocFreqMap1gram);
        } else if (k == 2) {
            calculateTFIDF(dictionaryMap2gram, inverseDocFreqMap2gram);
        } else if (k == 3) {
            calculateTFIDF(dictionaryMap3gram, inverseDocFreqMap3gram);
        } else if (k == 4) {
            calculateTFIDF(dictionaryMap4gram, inverseDocFreqMap4gram);
        } else if (k == 5) {
            calculateTFIDF(dictionaryMap2gramBasic, inverseDocFreqMap2gramBasic);
        } else if (k == 6) {
            calculateTFIDF(dictionaryMapWhiteSpace, inverseDocFreqMapWhiteSpace);
        }
    }

    public static void calculateTFandIDF(String docContents, String docName, int collectionSize,
            ArrayList tokens, float docLength, ConcurrentHashMap dictionaryMap, ConcurrentHashMap inverseDocFreqMap) {
        ArrayList<String[]> currentPostingsList = new ArrayList<String[]>();
        String[] posting = new String[4];

        //CONSTANTS
        final int IDF = 1;
        final int DOC_NAME = 0;
        final int FREQ = 1;
        final int TERM_FREQ = 2; //the frequency divided by the document length
        final int TFIDF_WEIGHT = 3; // log_e(Total number of documents / Number of documents with term t in it)

        int position = 0;
        boolean inThisDoc = false;
        boolean inMapAlready = false;

        for (int j = 0; j < tokens.size(); j++) {
            String currentToken = tokens.get(j).toString();
            if (dictionaryMap.isEmpty()) { //add first term
                //add this occurence to the inverseDocFreqMap
                Double[] doubleArray = {1.0, Math.log(collectionSize / 1.0)}; //1.0 because this is the first occurance
                inverseDocFreqMap.put(currentToken, doubleArray);
                //add this occurence to the dictionaryMap
                double newTermFrequency = 1.0 / docLength;
                String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), "0"};
                ArrayList<String[]> newPostingsList = new ArrayList<String[]>();
                newPostingsList.add(newPosting);
                dictionaryMap.put(currentToken, newPostingsList);

            } else {
                if (dictionaryMap.containsKey(currentToken)) { //word is in hashmap - 2 possibilities: in this doc, or not
                    inMapAlready = true;
                    currentPostingsList = (ArrayList) dictionaryMap.get(currentToken); //get the arraylist

                    for (int i = 0; i < currentPostingsList.size(); i++) {
                        if (currentPostingsList.get(i)[DOC_NAME].equals(docName)) { //if word already in this document
                            inThisDoc = true;
                            posting = currentPostingsList.get(i);
                            position = i;
                            break;
                        }
                    }
                    if (inThisDoc) {

                        //iDF doesn't change, just need to change TF
                        int newFrequency = (Integer.parseInt(posting[FREQ])) + 1; //add one to the frequency
                        System.out.println(currentToken + newFrequency);
                        float newTermFrequency = newFrequency / docLength; // calculate a new TF
                        posting[FREQ] = String.valueOf(newFrequency); //update the posting with the new frequency
                        posting[TERM_FREQ] = String.valueOf(newTermFrequency);
                        currentPostingsList.set(position, posting); //update arraylist
                        dictionaryMap.put(currentToken, currentPostingsList);
                        inThisDoc = false;

                    } else { //not in this doc: 

                        // 1) update inverseDocFreqMap
                        Double[] temp = (Double[]) inverseDocFreqMap.get(currentToken);
                        Double x = temp[0] + 1.0; //add 1 to no. of docs
                        temp[0] = x;
                        temp[1] = Math.log(collectionSize / x); //recalculate IDF
                        inverseDocFreqMap.put(currentToken, temp);

                        // 2) add new posting(array) 
                        float newTermFrequency = 1 / docLength;
                        double newTFIDFweight = temp[1] * newTermFrequency;
                        String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), String.valueOf(newTFIDFweight)};
                        currentPostingsList.add(newPosting);
                    }

                } else { //word not in hashmap - add new array and arraylist

                    //add new entry to inverseDocFreqMap, frequency is one
                    Double[] doubleArray = {1.0, Math.log(collectionSize / 1.0)};
                    inverseDocFreqMap.put(currentToken, doubleArray);

                    //add new posting to dictionaryMap
                    double newTermFrequency = 1.0 / docLength;
                    String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), "0"};
                    ArrayList<String[]> newPostingsList = new ArrayList<String[]>();
                    newPostingsList.add(newPosting);
                    dictionaryMap.put(currentToken, newPostingsList);
                }
            }
        }
    }

    public static void calculateTFandIDFapprox(String docContents, String docName, int collectionSize,
            ArrayList tokens, float docLength, ConcurrentHashMap dictionaryMap, ConcurrentHashMap inverseDocFreqMap) {
        ArrayList<String[]> currentPostingsList = new ArrayList<String[]>();
        String[] posting = new String[4];

        //CONSTANTS
        final int IDF = 1;
        final int DOC_NAME = 0;
        final int FREQ = 1;
        final int TERM_FREQ = 2; //the frequency divided by the document length
        final int TFIDF_WEIGHT = 3; // log_e(Total number of documents / Number of documents with term t in it)

        int position = 0;
        boolean inThisDoc = false;
        boolean inMapAlready = false;
        boolean inThisDoc2 = false;
        boolean inMapAlready2 = false;
        boolean approxMatch = false;
        int position2 = 0;

        for (int j = 0; j < tokens.size(); j++) {
            String currentToken = tokens.get(j).toString();

            if (dictionaryMap.isEmpty()) { //add first term
                //add this occurence to the inverseDocFreqMap
                Double[] doubleArray = {1.0, Math.log(collectionSize / 1.0)}; //1.0 because this is the first occurance
                inverseDocFreqMap.put(currentToken, doubleArray);
                //add this occurence to the dictionaryMap
                double newTermFrequency = 1.0 / docLength;
                String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), "0"};
                ArrayList<String[]> newPostingsList = new ArrayList<String[]>();
                newPostingsList.add(newPosting);
                dictionaryMap.put(currentToken, newPostingsList);

            } else {
                if (dictionaryMap.containsKey(currentToken)) { //word is in hashmap - 2 possibilities: in this doc, or not
                    inMapAlready = true;
                    currentPostingsList = (ArrayList) dictionaryMap.get(currentToken); //get the arraylist

                    for (int i = 0; i < currentPostingsList.size(); i++) {
                        if (currentPostingsList.get(i)[DOC_NAME].equals(docName)) { //if word already in this document
                            inThisDoc = true;
                            posting = currentPostingsList.get(i);
                            position = i;
                            break;
                        }
                    }
                    if (inThisDoc) {
                        //iDF doesn't change, just need to change TF
                        int newFrequency = (Integer.parseInt(posting[FREQ])) + 1; //add one to the frequency
                        System.out.println(currentToken + newFrequency);
                        float newTermFrequency = newFrequency / docLength; // calculate a new TF
                        posting[FREQ] = String.valueOf(newFrequency); //update the posting with the new frequency
                        posting[TERM_FREQ] = String.valueOf(newTermFrequency);
                        currentPostingsList.set(position, posting); //update arraylist
                        dictionaryMap.put(currentToken, currentPostingsList);
                        inThisDoc = false;

                    } else { //not in this doc: 
                        // 1) update inverseDocFreqMap
                        Double[] temp = (Double[]) inverseDocFreqMap.get(currentToken);
                        Double x = temp[0] + 1.0; //add 1 to no. of docs
                        temp[0] = x;
                        temp[1] = Math.log(collectionSize / x); //recalculate IDF
                        inverseDocFreqMap.put(currentToken, temp);
                        // 2) add new posting(array) 
                        float newTermFrequency = 1 / docLength;
                        double newTFIDFweight = temp[1] * newTermFrequency;
                        String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), String.valueOf(newTFIDFweight)};
                        currentPostingsList.add(newPosting);
                    }
                } else { //word not in hashmap - first check if it matches approximately:
                    Iterator it = dictionaryMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry termEntry = (Map.Entry) it.next();
                        String key = (String) termEntry.getKey();
                        Double levScore = lev.score(currentToken, key);
                        if (levScore.equals(-1.0)) { //we have an approximate match - add posting to postings list
                            approxMatch = true;
                            tempString = key;
                            break;
                            
                        }
                    }
                }

                if (approxMatch) {
                    // 1) update inverseDocFreqMap
                    Double[] temp = (Double[]) inverseDocFreqMap.get(tempString);
                    Double x = temp[0] + 1.0; //add 1 to no. of docs
                    temp[0] = x;
                    temp[1] = Math.log(collectionSize / x); //recalculate IDF
                    inverseDocFreqMap.put(currentToken, temp);
                    // 2) add new posting(array) 
                    float newTermFrequency = 1 / docLength;
                    double newTFIDFweight = temp[1] * newTermFrequency;
                    String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), String.valueOf(newTFIDFweight)};
                    currentPostingsList.add(newPosting);
                } else {
                    //add new entry to inverseDocFreqMap, frequency is one
                    Double[] doubleArray = {1.0, Math.log(collectionSize / 1.0)};
                    inverseDocFreqMap.put(currentToken, doubleArray);
                    //add new posting to dictionaryMap
                    double newTermFrequency = 1.0 / docLength;
                    String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), "0"};
                    ArrayList<String[]> newPostingsList = new ArrayList<String[]>();
                    newPostingsList.add(newPosting);
                    dictionaryMap.put(currentToken, newPostingsList);
                }
            }
        }
    }

    public static void calculateTFIDF(ConcurrentHashMap dictionaryMap, ConcurrentHashMap inverseDocFreqMap) {

        Iterator it3 = dictionaryMap.entrySet().iterator();
        while (it3.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it3.next();
            String key = (String) termEntry.getKey();
            ArrayList<String[]> postingsList = (ArrayList) termEntry.getValue();
            Double[] algo = (Double[]) inverseDocFreqMap.get(key);
            double IDF = algo[1];

            //CONSTANTS
            final int TERM_FREQ = 2; //the frequency divided by the document length
            final int TFIDF_WEIGHT = 3;

            for (int i = 0; i < postingsList.size(); i++) {

                String[] posting = postingsList.get(i); //get the posting
                double tF = Double.parseDouble(posting[TERM_FREQ]); //get the termfrequency
                double newTFIDF = IDF * tF; // calculate a new TFIDF
                posting[TFIDF_WEIGHT] = String.valueOf(newTFIDF); //re-assign the new TFIDF
                postingsList.set(i, posting); //update postingslist (arraylist)
            }
            dictionaryMap.put(key, postingsList); //finished, add postings list to map
        }
    }

    public static String getMatches(double minimumTFIDF, int quantityOfPostings, ConcurrentHashMap dictionaryMap, ConcurrentHashMap inverseDocFreqMap) {

        String output = "";
        Iterator it2 = dictionaryMap.entrySet().iterator();

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
                if ((Double.parseDouble(postingsList.get(i)[tFiDF]) > minimumTFIDF) && (postingsList.size() >= 2) && (postingsList.size() <= quantityOfPostings)) {
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
                    System.out.print(", Frequency: " + postingsList.get(i)[frequency]);
                    output = output + ", Frequency: " + postingsList.get(i)[frequency];

                    System.out.println(postingsList.size());
                    output = output + postingsList.size() + "\n";

                    for (int j = 0; j < postingsList.size(); j++) {
                        System.out.print(postingsList.get(j)[documentName] + ", ");
                        output = output + postingsList.get(j)[documentName] + ", ";
                    }
                    System.out.println(" ");
                    System.out.println(" ");
                    output = output + "\n\n";
                }
            }
        }
        return output;
    }

    public static void aggregate(ConcurrentHashMap dictionaryMap) {
        System.out.println("running");
        Iterator it2 = dictionaryMap.entrySet().iterator();
        while (it2.hasNext()) {
            System.out.println("working");
            Map.Entry termEntry = (Map.Entry) it2.next();
            //String key = (String) termEntry.getKey();
            ArrayList<String[]> postingsList = (ArrayList) termEntry.getValue();
            System.out.println("postingsList length is: " + postingsList.size());
            for (int i = 0; i < postingsList.size() - 1; i++) {
                for (int j = i + 1; j < postingsList.size(); j++) {
                    String temp = postingsList.get(i)[0] + postingsList.get(j)[0];
                    System.out.println("i is " + i + ", j is " + j + " " + postingsList.get(i)[0] + postingsList.get(j)[0]);
                    System.out.println("temp is " + temp);
                    if (tallyChart.containsKey(temp)) {
                        System.out.println("match");
                        int count = tallyChart.get(temp);
                        count++;
                        tallyChart.put(temp, count);
                    } else {
                        tallyChart.put(temp, 1);
                        System.out.println("no match");

                    }
                }
            }
        }
    }

    public static void printTally(ConcurrentHashMap tallyChart, ConcurrentHashMap inverseDocFreqMap) {

        Iterator it = tallyChart.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it.next();
            //            
            String key = (String) termEntry.getKey();
            Double[] algo = (Double[]) inverseDocFreqMap.get(key);
            double IDF = algo[1];
            //
            int count = Integer.parseInt(termEntry.getValue().toString());
            if (count > 90 && IDF > 3) {
                System.out.println(termEntry.getKey().toString() + " " + termEntry.getValue().toString());
            }
        }
    }
}
