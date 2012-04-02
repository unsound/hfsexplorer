package javax.swing;

import java.util.Comparator;

public abstract class DefaultRowSorter<M,I>
extends RowSorter<M>
{
    public void setComparator(int column, Comparator<?> comparator) {
	throw new RuntimeException("Stub.");
    }

    public void toggleSortOrder(int column) {
	throw new RuntimeException("Stub.");
    }
}