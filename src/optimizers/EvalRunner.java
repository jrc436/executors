package optimizers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import util.collections.GenericIter;
import util.data.DoubleList;
import util.sys.FileProcessor;

public class EvalRunner<K, J extends GenericIter<K>> extends FileProcessor<J, DoubleList> {
	private final Evaluator<K> ev;
	private final Class<J> listCls;
	public EvalRunner(String inpDir, String outDir, Evaluator<K> ev, Class<J> listCls) {
		super(inpDir, outDir, new DoubleList());
		this.ev = ev;
		this.listCls = listCls;
	}
	@SuppressWarnings("unchecked")
	public EvalRunner(String inpDir, String outDir, String[] str) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		this(inpDir, outDir, ((Class<Evaluator<K>>)Class.forName(str[0])).newInstance(), (Class<J>) Class.forName(str[2]));
	}
	public double sumEvaluations() {
		double sum = 0;
		synchronized (processAggregate) {
			for (double obj : processAggregate) {
				sum += obj;
			}
		}
		return sum;
	}
	@Override
	public int getNumFixedArgs() {
		return 2;
	}
	@Override
	public boolean hasNArgs() {
		return false;
	}
	@Override
	public String getConstructionErrorMsg() {
		return "EvalRunner needs the class of the Evaluator and the class of the GenericList";
	}
	@Override
	public J getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		try {
			return listCls.getConstructor(File.class).newInstance(f);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			System.err.println("GenericLists shoudl have a constructor that takes a File");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	@Override
	public void map(J newData, DoubleList threadAggregate) {
		for (K dat : newData) {
			threadAggregate.add(ev.evaluate(dat));
		}	
	}
	@Override
	public void reduce(DoubleList threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.addAll(threadAggregate);
		}
	}

}
