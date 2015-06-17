package org.xmlbean;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.xmlbean.annotation.ElementTag;
import org.xmlbean.exception.ElementNumberOutOfBoundsException;
import org.xmlbean.exception.ElementVisitingException;
import org.xmlbean.exception.LengthOutOfBoundsException;
import org.xmlbean.exception.NullElementException;
import org.xmlbean.exception.RegexNotMatchedException;
import org.xmlbean.exception.TextFormatException;
import org.xmlbean.util.BeanUtilx;
import org.xmlbean.util.PubUtils;

/**
 * Xml元素访问者. 执行"访问"后可取得对应Bean对象.
 * 
 * @see {@link ElementTag}
 * @since v1.1, 10/09/2008
 */

public class ElementVisitor extends VisitorSupport {
	private Class<?> clazz;
	private Object bean;
	private String valueText; // 虚拟<value/>元素的值
	private String valueTag; // 虚拟<value/>元素的名称
	private String valueFieldName; // 虚拟<value/>元素对应的字段

	/**
	 * 用于保存Xml文档元素与<tt>bean</tt>中<tt>java.util.List</tt>类型字段的对应关系<br>
	 * key: Xml文档中的元素名称; value: <tt>bean</tt>中List类型字段信息
	 */
	private Map<String, FieldParam> listParams = new HashMap<String, FieldParam>(
			5);

	/**
	 * 用于保存Xml文档元素与<tt>bean</tt>中数组类型字段的对应关系<br>
	 * key: Xml文档中的元素名称; value: <tt>bean</tt>中数组类型字段信息
	 */
	private Map<String, FieldParam> arrayParams = new HashMap<String, FieldParam>(
			5);

	/**
	 * 用于保存Xml文档元素与<tt>bean</tt>中<tt>StringArray</tt>类型字段的对应关系<br>
	 * key: Xml文档中的元素名称; value: <tt>bean</tt>中<tt>StringArray</tt>类型字段信息
	 */
	private Map<String, FieldParam> stringArrayParams = new HashMap<String, FieldParam>(
			5);

	@SuppressWarnings("unchecked")
	private final Map<String, FieldParam>[] fieldParams = new Map[]{
			arrayParams, stringArrayParams, listParams};

	/**
	 * <tt>bean</tt>中List/Array/StringArray类型字段信息
	 */
	private static class FieldParam {
		String fieldName;

		/**
		 * List/Array/StringArray的组件类型
		 */
		Class<?> typeParam;

		int minSize;

		int maxSize;

		List<Object> value = new ArrayList<Object>();

		public FieldParam(String fieldName, Class<?> typeParam, int minSize,
				int maxSize) {
			this.fieldName = BeanUtilx.stripToName(fieldName);
			this.typeParam = typeParam;
			this.minSize = minSize;
			this.maxSize = maxSize;
		}
	}

	public ElementVisitor(Class<?> clazz) throws InstantiationException,
			IllegalAccessException {
		this.clazz = clazz;
		this.bean = clazz.newInstance();
		registerByAnnotation(clazz);
	}

	/**
	 * 在访问的过程中给<code>bean</code>对象赋值
	 */
	public void visit(Element element) {
		try {
			visitAttribute(element); // 访问属性
			String fieldName = BeanUtilx.getName(element);
			Field field = BeanUtilx.getField(clazz, fieldName);
			if (field != null) {
				fieldName = field.getName();
			}
			Class<?> fieldType = (field == null) ? null : field.getType();
			if (fieldType == null || isListType(fieldType)) {
				for (Map<String, FieldParam> m : fieldParams) { // List/Array类型
					FieldParam p = m.get(element.getName());
					if (p != null) {
						Object value = convert(element, p.typeParam);
						if (value != null) {
							p.value.add(value);
						}
						break;
					}
				}
			} else {
				Method getm = clazz.getMethod(BeanUtilx.STRING_GET
						+ PubUtils.toTitle(fieldName));
				ElementTag tag = field.getAnnotation(ElementTag.class);
				if (getm.invoke(bean) == null || tag == null || tag.overwrite()) {
					checkElementText(field, element.getName(),
							BeanUtilx.getText(element));
					setBeanProperty(element, fieldName, fieldType);
				}
			}
		} catch (IllegalArgumentException e) {
			throw new TextFormatException(String.valueOf(e));
		} catch (ElementVisitingException e) {
			throw e;
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * 访问元素的属性
	 */
	private void visitAttribute(Element element) throws InstantiationException,
			IllegalAccessException, SecurityException,
			IllegalArgumentException, NoSuchFieldException,
			NoSuchMethodException, InvocationTargetException {
		List<?> attributeList = element.attributes();
		if (attributeList.isEmpty())
			return;

		String attributeName = BeanUtilx.getAttributeFieldName(element);
		Field attributeField = BeanUtilx.getField(clazz, attributeName);

		// 是否有属性字段
		final boolean hasAttrField = attributeField != null;
		Object attributeBean;
		Class<?> attributeType;
		// 如果属性字段为空，就将属性信息写入<code>this.bean</code>
		if (!hasAttrField) {
			attributeBean = this.bean;
			attributeType = this.clazz;
		} else {
			attributeType = attributeField.getType();
			attributeBean = attributeType.newInstance();
		}
		for (Object o : attributeList) {
			Attribute a = (Attribute) o;
			try {
				String name = BeanUtilx.getName(a);
				Field field = BeanUtilx.getField(attributeType, name);
				if (field == null) {
					continue;
				}
				String text = a.getText();
				checkAttributeText(field, element.getName()
						+ BeanUtilx.STRING_DOT + name, text);
				setPropertyByText(attributeType, field.getName(),
						field.getType(), attributeBean, text);
			} catch (IllegalArgumentException e) {
				throw new TextFormatException(String.valueOf(e));
			} catch (ElementVisitingException e) {
				throw e;
			} catch (Exception e) {
				// do nothing
			}
		}
		// 如果有单独的属性字段
		if (hasAttrField) {
			setProperty(clazz, attributeName, attributeType, bean,
					attributeBean);
		}
	}

	/**
	 * 判断给定的类型是否为列表类型
	 * 
	 * @param fieldType
	 *            指定的类型
	 * @return 如果是列表类型返回<tt>true</tt>,否则返回<tt>false</tt>
	 */
	private static boolean isListType(Class<?> fieldType) {
		return fieldType.isArray() || StringArray.class.equals(fieldType)
				|| List.class.equals(fieldType);
	}

	/**
	 * 执行"访问"后取得<tt>bean</tt>对象
	 */
	public Object getBean() {
		populate(listParams);
		populate(arrayParams);
		populate(stringArrayParams);
		setValueProperty();
		checkNullElement(clazz);

		return bean;
	}

	/**
	 * 设置对象属性
	 */
	private void setProperty(Class<?> clazz, String fieldName,
			Class<?> fieldType, Object bean, Object value)
			throws SecurityException, NoSuchFieldException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		if (value == null)
			return;

		Method setm = clazz.getMethod(
				BeanUtilx.STRING_SET + PubUtils.toTitle(fieldName),
				new Class<?>[]{fieldType});
		setm.invoke(bean, new Object[]{value});
	}

	/**
	 * 通过文本设置对象属性
	 */
	private void setPropertyByText(Class<?> clazz, String fieldName,
			Class<?> fieldType, Object bean, String textValue)
			throws SecurityException, NoSuchFieldException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Object value = BeanUtilx.parseText(textValue, fieldType);
		setProperty(clazz, fieldName, fieldType, bean, value);
	}

	private void setBeanProperty(Element element, String fieldName,
			Class<?> fieldType) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			SecurityException, NoSuchMethodException, NoSuchFieldException {
		Object value = convert(element, fieldType);
		setProperty(clazz, fieldName, fieldType, bean, value);
	}

	/**
	 * 设置<code>bean.value</code>字段的值
	 */
	private void setValueProperty() {
		try {
			Field field = BeanUtilx.getField(clazz, valueFieldName);
			if (field == null)
				return;
			checkElementText(field, valueTag, valueText);
			setPropertyByText(clazz, valueFieldName, field.getType(), bean,
					valueText);
		} catch (IllegalArgumentException e) {
			throw new TextFormatException(String.valueOf(e));
		} catch (ElementVisitingException e) {
			throw e;
		} catch (Exception e) {
			// do nothing
		}
	}

	private void registerList(String elmentName, String fieldName,
			Class<?> clazz, int minSize, int maxSize) {
		listParams.put(elmentName, new FieldParam(fieldName, clazz, minSize,
				maxSize));
	}

	private void registerArray(String elmentName, String fieldName,
			Class<?> clazz, int minSize, int maxSize) {
		arrayParams.put(elmentName, new FieldParam(fieldName, clazz, minSize,
				maxSize));
	}

	private void registerStringArray(String elmentName, String fieldName,
			Class<?> clazz, int minSize, int maxSize) {
		stringArrayParams.put(elmentName, new FieldParam(fieldName, clazz,
				minSize, maxSize));
	}

	private void registerByAnnotation(Class<?> clazz) {
		while (!Object.class.equals(clazz)) {
			for (Field f : clazz.getDeclaredFields()) {
				ElementTag tag = f.getAnnotation(ElementTag.class);
				if (tag == null)
					continue;
				// componentType
				Class<?> fieldType = f.getType();
				Class<?> componentType = tag.componentType();
				if (Object.class.equals(componentType)) {
					if (fieldType.isArray())
						componentType = fieldType.getComponentType();
					else if (StringArray.class.equals(fieldType))
						componentType = String.class;
					else if (List.class.equals(fieldType)) {
						ParameterizedType pType = (ParameterizedType) f
								.getGenericType();
						componentType = (Class<?>) pType
								.getActualTypeArguments()[0];
					}
				}
				// register
				if (fieldType.isArray()) {
					registerArray(tag.name(), f.getName(), componentType,
							tag.brotherLimit()[0], tag.brotherLimit()[1]);
				} else if (StringArray.class.equals(fieldType)) {
					registerStringArray(tag.name(), f.getName(), componentType,
							tag.brotherLimit()[0], tag.brotherLimit()[1]);
				} else if (List.class.equals(fieldType)) {
					registerList(tag.name(), f.getName(), componentType,
							tag.brotherLimit()[0], tag.brotherLimit()[1]);
				}
			}
			clazz = clazz.getSuperclass();
		}
	}

	/**
	 * 将Xml文档中的元素转换为指定类型的对象
	 * 
	 * @param element
	 *            Xml文档中的一个元素
	 * @param fieldType
	 *            <tt>bean</tt>中字段的类型
	 * @return 转换后的对象
	 */
	private Object convert(Element element, Class<?> fieldType) {
		String text = BeanUtilx.getText(element);
		Object value = BeanUtilx.parseText(text, fieldType);
		if (value == null) {
			try {
				ElementVisitor visitor = new ElementVisitor(fieldType);
				visitor.setValueText(text);
				visitor.setValueTag(element.getName());
				element.accept(visitor);
				return visitor.getBean();
			} catch (IllegalArgumentException e) {
				throw new TextFormatException(String.valueOf(e));
			} catch (ElementVisitingException e) {
				throw e;
			} catch (Exception e) {
				return null;
			}
		}

		return value;
	}

	/**
	 * 检查元素文本的有效性
	 */
	protected void checkElementText(Field field, String eName, String eText) {
		checkText("element", field, eName, eText);
	}

	/**
	 * 检查属性值的有效性
	 */
	protected void checkAttributeText(Field field, String attributeName,
			String attributeText) {
		checkText("attribute", field, attributeName, attributeText);
	}

	/**
	 * 检查文本有效性
	 */
	protected void checkText(String category, Field field, String name,
			String text) {
		ElementTag tag = field.getAnnotation(ElementTag.class);
		if (tag == null)
			return;

		final int len = text.length();
		if (len > tag.lengthBounds()[1] || len < tag.lengthBounds()[0]) {
			throw new LengthOutOfBoundsException("Length of " + category + "["
					+ name + "]: " + len + ", bounds: ["
					+ tag.lengthBounds()[0] + ", " + tag.lengthBounds()[1]
					+ ']');
		}
		if (PubUtils.hasText(tag.regex())) {
			if (!Pattern.matches(tag.regex(), text))
				throw new RegexNotMatchedException("Text of " + category + "["
						+ name + "] must match: " + tag.regex());
		}
	}

	protected void checkNullElement(Class<?> clazz) {
		while (!Object.class.equals(clazz)) {
			for (Field f : clazz.getDeclaredFields()) {
				ElementTag tag = f.getAnnotation(ElementTag.class);
				if (tag == null || tag.nullable())
					continue;
				try {
					Method getm = clazz.getMethod(BeanUtilx.STRING_GET
							+ PubUtils.toTitle(f.getName()));
					if (getm.invoke(bean) == null)
						throw new NullElementException("Missing element["
								+ tag.name() + "]");
				} catch (ElementVisitingException e) {
					throw e;
				} catch (Exception e) {
					// do nothing
				}
			}
			clazz = clazz.getSuperclass();
		}
	}

	private void populate(Map<String, FieldParam> params) {
		for (String eName : params.keySet()) {
			FieldParam p = params.get(eName);
			int size = p.value.size();
			if (size > p.maxSize || size < p.minSize) {
				throw new ElementNumberOutOfBoundsException(
						"Number of element[" + eName + "]: " + size + ", "
								+ "bounds: [" + p.minSize + ", " + p.maxSize
								+ ']');
			}
			try {
				// type & value
				Class<?> type = null;
				Object value = null;
				if (params == arrayParams) {
					value = p.value.toArray((Object[]) Array.newInstance(
							p.typeParam, 0));
					type = value.getClass();
				} else if (params == stringArrayParams) {
					value = new StringArray(p.value.toArray(new String[0]));
					type = StringArray.class;
				} else {
					value = p.value;
					type = List.class;
				}
				// set property
				setProperty(clazz, p.fieldName, type, bean, value);
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	private void setValueText(String valueText) {
		this.valueText = valueText;
	}

	private void setValueTag(String valueTag) {
		this.valueTag = valueTag;
		// .toLowerCase()
		this.valueFieldName = BeanUtilx.stripToName(valueTag)
				+ BeanUtilx.SUFFIX_VALUE_FIELD;
	}
}
