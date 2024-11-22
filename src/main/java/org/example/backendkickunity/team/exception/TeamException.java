package org.example.backendkickunity.team.exception;

import org.example.backendkickunity.global.exception.BaseException;
import org.example.backendkickunity.global.exception.BaseExceptionType;

public class TeamException extends BaseException {

    private final BaseExceptionType exceptionType;

    public TeamException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}