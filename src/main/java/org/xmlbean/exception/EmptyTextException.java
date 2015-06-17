package org.xmlbean.exception;

/**
 * 元素空文本异常
 */
public class EmptyTextException extends ElementVisitingException {
	private static final long serialVersionUID = -6226559817837509304L;

	public EmptyTextException() {
		super();
	}

	public EmptyTextException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmptyTextException(String message) {
		super(message);
	}

	public EmptyTextException(Throwable cause) {
		super(cause);
	}

}
