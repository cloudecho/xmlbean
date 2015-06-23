package org.xmlbean.util;

import static org.xmlbean.util.DateUtils.PATTERN_DATESHORT;
import static org.xmlbean.util.DateUtils.PATTERN_DATETIME;
import static org.xmlbean.util.DateUtils.PATTERN_TIMESTAMP;
import static org.xmlbean.util.DateUtils.PATTERN_DATETIME_COMPACT;
import static org.xmlbean.util.DateUtils.PATTERN_DATE_COMPACT;
import static org.xmlbean.util.DateUtils.PATTERN_DAYPATH;
import static org.xmlbean.util.DateUtils.PATTERN_DEFAULT;
import static org.xmlbean.util.DateUtils.PATTERN_YEARMONTH;
import static org.xmlbean.util.DateUtils.parseDate;
import static org.xmlbean.util.DateUtils.parseDateUTC;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.xmlbean.annotation.ElementTag;

/**
 * XmlBean实用类
 */
public abstract class BeanUtilx {
	public static final String STRING_SET = "set";

	public static final String STRING_GET = "get";

	public static final String STRING_DOT = ".";

	public static final String SUFFIX_ATTRIBUTE_FIELD = "Attr";

	public static final String SUFFIX_VALUE_FIELD = "Val";

	/**
	 * 元素名称被替换字符
	 */
	public static final String[] REPLACED_STRINGS = { "-", ":" };

	/**
	 * 元素名称替换字符
	 */
	public static final String[] REPLACEMENT_STRINGS = { "_", "$" };

	/**
	 * 取得元素对应的属性字段名
	 */
	public static String getAttributeFieldName(Element element) {
		return getName(element) + BeanUtilx.SUFFIX_ATTRIBUTE_FIELD;
	}

	/**
	 * 取得元素对应的<code>bean</code>中字段名
	 */
	public static String getName(Element element) {
		return toFieldName(stripToName(element.getName()));
	}

	/**
	 * 取得属性名
	 */
	public static String getName(Attribute attribute) {
		return toFieldName(stripToName(attribute.getName()));
	}

	public static String stripToName(String s) {
		for (int i = 0; i < REPLACED_STRINGS.length; i++) {
			s = PubUtils
					.replace(s, REPLACED_STRINGS[i], REPLACEMENT_STRINGS[i]);
		}
		return s;
	}

	/**
	 * 取得元素文本
	 */
	public static String getText(Element element) {
		return element.getTextTrim();
	}

	/**
	 * 根据字段名取得<code>java.lang.reflect.Field</code>对象,若不存在返回<code>null</code>
	 */
	public static Field getField(final Class<?> clazz, String fieldName) {
		Field field = null;
		Class<?> myClass = clazz;
		while (field == null && !Object.class.equals(myClass)) {
			try {
				field = myClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				// do nothing
			}
			myClass = myClass.getSuperclass();
		}
		// Find by ElementTag
		myClass = clazz;
		while (field == null && !Object.class.equals(myClass)) {
			for (Field fld : myClass.getDeclaredFields()) {
				ElementTag tag = fld.getAnnotation(ElementTag.class);
				if (tag == null) {
					continue;
				}
				if (fieldName.equals(tag.name())) {
					return fld;
				}
			}
			myClass = myClass.getSuperclass();
		}
		return field;
	}

	/**
	 * 解析文本
	 */
	public static Object parseText(String text, Class<?> fieldType) {
		if (text == null || fieldType == null)
			return null;

		if (String.class.equals(fieldType))
			return text;
		else if (Long.class.equals(fieldType) || long.class.equals(fieldType))
			return Long.valueOf(text);
		else if (Double.class.equals(fieldType)
				|| double.class.equals(fieldType))
			return Double.valueOf(text);
		else if (Integer.class.equals(fieldType) || int.class.equals(fieldType))
			return Integer.valueOf(text);
		else if (Character.class.equals(fieldType)
				|| char.class.equals(fieldType))
			return text.charAt(0);
		else if (Boolean.class.equals(fieldType)
				|| boolean.class.equals(fieldType))
			return Boolean.valueOf(text);
		else if (Byte.class.equals(fieldType) || byte.class.equals(fieldType))
			return Byte.valueOf(text);
		else if (Float.class.equals(fieldType) || float.class.equals(fieldType))
			return Float.valueOf(text);
		else if (Short.class.equals(fieldType) || short.class.equals(fieldType))
			return Short.valueOf(text);
		else if (java.util.Date.class.equals(fieldType)) {// 尝试各种模式去解析
			final String[] patterns = { PATTERN_TIMESTAMP, PATTERN_DATETIME,
					PATTERN_DATETIME_COMPACT, PATTERN_DEFAULT,
					PATTERN_DATE_COMPACT, PATTERN_DATESHORT, PATTERN_YEARMONTH,
					PATTERN_DAYPATH };
			Object value = null;
			for (String p : patterns) {
				value = parseDate(text, p);
				if (value != null) {
					break;
				}
			}
			return value == null ? parseDateUTC(text) : value;
		} else if (java.sql.Date.class.equals(fieldType))
			return java.sql.Date.valueOf(text);
		else if (java.sql.Timestamp.class.equals(fieldType))
			return java.sql.Timestamp.valueOf(text);
		else {
			return null;
		}
	}

	/**
	 * 取得虚拟<value/>元素值, 不存在返回null
	 * 
	 * @param element
	 *            元素
	 * @param bean
	 *            对应的XmlBean
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static String getValueString(Element element, Object bean,
			ElementTag tag) {
		try {
			Class<?> clazz = bean.getClass();
			String fieldName = toFieldName(element.getName())
					+ SUFFIX_VALUE_FIELD;
			Method getm = clazz.getMethod(STRING_GET
					+ PubUtils.toTitle(fieldName));
			Object value = getm.invoke(bean);
			return getText(getm.getReturnType(), value, tag);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 取得虚拟<value/>元素对应的字段, 不存在返回null
	 * 
	 * @param element
	 *            元素
	 * @param bean
	 *            对应的XmlBean
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static Field getValueField(Element element, Object bean) {
		try {
			Class<?> clazz = bean.getClass();
			String fieldName = toFieldName(element.getName())
					+ SUFFIX_VALUE_FIELD;
			return getField(clazz, fieldName);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 取得bean的属性对象, 不存在返回null
	 * 
	 * @param element
	 *            元素
	 * @param bean
	 *            对应的XmlBean
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static Object getAttributeValue(Element element, Object bean) {
		try {
			Class<?> clazz = bean.getClass();
			String fieldName = toFieldName(element.getName())
					+ SUFFIX_ATTRIBUTE_FIELD;
			Method getm = clazz.getMethod(STRING_GET
					+ PubUtils.toTitle(fieldName));
			return getm.invoke(bean);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 给元素添加文本
	 */
	public static Element addText(ElementTag tag, Element element, String text) {
		if (tag != null && tag.cdata())
			element.addCDATA(PubUtils.stripToEmpty(text));
		else
			element.addText(PubUtils.stripToEmpty(text));

		return element;
	}

	/**
	 * 根据对象的类型和值取得XML元素文本.<br>
	 * <b>约定:</b>如果以下条件均满足,该方法返回<code>null</code>:<br>
	 * <ul>
	 * <li><code>value</code>不是<code>String</code>对象;
	 * <li><code>value</code>不是<code>java.sql.Date</code>对象;
	 * <li><code>value</code>不是<code>java.sql.Timestamp</code>对象;
	 * <li><code>value</code>不是<code>java.util.Date</code>对象;
	 * <li><code>value</code>不是简单数据类型(包括其包装类).
	 * </ul>
	 * 为保证<code>{@link XmlBeanFormatter.format()}</code>方法的正确执行,该约定必须被遵循.
	 * 
	 * @param valueType
	 *            对象的类型
	 * @param value
	 *            对象的值
	 */
	public static String getText(Class<?> valueType, Object value,
			ElementTag tag) {
		if (value == null) {
			return "";
		}
		if (valueType == null) {
			valueType = value.getClass();
		}
		if (String.class.equals(valueType) || valueType.isPrimitive()
				|| Long.class.equals(valueType)
				|| Double.class.equals(valueType)
				|| Integer.class.equals(valueType)
				|| Character.class.equals(valueType)
				|| Boolean.class.equals(valueType)
				|| Byte.class.equals(valueType)
				|| Float.class.equals(valueType)
				|| Short.class.equals(valueType)
				|| java.sql.Timestamp.class.equals(valueType)) {
			return value.toString();
		} else if (java.util.Date.class.isAssignableFrom(valueType)) {
			return DateUtils.formatDate((java.util.Date) value, tag.format());
		} else {
			return null; // 遵循约定
		}
	}

	private static String toFieldName(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}
}
