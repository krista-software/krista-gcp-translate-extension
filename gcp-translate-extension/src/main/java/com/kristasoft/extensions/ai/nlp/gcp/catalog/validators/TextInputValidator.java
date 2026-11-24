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

import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Validator for text input fields.
 * Validates that text is not null, not empty, and within length limits.
 */
public class TextInputValidator implements Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextInputValidator.class);

    @Override
    public boolean validate(String value, Map<ValidationResource, String> resources) {
        if (value == null || value.trim().isEmpty()) {
            LOGGER.warn("Text validation failed: text is null or empty");
            return false;
        }

        if (value.length() > Constants.MAX_TEXT_LENGTH) {
            LOGGER.warn("Text validation failed: text length {} exceeds maximum {}", 
                value.length(), Constants.MAX_TEXT_LENGTH);
            return false;
        }

        return true;
    }

    @Override
    public String getConfirmStepMessage(String value, Map<ValidationResource, String> resources) {
        if (value == null || value.trim().isEmpty()) {
            return "We need some text to work with. Would you like to enter the text you want to translate or detect?";
        }

        if (value.length() > Constants.MAX_TEXT_LENGTH) {
            return String.format("Your text is %,d characters long, but the maximum allowed is %,d characters. " +
                "Would you like to shorten your text or split it into smaller parts?",
                value.length(), Constants.MAX_TEXT_LENGTH);
        }

        return "The text you entered isn't valid. Would you like to try entering it again?";
    }

    @Override
    public String getFetchFieldName() {
        return "Text";
    }

    @Override
    public String getFetchStepMessage() {
        return String.format("Please enter the text you want to translate or detect. You can enter up to %,d characters.",
            Constants.MAX_TEXT_LENGTH);
    }

    @Override
    public String getErrMessage(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Constants.TEXT_REQUIRED;
        }

        if (value.length() > Constants.MAX_TEXT_LENGTH) {
            return String.format(Constants.TEXT_TOO_LONG, value.length(), Constants.MAX_TEXT_LENGTH);
        }

        return "The text you entered isn't in a valid format. Please check and try again.";
    }

    @Override
    public String getFieldType() {
        return "Paragraph";
    }
}

