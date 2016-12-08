package nlp.rouge;

import java.util.List;

import nlp.StringPair;
import nlp.ngrams.SimpleSequencer;
import nlp.util.Segmenter;
import nlp.util.Tokenizer;
import optimizers.Variable;
import optimizers.VariableSet;

public class RougeL extends Rouge {
	private final Tokenizer token;
	private final LCSequencer lcs;
	private final double beta;
	public RougeL(Tokenizer t, double beta, LCSequencer lcs) {
		super(new VariableSet(new Variable[0]));
		this.token = t;
		this.beta = beta;
		this.lcs = lcs;
	}
	public RougeL(Tokenizer t) {
		this(t, defaultBeta, new SimpleSequencer(t));
	}
	public RougeL(Tokenizer t, double beta) {
		this(t, beta, new SimpleSequencer(t));
	}
	public RougeL(Tokenizer t, LongestCommonSubsequence lcs, Segmenter s) {
		this(t, defaultBeta, LCSequencer.makeLCS(lcs, s, t));
	}
	public RougeL() {
		this(Tokenizer::simpleTokenize);
		
	}
	
	/**
	 * Important note, this treats all text as a *single* sentence unless your LCS function preprocesses somehow
	 * @param realization
	 * @param goal
	 * @return
	 */
	public double l(String realization, String goal) {
		List<Integer> lcsAcc = lcs.lcsAcc(realization, goal);
		int sum = 0;
		for (Integer i : lcsAcc) {
			sum+= i;
		}
		double lcsr = ((double) sum) / ((double) token.tokenize(realization).size());
		double lcsp = ((double) sum) / ((double) token.tokenize(goal).size());
		double retval = Rouge.f(lcsp, lcsr, beta);
		if (Double.isNaN(retval)) {
			System.err.println("lol!");
		}
		return retval;
	}

	@Override
	public double evaluate(StringPair data) {
		return l(data.getProduced(), data.getGold());
	}
}
