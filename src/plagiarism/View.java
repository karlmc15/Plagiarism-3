package plagiarism;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import net.miginfocom.swing.MigLayout;

public class View extends JFrame implements ActionListener {

    private final JButton button1gram;
    private final JButton button2gram;
    private final JButton button3gram;
    private final JButton button2gramBasic;
    private final JTextField textField1;
    private final JTextField textField2;
    private final JLabel label1;
    private final JLabel label2;
    private final JLabel label3;
    private final JLabel spacer;
    private final JTextPane resultsPane;

    public View() {
        super();
        setTitle("Dave's Plagiarism Detector");
        setSize(600, 400);
        setDefaultCloseOperation(View.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new MigLayout());

        this.add(panel);

        this.button1gram = new JButton("One-grams");
        this.button1gram.addActionListener(this);
        panel.add(button1gram, "wrap");

        this.button2gram = new JButton("Two-grams");
        this.button2gram.addActionListener(this);
        panel.add(button2gram, "wrap");

        this.button2gramBasic = new JButton("Simple Two-grams");
        this.button2gramBasic.addActionListener(this);
        panel.add(button2gramBasic, "wrap");

        this.button3gram = new JButton("Three-grams");
        this.button3gram.addActionListener(this);
        panel.add(button3gram, "wrap");
        
        this.label1 = new JLabel("Enter maximum iDF:");
        panel.add(label1);
        
        this.textField1 = new JTextField(20);
        panel.add(textField1, "wrap");
        
        this.label2 = new JLabel("Enter maximum number of Postings");
        panel.add(label2);
        
        this.textField2 = new JTextField(20);
        panel.add(textField2, "wrap");
        
        this.spacer = new JLabel(" ");
        panel.add(spacer, "wrap");
        
        this.label3 = new JLabel("Matches");
        panel.add(label3, "wrap");
        
        this.resultsPane = new JTextPane();
        panel.add(resultsPane);
        
        

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1gram) {
            //System.out.println("button 1 pressed");
            Plagiarism plagiarism = new Plagiarism(1);
        } else if (e.getSource() == button2gram) {
            //System.out.println("button 2 pressed");
            Plagiarism plagiarism = new Plagiarism(2);
        } else if (e.getSource() == button2gramBasic) {
            //System.out.println("button 2 pressed");
            Plagiarism plagiarism = new Plagiarism(3);
        } else if (e.getSource() == button3gram) {
            //System.out.println("button 2 pressed");
            Plagiarism plagiarism = new Plagiarism(4);
        }
    }
}
