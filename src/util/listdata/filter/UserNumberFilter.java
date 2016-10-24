package util.listdata.filter;

import java.util.Collection;
import java.util.Map.Entry;

import filter.FilterFunction;
import filter.Filterable;
import filter.GenericFilter;
import util.listdata.UserList;

public class UserNumberFilter extends GenericFilter<Entry<String, Collection<String>>, UserList> {
	private final int prune = 500;

	
	private boolean good(Entry<String, Collection<String>> entry) {
		return entry.getValue().size() >= prune;
	}

	@Override
	protected FilterFunction<Entry<String, Collection<String>>> createFilter(Filterable<Entry<String, Collection<String>>> collection) {
		return this::good;
	}
	


}
