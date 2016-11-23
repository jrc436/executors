package filter;

import util.collections.GenericIter;

public interface Filterable<K> extends GenericIter<K> {
	public void destroy(K el);
}
