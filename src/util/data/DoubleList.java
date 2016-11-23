package util.data;

import util.collections.GenericList;
import util.sys.DataType;

public class DoubleList extends GenericList<Double> {
	private static final long serialVersionUID = -167097531923716161L;

	public DoubleList(DoubleList other) {
		super(other);
	}
	public DoubleList() {
		super();
	}
	@Override
	public DataType deepCopy() {
		return new DoubleList(this);
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
		return "DoubleList requires no args";
	}

}
