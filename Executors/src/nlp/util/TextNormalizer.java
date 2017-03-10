package nlp.util;

public class TextNormalizer {
	public static final String disfluencyString = "[disfluency]";
	public static String normalizeWord(String word) {
		if (word.equals(disfluencyString)) {
			return word;
		}
		String normalized = crushPCT(word.toLowerCase()).trim();
		if (normalized.isEmpty()) {
			return "";
		}
		if (!normalized.equals("<unk>") && !normalized.equals("<s>") && !normalized.equals("</s>") && !normalized.matches("[a-z\\d]+")) {
			throw new RuntimeException("This needs to be updated for a String like: "+word);
		}
		return normalized;
	}
	private static final String pcts = ".,:#&;-''{}!?()[]/_=";
	public static boolean isPCT(String inp) {
		for (int i = 0; i < inp.length(); i++) {
			if (!pcts.contains(""+inp.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	private static String crushPCT(String inp) {
		String crushed = "";
		for (int i = 0; i < inp.length(); i++) {
			if (!pcts.contains(""+inp.charAt(i))) {
				crushed += inp.charAt(i);
			}
		}
		return crushed;
	}
}
