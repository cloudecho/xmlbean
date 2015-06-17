package org.xmlbean.exception;

/**
 * 文本不匹配给定的正则表达式异常
 */
public class RegexNotMatchedException extends TextFormatException {
	private static final long serialVersionUID = -6226559817837509304L;

	public RegexNotMatchedException() {
		super();
	}

	public RegexNotMatchedException(String message, Throwable cause) {
		super(message, cause);
	}

	public RegexNotMatchedException(String message) {
		super(message);
	}

	public RegexNotMatchedException(Throwable cause) {
		super(cause);
	}

}
