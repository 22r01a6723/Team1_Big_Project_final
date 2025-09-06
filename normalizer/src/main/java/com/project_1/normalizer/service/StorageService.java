package com.project_1.normalizer.service;

import com.project_1.normalizer.model.CanonicalMessage;

public interface StorageService {
    public void store(CanonicalMessage message,String raw);
}
