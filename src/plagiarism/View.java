package plagiarism;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import net.miginfocom.swing.MigLayout;

public class View extends JFrame implements ActionListener {

    private final JButton chooseDirectory, runApplication, compare2Docs, getKnownPlagiarism, runOnce, runTest, displayTokens;
    private final JTextField textMaxPostings, textMinPrecision, textMinRecall, textMinMatchingTokens;
    private final JTextField docA, docB;
    private final JLabel labelSelectTokenization, labelMaxPostings, labelResultsLeft, labelResultsCentre, labelResultsRight;
    private final JLabel labelMinPrecision, labelMinRecall, labelMinMatchingTokens, labelRunTest, labelCompare2Docs;
    private final JLabel labelDocA, labelDocB, labelDisplayTokens;
    private final JTextArea resultsPaneLeft, resultsPaneCentre, resultsPaneRight;
    boolean generated1, generated2, generated3, generated4, generated5, generated6, generated7 = false;
    private final JScrollPane scrollpaneLeft, scrollpaneCentre, scrollpaneRight;
    public Integer maxPostings, minMatchingTokens, minRecall, minPrecision;
    ;;
    public Double minIDF, minTFIDF;
    private final String defaultIDF = "0";
    private final String defaultTFIDF = "0.0";
    private final String defaultMinMatchingTokens = "0";

    public final JCheckBox checkBoxDefaultParameters;
    public final JTextField textParameterTFIDF;
    public final JTextField textParameterIDF;
    public final JLabel labelParameterTFIDF;
    public final JLabel labelParameterIDF;

    public final JRadioButton radioSingleTest;
    public final JRadioButton radioMultipleTest;
    public final ButtonGroup buttonGroupTests;

    public final JFileChooser directoryChooser, knownPlagiarismChooser;

    public Plagiarism plagiarism = new Plagiarism();

    public final JPanel panelA, panelA1, panelB, panelC, panelD, panelE;

    public File directory;

    String[] tokenizationTypes = {"1-Grams", "Bi-Grams", "3-Grams", "4-Grams", "Basic 2-Grams", "White Space", "Java Syntax"};
    public JComboBox comboBoxSelectTokenization = new JComboBox(tokenizationTypes);

    public static ConcurrentHashMap<String, ArrayList<String[]>> dictionaryMap = new ConcurrentHashMap<String, ArrayList<String[]>>();
    public static ConcurrentHashMap<String, Double[]> inverseDocFreqMap = new ConcurrentHashMap<String, Double[]>();
    public static String tokenizationType;

    public View() {
        super();
        setTitle("Dave's Plagiarism Detector");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //setVisible(true);
        //setSize(950, 600);
        setDefaultCloseOperation(View.EXIT_ON_CLOSE);

        JPanel panelMain = new JPanel(new MigLayout());

        this.add(panelMain);
        //this.pack();
        panelA = new JPanel(new MigLayout());
        panelA1 = new JPanel(new MigLayout());
        panelB = new JPanel(new MigLayout());
        panelC = new JPanel(new MigLayout());
        panelD = new JPanel(new MigLayout());
        panelE = new JPanel(new MigLayout());

        panelMain.add(panelA, "span");
        //panelMain.add(panelA1, "span");
        panelMain.add(panelB, "span 0 1");
        panelMain.add(panelC, "span 3");
        panelMain.add(panelD, "span 3, wrap");
        panelMain.add(panelE, "span");

        //panelA : MAIN APPLICATION
        labelSelectTokenization = new JLabel("Select Tokenization Method:");
        panelA.add(labelSelectTokenization);

        panelA.add(comboBoxSelectTokenization);
        comboBoxSelectTokenization.setSelectedItem("Bi-Grams");
        comboBoxSelectTokenization.addActionListener(this);

        checkBoxDefaultParameters = new JCheckBox("Use Default Parameters");
        checkBoxDefaultParameters.addActionListener(this);
        checkBoxDefaultParameters.setSelected(true);
        panelA.add(checkBoxDefaultParameters);

        this.labelParameterTFIDF = new JLabel("Min TFIDF:");
        panelA.add(labelParameterTFIDF);
        this.textParameterTFIDF = new JTextField(defaultTFIDF, 5);
        textParameterTFIDF.setEnabled(false);
        panelA.add(textParameterTFIDF);

        labelParameterIDF = new JLabel("Min IDF:");
        panelA.add(labelParameterIDF);
        textParameterIDF = new JTextField(defaultIDF, 5);
        textParameterIDF.setEnabled(false);
        panelA.add(textParameterIDF);

        labelMinMatchingTokens = new JLabel("Min Matches:");
        panelA.add(labelMinMatchingTokens);
        textMinMatchingTokens = new JTextField(defaultMinMatchingTokens, 5);
        textMinMatchingTokens.setEnabled(false);
        panelA.add(textMinMatchingTokens);

        chooseDirectory = new JButton("Choose Directory");
        chooseDirectory.addActionListener(this);
        panelA.add(chooseDirectory);

        directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        runApplication = new JButton("Run Application");
        runApplication.addActionListener(this);
        runApplication.setEnabled(false);
        panelA.add(runApplication, "wrap");

        //PANEL B
        labelResultsLeft = new JLabel("Output A");
        panelB.add(labelResultsLeft, "wrap");

        this.resultsPaneLeft = new JTextArea(20, 80);
        resultsPaneLeft.setMinimumSize(new Dimension(900, 400));
        resultsPaneLeft.setMaximumSize(new Dimension(900, 400));
        this.scrollpaneLeft = new JScrollPane(resultsPaneLeft);

        this.labelResultsCentre = new JLabel("Ouput B");
        panelC.add(labelResultsCentre, "wrap");

        this.resultsPaneCentre = new JTextArea(20, 80);
        resultsPaneCentre.setMinimumSize(new Dimension(900, 400));
        resultsPaneCentre.setMaximumSize(new Dimension(900, 400));
        this.scrollpaneCentre = new JScrollPane(resultsPaneCentre);

        this.labelResultsRight = new JLabel("Output C");
        panelD.add(labelResultsRight, "wrap");

        this.resultsPaneRight = new JTextArea(20, 80);
        resultsPaneRight.setMinimumSize(new Dimension(900, 400));
        resultsPaneRight.setMaximumSize(new Dimension(900, 400));
        this.scrollpaneRight = new JScrollPane(resultsPaneRight);

        panelB.add(scrollpaneLeft, "span");
        panelC.add(scrollpaneCentre, "span");
        panelD.add(scrollpaneRight, "span");

        //COMPARE 2 DOCS
        this.labelCompare2Docs = new JLabel("COMPARE TWO DOCUMENTS");
        panelE.add(labelCompare2Docs, "wrap");

        this.labelDocA = new JLabel("Enter first document name:");
        panelE.add(labelDocA);

        this.docA = new JTextField(20);
        panelE.add(docA);

        this.labelDocB = new JLabel("Enter second document name:");
        panelE.add(labelDocB);

        this.docB = new JTextField(20);
        panelE.add(docB);

        this.compare2Docs = new JButton("Compare Two Documents");
        this.compare2Docs.addActionListener(this);
        panelE.add(compare2Docs, "wrap");

        // DISPLAY TOKENS
        this.labelDisplayTokens = new JLabel("DISPLAY TOKENS");
        panelE.add(labelDisplayTokens, "wrap, gaptop 15");

        this.labelMaxPostings = new JLabel("Enter maximum number of Postings:");
        panelE.add(labelMaxPostings);

        this.textMaxPostings = new JTextField(20);
        panelE.add(textMaxPostings);

        this.displayTokens = new JButton("Display Tokens");
        displayTokens.addActionListener(this);
        displayTokens.setEnabled(true);
        panelE.add(displayTokens, "wrap");

        // RUN TEST
        labelRunTest = new JLabel("TESTING");
        panelE.add(labelRunTest, "wrap, gaptop 15");

        getKnownPlagiarism = new JButton("Get Known Plagiarism");
        getKnownPlagiarism.addActionListener(this);
        panelE.add(getKnownPlagiarism, "wrap");

        knownPlagiarismChooser = new JFileChooser();

        radioSingleTest = new JRadioButton("Single Test");
        radioMultipleTest = new JRadioButton("Multiple Test");
        buttonGroupTests = new ButtonGroup();
        buttonGroupTests.add(radioSingleTest);
        buttonGroupTests.add(radioMultipleTest);
        radioSingleTest.setEnabled(false);
        radioMultipleTest.setEnabled(false);

        radioSingleTest.addActionListener(this);
        radioMultipleTest.addActionListener(this);

        panelE.add(radioSingleTest);

        runOnce = new JButton("Run Once");
        runOnce.addActionListener(this);
        panelE.add(runOnce, "wrap");
        runOnce.setEnabled(false);

        panelE.add(radioMultipleTest);

        labelMinPrecision = new JLabel("Enter minimum precision (0-100)");
        panelE.add(labelMinPrecision);
        labelMinPrecision.setEnabled(false);

        textMinPrecision = new JTextField("0", 20);
        panelE.add(textMinPrecision);
        textMinPrecision.setEnabled(false);

        labelMinRecall = new JLabel("Enter minimum recall (0-100)");
        panelE.add(labelMinRecall);
        labelMinRecall.setEnabled(false);

        textMinRecall = new JTextField("0", 20);
        panelE.add(textMinRecall);
        textMinRecall.setEnabled(false);

        runTest = new JButton("Run Test");
        runTest.addActionListener(this);
        panelE.add(runTest, "wrap");
        runTest.setEnabled(false);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        tokenizationType = (String) comboBoxSelectTokenization.getSelectedItem();

        minIDF = Double.parseDouble(textParameterIDF.getText());
        minTFIDF = Double.parseDouble(textParameterTFIDF.getText());
        minMatchingTokens = Integer.parseInt(textMinMatchingTokens.getText());
        minPrecision = Integer.parseInt(textMinPrecision.getText());
        minRecall = Integer.parseInt(textMinRecall.getText());

        Boolean includeApproximateMatches = false;

        if (e.getSource() == runApplication) {
            
            tokenizationType = (String) comboBoxSelectTokenization.getSelectedItem();

            resultsPaneLeft.setText(Plagiarism.displayResults(minIDF, minTFIDF, minMatchingTokens, tokenizationType,
                    includeApproximateMatches, directory));
            labelResultsLeft.setText("Suspicious Document Pairs");

            labelResultsCentre.setEnabled(false);
            labelResultsRight.setEnabled(false);

            resultsPaneCentre.setEnabled(false);
            resultsPaneRight.setEnabled(false);

        } else if (e.getSource() == displayTokens) {

            resultsPaneLeft.setText(Plagiarism.displayTokens(minTFIDF, maxPostings));

        } else if (e.getSource() == runTest) {

            Plagiarism.testPandR(dictionaryMap, inverseDocFreqMap, minPrecision, minRecall);

        } else if (e.getSource() == runOnce) {
            
            tokenizationType = (String) comboBoxSelectTokenization.getSelectedItem();

            resultsPaneLeft.setText(Plagiarism.displayResults(minIDF, minTFIDF, minMatchingTokens, tokenizationType,
                    includeApproximateMatches, directory));
            resultsPaneCentre.setText(Plagiarism.getKnownPlagiarism());
            resultsPaneRight.setText(Plagiarism.displayPrecisionAndRecall());

            labelResultsLeft.setText("Suspicious Document Pairs");
            labelResultsCentre.setText("Known Plagiarism");
            labelResultsRight.setText("Precision & Recall");

        } else if (e.getSource() == compare2Docs) {

            tokenizationType = (String) comboBoxSelectTokenization.getSelectedItem();
            
            String docAname = docA.getText();
            String docBname = docB.getText();
            resultsPaneCentre.setText(Plagiarism.getDocContents(docAname, directory));
            resultsPaneRight.setText(Plagiarism.getDocContents(docBname, directory));
            resultsPaneLeft.setText(Plagiarism.getMatchesBetween2docs(docAname, docBname));
            Plagiarism.highlightTokens(resultsPaneCentre, Plagiarism.matchingTokens);
            Plagiarism.highlightTokens(resultsPaneRight, Plagiarism.matchingTokens);

            //resultsPaneLeft.setText(Plagiarism.displayTokens(minTFIDF, 0, dictionaryMap, inverseDocFreqMap));
            labelResultsLeft.setText("Matching Tokens");
            labelResultsCentre.setText(docAname);
            labelResultsRight.setText(docBname);

            labelResultsLeft.setEnabled(true);
            labelResultsCentre.setEnabled(true);
            labelResultsRight.setEnabled(true);

            resultsPaneLeft.setEnabled(true);
            resultsPaneCentre.setEnabled(true);
            resultsPaneRight.setEnabled(true);

        } else if (e.getSource()
                == checkBoxDefaultParameters) {
            if (checkBoxDefaultParameters.isSelected()) {

                textParameterTFIDF.setEnabled(false);
                textParameterIDF.setEnabled(false);
                textMinMatchingTokens.setEnabled(false);

                textParameterTFIDF.setText(defaultTFIDF);
                textParameterIDF.setText(defaultIDF);
                textMinMatchingTokens.setText(defaultMinMatchingTokens);

            } else if (!checkBoxDefaultParameters.isSelected()) {
                textParameterTFIDF.setEnabled(true);
                textParameterIDF.setEnabled(true);
                textMinMatchingTokens.setEnabled(true);
            }
        } else if (e.getSource() == chooseDirectory) {

            directoryChooser.showOpenDialog(this);
            directory = directoryChooser.getSelectedFile();
            runApplication.setEnabled(true);

        } else if (e.getSource() == getKnownPlagiarism) {

            knownPlagiarismChooser.showOpenDialog(this);
            Plagiarism.setKnownPlagiarism(knownPlagiarismChooser.getSelectedFile());

            radioSingleTest.setEnabled(true);
            radioSingleTest.setSelected(true);
            radioMultipleTest.setEnabled(true);
            runOnce.setEnabled(true);

        } else if (e.getSource() == radioSingleTest) {
            runOnce.setEnabled(true);
            labelMinPrecision.setEnabled(false);
            textMinPrecision.setEnabled(false);
            labelMinRecall.setEnabled(false);
            textMinRecall.setEnabled(false);
            runTest.setEnabled(false);
        } else if (e.getSource() == radioMultipleTest) {
            labelMinPrecision.setEnabled(true);
            textMinPrecision.setEnabled(true);
            labelMinRecall.setEnabled(true);
            textMinRecall.setEnabled(true);
            runTest.setEnabled(true);

            runOnce.setEnabled(false);

        } else if (e.getSource() == comboBoxSelectTokenization) {
  
            tokenizationType = (String) comboBoxSelectTokenization.getSelectedItem();
            
            
        }

    }
}
