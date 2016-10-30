package util.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.sys.DataType;

public class TextList extends ArrayList<String> implements DataType {

	private static final long serialVersionUID = -813807579289923749L;
	public static TextList fromFile(File f) {
		try {
			return new TextList(Files.readAllLines(f.toPath()));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	public TextList() {
		super();
	}
	protected TextList(List<String> other) {
		super(other);
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
		return "TextList takes no arguments";
	}

	@Override
	public String getFileExt() {
		return ".txt";
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
		return super.iterator();
	}

	@Override
	public DataType deepCopy() {
		return new TextList(this);
	}

}
