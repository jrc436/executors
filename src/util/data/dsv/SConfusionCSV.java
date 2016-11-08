package util.data.dsv;

import util.sys.DataType;

public class SConfusionCSV extends ConfusionCSV<String> {


	public SConfusionCSV(String[] blargs) {
		super(blargs);
	}
	public SConfusionCSV(boolean symm) {
		super(symm);
	}
	public SConfusionCSV() {
		super(true);
	}
	private static final long serialVersionUID = 1423511210223449087L;

	public SConfusionCSV(ConfusionCSV<String> csv) {
		super(csv);
	}
	@Override
	public DataType deepCopy() {
		return new SConfusionCSV(this);
	}	
	
	@Override
	public int getNumFixedArgs() {
		return 0;
	}
}
