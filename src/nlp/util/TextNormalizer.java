package nlp.util;

public class TextNormalizer {
	public static String normalizeWord(String word) {
		String normalized = word.replace("[", "").replace("]", "").replace("-", "").toLowerCase();
		if (word.endsWith("'s")) {
			normalized = normalized.substring(0, normalized.length()-2);
		}
		if (!normalized.matches("[a-z]+")) {
			throw new RuntimeException("This needs to be updated for a String like: "+word);
		}
		return normalized;
	}
}
