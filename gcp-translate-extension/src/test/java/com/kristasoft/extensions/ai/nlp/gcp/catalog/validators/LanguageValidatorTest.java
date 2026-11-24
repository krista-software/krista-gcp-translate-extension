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
import com.kristasoft.extensions.ai.nlp.gcp.catalog.validators.Validator.ValidationResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LanguageValidator
 */
@ExtendWith(MockitoExtension.class)
class LanguageValidatorTest {

    @Mock
    private GcpTranslateClient gcpTranslateClient;

    private LanguageValidator sourceLanguageValidator;
    private LanguageValidator targetLanguageValidator;
    private List<String> supportedLanguages;

    @BeforeEach
    void setUp() {
        sourceLanguageValidator = new LanguageValidator(gcpTranslateClient, ValidationResource.SOURCE_LANGUAGE);
        targetLanguageValidator = new LanguageValidator(gcpTranslateClient, ValidationResource.TARGET_LANGUAGE);
        
        // Setup common supported languages
        supportedLanguages = Arrays.asList("English", "Hindi", "Spanish", "French", "German");
    }

    @Test
    void testValidateSourceLanguageWithValidLanguage() throws Exception {
        // Arrange
        when(gcpTranslateClient.getSupportedLanguages()).thenReturn(supportedLanguages);
        Map<ValidationResource, String> resources = new HashMap<>();

        // Act
        boolean result = sourceLanguageValidator.validate("English", resources);

        // Assert
        assertTrue(result);
        verify(gcpTranslateClient).getSupportedLanguages();
    }

    @Test
    void testValidateSourceLanguageWithEmptyValue() {
        // Arrange
        Map<ValidationResource, String> resources = new HashMap<>();

        // Act
        boolean result = sourceLanguageValidator.validate("", resources);

        // Assert
        assertTrue(result); // Empty source language is valid (auto-detect)
        verify(gcpTranslateClient, never()).getSupportedLanguages();
    }

    @Test
    void testValidateSourceLanguageWithNullValue() {
        // Arrange
        Map<ValidationResource, String> resources = new HashMap<>();

        // Act
        boolean result = sourceLanguageValidator.validate(null, resources);

        // Assert
        assertTrue(result); // Null source language is valid (auto-detect)
        verify(gcpTranslateClient, never()).getSupportedLanguages();
    }

    @Test
    void testValidateTargetLanguageWithValidLanguage() throws Exception {
        // Arrange
        when(gcpTranslateClient.getSupportedLanguages()).thenReturn(supportedLanguages);
        Map<ValidationResource, String> resources = new HashMap<>();

        // Act
        boolean result = targetLanguageValidator.validate("Hindi", resources);

        // Assert
        assertTrue(result);
        verify(gcpTranslateClient).getSupportedLanguages();
    }

    @Test
    void testValidateTargetLanguageWithEmptyValue() {
        // Arrange
        Map<ValidationResource, String> resources = new HashMap<>();

        // Act
        boolean result = targetLanguageValidator.validate("", resources);

        // Assert
        assertFalse(result); // Empty target language is invalid
        verify(gcpTranslateClient, never()).getSupportedLanguages();
    }

    @Test
    void testValidateTargetLanguageWithNullValue() {
        // Arrange
        Map<ValidationResource, String> resources = new HashMap<>();

        // Act
        boolean result = targetLanguageValidator.validate(null, resources);

        // Assert
        assertFalse(result); // Null target language is invalid
        verify(gcpTranslateClient, never()).getSupportedLanguages();
    }

    @Test
    void testValidateWithUnsupportedLanguage() throws Exception {
        // Arrange
        when(gcpTranslateClient.getSupportedLanguages()).thenReturn(supportedLanguages);
        Map<ValidationResource, String> resources = new HashMap<>();

        // Act
        boolean result = targetLanguageValidator.validate("Klingon", resources);

        // Assert
        assertFalse(result);
        verify(gcpTranslateClient).getSupportedLanguages();
    }

    @Test
    void testValidateCaseSensitiveLanguageName() throws Exception {
        // Arrange
        when(gcpTranslateClient.getSupportedLanguages()).thenReturn(supportedLanguages);
        Map<ValidationResource, String> resources = new HashMap<>();

        // Act
        boolean resultLowerCase = targetLanguageValidator.validate("hindi", resources);
        boolean resultCorrectCase = targetLanguageValidator.validate("Hindi", resources);

        // Assert
        assertFalse(resultLowerCase); // "hindi" should fail (case-sensitive)
        assertTrue(resultCorrectCase); // "Hindi" should pass
    }

    @Test
    void testValidateTargetSameAsSource() throws Exception {
        // Arrange
        when(gcpTranslateClient.getSupportedLanguages()).thenReturn(supportedLanguages);
        Map<ValidationResource, String> resources = new HashMap<>();
        resources.put(ValidationResource.SOURCE_LANGUAGE, "English");

        // Act
        boolean result = targetLanguageValidator.validate("English", resources);

        // Assert
        assertFalse(result); // Target same as source should fail
    }

    @Test
    void testValidateTargetDifferentFromSource() throws Exception {
        // Arrange
        when(gcpTranslateClient.getSupportedLanguages()).thenReturn(supportedLanguages);
        Map<ValidationResource, String> resources = new HashMap<>();
        resources.put(ValidationResource.SOURCE_LANGUAGE, "English");

        // Act
        boolean result = targetLanguageValidator.validate("Hindi", resources);

        // Assert
        assertTrue(result); // Target different from source should pass
    }

    @Test
    void testValidateTargetWithEmptySource() throws Exception {
        // Arrange
        when(gcpTranslateClient.getSupportedLanguages()).thenReturn(supportedLanguages);
        Map<ValidationResource, String> resources = new HashMap<>();
        resources.put(ValidationResource.SOURCE_LANGUAGE, "");

        // Act
        boolean result = targetLanguageValidator.validate("Hindi", resources);

        // Assert
        assertTrue(result); // Should pass when source is empty (auto-detect)
    }

    @Test
    void testValidateTargetWithNullSource() throws Exception {
        // Arrange
        when(gcpTranslateClient.getSupportedLanguages()).thenReturn(supportedLanguages);
        Map<ValidationResource, String> resources = new HashMap<>();
        resources.put(ValidationResource.SOURCE_LANGUAGE, null);

        // Act
        boolean result = targetLanguageValidator.validate("Hindi", resources);

        // Assert
        assertTrue(result); // Should pass when source is null (auto-detect)
    }

    @Test
    void testValidateWithClientException() throws Exception {
        // Arrange
        when(gcpTranslateClient.getSupportedLanguages()).thenThrow(new RuntimeException("API Error"));
        Map<ValidationResource, String> resources = new HashMap<>();

        // Act
        boolean result = targetLanguageValidator.validate("Hindi", resources);

        // Assert
        assertFalse(result); // Should return false on exception
    }

    @Test
    void testGetConfirmStepMessageForEmptyValue() {
        // Arrange
        Map<ValidationResource, String> resources = new HashMap<>();

        // Act
        String message = targetLanguageValidator.getConfirmStepMessage("", resources);

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("Target Language"));
        assertTrue(message.contains("specify"));
    }

    @Test
    void testGetConfirmStepMessageForUnsupportedLanguage() throws Exception {
        // Arrange
        when(gcpTranslateClient.getSupportedLanguages()).thenReturn(supportedLanguages);
        Map<ValidationResource, String> resources = new HashMap<>();

        // Act
        String message = targetLanguageValidator.getConfirmStepMessage("Klingon", resources);

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("don't recognize"));
        assertTrue(message.contains("Get Supported Languages"));
    }

    @Test
    void testGetConfirmStepMessageForSameLanguage() throws Exception {
        // Arrange
        when(gcpTranslateClient.getSupportedLanguages()).thenReturn(supportedLanguages);
        Map<ValidationResource, String> resources = new HashMap<>();
        resources.put(ValidationResource.SOURCE_LANGUAGE, "English");

        // Act
        String message = targetLanguageValidator.getConfirmStepMessage("English", resources);

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("are the same"));
    }

    @Test
    void testGetFetchFieldNameForSourceLanguage() {
        // Act
        String fieldName = sourceLanguageValidator.getFetchFieldName();

        // Assert
        assertEquals("Source Language", fieldName);
    }

    @Test
    void testGetFetchFieldNameForTargetLanguage() {
        // Act
        String fieldName = targetLanguageValidator.getFetchFieldName();

        // Assert
        assertEquals("Target Language", fieldName);
    }

    @Test
    void testGetFetchStepMessageForSourceLanguage() {
        // Act
        String message = sourceLanguageValidator.getFetchStepMessage();

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("source language"));
        assertTrue(message.contains("automatically detect"));
    }

    @Test
    void testGetFetchStepMessageForTargetLanguage() {
        // Act
        String message = targetLanguageValidator.getFetchStepMessage();

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("target language"));
    }

    @Test
    void testGetErrMessageForEmptyValue() {
        // Act
        String message = targetLanguageValidator.getErrMessage("");

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("Target Language"));
    }

    @Test
    void testGetErrMessageForInvalidValue() {
        // Act
        String message = targetLanguageValidator.getErrMessage("InvalidLang");

        // Assert
        assertNotNull(message);
        // For a target-language validator, getErrMessage returns the same-language guidance
        // (the invalid-code path is handled by the source-language validator / validate()).
        assertTrue(message.contains("target language"));
    }

    @Test
    void testGetFieldType() {
        // Act
        String fieldType = targetLanguageValidator.getFieldType();

        // Assert
        assertEquals("Text", fieldType);
    }
}

