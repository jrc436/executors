package nlp;

public class StringPair {
	private final String gold;
	private final String produced;
	public StringPair(String gold, String produced) {
		this.gold = gold;
		this.produced = produced;
	}
	public String getGold() {
		return gold;
	}
	public String getProduced() {
		return produced;
	}
}
