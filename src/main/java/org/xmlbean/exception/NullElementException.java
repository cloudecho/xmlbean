package org.xmlbean.exception;

/**
 * XML文档缺少元素异常
 */
public class NullElementException extends ElementVisitingException {
	private static final long serialVersionUID = -6226559817837509304L;

	public NullElementException() {
		super();
	}

	public NullElementException(String message, Throwable cause) {
		super(message, cause);
	}

	public NullElementException(String message) {
		super(message);
	}

	public NullElementException(Throwable cause) {
		super(cause);
	}

}
