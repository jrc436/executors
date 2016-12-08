package util.generic.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.sys.FileWritable;
import util.sys.Stringify;

public abstract class GenericList<K> extends ArrayList<K> implements Stringify<K>, GenericIter<K> {

	private static final long serialVersionUID = -8008918485810085716L;

	public GenericList() {
		super();
	}
	public GenericList(GenericList<K> other) {
		super(other);
	}
	public GenericList(List<K> other) {
		super(other);
	}
	@Override
	public Iterator<String> getStringIter() {
		return FileWritable.<K, List<K>>iterBuilder(this, this::makeString);
	}
	@Override
	public String makeString(K obj) {
		return obj.toString();
	}
}
