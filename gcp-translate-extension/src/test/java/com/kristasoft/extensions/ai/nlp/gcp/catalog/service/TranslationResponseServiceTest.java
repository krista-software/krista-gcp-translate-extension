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

package com.kristasoft.extensions.ai.nlp.gcp.catalog.service;

import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.ExtensionResponseBuilder;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.client.GcpTranslateClient;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.errorhandlers.ExtensionResponseGenerator;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.state.InternalStateManager;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.telemetry.TelemetryHelper;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.validators.ValidationOrchestrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.atLeastOnce;

/**
 * Unit tests for TranslationResponseService
 */
class TranslationResponseServiceTest {

    @Mock
    private GcpTranslateClient gcpTranslateClient;

    @Mock
    private ValidationOrchestrator validationOrchestrator;

    @Mock
    private InternalStateManager internalStateManager;

    @Mock
    private ExtensionResponseGenerator responseGenerator;

    @Mock
    private TelemetryHelper telemetryHelper;

    private TranslationResponseService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock telemetry helper to return null span (telemetry disabled)
        when(telemetryHelper.startSpan(anyString())).thenReturn(null);
        when(telemetryHelper.startSpan(anyString(), anyMap())).thenReturn(null);

        service = new TranslationResponseService(
            gcpTranslateClient,
            validationOrchestrator,
            internalStateManager,
            responseGenerator,
            telemetryHelper
        );
    }

    @Test
    void testTranslateToLanguageSuccess() {
        // Arrange
        String text = "Hello, world!";
        String sourceLanguage = "en";
        String targetLanguage = "es";
        String translatedText = "¡Hola, mundo!";

        // Mock validation success (empty list = no errors)
        when(validationOrchestrator.validate(anyMap())).thenReturn(Arrays.asList());

        // Mock translation
        when(gcpTranslateClient.translateText(sourceLanguage, targetLanguage, text))
            .thenReturn(translatedText);

        // Mock success response
        ExtensionResponse successResponse = mock(ExtensionResponse.class);
        when(successResponse.getResult()).thenReturn(ExtensionResponse.Result.SUCCESS);
        when(responseGenerator.generateSuccessResponse(anyMap()))
            .thenReturn(successResponse);

        // Act
        ExtensionResponse response = service.translateToLanguage(text, sourceLanguage, targetLanguage, true);

        // Assert
        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.SUCCESS, response.getResult());

        verify(validationOrchestrator, times(1)).validate(anyMap());
        verify(gcpTranslateClient, times(1)).translateText(sourceLanguage, targetLanguage, text);
        verify(responseGenerator, times(1)).generateSuccessResponse(anyMap());
        verify(telemetryHelper, atLeastOnce()).incrementCount(anyString());
    }

    @Test
    void testTranslateToLanguageValidationFailure() {
        // Arrange
        String text = "";  // Invalid - empty text
        String sourceLanguage = "en";
        String targetLanguage = "es";

        // Mock validation failure
        ValidationOrchestrator.ValidationResult validationError = 
            new ValidationOrchestrator.ValidationResult(
                "Confirm message",
                "Text",
                "Fetch message",
                "Text is required",
                "Paragraph"
            );
        when(validationOrchestrator.validate(anyMap()))
            .thenReturn(Arrays.asList(validationError));

        // Mock state manager
        when(internalStateManager.generateStateId()).thenReturn("state-123");

        // Mock error response
        ExtensionResponse errorResponse = mock(ExtensionResponse.class);
        when(errorResponse.getResult()).thenReturn(ExtensionResponse.Result.FAILURE);
        when(responseGenerator.generateConfirmationResponse(any(), anyList(), anyString(), anyMap()))
            .thenReturn(errorResponse);

        // Act
        ExtensionResponse response = service.translateToLanguage(text, sourceLanguage, targetLanguage, true);

        // Assert
        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());

        verify(validationOrchestrator, times(1)).validate(anyMap());
        verify(internalStateManager, times(1)).generateStateId();
        verify(internalStateManager, times(1)).saveState(eq("state-123"), anyMap());
        verify(responseGenerator, times(1)).generateConfirmationResponse(any(), anyList(), anyString(), anyMap());
        verify(gcpTranslateClient, never()).translateText(anyString(), anyString(), anyString());
    }

    @Test
    void testTranslateToLanguageSystemError() {
        // Arrange
        String text = "Hello";
        String sourceLanguage = "en";
        String targetLanguage = "es";

        // Mock validation success
        when(validationOrchestrator.validate(anyMap())).thenReturn(Arrays.asList());

        // Mock GCP client throwing exception
        RuntimeException gcpException = new RuntimeException("GCP API error");
        when(gcpTranslateClient.translateText(anyString(), anyString(), anyString()))
            .thenThrow(gcpException);

        // Mock error response
        ExtensionResponse errorResponse = mock(ExtensionResponse.class);
        when(errorResponse.getResult()).thenReturn(ExtensionResponse.Result.FAILURE);
        when(responseGenerator.generateSystemErrorResponse(anyString(), any(Exception.class), anyMap()))
            .thenReturn(errorResponse);

        // Act
        ExtensionResponse response = service.translateToLanguage(text, sourceLanguage, targetLanguage, true);

        // Assert
        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());

        verify(gcpTranslateClient, times(1)).translateText(sourceLanguage, targetLanguage, text);
        verify(responseGenerator, times(1)).generateSystemErrorResponse(anyString(), eq(gcpException), anyMap());
    }

    @Test
    void testDetectLanguageSuccess() {
        // Arrange
        String text = "Bonjour le monde!";
        String detectedLanguage = "fr";

        // Mock validation success
        when(validationOrchestrator.validate(anyMap())).thenReturn(Arrays.asList());

        // Mock detection
        when(gcpTranslateClient.detect(text)).thenReturn(detectedLanguage);

        // Mock success response
        ExtensionResponse successResponse = mock(ExtensionResponse.class);
        when(successResponse.getResult()).thenReturn(ExtensionResponse.Result.SUCCESS);
        when(responseGenerator.generateSuccessResponse(anyMap()))
            .thenReturn(successResponse);

        // Act
        ExtensionResponse response = service.detectLanguage(text, true);

        // Assert
        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.SUCCESS, response.getResult());

        verify(gcpTranslateClient, times(1)).detect(text);
        verify(responseGenerator, times(1)).generateSuccessResponse(anyMap());
    }

    @Test
    void testDetectLanguageValidationFailure() {
        // Arrange
        String text = null;  // Invalid - null text

        // Mock validation failure
        ValidationOrchestrator.ValidationResult validationError = 
            new ValidationOrchestrator.ValidationResult(
                "Confirm message",
                "Text",
                "Fetch message",
                "Text is required",
                "Paragraph"
            );
        when(validationOrchestrator.validate(anyMap()))
            .thenReturn(Arrays.asList(validationError));

        when(internalStateManager.generateStateId()).thenReturn("state-456");

        ExtensionResponse errorResponse = mock(ExtensionResponse.class);
        when(errorResponse.getResult()).thenReturn(ExtensionResponse.Result.FAILURE);
        when(responseGenerator.generateConfirmationResponse(any(), anyList(), anyString(), anyMap()))
            .thenReturn(errorResponse);

        // Act
        ExtensionResponse response = service.detectLanguage(text, true);

        // Assert
        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());

        verify(gcpTranslateClient, never()).detect(anyString());
    }

    @Test
    void testGetSupportedLanguagesSuccess() {
        // Arrange
        List<String> expectedLanguages = Arrays.asList("en", "es", "fr", "de", "ja", "zh");
        when(gcpTranslateClient.getSupportedLanguages()).thenReturn(expectedLanguages);

        // Act
        List<String> languages = service.getSupportedLanguages();

        // Assert
        assertNotNull(languages);
        assertEquals(6, languages.size());
        assertTrue(languages.contains("en"));
        assertTrue(languages.contains("ja"));
        
        verify(gcpTranslateClient, times(1)).getSupportedLanguages();
        verify(telemetryHelper, times(1)).incrementCount("gcp.translate.getSupportedLanguages");
        verify(telemetryHelper, times(1)).incrementCount("gcp.translate.getSupportedLanguages.success");
    }

    @Test
    void testGetSupportedLanguagesError() {
        // Arrange
        RuntimeException exception = new RuntimeException("API error");
        when(gcpTranslateClient.getSupportedLanguages()).thenThrow(exception);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            service.getSupportedLanguages();
        });
        
        verify(telemetryHelper, times(1)).incrementCount("gcp.translate.getSupportedLanguages");
        verify(telemetryHelper, times(1)).incrementCount("gcp.translate.getSupportedLanguages.error");
    }

    @Test
    void testTelemetryIsRecorded() {
        // Arrange
        String text = "Test";
        String source = "en";
        String target = "es";

        when(validationOrchestrator.validate(anyMap())).thenReturn(Arrays.asList());
        when(gcpTranslateClient.translateText(anyString(), anyString(), anyString())).thenReturn("Prueba");
        
        ExtensionResponse successResponse = mock(ExtensionResponse.class);
        when(responseGenerator.generateSuccessResponse(anyMap())).thenReturn(successResponse);

        // Act
        service.translateToLanguage(text, source, target, true);

        // Assert - verify telemetry was called
        verify(telemetryHelper, atLeastOnce()).incrementCount(anyString());
        verify(telemetryHelper, atLeastOnce()).incrementCount(anyString(), anyMap());
    }
}

