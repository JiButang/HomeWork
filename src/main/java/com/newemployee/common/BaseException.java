package com.newemployee.common;

public class BaseException extends RuntimeException{

    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
    }

    //扔出异常
    public static void toss(String message) {
        throw new BaseException(message);
    }
}
