package util.sys;

import java.io.File;

public abstract class LineProcessor<K extends DataType, V extends DataType> extends FileProcessor<K, V> {
	public LineProcessor() {
		super();
	}
	public LineProcessor(String inpDir, String outDir) {
		super(inpDir, outDir);
	}
	public LineProcessor(String inpDir, String outDir, V startingVal) {
		super(inpDir, outDir, startingVal);
	}
	@Override
	public File write() {
		throw new UnsupportedOperationException("LineProcessors should have their individual threads write, which uses the overloaded writeData.");
	}
	@Override
	public void reduce(V threadAggregate) {
		//line processors have no meaningful reduce
	}

}
