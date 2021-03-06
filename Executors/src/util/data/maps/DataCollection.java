package util.data.maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import filter.Filterable;
import util.collections.IterMap;
import util.data.corpus.Comment;
import util.data.corpus.CommentFormat;

public abstract class DataCollection<E> extends IterMap<String, Collection<E>> implements Filterable<Entry<String, Collection<E>>> {

	private static final long serialVersionUID = 1046358937480295913L;
	protected CommentFormat cf = null;
	public DataCollection() { // dummy constructor
		super();
	}
	public DataCollection(String[] keywords) {
		super();
		for (String key : keywords) {
			super.put(key, getEmptyCollection());
		}
	}
	public DataCollection(DataCollection<E> kl) {
		super(kl);
		//this.cf = kl.cf;
	}
	protected DataCollection(List<String> fileLines, CommentFormat cf) {
		super();
		this.cf = cf;
		if (fileLines.size() == 0 || !isKeyLine(fileLines.get(0))) {
			throw new IllegalArgumentException("There needs to be at least one key line starting the file.");
		}
		String currentKeyLine = null;
		for (String s : fileLines) {
			currentKeyLine = addLine(s, currentKeyLine);
		}
	}
	protected DataCollection(List<String> fileLines) {
		this(fileLines, null);
	}
	public void destroy(Entry<String, Collection<E>> o) {
		this.remove(o.getKey());
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
		return "DataCollections vary on the number of arguments they specify. If you are seeing this message, it's possible your collection is not fully defined";
	}

	@Override
	public String getFileExt() {
		return ".list";
	}
	public void add(String keyword, E datum) {
		if (!this.containsKey(keyword)) {
			this.put(keyword, getEmptyCollection());
		}
		this.get(keyword).add(datum);
	}
	public void addComment(String keyword, Comment datum) {
		try {
			add(keyword, getValue(datum));
		}
		catch (UnsupportedOperationException e) {
			System.err.println("The individual DataCollection must implement Comment-based methods by overriding the defaults");
			throw e;
		}
	}
	protected abstract Collection<E> getEmptyCollection();
	public Collection<E> getCopyCollection(String key) {
		Collection<E> fe = getEmptyCollection();
		fe.addAll(this.get(key));
		return fe;
	}
	
	private static final String init = "?-? ";
	private static final String out = " !-!";
	
	private static String getKeyMarker(String origLine) {
		return init + origLine + out;
	}
	public static boolean isKeyLine(String line) {
		return line.length() >= init.length() + out.length() && line.substring(0, init.length()).equals(init) && line.substring(line.length()-out.length()).equals(out);
	}
	private static String returnFromMarker(String line) {
		return line.substring(init.length(), line.length()-out.length());
	}
	@SuppressWarnings("unchecked")
	private String addLine(String s, String currentKeyLine) {
		if (isKeyLine(s)) {
			super.put(returnFromMarker(s), getEmptyCollection());
			currentKeyLine = returnFromMarker(s);
		}
		else {
			E add = null;
			try {
				add = parseValue(s);
			}
			catch (UnsupportedOperationException e) {
				add = (E) s;
				//System.err.println("Warn: adding line directly because parseValue is unimplemented");
			}
			super.get(currentKeyLine).add(add);
		}
		return currentKeyLine;
	}
	protected E getValue(Comment c) {
		throw new UnsupportedOperationException();
	}
	protected E parseValue(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ArrayList<String> getDataWriteLines() {
		ArrayList<String> lines = new ArrayList<String>();
		for (String key : this.keySet()) {
			lines.add(getKeyMarker(key));
			for (E c : this.get(key)) {
				lines.add(c.toString());
			}
		}
		return lines;
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
		Iterator<String> keys = this.keySet().iterator();
		final DataCollection<E> outer = this;
		Iterator<String> iter = new Iterator<String>() {
			private Iterator<E> vals;
			
			public boolean hasNext() {
				return keys.hasNext() || (vals != null && vals.hasNext());
			}
			public String next() {
				if ((vals == null || !vals.hasNext()) && keys.hasNext()) {
					String key = keys.next();
					vals = outer.get(key).iterator();
					return getKeyMarker(key);
				}
				else if (vals != null && vals.hasNext()) { //we're going to return, so yeah!
					E val = vals.next();
					if (val == null) {
						return null;
					}
					return val.toString();
				} //neither keys nor vals have a next
				throw new NoSuchElementException();
			}
		};
		return iter;
	}
}
