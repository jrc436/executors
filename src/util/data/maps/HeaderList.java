package util.data.maps;

import java.util.ArrayList;

import util.collections.GenericList;
import util.sys.DataType;

public class HeaderList extends GenericList<String> {

	public HeaderList() {
		super();
	}
	public HeaderList(HeaderList other) {
		super(other);
	}
	private static final long serialVersionUID = -803974506259550847L;

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
		return "HeaderList requires no further arguments";
	}

	@Override
	public String getFileExt() {
		return ".headers";
	}

	@Override
	public ArrayList<String> getDataWriteLines() {
		return this;
	}

	@Override
	public String getHeaderLine() {
		return null;
	}

	@Override
	public String getFooterLine() {
		return null;
	}

	@Override
	public DataType deepCopy() {
		return new HeaderList(this);
	}

}
