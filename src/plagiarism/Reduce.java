
package plagiarism;

import java.util.ArrayList;
import com.wcohen.ss.Levenstein;

public class Reduce {

    public static void main(String[] args) {

        ArrayList<String> tokens = new ArrayList<String>();
        tokens.add("hello");
        tokens.add("dave");

        Levenstein lev = new Levenstein();
        
        for (int i = 0; i < tokens.size(); i++) {
            for (int j = i+1; j < tokens.size(); j++) {
                Double distance = lev.score(tokens.get(i), tokens.get(j));
                if (distance == -1){
                    
                }
            }
        }
    }
}
