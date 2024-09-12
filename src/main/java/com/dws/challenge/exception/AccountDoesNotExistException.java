package com.dws.challenge.exception;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * A RuntimeException class to thrown when requested for Account which does not exist
 */
public class AccountDoesNotExistException extends RuntimeException {
    public AccountDoesNotExistException(@NotNull @NotEmpty String message) {
        super(message);
    }
}
