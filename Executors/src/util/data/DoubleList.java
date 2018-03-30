package util.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import util.generic.data.GenericList;
import util.sys.DataType;

public class DoubleList extends GenericList<Double> {
	private static final long serialVersionUID = -167097531923716161L;

	public DoubleList(DoubleList other) {
		super(other);
	}
	public DoubleList() {
		super();
	}
	public DoubleList(File f) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(f.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		for (String line : lines) {
			this.add(Double.parseDouble(line));
		}
	}
	@Override
	public DataType deepCopy() {
		return new DoubleList(this);
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
		return "DoubleList requires no args";
	}

}
