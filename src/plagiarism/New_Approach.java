/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plagiarism;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author David
 */
public class New_Approach {

    static List doc1List;
    static List doc2List;
    static String doc1Name;
    static String doc2Name;
    static String doc1Contents;
    static String doc2Contents;
    static HashMap newMap = new HashMap();

    public static void main(String[] args) {
//        String doc1 = "this is the first document";
//        String doc2 = "this is the second document this is the first document";
//        String doc3 = "this is the third document this is the second document";
//
//        LinkedList list1 = createLinkedList(doc1);
//        LinkedList list2 = createLinkedList(doc2);
//        LinkedList list3 = createLinkedList(doc3);
//
//        int minimum_nGram_size = 3;
//        compareDocs(minimum_nGram_size, list1, list2, newMap, "doc1", "doc2");
//        compareDocs(minimum_nGram_size, list1, list3, newMap, "doc1", "doc3");
//        compareDocs(minimum_nGram_size, list2, list3, newMap, "doc2", "doc3");
//        printTally(newMap);

        readFiles();
        printTally(newMap);
        generateCSV(newMap);

    }

    public static LinkedList createLinkedList(String docContents) {
        LinkedList list = new LinkedList();
        StringTokenizer st = new StringTokenizer(docContents);
        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        return list;
    }

    public static void compareDocs(int minimum_nGram_size, List list1, List list2, HashMap newMap, String doc1, String doc2) {
        String docPair = doc1 + doc2;
        String match = "";
        int current_size = 0;
        int tally = 0;
        //System.out.println("Size of list1: " + list1.size() + ", and size of list2: " + list2.size());

        for (int i = 0; i < list1.size(); i++) {
            for (int j = 0; j < list2.size(); j++) {
                String tokenA1 = (String) list1.get(i);
                String tokenB1 = (String) list2.get(j);
                //System.out.println("1) Token A1: " + tokenA1 + ", Token B1: " + tokenB1);

                if (tokenA1.equals(tokenB1)) {
                    int indexA = i;
                    int indexB = j;
                    while (tokenA1.equals(tokenB1)) {
                        //System.out.println("2) Token A1: " + tokenA1 + ", Token B1: " + tokenB1);
                        current_size++;
                        match = match + " " + tokenA1;
                        indexA = indexA + 1;
                        indexB = indexB + 1;

                        //System.out.println("indexA: " + indexA + ", and indexB: " + indexB);
                        if (indexA < list1.size() && indexB < list2.size()) {
                            tokenA1 = (String) list1.get(indexA);
                            tokenB1 = (String) list2.get(indexB);
                        } else {
                            //System.out.println("out of range");
                            break;
                        }
                    }
                    if (current_size >= minimum_nGram_size) {
                        System.out.println(match);
                        tally = tally + 1;

                    }
                    match = "";
                    current_size = 0;

                }

            }
        }
        newMap.put(docPair, tally);
    }

    public static void printTally(HashMap newMap) {
        Iterator it = newMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry termEntry = (Map.Entry) it.next();
            System.out.println(termEntry);
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

    public static void readFiles() {

        //read all files in a directory
        File path = new File("C:\\Users\\David\\Desktop\\code20");
        //C:\Users\c1343067.X7054D28FCA47.000\Desktop\code3
        //File path = new File("C:\\Users\\c1343067.X7054D28FCA47.000\\Desktop\\code");
        //File path = new File("C:\\Users\\c1343067.X7054D28FCAE8\\Desktop\\code3");
        File[] files = path.listFiles();
        int collectionSize = files.length; //total number of docs in collection

        ReadFile rf = new ReadFile();

        for (int i = 0; i < files.length; i++) {

            if (files[i].isFile()) {
                try {
                    doc1Name = files[i].getName();
                    doc1Contents = rf.readFile(files[i].toString());
                    doc1List = createLinkedList(doc1Contents);

                } catch (IOException ioe) {
                    System.out.println("error");
                }

                for (int j = i + 1; j < files.length; j++) {
                    try {
                        doc2Name = files[j].getName();
                        doc2Contents = rf.readFile(files[j].toString());
                        doc2List = createLinkedList(doc2Contents);
                        System.out.println("");
                        System.out.println("");
                        System.out.println(" - - NEW COMPARISON - - " + doc1Name + " AND " + doc2Name);
                        System.out.println("");
                        System.out.println("");
                        compareDocsKMP(7, doc1List, doc2List, newMap, doc1Name, doc2Name);

                    } catch (IOException ioe) {
                        System.out.println("error");
                    }
                }
            }
        }
    }

    public static void compareDocsKMP(int minimum_nGram_size, List list1, List list2, HashMap newMap, String doc1, String doc2) {
        String docPair = doc1 + doc2;
        String match = "";
        int current_size = 0;
        int tally = 0;
        //System.out.println("Size of list1: " + list1.size() + ", and size of list2: " + list2.size());

        for (int i = 0; i < list1.size(); i++) {
            for (int j = 0; j < list2.size(); j++) {
                String tokenA1 = (String) list1.get(i);
                String tokenB1 = (String) list2.get(j);
                //System.out.println("1) Token A1: " + tokenA1 + ", Token B1: " + tokenB1);

                if (tokenA1.equals(tokenB1)) {
                    int indexA = i;
                    int indexB = j;
                    String firstMatch = tokenA1;
                    while (tokenA1.equals(tokenB1)) {
                        //System.out.println("2) Token A1: " + tokenA1 + ", Token B1: " + tokenB1);
                        current_size++;
                        match = match + " " + tokenA1;
                        indexA = indexA + 1;
                        indexB = indexB + 1;

                        //System.out.println("indexA: " + indexA + ", and indexB: " + indexB);
                        if (indexA < list1.size() && indexB < list2.size()) {
                            i++;
                            tokenA1 = (String) list1.get(indexA);
                            tokenB1 = (String) list2.get(indexB);
                        } else {
                            //System.out.println("out of range");
                            break;
                        }
                    }
                    if (current_size >= minimum_nGram_size) {
                        System.out.println(match);
                        tally = tally + 1;

                    }
                    match = "";
                    current_size = 0;

                }

            }
        }
        newMap.put(docPair, tally);
    }
}
