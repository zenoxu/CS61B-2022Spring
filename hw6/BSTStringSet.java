import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Implementation of a BST based String Set.
 * @author Ziyi Xu
 */
public class BSTStringSet implements StringSet, Iterable<String>,SortedStringSet {
    /** Creates a new empty set. */
    public BSTStringSet() {
        _root = null;
        _StringList = new ArrayList<>();
    }

    @Override
    public void put(String s) {
        // FIXME: PART A
        if (this._root == null) {
            this._root = new Node(s);
        }
        Node last = lastNode(this._root, s);
        if (last.s.compareTo(s) > 0) {
            last.left = new Node(s);
        }
        else if (last.s.compareTo(s) < 0) {
            last.right = new Node(s);
        }
    }

    @Override
    public boolean contains(String s) {
        if (this._root == null) {
            return false;
        }
        Node last = lastNode(this._root, s);
        if (last.s.compareTo(s) == 0) {
            return true;
        } else {
            return false; // FIXME: PART A
        }
    }

    @Override
    public List<String> asList() {
        return asList(this._root);
    }

    public ArrayList<String> asList(Node rootNode) {
        if (rootNode == null) {
            return new ArrayList<>();
        }
        if (rootNode.left != null) {
            asList(rootNode.left);
        }
        this._StringList.add(rootNode.s);
        if (rootNode.right != null) {
            asList(rootNode.right);
        }
        return this._StringList;
    }

    public Node get_root() {
        return _root;
    }

    /** If there is a node in the tree contains String s,return that node.
     * Otherwise, return the last node.
     * The rootNode can not be null
     */
    private Node lastNode(Node rootNode, String s){
        if (rootNode.s.compareTo(s) > 0 && rootNode.left != null) {
            return lastNode(rootNode.left, s);
        }
        else if (rootNode.s.compareTo(s) < 0 && rootNode.right != null) {
            return lastNode(rootNode.right, s);
        }
        return rootNode;
    }



    /** Represents a single Node of the tree. */
    static class Node {
        /** String stored in this Node. */
        private String s;
        /** Left child of this Node. */
        private Node left;
        /** Right child of this Node. */
        private Node right;

        public String getValue(){
            return this.s;
        }
        public Node getLeft(){
            return this.left;
        }
        public Node getRight(){
            return this.right;
        }
        /** Creates a Node containing SP. */
        Node(String sp) {
            s = sp;
        }
    }

    /** An iterator over BSTs. */
    private static class BSTIterator implements Iterator<String> {
        /** Stack of nodes to be delivered.  The values to be delivered
         *  are (a) the label of the top of the stack, then (b)
         *  the labels of the right child of the top of the stack inorder,
         *  then (c) the nodes in the rest of the stack (i.e., the result
         *  of recursively applying this rule to the result of popping
         *  the stack. */
        private Stack<Node> _toDo = new Stack<>();

        /** A new iterator over the labels in NODE. */
        BSTIterator(Node node) {
            addTree(node);
        }

        @Override
        public boolean hasNext() {
            return !_toDo.empty();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Node node = _toDo.pop();
            addTree(node.right);
            return node.s;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /** Add the relevant subtrees of the tree rooted at NODE. */
        private void addTree(Node node) {
            while (node != null) {
                _toDo.push(node);
                node = node.left;
            }
        }
    }

    @Override
    public Iterator<String> iterator() {
        return new BSTIterator(_root);
    }


    // FIXME: UNCOMMENT THE NEXT LINE FOR PART B
    @Override
    public Iterator<String> iterator(String low, String high) {
        Iterator<String> TreeIterator = iterator();
        ArrayList<String> StringInBound = new ArrayList<>();
        while (TreeIterator.hasNext()) {
            String next= TreeIterator.next();
            if (next.compareTo(low) >= 0 && next.compareTo(high) <= 0) {
                StringInBound.add(next);
            }
        }
        return StringInBound.iterator();  // FIXME: PART B (OPTIONAL)
    }


    /** Root node of the tree. */
    private Node _root;

    private ArrayList<String> _StringList;
}
