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

package com.kristasoft.extensions.ai.nlp.gcp.catalog.extresp;

import app.krista.model.field.NamedField;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.Constants;

import java.util.Collections;
import java.util.Map;

/**
 * Factory class for creating NamedField instances.
 */
public class NamedFieldFactory {

    private NamedFieldFactory() {
        // Utility class
    }

    /**
     * Creates a switch field (boolean toggle).
     */
    public static NamedField createSwitchField(String fieldName) {
        return new NamedField(fieldName, Constants.FIELD_TYPE_SWITCH, Collections.emptyMap(), Collections.emptyMap());
    }

    /**
     * Creates a field with specified type and attributes.
     * Converts short field type names (e.g., "Text", "Paragraph") to full logical names.
     */
    public static NamedField createFieldWithAttributes(String fieldName, String fieldType, Map<String, Object> attributes) {
        Map<String, Object> finalAttributes = attributes != null ? attributes : Collections.emptyMap();
        String logicalName = convertToLogicalName(fieldType);
        return new NamedField(fieldName, logicalName, finalAttributes, Collections.emptyMap());
    }

    /**
     * Converts a short field type name to its full logical name.
     * E.g., "Text" -> "com.krista.fields.Text"
     */
    private static String convertToLogicalName(String fieldType) {
        if (fieldType == null || fieldType.isEmpty()) {
            return Constants.FIELD_TYPE_TEXT; // Default to Text
        }

        if (fieldType.startsWith(Constants.FIELD_TYPE_PREFIX)) {
            return fieldType;
        }

        // Convert short name to logical name
        return Constants.FIELD_TYPE_PREFIX + fieldType;
    }
}

