package plagiarism;

import com.wcohen.ss.Levenstein;

public class LevTest {
    public static void main (String[] args) {
        String s1 = "hell o";
        String s2 = "hel lo";
        
        Levenstein lev = new Levenstein();
        Double x = lev.score(s2, s1);
        System.out.println(x);
        
    }
}
