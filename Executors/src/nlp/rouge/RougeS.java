package nlp.rouge;

import nlp.StringPair;
import nlp.ngrams.SimpleSkipBigrams;
import nlp.ngrams.AbstractSkipgramMaker;
import nlp.util.Tokenizer;
import optimizers.Variable;
import optimizers.VariableSet;

public class RougeS extends Rouge {
	private static final double defaultBeta = 0.5;
	private final Tokenizer token;
	private final AbstractSkipgramMaker sg;
	private final double beta;
	public RougeS(Tokenizer t, AbstractSkipgramMaker sg, double beta) {
		super(new VariableSet(new Variable[0]));
		this.token = t;
		this.sg = sg;
		this.beta = beta;
	}
	public RougeS(Tokenizer t) {
		this(t, new SimpleSkipBigrams(), defaultBeta);
	}
	public RougeS() {
		this(Tokenizer::simpleTokenize);
	}
	public double s(String realization, String goal) {
		GramSet gsr = new GramSet(sg.makeAllNgrams(token.tokenize(realization)));
		GramSet gsg = new GramSet(sg.makeAllNgrams(token.tokenize(goal)));
		int skip = gsg.setIntersect(gsr).size();
		if (skip == 0) {
			return 0;
		}
		double skipr = ((double) skip) / ((double) gsg.size());
		double skipp = ((double) skip) / ((double) gsr.size());
		return f(skipp, skipr, beta);
	}
	@Override
	public double evaluate(StringPair data) {
		return s(data.getProduced(), data.getGold());
	}
}
