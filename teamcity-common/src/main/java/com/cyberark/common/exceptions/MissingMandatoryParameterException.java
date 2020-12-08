package com.cyberark.common.exceptions;

public class MissingMandatoryParameterException extends Exception {
    public MissingMandatoryParameterException(String errorMessage) {
        super(errorMessage);
    }
}