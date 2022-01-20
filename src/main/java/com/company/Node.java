package com.company;

import javax.swing.*;
import java.io.Serializable;

public class Node implements Serializable {

    /**
     * Holds either a question if it is a branch or the guess if it is a leaf node
     */
    String data;
    /**
     * Holds an image if this is a leaf node and the user has supplied an image
     */
    Icon img;
    /**
     * The no branch
     */
    Node left;
    /**
     * The yes branch (because 'right'!)
     */
    Node right;

    /**
     * Creates a new Node object
     *
     * @param data the text this node stores
     */
    public Node(String data) {
        this.data = data;
    }

    public Node(String data, Icon img) {
        this.data = data;
        this.img = img;
    }

    /**
     * Whether this node is a leaf node (no children)
     *
     * @return true if this node is a leaf node
     */
    public boolean isLeaf() {
        return left == null && right == null;
    }
}
