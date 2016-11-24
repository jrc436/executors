package optimizers;

import java.util.ArrayList;

public class VarValues extends ArrayList<Double> {

	private static final long serialVersionUID = -7044915094001395461L;
	public VarValues(double[] vals) {
		super();
		for (double d : vals) {
			super.add(d);
		}
	}
	@Override
	public int hashCode() {
		int tot = 0;
		for (int i = 0; i < this.size(); i++) {
			tot += (int)Math.round(this.get(i)*1000) *  (int) Math.pow(17, i+1);
		}
		return tot;
	}
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof VarValues)) {
			return false;
		}
		VarValues vv = (VarValues) other;
		if (vv.size() != this.size()) {
			return false;
		}
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i) != vv.get(i)) {
				return false;
			}
		}
		return true;
	}
}
