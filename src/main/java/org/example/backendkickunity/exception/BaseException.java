package org.example.backendkickunity.exception;

//앞으로 정의할 모든 custom 예외의 부모 클래스 -> 모든 custom 예외 클래스들을 BaseException 타입으로 처리
public abstract class BaseException extends RuntimeException{

    //BaseExceptionType을 반환하는 메서드
    public abstract BaseExceptionType getExceptionType();
}


