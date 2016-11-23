package optimizers;

import java.lang.reflect.InvocationTargetException;

import util.collections.GenericIter;
import util.data.DoubleList;
import util.sys.DataType;
import util.sys.Executor;
import util.sys.FileProcessor;
import util.sys.InputParse;


//a hillclimber... that likes valleys
public class ValleyClimber<M, J extends FileProcessor<K, V>, K extends DataType, V extends GenericIter<M>> extends Executor<J, K, V> {
	//private final MassEvaluator<M, K> eval;
	private VariableSet vs;
	private Evaluator<M> eval;
	private final EvaluationList evs;
	private String inputPath;
	private String outputPath;
	private int maxFiles = -1;
	public ValleyClimber(int gbperthread, String procName, Class<J> fp, Class<K> dt1, Class<V> dt2) {
		super(procName, gbperthread, fp, dt1, dt2);
		evs = new EvaluationList();
	}
	public void randomRestart() {
		vs.randomAll();
	}
	public void resetToValues(double[] vars) {
		vs.forceAll(vars);
	}
	@Override
	public void run() {
		double currentScore = 0.0;
		double lastScore = currentScore;
		int currentIter = 1; 
		int maxiter = 100000;
		String iterName;
		String evalName;
		String iterDir;
		while (true) {
			if (currentIter > maxiter) {
				break;
			}
			iterName = super.name+"-i"+String.format("%04d", currentIter);
			evalName = "eval-"+iterName;
			iterDir = this.outputPath + "/" +iterName;
			lastScore = currentScore;
			//this is a constructor that's needed for ValleyClimbers. It's the VariableSet (for variables) + the old processor (for run constants) + inp/out dirs, obv
			
			try {
				super.initializeFromProcessor(super.fp.getConstructor(String.class, String.class, super.fp, VariableSet.class).newInstance(this.inputPath, iterDir, super.getProcessor(), vs), maxFiles);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				System.err.println("The most likely reason for this error is you don't have the special ValleyClimber constructor in your FileProcessor. String inpdir, String outDir, a self-reference, and a variableset");
				e.printStackTrace();
				System.exit(1);
			}
			currentScore = run(iterDir, this.outputPath + "/" + evalName);
			boolean goodStep = currentScore <= lastScore;
			if (goodStep) { vs.acknowledgeImprovement(); }
			boolean stillMoving = vs.step(goodStep); //this checks if the variable itself is still improving
			if (!vs.updateIndex(stillMoving)) {
				break;
			}
			currentIter++;
		}
		System.out.println("Score: "+currentScore);
	}
	//startIndex refers to which variable to optimize first!
	
	//experiment name should be a specific run of an experiment... which should be independent of the settings
	//and only dependent on opt
	private double run(String inputDir, String outputDir) {
		super.run(); //run the processor, create the dats in the output folder
		EvalRunner<M, V> runner = new EvalRunner<M, V>(inputDir, outputDir, this.eval, super.out);
		@SuppressWarnings("unchecked")
		Executor<EvalRunner<M, V>, V, DoubleList> eval = new Executor<EvalRunner<M, V>, V, DoubleList>("eval", super.gbPerThread, (Class<EvalRunner<M, V>>) runner.getClass(), super.out, DoubleList.class);
		eval.initializeFromProcessor(runner);
		eval.run();
		double d = runner.sumEvaluations();
		Evaluation ev = new Evaluation(vs, d);
		System.out.println(ev.toString());
		evs.add(ev);
		return d;
	}
	@SuppressWarnings("unchecked")
	@Override
	public InputParse initializeFromCmdLine(String[] args) {
		try {
			this.vs = VariableSet.fromString(args[0]);
			this.eval = (Evaluator<M>) Class.forName(args[1]).getConstructor(VariableSet.class).newInstance(this.vs);
			if (args[2].equals("-n")) {
				this.inputPath = args[4];
				this.outputPath = args[5];
				maxFiles = Integer.parseInt(args[3]);
			}
			else {
				this.inputPath = args[2];
				this.outputPath = args[3];
			}
		}
		catch (Exception e) {
			System.err.println("Error initializing ValleyClimber.");
			System.err.println("First, you need to specify the VariableSet. It is in the format vars:[varname:initval:lowbound:highbound:incr,var2...]");
			System.err.println("Next, you should specify a fully qualified class name extending Evaluator.");
			System.err.println("Then, specify all other args as normal, including LineProcessor args");
			e.printStackTrace();
			System.exit(1);
		}
		String[] cmdArgs = new String[args.length-2];
		for (int i = 2; i < args.length; i++) {
			cmdArgs[i-2] = args[i];
		}
		InputParse ip = super.initializeFromCmdLine(cmdArgs);
		return ip;
	}
}
