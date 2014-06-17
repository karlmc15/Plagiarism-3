package plagiarism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Patterns {

    public static void main(String args[]) {

        String pattern = "(?<=\\W)";
        String s = "this is another test string";

        Pattern splitter = Pattern.compile(pattern);
        String[] results = splitter.split(s);

        for (String pair : results) {
            System.out.println(pair);
        }
        System.out.println(Arrays.toString(results));


        String input = "one two three four   five six seven{ {}";
        String[] pairs = input.split("(?<!\\G\\S+)\\s");
        System.out.println(Arrays.toString(pairs));


        String basic = "one     two three four five six seven ";
        basic = basic + " ";
        char[] x = basic.toCharArray();
        boolean firstBreak = false;
        boolean firstChar = true;
        int position = 0;
        String temp = "";
        ArrayList<String> arrayList = new ArrayList();
        
        for (int i = 0; i < basic.length()-1; i++) {
            
            System.out.println("new loop: " + x[i]);

            String currentChar = String.valueOf(x[i]);
            if (currentChar.matches("\\S")) {
                System.out.println("this is a character: " + currentChar);
                if (firstChar) { 
                    firstChar = false;
                    
                }
                temp = temp + currentChar;
            } else { // not a character (is a space)
                System.out.println("this is not a character: " + currentChar);
                if (!firstBreak) { //end of first word 
                    temp = temp + currentChar; //add space to temp
                    System.out.println(temp); 
                    firstBreak = true; //set firstBreak
                    position = i; //set position ready for next word
                } else { // end of second word
                    arrayList.add(temp); //add temp to arraylist
                    System.out.println("adding temp: " + temp);
                    firstChar = true; // set first char ready for next word
                    i = position; //move i back to middle of last pair
                    firstBreak = false; // do we need this?
                    temp = ""; //reset temp to "";
                }
            }
        }
        //arrayList.add(temp);
        for (int i = 0; i < arrayList.size(); i++) {
            System.out.println(arrayList.get(i).toString());
        }

    }
}