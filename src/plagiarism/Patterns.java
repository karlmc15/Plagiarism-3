/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plagiarism;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 *
 * @author c1343067
 */
public class Patterns {

    public static void main(String args[]) {

        String pattern = "[a-z]+\\s[a-z]+\\s";
        String s = "this is another test string";

        Pattern splitter = Pattern.compile(pattern);
        String[] results = splitter.split(s);

        for (String pair : results) {
            System.out.println(pair);
        }

        String input = "one two three four five six seven";
        String[] pairs = input.split("(?<!\\G\\w+)\\s");
        System.out.println(Arrays.toString(pairs));

    }
}
