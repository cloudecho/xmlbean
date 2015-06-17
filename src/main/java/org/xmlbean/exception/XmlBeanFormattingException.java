package org.xmlbean.exception;

public class XmlBeanFormattingException extends RuntimeException {
	private static final long serialVersionUID = 3607157081690058374L;

	public XmlBeanFormattingException() {
		super();
	}

	public XmlBeanFormattingException(String message, Throwable cause) {
		super(message, cause);
	}

	public XmlBeanFormattingException(String message) {
		super(message);
	}

	public XmlBeanFormattingException(Throwable cause) {
		super(cause);
	}

}
