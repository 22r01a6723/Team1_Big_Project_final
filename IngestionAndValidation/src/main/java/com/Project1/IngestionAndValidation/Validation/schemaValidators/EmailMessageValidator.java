package com.Project1.IngestionAndValidation.Validation.schemaValidators;

import com.Project1.IngestionAndValidation.Validation.AbstractJsonSchemaValidator;
import org.springframework.stereotype.Component;


@Component
public class EmailMessageValidator extends AbstractJsonSchemaValidator {
    public EmailMessageValidator() {
        super("/schemas/email-schema.json");
    }

    @Override
    public String getNetwork() {
        return "email";
    }
}

