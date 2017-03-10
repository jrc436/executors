package nlp.rouge;

import nlp.StringPair;
import nlp.ngrams.INgramMaker;
import nlp.ngrams.AbstractNgramMaker;
import nlp.ngrams.SimpleNgramMaker;
import nlp.util.Tokenizer;
import optimizers.Variable;
import optimizers.VariableSet;

public class RougeN extends Rouge {
	private final Tokenizer token;
	private final AbstractNgramMaker gram;
	public RougeN(Tokenizer t, AbstractNgramMaker g) {
		super(new VariableSet(new Variable[0]));
		this.token = t;
		this.gram = g;
	}
	public RougeN(Tokenizer t, INgramMaker g, int ngramOrder) {
		this(t, AbstractNgramMaker.make(g, ngramOrder));
	}
	public RougeN(Tokenizer t) {
		this(t, new SimpleNgramMaker(1));
	}
	public RougeN() {
		this(Tokenizer::simpleTokenize);
		
	}
	public double n(String realization, String goal) {
		GramSet gsr = new GramSet(gram.makeAllNgrams(token.tokenize(realization)));
		GramSet gsg = new GramSet(gram.makeAllNgrams(token.tokenize(goal)));
		return ((double) gsg.setIntersect(gsr).size()) / ((double) gsg.size());
	}
	@Override
	public double evaluate(StringPair toEvaluate) {
		return n(toEvaluate.getProduced(), toEvaluate.getGold());
	}
}
