package plagiarism;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import net.miginfocom.swing.MigLayout;

public class View extends JFrame implements ActionListener {

    private final JButton printTally, compare2Docs, runTest, runApplication, getMatches;
    private final JTextField textField1, textField2, textField3, textField4, textField5, textField6;
    private final JTextField docA, docB;
    private final JLabel labelSelectTokenization, label2, label3, label4, label5, label6, label7;
    private final JLabel labelDocA, labelDocB;
    private final JLabel spacer;
    private final JTextArea resultsPaneLeft, resultsPaneCentre, resultsPaneRight;
    private final JRadioButtonMenuItem radio1, radio2, radio3, radio4, radio5, radio6, radio7;
    private final ButtonGroup buttongroup;
    private final JRadioButtonMenuItem approx;
    private final ButtonGroup buttongroup2;
    boolean generated1, generated2, generated3, generated4, generated5, generated6, generated7 = false;
    private final JScrollPane scrollpaneLeft, scrollpaneCentre, scrollpaneRight;
    public Double minTFIDF;
    public Integer maxPostings, minMatches, minRecall, minPrecision;
    public Double minIDF;
    public Plagiarism plagiarism = new Plagiarism();

    String[] tokenizationType = {"1-Grams", "Bi-Grams", "3-Grams", "4-Grams", "Basic 2-Grams", "White Space", "Java Syntax"};
    public JComboBox comboBoxSelectTokenization = new JComboBox(tokenizationType);

    public View() {
        super();
        setTitle("Dave's Plagiarism Detector");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //setVisible(true);
        //setSize(950, 600);
        setDefaultCloseOperation(View.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new MigLayout());

        this.add(panel);
        //this.pack();

        this.labelSelectTokenization = new JLabel("Select Tokenization: ");
        panel.add(labelSelectTokenization);

        panel.add(comboBoxSelectTokenization);
        comboBoxSelectTokenization.addActionListener(this);

        this.runApplication = new JButton("Run Application");
        this.runApplication.addActionListener(this);
        //runApplication.setEnabled(false);
        panel.add(runApplication, "wrap");

        this.textField1 = new JTextField(20);
        panel.add(textField1, "wrap");

        this.label2 = new JLabel("Enter maximum number of Postings:");
        panel.add(label2);

        this.textField2 = new JTextField(20);
        panel.add(textField2, "wrap");

        this.spacer = new JLabel(" ");
        panel.add(spacer, "wrap");

        this.radio1 = new JRadioButtonMenuItem("1-grams");
        this.radio2 = new JRadioButtonMenuItem("Bi-grams");
        this.radio3 = new JRadioButtonMenuItem("3-grams");
        this.radio4 = new JRadioButtonMenuItem("4-grams");
        this.radio5 = new JRadioButtonMenuItem("Basic 2-grams");
        this.radio6 = new JRadioButtonMenuItem("WhiteSpace");
        this.radio7 = new JRadioButtonMenuItem("Java Terminators");
        panel.add(radio1, "split 10");
        panel.add(radio2);
        panel.add(radio3);
        panel.add(radio4, "split 3");
        panel.add(radio5);
        panel.add(radio6);
        panel.add(radio7);

        this.approx = new JRadioButtonMenuItem("Include Approximate Values");
        panel.add(approx, "wrap");
        approx.addActionListener(this);
        this.buttongroup2 = new ButtonGroup();
        buttongroup2.add(approx);

        //add action listeners to enable "get matches" button
        radio1.addActionListener(this);

        this.buttongroup = new ButtonGroup();
        buttongroup.add(radio1);

        this.getMatches = new JButton("Display Tokens based on TD-IDF");
        getMatches.addActionListener(this);
        getMatches.setEnabled(false);
        panel.add(getMatches, "wrap");

        this.label3 = new JLabel("Matches");
        panel.add(label3, "wrap");

        this.resultsPaneLeft = new JTextArea(20, 80);
        resultsPaneLeft.setMinimumSize(new Dimension(900, 400));
        resultsPaneLeft.setMaximumSize(new Dimension(900, 400));
        this.scrollpaneLeft = new JScrollPane(resultsPaneLeft);

        this.resultsPaneCentre = new JTextArea(20, 80);
        resultsPaneCentre.setMinimumSize(new Dimension(900, 400));
        resultsPaneCentre.setMaximumSize(new Dimension(900, 400));
        this.scrollpaneCentre = new JScrollPane(resultsPaneCentre);

        this.resultsPaneRight = new JTextArea(20, 80);
        resultsPaneRight.setMinimumSize(new Dimension(900, 400));
        resultsPaneRight.setMaximumSize(new Dimension(900, 400));
        this.scrollpaneRight = new JScrollPane(resultsPaneRight);

        panel.add(scrollpaneLeft, "span 2");
        panel.add(scrollpaneCentre, "span 2, gapleft 30");
        panel.add(scrollpaneRight, "wrap");

        this.label4 = new JLabel("Enter minimum number of matches");
        panel.add(label4);

        this.textField3 = new JTextField(20);
        panel.add(textField3, "wrap");

        this.label5 = new JLabel("Enter minimum IDF (between 0 and 5");
        panel.add(label5);

        this.textField4 = new JTextField(20);
        panel.add(textField4, "wrap");

        this.printTally = new JButton("Print tally");
        this.printTally.addActionListener(this);
        panel.add(printTally, "wrap");

        this.label6 = new JLabel("Enter minimum precision");
        panel.add(label6);

        this.textField5 = new JTextField(20);
        panel.add(textField5, "wrap");

        this.label7 = new JLabel("Enter minimum recall");
        panel.add(label7);

        this.textField6 = new JTextField(20);
        panel.add(textField6, "wrap");

        this.runTest = new JButton("Run Test");
        this.runTest.addActionListener(this);
        panel.add(runTest, "wrap");

        this.labelDocA = new JLabel("Enter first document name:");
        panel.add(labelDocA);

        this.docA = new JTextField(20);
        panel.add(docA, "wrap");

        this.labelDocB = new JLabel("Enter second document name:");
        panel.add(labelDocB);

        this.docB = new JTextField(20);
        panel.add(docB, "wrap");

        this.compare2Docs = new JButton("Compare 2 documents");
        this.compare2Docs.addActionListener(this);
        panel.add(compare2Docs, "wrap");

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String tokenizationType = (String)comboBoxSelectTokenization.getSelectedItem();

        Boolean includeApproximateMatches = false;

        if (e.getSource()
                == runApplication) {
            resultsPaneLeft.setText(tokenizationType);
            Plagiarism.generateInvertedIndex(tokenizationType, includeApproximateMatches);
            Plagiarism.aggregateWeighted(Plagiarism.dictionaryMap1gram, Plagiarism.inverseDocFreqMap1gram, 3);
            resultsPaneLeft.setText(Plagiarism.printTally(Plagiarism.matchingTokensBetweenDocPairs, Plagiarism.inverseDocFreqMap1gram, 0));
            

        } else if (e.getSource() == getMatches) {
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
                resultsPaneLeft.setText(Plagiarism.getMatches(minTFIDF, maxPostings, Plagiarism.dictionaryMap1gram, Plagiarism.inverseDocFreqMap1gram));

            } else if (radio2.isSelected()) {
                if (!generated2) {
                    Plagiarism.generateTokens(2, includeApproximateMatches);
                    generated2 = true;
                }
                resultsPaneLeft.setText(Plagiarism.getMatches(minTFIDF, maxPostings, Plagiarism.dictionaryMap2gram, Plagiarism.inverseDocFreqMap2gram));

            } else if (radio3.isSelected()) {
                if (!generated3) {
                    Plagiarism.generateTokens(3, includeApproximateMatches);
                    generated3 = true;
                }
                resultsPaneLeft.setText(Plagiarism.getMatches(minTFIDF, maxPostings, Plagiarism.dictionaryMap3gram, Plagiarism.inverseDocFreqMap3gram));

            } else if (radio4.isSelected()) {
                if (!generated4) {
                    Plagiarism.generateTokens(4, includeApproximateMatches);
                    generated4 = true;
                }
                resultsPaneLeft.setText(Plagiarism.getMatches(minTFIDF, maxPostings, Plagiarism.dictionaryMap4gram, Plagiarism.inverseDocFreqMap4gram));

            } else if (radio5.isSelected()) {
                if (!generated5) {
                    Plagiarism.generateTokens(5, includeApproximateMatches);
                    generated5 = true;
                }
                resultsPaneLeft.setText(Plagiarism.getMatches(minTFIDF, maxPostings, Plagiarism.dictionaryMap2gramBasic, Plagiarism.inverseDocFreqMap2gramBasic));

            } else if (radio6.isSelected()) {
                if (!generated5) {
                    Plagiarism.generateTokens(6, includeApproximateMatches);
                    generated5 = true;
                }
                resultsPaneLeft.setText(Plagiarism.getMatches(minTFIDF, maxPostings, Plagiarism.dictionaryMapWhiteSpace, Plagiarism.inverseDocFreqMapWhiteSpace));

            } else if (radio7.isSelected()) {
                if (!generated7) {
                    Plagiarism.generateTokens(7, includeApproximateMatches);
                    generated7 = true;
                }
                resultsPaneLeft.setText(Plagiarism.getMatches(minTFIDF, maxPostings, Plagiarism.dictionaryMapJava, Plagiarism.inverseDocFreqMapJava));

            }

        } else if (e.getSource()
                == printTally) {
            minMatches = Integer.parseInt(textField3.getText());
            minIDF = Double.parseDouble(textField4.getText());
            
            

            if (radio1.isSelected()) {
                Plagiarism.aggregateWeighted(Plagiarism.dictionaryMap1gram, Plagiarism.inverseDocFreqMap1gram, minIDF);
                Plagiarism.printTally(Plagiarism.matchingTokensBetweenDocPairs, Plagiarism.inverseDocFreqMap1gram, minMatches);

            } else if (radio2.isSelected()) {
                Plagiarism.aggregateWeighted(Plagiarism.dictionaryMap2gram, Plagiarism.inverseDocFreqMap2gram, minIDF);
                Plagiarism.printTally(Plagiarism.matchingTokensBetweenDocPairs, Plagiarism.inverseDocFreqMap2gram, minMatches);
            } else if (radio3.isSelected()) {
                Plagiarism.aggregateWeighted(Plagiarism.dictionaryMap3gram, Plagiarism.inverseDocFreqMap3gram, minIDF);
                Plagiarism.printTally(Plagiarism.matchingTokensBetweenDocPairs, Plagiarism.inverseDocFreqMap3gram, minMatches);
            } else if (radio4.isSelected()) {
                Plagiarism.aggregateWeighted(Plagiarism.dictionaryMap4gram, Plagiarism.inverseDocFreqMap4gram, minIDF);
                Plagiarism.printTally(Plagiarism.matchingTokensBetweenDocPairs, Plagiarism.inverseDocFreqMap4gram, minMatches);
            } else if (radio5.isSelected()) {
                Plagiarism.aggregateWeighted(Plagiarism.dictionaryMap2gramBasic, Plagiarism.inverseDocFreqMap2gramBasic, minIDF);
                Plagiarism.printTally(Plagiarism.matchingTokensBetweenDocPairs, Plagiarism.inverseDocFreqMap2gramBasic, minMatches);
            } else if (radio6.isSelected()) {
                Plagiarism.aggregateWeighted(Plagiarism.dictionaryMapWhiteSpace, Plagiarism.inverseDocFreqMapWhiteSpace, minIDF);
                Plagiarism.printTally(Plagiarism.matchingTokensBetweenDocPairs, Plagiarism.inverseDocFreqMap2gram, minMatches);

            } else if (radio7.isSelected()) {
                Plagiarism.aggregateWeighted(Plagiarism.dictionaryMapJava, Plagiarism.inverseDocFreqMapJava, minIDF);
                Plagiarism.printTally(Plagiarism.matchingTokensBetweenDocPairs, Plagiarism.inverseDocFreqMapJava, minMatches);
            }

        } else if (e.getSource() == runTest) {
            minPrecision = Integer.parseInt(textField5.getText());
            minRecall = Integer.parseInt(textField6.getText());
            if (radio1.isSelected()) {
                Plagiarism.testPandR(Plagiarism.dictionaryMap1gram, Plagiarism.inverseDocFreqMap1gram, minPrecision, minRecall);
            } else if (radio2.isSelected()) {
                Plagiarism.testPandR(Plagiarism.dictionaryMap2gram, Plagiarism.inverseDocFreqMap2gram, minPrecision, minRecall);
            } else if (radio3.isSelected()) {
                Plagiarism.testPandR(Plagiarism.dictionaryMap3gram, Plagiarism.inverseDocFreqMap3gram, minPrecision, minRecall);
            } else if (radio4.isSelected()) {
                Plagiarism.testPandR(Plagiarism.dictionaryMap4gram, Plagiarism.inverseDocFreqMap4gram, minPrecision, minRecall);
            } else if (radio5.isSelected()) {
                Plagiarism.testPandR(Plagiarism.dictionaryMap2gramBasic, Plagiarism.inverseDocFreqMap2gramBasic, minPrecision, minRecall);
            } else if (radio6.isSelected()) {
                Plagiarism.testPandR(Plagiarism.dictionaryMapWhiteSpace, Plagiarism.inverseDocFreqMapWhiteSpace, minPrecision, minRecall);
            } else if (radio7.isSelected()) {
                Plagiarism.testPandR(Plagiarism.dictionaryMapJava, Plagiarism.inverseDocFreqMapJava, minPrecision, minRecall);
            }
        } else if (e.getSource()
                == radio1 || e.getSource() == radio2 || e.getSource() == radio3 || e.getSource() == radio4
                || e.getSource() == radio5 || e.getSource() == radio6 || e.getSource() == radio7) {
            getMatches.setEnabled(true);
        } else if (e.getSource() == compare2Docs) {
            String docAname = docA.getText();
            String docBname = docB.getText();
            resultsPaneLeft.setText(Plagiarism.getDocContents(docAname));
            resultsPaneRight.setText(Plagiarism.getDocContents(docBname));
            if (radio1.isSelected()) {
                Plagiarism.getMatchesBetween2docs(Plagiarism.dictionaryMap1gram, Plagiarism.inverseDocFreqMap1gram, docAname, docBname);
            } else if (radio2.isSelected()) {
                Plagiarism.getMatchesBetween2docs(Plagiarism.dictionaryMap2gram, Plagiarism.inverseDocFreqMap2gram, docAname, docBname);
            } else if (radio3.isSelected()) {
                Plagiarism.getMatchesBetween2docs(Plagiarism.dictionaryMap3gram, Plagiarism.inverseDocFreqMap3gram, docAname, docBname);
            } else if (radio4.isSelected()) {
                Plagiarism.getMatchesBetween2docs(Plagiarism.dictionaryMap4gram, Plagiarism.inverseDocFreqMap4gram, docAname, docBname);
            } else if (radio5.isSelected()) {
                Plagiarism.getMatchesBetween2docs(Plagiarism.dictionaryMap2gramBasic, Plagiarism.inverseDocFreqMap2gramBasic, docAname, docBname);
            } else if (radio6.isSelected()) {
                Plagiarism.getMatchesBetween2docs(Plagiarism.dictionaryMapWhiteSpace, Plagiarism.inverseDocFreqMapWhiteSpace, docAname, docBname);
            } else if (radio7.isSelected()) {
                Plagiarism.getMatchesBetween2docs(Plagiarism.dictionaryMapJava, Plagiarism.inverseDocFreqMapJava, docAname, docBname);
            }

            Plagiarism.highlightTokens(resultsPaneLeft, Plagiarism.matchingTokens);
            Plagiarism.highlightTokens(resultsPaneRight, Plagiarism.matchingTokens);

        }
    }
}
