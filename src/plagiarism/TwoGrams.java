package plagiarism;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public class TwoGrams {

    private final ConcurrentHashMap<String, ArrayList<String[]>> dictionaryMap = new ConcurrentHashMap<String, ArrayList<String[]>>();
    // Key is term
    // Value is array list (postings list) of string arrays (postings) of the form [docName, frequency, termFrequency (tf), tF-idf weight]
    private final ConcurrentHashMap<String, Double[]> inverseDocFreqMap = new ConcurrentHashMap<String, Double[]>();
    // Key is term
    // Value is double array containing [no. of documents in which this term occurs, inverse document frequency (idf)]

    public TwoGrams(){} 
 
    public void generate() {

        //read all files in a directory
        //http://stackoverflow.com/questions/4917326/how-to-iterate-over-the-files-of-a-certain-directory-in-java
        File path = new File("C:\\Users\\David\\Desktop\\code");
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
                    al = ngg.generateTwoGrams(docContents);
                    StringTokenizer st = new StringTokenizer(docContents);
                    float docLength = st.countTokens();
                    calculateTFandIDF(docContents, docName, collectionSize, al, docLength);
                } catch (IOException ioe) {
                    System.out.println("error");
                }
            }
        }
        calculateTFIDF();

    }

    public ConcurrentHashMap calculateTFandIDF(String docContents, String docName, int collectionSize, ArrayList al, float docLength) {

        ArrayList<String[]> currentPostingsList = new ArrayList<String[]>();
        String[] posting = new String[4];

        //CONSTANTS
        final int IDF = 1;
        final int documentName = 0;
        final int frequency = 1;
        final int termFrequency = 2; //the frequency divided by the document length
        final int TFIDFweight = 3; // log_e(Total number of documents / Number of documents with term t in it)

        int position = 0;
        boolean inThisDoc = false;
        boolean inMapAlready = false;

        for (int j = 0; j < al.size(); j++) {
            String currentToken = al.get(j).toString();
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
                        if (currentPostingsList.get(i)[documentName].equals(docName)) { //if word already in this document
                            inThisDoc = true;
                            posting = currentPostingsList.get(i);
                            position = i;
                            break;
                        }
                    }

                    if (inThisDoc) {

                        //iDF doesn't change, just need to change TF
                        

                        int newFrequency = Integer.parseInt(posting[frequency]) + 1; //add one to the frequency
                        float newTermFrequency = newFrequency / docLength; // calculate a new TF
                        posting[frequency] = String.valueOf(newFrequency); //update the posting with the new frequency
                        posting[termFrequency] = String.valueOf(newTermFrequency);
                        currentPostingsList.set(position, posting); //update arraylist
                        dictionaryMap.put(currentToken, currentPostingsList);
                        inThisDoc = false;

                    } else { //not in this doc: 
                        // 1) update inverseDocFreqMap
                        // 2) add new posting(array) 

                        // 1)
                        Double[] temp = inverseDocFreqMap.get(currentToken);
                        Double x = temp[0] + 1.0; //add 1 to no. of docs
                        temp[0] = x;
                        temp[1] = Math.log(collectionSize / x); //recalculate IDF
                        inverseDocFreqMap.put(currentToken, temp);

                        // 2)
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
        return dictionaryMap;
    }

    public void calculateTFIDF() {
        Iterator it3 = dictionaryMap.entrySet().iterator();
        while (it3.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it3.next();
            String key = (String) termEntry.getKey();
            ArrayList<String[]> postingsList = (ArrayList) termEntry.getValue();
            double IDF = inverseDocFreqMap.get(key)[1];

            //constants
            int termFrequency = 2; //the frequency divided by the document length
            int tFiDF = 3;

            for (int i = 0; i < postingsList.size(); i++) {

                String[] posting = postingsList.get(i); //get the posting
                double tF = Double.parseDouble(posting[termFrequency]); //get the termfrequency
                double newTFIDF = IDF * tF; // calculate a new TFIDF
                posting[tFiDF] = String.valueOf(newTFIDF); //re-assign the new TFIDF
                postingsList.set(i, posting); //update postingslist (arraylist)

            }
            dictionaryMap.put(key, postingsList); //finished, add postings list to map
        }
    }
    
    public void getMatches(double minimumTFIDF, int quantityOfPostings) {
        Iterator it2 = dictionaryMap.entrySet().iterator();
        System.out.println(" - - - - - tFiDF > " + minimumTFIDF + " - - - - - -");
        System.out.println(" ");

        while (it2.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it2.next();
            String key = (String) termEntry.getKey();
            ArrayList<String[]> postingsList = (ArrayList) termEntry.getValue();
            double IDF = inverseDocFreqMap.get(key)[1];

            int documentName = 0;
            int frequency = 1;
            int termFrequency = 2; //the frequency divided by the document length
            int tFiDF = 3;

            for (int i = 0; i < postingsList.size(); i++) {
                if ((Double.parseDouble(postingsList.get(i)[tFiDF]) > minimumTFIDF) && (postingsList.size() >= 2) && (postingsList.size() <= quantityOfPostings)) {

                    System.out.print("'" + key + "'");
                    System.out.print(", iDF: " + IDF);
                    System.out.print(", Document: " + postingsList.get(i)[documentName]);
                    System.out.print(", Frequency: " + postingsList.get(i)[frequency]);
                    System.out.print(", Term Frequency: " + postingsList.get(i)[termFrequency]);
                    System.out.print(", tFiDF: " + postingsList.get(i)[tFiDF] + " || ");
                    System.out.println(postingsList.size());

                    for (int j = 0; j < postingsList.size(); j++) {
                        System.out.print(postingsList.get(j)[documentName] + ", ");
                    }

                    System.out.println(" ");
                    System.out.println(" ");
                }
            }
        }
    }
}
