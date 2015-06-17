package org.xmlbean;

import org.dom4j.Document;

public abstract class XmlDocument {
	public abstract String getRootName();

	public Document asDocument() {
		return XmlBeanHelper.getDocument(this, getRootName());
	}
}
