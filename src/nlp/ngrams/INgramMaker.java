package nlp.ngrams;

import java.util.List;

@FunctionalInterface
public interface INgramMaker {
	/**
	 * Transforms tokens into ngrams. 
	 * @param tokens
	 * A list of tokens, created by some Tokenizer
	 * @param order
	 * The order the ngrams should be of
	 * @return
	 * a list of ngrams of the specified order
	 */
	public List<Ngram> makeAllNgrams(List<String> tokens, int order);
}
