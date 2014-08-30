package plagiarism;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class TestFile {

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

    public static void calculateTFandIDF(String docContents, String docName, int collectionSize,
            ArrayList tokens, double docLength, ConcurrentHashMap dictionaryMap, ConcurrentHashMap inverseDocFreqMap) {
        System.out.println("calculateTFandIDF running");
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

    public static void TESTcalculateTFandIDF() {

            String docContents = "this is a test";
            String docName = "doc1";
            int collectionSize = 1;
            ArrayList<String> tokens = new ArrayList<String>();
            tokens.add("this");
            tokens.add("is");
            tokens.add("a");
            tokens.add("test");
            double docLength = 4;
            ConcurrentHashMap<String, ArrayList<String[]>> dictionaryMap = new ConcurrentHashMap<String, ArrayList<String[]>>();
            ConcurrentHashMap<String, Double[]> inverseDocFreqMap = new ConcurrentHashMap<String, Double[]>();
            
            calculateTFandIDF(docContents, docName, collectionSize, tokens, docLength, dictionaryMap, inverseDocFreqMap);
       
    }
    
    public static void main (String[] args) {
        TESTcalculateTFandIDF();
    }
}
