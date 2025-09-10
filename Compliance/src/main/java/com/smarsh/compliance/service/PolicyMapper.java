package com.smarsh.compliance.service;

import com.smarsh.compliance.entity.KeywordPolicy;
import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.entity.RegexPolicy;
import com.smarsh.compliance.exception.CompliancePolicyException;

public class PolicyMapper {

    public static KeywordPolicy getKeywordPolicy(Policy policy) {
        if (!(policy instanceof KeywordPolicy)) {
            throw new CompliancePolicyException("Provided policy is not a KeywordPolicy. Actual: " + policy.getClass().getSimpleName());
        }
        return (KeywordPolicy) policy;
    }

    public static RegexPolicy getRegexPolicy(Policy policy) {
        if (!(policy instanceof RegexPolicy)) {
            throw new CompliancePolicyException("Provided policy is not a RegexPolicy. Actual: " + policy.getClass().getSimpleName());
        }
        return (RegexPolicy) policy;
    }
}
