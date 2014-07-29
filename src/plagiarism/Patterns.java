package plagiarism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Patterns {

    public static void main(String args[]) {


        String basic = "one two three   {   f   our f  ive six seven {some java;; blah =-      () ";
        basic = basic + " ";
        char[] x = basic.toCharArray();
        boolean firstBreak = false;
        boolean firstChar = true;
        boolean lastCharWasSpace = false;
        int position = 0;
        String temp = "";
        ArrayList<String> arrayList = new ArrayList();
        
        for (int i = 0; i < basic.length()-1; i++) {
            
            System.out.println("new loop: " + x[i]);
            String currentChar = String.valueOf(x[i]);
            if (currentChar.matches("\\S")) {
                System.out.println("this is a character: " + currentChar);
                if (firstChar) { 
                    System.out.println("this is the first character of a word");
                    firstChar = false;   
                }
                temp = temp + currentChar;
                System.out.print("adding character");
                lastCharWasSpace = false;
            } else { // not a character (is a space)
                System.out.println("this is whitespace: " + currentChar);
                
                if (!firstBreak && !lastCharWasSpace) { //end of first word 
                    temp = temp + currentChar; //add space to temp
                    System.out.println("adding whitespace ");
                    System.out.println(temp); 
                    firstBreak = true; //set firstBreak
                    position = i; //set position ready for next word
                } else if (firstBreak && !lastCharWasSpace) { // end of second word
                    arrayList.add(temp); //add temp to arraylist
                    System.out.println("adding temp: " + temp);
                    firstChar = true; // set first char ready for next word
                    i = position; //move i back to middle of last pair
                    firstBreak = false; // do we need this?
                    temp = ""; //reset temp to "";
                } else if (lastCharWasSpace) { //more than one whitespace
                    temp = temp + currentChar; //add space to temp
                    System.out.println("adding whitespace ");
                    System.out.println(temp);
                }
                lastCharWasSpace = true;
            }
        }
        //arrayList.add(temp);
        for (int i = 0; i < arrayList.size(); i++) {
            System.out.println(arrayList.get(i).toString());
        }

    }
}