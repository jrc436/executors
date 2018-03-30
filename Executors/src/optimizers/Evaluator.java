package optimizers;

public abstract class Evaluator<K> {
	protected final VariableSet vs;
	public Evaluator(VariableSet vs) {
		this.vs = vs;
	}
	public abstract double evaluate(K toEvaluate);
}
