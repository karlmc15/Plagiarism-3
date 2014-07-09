package plagiarism;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
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
    private final JLabel label1, label2, label3;
    private final JLabel spacer;
    private final JTextPane resultsPane;
    private final JRadioButtonMenuItem radio1, radio2, radio3, radio4, radio5;
    private final ButtonGroup buttongroup;
    OneGrams onegrams;
    TwoGrams twograms;
    ThreeGrams threegrams;
    FourGrams fourgrams;
    TwoGramsBasic twogramsbasic;
    boolean generated1, generated2, generated3, generated4, generated5 = false;

    private final JButton getMatches;

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

        this.label1 = new JLabel("Enter minimum tFiDF:");
        panel.add(label1);

        this.textField1 = new JTextField(20);
        panel.add(textField1, "wrap");

        this.label2 = new JLabel("Enter maximum number of Postings");
        panel.add(label2);

        this.textField2 = new JTextField(20);
        panel.add(textField2, "wrap");

        this.spacer = new JLabel(" ");
        panel.add(spacer, "wrap");

        this.radio1 = new JRadioButtonMenuItem("One-grams (Simple tokens using Java Tokenizer");
        this.radio2 = new JRadioButtonMenuItem("Two-grams (1-MANY-CHARACTERS, 1-MANY SPACES, 1-MANY-CHARACTERS");
        this.radio3 = new JRadioButtonMenuItem("Three-grams");
        this.radio4 = new JRadioButtonMenuItem("Basic Two Grams");
        this.radio5 = new JRadioButtonMenuItem("Four-Grams");
        panel.add(radio1, "wrap");
        panel.add(radio2, "wrap");
        panel.add(radio3, "wrap");
        panel.add(radio4, "wrap");
        panel.add(radio5, "wrap");

        this.buttongroup = new ButtonGroup();
        buttongroup.add(radio1);
        buttongroup.add(radio2);
        buttongroup.add(radio3);
        buttongroup.add(radio4);
        buttongroup.add(radio5);

        this.getMatches = new JButton("Get Matches");
        this.getMatches.addActionListener(this);
        panel.add(getMatches, "wrap");

        this.label3 = new JLabel("Matches");
        panel.add(label3, "wrap");

        this.resultsPane = new JTextPane();
        panel.add(resultsPane, "wrap");

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == getMatches) {
            if (radio1.isSelected()) {
                if (!generated1) {
                    onegrams = new OneGrams();
                    onegrams.generate();
                    onegrams.getMatches(Double.parseDouble(textField1.getText()), Integer.parseInt(textField2.getText()));
                    generated1 = true;
                } else {
                    onegrams.getMatches(Double.parseDouble(textField1.getText()), Integer.parseInt(textField2.getText()));
                }
            } else if (radio2.isSelected()) {
                if (!generated2) {
                    twograms = new TwoGrams();
                    twograms.generate();
                    twograms.getMatches(Double.parseDouble(textField1.getText()), Integer.parseInt(textField2.getText()));
                    generated2 = true;
                } else {
                    twograms.getMatches(Double.parseDouble(textField1.getText()), Integer.parseInt(textField2.getText()));
                }
            } else if (radio3.isSelected()) {
                if (!generated3) {
                    threegrams = new ThreeGrams();
                    threegrams.generate();
                    threegrams.getMatches(Double.parseDouble(textField1.getText()), Integer.parseInt(textField2.getText()));
                    generated3 = true;
                } else {
                    threegrams.getMatches(Double.parseDouble(textField1.getText()), Integer.parseInt(textField2.getText()));

                }
            } else if (radio4.isSelected()) {
                if (!generated4) {
                    twogramsbasic = new TwoGramsBasic();
                    twogramsbasic.generate();
                    twogramsbasic.getMatches(Double.parseDouble(textField1.getText()), Integer.parseInt(textField2.getText()));
                    generated4 = true;
                } else {
                    twogramsbasic.getMatches(Double.parseDouble(textField1.getText()), Integer.parseInt(textField2.getText()));
                }
            } else if (radio5.isSelected()) {
                if (!generated5) {
                    fourgrams = new FourGrams();
                    fourgrams.generate();
                    fourgrams.getMatches(Double.parseDouble(textField1.getText()), Integer.parseInt(textField2.getText()));
                    generated3 = true;
                } else {
                    fourgrams.getMatches(Double.parseDouble(textField1.getText()), Integer.parseInt(textField2.getText()));
            }
        }
    }}
}
