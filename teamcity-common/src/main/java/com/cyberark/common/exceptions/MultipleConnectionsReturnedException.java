package com.cyberark.common.exceptions;

public class MultipleConnectionsReturnedException extends Exception {
    public MultipleConnectionsReturnedException(String errorMessage) {
        super(errorMessage);
    }
}