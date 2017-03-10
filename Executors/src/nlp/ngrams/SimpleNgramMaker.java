package nlp.ngrams;

import java.util.ArrayList;
import java.util.List;

public class SimpleNgramMaker extends AbstractNgramMaker {
	public SimpleNgramMaker(int order) {
		super(order);
	}
	public List<Ngram> makeAllNgrams(List<String> tokens, int order) {
		return sgrammify(tokens, order);
	}
	public static List<Ngram> sgrammify(List<String> tokens, int order) {
		List<Ngram> ngrams = new ArrayList<Ngram>();
		for (int i = 0; i < tokens.size(); i++) {
			if (i + order <= tokens.size()) {
				List<String> grams = new ArrayList<String>();
				for (int j = i; j < i + order; j++) {
					grams.add(tokens.get(j));
				}
				ngrams.add(new Ngram(order, grams));
			}
		}
		return ngrams;
	}
}
