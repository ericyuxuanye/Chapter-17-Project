package com.company;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Main {

    public static final Tree tree;
    // put the file that stores the tree in the user's downloads directory, feel free to change this
    public static final File treeFile =
            new File(System.getProperty("user.home") + "/Downloads/treefile.ser.gz");

    public static final ImageIcon QUESTION_IMAGE = new FlatSVGIcon("question.svg", 200, 200);

    // to store currentNode
    public static Node currentNode;
    /**
     * JPanel that holds the prompt and a way to input questions for the user.
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
     * <p>
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
    /**
     * The prompt shown to the user every time
     */
    public static JLabel questionOrGuess;
    /**
     * Image supplied by user
     */
    public static JLabel image;
    /**
     * Panel that holds the guess, along with an image
     */
    public static JPanel guessPanel;
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
    /**
     * Yes button
     */
    public static JButton yesButton;
    /**
     * No button
     */
    public static JButton noButton;
    /**
     * Where the user can type the answer
     */
    public static JTextField userInput;
    /**
     * Submit button for the JTextField
     */
    public static JButton submitButton;
    /**
     * Whether the program has reached a leaf and is guessing
     */
    public static boolean currentlyGuessing = false;
    /**
     * Whether the program is asking the user for the correct answer
     */
    public static boolean isAskingForAnswer = true;
    /**
     * Whether the program is asking whether user wants to replay
     */
    public static boolean isAskingForReplay = false;
    /**
     * Whether user is choosing an image
     */
    public static boolean isChoosingImage = false;
    /**
     * To store the question supplied by user
     */
    public static String question;
    /**
     * To store the correct answer supplied by user
     */
    public static String correctAnswer;

    // static block, which means that this should be run as soon as the Main class is loaded
    static {
        Tree tree1;
        if (treeFile.exists()) {
            // read the tree from file if the file exists
            try (ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(treeFile)))) {
                tree1 = (Tree) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                tree1 = new Tree();
            }
        } else tree1 = new Tree();
        tree = tree1;
        currentNode = tree.getRoot();
    }

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

        guessPanel = new JPanel();
        guessPanel.setLayout(new BoxLayout(guessPanel, BoxLayout.PAGE_AXIS));
        // set the questionOrGuess to an empty String because we will fill it later
        questionOrGuess = new JLabel();
        questionOrGuess.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionOrGuess.setFont(displayFont);
        // image
        image = new JLabel(tree.getRoot().img);
        image.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(10));
        guessPanel.add(questionOrGuess);
        guessPanel.add(Box.createVerticalStrut(5));
        guessPanel.add(image);
        panel.add(guessPanel);

        // setup the submitPanel
        submitPanel = new JPanel();
        userInput = new JTextField(30);
        submitButton = new JButton("Submit");
        userInput.addActionListener(e -> submitAction());
        submitButton.addActionListener(e -> submitAction());
        submitPanel.add(userInput);
        submitPanel.add(submitButton);

        // setup yesNoPanel
        yesNoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
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
        welcomePanel.add(Box.createVerticalGlue());
        welcomePanel.add(welcomeLabel);
        JButton continueButton = new JButton("Continue");
        // when continue button is pressed, we show the game to the user
        continueButton.addActionListener(e -> {
            f.setContentPane(panel);
            f.revalidate();
            f.repaint();
        });
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.setPreferredSize(new Dimension(200, 50));
        continueButton.setFont(displayFont);
        welcomePanel.add(Box.createVerticalGlue());
        welcomePanel.add(continueButton);
        welcomePanel.add(Box.createVerticalGlue());

        f.setContentPane(welcomePanel);
        f.pack();
        f.setSize(800, 320);
        f.setVisible(true);
        f.setResizable(false);

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
     * Checks if a character is a vowel
     *
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
        if (isChoosingImage) {
            FileDialog fd =
                    new FileDialog((Frame) SwingUtilities.getWindowAncestor(panel), "Choose Image", FileDialog.LOAD);
            fd.setFilenameFilter(new FilenameFilter() {
                static final HashSet<String> acceptedImages =
                        new HashSet<>(List.of("jpg", "png", "gif", "jpeg", "webp"));

                @Override
                public boolean accept(File dir, String name) {
                    String extension = name.substring(name.lastIndexOf('.') + 1);
                    return acceptedImages.contains(extension);
                }
            });
            fd.setVisible(true);
            if (fd.getFile() != null) {
                String filename = fd.getDirectory() + fd.getFile();
                File imageFile = new File(filename);
                BufferedImage img;
                try {
                    img = ImageIO.read(imageFile);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                            panel, "Error reading image: " + imageFile.getName(),
                            "Unable to load image", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (img == null) {
                    JOptionPane.showMessageDialog(
                            panel,
                            "Unsupported Image format",
                            "Unable to load image",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // scale image so it isn't too big/small
                ImageIcon animalImage = new ImageIcon(img.getScaledInstance(-1, 200, Image.SCALE_DEFAULT));
                tree.update(currentNode, new Node(correctAnswer, animalImage), question);
                isChoosingImage = false;
                askForReplay("I'll learn that! Do you want to play again?");
            }
        } else if (currentlyGuessing) {
            // If the user pressed yes when the computer was guessing, then the computer is correct
            image.setIcon(QUESTION_IMAGE);
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
        if (isChoosingImage) {
            isChoosingImage = false;
            tree.update(currentNode, new Node(correctAnswer), question);
            askForReplay("I'll learn that! Do you want to play again?");
        } else if (currentlyGuessing) {
            // switch to text entry mode
            panel.remove(3);
            panel.add(submitPanel);
            userInput.requestFocus();
            questionOrGuess.setText("What is the correct answer?");
            image.setIcon(QUESTION_IMAGE);
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
            image.setIcon(Objects.requireNonNullElse(currentNode.img, QUESTION_IMAGE));
            questionOrGuess.setText("Is your animal " + (isVowel(guess.charAt(0)) ? "an " : "a ") + guess + "?");
            currentlyGuessing = true;
        } else {
            // ask question
            questionOrGuess.setText(currentNode.data);
            image.setIcon(QUESTION_IMAGE);
        }
    }

    /**
     * Asks user for replay
     *
     * @param question question to ask for replay
     */
    public static void askForReplay(String question) {
        currentlyGuessing = false;
        questionOrGuess.setText(question);
        image.setIcon(QUESTION_IMAGE);
        isAskingForReplay = true;
        isAskingForAnswer = true;
    }

    /**
     * Runs when submit button is pressed
     */
    public static void submitAction() {
        if (userInput.getText().length() == 0) {
            JOptionPane.showMessageDialog(
                    panel, "Cannot have empty answer", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (isAskingForAnswer) {
            correctAnswer = userInput.getText();
            isAskingForAnswer = false;
            questionOrGuess.setText("Enter a question that is true for " + correctAnswer
                    + " but false for " + currentNode.data + ".");
            userInput.setText(null);
            userInput.requestFocus();
        } else {
            question = userInput.getText();
            userInput.setText(null);
            questionOrGuess.setText("Would you like to add an image of "
                    + (isVowel(correctAnswer.charAt(0)) ? "an " : "a ") + correctAnswer + "?");

            // replace with yes no panel
            panel.remove(3);
            panel.add(yesNoPanel);
            panel.revalidate();
            panel.repaint();
            isChoosingImage = true;
        }
    }

    /**
     * Write the Tree object to file
     */
    public static void saveTree() {
        try (ObjectOutputStream os = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(treeFile)))) {
            os.writeObject(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
