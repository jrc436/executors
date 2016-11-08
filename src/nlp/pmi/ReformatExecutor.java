package nlp.pmi;

import util.sys.Executor;

public class ReformatExecutor extends Executor<DictReformatter, PMIDict, PMIDict>{

	public ReformatExecutor() {
		super("reformat", 12, DictReformatter.class, PMIDict.class, PMIDict.class);
	}
	public static void main(String[] args) {
		ReformatExecutor rfe = new ReformatExecutor();
		rfe.initializeFromCmdLine(args);
		rfe.run();
	}
	
}
