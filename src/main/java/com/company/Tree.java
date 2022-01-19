package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public class Tree implements Serializable {

    private static ImageIcon img;
    static {
        try {
            BufferedImage bufferedImage =
                    ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("cow.jpg")));
            img = new ImageIcon(bufferedImage);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "AAAAAH THIS SHOULD NEVER HAVE HAPPENED!!!!!",
                    "AAAHH", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private final Node root;

    public Tree() {
        // The first animal is Cow, how nice!
        root = new Node("Cow", img);
    }

    public Node getRoot() {
        return root;
    }

    /**
     * Updates a guess node into a new question node that branches out into two guess nodes.<br><br>
     *
     * Example: The program guessed cow, which is wrong. The user provides the right answer, which is rabbit, and
     * the question supplied by the user is 'Does it have long ears?'<br><br>
     *
     * The following node is before this method is called.<br><br>
     * <pre>
     * ╭────────────╮
     * │ Guess: Cow │
     * ╰────────────╯
     * </pre>
     * And this is after this method has been called:<br><br>
     * <pre>
     * ╭───────────────────────────────────╮
     * │ Question: Does it have long ears? │
     * ╰────────────────┬──────────────────╯
     *       ┌──────────┴──────────┐
     *    No │                     │ Yes
     * ╭─────┴──────╮      ╭───────┴───────╮
     * │ Guess: Cow │      │ Guess: Rabbit │
     * ╰────────────╯      ╰───────────────╯
     * </pre>
     * @param wrongGuess The original node that contains the wrong guess
     * @param rightGuess The node containing the correct answer
     * @param question The question used to distinguish between the wrong guess and the right guess
     */
    public void update(Node wrongGuess, Node rightGuess, String question) {
        Node leftBranch = new Node(wrongGuess.data, wrongGuess.img);
        wrongGuess.data = question;
        wrongGuess.img = null;
        wrongGuess.left = leftBranch;
        wrongGuess.right = rightGuess;
    }
}
