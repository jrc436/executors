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
	public VariableSet(Variable[] vars) {
		this(vars, new Random().nextInt(vars.length));
	}
	public void acknowledgeImprovement() {
		this.lastSuperIterImproved = true;
	}
	public boolean step(boolean lastStepGood) {
		boolean toReturn = false;
		if (vars.length != 0) {
			try {
				toReturn = vars[currentIndex].step(lastStepGood);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.err.println("Inconsistent settlement!!");
				System.exit(1);
			}
		}
		return toReturn; 
	}
	protected Variable[] getVarArray() {
		return this.vars;
	}
	public double[] getDoubleArray() {
		double[] d = new double[vars.length];
		for (int i = 0; i < vars.length; i++) {
			d[i] = vars[i].getCurrentValue();
		}
		return d;
	}
	
	//return true if we've successfully incremented the index.
	//return false if the loop should break.
	protected boolean updateIndex(boolean curVarImproving) {
		if (curVarImproving) {
			return true; //don't need to update index!
		}
		//need to update index, we also might need to start a new superIter!
		if (currentIndex + 1 == initIndex) {
			return startNewSuperIter();
		}
		currentIndex = currentIndex + 1 == vars.length ? 0 : currentIndex + 1;
		return true;
	}
	private boolean startNewSuperIter() {
		if (!lastSuperIterImproved) {
			return false;
		}
		for (Variable v : vars) {
			v.unsettle();
		}
		this.currentIndex = initIndex;
		this.lastSuperIterImproved = false; //need to check if there's an improvement again
		return true;
	}
	public void randomAll() {
		for (int i = 0; i < vars.length; i++) {
			vars[i].resetValueRandom(); //this will produce a value between 0 and 1, so it has to be normalized with the range...
		}
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
		for (Variable v : vars) {
			s += v.toString() + varsplit;
		}
		return s.substring(0, s.length()-varsplit.length()) + suffix;
	}
}
