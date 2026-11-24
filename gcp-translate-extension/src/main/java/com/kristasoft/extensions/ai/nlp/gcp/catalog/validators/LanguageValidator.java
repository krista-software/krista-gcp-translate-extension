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
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Validator for language code fields.
 * Validates that language codes are valid and supported by GCP Translation API.
 */
public class LanguageValidator implements Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageValidator.class);
    private final GcpTranslateClient gcpTranslateClient;
    private final ValidationResource resourceType;

    public LanguageValidator(GcpTranslateClient gcpTranslateClient, ValidationResource resourceType) {
        this.gcpTranslateClient = gcpTranslateClient;
        this.resourceType = resourceType;
    }

    @Override
    public boolean validate(String value, Map<ValidationResource, String> resources) {

        // If value is null or empty, it's only valid for SOURCE_LANGUAGE (auto-detect)
        if (value == null || value.trim().isEmpty()) {
            boolean isValid = resourceType == ValidationResource.SOURCE_LANGUAGE;
            if (!isValid) {
                LOGGER.warn("Language validation failed: {} is required", resourceType);
            }
            return isValid;
        }

        try {
            // Get supported languages
            List<String> supportedLanguages = gcpTranslateClient.getSupportedLanguages();

            // Check if language is supported (case-sensitive - must match exactly)
            boolean isSupported = supportedLanguages.stream()
                .anyMatch(lang -> lang.equals(value));

            if (!isSupported) {
                LOGGER.warn("Language validation failed: {} is not a supported language", value);
                return false;
            }

            // If this is target language, check it's not the same as source (case-sensitive)
            if (resourceType == ValidationResource.TARGET_LANGUAGE) {
                String sourceLanguage = resources.get(ValidationResource.SOURCE_LANGUAGE);
                if (sourceLanguage != null && !sourceLanguage.trim().isEmpty()
                    && sourceLanguage.equals(value)) {
                    LOGGER.warn("Language validation failed: source and target languages are the same");
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            LOGGER.error("Error validating language: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getConfirmStepMessage(String value, Map<ValidationResource, String> resources) {
        if (value == null || value.trim().isEmpty()) {
            return String.format("We need you to specify the %s. Would you like to enter it now?",
                getFieldName());
        }

        // First check if the language is valid/supported (case-sensitive)
        try {
            List<String> supportedLanguages = gcpTranslateClient.getSupportedLanguages();
            boolean isSupported = supportedLanguages.stream()
                .anyMatch(lang -> lang.equals(value));

            if (!isSupported) {
                // Language is not valid/supported - this is the primary error
                return String.format("We don't recognize '%s' as a supported %s. " +
                    "Would you like to choose a different language? You can use the 'Get Supported Languages' request to see all available options, " +
                    "or try common codes like 'en' (English), 'es' (Spanish), 'fr' (French), 'de' (German), or 'ja' (Japanese).",
                    value, getFieldName());
            }
        } catch (Exception e) {
            LOGGER.error("Error checking supported languages: {}", e.getMessage(), e);
            return String.format("We couldn't verify if '%s' is a supported %s. " +
                "Would you like to try a different language? Common codes include 'en' (English), 'es' (Spanish), 'fr' (French), 'de' (German), or 'ja' (Japanese). " +
                "You can also use the 'Get Supported Languages' request to see all options.",
                value, getFieldName());
        }

        // Only check if same as source if the language is valid
        if (resourceType == ValidationResource.TARGET_LANGUAGE) {
            String sourceLanguage = resources.get(ValidationResource.SOURCE_LANGUAGE);
            // Only check if source language is provided (not null and not empty)
            if (sourceLanguage != null && !sourceLanguage.trim().isEmpty()
                && sourceLanguage.equals(value)) {
                return "Your source and target languages are the same, so there's nothing to translate. Would you like to choose a different target language?";
            }
        }

        // This shouldn't be reached, but return a generic message just in case
        return String.format("There's an issue with the %s '%s'. " +
            "Would you like to try a different language? Use 'Get Supported Languages' to see all available options.",
            getFieldName(), value);
    }

    @Override
    public String getFetchFieldName() {
        return getFieldName();
    }

    @Override
    public String getFetchStepMessage() {
        if (resourceType == ValidationResource.SOURCE_LANGUAGE) {
            return "Please enter the source language code (like 'en' for English or 'es' for Spanish). " +
                "You can also leave this empty to automatically detect the language. " +
                "Use the 'Get Supported Languages' request to see all available language codes.";
        } else {
            return "Please enter the target language code (like 'en' for English or 'es' for Spanish). " +
                "Use the 'Get Supported Languages' request to see all available language codes.";
        }
    }

    @Override
    public String getErrMessage(String value) {
        if (value == null || value.trim().isEmpty()) {
            return String.format(Constants.LANGUAGE_REQUIRED, getFieldName());
        }

        // Check if same as source
        if (resourceType == ValidationResource.TARGET_LANGUAGE) {
            // This will be checked in validate method
            return Constants.LANGUAGE_SAME;
        }

        return String.format(Constants.LANGUAGE_INVALID, value);
    }

    @Override
    public String getFieldType() {
        return "Text";
    }

    private String getFieldName() {
        return resourceType == ValidationResource.SOURCE_LANGUAGE ? "Source Language" : "Target Language";
    }
}

