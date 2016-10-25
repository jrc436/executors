package util.listdata;

import java.util.ArrayList;
import java.util.Iterator;

import util.sys.DataType;

public class HeaderList extends ArrayList<String> implements DataType {

	/**
	 * 
	 */
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
	public Iterator<String> getStringIter() {
		return this.iterator();
	}

	@Override
	public DataType deepCopy() {
		return null;
	}

}
