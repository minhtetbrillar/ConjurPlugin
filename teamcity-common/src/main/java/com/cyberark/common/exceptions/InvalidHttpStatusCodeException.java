package com.cyberark.common.exceptions;

public class InvalidHttpStatusCodeException extends Exception {
    public InvalidHttpStatusCodeException(String errorMessage) {
        super(errorMessage);
    }
}