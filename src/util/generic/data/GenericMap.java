package util.generic.data;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import util.collections.IterMap;
import util.sys.FileWritable;

public abstract class GenericMap<K, V> extends IterMap<K, V> implements GenericIter<Map.Entry<K, V>> {
	private static final long serialVersionUID = 6100449008579040545L;

	public GenericMap() {
		super();
	}
	public GenericMap(GenericMap<K, V> other) {
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
		return "GenericMaps in general don't require any further arguments, but this could be not fully specified";
	}
	protected String separator() {
		return "::";
	}
	protected String entryString(Entry<K, V> ent) {
		return ent.getKey() + separator() + ent.getValue();
	}
	@Override
	public Iterator<String> getStringIter() {
		 return FileWritable.<Entry<K, V>, Set<Entry<K, V>>>iterBuilder(this.entrySet(), this::entryString);
	}

}
