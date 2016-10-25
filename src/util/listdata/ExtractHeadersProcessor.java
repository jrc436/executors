package util.listdata;

import java.io.File;

import util.sys.FileProcessor;

public class ExtractHeadersProcessor extends FileProcessor<UserList, HeaderList> {
	public ExtractHeadersProcessor() {
		super();
	}
	public ExtractHeadersProcessor(String inp, String out) {
		super(inp, out, new HeaderList());
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
		return "ExtractHeaders requires no arguments";
	}

	@Override
	public UserList getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		return UserList.createFromFile(f);
	}

	@Override
	public void map(UserList newData, HeaderList threadAggregate) {
		threadAggregate.addAll(newData.keySet());
	}

	@Override
	public void reduce(HeaderList threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.addAll(threadAggregate);
		}
	}

}
