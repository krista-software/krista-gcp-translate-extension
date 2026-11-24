/*
 * Gcp Translate Extension Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kristasoft.extensions.ai.nlp.gcp.catalog.validators;

import com.kristasoft.extensions.ai.nlp.gcp.catalog.client.GcpTranslateClient;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Orchestrates validation across multiple validators.
 * This class is responsible for managing all validators and coordinating validation operations.
 */
@Service
public class ValidationOrchestrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationOrchestrator.class);
    private final Map<Validator.ValidationResource, Validator> validators = new HashMap<>();

    @Inject
    public ValidationOrchestrator(GcpTranslateClient gcpTranslateClient) {
        // Initialize all validators
        validators.put(Validator.ValidationResource.TEXT_INPUT, new TextInputValidator());
        validators.put(Validator.ValidationResource.SOURCE_LANGUAGE, 
            new LanguageValidator(gcpTranslateClient, Validator.ValidationResource.SOURCE_LANGUAGE));
        validators.put(Validator.ValidationResource.TARGET_LANGUAGE, 
            new LanguageValidator(gcpTranslateClient, Validator.ValidationResource.TARGET_LANGUAGE));
    }

    /**
     * Represents the result of a validation operation.
     */
    public static class ValidationResult {
        private final String confirmStepMessage;
        private final String fetchFieldName;
        private final String fetchStepMessage;
        private final String errMessage;
        private final String fieldType;

        public ValidationResult(String confirmStepMessage, String fetchFieldName,
                                String fetchStepMessage, String errMessage, String fieldType) {
            this.confirmStepMessage = confirmStepMessage;
            this.fetchFieldName = fetchFieldName;
            this.fetchStepMessage = fetchStepMessage;
            this.errMessage = errMessage;
            this.fieldType = fieldType;
        }

        // Getters
        public String getConfirmStepMessage() {
            return confirmStepMessage;
        }

        public String getErrMessage() {
            return errMessage;
        }

        public String getFetchFieldName() {
            return fetchFieldName;
        }

        public String getFieldType() {
            return fieldType;
        }

        public String getFetchStepMessage() {
            return fetchStepMessage;
        }
    }

    /**
     * Validates a map of resources against their respective validators
     *
     * @param resources Map of resource types to resource values
     * @return List of validation results for invalid resources
     */
    public List<ValidationResult> validate(Map<Validator.ValidationResource, String> resources) {
        List<ValidationResult> results = new ArrayList<>();

        for (Map.Entry<Validator.ValidationResource, String> entry : resources.entrySet()) {
            Validator validator = validators.get(entry.getKey());
            if (validator != null) {
                try {
                    if (!validator.validate(entry.getValue(), resources)) {
                        results.add(new ValidationResult(
                                validator.getConfirmStepMessage(entry.getValue(), resources),
                                validator.getFetchFieldName(),
                                validator.getFetchStepMessage(),
                                validator.getErrMessage(entry.getValue()),
                                validator.getFieldType()
                        ));
                    }
                } catch (Exception cause) {
                    LOGGER.error("Error during validation for resource {}: {}", entry.getKey(), cause.getMessage(), cause);
                    results.add(new ValidationResult(
                            "An error occurred during validation. Would you like to try again?",
                            validator.getFetchFieldName(),
                            "Please provide a valid value.",
                            "Validation error: " + cause.getMessage(),
                            validator.getFieldType()
                    ));
                }
            }
        }

        return results;
    }
}

