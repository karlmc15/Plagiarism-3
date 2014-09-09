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
import javax.swing.text.DefaultCaret;
import net.miginfocom.swing.MigLayout;

public class View extends JFrame implements ActionListener {

    private final JButton chooseDirectory, runApplication, compare2Docs, getKnownPlagiarism, runOnce, runTest;
    private final JTextField textMinTokenThreshold, textMaxTokenThreshold, textMinMatchingTokens;
    private final JTextField docA, docB;
    private final JLabel labelSelectTokenization, labelResultsLeft, labelResultsCentre, labelResultsRight;
    private final JLabel labelMaxTokenThreshold, labelMinTokenThreshold, labelMinMatchingTokens, labelRunTest, labelCompare2Docs;
    private final JLabel labelDocA, labelDocB;
    private final JTextArea resultsPaneLeft, resultsPaneCentre, resultsPaneRight;
    boolean displayTokens, weighted = false;
    private final JScrollPane scrollpaneLeft, scrollpaneCentre, scrollpaneRight;
    public Integer maxPostings, minMatchingTokens, minTokenThreshold, maxTokenThreshold;
    public Double minIDF, minTFIDF;
    private final String defaultIDF = "0";
    private final String defaultTFIDF = "0.0";
    private final String defaultMinMatchingTokens = "0";

    public final JCheckBox checkBoxDefaultParameters, checkBoxDisplayTokens, checkBoxWeighted;
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

    String[] tokenizationTypes = {"1-Grams", "Bi-Grams", "3-Grams", "4-Grams", "1-Grams plus WhiteSpace", "White Space", "Java Syntax"};
    public JComboBox comboBoxSelectTokenization = new JComboBox(tokenizationTypes);

    public static ConcurrentHashMap<String, ArrayList<String[]>> dictionaryMap = new ConcurrentHashMap<String, ArrayList<String[]>>();
    public static ConcurrentHashMap<String, Double[]> inverseDocFreqMap = new ConcurrentHashMap<String, Double[]>();
    public static String tokenizationType;

    public View() {
        super();
        setTitle("Similarity Detector");
        setSize(950, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //setVisible(true);

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
        labelSelectTokenization = new JLabel("Tokenisation Method:");
        panelA.add(labelSelectTokenization);

        panelA.add(comboBoxSelectTokenization);
        comboBoxSelectTokenization.setSelectedItem("Bi-Grams");
        comboBoxSelectTokenization.addActionListener(this);

        checkBoxDefaultParameters = new JCheckBox("Default Parameters");
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

        checkBoxDisplayTokens = new JCheckBox("Display Tokens");
        checkBoxDisplayTokens.addActionListener(this);
        checkBoxDisplayTokens.setSelected(false);
        panelA.add(checkBoxDisplayTokens);

        checkBoxWeighted = new JCheckBox("Weighted");
        checkBoxWeighted.addActionListener(this);
        checkBoxWeighted.setSelected(false);
        panelA.add(checkBoxWeighted);

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

        this.labelResultsCentre = new JLabel("Output B");
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

        labelMinTokenThreshold = new JLabel("Enter minimum token threshold");
        panelE.add(labelMinTokenThreshold);
        labelMinTokenThreshold.setEnabled(false);

        textMinTokenThreshold = new JTextField("0", 10);
        panelE.add(textMinTokenThreshold);
        textMinTokenThreshold.setEnabled(false);

        labelMaxTokenThreshold = new JLabel("Enter maximum token threshold");
        panelE.add(labelMaxTokenThreshold);
        labelMaxTokenThreshold.setEnabled(false);

        textMaxTokenThreshold = new JTextField("0", 10);
        panelE.add(textMaxTokenThreshold);
        textMaxTokenThreshold.setEnabled(false);

        runTest = new JButton("Run Test");
        runTest.addActionListener(this);
        panelE.add(runTest, "wrap");
        runTest.setEnabled(false);

        DefaultCaret caretCentre = (DefaultCaret) resultsPaneCentre.getCaret();
        caretCentre.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        DefaultCaret caretLeft = (DefaultCaret) resultsPaneLeft.getCaret();
        caretLeft.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        DefaultCaret caretRight = (DefaultCaret) resultsPaneRight.getCaret();
        caretRight.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        tokenizationType = (String) comboBoxSelectTokenization.getSelectedItem();

        minIDF = Double.parseDouble(textParameterIDF.getText());
        minTFIDF = Double.parseDouble(textParameterTFIDF.getText());
        minMatchingTokens = Integer.parseInt(textMinMatchingTokens.getText());
        minTokenThreshold = Integer.parseInt(textMinTokenThreshold.getText());
        maxTokenThreshold = Integer.parseInt(textMaxTokenThreshold.getText());

        Boolean includeApproximateMatches = false;

        if (e.getSource() == runApplication) {

            tokenizationType = (String) comboBoxSelectTokenization.getSelectedItem();

            resultsPaneLeft.setText(Plagiarism.displayResults(minIDF, minTFIDF, minMatchingTokens, tokenizationType,
                    includeApproximateMatches, directory, weighted));

            labelResultsLeft.setText("Suspicious Document Pairs");

            labelResultsCentre.setText(" ");
            labelResultsRight.setText(" ");

            labelResultsCentre.setEnabled(false);
            labelResultsRight.setEnabled(false);

            resultsPaneCentre.setText(" ");
            resultsPaneRight.setText(" ");

            resultsPaneCentre.setEnabled(false);
            resultsPaneRight.setEnabled(false);

            if (displayTokens) {
                resultsPaneCentre.setText(Plagiarism.displayTokens(minTFIDF, minIDF));
                resultsPaneCentre.setEnabled(true);
                labelResultsCentre.setText("All Tokens");
                labelResultsCentre.setEnabled(true);
            }

        } else if (e.getSource() == runTest) {

            tokenizationType = (String) comboBoxSelectTokenization.getSelectedItem();
            Plagiarism.testPandR(minTokenThreshold, maxTokenThreshold, includeApproximateMatches, tokenizationType, directory, weighted);

        } else if (e.getSource() == runOnce) {

            tokenizationType = (String) comboBoxSelectTokenization.getSelectedItem();

            resultsPaneLeft.setText(Plagiarism.displayResults(minIDF, minTFIDF, minMatchingTokens, tokenizationType,
                    includeApproximateMatches, directory, weighted));
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
        } else if (e.getSource()
                == checkBoxDisplayTokens) {
            if (checkBoxDisplayTokens.isSelected()) {
                displayTokens = true;
            } else {
                displayTokens = false;
            }

        } else if (e.getSource() == checkBoxWeighted) {
            if (checkBoxWeighted.isSelected()) {
                weighted = true;
            } else {
                weighted = false;
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
            labelMinTokenThreshold.setEnabled(false);
            textMinTokenThreshold.setEnabled(false);
            labelMaxTokenThreshold.setEnabled(false);
            textMaxTokenThreshold.setEnabled(false);
            runTest.setEnabled(false);
        } else if (e.getSource() == radioMultipleTest) {
            labelMinTokenThreshold.setEnabled(true);
            textMinTokenThreshold.setEnabled(true);
            labelMaxTokenThreshold.setEnabled(true);
            textMaxTokenThreshold.setEnabled(true);
            runTest.setEnabled(true);

            runOnce.setEnabled(false);

        } else if (e.getSource() == comboBoxSelectTokenization) {

            tokenizationType = (String) comboBoxSelectTokenization.getSelectedItem();

        }

    }
}
