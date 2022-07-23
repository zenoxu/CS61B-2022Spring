/**
 * TableFilter to filter for entries greater than a given string.
 *
 * @author Matthew Owen
 */
public class GreaterThanFilter extends TableFilter {

    public GreaterThanFilter(Table input, String colName, String ref) {
        super(input);
        // FIXME: Add your code here.
        this._colName = colName;
        this._ref = ref;
        this._table = input;
    }

    @Override
    protected boolean keep() {
        // FIXME: Replace this line with your code.
        int index = this._table.colNameToIndex(this._colName);
        if (_next.getValue(index).compareTo(this._ref) > 0) {
            return true;
        }
        return false;
    }

    // FIXME: Add instance variables?
    private String _colName;
    private String _ref;
    private Table _table;
}

