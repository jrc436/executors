package optimizers;

public abstract class RsquaredEvaluator<K> extends Evaluator<K> {
	public RsquaredEvaluator(VariableSet vs) {
		super(vs);
	}
	protected abstract DoublePair getDoublePair(K dat);
	@Override
	public double evaluate(K dat) {
		DoublePair toEvaluate = getDoublePair(dat);
		return Math.pow(toEvaluate.getFirst() - toEvaluate.getSecond(), 2);
	}

}
