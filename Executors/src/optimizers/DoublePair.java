package optimizers;

public class DoublePair {
	private final double first;
	private final double second;
	public DoublePair(double first, double second) {
		this.first = first;
		this.second = second;
	}
	public double getFirst() {
		return first;
	}
	public double getSecond() {
		return second;
	}
	/**
	 * Second minus first
	 * @return
	 */
	public double getDifference() {
		return second - first;
	}
	public static DoublePair fromString(String l) {
		String[] parts = l.split(",");
		double first = Double.parseDouble(parts[0]);
		double second = Double.parseDouble(parts[1]);
		return new DoublePair(first, second);
	}
	public String toString() {
		return first + "," + second;
	}
}
