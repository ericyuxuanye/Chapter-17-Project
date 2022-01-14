package com.company;

import java.io.Serializable;

public class Node implements Serializable {

    /**
     * Holds either a question if it is a branch or the guess if it is a leaf node
     */
    String data;
    /**
     * The no branch
     */
    Node left;
    /**
     * The yes branch (because 'right'!)
     */
    Node right;

    public Node(String data) {
        this.data = data;
    }

    public boolean isLeaf() {
        return left == null || right == null;
    }
}