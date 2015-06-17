package org.xmlbean;

import java.io.File;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xmlbean.annotation.ElementTag;


/**
 * 提供了JavaBean与Xml文档互相转换的实用方法
 * 
 * @see {@link ElementTag}
 * 
 */
public class XmlBeanHelper {
	private XmlBeanHelper() {

	}

	/**
	 * 根据Xml文档取得JavaBean
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Document doc, Class<T> clazz)
			throws InstantiationException, IllegalAccessException {
		ElementVisitor visitor = new ElementVisitor(clazz);
		doc.accept(visitor);
		return (T) visitor.getBean();
	}

	/**
	 * 根据Xml文档取得JavaBean
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Element element, Class<T> clazz)
			throws InstantiationException, IllegalAccessException {
		ElementVisitor visitor = new ElementVisitor(clazz);
		element.accept(visitor);
		return (T) visitor.getBean();
	}

	/**
	 * 根据Xml文档取得JavaBean
	 */
	public static <T> T getBean(InputStream in, Class<T> clazz)
			throws InstantiationException, IllegalAccessException,
			DocumentException {
		return getBean(new SAXReader().read(in), clazz);
	}

	/**
	 * 根据Xml文档取得JavaBean
	 */
	public static <T> T getBean(File file, Class<T> clazz)
			throws InstantiationException, IllegalAccessException,
			DocumentException {
		return getBean(new SAXReader().read(file), clazz);
	}

	/**
	 * 根据Xml文档取得JavaBean
	 */
	public static <T> T getBean(String xml, Class<T> clazz)
			throws InstantiationException, IllegalAccessException,
			DocumentException {
		return getBean(DocumentHelper.parseText(xml), clazz);
	}

	/**
	 * 取得JavaBean对应的<code>org.dom4j.Element</code>对象
	 */
	public static Element getElement(Object bean, String elementName) {
		return new XmlBeanFormatter(bean, elementName).format();
	}

	/**
	 * 取得JavaBean对应的<code>org.dom4j.Document</code>对象
	 * 
	 * @see {@link #getElement(Object, String)}
	 */
	public static Document getDocument(Object bean, String rootName) {
		Document doc = DocumentHelper.createDocument();
		doc.setRootElement(getElement(bean, rootName));
		return doc;
	}
}
