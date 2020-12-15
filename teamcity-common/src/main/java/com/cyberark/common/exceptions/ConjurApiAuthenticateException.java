package com.cyberark.common.exceptions;

public class ConjurApiAuthenticateException extends Exception {
    public ConjurApiAuthenticateException(String errorMessage) {
        super(errorMessage);
    }
}
