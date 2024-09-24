package org.example.backedkickunity.domain.member.exception;

import org.example.backedkickunity.exception.BaseException;
import org.example.backedkickunity.exception.BaseExceptionType;

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
