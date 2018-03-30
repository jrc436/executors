package util.data.dsv.filter;

import java.util.List;
import java.util.Set;
import java.lang.IllegalArgumentException;

import filter.FilterFunction;
import filter.Filterable;
import filter.GenericFilter;
import util.data.dsv.ListDSV;

public class ValueMatchFilter extends GenericFilter<List<String>, ListDSV> {

	private final int columnIndex;
	private final String columnName;
	private final Set<String> desiredValues;
	public ValueMatchFilter(int columnIndex, Set<String> desiredValue) {
		this.columnIndex = columnIndex;
		this.desiredValues = desiredValue;
		this.columnName = null;
	}
	public ValueMatchFilter(String columnName, Set<String> desiredValues) {
		this.desiredValues = desiredValues;
		this.columnIndex = -1;
		this.columnName = columnName;
	}

	@Override
	protected FilterFunction<List<String>> createFilter(Filterable<List<String>> collection) {
		ListDSV ldsv = (ListDSV) collection;
		if (this.columnIndex == -1 && this.columnName == null) {
			throw new UnsupportedOperationException("the columnname or the index should always be specified");
		}
		else if (this.columnIndex == -1) {
			return getFilter(ldsv.getIndexForHeader(this.columnName));
		}
		return getFilter(this.columnIndex);
	}
	
	private boolean filterFunction(List<String> s, int columnIndex) {
		if (s.size() <= columnIndex || columnIndex < 0) {
			System.err.println("Requesting a non-present index!");
			System.err.println("List: "+s+"; index: "+columnIndex);
			if (columnName != null) {
				System.err.println("Requested column name: "+columnName);
			}
			throw new IllegalArgumentException();
		}
		return desiredValues.contains(s.get(columnIndex));
	}
	private FilterFunction<List<String>> getFilter(int columnIndex) {
		return new FilterFunction<List<String>>() {
			@Override
			public boolean good(List<String> entry) {
				return filterFunction(entry, columnIndex);
			}
		};
	}

}
