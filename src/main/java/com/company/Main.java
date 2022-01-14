package com.company;

import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

import javax.swing.*;

public class Main {

    public static final Tree tree;
    // put the file that stores the tree in the user's downloads directory, feel free to change this
    public static final File treeFile = new File(System.getProperty("user.home") + "/Downloads/treefile.ser");

    // to store currentNode
    public static Node currentNode;

    // static block, which means that this should be run as soon as the Main class is loaded
    static {
        Tree tree1;
        if (treeFile.exists()) {
            // read the tree from file if the file exists
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(treeFile))) {
                tree1 = (Tree)ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                tree1 = new Tree();
            }
        } else tree1 = new Tree();
        tree = tree1;
        currentNode = tree.getRoot();
    }

    /**
     * Layout of panel when asking a yes or no question, using yesNoPanel at the bottom
     * <pre>
     * ┌───────────────────────────────────────────────────────┐
     * │      Question or Guess that the computer makes        │
     * │                                                       │
     * │     ╭────────╮                       ╭───────╮        │
     * │     │  Yes   │                       │  No   │        │
     * │     ╰────────╯                       ╰───────╯        │
     * └───────────────────────────────────────────────────────┘
     * </pre>
     *
     * Layout of panel when asking for input, using submitPanel at the bottom
     * <pre>
     * ┌───────────────────────────────────────────────────────┐
     * │ Asking the user for the correct answer and a question │
     * │                                                       │
     * │   ┌─────────────────────────────────┐  ╭────────╮     │
     * │   │                                 │  │ Submit │     │
     * │   └─────────────────────────────────┘  ╰────────╯     │
     * └───────────────────────────────────────────────────────┘
     * </pre>
     */
    public static JPanel panel;

    /** The prompt shown to the user every time */
    public static JLabel questionOrGuess;

    /**
     * JPanel that contains a JTextField and a submit button
     * <pre>
     * ┌─────────────────────────────────┐  ╭────────╮
     * │                                 │  │ Submit │
     * └─────────────────────────────────┘  ╰────────╯
     * </pre>
     */
    public static JPanel submitPanel;

    /**
     * JPanel that contains a yes and a no button
     * <pre>
     * ╭───────╮             ╭───────╮
     * │  Yes  │             │  No   │
     * ╰───────╯             ╰───────╯
     * </pre>
     */
    public static JPanel yesNoPanel;

    /** Yes button */
    public static JButton yesButton;

    /** No button */
    public static JButton noButton;

    /** Where the user can type the answer */
    public static JTextField userInput;

    /** Submit button for the JTextField */
    public static JButton submitButton;

    /** Whether the program has reached a leaf and is guessing */
    public static boolean currentlyGuessing = false;

    /** Whether the program is asking the user for the correct answer */
    public static boolean isAskingForAnswer = true;

    /** Whether the program is asking whether user wants to replay */
    public static boolean isAskingForReplay = false;

    /** To store the correct answer supplied by user */
    public static String correctAnswer;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::setupUI);
    }

    /**
     * Initializes graphics components to start the game
     */
    public static void setupUI() {
        // set the look and feel
        FlatDarkLaf.setup();
        // font to use for display
        Font displayFont = new Font("Arial", Font.PLAIN, 18);
        JFrame f = new JFrame("The Animal Guessing Game");
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        // set the questionOrGuess to an empty String because we will fill it later
        questionOrGuess = new JLabel("");
        questionOrGuess.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionOrGuess.setFont(displayFont);
        panel.add(Box.createVerticalStrut(10));
        panel.add(questionOrGuess);

        // setup the submitPanel
        submitPanel = new JPanel();
        userInput = new JTextField(30);
        submitButton = new JButton("Submit");
        userInput.addActionListener(e -> submitAction());
        submitButton.addActionListener(e -> submitAction());
        submitPanel.add(userInput);
        submitPanel.add(submitButton);

        // setup yesNoPanel
        yesNoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        yesButton = new JButton("Yes");
        yesButton.addActionListener(e -> yesAction());
        noButton = new JButton("No");
        noButton.addActionListener(e -> noAction());
        yesNoPanel.add(yesButton);
        yesNoPanel.add(noButton);

        panel.add(Box.createVerticalGlue());
        panel.add(yesNoPanel);

        // start game
        displayData();

        // show welcome screen
        // ┌───────────────────────────────────────────────────────┐
        // │        Think of an animal and press continue.         │
        // │                                                       │
        // │                    ╭────────────╮                     │
        // │                    │  Continue  │                     │
        // │                    ╰────────────╯                     │
        // └───────────────────────────────────────────────────────┘
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.PAGE_AXIS));
        JLabel welcomeLabel = new JLabel("Think of an animal and press continue.");
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setFont(displayFont);
        welcomePanel.add(Box.createVerticalStrut(10));
        welcomePanel.add(welcomeLabel);
        JButton continueButton = new JButton("Continue");
        // when continue button is pressed, we show the game to the user
        continueButton.addActionListener(e -> {
            f.setContentPane(panel);
            f.revalidate();
            f.repaint();
        });
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomePanel.add(Box.createVerticalGlue());
        welcomePanel.add(continueButton);
        welcomePanel.add(Box.createVerticalGlue());

        f.setContentPane(welcomePanel);
        f.pack();
        // f.setResizable(false);
        f.setSize(500, 150);
        f.setVisible(true);

        // write tree to file when close button is pressed
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // write tree to file
                saveTree();
                System.exit(0);
            }
        });
    }

    /**
     * Checks if a character is a vower
     * @param c the character to check
     * @return whether c is 'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', or 'U'
     */
    public static boolean isVowel(char c) {
        return switch (c) {
            case 'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U' -> true;
            default -> false;
        };
    }

    /**
     * Runs when 'yes' button is pressed
     */
    public static void yesAction() {
        if (currentlyGuessing) {
            // If the user pressed yes when the computer was guessing, then the computer is correct
            askForReplay("Yay! I guessed right. Do you want to play again?");
        } else if (isAskingForReplay) {
            isAskingForReplay = false;
            currentNode = tree.getRoot();
            displayData();
        } else {
            currentNode = currentNode.right;
            displayData();
        }
    }

    /**
     * Runs when 'no' button is pressed
     */
    public static void noAction() {
        if (currentlyGuessing) {
            // switch to text entry mode
            panel.remove(3);
            panel.add(submitPanel);
            questionOrGuess.setText("What is the correct answer?");
            panel.revalidate();
            panel.repaint();
        } else if (isAskingForReplay) {
            // write file to disk
            saveTree();
            System.exit(0);
        } else {
            currentNode = currentNode.left;
            displayData();
        }
    }

    /**
     * Loads the question or guess in the current node
     */
    public static void displayData() {
        if (currentNode.isLeaf()) {
            // guess
            String guess = currentNode.data;
            questionOrGuess.setText("Is your animal " + (isVowel(guess.charAt(0)) ? "an " : "a ") + guess + "?");
            currentlyGuessing = true;
        } else {
            // ask question
            questionOrGuess.setText(currentNode.data);
        }
    }

    /**
     * Asks user for replay
     * @param question question to ask for replay
     */
    public static void askForReplay(String question) {
        currentlyGuessing = false;
        questionOrGuess.setText(question);
        isAskingForReplay = true;
    }

    /**
     * Runs when submit button is pressed
     */
    public static void submitAction() {
        if (isAskingForAnswer) {
            correctAnswer = userInput.getText();
            isAskingForAnswer = false;
            questionOrGuess.setText("Enter a question that is true for " + correctAnswer
                    + " but false for " + currentNode.data + ".");
            userInput.setText(null);
        } else {
            String question = userInput.getText();
            userInput.setText(null);
            tree.update(currentNode, correctAnswer, question);

            // reset stuff
            isAskingForAnswer = true;
            panel.remove(3);
            panel.add(yesNoPanel);
            panel.revalidate();
            panel.repaint();
            askForReplay("I'll learn that! Do you want to play again?");
        }
    }

    public static void saveTree() {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(treeFile))) {
            os.writeObject(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
