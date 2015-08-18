package org.xmlbean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.xmlbean.annotation.ElementTag;
import org.xmlbean.exception.XmlBeanFormattingException;
import org.xmlbean.util.BeanUtilx;
import org.xmlbean.util.PubUtils;

/**
 * 此类提供了一个<code>format()</code>方法, 用于将一个JavaBean对象转换为
 * <code>org.dom4j.Document</code>对象.
 * 
 * @see {@link ElementTag}
 * @since v1.0, 09/08/08
 */
public class XmlBeanFormatter {
	private static final Log log = LogFactory.getLog(XmlBeanFormatter.class);
	private Object bean;
	private Element element;

	private List<ElementGroup> children = new ArrayList<ElementGroup>();

	/**
	 * 根据order对子元素分组
	 */
	private static class ElementGroup implements Comparable<ElementGroup> {
		final int order;
		final List<Element> elements = new ArrayList<Element>();

		ElementGroup(int order) {
			super();
			this.order = order;
		}

		@Override
		public int compareTo(ElementGroup o) {
			return this.order - o.order;
		}
	}

	/**
	 * 构造方法
	 * 
	 * @param bean
	 *            Xml元素对应的JavaBean
	 * @param elementName
	 *            <code>bean</code>对应的Xml元素名
	 */
	public XmlBeanFormatter(Object bean, String elementName) {
		if (!PubUtils.hasText(elementName))
			throw new XmlBeanFormattingException(
					"elementName must not be empty");
		this.bean = bean;
		this.element = DocumentHelper.createElement(elementName);
	}

	/**
	 * 将<code>bean</code>转换为<code>org.dom4j.Document</code>对象.
	 */
	public Element format() {
		String eText = BeanUtilx.getText(bean.getClass(), bean, null);
		if (eText == null) {
			format(bean.getClass());
		} else {
			element.addText(eText);
		}
		return collect();
	}

	private Element collect() {
		Collections.sort(children);
		for (ElementGroup g : children) {
			for (Element e : g.elements) {
				this.element.add(e);
			}
		}
		return this.element;
	}

	/**
	 * 添加子节点
	 */
	private void addChild(Element child, ElementTag tag) {
		ElementGroup g = findGroup(tag.order());
		if (g == null) {
			g = new ElementGroup(tag.order());
			children.add(g);
		}
		g.elements.add(child);
	}

	private ElementGroup findGroup(int order) {
		for (ElementGroup g : children) {
			if (g.order == order) {
				return g;
			}
		}
		return null;
	}

	/**
	 * 遍历clazz的所有属性，添加相应的节点到element
	 */
	private void format(Class<?> clazz) {
		while (!Object.class.equals(clazz)) {
			for (Field f : clazz.getDeclaredFields()) {
				ElementTag tag = f.getAnnotation(ElementTag.class);
				// 可序列化判断
				if (tag == null || !tag.serializable() || tag.attribute()) {
					continue;
				}
				final String eName = PubUtils.hasText(tag.name()) ? tag.name()
						: f.getName();

				Class<?> fieldType = f.getType();
				try {
					Method getm = clazz.getDeclaredMethod("get"
							+ PubUtils.toTitle(f.getName()));
					Object value = getm.invoke(bean);
					if (value == null) {
						if (!tag.nullable())
							// element.addElement(eName);
							addChild(DocumentHelper.createElement(eName), tag);
					} else if (fieldType.isArray()) {
						for (Object v : (Object[]) value)
							// element.add(new XmlBeanFormatter(v,
							// eName).format());
							addChild(new XmlBeanFormatter(v, eName).format(),
									tag);
					} else if (StringArray.class.equals(fieldType)) {
						for (Object v : ((StringArray) value).getValue())
							// element.add(new XmlBeanFormatter(v,
							// eName).format());
							addChild(new XmlBeanFormatter(v, eName).format(),
									tag);
					} else if (value instanceof List) {
						for (Object v : (List<?>) value)
							// element.add(new XmlBeanFormatter(v,
							// eName).format());
							addChild(new XmlBeanFormatter(v, eName).format(),
									tag);
					} else {
						// element.add(createElement(tag, eName, fieldType,
						// value));
						addChild(createElement(tag, eName, fieldType, value),
								tag);
					}
				} catch (Exception e) {
					if (log.isWarnEnabled()) {
						log.warn("format(" + clazz + ") error", e);
					}
				}
			}
			appendValueText();
			appendAttribute();
			clazz = clazz.getSuperclass();
		}
	}

	/**
	 * 创建<code>org.dom4j.Element</code>对象
	 * 
	 * @param tag
	 *            {@link ElementTag}
	 * @param fieldType
	 *            <code>bean</code>中字段的类型
	 * @param value
	 *            <code>bean</code>中字段的值
	 * @return <code>org.dom4j.Element</code>对象
	 */
	private Element createElement(ElementTag tag, String eName,
			Class<?> fieldType, Object value) {
		String eText = BeanUtilx.getText(fieldType, value, tag);
		if (eText == null) {
			return new XmlBeanFormatter(value, eName).format();
		} else {
			return BeanUtilx.addText(DocumentHelper.createElement(eName),
					eText, isCdata(tag));
		}
	}

	private boolean isCdata(ElementTag tag) {
		return tag != null && tag.cdata();
	}

	/**
	 * 给元素添加属性
	 * 
	 * @param element
	 *            元素
	 * @param bean
	 *            对应的XmlBean
	 */
	private void appendAttribute() {
		Object attribute = BeanUtilx.getAttributeValue(element, bean);
		if (attribute == null)
			attribute = bean;
		Class<?> clazz = attribute.getClass();
		while (!Object.class.equals(clazz)) {
			for (Field field : clazz.getDeclaredFields()) {
				ElementTag tag = field.getAnnotation(ElementTag.class);
				if (tag == null || !tag.attribute())
					continue;
				try {
					final String aName = PubUtils.hasText(tag.name()) ? tag
							.name() : field.getName();
					Method getm = clazz.getMethod(BeanUtilx.STRING_GET
							+ PubUtils.toTitle(field.getName()));
					String value = BeanUtilx.getText(null,
							getm.invoke(attribute), tag);
					element.addAttribute(aName, value);
				} catch (Exception e) {
					if (log.isErrorEnabled()) {
						log.error("appendAttribute", e);
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
	}

	/**
	 * 添加虚拟元素<value/>文本
	 */
	private void appendValueText() {
		String value = BeanUtilx.getValueString(element, bean, null);
		if (value == null)
			return;

		Field field = BeanUtilx.getValueField(element, bean);
		if (field == null) {
			BeanUtilx.addText(element, value, false);
		} else {
			BeanUtilx.addText(element, value,
					String.class.equals(field.getType()));
		}
	}
}