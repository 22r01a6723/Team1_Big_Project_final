package com.smarsh.compliance.handler;

import com.smarsh.compliance.entity.Flag;
import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.models.Message;

import java.util.Optional;

/**
 * Base implementation that maintains a next pointer and delegates.
 */
public abstract class AbstractPolicyHandler implements PolicyHandler {

    protected PolicyHandler next;

    @Override
    public void setNext(PolicyHandler next) {
        this.next = next;
    }

    /**
     * If concrete handler chooses not to short-circuit, it should call superHandle to continue.
     */
    protected Optional<Flag> superHandle(Message message, Policy policy) {
        if (next != null) {
            return next.handle(message, policy);
        }
        return Optional.empty();
    }
}
