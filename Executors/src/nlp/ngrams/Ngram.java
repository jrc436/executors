package nlp.ngrams;

import java.util.ArrayList;
import java.util.List;

import nlp.util.Tokenizer;

public class Ngram  {
	private final int order;
	private final List<String> tokens;
	public Ngram(int order, List<String> tokens) {
		if (order != tokens.size()) {
			System.err.println("Order given: "+order+", tokens given: "+tokens);
			throw new IllegalArgumentException("This is not an Ngram because an Ngram should always have 'order' nubmer of tokens");
		}
		this.order = order;
		this.tokens = tokens;
	}
	public static Ngram makeNgram(String s, Tokenizer t) {
		List<String> tokens = new ArrayList<String>(t.tokenize(s));
		return new Ngram(tokens.size(), tokens);
	}
	public List<Ngram> reduceNgram(int newOrder, INgramMaker inm) {
		return inm.makeAllNgrams(tokens, newOrder);
	}
	public List<Ngram> reduceNgram(int newOrder) {
		return reduceNgram(newOrder, SimpleNgramMaker::sgrammify);
	}
	public List<Ngram> reduceNgram(INgramMaker inm) {
		return reduceNgram(order-1, inm);
	}
	public List<Ngram> reduceNgram() {
		return reduceNgram(order - 1, SimpleNgramMaker::sgrammify);
	}
	public int getOrder() {
		return order;
	}
	public String toString() {
		String retval = "";
		for (String s : tokens) {
			retval += s;
		}
		return retval;
	}
	@Override
	public int hashCode() {
		int retval = 0;
		for (int i = 0; i < tokens.size(); i++) {
			retval += tokens.get(i).hashCode() * (int)Math.pow(53, i);
		}
		return retval;
	}
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Ngram)) {
			return false;
		}
		Ngram o = (Ngram) other;
		if (o.order != this.order) {
			return false;
		}
		for (int i = 0; i < o.tokens.size(); i++) {
			if (!o.tokens.get(i).equals(this.tokens.get(i))) {
				return false;
			}
		}
		return true;
	}
}
