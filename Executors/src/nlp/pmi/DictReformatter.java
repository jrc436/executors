package nlp.pmi;

import java.io.File;
import java.io.IOException;

import util.sys.LineProcessor;

public class DictReformatter extends LineProcessor<PMIDict, PMIDict> {
	public DictReformatter() {
		super();
	}
	private DictReformatter(String inputDir) {
		super(inputDir, "tmp", new PMIDict());
	}
	public DictReformatter(String inpDIr, String outDIr) {
		super(inpDIr, outDIr, new PMIDict());
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
		return "DictReformatter needs no arguments";
	}

	@Override
	public PMIDict getNextData() {
		File fappend = super.getNextFile();
		if (fappend == null) {
			return null;
		}
		try {
			return PMIDict.fromFile(fappend, false);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
		
	}
	public static PMIDict readOversizeDict(String inputDir) {
		DictReformatter dr = new DictReformatter(inputDir);
		PMIDict oversize = new PMIDict();
		System.out.println("Beginning PMI absorption");
		int i = 0;
		int j = 1;
		while (true) {
			File fappend = dr.getNextFile();
			if (fappend == null) {
				break;
			}
			try {
				if (i % j == 0) {
					System.out.println("Absorbed PMI file: "+i);
					j *= 2;
				}
				oversize.fastAbsorb(PMIDict.fromFile(fappend));
				i++;
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		System.out.println("PMI absorption complete");
		return oversize;
	}

	@Override
	public void map(PMIDict newData, PMIDict threadAggregate) {
		threadAggregate.fastAbsorb(newData);
	}

}
