package com.Project1.IngestionAndValidation.Validation;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

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
}
