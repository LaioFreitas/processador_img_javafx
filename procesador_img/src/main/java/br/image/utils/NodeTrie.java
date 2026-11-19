package br.image.utils;

public class NodeTrie implements Comparable<NodeTrie> {

    private int symbol;
    private int frequency;
    private NodeTrie left;
    private NodeTrie right;

    public NodeTrie(NodeTrie left, NodeTrie right) {
        this.left = left;
        this.right = right;
    }

    public NodeTrie(int symbol, int frequency) {
        this.symbol = symbol;
        this.frequency = frequency;
    }

    public NodeTrie(int symbol, int frequency, NodeTrie left, NodeTrie right) {
        this.symbol = symbol;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    public NodeTrie getLeft() {
        return left;
    }

    public NodeTrie getRight() {
        return right;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    @Override
    public int compareTo(NodeTrie arg) {
        return this.frequency - arg.frequency;
    }
}
