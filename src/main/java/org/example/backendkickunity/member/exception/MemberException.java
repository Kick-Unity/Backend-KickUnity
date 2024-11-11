package org.example.backendkickunity.member.exception;

import org.example.backendkickunity.global.exception.BaseException;
import org.example.backendkickunity.global.exception.BaseExceptionType;

public class MemberException extends BaseException {

    private BaseExceptionType exceptionType;

    public MemberException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
