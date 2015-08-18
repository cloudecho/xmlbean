package org.xmlbean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.xmlbean.util.DateUtils;

/**
 * 此注释用于标记JavaBean中的字段对应的XML元素
 * 
 * @since v1.0, 09/08/08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ElementTag {
	/**
	 * XML元素序号(可重复,表示元素在XML文档中的顺序,默认值<code>Integer.MAX_VALUE</code>)
	 */
	int order() default Integer.MAX_VALUE;

	/**
	 * XML元素名称，缺省取字段名
	 */
	String name() default "";

	/**
	 * 元素是否可空(默认否)
	 */
	boolean nullable() default false;

	/**
	 * JavaBean中的字段是否可以复写(默认是)
	 */
	boolean overwrite() default true;

	/**
	 * 元素的文本是否以CDATA区域表达(默认否)
	 */
	boolean cdata() default false;

	/**
	 * 如果是Array/List类型,至少有<code>brotherLimit()[0]</code>(默认值<code>0</code>
	 * )个兄弟元素, 至多有<code>brotherLimit()[1]</code>(默认值
	 * <code>Integer.MAX_VALUE</code>)个兄弟元素
	 */
	int[] brotherLimit() default { 0, Integer.MAX_VALUE };

	/**
	 * 如果是Array/List类型,返回其组件类型(默认值<code>Object.class</code>)
	 */
	Class<?> componentType() default Object.class;

	/**
	 * 元素文本长度限制(默认值<code>{0, Integer.MAX_VALUE}</code>)
	 */
	int[] lengthBounds() default { 0, Integer.MAX_VALUE };

	/**
	 * 元素文本要匹配的正则表达式(默认值<code>""</code>表示无需正则校验)
	 */
	String regex() default "";

	/**
	 * 是否可序列化
	 */
	boolean serializable() default true;

	/**
	 * 是否元素属性（默认否）
	 */
	boolean attribute() default false;

	/**
	 * 日期/数字等格式
	 */
	String format() default DateUtils.PATTERN_TIMESTAMP;
}
