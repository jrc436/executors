package util.listdata;

import util.sys.Executor;

public class ExtractHeadersExecutor extends Executor<ExtractHeadersProcessor, UserList, HeaderList> {

	public ExtractHeadersExecutor() {
		super("extract", 10, ExtractHeadersProcessor.class, UserList.class, HeaderList.class);
	}
	public static void main(String[] args) {
		ExtractHeadersExecutor ehe = new ExtractHeadersExecutor();
		ehe.initializeFromCmdLine(args);
		ehe.run();
	}

}
