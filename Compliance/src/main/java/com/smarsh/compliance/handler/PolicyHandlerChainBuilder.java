package com.smarsh.compliance.handler;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a chain by collecting all beans of type PolicyHandler and linking them.
 * Order is the iteration order of beans returned by Spring (you can control with @Order or @Component order).
 */
@Component
public class PolicyHandlerChainBuilder {

    private final ApplicationContext ctx;

    public PolicyHandlerChainBuilder(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Returns head of chain (first handler). All handlers are linked to next.
     */
    public PolicyHandler buildChain() {
        String[] beanNames = ctx.getBeanNamesForType(PolicyHandler.class);
        List<PolicyHandler> handlers = new ArrayList<>();
        for (String name : beanNames) {
            PolicyHandler h = (PolicyHandler) ctx.getBean(name);
            handlers.add(h);
        }

        // Link handlers in the order discovered
        PolicyHandler head = null;
        PolicyHandler prev = null;
        for (PolicyHandler h : handlers) {
            if (head == null) head = h;
            if (prev != null) prev.setNext(h);
            prev = h;
        }
        // last handler next = null (default)
        return head;
    }
}
