package util.sys;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//k and v should obviously be the same classes are inp and out... just poop
public class Executor<J extends FileProcessor<K, V>, K extends DataType, V extends DataType> {
	private final BlockingQueue<String> messages;
	protected final int gbPerThread;
	protected final String name;
	private final Class<K> in;
	protected final Class<J> fp;
	protected final Class<V> out;
	private int maxNumInputs;
	private J proc;
	public Executor(String name, int gbPerThread, Class<J> fp, Class<K> inp, Class<V> out) {
		this.name = name;
		this.gbPerThread = gbPerThread;
		this.messages = new LinkedBlockingQueue<String>();
		this.in = inp;
		this.out = out;
		this.fp = fp;
	}
	protected J getProcessor() {
		return proc;
	}
	public InputParse initializeFromCmdLine(String[] cmdArgs) {
		if (cmdArgs.length < 2) {
			System.err.println("All Executors of any sort require the input and output directories.");
			System.exit(1);
		}
		int maxNumInputs = -1;
		if (LineProcessor.class.isAssignableFrom(fp)) {
			System.err.println("Using Line Processors");
			if (cmdArgs[0].equals("-n")) {
				try {
					maxNumInputs = Integer.parseInt(cmdArgs[1]);
				}
				catch (NumberFormatException nfe) {
					System.err.println(cmdArgs[1] + " is not a parsable number to use as the max number of inputs");
					nfe.printStackTrace();
					System.exit(1);
				}
				String[] newArgs = new String[cmdArgs.length-2];
				for (int i = 2; i < cmdArgs.length; i++) {
					newArgs[i-2] = cmdArgs[i];
				}
				cmdArgs = newArgs;
			}
			else {
				System.err.println("You are using a LineProcessor. The first two args should be '-n' followed by the max number of inputs a thread can process)");
				System.err.println("Instead got: arg0: "+cmdArgs[0]+" and arg1: "+cmdArgs[1]);
				System.exit(1);
			}
		}
		else if (cmdArgs[0].equals("-n")) {
			System.err.println("As your FileProessor does not extend LineProcessor, it cannot use LineExecutors.");
			System.err.println("If you are running into memory issues and your process has no meaningful reduce, please have it extend LineProcessor");
			System.exit(1);
		}
		else {
			System.err.println("Using File Processors");
		}
		
		this.maxNumInputs = maxNumInputs;
		InputParse ip = new InputParse(cmdArgs);
		InputProcessor<J, K, V> ipr = new InputProcessor<J, K, V>(fp, in, out, ip);	
		if (cmdArgs.length < 2) {
			ipr.createError();
		}
		String[][] args = new String[3][];	
		int counts[] = new int[3];
		boolean copiedArgs = false;
		int count = 0;
		for (int i = 0; i < 3; i++) {
			int oldCount = count;
			//the "reuse" case is obviously pretty minor... but still best way to do this
			if (i == 2 && count == ip.getRemaining().length && in.equals(out)) {
				args[i] = Arrays.copyOf(args[i-1], args[i-1].length);
				copiedArgs = true;
			}
			else {
				args[i] = ipr.getArgs(count, i);
			}
			count += args[i].length;	
			if (count < ip.getRemaining().length && ipr.indexIsBound(count)) {
				count++;
			}
			counts[i] = count - oldCount;
		}	
		if (count != ip.getRemaining().length) {
			int fullcount = 2 + count;
			System.err.println("Arguments received: "+cmdArgs.length+". Process effectively parsed: "+fullcount);
			if (copiedArgs) {
				System.err.println("Warning: Args were copied from Input for Output, check for correctness");
			}
			System.err.println("Input and Output directories were successfully received"); //or it would've thrown an error earlier
			ipr.countErrorHandling(counts, args);
			ipr.createError();
		}
 		setProc(ip, ipr, args);
 		return ip;
	}
	
	//only call these if you know what you're doing!
	public void initializeFromProcessor(J fp, int maxNumInputs) {
		proc = fp;
		this.maxNumInputs = maxNumInputs;
	}
	public void initializeFromProcessor(J fp) {
		initializeFromProcessor(fp, -1);
	}
	
	private void setProc(InputParse ip, InputProcessor<J, K, V> ipr, String[][] args) {
		List<String[]> goodArgs = new ArrayList<String[]>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].length != 0) {
				goodArgs.add(args[i]);
			}
		}
		try {	
			if (goodArgs.size() == 0) {
				proc = fp.getConstructor(String.class, String.class).newInstance(ip.getInputPath(), ip.getOutputPath());
			}
			else if (goodArgs.size() == 1) {
				proc = fp.getConstructor(String.class, String.class, String[].class).newInstance(ip.getInputPath(), ip.getOutputPath(), goodArgs.get(0));
			}
			else if (goodArgs.size() == 2) {
				proc = fp.getConstructor(String.class, String.class, String[].class, String[].class).newInstance(ip.getInputPath(), ip.getOutputPath(), goodArgs.get(0), goodArgs.get(1));
			}
			else {
				proc = fp.getConstructor(String.class, String.class, String[].class, String[].class, String[].class).newInstance(ip.getInputPath(), ip.getOutputPath(), goodArgs.get(0), goodArgs.get(1), goodArgs.get(2));
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			System.err.println("Error Creating Process. The most likely reason for this is that there is no accessible constructor of the type you requested.");
			System.err.println("This should be easily solvable by adding one. You requested a constructor that takes the input and output paths, along with: "+goodArgs.size()+" string arrays.");
			System.err.println("It could also be an error in the existing constructor, which will appear below.");
			e.printStackTrace();
			ipr.createError();
		} 
	}
	
	
	protected Executable getExecutable(int threadNum, BlockingQueue<String> messages) {//, V initialValue) {
		return maxNumInputs < 0 ? new ProcessWorker(threadNum, messages) : new LineWorker(threadNum, messages, maxNumInputs);
	}
	public void run() {
		Thread log;
		try {
            log = new Thread(new Logger(messages, name+".log"));
            log.setDaemon(true);
            log.start();
		} catch (IOException e) {
            System.err.println("Error initializing output. Check your output paths");
            e.printStackTrace();
            System.exit(1);
		}
	    int numThr = ResourceAllocator.getSuggestedNumThreads(gbPerThread);
	    ExecutorService es = Executors.newCachedThreadPool();  
	    for (int i = 0; i < numThr; i++) {
	    	es.execute(getExecutable(i, messages));//, proc.getInitialThreadValue()));
	    }
	    es.shutdown();
	    try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			System.err.println("Process interrupted: "+proc.toString()+", will attempt to write");
			e.printStackTrace();
		}
	    finally {
	    	messages.add("Writing process beginning.");
	    	try {
	    		proc.write();
	    	}
	    	catch (UnsupportedOperationException uoe) {
	    		System.err.println("Possibly finished, check for errors!");
	    		uoe.printStackTrace();
	    		messages.add("Process wrote using its LineProcessors, should not need to write now");
	    	}
	    	messages.add("Writing process complete. Process will now terminate");
	    }
	}
	private static Integer filenum = 0;
	private static Object lock = new Object();
	class LineWorker extends Executable {
		private V threadAggregate;
		private final int maxNumInps;
		public LineWorker(int threadnum, BlockingQueue<String> log, int maxNumInputs) {
			super(threadnum, log);
			this.threadAggregate = proc.getInitialThreadValue();
			this.maxNumInps = maxNumInputs;
		}
		private boolean tryReduce(int numInps) {
			if (numInps > maxNumInps) {
				//synchronized(proc.processAggregate) {
					//proc.reduce(threadAggregate);
				synchronized(lock) {
					this.logMessage("Thread "+getNum()+" is preparing to write. It's starting file number is: "+filenum);
					filenum = proc.writeData(threadAggregate, filenum);
					this.logMessage("Thread "+getNum()+" has finished writing. It's ending file number is: "+filenum);
				}
				this.threadAggregate = proc.getInitialThreadValue();	
					//proc.processAggregate = proc.getInitialThreadValue();
				//}
				return true;
			}
			return false;
		}
		@Override
		public void run() {
			this.logMessage("Thread"+getNum()+" is beginning its run");
			int numInps = 0;
			while (true) {
				this.logMessage("Thread"+getNum()+" is waiting to acquire another file. "+proc.numReadableRemaining()+ " files remain.");
				K data = proc.getNextData();						
				if (data == null) {
					this.logMessage("Thread"+getNum()+" has failed to acquire more data. It's ending its run.");
					break;
				}
				else {
					this.logMessage("Thread"+getNum()+" has acquired more data");		
				}
				numInps++;
				proc.map(data, threadAggregate);
				if (tryReduce(numInps)) {
					numInps = 0;
				}
			}
			this.logMessage("Thread"+getNum()+" is beginning its reduce.");
			tryReduce(maxNumInputs+1); //has to write at least once..
			this.logMessage("Thread"+getNum()+" is exiting.");			
		}
	}
	class ProcessWorker extends Executable {
		private V threadAggregate;
		public ProcessWorker(int threadnum, BlockingQueue<String> log) {//,int gbAlloc, J proc, V aggregate) {
			super(threadnum, log);
			this.threadAggregate = proc.getInitialThreadValue();
		}
		@Override
		public void run() {
			this.logMessage("Thread"+getNum()+" is beginning its run");
			while (true) {
				this.logMessage("Thread"+getNum()+" is waiting to acquire another file. "+proc.numReadableRemaining()+ " files remain.");
				K data = proc.getNextData();						
				if (data == null) {
					this.logMessage("Thread"+getNum()+" has failed to acquire more data. It's ending its run.");
					break;
				}
				else {
					this.logMessage("Thread"+getNum()+" has acquired more data");		
				}
				proc.map(data, threadAggregate);
			}
			this.logMessage("Thread"+getNum()+" is beginning its reduce.");
			proc.reduce(threadAggregate);
			this.logMessage("Thread"+getNum()+" is exiting.");			
		}
	}
}
