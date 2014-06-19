package plagiarism;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class NGrams {

    public static void main(String args[]) {

        String x = new String();
        //File fileName = new File("C:\\Users\\c1343067.X7054D28FCA3D\\Desktop\\code\\1.java");
        File fileName = new File("C:\\Users\\c1343067\\Desktop\\code\\1.java");
        try {
            x = readFile(fileName.toString());
        } catch (IOException ioe) {
            System.out.println("error");
        }


        String s = "this    is a test   string";
        String[] tokens = x.split("\\b");
        int nGrams = 3;

        for (int i = 0; i < tokens.length - (nGrams - 1); i++) {
            //System.out.println(tokens[i]);
            String[] nGramsArray = new String[tokens.length - (nGrams - 1)];

            nGramsArray[i] = tokens[i];

            for (int j = i + 1; j <= (i + nGrams - 1); j++) {
                nGramsArray[i] = nGramsArray[i] + tokens[j];
            }

            System.out.println(nGramsArray[i]);

        }

    }

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
}
