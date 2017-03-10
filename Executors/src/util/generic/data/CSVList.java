package util.generic.data;

import util.sys.DataType;

public class CSVList extends GenericList<String> {
	private static final long serialVersionUID = -6567351985636804291L;
	private final String[] headers;
	public CSVList() {
		super();
		headers = null;
	}
	public CSVList(String[] headers) {
		super();
		this.headers = headers;
	}
	public CSVList(CSVList other) {
		super(other);
		this.headers = other.headers;
	}
	@Override
	public String getHeaderLine() {
		return csvString(headers);
	}
	
	@Override
	public DataType deepCopy() {
		return new CSVList(this);
	}
	public void addRow(String[] rowData) {
		if (rowData.length != headers.length) {
			throw new IllegalArgumentException("Data mismatch. Given Row has : "+rowData.length+" columns, expected: "+headers.length);
		}
		super.add(csvString(rowData));
	}
	private String csvString(String[] row) {
		String retv = "";
		for (String header : row) {
			retv += header + ",";
		}
		if (row.length != 0) {
			retv = retv.substring(0, retv.length()-1);
		}
		return retv;
	}

	@Override
	public int getNumFixedArgs() {
		return 0;
	}

	@Override
	public boolean hasNArgs() {
		return true;
	}

	@Override
	public String getConstructionErrorMsg() {
		return "needs no args";
	}

}
