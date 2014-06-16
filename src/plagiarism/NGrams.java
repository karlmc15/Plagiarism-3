package plagiarism;

import java.util.StringTokenizer;

public class NGrams {

    public static void main(String args[]) {

        String s = "this    is a test string";
        String[] tokens = s.split("\\s");
        int nGrams = 3;

        for (int i = 0; i < tokens.length - (nGrams-1); i++) {
            //System.out.println(tokens[i]);
            String[] nGramsArray = new String[tokens.length-(nGrams-1)];
            
            nGramsArray[i] = tokens[i];
            
            for (int j=i+1; j<=(i+nGrams-1); j++)
                nGramsArray[i] = nGramsArray[i] + " " + tokens[j];
            
            System.out.println(nGramsArray[i]);

        }

    }
}
