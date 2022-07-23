/**
 * TableFilter to filter for entries equal to a given string.
 *
 * @author Matthew Owen
 */
public class EqualityFilter extends TableFilter {

    public EqualityFilter(Table input, String colName, String match) {
        super(input);
        // FIXME: Add your code here.
        this._colName = colName;
        this._match = match;
        this._table = input;
    }

    @Override
    protected boolean keep() {
        // FIXME: Replace this line with your code.
        int matchIndex = this._table.colNameToIndex(this._colName);
        if (_next.getValue(matchIndex).equals(this._match)) {
            return true;
        }
        return false;
    }

    // FIXME: Add instance variables?
    private String _colName;
    private String _match;
    private Table _table;
}
