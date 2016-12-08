package nlp.rouge;

import java.util.List;

@FunctionalInterface
public interface LongestCommonSubsequence {
	public List<String> lcs(List<String> a, List<String> b);
}
