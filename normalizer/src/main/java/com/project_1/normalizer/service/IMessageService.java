package com.project_1.normalizer.service;

import com.project_1.normalizer.model.CanonicalMessage;

public interface IMessageService {
    CanonicalMessage processMessage(String json) throws Exception;
}

