package util.sys;

import java.util.ArrayList;
import java.util.Iterator;

public interface FileWritable extends IterStrings {
	public default String getFileExt() {
		return ".txt";
	}
	public default ArrayList<String> getDataWriteLines() {
		ArrayList<String> datalines = new ArrayList<String>();
		Iterator<String> iter = getStringIter();
		while (iter.hasNext()) {
			datalines.add(iter.next());
		}
		return datalines;
	}
	public default String getHeaderLine() {
		return null;
	}
	public default String getFooterLine() {
		return null;
	}
	/**
	 * sometimes the line before the footer has different properties, ala json
	 * @param finalLine
	 * @return
	 */
	public default String editFinalLine(String finalLine) {
		return finalLine;
	}
	public static <K, E extends Iterable<K>> Iterator<String> iterBuilder(E obj, Stringify<K> s) {
		Iterator<K> it = obj.iterator();
		Iterator<String> iter = new Iterator<String>() {
			public boolean hasNext() {
				return it.hasNext();
			}
			public String next() {
				K ob = it.next();
				return s.makeString(ob);
			}
		};
		return iter;
	}
	public static <K, E extends Iterable<K>> Iterator<String> iterBuilder(E obj) {
		return iterBuilder(obj, FileWritable::convertToString);
	}
	static <K> String convertToString(K type) {
		return type.toString();
	}
}
interface IterStrings {
	public Iterator<String> getStringIter();
}
