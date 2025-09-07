package com.Project1.IngestionAndValidation.Validation;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * SOLID Principles:
 * - SRP: Only responsible for validating messages against a JSON schema.
 * - OCP: Can be extended for new schema types without modification.
 * - LSP: Subclasses can be used wherever MessageValidator is expected.
 * - ISP: Implements focused MessageValidator interface.
 * - DIP: Depends on MessageValidator abstraction.
 */
public abstract class AbstractJsonSchemaValidator implements MessageValidator {

    private final Schema schema;

    public AbstractJsonSchemaValidator(String schemaPath) {
        JSONObject schemaJson = new JSONObject(
                new JSONTokener(this.getClass().getResourceAsStream(schemaPath))
        );
        this.schema = SchemaLoader.load(schemaJson);
    }

    @Override
    public void validate(String payload) throws ValidationException {
        JSONObject input = new JSONObject(payload);
        schema.validate(input); // throws ValidationException if invalid
    }

    /**
     * Template method for full validation workflow.
     */
    public void validateMessage(String payload) throws ValidationException {
        preValidate(payload);
        validate(payload);
        postValidate(payload);
    }

    /**
     * Hook for pre-validation logic (optional).
     */
    protected void preValidate(String payload) {
        // Default: do nothing
    }

    /**
     * Hook for post-validation logic (optional).
     */
    protected void postValidate(String payload) {
        // Default: do nothing
    }
}
