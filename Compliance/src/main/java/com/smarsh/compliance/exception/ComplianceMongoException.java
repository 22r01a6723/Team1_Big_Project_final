package com.smarsh.compliance.exception;

public class ComplianceMongoException extends ComplianceException {
    public ComplianceMongoException(String message) { super(message); }
    public ComplianceMongoException(String message, Throwable cause) { super(message, cause); }
}

