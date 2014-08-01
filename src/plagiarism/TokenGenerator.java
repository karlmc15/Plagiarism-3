package plagiarism;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class TokenGenerator {
    
//    public static void main (String[] args){
//        String testString = "this is a       test";
//        ArrayList testList = generateSpaces(testString);
//        for (int i = 0; i<testList.size(); i++){
//            System.out.println(testList.get(i));           
//        }
//    }
    
    //takes a single document, and adds all tokens to an array list
    public ArrayList<String> generateOneGrams(String docContents) {
        StringTokenizer st = new StringTokenizer(docContents);
        
        ArrayList<String> tokens = new ArrayList<String>();
        
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }
        return tokens;
    }
    
    public ArrayList<String> generateSpaces(String docContents) {
        docContents = docContents + " ";
        char[] docChars = docContents.toCharArray();
        ArrayList<String> tokens = new ArrayList<String>();
        
        StringBuilder currentToken = new StringBuilder();
        boolean tokenFlag = false;
        
        for (int i = 0; i<docChars.length; i++) {
            String currentChar = String.valueOf(docChars[i]);
            if (currentChar.matches("\\s")){ //is a space
                currentToken.append(currentChar);
                tokenFlag = true;
                //?maybe?need a break here? to go to next letter
            } else { //currentchar is a alphanumeric character
                if (tokenFlag){ //end of a token, add and reset
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                    tokenFlag = false;
                } else {
                    // do nothing
                }
            }  
        }
        return tokens;
    }
    
    public ArrayList<String> generateTwoGramsBasic(String docContents) {

        docContents = docContents + " "; //ensures last word is added.
        char[] a_char_array = docContents.toCharArray();
        ArrayList<String> tokens = new ArrayList<String>();
        
        boolean firstWord = true;
        boolean addWord = false;
        int position = 0;
        //String currentWord = "";
        StringBuilder currentToken = new StringBuilder();
        String lastChar;

        for (int i = 0; i < a_char_array.length; i++) { //start at second position becuase first char already added
            //new char
            String currentChar = String.valueOf(a_char_array[i]);
            if (currentToken.length() == 0) {
                //currentWord = currentWord + currentChar;
                currentToken.append(currentChar);
            } else {
                lastChar = String.valueOf(a_char_array[i - 1]); //set last character
                if (lastChar.matches("\\s") && currentChar.matches("\\s") && firstWord) { //WW
                    position = i - 1; // set position for next n-gram      
                    addWord = true;
                } else if (lastChar.matches("\\s") && !currentChar.matches("\\s") && firstWord) {//WC yes
                    position = i - 1;
                    //currentWord = currentWord + currentChar;
                    currentToken.append(currentChar);
                    firstWord = false;
                } else if (!lastChar.matches("\\s") && !currentChar.matches("\\s") && firstWord) { //CC yes
                    //currentWord = currentWord + currentChar;
                    currentToken.append(currentChar);
                } else if (!lastChar.matches("\\s") && currentChar.matches("\\s") && firstWord) { //CW yes
                    //currentWord = currentWord + currentChar;
                    currentToken.append(currentChar);
                    position = i - 1;
                    addWord = true;
                } else if (lastChar.matches("\\s") && currentChar.matches("\\s") && !firstWord) { //WW
                    position = i - 1;
                    addWord = true;
                } else if (lastChar.matches("\\s") && !currentChar.matches("\\s") && !firstWord) {//WC yes
                    //currentWord = currentWord + currentChar;
                    currentToken.append(currentChar);
                    // nb this never gets reached because if the previous char is W, then the word gets added.
                    firstWord = true;
                } else if (!lastChar.matches("\\s") && !currentChar.matches("\\s") && !firstWord) { //CC yes
                    //currentWord = currentWord + currentChar;
                    currentToken.append(currentChar);
                } else if (!lastChar.matches("\\s") && currentChar.matches("\\s") && !firstWord) { //CW
                    addWord = true;
                }

                if (addWord) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                    i = position;
                    firstWord = true;
                    addWord = false;
                }
            }
        }
        return tokens;
    }
    
    public ArrayList<String> generateTwoGrams(String docContents) {

        docContents = docContents + " ";
        char[] inputCharacters = docContents.toCharArray();
        boolean firstBreak = false;
        boolean firstChar = true;
        boolean lastCharWasSpace = false;
        int position = 0;
        StringBuilder currentToken = new StringBuilder();
        
        ArrayList<String> tokens = new ArrayList<String>();
        
        for (int i = 0; i < docContents.length()-1; i++) {
            String currentChar = String.valueOf(inputCharacters[i]);
            if (currentChar.matches("\\S")) { // not whitespace
                
                if (lastCharWasSpace && !firstChar) { //start of second word - set position 
                    position = i-1;
                }
                
                if (firstChar) { //new two-gram, first letter of first word
                    firstChar = false;   
                }
                currentToken.append(currentChar);
                lastCharWasSpace = false;
            } else { // not a character (is a space)
                if (!firstBreak && !lastCharWasSpace) { //end of first word
                    currentToken.append(currentChar); //add space
                    firstBreak = true; //set firstBreak
                    //position = i; //set position ready for next word
                } else if (firstBreak && !lastCharWasSpace) { // end of second word
                    tokens.add(currentToken.toString()); //add token to arraylist
                    firstChar = true; // set first char ready for next word
                    i = position; //move i back to middle of last pair
                    firstBreak = false; // do we need this?
                    currentToken.setLength(0); //reset current token
                } else if (lastCharWasSpace) { //more than one whitespace
                    currentToken.append(currentChar); //add space
                }
                lastCharWasSpace = true;
            }
        }
        return tokens;
    }
    
    public ArrayList generateThreeGrams(String docContents) {

        docContents = docContents + " ";
        char[] inputCharacters = docContents.toCharArray();
        boolean firstBreak = false;
        boolean secondBreak = false;
        boolean firstChar = true;
        boolean lastCharWasSpace = false;
        int position = 0;
        StringBuilder currentToken = new StringBuilder();
        ArrayList<String> tokens = new ArrayList();

        for (int i = 0; i < docContents.length() - 1; i++) {
            String currentChar = String.valueOf(inputCharacters[i]);    //get current character as string
            if (currentChar.matches("\\S")) {                           //not white space, then add to current string
                
                if (lastCharWasSpace && !firstChar) { //start of second word - set position 
                    position = i-1;
                }
                if (firstChar) {
                    firstChar = false;
                }
                currentToken.append(currentChar);
                lastCharWasSpace = false;
            } else {                                                    //not a character (is a space)

                if (!firstBreak && !secondBreak && !lastCharWasSpace) {                 //end of first word 
                    currentToken.append(currentChar);;                          //add space
                    firstBreak = true;                                  //set firstBreak, i.e. first word finished
                    //position = i;                                       //set position ready for next word
                } else if (firstBreak && !secondBreak && !lastCharWasSpace) { //end of second word 
                    currentToken.append(currentChar);                       //add space 
                    secondBreak = true;                                  //set firstBreak, i.e. first word finished
                                                                        //no need to set position
                } else if (firstBreak && secondBreak &&!lastCharWasSpace) {           // end of third word
                    tokens.add(currentToken.toString());                                //add token to arraylist
                    firstChar = true;                                   // set first char ready for next word
                    i = position;                                       //move i back to middle of last pair
                    firstBreak = false;                                 // do we need this?
                    secondBreak = false;
                    currentToken.setLength(0);                                          //reset current token
                } else if (lastCharWasSpace) {                          //more than one whitespace
                    currentToken.append(currentChar);                      //add space
                }
                lastCharWasSpace = true;
            }
        }
        return tokens;

    }
    
    public ArrayList generateFourGrams(String docContents) {

        docContents = docContents + " ";
        char[] inputCharacters = docContents.toCharArray();
        boolean firstBreak = false;
        boolean secondBreak = false;
        boolean thirdBreak = false;
        boolean firstChar = true;
        boolean lastCharWasSpace = false;
        int position = 0;
        StringBuilder currentToken = new StringBuilder();
        ArrayList<String> tokens = new ArrayList();

        for (int i = 0; i < docContents.length() - 1; i++) {    //COME BACK TO THIS -- I THINK THIS NEEDS CHANGING - MAYBE USE 
                                                                //SPLIT METHOD TO GET THE NUMBER OF WORDS 
            String currentChar = String.valueOf(inputCharacters[i]);    //get current character as string
            if (currentChar.matches("\\S")) {                           //not white space, then add to current string
                
                if (lastCharWasSpace && !firstChar) {                   //start of second word - set position 
                    position = i-1;
                }
                if (firstChar) {
                    firstChar = false;
                }
                currentToken.append(currentChar);
                lastCharWasSpace = false;
            } else {                                                        //not a character (is a space)

                if (!firstBreak && !secondBreak && !lastCharWasSpace) {     //end of first word 
                    currentToken.append(currentChar);                             //add space
                    firstBreak = true;                                      //set firstBreak, i.e. first word finished
                    //position = i;                                         //set position ready for next word
                } else if (firstBreak && !secondBreak && !lastCharWasSpace) { //end of second word 
                    currentToken.append(currentChar);                         //add space 
                    secondBreak = true;                                     //set firstBreak, i.e. first word finished
                                                                            //no need to set position
                } else if (firstBreak && secondBreak &&!thirdBreak && !lastCharWasSpace) { //end of third word
                    currentToken.append(currentChar);
                    thirdBreak = true;
                    
                } else if (firstBreak && secondBreak && thirdBreak &&!lastCharWasSpace) { //end of fourth word
                    tokens.add(currentToken.toString());                                       //add token to arraylist
                    firstChar = true;                                       //set first char ready for next word
                    i = position;                                           //move i back to middle of last pair
                    firstBreak = false;                                     //do I need this?
                    secondBreak = false;
                    thirdBreak = false;
                    currentToken.setLength(0);                                             //reset token
                } else if (lastCharWasSpace) {                              //more than one whitespace
                    currentToken.append(currentChar);                             //add space
                }
                lastCharWasSpace = true;
            }
        }
        return tokens;

    }
}
    

