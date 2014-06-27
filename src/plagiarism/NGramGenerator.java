package plagiarism;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class NGramGenerator {
    
    public ArrayList generateOneGrams(String docContents) {
        StringTokenizer st = new StringTokenizer(docContents);
        
        ArrayList nGrams = new ArrayList<String>();

        while (st.hasMoreTokens()) {
            nGrams.add(st.nextToken());

        }
        return nGrams;
        
    }
    
    public ArrayList generateTwoGramsBasic(String docContents) {

        docContents = docContents + " "; //ensures last word is added.
        char[] a_char_array = docContents.toCharArray();
        ArrayList<String> nGrams = new ArrayList<String>();

        boolean firstWord = true;
        boolean addWord = false;
        int position = 0;
        String currentWord = "";
        String lastChar;

        for (int i = 0; i < a_char_array.length; i++) { //start at second position becuase first char already added
            //new char
            String currentChar = String.valueOf(a_char_array[i]);
            if (currentWord.length() == 0) {
                currentWord = currentWord + currentChar;
            } else {
                lastChar = String.valueOf(a_char_array[i - 1]); //set last character
                if (lastChar.matches("\\s") && currentChar.matches("\\s") && firstWord) { //WW
                    position = i - 1; // set position for next n-gram      
                    addWord = true;
                } else if (lastChar.matches("\\s") && !currentChar.matches("\\s") && firstWord) {//WC yes
                    position = i - 1;
                    currentWord = currentWord + currentChar;
                    firstWord = false;
                } else if (!lastChar.matches("\\s") && !currentChar.matches("\\s") && firstWord) { //CC yes
                    currentWord = currentWord + currentChar;
                } else if (!lastChar.matches("\\s") && currentChar.matches("\\s") && firstWord) { //CW yes
                    currentWord = currentWord + currentChar;
                    position = i - 1;
                    addWord = true;
                } else if (lastChar.matches("\\s") && currentChar.matches("\\s") && !firstWord) { //WW
                    position = i - 1;
                    addWord = true;
                } else if (lastChar.matches("\\s") && !currentChar.matches("\\s") && !firstWord) {//WC yes
                    currentWord = currentWord + currentChar;
                    // nb this never gets reached because if the previous char is W, then the word gets added.
                    firstWord = true;
                } else if (!lastChar.matches("\\s") && !currentChar.matches("\\s") && !firstWord) { //CC yes
                    currentWord = currentWord + currentChar;
                } else if (!lastChar.matches("\\s") && currentChar.matches("\\s") && !firstWord) { //CW
                    addWord = true;
                }

                if (addWord) {
                    nGrams.add(currentWord);
                    currentWord = "";
                    i = position;
                    firstWord = true;
                    addWord = false;
                }
            }
        }
        return nGrams;
    }
    
    public ArrayList generateTwoGrams(String docContents) {

        docContents = docContents + " ";
        char[] inputCharacters = docContents.toCharArray();
        boolean firstBreak = false;
        boolean firstChar = true;
        boolean lastCharWasSpace = false;
        int position = 0;
        String temp = "";
        ArrayList<String> nGrams = new ArrayList();
        
        for (int i = 0; i < docContents.length()-1; i++) {
            String currentChar = String.valueOf(inputCharacters[i]);
            if (currentChar.matches("\\S")) { // not whitespace
                
                if (lastCharWasSpace && !firstChar) { //start of second word - set position 
                    position = i-1;
                }
                
                if (firstChar) { //new two-gram, first letter of first word
                    firstChar = false;   
                }
                
                temp = temp + currentChar; //add character to temp
                lastCharWasSpace = false;
            } else { // not a character (is a space)
                if (!firstBreak && !lastCharWasSpace) { //end of first word 
                    temp = temp + currentChar; //add space to temp

                    firstBreak = true; //set firstBreak
                    //position = i; //set position ready for next word
                } else if (firstBreak && !lastCharWasSpace) { // end of second word
                    nGrams.add(temp); //add temp to arraylist
                    firstChar = true; // set first char ready for next word
                    i = position; //move i back to middle of last pair
                    firstBreak = false; // do we need this?
                    temp = ""; //reset temp to "";
                } else if (lastCharWasSpace) { //more than one whitespace
                    temp = temp + currentChar; //add space to temp
                }
                lastCharWasSpace = true;
            }
        }
       
        return nGrams;

    }
    
    public ArrayList generateThreeGrams(String docContents) {

        docContents = docContents + " ";
        char[] inputCharacters = docContents.toCharArray();
        boolean firstBreak = false;
        boolean secondBreak = false;
        boolean firstChar = true;
        boolean lastCharWasSpace = false;
        int position = 0;
        String temp = "";
        ArrayList<String> nGrams = new ArrayList();

        for (int i = 0; i < docContents.length() - 1; i++) {
            String currentChar = String.valueOf(inputCharacters[i]);    //get current character as string
            if (currentChar.matches("\\S")) {                           //not white space, then add to current string
                
                if (lastCharWasSpace && !firstChar) { //start of second word - set position 
                    position = i-1;
                }
                if (firstChar) {
                    firstChar = false;
                }
                temp = temp + currentChar;
                lastCharWasSpace = false;
            } else {                                                    //not a character (is a space)

                if (!firstBreak && !secondBreak && !lastCharWasSpace) {                 //end of first word 
                    temp = temp + currentChar;                          //add space to temp
                    firstBreak = true;                                  //set firstBreak, i.e. first word finished
                    //position = i;                                       //set position ready for next word
                } else if (firstBreak && !secondBreak && !lastCharWasSpace) { //end of second word 
                    temp = temp + currentChar;                          //add space to temp
                    secondBreak = true;                                  //set firstBreak, i.e. first word finished
                                                                        //no need to set position
                } else if (firstBreak && secondBreak &&!lastCharWasSpace) {           // end of third word
                    nGrams.add(temp);                                //add temp to arraylist
                    firstChar = true;                                   // set first char ready for next word
                    i = position;                                       //move i back to middle of last pair
                    firstBreak = false;                                 // do we need this?
                    secondBreak = false;
                    temp = "";                                          //reset temp to "";
                } else if (lastCharWasSpace) {                          //more than one whitespace
                    temp = temp + currentChar;                          //add space to temp
                }
                lastCharWasSpace = true;
            }
        }
        return nGrams;

    }
}
    

