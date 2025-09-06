package com.Project1.IngestionAndValidation.Validation.schemaValidators;

import com.Project1.IngestionAndValidation.Validation.AbstractJsonSchemaValidator;
import org.springframework.stereotype.Component;


@Component
public class SlackMessageValidator extends AbstractJsonSchemaValidator {
    public SlackMessageValidator() {
        super("/schemas/slack-schema.json");
    }

    @Override
    public String getNetwork() {
        return "slack";
    }
}

