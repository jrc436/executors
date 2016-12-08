package optimizers;

import util.generic.data.GenericList;
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
//	public EvaluationList(File f) {
//		List<String> lines = null;
//		try {
//			lines = Files.readAllLines(f.toPath());
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit(1);
//		}
//		for (String line : lines) {
//			this.add(Evaluation.);
//		}
//	}
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
