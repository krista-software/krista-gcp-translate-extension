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
import com.kristasoft.extensions.ai.nlp.gcp.catalog.validators.Validator.ValidationResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TextInputValidator
 */
class TextInputValidatorTest {

    private TextInputValidator validator;
    private Map<ValidationResource, String> resources;

    @BeforeEach
    void setUp() {
        validator = new TextInputValidator();
        resources = new HashMap<>();
    }

    @Test
    void testValidateWithValidText() {
        // Arrange
        String validText = "This is a valid text to translate.";

        // Act
        boolean result = validator.validate(validText, resources);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidateWithNullText() {
        // Act
        boolean result = validator.validate(null, resources);

        // Assert
        assertFalse(result);
    }

    @Test
    void testValidateWithEmptyText() {
        // Act
        boolean result = validator.validate("", resources);

        // Assert
        assertFalse(result);
    }

    @Test
    void testValidateWithWhitespaceOnlyText() {
        // Act
        boolean result = validator.validate("   ", resources);

        // Assert
        assertFalse(result);
    }

    @Test
    void testValidateWithTextExceedingMaxLength() {
        // Arrange
        String longText = "a".repeat(Constants.MAX_TEXT_LENGTH + 1);

        // Act
        boolean result = validator.validate(longText, resources);

        // Assert
        assertFalse(result);
    }

    @Test
    void testValidateWithTextAtMaxLength() {
        // Arrange
        String maxLengthText = "a".repeat(Constants.MAX_TEXT_LENGTH);

        // Act
        boolean result = validator.validate(maxLengthText, resources);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidateWithTextJustBelowMaxLength() {
        // Arrange
        String text = "a".repeat(Constants.MAX_TEXT_LENGTH - 1);

        // Act
        boolean result = validator.validate(text, resources);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidateWithSingleCharacter() {
        // Act
        boolean result = validator.validate("a", resources);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidateWithSpecialCharacters() {
        // Arrange
        String specialText = "Hello! @#$%^&*() 你好 مرحبا";

        // Act
        boolean result = validator.validate(specialText, resources);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidateWithNewlinesAndTabs() {
        // Arrange
        String textWithWhitespace = "Line 1\nLine 2\tTabbed";

        // Act
        boolean result = validator.validate(textWithWhitespace, resources);

        // Assert
        assertTrue(result);
    }

    @Test
    void testGetConfirmStepMessageForNullText() {
        // Act
        String message = validator.getConfirmStepMessage(null, resources);

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("some text"));
        assertTrue(message.contains("translate or detect"));
    }

    @Test
    void testGetConfirmStepMessageForEmptyText() {
        // Act
        String message = validator.getConfirmStepMessage("", resources);

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("some text"));
        assertTrue(message.contains("translate or detect"));
    }

    @Test
    void testGetConfirmStepMessageForTextExceedingMaxLength() {
        // Arrange
        String longText = "a".repeat(Constants.MAX_TEXT_LENGTH + 100);

        // Act
        String message = validator.getConfirmStepMessage(longText, resources);

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("maximum allowed"));
        assertTrue(message.contains(String.format("%,d", Constants.MAX_TEXT_LENGTH)));
        assertTrue(message.contains(String.format("%,d", longText.length())));
    }

    @Test
    void testGetFetchFieldName() {
        // Act
        String fieldName = validator.getFetchFieldName();

        // Assert
        assertEquals("Text", fieldName);
    }

    @Test
    void testGetFetchStepMessage() {
        // Act
        String message = validator.getFetchStepMessage();

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("enter the text"));
        assertTrue(message.contains(String.format("%,d", Constants.MAX_TEXT_LENGTH)));
    }

    @Test
    void testGetErrMessageForNullText() {
        // Act
        String message = validator.getErrMessage(null);

        // Assert
        assertNotNull(message);
        assertEquals(Constants.TEXT_REQUIRED, message);
    }

    @Test
    void testGetErrMessageForEmptyText() {
        // Act
        String message = validator.getErrMessage("");

        // Assert
        assertNotNull(message);
        assertEquals(Constants.TEXT_REQUIRED, message);
    }

    @Test
    void testGetErrMessageForTextExceedingMaxLength() {
        // Arrange
        String longText = "a".repeat(Constants.MAX_TEXT_LENGTH + 1);

        // Act
        String message = validator.getErrMessage(longText);

        // Assert
        assertNotNull(message);
        assertTrue(message.contains(String.format("%,d", Constants.MAX_TEXT_LENGTH)));
    }

    @Test
    void testGetFieldType() {
        // Act
        String fieldType = validator.getFieldType();

        // Assert
        assertEquals("Paragraph", fieldType);
    }

    @Test
    void testValidateWithUnicodeText() {
        // Arrange
        String unicodeText = "Hello 世界 🌍 مرحبا";

        // Act
        boolean result = validator.validate(unicodeText, resources);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidateWithMultilineText() {
        // Arrange
        String multilineText = "Line 1\nLine 2\nLine 3\nLine 4";

        // Act
        boolean result = validator.validate(multilineText, resources);

        // Assert
        assertTrue(result);
    }
}

