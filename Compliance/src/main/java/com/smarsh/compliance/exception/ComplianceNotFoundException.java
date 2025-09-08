package com.smarsh.compliance.exception;

public class ComplianceNotFoundException extends ComplianceException {
    public ComplianceNotFoundException(String message) { super(message); }
    public ComplianceNotFoundException(String message, Throwable cause) { super(message, cause); }
}
