package optimizers;

import util.collections.GenericList;
import util.sys.DataType;

public class EvaluationList extends GenericList<Evaluation> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5195974567495128854L;

	public EvaluationList() {
		super();
	}
	public EvaluationList(EvaluationList other) {
		super(other);
	}
	@Override
	public DataType deepCopy() {
		return new EvaluationList(this);
	}

	@Override
	public int getNumFixedArgs() {
		return 0;
	}

	@Override
	public boolean hasNArgs() {
		return false;
	}

	@Override
	public String getConstructionErrorMsg() {
		return "EvaluationList has no args";
	}

}
