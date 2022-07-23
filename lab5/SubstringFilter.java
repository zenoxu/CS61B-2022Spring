/**
 * TableFilter to filter for containing substrings.
 *
 * @author Matthew Owen
 */
public class SubstringFilter extends TableFilter {

    public SubstringFilter(Table input, String colName, String subStr) {
        super(input);
        // FIXME: Add your code here.
        this._colName = colName;
        this._subStr = subStr;
        this._table = input;
    }

    @Override
    protected boolean keep() {
        // FIXME: Replace this line with your code.
        int index = this._table.colNameToIndex(this._colName);
        if (this._next.getValue(index).contains(this._subStr)) {
            return true;
        }
        return false;
    }

    // FIXME: Add instance variables?
    private Table _table;
    private String _colName;
    private String _subStr;

}
