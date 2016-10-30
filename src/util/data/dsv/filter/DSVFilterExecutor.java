package util.data.dsv.filter;

import util.data.dsv.ListDSV;
import util.sys.Executor;

public class DSVFilterExecutor extends Executor<DSVFilterProcessor, ListDSV, ListDSV> {

	public DSVFilterExecutor() {
		super("dsvfilter", 6, DSVFilterProcessor.class, ListDSV.class, ListDSV.class);
	}
	public static void main(String[] args) {
		DSVFilterExecutor ex = new DSVFilterExecutor();
		ex.initializeFromCmdLine(args);
		ex.run();
	}
}
