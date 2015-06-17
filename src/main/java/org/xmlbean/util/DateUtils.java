package org.xmlbean.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *日期操作实用类。
 */
public abstract class DateUtils {
	private static final String[] MONTHS_STRING = { "Jan", "Feb", "Mar", "Apr",
			"May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

	private static final String UTC_0800 = "UTC 0800 ";

	/**
	 * 默认日期格式, <code>yyyy-MM-dd</code>
	 */
	public static final String PATTERN_DEFAULT = "yyyy-MM-dd";

	/**
	 * 路径格式, <code>yyyy\MM\dd\</code>
	 */
	public static final String PATTERN_DAYPATH = "yyyy\\MM\\dd\\";

	/**
	 * 日期时间格式, <code>yyyy-MM-dd HH:mm:ss</code>, 24小时制
	 */
	public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 无间隔符的日期时间格式, <code>yyyyMMddHHmmss</code>, 24小时制
	 */
	public static final String PATTERN_DATETIME_COMPACT = "yyyyMMddHHmmss";

	/**
	 * 无间隔符日期格式, <code>yyyyMMdd</code>
	 */
	public static final String PATTERN_DATE_COMPACT = "yyyyMMdd";

	/**
	 * 无间隔符日期格式, <code>yyMMdd</code>
	 */
	public static final String PATTERN_DATESHORT = "yyMMdd";

	/**
	 * 年月, <code>yyyyMM</code>
	 */
	public static final String PATTERN_YEARMONTH = "yyyyMM";

	/**
	 * <b> 根据默认格式(<code>yyyy-MM-dd</code>),格式化日期 </b>
	 * 
	 * @param date
	 *            日期
	 * @return java.util.Date
	 */
	public static String formatDate(Date date) {
		if (date == null)
			return null;
		return new SimpleDateFormat(PATTERN_DEFAULT).format(date);
	}

	/**
	 * <b> 根据指定格式,格式化日期 </b>
	 * 
	 * @param date
	 *            日期
	 * @param pattern
	 *            指定格式,参照类中常量定义
	 * @return java.util.Date
	 */
	public static String formatDate(Date date, String pattern) {
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * <b> 根据指定格式转换字符串为日期 </b> <br>
	 * 
	 * 如果字符串格式不正确,则返回null
	 * 
	 * 
	 * @param dateString
	 *            日期字符串
	 * @param pattern
	 *            指定格式,参照类中常量定义
	 * @return java.util.Date
	 */
	public static Date parseDate(String dateString, String pattern) {
		try {
			return new SimpleDateFormat(pattern).parse(dateString);
		} catch (ParseException pe) {
			return null;
		}
	}

	/**
	 * <b> 将默认格式(<code>yyyy-MM-dd</code>)的日期字符串转换成<code>java.util.Date</code>类型
	 * </b>
	 * 
	 * @param dateString
	 *            日期字符串
	 * @return java.util.Date
	 */
	public static Date parseDate(String dateString) {
		return parseDate(dateString, PATTERN_DEFAULT);
	}

	/**
	 * 将形如 <li>"Thu Dec 6 11:45:13 UTC 0800 2007", 或 <li>
	 * "Thu 12 6 11:45:13 UTC 0800 2007", 或 <li>"Thu Dec 6 11:45:13 2007", 或 <li>
	 * "Thu 12 6 11:45:13 2007" <br>
	 * 的字符串解析为Date对象
	 * 
	 * @return Date对象，解析失败将返回 null
	 * @since 12/06/07
	 */
	public static Date parseDateUTC(String date) {
		date = date.substring(4);
		date = date.replace(UTC_0800, "");
		for (int i = 0; i < MONTHS_STRING.length; i++) {
			if (date.startsWith(MONTHS_STRING[i])) {
				date = date.replace(MONTHS_STRING[i], String.valueOf(i + 1));
				break;
			}
		}

		try {
			SimpleDateFormat df = new SimpleDateFormat("MM dd HH:mm:ss yyyy");
			return df.parse(date);
		} catch (Exception ex) {
			return null;
		}
	}

}
