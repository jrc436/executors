package optimizers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Evaluation {
	private final Map<VariableName, Double> vs;
	private final double score;
	public Evaluation(VariableSet vs, double score) {
		this.vs = new HashMap<VariableName, Double>();
		for (Variable v : vs.getVarArray()) {
			this.vs.put(v.getName(), v.getCurrentValue());
		}
		this.score = score;
	}
	public String toString() {
		return vs.toString()+":::"+score;
	}
	public static Comparator<Evaluation> sortScores() {
		return new Comparator<Evaluation>() {
			public int compare(Evaluation arg1, Evaluation arg0) {
				if (arg1.score > arg0.score) {
					return 1;
				}
				else if (arg1.score == arg0.score) {
					return 0;
				}
				return -1;
			}
		};
	}
	
}
