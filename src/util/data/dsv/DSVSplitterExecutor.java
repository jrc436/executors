package util.data.dsv;

import util.sys.Executor;

public class DSVSplitterExecutor extends Executor<DSVSplitter, ListDSV, ListDSV> {

	public DSVSplitterExecutor() {
		super("dsvsplit", 30, DSVSplitter.class, ListDSV.class, ListDSV.class);
	}
	public static void main(String[] args) {
		DSVSplitterExecutor dse = new DSVSplitterExecutor();
		dse.initializeFromCmdLine(args);
		dse.run();
	}

}
