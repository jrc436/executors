package filter;

import util.generic.data.GenericIter;

public interface Filterable<K> extends GenericIter<K> {
	public void destroy(K el);
}
