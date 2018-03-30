package nlp.ngrams;

import java.util.ArrayList;
import java.util.List;

public class SimpleSkipBigrams extends AbstractSkipgramMaker {
	public SimpleSkipBigrams() {
		super(2);
	}

	public static List<Ngram> skipBiGrams(List<String> tokens, int order) {
		if (order != 2) {
			throw new IllegalArgumentException("only supports bigrams");
		}
		List<Ngram> retval = new ArrayList<Ngram>();
		for (int i = 0; i < tokens.size() - 1; i++) {
			for (int j = i + 1; j < tokens.size(); j++) {
				List<String> tok = new ArrayList<String>();
				tok.add(tokens.get(i));
				tok.add(tokens.get(j));
				retval.add(new Ngram(2, tok));
			}
		}
		return retval;
	}
	@Override
	public List<Ngram> makeAllNgrams(List<String> tokens, int order) {
		if (order != 2) {
			throw new IllegalArgumentException("only supports bigrams");
		}
		return skipBiGrams(tokens, order);
	}
}
