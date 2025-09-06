package com.project_1.normalizer.service;


import com.project_1.normalizer.kafka.ComplianceProducer;
import com.project_1.normalizer.model.CanonicalMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProducerService {

    private ComplianceProducer complianceProducer;
    public ProducerService(ComplianceProducer complianceProducer) {
        this.complianceProducer = complianceProducer;
    }
     public void sendMessage(CanonicalMessage messageJson) {
        try {
           complianceProducer.sendMessage(messageJson);
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
     }
}
