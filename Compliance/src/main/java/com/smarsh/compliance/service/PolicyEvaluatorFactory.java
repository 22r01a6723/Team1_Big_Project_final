package com.smarsh.compliance.service;

import com.smarsh.compliance.evaluators.PolicyEvaluator;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Factory Pattern: Selects the correct PolicyEvaluator strategy based on policy type.
 * Supports OCP and DIP by centralizing evaluator selection logic.
 */
@Service
public class PolicyEvaluatorFactory {
    private final List<PolicyEvaluator> evaluators;

    public PolicyEvaluatorFactory(List<PolicyEvaluator> evaluators) {
        this.evaluators = evaluators;
    }

    public PolicyEvaluator getEvaluator(String type) {
        return evaluators.stream()
                .filter(e -> e.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No evaluator for type: " + type));
    }
}

