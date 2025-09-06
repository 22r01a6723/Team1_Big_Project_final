package com.project_1.normalizer.util;

import com.project_1.normalizer.util.adapters.MessageAdapter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageAdapterFactory {

    private final List<MessageAdapter> adapters;

    public MessageAdapterFactory(List<MessageAdapter> adapters) {
        this.adapters = adapters;
    }

    public MessageAdapter getAdapter(String network) {
        return adapters.stream()
                .filter(a -> a.supports(network))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No adapter for network: " + network));
    }
}

