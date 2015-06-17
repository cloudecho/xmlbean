package org.xmlbean.util;

import java.io.InputStream;

public abstract class PubUtils {
	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public static boolean isBlank(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static boolean hasLength(String s) {
		return !isEmpty(s);
	}

	public static boolean hasText(String s) {
		return !isBlank(s);
	}

	/**
	 * 空字符串或<code>null</code>或<code>"null"</code>返回<code>true</code>
	 */
	public static boolean isNull(String s) {
		return s == null || s.length() == 0 || "null".equals(s);
	}

	/**
	 * (Copy from org.springframework.util.StringUtils.java)<br>
	 * Replace all occurences of a substring within a string with another
	 * string.
	 * 
	 * @param inString
	 *            String to examine
	 * @param oldPattern
	 *            String to replace
	 * @param newPattern
	 *            String to insert
	 * @return a String with the replacements
	 */
	public static String replace(String inString, String oldPattern,
			String newPattern) {
		if (!hasLength(inString) || !hasLength(oldPattern)
				|| newPattern == null) {
			return inString;
		}
		StringBuilder sb = new StringBuilder();
		int pos = 0; // our position in the old string
		int index = inString.indexOf(oldPattern);
		// the index of an occurrence we've found, or -1
		int patLen = oldPattern.length();
		while (index >= 0) {
			sb.append(inString.substring(pos, index));
			sb.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		sb.append(inString.substring(pos));
		// remember to append any characters to the right of a match
		return sb.toString();
	}

	/** 第一个字符转为大写 */
	public static String toTitle(String s) {
		if (isEmpty(s))
			return s;

		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	/** 去掉下划线，并将下划线后的字母大写 */
	public static String prettyWord(String s) {
		if (isEmpty(s))
			return s;

		String[] words = s.split("_");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < words.length; i++) {
			sb.append(i == 0 ? words[i] : toTitle(words[i]));
		}

		return sb.toString();
	}

	/** 第一个字符转为小写 */
	public static String unTitle(String s) {
		if (isEmpty(s))
			return s;

		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}

	/** 去掉下划线，并将下划线后的字母大写。第一个字符转为大写 */
	public static String toTitlePretty(String s) {
		if (isEmpty(s))
			return s;

		return toTitle(prettyWord(s));
	}

	/** 去掉下划线，并将下划线后的字母大写。第一个字符转为大写 */
	public static String unTitlePretty(String s) {
		if (isEmpty(s))
			return s;

		return unTitle(prettyWord(s));
	}

	public static InputStream getResourceAsStream(String file) {
		return Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(file);
	}

	/**
	 * 返回首尾无空格的字符串
	 */
	public static String trim(String str) {
		if (str == null)
			return str;

		return str.replaceAll("^\\s+|\\s+$", "");
	}

	public static String stripToEmpty(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}

	// public static void main(String[] args) {
	// System.out.println(prettyWord("hello_world_jerry"));
	// }
}
