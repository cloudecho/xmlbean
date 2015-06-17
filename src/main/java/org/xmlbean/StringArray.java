package org.xmlbean;

/**
 * 此类是对<tt>java.lang.String[]</tt>的简单封装
 */
public class StringArray {
	private String[] value;

	public StringArray() {

	}

	public StringArray(String[] value) {
		this.value = value;
	}

	public String[] getValue() {
		return value;
	}

	public void setValue(String[] value) {
		this.value = value;
	}

	/**
	 * 返回一个代表该对象的字符串.
	 * 
	 * @return 如果<tt>value</tt>为<tt>null</tt>,返回"null"; 否则返回<tt>value</tt>
	 *         的拼串(;分割)
	 */
	public String toString() {
		if (value == null)
			return "null";

		StringBuffer buf = new StringBuffer();
		for (String v : value)
			buf.append(v).append(';');

		return buf.toString();
	}
}
