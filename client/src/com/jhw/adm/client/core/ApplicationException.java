package com.jhw.adm.client.core;

/**
 * ϵͳ����ʱ�쳣
 */
public class ApplicationException extends RuntimeException {

	public ApplicationException() {
		super();
	}

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = -8353925453304387358L;
}