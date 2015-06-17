package org.xmlbean.exception;

/**
 * ElelementVisitor在visit(..)方法执行中发生的异常
 */
public class ElementVisitingException extends RuntimeException {
	private static final long serialVersionUID = -5544465583178786244L;

	public ElementVisitingException() {
		super();
	}

	public ElementVisitingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ElementVisitingException(String message) {
		super(message);
	}

	public ElementVisitingException(Throwable cause) {
		super(cause);
	}

}
