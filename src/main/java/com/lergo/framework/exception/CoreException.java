package com.lergo.framework.exception;

public class CoreException extends RuntimeException {

    private ErrorMessage errorMessage;

    public CoreException() {
        super();
    }

    public CoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoreException(String message) {
        super(message);
    }

    public CoreException(Throwable cause) {
        super(cause);
    }

    public CoreException(ErrorMessage errorMessage) {
        super(String.format("{\"code\":\"%s\", \"msg\":\"%s\"}", errorMessage.getCode(), errorMessage.getMessage()));
        this.errorMessage = errorMessage;
    }

    public CoreException(ErrorMessage errorMessage, Throwable cause) {
        this(cause);
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
