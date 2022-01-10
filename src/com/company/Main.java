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
            System.out.println("Do you want to play again?");
            do {
                playAgain = sc.nextLine().trim();
            } while (!playAgain.equalsIgnoreCase("y") && !playAgain.equalsIgnoreCase("n"));
        } while (!playAgain.equalsIgnoreCase("n"));
        // write to file
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(treeFile))) {
            os.writeObject(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void play(Scanner sc) {
        Node currentNode = tree.getRoot();
        String input;
        while (!currentNode.isLeaf()) {
            String question = currentNode.data;
            System.out.println(question + " (y or n)");
            input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("y")) {
                currentNode = currentNode.right;
            } else if (input.equalsIgnoreCase("n")) {
                currentNode = currentNode.left;
            } else {
                System.out.println("Invalid answer");
            }
        }
        String guess = currentNode.data;
        do {
            System.out.println("Is your animal " + (isVowel(guess.charAt(0)) ? "an " : "a ") + guess + "? (y or n)");
            input = sc.nextLine().trim();
        } while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n"));

        if (input.equalsIgnoreCase("y")) {
            System.out.println("Hooray! I guessed right!");
        } else {
            System.out.println("What is the correct answer?");
            String correctAnswer = sc.nextLine().trim();
            System.out.println("Enter a question that is true for " + correctAnswer + " but false for " + guess + ".");
            String question = sc.nextLine().trim();
            tree.update(currentNode, correctAnswer, question);
        }
    }

    public static boolean isVowel(char c) {
        return switch (c) {
            case 'a', 'e', 'i', 'o', 'u' -> true;
            default -> false;
        };
    }
}
