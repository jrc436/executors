package util.data.xml;

import java.io.File;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.ParsingException;
import util.sys.DataType;
import util.sys.LineProcessor;

public abstract class XmlLayer<E extends DataType> extends LineProcessor<XmlDocReadable, E> {
	public XmlLayer() {
		super();
	}
	public XmlLayer(String inpDir, String outDir, E outputType) {
		super(inpDir, outDir, outputType);
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
		return "XmlLayer is an abstract type. If you are seeing this message, it's possible your FileProcessor isn't fully defined";
	}

	@Override
	public XmlDocReadable getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		try {
			Builder b = new Builder();
			return new XmlDocReadable(b.build(f));
		} catch (ParsingException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
}
