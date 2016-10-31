package util.data.dsv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import filter.Filterable;
import util.sys.DataType;
import util.sys.FileWritable;

//delimiter separated values
public class ListDSV extends ArrayList<List<String>> implements Filterable<List<String>> {
	/**
	 * 
	 */
	public ListDSV() {
		super();
		this.headers = null;
		this.splitter = null;
	}
	public ListDSV(String splitter) {
		super();
		this.splitter = splitter;
	}
	private static final long serialVersionUID = -4843965415284440893L;
	private List<String> headers;
	private final String splitter;
	public static ListDSV fromFile(File f, String splitter) throws IOException {
		List<String> lines = Files.readAllLines(f.toPath());
		if (lines.isEmpty()) {
			throw new IllegalArgumentException(f + " doesn't contain any data");
		}
		String headers = lines.get(0);
		List<String> heads = new ArrayList<String>();
		for (String s : headers.split(splitter)) {
			heads.add(s);
		}
		ListDSV toret = new ListDSV(heads, splitter);
		for (int i = 1; i < lines.size(); i++) {
			toret.add(new ArrayList<String>());
			for (String s : lines.get(i).split(splitter)) {
				toret.get(i-1).add(s);
			}
		}
		return toret;
	}
	private ListDSV(List<String> heads, String splitter) {
		this.headers = heads;
		this.splitter = splitter;
	}
	private ListDSV(ListDSV other) {
		super();
		this.headers = other.headers == null ? null : new ArrayList<String>(other.headers);
		this.splitter = other.splitter;
		for (List<String> data : other) {
			super.add(new ArrayList<String>(data));
		}
	}
	public void absorb(ListDSV other) {
		if (this.headers == null) {
			this.headers = new ArrayList<String>();
			this.headers.addAll(other.headers);
		}
		else if (this.headers.size() != other.headers.size()) {
			throw new IllegalArgumentException("Non-header aligned CSVs cannot be filtered using this system");
		}
		this.addAll(other);
	}
	public int getIndexForHeader(String header) {
		for (int i = 0; i < headers.size(); i++) {
			if (headers.get(i).equals(header)) {
				return i;
			}
		}
		return -1;
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
		return FileWritable.<List<String>, ListDSV>iterBuilder(this, this::buildCSVString);
	}
	private String buildCSVString(List<String> string) {
		String add = "";
		for (String s : string) {
			add += s + splitter;
		}
		return add.substring(0, add.length()-splitter.length());
	}

	@Override
	public DataType deepCopy() {
		return new ListDSV(this);
	}
	@Override
	public void destroy(List<String> el) {
		super.remove(el);
	}

}
