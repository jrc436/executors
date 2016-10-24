package filter;

import util.sys.DataType;

public interface Filterable<K> extends Iterable<K>, DataType {
	public void destroy(K el);
}
