package nlp.ngrams;

import java.util.List;

public abstract class AbstractNgramMaker implements INgramMaker {
	private final int order;
	public AbstractNgramMaker(int order) {
		this.order = order;
	}
	public static AbstractNgramMaker make(INgramMaker g, int order) {
		return new AbstractNgramMaker(order) {
			public List<Ngram> makeAllNgrams(List<String> tokens, int order) {
				return g.makeAllNgrams(tokens, order);
			}
		};
	}
	public List<Ngram> makeAllNgrams(List<String> tokens) {
		return this.makeAllNgrams(tokens, order);
	}
}
