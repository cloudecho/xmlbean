package org.xmlbean.exception;

/**
 * 元素名称相同的元素数目超出范围异常
 */
public class ElementNumberOutOfBoundsException extends ElementVisitingException {
	private static final long serialVersionUID = -6226559817837509304L;

	public ElementNumberOutOfBoundsException() {
		super();
	}

	public ElementNumberOutOfBoundsException(String message, Throwable cause) {
		super(message, cause);
	}

	public ElementNumberOutOfBoundsException(String message) {
		super(message);
	}

	public ElementNumberOutOfBoundsException(Throwable cause) {
		super(cause);
	}

}
