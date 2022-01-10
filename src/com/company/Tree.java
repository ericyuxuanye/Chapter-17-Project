package com.company;

import java.io.Serializable;

public class Tree implements Serializable {
    private final Node root;

    public Tree() {
        // default constructor
        root = new Node("Cow");
    }

    public Node getRoot() {
        return root;
    }

    /**
     * Updates a guess node into a new question node that branches out into two guess nodes.<br><br>
     *
     * Example: The program guessed cow, which is wrong. The user provides the right answer, which is a rabbit, and
     * the question supplied is that a rabbit has long ears.<br><br>
     *
     * The following node is before this method is called.<br>
     * <pre>
     *              Guess: Cow
     * </pre>
     * And this is after this method has been called:<br>
     * <pre>
     *              Question: Does it have long ears?
     *                          /\
     *                         /  \
     *                        /    \
     *                    No /      \  Yes
     *                      /        \
     *                     /          \
     *                    /            \
     *             Guess: Cow        Guess: Rabbit
     * </pre>
     * @param wrongGuess The original node that contains the wrong guess
     * @param rightGuess The correct answer provided by the user
     * @param question The question used to distinguish between the wrong guess and the right guess
     */
    public void update(Node wrongGuess, String rightGuess, String question) {
        Node rightBranch = new Node(rightGuess);
        Node leftBranch = new Node(wrongGuess.data);
        wrongGuess.data = question;
        wrongGuess.left = leftBranch;
        wrongGuess.right = rightBranch;
    }
}
