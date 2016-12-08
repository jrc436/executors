package nlp.rouge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import nlp.ngrams.AbstractNgramMaker;
import nlp.ngrams.Ngram;
import nlp.util.Tokenizer;

public class GramSet extends HashSet<Ngram> {
	private static final long serialVersionUID = -8338694341574055200L;
//	private final Tokenizer t;
//	private final INgramMaker g;
	public GramSet(List<String> sentences, Tokenizer t, AbstractNgramMaker g) {
		this();
		addSentences(sentences, t, g);
	}
	public GramSet() {
		super();
	}
	public GramSet(List<Ngram> gramsToAdd) {
		addGrams(gramsToAdd);
	}
	public void addGrams(List<Ngram> grams) {
		this.addAll(grams);
	}
	public void addGrams(GramSet grams) {
		this.addAll(grams);
	}
	private void addSentences(List<String> sentences, Tokenizer t, AbstractNgramMaker g) {
		for (String s : sentences) {
			List<Ngram> words = g.makeAllNgrams(t.tokenize(s));
			super.addAll(words);
		}
	}
	public boolean isSuperSet(GramSet ws) {
		boolean containsAll = true;
		for (Ngram s : ws) {
			containsAll = containsAll && this.contains(s);
		}
		return containsAll;
	}
	public GramSet setDiff(GramSet ws) {
		List<Ngram> words = new ArrayList<Ngram>();
		for (Ngram s : ws) {
			if (!this.contains(s)) {
				words.add(s);
			}
		}
		return new GramSet(words);
	}
	public GramSet setIntersect(GramSet ws) {
		List<Ngram> sharedGrams = new ArrayList<Ngram>();
		for (Ngram s : this) {
			if (ws.contains(s)) {
				sharedGrams.add(s);
			}
		}
		return new GramSet(sharedGrams);
	}
	public boolean isSubSet(GramSet ws) {
		boolean containsAll = true;
		for (Ngram s : this) {
			containsAll = containsAll && ws.contains(s);
		}
		return containsAll;
	}
}
