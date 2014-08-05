package plagiarism;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import net.miginfocom.swing.MigLayout;
import plagiarism.Plagiarism;

public class View extends JFrame implements ActionListener {

    private final JButton printTally;
    private final JTextField textField1;
    private final JTextField textField2;
    private final JLabel label1, label2, label3;
    private final JLabel spacer;
    private final JTextArea resultsPane;
    private final JRadioButtonMenuItem radio1, radio2, radio3, radio4, radio5, radio6, radio7;
    private final ButtonGroup buttongroup;
    private final JRadioButtonMenuItem approx;
    private final ButtonGroup buttongroup2;
    boolean generated1, generated2, generated3, generated4, generated5, generated6, generated7 = false;
    private final JScrollPane scrollpane;
    public Double minTFIDF;
    public Integer maxPostings;
    private final JButton getMatches;
    public Plagiarism plagiarism = new Plagiarism();

    public View() {
        super();
        setTitle("Dave's Plagiarism Detector");
        setSize(950, 600);
        setDefaultCloseOperation(View.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new MigLayout());

        this.add(panel);

        this.label1 = new JLabel("Enter minimum tFiDF:");
        panel.add(label1);

        this.textField1 = new JTextField(20);
        panel.add(textField1, "wrap");

        this.label2 = new JLabel("Enter maximum number of Postings:");
        panel.add(label2);

        this.textField2 = new JTextField(20);
        panel.add(textField2, "wrap");

        this.spacer = new JLabel(" ");
        panel.add(spacer, "wrap");

        this.radio1 = new JRadioButtonMenuItem("One-grams (Simple tokens using Java Tokenizer)");
        this.radio2 = new JRadioButtonMenuItem("Two-grams (1-many chars, 1-many spaces, 1-many chars)");
        this.radio3 = new JRadioButtonMenuItem("Three-grams");
        this.radio4 = new JRadioButtonMenuItem("Four-grams");
        this.radio5 = new JRadioButtonMenuItem("Basic Two-grams");
        this.radio6 = new JRadioButtonMenuItem("WhiteSpace");
        this.radio7 = new JRadioButtonMenuItem("Java Terminators");
        panel.add(radio1, "wrap");
        panel.add(radio2, "wrap");
        panel.add(radio3, "wrap");
        panel.add(radio4, "wrap");
        panel.add(radio5, "wrap");
        panel.add(radio6, "wrap");
        panel.add(radio7, "wrap");

        this.approx = new JRadioButtonMenuItem("Include Approximate Values");
        panel.add(approx, "wrap");
        approx.addActionListener(this);
        this.buttongroup2 = new ButtonGroup();
        buttongroup2.add(approx);

        //add action listeners to enable "get matches" button
        radio1.addActionListener(this);
        radio2.addActionListener(this);
        radio3.addActionListener(this);
        radio4.addActionListener(this);
        radio5.addActionListener(this);
        radio6.addActionListener(this);
        radio7.addActionListener(this);

        this.buttongroup = new ButtonGroup();
        buttongroup.add(radio1);
        buttongroup.add(radio2);
        buttongroup.add(radio3);
        buttongroup.add(radio4);
        buttongroup.add(radio5);
        buttongroup.add(radio6);
        buttongroup.add(radio7);

        this.getMatches = new JButton("Get Matches");
        getMatches.addActionListener(this);
        getMatches.setEnabled(false);
        panel.add(getMatches, "wrap");

        this.label3 = new JLabel("Matches");
        panel.add(label3, "wrap");

        this.resultsPane = new JTextArea(10, 80);
        resultsPane.setMinimumSize(new Dimension(900, 250));
        resultsPane.setMaximumSize(new Dimension(900, 250));
        this.scrollpane = new JScrollPane(resultsPane);

        panel.add(scrollpane, "span");

        this.printTally = new JButton("Print tally");
        this.printTally.addActionListener(this);
        panel.add(printTally, "wrap");

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Boolean includeApproximateMatches = false;

        if (e.getSource() == getMatches) {
            minTFIDF = Double.parseDouble(textField1.getText());
            maxPostings = Integer.parseInt(textField2.getText());
            getMatches.setEnabled(true);

            if (approx.isSelected()) {
                includeApproximateMatches = true;
            }

            if (radio1.isSelected()) {
                if (!generated1) {
                    Plagiarism.generateTokens(1, includeApproximateMatches);
                    generated1 = true;
                }
                resultsPane.setText(Plagiarism.getMatches(minTFIDF, maxPostings, Plagiarism.dictionaryMap1gram, Plagiarism.inverseDocFreqMap1gram));

            } else if (radio2.isSelected()) {
                if (!generated2) {
                    Plagiarism.generateTokens(2, includeApproximateMatches);
                    generated2 = true;
                }
                resultsPane.setText(Plagiarism.getMatches(minTFIDF, maxPostings, Plagiarism.dictionaryMap2gram, Plagiarism.inverseDocFreqMap2gram));

            } else if (radio3.isSelected()) {
                if (!generated3) {
                    Plagiarism.generateTokens(3, includeApproximateMatches);
                    generated3 = true;
                }
                resultsPane.setText(Plagiarism.getMatches(minTFIDF, maxPostings, Plagiarism.dictionaryMap3gram, Plagiarism.inverseDocFreqMap3gram));

            } else if (radio4.isSelected()) {
                if (!generated4) {
                    Plagiarism.generateTokens(4, includeApproximateMatches);
                    generated4 = true;
                }
                resultsPane.setText(Plagiarism.getMatches(minTFIDF, maxPostings, Plagiarism.dictionaryMap4gram, Plagiarism.inverseDocFreqMap4gram));

            } else if (radio5.isSelected()) {
                if (!generated5) {
                    Plagiarism.generateTokens(5, includeApproximateMatches);
                    generated5 = true;
                }
                resultsPane.setText(Plagiarism.getMatches(minTFIDF, maxPostings, Plagiarism.dictionaryMap2gramBasic, Plagiarism.inverseDocFreqMap2gramBasic));

            } else if (radio6.isSelected()) {
                if (!generated5) {
                    Plagiarism.generateTokens(6, includeApproximateMatches);
                    generated5 = true;
                }
                resultsPane.setText(Plagiarism.getMatches(minTFIDF, maxPostings, Plagiarism.dictionaryMapWhiteSpace, Plagiarism.inverseDocFreqMapWhiteSpace));

            } else if (radio7.isSelected()) {
                if (!generated7) {
                    Plagiarism.generateTokens(7, includeApproximateMatches);
                    generated7 = true;
                }
                resultsPane.setText(Plagiarism.getMatches(minTFIDF, maxPostings, Plagiarism.dictionaryMapJava, Plagiarism.inverseDocFreqMapJava));


            }

        } else if (e.getSource()
                == printTally) {
            //Plagiarism.aggregate(Plagiarism.dictionaryMap2gram, Plagiarism.inverseDocFreqMap2gram);
            if (radio1.isSelected()) {
                Plagiarism.aggregate(Plagiarism.dictionaryMap1gram, Plagiarism.inverseDocFreqMap1gram);
                Plagiarism.printTally(Plagiarism.tallyChart, Plagiarism.inverseDocFreqMap1gram);
            } else if (radio2.isSelected()) {
                Plagiarism.aggregate(Plagiarism.dictionaryMap2gram, Plagiarism.inverseDocFreqMap2gram);
                Plagiarism.printTally(Plagiarism.tallyChart, Plagiarism.inverseDocFreqMap2gram);
            } else if (radio3.isSelected()) {
                Plagiarism.aggregate(Plagiarism.dictionaryMap3gram, Plagiarism.inverseDocFreqMap3gram);
                Plagiarism.printTally(Plagiarism.tallyChart, Plagiarism.inverseDocFreqMap3gram);
            } else if (radio4.isSelected()) {
                Plagiarism.aggregate(Plagiarism.dictionaryMap4gram, Plagiarism.inverseDocFreqMap4gram);
                Plagiarism.printTally(Plagiarism.tallyChart, Plagiarism.inverseDocFreqMap4gram);
            } else if (radio5.isSelected()) {
                Plagiarism.aggregate(Plagiarism.dictionaryMap2gramBasic, Plagiarism.inverseDocFreqMap2gramBasic);
                Plagiarism.printTally(Plagiarism.tallyChart, Plagiarism.inverseDocFreqMap2gramBasic);
            } else if (radio6.isSelected()) {
                Plagiarism.aggregate(Plagiarism.dictionaryMapWhiteSpace, Plagiarism.inverseDocFreqMapWhiteSpace);
                Plagiarism.printTally(Plagiarism.tallyChart, Plagiarism.inverseDocFreqMap2gram);
            } else if (radio7.isSelected()) {
                Plagiarism.aggregate(Plagiarism.dictionaryMapJava, Plagiarism.inverseDocFreqMapJava);
                Plagiarism.printTally(Plagiarism.tallyChart, Plagiarism.inverseDocFreqMapJava);
            } 

        } else if (e.getSource()
                == radio1 || e.getSource() == radio2 || e.getSource() == radio3 || e.getSource() == radio4
                || e.getSource() == radio5 || e.getSource() == radio6 || e.getSource() == radio7) {
            getMatches.setEnabled(true);
        }
    }
}
