package util.data.csv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.sys.DataType;
import util.sys.FileWritable;

public class ListCSV extends ArrayList<List<String>> implements DataType {
	/**
	 * 
	 */
	public ListCSV() {
		headers = null;
	}
	private static final long serialVersionUID = -4843965415284440893L;
	private final List<String> headers;
	public static ListCSV fromFile(File f) throws IOException {
		List<String> lines = Files.readAllLines(f.toPath());
		if (lines.isEmpty()) {
			throw new IllegalArgumentException(f + " doesn't contain any data");
		}
		String headers = lines.get(0);
		List<String> heads = new ArrayList<String>();
		for (String s : headers.split(",")) {
			heads.add(s);
		}
		ListCSV toret = new ListCSV(heads);
		for (int i = 1; i < lines.size(); i++) {
			toret.add(new ArrayList<String>());
			for (String s : lines.get(i).split(",")) {
				toret.get(i-1).add(s);
			}
		}
		return toret;
	}
	private ListCSV(List<String> heads) {
		this.headers = heads;
	}
	private ListCSV(ListCSV other) {
		super();
		this.headers = new ArrayList<String>(other.headers);
		for (List<String> data : other) {
			super.add(new ArrayList<String>(data));
		}
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
		return "ListCSV requires no args";
	}

	@Override
	public String getFileExt() {
		return ".csv";
	}

	@Override
	public ArrayList<String> getDataWriteLines() {
		ArrayList<String> ret = new ArrayList<String>();
		for (List<String> data : this) {
			ret.add(buildCSVString(data));
		}
		return ret;
	}

	@Override
	public String getHeaderLine() {
		return buildCSVString(headers);
	}

	@Override
	public String getFooterLine() {
		return null;
	}

	@Override
	public Iterator<String> getStringIter() {
		return FileWritable.<List<String>, ListCSV>iterBuilder(this, ListCSV::buildCSVString);
	}
	private static String buildCSVString(List<String> string) {
		String add = "";
		for (String s : string) {
			add += s + ",";
		}
		return add.substring(0, add.length()-1);
	}

	@Override
	public DataType deepCopy() {
		return new ListCSV(this);
	}

}
