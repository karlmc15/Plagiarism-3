/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plagiarism;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author c1343067
 */
public class Testing {

    public static void main(String[] args) {

        String testString = "this is a test string";
        String currentDoc = "testDoc";
        StringTokenizer st = new StringTokenizer(testString);
        ConcurrentHashMap<String, ArrayList<String[]>> testMap = new ConcurrentHashMap<String, ArrayList<String[]>>();
        ArrayList<String[]> currentArrayList = new ArrayList<String[]>();
        String[] editThis = new String[2];
        
        boolean inMapAlready = false;
        boolean inThisDoc = false;
        

        while (st.hasMoreTokens()) {
            String currentToken = st.nextToken();
            if (testMap.isEmpty()) {
                ArrayList<String[]> newArrayList = new ArrayList<String[]>();
                String[] newArray = {"new", "array"};
                newArrayList.add(newArray);
                testMap.put(currentToken, newArrayList);
            } else {
                Iterator it = testMap.entrySet().iterator();
                while (it.hasNext()) {
                    
                    Map.Entry termEntry = (Map.Entry) it.next();
                    
                    if (termEntry.getKey().equals(currentToken)){ //word is in map
                        inMapAlready = true;
                        currentArrayList = (ArrayList)termEntry.getValue();
                        for (int i =0; i<currentArrayList.size(); i++) {
                            if (currentArrayList.get(i)[0].equals(currentDoc)){
                                inThisDoc = true;
                                editThis = currentArrayList.get(i);
                                break;
                            }
                        }
                        if (inThisDoc) {
                            //ammend array
                            editThis[1] = "changed"; 
                        } else {
                            //   add new array to array list
                            //ie add new posting to postings list
                            String[] newArray2 = {"new", "array"};
                            currentArrayList.add(newArray2);
                        }
                    }
                    
                    
                    it.remove();
                }
                
                //end of iterator, i.e. not in map
                //add new array list
                
            }

        }
    
}}