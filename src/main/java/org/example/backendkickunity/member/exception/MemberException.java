package org.example.backendkickunity.member.exception;

import org.example.backendkickunity.global.exception.BaseException;
import org.example.backendkickunity.global.exception.BaseExceptionType;

public class MemberException extends BaseException {

    private final BaseExceptionType exceptionType;

    public MemberException(BaseExceptionType exceptionType) {
        super(exceptionType.getErrorMessage());  // 예외 메시지를 부모 클래스에 전달
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
