package util.data.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import util.sys.DataType;

public class XmlDocReadable implements DataType {
	private final Document doc;
	private final List<XmlElement> elements;
	public XmlDocReadable() {
		doc = null;
		elements = null;
	}
	public XmlDocReadable(Document doc) {
		this.doc = doc;
		this.elements = getChildren();
	}
	private List<XmlElement> getChildren() {
		List<XmlElement> nodes = new ArrayList<XmlElement>();
		for (int i = 0; i < doc.getChildCount(); i++) {
			Node n = doc.getChild(i);
			if (n instanceof Element) {
				nodes.add(new XmlElement((Element)doc.getChild(i)));
			}
		}
		return nodes;
	}
	public Iterator<XmlElement> getElements() {
		return elements.iterator();
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
		return "XmlDocs require no arguments";
	}
	@Override
	public Iterator<String> getStringIter() {
		String doc = this.doc.toXML();
		List<String> lines = new ArrayList<String>();
		for (String line : doc.split(System.getProperty("line.separator"))) {
			lines.add(line);
		}
		return lines.iterator();
	}
	@Override
	public DataType deepCopy() {
		// TODO Auto-generated method stub
		return null;
	}
}
