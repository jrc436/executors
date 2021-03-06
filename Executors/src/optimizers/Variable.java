package optimizers;

import java.util.Random;

//a variable to be used for simple hill climbing
public class Variable {
	private double upperBound;
	private double increment;
	private double lowerBound;
	private double currentValue;
	private double lastValue;
	private boolean settledP; //no more improvements in the P direction
	private boolean settledN; //no more improvements in the N direction
	private double direction; //1 means moving in the pos direction, -1 means moving in the negative direction
	private boolean firstRun;
	private final VariableName name;
	public Variable(VariableName name, double initialValue, double lowerBound, double upperBound, double increment) {
		this.name = name;
		if (upperBound < lowerBound || initialValue > upperBound || initialValue < lowerBound) {
			throw new IllegalArgumentException("This variable's specified initial value isn't between the bounds!");
		}
		this.upperBound = upperBound;
		this.increment = increment;
		this.lowerBound = lowerBound;
		this.currentValue = initialValue;
		this.lastValue = 0.0;
		this.firstRun = true;
		this.settledP = false;
		this.settledN = false;
		this.direction = new Random().nextBoolean() ? 1.0 : -1.0;
	}
	public VariableName getName() {
		return name;
	}
	private static final String split = ":";
	public String toString() {
		 return this.getName()+split+this.getCurrentValue()+split+lowerBound+split+upperBound+split+increment;
	}
	public static Variable fromString(String v) {
		String[] parts = v.split(split);
		String name = parts[0];
		double curval = Double.parseDouble(parts[1]);
		double lowerbound = Double.parseDouble(parts[2]);
		double upperbound = Double.parseDouble(parts[3]);
		double increment = Double.parseDouble(parts[4]);
		return new Variable(VariableName.fromString(name), curval, lowerbound, upperbound, increment);
		
	}
	public double normalizeInRange(double toNormalize) {
		int numIncrements = (int) Math.round(toNormalize / increment);
		double retval = numIncrements * increment;
		return retval;
	}
	public double getCurrentValue() {
		return normalizeInRange(currentValue);
	}
	protected void forceCurrentValue(double newValue) {
		this.currentValue = normalizeInRange(newValue);
		this.firstRun = true;
	}
	protected void resetValueRandom() {
		double newVal = this.lowerBound + (new Random().nextDouble() * (this.upperBound-this.lowerBound));
		this.currentValue = normalizeInRange(newVal);
		this.unsettle();
	}
	//if lastvalue is not set, then goodincr doesn't matter, we'll use whatever direction (typo caught by jamie) was initially set to
	public boolean tryStep(boolean goodIncr) {
		if (this.firstRun) {
			this.firstRun = false;
			increment();
			return checkBounds();
		}
		//if the increment is good, do it again!
		if (goodIncr) {
			//if it's good, then we can "settle" the other way now, to be simple. It's possible (but unlikely) that we're in a minima,
			//which would make this strategy not work.
			settleOppositeDirection();
			//now that we've settled it, we should perform the increment again
			increment();
			return checkBounds();
		}		
		else {
			//reset since we had a decrease
			this.currentValue = this.lastValue;
			settleCurrentDirection();
			if (settled()) {
				return false;
			}		
			//otherwise, changedirection and keep going
			
			changeDirection();
			increment();
			return checkBounds();
		}
		//we return false if the variable becomes settled or it's out of bounds. In either way, this variable is done for that superiter.
	}
	private void settleOppositeDirection() {
		changeDirection();
		settleCurrentDirection();
		changeDirection();
	}
	private void settleCurrentDirection() {
		if (direction < 0.0) {
			settledN = true;
		}
		else {
			settledP = true;
		}
	}
	private boolean settled() {
		return settledN && settledP;
	}
	//to be called after optimizing every other parameter to see if we can get further improvements
	public void unsettle() {
		settledN = false;
		settledP = false;
		this.firstRun = true;
		//see checkbounds explanation
		if (!inBounds(this.currentValue + (direction * increment))) {
			changeDirection();
		}
	}
	//after checkbounds returns true, settled should also return true in either case... (in one case, we had settled the other side because we were advancing,
	//in the other case, we settled the other side because it caused a bad incr.
	//the only time this isn't true is if it settles on the maximum, and unsettle is called to try to gain further improvements
	//this case will be handled in unsettle
	private boolean checkBounds() {
		if (this.currentValue > this.upperBound && direction > 0.0) {
			this.currentValue = this.lastValue;
			settledP = true;
			if (!this.settled()) {
				changeDirection();
				return true;
			}
			return false;
		}
		else if (this.currentValue < this.lowerBound && direction < 0.0) {
			this.currentValue = this.lastValue;
			settledN = true;
			if (!this.settled()) {
				changeDirection();
				return true;
			}
			return false;
		}
		return true;
	}
	private boolean inBounds(double val) {
		return val <= this.upperBound && val >= this.lowerBound;
	}
	private void increment() {
		this.lastValue = this.currentValue;
		this.currentValue += increment * direction;
	}
	private void changeDirection() {
		if (this.direction < 0.0) {
			this.direction = 1.0;
		}
		else {
			this.direction = -1.0;
		}
	}
}

