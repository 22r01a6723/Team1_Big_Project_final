package com.smarsh.compliance.handler;

import com.smarsh.compliance.entity.Flag;
import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.models.Message;

import java.util.Optional;

/**
 * Chain Node contract. Each handler may return Optional<Flag>.
 * Handlers should call next.handle(...) to continue the chain.
 */
public interface PolicyHandler {
    boolean supports(String type);
    Optional<Flag> handle(Message message, Policy policy);
    void setNext(PolicyHandler next);
}
