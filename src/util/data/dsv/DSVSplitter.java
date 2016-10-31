package util.data.dsv;

import java.io.File;
import java.io.IOException;

import util.data.BigDataSplitter;

public class DSVSplitter extends BigDataSplitter<ListDSV> {
	private final String delim;
	public DSVSplitter() {
		super();
		delim = null;
	}
	public DSVSplitter(String input, String output, String[] args) {
		super(input, output, new ListDSV(args[0]));
		delim = args[0];
	}
	@Override
	public ListDSV getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		try {
			return ListDSV.fromFile(f, delim);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	@Override
	public String getConstructionErrorMsg() {
		return "DsvSplitter needs the delimiter as a fixedarg";
	}
	@Override
	public int getNumFixedArgs() {
		return 1;
	}

}
