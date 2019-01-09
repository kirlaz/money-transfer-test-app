package com.moneytransfer.exception;

public class RequestParamException extends Exception {

	private static final long serialVersionUID = 1L;

	public RequestParamException(String msg) {
		super(msg);
	}

	public RequestParamException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
