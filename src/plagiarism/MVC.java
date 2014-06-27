package plagiarism;

public class MVC {
    
    // create model, view and controller
    // they are created once here, and passed to 
    // the parts that need them so that
    // there is only one copy of each
        
    public static void main (String[] args) {
        
        //Plagiarism model = new Plagiarism();
        View view = new View();
        //controller
        
        view.setVisible(true);
        
    }
}
