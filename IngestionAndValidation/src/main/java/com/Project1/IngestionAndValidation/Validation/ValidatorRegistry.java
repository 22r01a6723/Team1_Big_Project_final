package com.Project1.IngestionAndValidation.Validation;

import com.Project1.IngestionAndValidation.exception.UnsupportedNetworkException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * SOLID Principles:
 * - SRP: Only responsible for managing validator instances.
 * - OCP: Can register new validators without modifying existing code.
 * - DIP: Depends on MessageValidator abstraction.
 */
@Service
public class ValidatorRegistry {
    private final Map<String, MessageValidator> validators = new HashMap<>();

    public ValidatorRegistry(MessageValidator... validatorList) {
        for (MessageValidator v : validatorList) {
            validators.put(v.getNetwork().toLowerCase(), v);
        }
    }

    public MessageValidator getValidator(String network) {
        System.out.println(validators.size());
//        if (!validators.containsKey(network.toLowerCase())) {
//            throw new IllegalArgumentException("No validator found for network: " + network);
//        }
        if (!validators.containsKey(network.toLowerCase())) {
            throw new UnsupportedNetworkException("Unsupported network: " + network);
        }

        return validators.get(network.toLowerCase());
    }

    public void registerValidator(String network, MessageValidator validator) {
        validators.put(network.toLowerCase(), validator);
    }
}
