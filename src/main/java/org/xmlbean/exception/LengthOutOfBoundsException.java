package org.xmlbean.exception;

/**
 * 文本长度不在规定的范围内异常
 */
public class LengthOutOfBoundsException extends TextFormatException {
	private static final long serialVersionUID = -6226559817837509304L;

	public LengthOutOfBoundsException() {
		super();
	}

	public LengthOutOfBoundsException(String message, Throwable cause) {
		super(message, cause);
	}

	public LengthOutOfBoundsException(String message) {
		super(message);
	}

	public LengthOutOfBoundsException(Throwable cause) {
		super(cause);
	}

}
