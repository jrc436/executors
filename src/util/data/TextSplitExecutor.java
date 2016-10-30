package util.data;

import util.sys.Executor;

public class TextSplitExecutor extends Executor<TextSplitter, TextList, TextList> {

	public TextSplitExecutor() {
		super("textsplit", 4, TextSplitter.class, TextList.class, TextList.class);
	}
	
	public static void main(String[] args) {
		TextSplitExecutor tse = new TextSplitExecutor();
		tse.initializeFromCmdLine(args);
		tse.run();
	}

}
