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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TranslationOperationsFacade
 */
class TranslationOperationsFacadeTest {

    @Mock
    private TranslationResponseService translationResponseService;

    private TranslationOperationsFacade facade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        facade = new TranslationOperationsFacade(translationResponseService);
    }

    @Test
    void testTranslateToLanguage() {
        // Arrange
        String text = "Hello, world!";
        String sourceLanguage = "en";
        String targetLanguage = "es";
        Boolean allowRetry = true;

        ExtensionResponse expectedResponse = mock(ExtensionResponse.class);
        when(translationResponseService.translateToLanguage(text, sourceLanguage, targetLanguage, allowRetry))
            .thenReturn(expectedResponse);

        // Act
        ExtensionResponse response = facade.translateToLanguage(text, sourceLanguage, targetLanguage, allowRetry);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(translationResponseService, times(1))
            .translateToLanguage(text, sourceLanguage, targetLanguage, allowRetry);
    }

    @Test
    void testTranslateToLanguageWithNullSource() {
        // Arrange
        String text = "Hello, world!";
        String sourceLanguage = null;
        String targetLanguage = "es";
        Boolean allowRetry = false;

        ExtensionResponse expectedResponse = mock(ExtensionResponse.class);
        when(translationResponseService.translateToLanguage(text, sourceLanguage, targetLanguage, allowRetry))
            .thenReturn(expectedResponse);

        // Act
        ExtensionResponse response = facade.translateToLanguage(text, sourceLanguage, targetLanguage, allowRetry);

        // Assert
        assertNotNull(response);
        verify(translationResponseService, times(1))
            .translateToLanguage(text, null, targetLanguage, allowRetry);
    }

    @Test
    void testDetectLanguage() {
        // Arrange
        String text = "Bonjour le monde!";
        Boolean allowRetry = true;

        ExtensionResponse expectedResponse = mock(ExtensionResponse.class);
        when(translationResponseService.detectLanguage(text, allowRetry))
            .thenReturn(expectedResponse);

        // Act
        ExtensionResponse response = facade.detectLanguage(text, allowRetry);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(translationResponseService, times(1)).detectLanguage(text, allowRetry);
    }

    @Test
    void testGetSupportedLanguages() {
        // Arrange
        List<String> expectedLanguages = Arrays.asList("en", "es", "fr", "de", "ja");
        when(translationResponseService.getSupportedLanguages())
            .thenReturn(expectedLanguages);

        // Act
        List<String> languages = facade.getSupportedLanguages();

        // Assert
        assertNotNull(languages);
        assertEquals(5, languages.size());
        assertTrue(languages.contains("en"));
        assertTrue(languages.contains("es"));
        verify(translationResponseService, times(1)).getSupportedLanguages();
    }

    @Test
    void testFacadeDelegatesCorrectly() {
        // Verify that facade is just a thin routing layer
        String text = "Test";
        String source = "en";
        String target = "fr";
        Boolean allowRetry = true;

        facade.translateToLanguage(text, source, target, allowRetry);
        facade.detectLanguage(text, allowRetry);
        facade.getSupportedLanguages();

        // Verify all calls were delegated
        verify(translationResponseService, times(1)).translateToLanguage(text, source, target, allowRetry);
        verify(translationResponseService, times(1)).detectLanguage(text, allowRetry);
        verify(translationResponseService, times(1)).getSupportedLanguages();
        verifyNoMoreInteractions(translationResponseService);
    }
}

