/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plagiarism;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author c1343067
 */
public class Basics {

    public static void main(String args[]) {

        String docName = "doc1";
        ArrayList<String[]> TermDataList = new ArrayList<String[]>();
        String docContents = "this is is is a is string of words words";
        int freq = 1;

        StringTokenizer st = new StringTokenizer(docContents);
        
        while (st.hasMoreTokens()) {
            String currentToken = st.nextToken();
            System.out.println("new token: " + currentToken);
            if (TermDataList.isEmpty()) {
                String[] temp = {docName, currentToken, "1", "0"};
                TermDataList.add(temp);
            } else {
                for (int i = 0; i < TermDataList.size(); i++) {
                    if (TermDataList.get(i)[1].equals(currentToken)) {
                        freq = Integer.parseInt(TermDataList.get(i)[2]) + 1;
                        TermDataList.remove(i);  
                    }
                }
                String[] temp = {docName, currentToken, "1", "0"};
                temp[2] = String.valueOf(freq);
                TermDataList.add(temp);
                freq = 1;
            }
        }


        for (int i = 0; i < TermDataList.size(); i++) {
            String[] temp2 = TermDataList.get(i);
            System.out.println(temp2[1] + ", Frequency: " + temp2[2]);
        }
    }
}