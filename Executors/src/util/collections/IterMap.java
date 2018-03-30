package util.collections;

import java.util.HashMap;
import java.util.Iterator;

public class IterMap<K, V> extends HashMap<K, V> {
	public IterMap(IterMap<K, V> other) {
		super(other);
	}
	public IterMap() {
		super();
	}
	private static final long serialVersionUID = 7741382475976150472L;

	public Iterator<Entry<K, V>> iterator() {
		return this.entrySet().iterator();
	}
}
