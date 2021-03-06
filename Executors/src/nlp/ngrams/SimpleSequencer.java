package nlp.ngrams;

import java.util.ArrayList;
import java.util.List;

import nlp.rouge.LCSequencer;
import nlp.util.Segmenter;
import nlp.util.Tokenizer;

public class SimpleSequencer extends LCSequencer {
	public SimpleSequencer(Segmenter s, Tokenizer t) {
		super(s, t);
	}
	public SimpleSequencer(Tokenizer t) {
		super(t);
	}
//	public SimpleSequencer() {
//		super();
//	}
	@Override
	public List<String> lcs(List<String> a, List<String> b) {
		List<String> start = new ArrayList<String>();
		List<String> end = new ArrayList<String>();
		if (a.isEmpty() || b.isEmpty()) {
			return start;
		}
		
		int sIdx = 0;
		int aEnd = a.size() - 1;
		int bEnd = b.size() - 1;
		
		while (sIdx < a.size() && sIdx < b.size() && a.get(sIdx).equals(b.get(sIdx))) {
			start.add(a.get(sIdx));
			sIdx++;
		}
		while (aEnd > 0 && bEnd > 0 && (a.get(aEnd).equals(b.get(bEnd)))) {
			end.add(a.get(aEnd));
			aEnd--;
			bEnd--;
		}
		List<String> trimmedA = null; 
		List<String> trimmedB = null; 
		try {
			trimmedA = a.subList(sIdx, aEnd+1);
			trimmedB = b.subList(sIdx, bEnd + 1);
		}
		catch (IllegalArgumentException i) {
			System.err.println(a);
			System.err.println(b);
			trimmedA = a;
			trimmedB = b;
		}
		for (int i = 0; i < trimmedB.size(); i++) {
			for (int j = 0; j < trimmedA.size(); j++) {
				if (trimmedB.get(i).equals(trimmedA.get(j))) {
					start.add(trimmedA.get(j));
				}
			}
		}
		start.addAll(end);
		return start;
	}
}
