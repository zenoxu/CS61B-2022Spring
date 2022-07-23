/**
 * TableFilter to filter for entries whose two columns match.
 *
 * @author Matthew Owen
 */
public class ColumnMatchFilter extends TableFilter {

    public ColumnMatchFilter(Table input, String colName1, String colName2) {
        super(input);
        // FIXME: Add your code here.
        this._colName1 = colName1;
        this._colName2 = colName2;
        this._table = input;
    }

    @Override
    protected boolean keep() {
        // FIXME: Replace this line with your code.
        int index1 = this._table.colNameToIndex(this._colName1);
        int index2 = this._table.colNameToIndex(this._colName2);
        if (_next.getValue(index1).equals(_next.getValue(index2))) {
            return true;
        }
        return false;
    }

    // FIXME: Add instance variables?
    private String _colName1;
    private String _colName2;
    private Table _table;
}
