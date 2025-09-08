package com.smarsh.compliance.exception;

public class ComplianceDuplicateException extends ComplianceException {
    public ComplianceDuplicateException(String message) { super(message); }
    public ComplianceDuplicateException(String message, Throwable cause) { super(message, cause); }
}

