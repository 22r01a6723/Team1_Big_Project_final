package com.smarsh.compliance.service;

import com.smarsh.compliance.entity.KeywordPolicy;
import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.entity.RegexPolicy;

public class PolicyMapper {
    public static  KeywordPolicy getKeywordPolicy(Policy policy) {
        if (!(policy instanceof KeywordPolicy)) {
            throw new com.smarsh.compliance.exception.CompliancePolicyException(
                "Provided policy is not a KeywordPolicy. Actual type: " + policy.getClass().getSimpleName()
            );
        }
        return (KeywordPolicy) policy;
    }

    public static RegexPolicy getRegexPolicy(Policy policy) {
        if (!(policy instanceof RegexPolicy)) {
            throw new com.smarsh.compliance.exception.CompliancePolicyException(
                "Provided policy is not a RegexPolicy. Actual type: " + policy.getClass().getSimpleName()
            );
        }
        return (RegexPolicy) policy;
    }
}
