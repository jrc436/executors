package nlp.ngrams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import nlp.ngrams.NgramModel.NgramValue;
import nlp.util.Tokenizer;

//how does an ngram model work?
//Well!
//The probability of a given sentence is the joint probability of all of the ngrams in the sentence. Due to performance caps, we normally are really talking about
//5 grams. So given some sentence of length N, do a sliding window of 5grams and sum them. Okay, done.

//Okay, but how do we compute that 5gram probability if it's not there? Obviously, many 5grams probably won't have occurred in any given corpus, but that doesn't
//mean their probability is zero! So we have to compute some kind of estimate... 

//so we have A SINGLE basic functions: COMPUTE_JOINT_PROBABILITY [n | n-1...1]
//                                         1. If in table, lookup
//                                         2. If not in table, COMPUTE_JOINT_PROBABILITY [n | n-1...2] * BOW[n-1 | n-2 ... 1]
//                                                                            NOTE: this isn't SO different than just adding the fuckers, so long as you're consistent
//                                                                            SECONDNOTE: this should be the same process as when the order is too large, such as with a sentence
//                                                                            THIRDNOTE: the important points, is basically you're still just saying P(1...n-1) * P(2...n)... bow is a normalization scheme
 //never finished                                   
@Deprecated
public class NgramModel extends HashMap<Ngram, NgramValue> {
	private static final long serialVersionUID = 6596387879736780034L;
	private static final String unkString = "<unk>";
	private static final Ngram unkGram = new Ngram(1, Arrays.asList(unkString));
	private final int order;
	private final INgramMaker ngm;
	public NgramModel(int order, INgramMaker ngm) {
		this.order = order;
		this.ngm = ngm;
	}
	public NgramModel(int order) {
		this(order, SimpleNgramMaker::sgrammify);
	}
	public static NgramModel readFile(Path p) throws IOException {
		return readFile(p, SimpleNgramMaker::sgrammify);
	}
	public static NgramModel readFile(Path p, INgramMaker gram) throws IOException {
		List<String> lines = Files.readAllLines(p);
		NgramModel m = null;
		int currentOrder = 0;
		int maxOrder = 0;
		String ng = "ngram";
		for (String line : lines) {
			int nextOrder = currentOrder + 1;
			if (line.trim().isEmpty()) {
				continue;
			}
			else if (line.equals("\\data\\")) {
				System.out.println("Data found");
				continue;
			}
			else if (line.equals("\\end\\")) {
				System.out.println("End of file");
				continue;
			}
			else if (line.contains(ng) && m == null) { //this is just so it doesn't come back if any words have gram in it...
				String num = line.substring(line.indexOf(ng)+ng.length(), line.indexOf('='));
				int max = Integer.parseInt(num.trim());
				if (max > maxOrder) {
					maxOrder = max;
				}
				continue;
			}
			else if (line.contains("\\"+nextOrder+"-grams")) {
				//once we've reached the data, we know max is at max!
				if (m == null) {
					m = new NgramModel(maxOrder);
				}
				String chkNext = line.substring(line.indexOf('\\')+1,line.indexOf('-'));
				int next = Integer.parseInt(chkNext);
				if (next == nextOrder) {
					currentOrder++;
				}
				else {
					throw new RuntimeException("We seem to have missed some data. Skipping from: "+currentOrder+", to: "+next);
				}
				continue;
			}
			String[] parts = line.split("\\s+");
			if ((parts.length != maxOrder+1 && currentOrder == maxOrder) || (parts.length != currentOrder+2 && currentOrder < maxOrder)) {
				//System.err.println(line);
				//System.err.println("malformed input, attempting to fix..."+line);
				String[] newparts = new String[parts.length + 1];
				for (int i = 0; i < parts.length; i++) {
					newparts[i] = parts[i];
				}
				newparts[newparts.length-1] = newparts[0]; //just set the bow to the prob
				parts = newparts;
				if ((parts.length != maxOrder+1 && currentOrder == maxOrder) || (parts.length != currentOrder+2 && currentOrder < maxOrder)) {
					throw new IllegalArgumentException("malformed input, fix failed: "+line);
				}
			}
//			if (line.contains("<s>")) {
//				System.err.println("wiat!");
//			}
			//System.out.println(line);
			List<String> tokens = new ArrayList<String>();
			int end = currentOrder == maxOrder ? parts.length : parts.length-1;
			for (int i = 1; i < end; i++) {
				tokens.add(parts[i]);
			}
			double val = Double.parseDouble(parts[0]);
			double bow = parts.length > currentOrder + 1 ? Double.parseDouble(parts[parts.length-1]) : -99.0;
			m.put(new Ngram(currentOrder, tokens), new NgramValue(val, bow));
		}
		if (!m.containsKey(unkGram)) {
			throw new IllegalArgumentException("LM without unk won't work!");
		}
		return m;
	}
	public double logProb(Ngram g) {
		//test..
		double retval = super.get(g).get(false);
		if (g.getOrder() == 1) {
			return retval;
		}
		List<Ngram> ng = g.reduceNgram();
		for (Ngram n : ng) {
			retval += logProb(n);
		}
		return retval;
		
		
//		if (g.getOrder() > order) {
//			List<Ngram> grams = g.reduceNgram(order, ngm);
//			double retval = 0.0;
//			for (Ngram ng : grams) {
//				retval += logProb(ng);
//			}
//			return retval;
//		}
//		Ngram rel = g;
//		boolean found = this.containsKey(rel);
//		List<Ngram> reduced = new ArrayList<Ngram>();
//		while (!found) {
//			if (rel.getOrder() == 1) {
//				break;
//			}			
//			reduced = g.reduceNgram(rel.getOrder()-1);
//			rel = reduced.get(reduced.size()-1); //last ngram
//			found = super.containsKey(rel);
//		}
//		double retval = found ? super.get(rel).logProb : super.get(unkGram).logProb;
//		for (int i = 0; i < reduced.size()-1; i++) {
//			Ngram red = reduced.get(i);
//			double add = super.containsKey(red) ? super.get(red).normedLogProb : logProb(red);
//			retval += add;
//		}
//		return retval;
		//return logProb(reduced.get(0), false, nm) + logProb(reduced.get(1), true, nm);
		//the issue: if LHS is not found, then RHS should be zero. Instead, it will just add an additional BOW.
	}
	public double logProb(String s) {
		return logProb(putIntoSentence(s), Tokenizer::simpleTokenize, true);
	}
	public double logProb(String s, Tokenizer t, boolean inSentence) {
		if (s.isEmpty()) {
			return super.get(unkGram).get(false);
		}
		s = inSentence ? s : putIntoSentence(s);
		return logProb(Ngram.makeNgram(s, t));
	}
	public String putIntoSentence(String s) {
		return "<s> "+s+" </s>";
	}
	static class NgramValue {
		private final double logProb;
		private final double normedLogProb;
		private NgramValue(double logProb) {
			this.logProb = logProb;
			this.normedLogProb = -99;
		}
		private NgramValue(double logProb, double bow) {
			this.logProb = logProb;
			this.normedLogProb = bow;
		}
		public double get(boolean normed) {
			return normed ? normedLogProb : logProb;
		}
	}
}
