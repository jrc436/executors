package filter;

import java.util.ArrayList;
import java.util.List;

import util.sys.DataType;

//K is the individual data, E is the collection
public abstract class GenericFilter<K, E extends Filterable<K>> extends Filter {
	protected abstract FilterFunction<K> createFilter(Filterable<K> collection);
	@Override
	public void filter(DataType dt) {
		@SuppressWarnings("unchecked")
		E wm = (E) dt;
		FilterFunction<K> filt = createFilter(wm);
		applyFilter(wm, filt);
	}
	private void applyFilter(Filterable<K> wm, FilterFunction<K> filt) {
		List<K> mark = new ArrayList<K>();
		for (K word : wm) {
			if (!filt.good(word)) {
				mark.add(word);
			}
		}
		for (K w : mark) {
			wm.destroy(w);
		}
	}

}
