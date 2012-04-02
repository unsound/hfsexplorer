package javax.swing.table;

import javax.swing.DefaultRowSorter;

public class TableRowSorter<M extends TableModel>
    extends DefaultRowSorter<M,Integer>
{
    public TableRowSorter(M model) {
	throw new RuntimeException("Stub.");
    }
}