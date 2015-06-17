package org.xmlbean.exception;
/**
 * 文本格式异常
 */
public class TextFormatException extends ElementVisitingException {
	private static final long serialVersionUID = -6226559817837509304L;

	public TextFormatException() {
		super();		
	}

	public TextFormatException(String message, Throwable cause) {
		super(message, cause);		
	}

	public TextFormatException(String message) {
		super(message);		
	}

	public TextFormatException(Throwable cause) {
		super(cause);		
	}

}
