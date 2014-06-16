package plagiarism;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public class Plagiarism {

    public static ConcurrentHashMap<String, ArrayList<String[]>> dictionaryMap = new ConcurrentHashMap<String, ArrayList<String[]>>();
    // Key is term
    // Value is array list (postings list) of string arrays (postings) of the form [docName, frequency, termFrequency (tf), tF-idf weight]
    public static ConcurrentHashMap<String, Double[]> inverseDocFreqMap = new ConcurrentHashMap<String, Double[]>();
    // Key is term
    // Value is double array containing [no. of documents in which this term occurs, inverse document frequency (idf)]

    public static void main(String[] args) {
        
        //read all files in a directory
        //http://stackoverflow.com/questions/4917326/how-to-iterate-over-the-files-of-a-certain-directory-in-java
        //File path = new File("C:\\Users\\David\\Desktop\\code");
        File path = new File("C:\\Users\\c1343067\\Desktop\\code");
        File[] files = path.listFiles();
        int collectionSize = files.length; //total number of docs in collection
        //ConcurrentHashMap<String, ArrayList<String[]>> x = new ConcurrentHashMap<String, ArrayList<String[]>>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                String docName = files[i].getName();
                try {
                    String docContents = readFile(files[i].toString());
                    calculateTFIDF(docContents, docName, collectionSize);
                } catch (IOException ioe) {
                    System.out.println("error");
                }
            }
        }
        //printResults1();
        printResults2();
        printResults3("testFilePath");
    }

    //this code is "Donal"'s answer from here:
    //http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
    private static String readFile(String pathname) throws IOException {

        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int) file.length());
        Scanner scanner = new Scanner(file);
        String lineSeparator = System.getProperty("line.separator");
        try {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }

    public static ConcurrentHashMap calculateTFIDF(String docContents, String docName, int collectionSize) {

        ArrayList<String[]> currentPostingsList = new ArrayList<String[]>();
        String[] posting = new String[4];

        int documentName = 0;
        int frequency = 1;
        int termFrequency = 2; //the frequency divided by the document length
        int TFIDFweight = 3; // log_e(Total number of documents / Number of documents with term t in it)

        int position = 0;
        boolean inThisDoc = false;
        boolean inMapAlready = false;

        StringTokenizer st = new StringTokenizer(docContents);
        float docLength = (float) st.countTokens();

        while (st.hasMoreTokens()) {
            String currentToken = st.nextToken();
            if (dictionaryMap.isEmpty()) {
                
                //add this occurence to the inverseDocFreqMap
                Double[] doubleArray = {1.0, Math.log(collectionSize/1.0)};
                inverseDocFreqMap.put(currentToken, doubleArray);
                                
                //add this occurence to the dictionaryMap
                float newTermFrequency = 1 / docLength;
                double newTFIDFweight = doubleArray[1]*newTermFrequency;
                String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), String.valueOf(newTFIDFweight)};
                ArrayList<String[]> newPostingsList = new ArrayList<String[]>();
                newPostingsList.add(newPosting);
                dictionaryMap.put(currentToken, newPostingsList);
                
                //done
                
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
                        
                        Double[] temp = inverseDocFreqMap.get(currentToken);
                        Double iDF = temp[1]; 
                        
                        int newFrequency = Integer.parseInt(posting[frequency]) + 1;
                        float newTermFrequency = newFrequency / docLength;
                        double newTFIDFweight = iDF*newTermFrequency;
                        
                        posting[frequency] = String.valueOf(newFrequency);
                        posting[termFrequency] = String.valueOf(newTermFrequency);
                        posting[TFIDFweight] = String.valueOf(newTFIDFweight);
                        currentPostingsList.set(position, posting); //update arraylist
                        dictionaryMap.put(currentToken, currentPostingsList);
                        inThisDoc = false;
                        
                        //done
                        
                    } else { //not in this doc - update inverseDocFreqMap and add new posting(array) 
                                              
                        Double[] temp = inverseDocFreqMap.get(currentToken);
                        Double x = temp[0] + 1.0; //add 1 to no. of docs
                        temp[0] = x;
                        temp[1] = Math.log(collectionSize/x); //recalculate IDF
                        inverseDocFreqMap.put(currentToken, temp);
                        
                        float newTermFrequency = 1 / docLength;
                        double newTFIDFweight = temp[1]*newTermFrequency; 
                        String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), String.valueOf(newTFIDFweight)};
                        currentPostingsList.add(newPosting);
                        dictionaryMap.put(currentToken, currentPostingsList);                       
                    }
                } else { //word not in hashmap - add new array and arraylist
                    
                    Double[] doubleArray = {1.0, Math.log(collectionSize/1.0)};
                    inverseDocFreqMap.put(currentToken, doubleArray);
                    
                    float newTermFrequency = 1 / docLength;
                    String[] newPosting = {docName, "1", String.valueOf(newTermFrequency), "0"};
                    ArrayList<String[]> newPostingsList = new ArrayList<String[]>();
                    newPostingsList.add(newPosting);
                    dictionaryMap.put(currentToken, newPostingsList);                                                           
                }
            }
        }
        return dictionaryMap;
    }

        //inverseDocFreqMap
        //dictionaryMap
    
    public static void printResults1() {
        Iterator it2 = inverseDocFreqMap.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it2.next();
            String key = (String) termEntry.getKey();
            //ArrayList<String[]> postingsList = (ArrayList) termEntry.getValue();
            Double[] data = (Double[])termEntry.getValue();
            int documentName = 0;
            int frequency = 1;
            int termFrequency = 2; //the frequency divided by the document length
            int inverseDocumentFrequency = 3;
            
            System.out.print("Key: " + key + ", ");
           System.out.println(data[0] + " " + data[1]);
            
          
        }
    }

    public static void printResults2() {
        Iterator it2 = dictionaryMap.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it2.next();
            String key = (String) termEntry.getKey();
            ArrayList<String[]> postingsList = (ArrayList) termEntry.getValue();
            
            int documentName = 0;
            int frequency = 1;
            int termFrequency = 2; //the frequency divided by the document length
            int iDF = 3;
    
            for (int i = 0; i < postingsList.size(); i++) {
                if ((Double.parseDouble(postingsList.get(i)[iDF]) > 0.05) && (postingsList.size()>=2) && (postingsList.size()<=5)) {
                
                    System.out.print("Key: " + key);
                    System.out.print(", Document: " + postingsList.get(i)[documentName]);
                    System.out.print(", Frequency: " + postingsList.get(i)[frequency]);
                    System.out.print(", Term Frequency: " + postingsList.get(i)[termFrequency]);
                    System.out.print(", iDF: " + postingsList.get(i)[iDF] + " || ");
                    System.out.println(postingsList.size());
                }
            }
            
        }
    }
    
    public static void printResults3(String term) {
        ArrayList<String[]> postingsList = (ArrayList)dictionaryMap.get(term);
            for (int i = 0; i < postingsList.size(); i++) {
                System.out.println(postingsList.get(i)[0]);
            }
    }

}
