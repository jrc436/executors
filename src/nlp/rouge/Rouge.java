package nlp.rouge;

import nlp.StringPair;
import optimizers.Evaluator;
import optimizers.VariableSet;

public abstract class Rouge extends Evaluator<StringPair> {
	protected static final double defaultBeta = 0.5;
	public Rouge(VariableSet vs) {
		super(vs);
		// TODO Auto-generated constructor stub
	}
	protected static double f(double p, double r, double beta) {
		if (r == 0 && p == 0) {
			return 0;
		}
		if (0 <= beta && beta <= 1) {
			return ((1 + beta * beta) * r * p) / (r + beta * beta * p);
		}
		return r;
	}
}
