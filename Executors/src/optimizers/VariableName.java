package optimizers;

public enum VariableName {
	negd,
	rt_intercept,
	lm_corr,
	lm_intercept,
	words_per_second,
	recencyK,
	cutoffK;
	public static VariableName fromString(String arg) {
		for (VariableName vn : VariableName.values()) {
			if (vn.toString().equals(arg)) {
				return vn;
			}
		}
		throw new IllegalArgumentException("String incorrectly represents a VariableName: "+arg);
	}
}
