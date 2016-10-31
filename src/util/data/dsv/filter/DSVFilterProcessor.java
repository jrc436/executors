package util.data.dsv.filter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import util.data.dsv.ListDSV;
import util.sys.FileProcessor;

public class DSVFilterProcessor extends FileProcessor<ListDSV, ListDSV> {
	private final String delimiter;
	private final ValueMatchFilter vmf;
	public DSVFilterProcessor() {
		super();
		this.vmf = null;
		this.delimiter = null;
	}
	public DSVFilterProcessor(String input, String output, String[] args) {
		super(input, output, new ListDSV(args[0]));		
		this.delimiter = args[0];
		String column = args[1];
		Set<String> acceptableValues = new HashSet<String>();
		for (int i = 2; i < args.length; i++) {
			acceptableValues.add(args[i]);
		}
		this.vmf = new ValueMatchFilter(column, acceptableValues);
	}
	
	@Override
	public int getNumFixedArgs() {
		return 2;
	}

	@Override
	public boolean hasNArgs() {
		return true;
	}

	@Override
	public String getConstructionErrorMsg() {
		return "DSVFilterProcessor will first take the delimiter, then the name of the column that's being filtered, and then the acceptable values as NARGS.";
	}

	@Override
	public ListDSV getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		try {
			return ListDSV.fromFile(f, delimiter);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	@Override
	public void map(ListDSV newData, ListDSV threadAggregate) {
		vmf.filter(newData);
		threadAggregate.absorb(newData);
	}

	@Override
	public void reduce(ListDSV threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.absorb(threadAggregate);
		}
	}

}
