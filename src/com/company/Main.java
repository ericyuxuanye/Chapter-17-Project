package com.company;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static final Tree tree;
    // put the file that stores the tree in the user's downloads directory, feel free to change this
    public static final File treeFile = new File(System.getProperty("user.home") + "/Downloads/treefile.ser");

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
    }

    public static void main(String[] args) {
	    // write your code here
        Scanner sc = new Scanner(System.in);
        String playAgain;
        do {
            play(sc);
            do {
                System.out.println("Do you want to play again? (y or n)");
                playAgain = sc.nextLine().trim();
                System.out.println();
            } while (!playAgain.equalsIgnoreCase("y") && !playAgain.equalsIgnoreCase("n"));
        } while (!playAgain.equalsIgnoreCase("n"));
        // write to file
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(treeFile))) {
            os.writeObject(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads in input and plays the guessing game with the user. It starts off by asking a series of questions until
     * a leaf node is reached. It then asks the user if the guess is correct. If the guess is correct, then the method
     * will end, otherwise, the program will ask the user for the correct answer and a question to differentiate between
     * the guess and the correct answer.
     *
     * @param sc scanner to get user input
     */
    public static void play(Scanner sc) {
        Node currentNode = tree.getRoot();
        String input;
        while (!currentNode.isLeaf()) {
            // keep asking questions until we have only one possible guess
            String question = currentNode.data;
            System.out.println(question + " (y or n)");
            input = sc.nextLine().trim();
            System.out.println();
            if (input.equalsIgnoreCase("y")) {
                currentNode = currentNode.right;
            } else if (input.equalsIgnoreCase("n")) {
                currentNode = currentNode.left;
            } else {
                System.out.println("Invalid answer");
            }
        }
        // guess the animal
        String guess = currentNode.data;
        do {
            System.out.println("Is your animal " + (isVowel(guess.charAt(0)) ? "an " : "a ") + guess + "? (y or n)");
            input = sc.nextLine().trim();
            System.out.println();
        } while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n"));

        if (input.equalsIgnoreCase("y")) {
            System.out.println("Hooray! I guessed right!");
        } else {
            // if the guess is wrong, ask the user for the correct answer and a question.
            System.out.println("What is the correct answer?");
            String correctAnswer = sc.nextLine().trim();
            System.out.println();
            System.out.println("Enter a question that is true for " + correctAnswer + " but false for " + guess + ".");
            String question = sc.nextLine().trim();
            System.out.println();
            tree.update(currentNode, correctAnswer, question);
        }
    }

    public static boolean isVowel(char c) {
        return switch (c) {
            case 'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U' -> true;
            default -> false;
        };
    }
}
