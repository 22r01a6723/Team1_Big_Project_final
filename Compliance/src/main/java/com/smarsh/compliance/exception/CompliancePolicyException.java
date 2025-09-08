package com.smarsh.compliance.exception;

public class CompliancePolicyException extends ComplianceException {
    public CompliancePolicyException(String message) { super(message); }
    public CompliancePolicyException(String message, Throwable cause) { super(message, cause); }
}

