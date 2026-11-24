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

import java.util.Map;

/**
 * Interface for all validators in the GCP Translate extension.
 * Validators are responsible for validating specific resources (text, language codes, etc.)
 * and providing user-friendly error messages.
 */
public interface Validator {

    /**
     * Enum representing different validation resources
     */
    enum ValidationResource {
        TEXT_INPUT,
        SOURCE_LANGUAGE,
        TARGET_LANGUAGE
    }

    /**
     * Validates the given resource value
     *
     * @param value     The value to validate
     * @param resources Map of all resources being validated (for cross-field validation)
     * @return true if valid, false otherwise
     */
    boolean validate(String value, Map<ValidationResource, String> resources);

    /**
     * Gets the confirmation step message to show the user when validation fails
     *
     * @param value     The invalid value
     * @param resources Map of all resources
     * @return User-friendly confirmation message
     */
    String getConfirmStepMessage(String value, Map<ValidationResource, String> resources);

    /**
     * Gets the field name for fetching a new value
     *
     * @return Field name
     */
    String getFetchFieldName();

    /**
     * Gets the fetch step message to prompt user for new value
     *
     * @return Fetch step message
     */
    String getFetchStepMessage();

    /**
     * Gets the error message for the invalid value
     *
     * @param value The invalid value
     * @return Error message
     */
    String getErrMessage(String value);

    /**
     * Gets the field type for the validator
     *
     * @return Field type (e.g., "Text", "Paragraph")
     */
    String getFieldType();
}

