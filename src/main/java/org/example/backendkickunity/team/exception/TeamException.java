package org.example.backendkickunity.team.exception;

import org.example.backendkickunity.global.exception.BaseException;
import org.example.backendkickunity.global.exception.BaseExceptionType;

public class TeamException extends BaseException {

    private final BaseExceptionType exceptionType;

    public TeamException(BaseExceptionType exceptionType) {
        super(exceptionType.getErrorMessage());  // 예외 메시지를 부모 클래스에 전달
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
