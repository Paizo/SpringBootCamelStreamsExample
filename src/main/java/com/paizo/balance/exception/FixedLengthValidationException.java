package com.paizo.balance.exception;

public class FixedLengthValidationException extends Exception {

    public FixedLengthValidationException(String msg) {
        super(msg);
    }

    public FixedLengthValidationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
