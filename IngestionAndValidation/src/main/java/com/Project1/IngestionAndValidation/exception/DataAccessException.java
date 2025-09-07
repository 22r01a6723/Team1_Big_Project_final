package com.Project1.IngestionAndValidation.exception;

public class DataAccessException extends CompanyVaultException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}