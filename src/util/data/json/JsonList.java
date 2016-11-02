package util.data.json;

import java.util.ArrayList;
import java.util.Iterator;

import util.sys.DataType;

public class JsonList extends ArrayList<JsonReadable> implements DataType {
	public JsonList() {
		super();
	}
	public JsonList(JsonList jl) {
		super(jl);
	}
	private static final long serialVersionUID = -429282291590763784L;

	@Override
	public String getFileExt() {
		return ".json";
	}

	@Override
	public int getNumFixedArgs() {
		return 0;
	}

	@Override
	public String getConstructionErrorMsg() {
		return "No further arguments should be given";
	}

	@Override
	public ArrayList<String> getDataWriteLines() {
		return JsonLayer.collectJsons(this);
	}

	@Override
	public DataType deepCopy() {
		return new JsonList(this);
	}

	@Override
	public String getHeaderLine() {
		return "[";
	}
	@Override
	public String getFooterLine() {
		return "]";
	}
	@Override
	public String editFinalLine(String finalLine) {
		return finalLine.substring(0, finalLine.length()-1);
	}
	@Override
	public boolean hasNArgs() {
		return false;
	}
	@Override
	public Iterator<String> getStringIter() {
		JsonList outer = this;
		return new Iterator<String>() {
			Iterator<JsonReadable> jriter = outer.iterator(); 
			@Override
			public boolean hasNext() {
				return jriter.hasNext();
			}

			@Override
			public String next() {
				String ret = jriter.next().toString();
				if (hasNext()) {
					ret += ",";
				}
				return ret;
			}	
		};
	}

}
