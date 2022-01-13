package com.company;

import com.formdev.flatlaf.FlatLightLaf;

import java.awt.*;
import java.io.*;
import java.util.Scanner;

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
     * │ Question or Guess that the computer makes             │
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
     * │   ┌────────────────────────────┐       ╭────────╮     │
     * │   │                            │       │ Submit │     │
     * │   └────────────────────────────┘       ╰────────╯     │
     * └───────────────────────────────────────────────────────┘
     * </pre>
     */
    public static JPanel panel;

    /** The prompt shown to the user every time */
    public static JLabel questionOrGuess;

    /**
     * JPanel that contains a JTextField and a submit button
     * <pre>
     * ┌────────────────────────────┐       ╭────────╮
     * │                            │       │ Submit │
     * └────────────────────────────┘       ╰────────╯
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

    public static void setupUI() {
        // set the look and feel
        FlatLightLaf.setup();
        JFrame f = new JFrame("The Animal Guessing Game");
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        // set the questionOrGuess to an empty String because we will fill it later
        questionOrGuess = new JLabel("");
        questionOrGuess.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionOrGuess.setFont(new Font("Arial", Font.PLAIN, 18));
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
        loadNext();

        f.setContentPane(panel);
        f.pack();
        // f.setResizable(false);
        f.setSize(500, 150);
        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static boolean isVowel(char c) {
        return switch (c) {
            case 'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U' -> true;
            default -> false;
        };
    }


    public static void yesAction() {
        if (currentlyGuessing) {
            askForReplay("Yay! I guessed right. Do you want to play again?");
        } else if (isAskingForReplay) {
            isAskingForReplay = false;
            // user answers yes to replay, so we ask the first question again
            currentNode = tree.getRoot();
            loadNext();
        } else {
            currentNode = currentNode.right;
            loadNext();
        }
    }

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
            try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(treeFile))) {
                os.writeObject(tree);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        } else {
            currentNode = currentNode.left;
            loadNext();
        }
    }

    public static void loadNext() {
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

    public static void askForReplay(String question) {
        currentlyGuessing = false;
        questionOrGuess.setText(question);
        isAskingForReplay = true;
    }


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
}
