package util.data.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

public class XmlElement extends HashMap<String, String> {
	private static final long serialVersionUID = -393903981862427207L;
	@SuppressWarnings("unused")
	private final Node node; //future-proof
	private final List<XmlElement> children;
	public XmlElement(Element child) {
		this.node = child;
		Elements e = child.getChildElements();
		this.children = new ArrayList<XmlElement>();
		for (int i = 0; i < e.size(); i++) {
			children.add(new XmlElement(e.get(i)));
		}
		for (int i = 0; i < child.getAttributeCount(); i++) {
			Attribute a = (Attribute) child.getAttribute(i);
			super.put(a.getQualifiedName(), a.getValue());
		}
	}
	public Iterator<XmlElement> getChildren() {
		return children.iterator();
	}

}
