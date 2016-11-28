package optimizers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Evaluation {
	//private final Map<VariableName, Double> vs;
	private final VariableSet vs;
	private final double score;
	public Evaluation(VariableSet vs, double score) {
		this.vs = vs;
		//for (Variable v : vs.getVarArray()) {
		//	this.vs.put(v.getName(), v.getCurrentValue());
		//}
		this.score = score;
	}
	public String toString() {
		String s = "";
		for (double d : vs.getDoubleArray()) {
			s += d+",";
		}
		s += score + delim + "full:";
		return s+vs.toString()+delim+score;
	}
	private static final String delim = ":::";
//	public static Evaluation fromString(String s) {
//		String[] parts = s.split(delim);
//		if (parts.length != 2) {
//			throw new IllegalArgumentException("String "+s+" does nto meaningfully encode a Evaluation. Not enough delims: "+delim);
//		}
//		
//	}
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
