package util.collections;

public abstract class Pair<K, V> {
	protected final K typeOne;
	protected final V typeTwo;
	protected Pair(K typeOne, V typeTwo) {
		this.typeOne = typeOne;
		this.typeTwo = typeTwo;
	}
	public String toString() {
		return "["+typeOne.toString()+","+typeTwo.toString()+"]";
	}
	public K one() {
		return typeOne;
	}
	public V two() {
		return typeTwo;
	}
}
