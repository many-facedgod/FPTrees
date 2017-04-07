
package fptree;

import java.util.*;

/**
 * A class representing an FP tree that applies the FP Growth algorithm to get
 * the frequent item-sets from a set of transactions.
 * @author Tanmaya
 */
public class Tree {

    int max_label;
    Node root;
    int counts[];
    Node head[];
    Node tail[];
    boolean transparent[];
    int numNodes;
    List<Node> all;
    /**
     * The constructor for the FP Tree
     * @param max_label The number of different kinds of items
     */
    Tree(int max_label) {
        this.max_label = max_label;
        this.root = new Node(-1, max_label);
        this.counts = new int[max_label];
        this.head = new Node[max_label];
        this.tail = new Node[max_label];
        this.transparent = new boolean[max_label];
        this.numNodes = 0;
        this.all = new ArrayList<>();
    }
    /**
     * Adds a transaction to the FP Tree
     * @param list The transaction in the form of a sorted integer array
     */
    void add(int list[]) {
        Node curr = root;
        for (int i = 0; i < list.length; i++) {
            if (curr.children[list[i]] == null) {
                curr.children[list[i]] = new Node(list[i], max_label);
                curr = curr.children[list[i]];
                curr.id = numNodes++;
                all.add(curr);
                if (head[list[i]] == null) {
                    head[list[i]] = curr;
                    tail[list[i]] = curr;
                } else {
                    tail[list[i]].next_same = curr;
                    tail[list[i]] = curr;
                }
            } else {
                curr = curr.children[list[i]];
                curr.count++;

            }
            counts[list[i]]++;

        }
    }
    /**
     * Checks whether the node was active in the previous depth of the recursion
     * tree
     * @param n The node to be checked
     * @param depth The current depth of the recursion tree
     * @return True or false based on whether it's active
     */
    private boolean isActivePrev(Node n, int depth) {
        if (n == null) {
            return false;
        } else {
            return n.activation >= depth;
        }
    }
    /**
     * Checks whether the node is active in the current depth of the recursion
     * tree
     * @param n The node to be checked
     * @param depth The current depth of the recursion tree
     * @return True or false based on whether it's active
     */
    private boolean isActiveCurr(Node n, int depth) {
        if (n == null) {
            return false;
        } else {
            return n.activation > depth;
        }
    }
    /**
     * Deactivates some nodes to form a prefix tree
     * @param n The root node
     * @param depth The current depth of the recursion tree
     * @param element The element for which a prefix tree is needed
     * @return Whether the node n is or has a successor that has element as the 
     *         label
     */
    private boolean deactivate(Node n, int depth, int element) {
        if (!isActivePrev(n, depth)) {
            return false;
        } else {
            boolean z = false;
            for (int i = 0; i < max_label; i++) {
                boolean temp = deactivate(n.children[i], depth, element);
                z = z || temp;
            }
            if (!z) {
                n.activation = depth;
                if (n.label != -1) {
                    counts[n.label] -= n.count;
                }
            }
            return z || (n.label == element);
        }
    }
    /**
     * Undoes the one "deactivate" call
     * @param n The root node
     * @param depth The depth of the recursion tree
     */
    private void reactivate(Node n, int depth) {
        if (!isActivePrev(n, depth)) {
            return;
        }
        if (!isActiveCurr(n, depth)) {
            n.activation = Integer.MAX_VALUE;
            if (n.label != -1) {
                counts[n.label] += n.count;
            }
        }
        for (int i = 0; i < max_label; i++) {
            reactivate(n.children[i], depth);
        }

    }
    /**
     * Percolates the values upwards to form a conditional tree
     * @param n The root node
     * @param depth The depth of the recursion tree
     * @param stored The HashMap to store values for undoing percolateUp
     * @param elem The element under consideration
     * @return The number percolated through this node
     */
    private int percolateUp(Node n, int depth, HashMap<Integer, Integer> stored, int elem) {
        if (!isActivePrev(n, depth)) {
            return 0;
        } else if (n.label == elem) {
            return n.count;
        } else if (!isActiveCurr(n, depth)) {
            return 0;
        } else {

            int k = 0;
            for (int i = 0; i < max_label; i++) {
                k += percolateUp(n.children[i], depth, stored, elem);

            }
            if (k != n.count && n.label != -1) {
                stored.put(n.id, n.count);
                counts[n.label] += k - n.count;
                n.count = k;
            }
            return k;
        }
    }
    /**
     * Undoes the most recent call to "percolateUp"
     * @param stored The HashMap created by the percolateUp call
     */
    private void reestablish(HashMap<Integer, Integer> stored) {
        Set<Integer> keys = stored.keySet();
        for (int k : keys) {
            Node x = all.get(k);
            counts[x.label] += stored.get(k) - x.count;
            x.count = stored.get(k);
        }
    }
    /**
     * Recalculates the transparency values based on the current count values
     * @param minsup The minimum support count. (<= marked transparent)
     */
    private void transparency(int minsup) {
        for (int i = 0; i < max_label; i++) {
            transparent[i] = counts[i] <= minsup;
        }
    }
    /**
     * Generates the frequent item sets with support > minsup
     * @param minsup The support count threshold
     * @param prefix The prefix from previous call
     * @param depth The current depth of the recursion tree
     * @param finalset The List to which the extracted item sets are appended
     */
    private void getFrequent(int minsup, int prefix[], int depth, List<int[]> finalset) {
        if (!isActivePrev(root, depth)) {
            return;
        }
        for (int i = max_label - 1; i >= 0; i--) {
            if (transparent[i]) {
                continue;
            }
            int new_pref[] = new int[prefix.length + 1];
            System.arraycopy(prefix, 0, new_pref, 1, prefix.length);
            new_pref[0] = i;
            deactivate(root, depth, i);
            HashMap<Integer, Integer> stored = new HashMap<>();
            percolateUp(root, depth, stored, i);
            transparency(minsup);
            getFrequent(minsup, new_pref, depth + 1, finalset);
            reestablish(stored);
            reactivate(root, depth);
            transparency(minsup);
            finalset.add(new_pref);

        }
    }
    /**
     * Returns a List of item sets with support count > minsup
     * @param minsup The support count threshold
     * @return A List of the generated frequent item sets.
     */
    public List<int[]> frequent(int minsup) {
        List<int[]> finalset = new ArrayList<>();
        int pref[] = new int[0];
        transparency(minsup);
        getFrequent(minsup, pref, 0, finalset);
        return finalset;
    }

}
/**
 * Class representing a node of the FP Tree
 * @author Tanmaya
 */
class Node {

    Node next_same;
    int label;
    int count;
    int activation;
    int id;
    Node children[];
    /**
     * Constructor for a node
     * @param label The label of this node (the item)
     * @param max_label The number of different items possible
     */
    Node(int label, int max_label) {
        this.label = label;
        this.children = new Node[max_label];
        this.activation = Integer.MAX_VALUE;
        this.count = 1;
        this.id = -1;

    }
}
