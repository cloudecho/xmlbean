package org.xmlbean.exception;

public class UnknownDocumentException extends IllegalArgumentException {
	private static final long serialVersionUID = -3124555897600431404L;

	public UnknownDocumentException() {
		super();
	}

	public UnknownDocumentException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownDocumentException(String s) {
		super(s);
	}

	public UnknownDocumentException(Throwable cause) {
		super(cause);
	}

}
