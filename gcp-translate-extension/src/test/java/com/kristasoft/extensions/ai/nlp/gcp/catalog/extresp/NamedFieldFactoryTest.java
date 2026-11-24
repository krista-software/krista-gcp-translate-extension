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
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NamedFieldFactory
 */
class NamedFieldFactoryTest {

    @Test
    void testCreateSwitchField() {
        // Arrange
        String fieldName = "REENTER";

        // Act
        NamedField field = NamedFieldFactory.createSwitchField(fieldName);

        // Assert
        assertNotNull(field);
        assertEquals(fieldName, field.getName());
        assertEquals(Constants.FIELD_TYPE_SWITCH, field.getFieldType());
    }

    @Test
    void testCreateSwitchFieldWithNullName() {
        // Act
        NamedField field = NamedFieldFactory.createSwitchField(null);

        // Assert
        assertNotNull(field);
        assertNull(field.getName());
        assertEquals(Constants.FIELD_TYPE_SWITCH, field.getFieldType());
    }

    @Test
    void testCreateFieldWithAttributesTextType() {
        // Arrange
        String fieldName = "TARGET_LANGUAGE";
        String fieldType = "Text";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("required", true);
        attributes.put("maxLength", 100);

        // Act
        NamedField field = NamedFieldFactory.createFieldWithAttributes(fieldName, fieldType, attributes);

        // Assert
        assertNotNull(field);
        assertEquals(fieldName, field.getName());
        assertEquals(Constants.FIELD_TYPE_TEXT, field.getFieldType());
    }

    @Test
    void testCreateFieldWithAttributesParagraphType() {
        // Arrange
        String fieldName = "TEXT";
        String fieldType = "Paragraph";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("rows", 5);

        // Act
        NamedField field = NamedFieldFactory.createFieldWithAttributes(fieldName, fieldType, attributes);

        // Assert
        assertNotNull(field);
        assertEquals(fieldName, field.getName());
        assertEquals(Constants.FIELD_TYPE_PARAGRAPH, field.getFieldType());
    }

    @Test
    void testCreateFieldWithNullAttributes() {
        // Arrange
        String fieldName = "SOURCE_LANGUAGE";
        String fieldType = "Text";

        // Act
        NamedField field = NamedFieldFactory.createFieldWithAttributes(fieldName, fieldType, null);

        // Assert
        assertNotNull(field);
        assertEquals(fieldName, field.getName());
        assertEquals(Constants.FIELD_TYPE_TEXT, field.getFieldType());
    }

    @Test
    void testCreateFieldWithEmptyAttributes() {
        // Arrange
        String fieldName = "FIELD";
        String fieldType = "Text";
        Map<String, Object> attributes = new HashMap<>();

        // Act
        NamedField field = NamedFieldFactory.createFieldWithAttributes(fieldName, fieldType, attributes);

        // Assert
        assertNotNull(field);
        // Field should be created successfully even with empty attributes
    }

    @Test
    void testCreateFieldWithDifferentTypes() {
        // Arrange
        String[] fieldTypes = {"Text", "Paragraph", "Number", "Date", "Switch"};

        // Act & Assert
        for (String type : fieldTypes) {
            NamedField field = NamedFieldFactory.createFieldWithAttributes("TestField", type, null);
            assertNotNull(field);
            assertTrue(field.getFieldType().startsWith(Constants.FIELD_TYPE_PREFIX));
            assertTrue(field.getFieldType().contains(type));
        }
    }

    @Test
    void testCreateFieldVerifiesLogicalNameConversion() {
        // Test that short names are converted to logical names
        NamedField textField = NamedFieldFactory.createFieldWithAttributes("Field1", "Text", null);
        assertEquals(Constants.FIELD_TYPE_TEXT, textField.getFieldType());

        NamedField paragraphField = NamedFieldFactory.createFieldWithAttributes("Field2", "Paragraph", null);
        assertEquals(Constants.FIELD_TYPE_PARAGRAPH, paragraphField.getFieldType());

        NamedField switchField = NamedFieldFactory.createSwitchField("Field3");
        assertEquals(Constants.FIELD_TYPE_SWITCH, switchField.getFieldType());
    }
}

