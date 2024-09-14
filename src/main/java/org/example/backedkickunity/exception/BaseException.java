package org.example.backedkickunity.exception;

public abstract class BaseException extends RuntimeException {

    public BaseException() {
    }

    public abstract BaseExceptionType exceptionType();

}
