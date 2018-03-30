package nlp.ngrams;

import java.util.List;

public abstract class AbstractSkipgramMaker extends AbstractNgramMaker {
	public AbstractSkipgramMaker(int order) {
		super(order);
	}
	public static AbstractSkipgramMaker make(INgramMaker g, int order) {
		return new AbstractSkipgramMaker(order) {
			public List<Ngram> makeAllNgrams(List<String> tokens, int order) {
				return g.makeAllNgrams(tokens, order);
			}
		};
	}
}
