/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plagiarism;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author c1343067
 */


public class Stuff {

    public static void main(String[] args) {

        Map allDocNamesandContentsAsString = new HashMap();

        //read all files in a directory
        //http://stackoverflow.com/questions/4917326/how-to-iterate-over-the-files-of-a-certain-directory-in-java
        File path = new File("C:\\Users\\c1343067\\Desktop\\test files");
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                String docName = files[i].getName();
                try {
                    String docContents = readFile(files[i].toString());
                    System.out.println(docName + ":" + docContents);
                    allDocNamesandContentsAsString.put(docName, docContents);
                    getTermData(docContents, docName);
                } catch (IOException ioe) {
                    System.out.println("error");
                }
            }
        }

        String s1 = new String();
        String s2 = new String();
        String s3 = new String();
        s1 = "one one one";
        s2 = "this is the second document";
        s3 = "this is the third third third document";

        //put all documents in a string array allDocs
        String[] allDocs = new String[3];
        allDocs[0] = s1;
        allDocs[1] = s2;
        allDocs[2] = s3;

        //create a hashmap - I'll need to change this
        //to a hashmap per document
        //Map dictionary = new HashMap();

        Map allDocsFrequencyMap = new HashMap();
        Map allDocsTermFrequency = new HashMap();

        for (int i = 0; i < allDocs.length; i++) {

            StringTokenizer st = new StringTokenizer(allDocs[i]);
            int docLength = st.countTokens();
            Map frequencyList = new HashMap();
            while (st.hasMoreTokens()) {
                String currentToken = st.nextToken();
                System.out.println(currentToken);

                if (frequencyList.containsKey(currentToken)) {
                    Integer val = Integer.parseInt(frequencyList.get(currentToken).toString());
                    frequencyList.put(currentToken, val + 1);
                } else {
                    frequencyList.put(currentToken, 1);
                }
            }

            //add each id-frequencyList pair to this map
            allDocsFrequencyMap.put(i, frequencyList);

            //calculate the Term Frequency for each word
            //generate a id-termfrequency map
            //and add the map to the allDocs map
            Iterator it = frequencyList.entrySet().iterator();
            Map termFrequencyList = new HashMap();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                float termFrequency = Float.parseFloat(pairs.getValue().toString()) / docLength;
                termFrequencyList.put(pairs.getKey(), termFrequency);
            }
            allDocsTermFrequency.put(i, termFrequencyList);
        }

        //print to check it works
        Iterator it = allDocsFrequencyMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
            it.remove();
        }

        Iterator it2 = allDocsTermFrequency.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry pairs = (Map.Entry) it2.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
            it2.remove();
        }
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
    //String[] TermData = new String[4];
    private static ArrayList<String[]> TermDataList = new ArrayList<String[]>();

    private static void getTermData(String docContents, String docName) {
        StringTokenizer st = new StringTokenizer(docContents);
        int docLength = st.countTokens();
        String[] temp = new String[4];
        //String[] TermData = new String[4];
        while (st.hasMoreTokens()) {
            String currentToken = st.nextToken();
            System.out.println(currentToken);
            if (TermDataList.isEmpty()) {
                //temp = [docName, currentToken, "1", "0"];
                temp[0] = docName;
                temp[1] = currentToken;
                temp[2] = "1";
                temp[3] = "0";
                //TermDataList.add(temp);
                System.out.println("Is empty, added first value");

            } else {
                System.out.println("hits else");
                for (int i = 0; i < TermDataList.size(); i++) {
                    System.out.println("i: " + i);
                    System.out.println("Current token: " + currentToken);
                    System.out.println("term data list value: " + TermDataList.get(i)[1]);
                    if (TermDataList.get(i)[1].equals(currentToken)) {
                        temp = TermDataList.get(i);
                        System.out.println(temp.toString());
                        int tempFreq = Integer.parseInt(temp[2]);
                        tempFreq = tempFreq + 1;
                        System.out.println("tempfreq: " + tempFreq);
                        temp[2] = String.valueOf(tempFreq);
                        TermDataList.remove(i);
                        //TermDataList.set(i, temp);
                        System.out.println("Match!");

                    } else {
                        //temp = {docName, currentToken, "1", "0"};
                        //TermDataList.add(temp);
                        temp[0] = docName;
                        temp[1] = currentToken;
                        temp[2] = "1";
                        temp[3] = "0";
                        break;
                    }
                }

            }
        
        TermDataList.add(temp);

        }
        for (int i = 0; i < TermDataList.size(); i++) {
            String[] temp2 = TermDataList.get(i);
            System.out.println(temp2[1] + ", Frequency: " + temp2[2]);

        }
    }
}
