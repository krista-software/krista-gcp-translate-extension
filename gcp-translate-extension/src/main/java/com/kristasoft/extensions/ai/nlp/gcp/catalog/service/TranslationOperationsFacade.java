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
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * Facade for translation operations.
 * Provides a thin routing layer between catalog requests and the response service.
 */
@Service
public class TranslationOperationsFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationOperationsFacade.class);
    private final TranslationResponseService translationResponseService;

    @Inject
    public TranslationOperationsFacade(TranslationResponseService translationResponseService) {
        this.translationResponseService = translationResponseService;
    }

    /**
     * Translates text to a target language
     *
     * @param text           The text to translate
     * @param sourceLanguage The source language (can be null for auto-detect)
     * @param targetLanguage The target language
     * @param allowRetry     Whether to allow retry on validation failure
     * @return ExtensionResponse with translated text or error
     */
    public ExtensionResponse translateToLanguage(String text, String sourceLanguage, String targetLanguage, Boolean allowRetry) {
        LOGGER.debug("Facade: translateToLanguage - text length: {}, source: {}, target: {}, allowRetry: {}",
                text != null ? text.length() : 0, sourceLanguage, targetLanguage, allowRetry);
        return translationResponseService.translateToLanguage(text, sourceLanguage, targetLanguage, allowRetry);
    }

    /**
     * Detects the language of the given text
     *
     * @param text The text to analyze
     * @param allowRetry Whether to allow retry on validation failure
     * @return ExtensionResponse with detected language or error
     */
    public ExtensionResponse detectLanguage(String text, Boolean allowRetry) {
        LOGGER.debug("Facade: detectLanguage - text length: {}, allowRetry: {}", text != null ? text.length() : 0, allowRetry);
        return translationResponseService.detectLanguage(text, allowRetry);
    }

    /**
     * Gets the list of supported languages
     *
     * @return List of supported language codes
     */
    public List<String> getSupportedLanguages() {
        LOGGER.debug("Facade: getSupportedLanguages");
        return translationResponseService.getSupportedLanguages();
    }
}

