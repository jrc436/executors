package filter;

@FunctionalInterface
public interface FilterFunction<K> {
	public boolean good(K entry);
}
