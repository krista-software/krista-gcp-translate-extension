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
import com.kristasoft.extensions.ai.nlp.gcp.catalog.client.GcpTranslateClient;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.errorhandlers.ExtensionResponseGenerator;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.handlers.AreaOperationHandler;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.state.InternalStateManager;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.telemetry.TelemetryHelper;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.Constants;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.SubCatalogConstants;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.TelemetryTagsHelper;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.ValidationMapHelper;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.validators.ValidationOrchestrator;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.validators.Validator;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core service for translation operations.
 * Orchestrates validation, business logic execution, and response generation.
 */
@Service
public class TranslationResponseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationResponseService.class);

    private final GcpTranslateClient gcpTranslateClient;
    private final ValidationOrchestrator validationOrchestrator;
    private final InternalStateManager internalStateManager;
    private final ExtensionResponseGenerator responseGenerator;
    private final TelemetryHelper telemetryHelper;

    @Inject
    public TranslationResponseService(
            GcpTranslateClient gcpTranslateClient,
            ValidationOrchestrator validationOrchestrator,
            InternalStateManager internalStateManager,
            ExtensionResponseGenerator responseGenerator,
            TelemetryHelper telemetryHelper) {
        this.gcpTranslateClient = gcpTranslateClient;
        this.validationOrchestrator = validationOrchestrator;
        this.internalStateManager = internalStateManager;
        this.responseGenerator = responseGenerator;
        this.telemetryHelper = telemetryHelper;
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

        LOGGER.info("TranslationResponseService: translateToLanguage with allowRetry: {}", allowRetry);

        // Increment telemetry counter
        telemetryHelper.incrementCount(Constants.METRIC_TRANSLATE_TO_LANGUAGE);

        // Create operation handler
        AreaOperationHandler<String> handler = new AreaOperationHandler<>(
                validationOrchestrator,
                internalStateManager,
                responseGenerator,
                telemetryHelper
        );

        // Create validation map
        Map<Validator.ValidationResource, String> validationMap =
                ValidationMapHelper.createTranslateValidationMap(text, sourceLanguage, targetLanguage);

        // Execute with handler
        return handler.execute(
                Constants.OPERATION_TRANSLATE_TO_LANGUAGE,
                validationMap,
                SubCatalogConstants.CONFIRM_REENTER_TRANSLATE,
                stateId -> createTranslateStateMap(stateId, text, sourceLanguage, targetLanguage),
                () -> gcpTranslateClient.translateText(sourceLanguage, targetLanguage, text),
                result -> Map.of(Constants.RESPONSE_FIELD_TRANSLATED_TEXT, result),
                TelemetryTagsHelper.createTranslationTags(sourceLanguage, targetLanguage),
                allowRetry
        );
    }

    /**
     * Detects the language of the provided text
     *
     * @param text The text to analyze
     * @param allowRetry Whether to allow retry on validation failure
     * @return ExtensionResponse with detected language or error
     */
    public ExtensionResponse detectLanguage(String text, Boolean allowRetry) {

        LOGGER.info("TranslationResponseService: detectLanguage with allowRetry: {}", allowRetry);

        // Increment telemetry counter
        telemetryHelper.incrementCount(Constants.METRIC_DETECT_LANGUAGE);

        // Create operation handler
        AreaOperationHandler<String> handler = new AreaOperationHandler<>(
                validationOrchestrator,
                internalStateManager,
                responseGenerator,
                telemetryHelper
        );

        // Create validation map
        Map<Validator.ValidationResource, String> validationMap =
                ValidationMapHelper.createDetectLanguageValidationMap(text);

        // Execute with handler
        return handler.execute(
                Constants.OPERATION_DETECT_LANGUAGE,
                validationMap,
                SubCatalogConstants.CONFIRM_REENTER_DETECT,
                stateId -> createDetectStateMap(stateId, text),
                () -> gcpTranslateClient.detect(text),
                result -> Map.of(Constants.RESPONSE_FIELD_DETECTED_LANGUAGE, result),
                TelemetryTagsHelper.createDetectionTags(text != null ? text.length() : 0),
                allowRetry
        );
    }

    /**
     * Gets the list of supported languages
     *
     * @return List of supported language codes
     */
    public List<String> getSupportedLanguages() {

        LOGGER.info("TranslationResponseService: getSupportedLanguages");
        telemetryHelper.incrementCount(Constants.METRIC_GET_SUPPORTED_LANGUAGES);

        try {
            // No validation needed for this operation
            List<String> languages = gcpTranslateClient.getSupportedLanguages();

            telemetryHelper.incrementCount(Constants.METRIC_GET_SUPPORTED_LANGUAGES_SUCCESS);

            LOGGER.info("Retrieved {} supported languages", languages.size());
            return languages;

        } catch (Exception e) {
            LOGGER.error("Error getting supported languages", e);
            telemetryHelper.incrementCount(Constants.METRIC_GET_SUPPORTED_LANGUAGES_ERROR);
            throw e;
        }
    }

    /**
     * Creates state map for translate operation
     */
    private Map<String, Object> createTranslateStateMap(
            String stateId, String text, String sourceLanguage, String targetLanguage) {
        
        Map<String, Object> state = new HashMap<>();
        state.put(SubCatalogConstants.STATE_ID, stateId);
        state.put(SubCatalogConstants.TEXT, text);
        state.put(SubCatalogConstants.SOURCE_LANGUAGE, sourceLanguage);
        state.put(SubCatalogConstants.TARGET_LANGUAGE, targetLanguage);
        
        return state;
    }

    /**
     * Creates state map for detect operation
     */
    private Map<String, Object> createDetectStateMap(String stateId, String text) {
        
        Map<String, Object> state = new HashMap<>();
        state.put(SubCatalogConstants.STATE_ID, stateId);
        state.put(SubCatalogConstants.TEXT, text);
        
        return state;
    }
}

