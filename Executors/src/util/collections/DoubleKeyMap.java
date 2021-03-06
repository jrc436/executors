package util.collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DoubleKeyMap<E, V, K> extends HashMap<Pair<E, V>, K> {
	private static final long serialVersionUID = 2223800696872774323L;
	private final boolean symmetric;
	public DoubleKeyMap() {
		super();
		symmetric = false;
	}
	public DoubleKeyMap(DoubleKeyMap<E, V, K> dkm) {
		super(dkm);
		this.symmetric = dkm.symmetric;
	}
	public DoubleKeyMap(boolean symmetric) {
		super();
		this.symmetric = symmetric;
	}
	public void put(E key1, V key2, K val) {
		if (symmetric) {
			super.put(new UnorderedPair<E, V>(key1, key2), val);
		}
		else {
			super.put(new OrderedPair<E, V>(key1, key2), val);
		}
	}
	protected boolean isSymmetric() {
		return symmetric;
	}
	public void remove(E key1, V key2, boolean pairedRemove) {
		if (symmetric) {
			if (pairedRemove) {
				super.remove(new UnorderedPair<V, E>(key2, key1));
			}
			super.remove(new UnorderedPair<E, V>(key1, key2));
		}
		else {
			if (pairedRemove) {
				super.remove(new OrderedPair<V, E>(key2, key1));
			}
			super.remove(new OrderedPair<E, V>(key1, key2));
		}
	}
	public void purgeKey(E key1) {
		for (V key : this.getPairedKeys(key1)) {
			this.remove(key1, key, false);
		}
	}
	public void purgeKey2(V key2) {
		for (E key : this.getPairedKeys2(key2)) {
			this.remove(key, key2, false);
		}
	}
	public boolean containsKey(E key1, V key2) {
		if (symmetric) {
			return super.containsKey(new UnorderedPair<E, V>(key1, key2));
		}
		return super.containsKey(new OrderedPair<E, V>(key1, key2));
	}
	public K get(E key1, V key2) {
		if (symmetric) {
			return super.get(new UnorderedPair<E, V>(key1, key2));
		}
		return super.get(new OrderedPair<E, V>(key1, key2));
	}
	@SuppressWarnings("unchecked")
	public Set<E> getFullKeySet() { 
		Set<E> one = new HashSet<E>();
		for (Pair<E, V> p : super.keySet()) {
			if (!p.typeTwo.getClass().isAssignableFrom(p.typeOne.getClass())) {
				throw new UnsupportedOperationException("Type: "+p.typeTwo.getClass()+" does not match Type: "+p.typeOne.getClass());
			}
			one.add(p.typeOne);
			one.add((E) p.typeTwo);
		}
		return one;
	}
	public Set<E> getKeysetOne() {
		Set<E> one = new HashSet<E>();
		for (Pair<E, V> p : super.keySet()) {
			one.add(p.typeOne);
		}
		return one;
	}
	public Set<V> getKeysetTwo() {
		Set<V> two = new HashSet<V>();
		for (Pair<E, V> p : super.keySet()) {
			two.add(p.typeTwo);
		}
		return two;
	}
	
	@SuppressWarnings("unchecked")
	public Set<V> getPairedKeys(E key1) {
		Set<V> pairedKeys = new HashSet<V>();
		for (Pair<E, V> p : super.keySet()) {
			if (p.typeOne.equals(key1)) {
				pairedKeys.add(p.typeTwo);
			}
			if (symmetric && p.typeTwo.equals(key1)) {
				pairedKeys.add((V) p.typeOne);
			}
		}
		return pairedKeys;
	}
	@SuppressWarnings("unchecked")
	public Set<E> getPairedKeys2(V key2) {
		Set<E> pairedKeys = new HashSet<E>();
		for (Pair<E, V> p : super.keySet()) {
			if (p.typeTwo.equals(key2)) {
				pairedKeys.add(p.typeOne);
			}
			if (symmetric && p.typeOne.equals(key2)) {
				pairedKeys.add((E) p.typeTwo);
			}
		}
		return pairedKeys;
	}

}
