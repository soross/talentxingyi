package com.snda.inote.exception;

public class ControllerException extends Exception {
	private static final long serialVersionUID = 1L;
	private Throwable cause = this;

    private int errorCode = -1;
    private String message;
    private Object[] params;

    public int getErrorCode() {
        return this.errorCode;
    }


    public Object[] getParams() {
        return params;
    }

    public ControllerException(String message) {
        this.message = message;
    }


    public ControllerException(Throwable cause) {
        fillInStackTrace();
        this.message = (cause==null ? null : cause.toString());
        this.cause = cause;
    }
    
    public ControllerException(String message, Throwable cause) {
        fillInStackTrace();
        this.message = message;
        this.cause = cause;
    }


    public ControllerException(int errorCode, Object ... params) {
        this.errorCode = errorCode;
        this.params = params;
        this.message = new StringBuffer("error code is:").append(errorCode).toString();
    }

    public ControllerException(int errorCode, Throwable cause, Object ... params) {
        fillInStackTrace();
        this.errorCode = errorCode;
        this.params = params;
        this.message = new StringBuffer("error code is:").append(errorCode).toString();
        this.cause = cause;
    }

    public ControllerException(int errorCode) {
        this.errorCode = errorCode;
        this.message = new StringBuffer("error code is:").append(errorCode).toString();
    }

    public String getMessage() {
        return this.message;
    }

    public Throwable getCause() {
        return (cause==this ? null : cause);
    }
}
