package com.dws.challenge.exception;

/**
 * A RuntimeException thrown when the account is having insufficient balance to perform the desired operation
 */
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
