package util.data;

import util.sys.DataType;
import util.sys.LineProcessor;

public abstract class BigDataSplitter<E extends DataType> extends LineProcessor<E, E> {
	private Integer filecounter;
	public BigDataSplitter() {
		filecounter = 0;
	}
	public BigDataSplitter(String inpDir, String outDir, E outtype) {
		super(inpDir, outDir, outtype);
		filecounter = 0;
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
		return "BigDataSplitters require no further arguments";
	}

	@Override
	public void map(E newData, E threadAggregate) {
		int filenum = 0;
		synchronized(filecounter) {
			filenum = filecounter;
			filecounter = this.writeData(newData, filenum);;
		}
	}
}
