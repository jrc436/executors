package nlp.pmi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nlp.util.TextNormalizer;
import util.collections.DoubleKeyMap;
import util.collections.Pair;
import util.sys.DataType;
import util.sys.FileWritable;

public class PMIDict extends DoubleKeyMap<String, String, Double> implements DataType {	
	private static final long serialVersionUID = 1061766326793040559L;
	
	public PMIDict() {
		super(true);
	}
	public PMIDict(PMIDict o) {
		super(o);
	}
	private static final String delim = "<><><>";
	public static PMIDict fromFile(File f, boolean original) throws IOException {
		if (original) {
			return fromFile(f);
		}
		List<String> lines = Files.readAllLines(f.toPath());
		PMIDict pmi = new PMIDict();
		for (String line : lines) {
			String[] parts = line.split("\\s+");
			List<String> realParts = new ArrayList<String>();
			for (String part : parts) {
				if (!part.isEmpty()) {
					realParts.add(part);
				}
			}
			if (realParts.size() != 3) {
				System.err.println("Skipping:" + line);
				continue;
			}
			try {
				double parse = Double.parseDouble(realParts.get(2));
				String part1 = realParts.get(0);
				String part2 = realParts.get(1);
				pmi.put(TextNormalizer.normalizeWord(part1), TextNormalizer.normalizeWord(part2), parse);
			}
			catch (NumberFormatException nfe) {
				System.err.println("Failed to parse double, skipping: "+line);
				continue;
			}
		}
		return pmi;
	}
	public static PMIDict fromFile(File f) throws IOException {
		List<String> lines = Files.readAllLines(f.toPath());
		PMIDict pmi = new PMIDict();
		for (String line : lines) {
			String[] parts = line.split(delim);
			pmi.put(parts[0], parts[1], Double.parseDouble(parts[2]));
		}
		return pmi;
	}
	public void fastAbsorb(PMIDict other) {
		super.putAll(other);
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
		return "PMIDict requires no arguments";
	}
	public String entryString(Pair<String, String> key) {
		return key.one() + delim + key.two() + delim + super.get(key);
	}

	@Override
	public Iterator<String> getStringIter() {
		return FileWritable.<Pair<String, String>, Set<Pair<String, String>>>iterBuilder(this.keySet(), this::entryString);
	}

	@Override
	public DataType deepCopy() {
		return new PMIDict(this);
	}

}
