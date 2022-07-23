import org.w3c.dom.Node;

/**
 * Scheme-like pairs that can be used to form a list of integers.
 *
 * @author P. N. Hilfinger; updated by Linda Deng (1/26/2022)
 */
public class IntDList {

    /**
     * First and last nodes of list.
     */
    protected DNode _front, _back;

    /**
     * An empty list.
     */
    public IntDList() {
        _front = _back = null;
    }

    /**
     * @param values the ints to be placed in the IntDList.
     */
    public IntDList(Integer... values) {
        _front = _back = null;
        for (int val : values) {
            insertBack(val);
        }
    }

    /**
     * @return The first value in this list.
     * Throws a NullPointerException if the list is empty.
     */
    public int getFront() {
        return _front._val;
    }

    /**
     * @return The last value in this list.
     * Throws a NullPointerException if the list is empty.
     */
    public int getBack() {
        return _back._val;
    }

    /**
     * @return The number of elements in this list.
     */
    public int size() {
        // TODO: Implement this method and return correct value
        int size_list = 0;
        DNode check_pointer = this._front;
        while(check_pointer != null){
            size_list += 1;
            check_pointer = check_pointer._next;
        }
        return size_list;
    }

    /**
     * @param index index of node to return,
     *          where index = 0 returns the first node,
     *          index = 1 returns the second node, and so on.
     *          You can assume index will always be a valid index,
     *              i.e 0 <= index < size.
     * @return The node at index index
     */
    private DNode getNode(int index) {
        // TODO: Implement this method and return correct node
        DNode pointer = this._front;
        int i = 0;
        while(i<index){
            i += 1;
            pointer = pointer._next;
        }
        return pointer;
    }

    /**
     * @param index index of element to return,
     *          where index = 0 returns the first element,
     *          index = 1 returns the second element,and so on.
     *          You can assume index will always be a valid index,
     *              i.e 0 <= index < size.
     * @return The integer value at index index
     */
    public int get(int index) {
        // TODO: Implement this method (Hint: use `getNode`)
        return this.getNode(index)._val;
    }

    /**
     * @param d value to be inserted in the front
     */
    public void insertFront(int d) {
        // TODO: Implement this method
        DNode Node_insert = new DNode(d);
        if(this._back == null && this._front == null){
            this._front = this._back = Node_insert;
        }else{
            Node_insert._next = this._front;
            this._front._prev = Node_insert;
            this._front = Node_insert;
        }
        return;
    }

    /**
     * @param d value to be inserted in the back
     */
    public void insertBack(int d) {
        // TODO: Implement this method
        DNode Node_insert = new DNode(d);
        if(this._back == null){
            this._front = Node_insert;
            this._back = Node_insert;
        }else{/*The LinkedList is not empty */
            this._back._next = Node_insert;
            Node_insert._prev = this._back;
            this._back = Node_insert;
        }
        return;
    }

    /**
     * @param d     value to be inserted
     * @param index index at which the value should be inserted
     *              where index = 0 inserts at the front,
     *              index = 1 inserts at the second position, and so onh.
     *              You can assume index will always be a valid index,
     *              i.e 0 <= index <= size.
     */
    public void insertAtIndex(int d, int index) {
        // TODO: Implement this method
        DNode Node_insert = new DNode(d);
        if(this.size() == 0){
            this._front = this._back = Node_insert;
        }
        else if(index == 0){/* insert at the front */
            Node_insert._next = this._front;
            this._front._prev = Node_insert;
            this._front = Node_insert;
        }
        else if(index == this.size()){ /* insert at the last position */
            this._back._next = Node_insert;
            Node_insert._prev = this._back;
            this._back = Node_insert;
        }else{
            DNode pre_Node = getNode(index - 1);
            DNode next_Node = getNode(index);
            Node_insert._prev = pre_Node;
            Node_insert._next = next_Node;
            next_Node._prev = pre_Node._next = Node_insert;
        }
        return;
    }

    /**
     * Removes the first item in the IntDList and returns it.
     * Assume `deleteFront` is never called on an empty IntDList.
     *
     * @return the item that was deleted
     */
    public int deleteFront() {
        // TODO: Implement this method and return correct value
        int value = this._front._val;
        this._front = this._front._next;
        return value;
    }

    /**
     * Removes the last item in the IntDList and returns it.
     * Assume `deleteBack` is never called on an empty IntDList.
     *
     * @return the item that was deleted
     */
    public int deleteBack() {
        // TODO: Implement this method and return correct value
        int value = this._back._val;
        if(this._back._prev == null){
            this._front = null;
            this._back = null;
        }else{
            this._back = this._back._prev;
            this._back._next = null;
        }
        return value;
    }

    /**
     * @param index index of element to be deleted,
     *          where index = 0 returns the first element,
     *          index = 1 will delete the second element, and so on.
     *          You can assume index will always be a valid index,
     *              i.e 0 <= index < size.
     * @return the item that was deleted
     */
    public int deleteAtIndex(int index) {
        // TODO: Implement this method and return correct value
        return 0;
    }

    /**
     * @return a string representation of the IntDList in the form
     * [] (empty list) or [1, 2], etc.
     * Hint:
     * String a = "a";
     * a += "b";
     * System.out.println(a); //prints ab
     */
    public String toString() {
        // TODO: Implement this method to return correct value
        if (size() == 0) {
            return "[]";
        }
        String str = "[";
        DNode curr = _front;
        for (; curr._next != null; curr = curr._next) {
            str += curr._val + ", ";
        }
        str += curr._val +"]";
        return str;
    }

    /**
     * DNode is a "static nested class", because we're only using it inside
     * IntDList, so there's no need to put it outside (and "pollute the
     * namespace" with it. This is also referred to as encapsulation.
     * Look it up for more information!
     */
    static class DNode {
        /** Previous DNode. */
        protected DNode _prev;
        /** Next DNode. */
        protected DNode _next;
        /** Value contained in DNode. */
        protected int _val;

        /**
         * @param val the int to be placed in DNode.
         */
        protected DNode(int val) {
            this(null, val, null);
        }

        /**
         * @param prev previous DNode.
         * @param val  value to be stored in DNode.
         * @param next next DNode.
         */
        protected DNode(DNode prev, int val, DNode next) {
            _prev = prev;
            _val = val;
            _next = next;
        }
    }

}
