import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/** A set of String values.
 *  @author Ziyi Xu
 */
class ECHashStringSet implements StringSet {

    private LinkedList<String>[] buckets;

    private int _numElements;

    private ArrayList<String> _StringList;

    public ECHashStringSet(){
        int _default_num_bucket = 4;
        buckets = (LinkedList<String>[]) new LinkedList[_default_num_bucket];
        for (int i = 0; i < _default_num_bucket; i++) {
            buckets[i] = new LinkedList<>();
        }
        _numElements = 0;
        _StringList = new ArrayList<>();
    }

    private int getBucketIndex(String s) {
        return  (s.hashCode() & 0x7fffffff) % buckets.length;
    }

    @Override
    public void put(String s) {
        // FIXME
        if ((_numElements + 1)/buckets.length > 3) {
            resize();
        }
        buckets[getBucketIndex(s)].add(s);
        _numElements += 1;
    }

    public void resize() {
        LinkedList<String>[] newBuckets = (LinkedList<String>[]) new LinkedList[this.buckets.length * 2];
        for (int i = 0; i < newBuckets.length; i++) {
            newBuckets[i] = new LinkedList<>();
        }
        for (LinkedList<String> bucket : buckets) {
            for (String next : bucket) {
                int Index = (next.hashCode() & 0x7fffffff) % newBuckets.length;
                newBuckets[Index].add(next);
            }
        }
        this.buckets = newBuckets;
    }

    @Override
    public boolean contains(String s) {
        return buckets[getBucketIndex(s)].contains(s); // FIXME
    }

    @Override
    public List<String> asList() {
        for (LinkedList<String> bucket : this.buckets) {
            this._StringList.addAll(bucket);
        }
        return this._StringList; // FIXME
    }


    public Iterator<String> iterator(String low, String high) {
        ArrayList<String> temp = new ArrayList<>();
        for(String s: this.asList()) {
            if(s.compareTo(low) >= 0 && s.compareTo(high) <= 0) {
                temp.add(s);
            }
        }
        return temp.iterator();
    }

    public LinkedList<String>[] getBuckets() {
        return buckets;
    }
}
