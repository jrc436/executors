package optimizers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class VariableSet {
	private final Variable[] vars;
	private final Map<VariableName, Variable> nameMap;
	private int currentIndex;
	private final int initIndex;
	private boolean lastSuperIterImproved;
	
	public VariableSet(Variable[] vars, int initIndex) {
		if (vars.length == 0) {
			throw new IllegalArgumentException("A zero length VariableSet isn't much of a set.");
		}
		this.vars = vars;
		this.nameMap = new HashMap<VariableName, Variable>();
		for (Variable v : vars) {
			if (nameMap.containsKey(v.getName())) {
				throw new IllegalArgumentException("Variable names should be unique");
			}
			nameMap.put(v.getName(), v);
		}
		this.currentIndex = initIndex;
		this.initIndex = initIndex;
		this.lastSuperIterImproved = true;	
	}
	public double getValue(VariableName vn) {
		if (!nameMap.containsKey(vn)) {
			System.err.println(nameMap);
			throw new RuntimeException("getValue cannot fail, key is not contained!");
		}
		return nameMap.get(vn).getCurrentValue();
	}
	public String getVarName(int ind) {
		return vars[ind].getName().toString();
	}
	public VariableSet(Variable[] vars) {
		this(vars, new Random().nextInt(vars.length));
	}
	public boolean checkLastSuperIterImproved() {
		return this.lastSuperIterImproved;
	}
	public void acknowledgeImprovement() {
		this.lastSuperIterImproved = true;
	}
	public int getStartingIndex() {
		if (currentIndex != initIndex) {
			System.out.println("WARN: current index is no longer starting index, cur:"+currentIndex+"; start:"+initIndex);
		}
		return initIndex;
	}
	public boolean tryStep(boolean lastStepGood) {
		return vars[currentIndex].tryStep(lastStepGood);
		
	}
	public int getNumVars() {
		return vars.length;
	}
	//protected Variable[] getVarArray() {
	//	return this.vars;
	//}
	public double[] getDoubleArray() {
		double[] d = new double[vars.length];
		for (int i = 0; i < vars.length; i++) {
			d[i] = vars[i].getCurrentValue();
		}
		return d;
	}
//	public int getCurIndex() {
//		return currentIndex;
//	}
	
	//return true if we've successfully incremented the index.
	//return false if this super iter is over and it didn't contain an improvement
	protected int updateIndex() {
		this.currentIndex = nextIndex(currentIndex);
		return this.currentIndex;
		//if (curVarImproving) {
		//	return true; //don't need to update index!
		//}
		//need to update index, we also might need to start a new superIter!
		//if (nextIndex(currentIndex) == initIndex) {
		//	return startNewSuperIter();
		//}
		//0, 3, 1, 0, 
		//currentIndex = nextIndex(currentIndex);
		//return true;
	}
	public boolean onLastIndex() {
		return nextIndex(currentIndex) == initIndex;
	} 
	private int nextIndex(int ind) {
		return ind + 1 == vars.length ? 0 : ind + 1;
	}
	protected int startNewSuperIter() {
		//if (!lastSuperIterImproved) {
		//	return false;
		//}
		for (Variable v : vars) {
			v.unsettle();
		}
		this.currentIndex = initIndex;
		this.lastSuperIterImproved = false; //need to check if there's an improvement again
		return this.currentIndex;
	}
	public int randomAll() {
		for (int i = 0; i < vars.length; i++) {
			vars[i].resetValueRandom(); 
		}
		this.currentIndex = initIndex;
		return this.currentIndex;
	}
	protected void forceAll(double[] vals) {
		for (int i = 0; i < vars.length; i++) {
			vars[i].forceCurrentValue(vals[i]);
		}
	}
	public static VariableSet fromString(String s) {
		String begin = s.substring(0, prefix.length());
		String end = s.substring(s.length()-suffix.length());
		if (!begin.equals(prefix) || !end.equals(suffix)) {
			throw new IllegalArgumentException("Malformatted variable set, should begin with: "+prefix+" and end with "+suffix);
		}
		String varset = s.substring(begin.length(), s.length()-suffix.length()-1);
		String[] svars = varset.split(varsplit);
		Variable[] vars = new Variable[svars.length];
		for (int i = 0; i < svars.length; i++) {
			vars[i] = Variable.fromString(svars[i]);
		}
		return new VariableSet(vars);
	}
	private static final String prefix = "vars:[";
	private static final String suffix = "]";
	private static final String varsplit = ",";
	@Override
	public String toString() {
		String s = prefix;
		for (int i = 0; i < vars.length; i++) {
			s += vars[i].toString() + varsplit;
		}
		return s.substring(0, s.length()-varsplit.length()) + suffix;
	}
}
