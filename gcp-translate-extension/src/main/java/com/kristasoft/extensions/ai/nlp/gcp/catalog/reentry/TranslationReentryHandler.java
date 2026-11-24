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

package com.kristasoft.extensions.ai.nlp.gcp.catalog.reentry;

import app.krista.extension.executor.ExtensionResponse;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.errorhandlers.ExtensionResponseGenerator;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.service.TranslationResponseService;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.state.InternalStateManager;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.Constants;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.SubCatalogConstants;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.validators.ValidationOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles re-entry flow for translation operations.
 * Processes user confirmation and re-entry of translation data.
 */
public class TranslationReentryHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationReentryHandler.class);

    private final InternalStateManager stateManager;
    private final TranslationResponseService translationResponseService;
    private final ExtensionResponseGenerator responseGenerator;

    public TranslationReentryHandler(InternalStateManager stateManager,
                                    TranslationResponseService translationResponseService,
                                    ExtensionResponseGenerator responseGenerator) {
        this.stateManager = stateManager;
        this.translationResponseService = translationResponseService;
        this.responseGenerator = responseGenerator;
    }

    /**
     * Handles confirmation for translate to language re-entry.
     * If user confirms (REENTER = true), returns the saved state for re-entry.
     * If user declines (REENTER = false), returns null/empty response.
     *
     * @param inputMap Map containing REENTER flag and State Id
     * @return ExtensionResponse with state data or empty response
     */
    public ExtensionResponse handleConfirmation(Map<String, Object> inputMap) {
        LOGGER.info("Handling translate confirmation with input: {}", inputMap);

        Boolean reenter = (Boolean) inputMap.get(SubCatalogConstants.REENTER);
        String stateId = (String) inputMap.get(SubCatalogConstants.STATE_ID);

        // Retrieve saved state
        Map<String, Object> savedState = stateManager.getState(stateId);

        if (savedState == null) {
            LOGGER.warn("No saved state found for stateId: {}", stateId);
            return createEmptyResponse();
        }

        // Get validation results from saved state
        @SuppressWarnings("unchecked")
        List<ValidationOrchestrator.ValidationResult> validationResults =
            (List<ValidationOrchestrator.ValidationResult>) savedState.get(SubCatalogConstants.VALIDATION_RESULTS);

        if (Boolean.FALSE.equals(reenter)) {
            LOGGER.info("User chose not to re-enter translation data. Returning fetch deny response.");
            return responseGenerator.generateFetchDenyResponse(
                    ExtensionResponse.Error.ExceptionType.INPUT_ERROR,
                    validationResults,
                    null,
                    Map.of()
            );
        }

        LOGGER.info("User chose to re-enter translation data. Preparing fetch response.");

        // Prepare state map with current values
        Map<String, Object> stateMap = new HashMap<>();
        stateMap.put(SubCatalogConstants.STATE_ID, stateId);
        stateMap.put(SubCatalogConstants.TEXT, inputMap.get(SubCatalogConstants.TEXT));
        stateMap.put(SubCatalogConstants.SOURCE_LANGUAGE, inputMap.get(SubCatalogConstants.SOURCE_LANGUAGE));
        stateMap.put(SubCatalogConstants.TARGET_LANGUAGE, inputMap.get(SubCatalogConstants.TARGET_LANGUAGE));

        // Return fetch response asking user to provide corrected field values
        return responseGenerator.generateFetchResponse(
                ExtensionResponse.Error.ExceptionType.INPUT_ERROR,
                validationResults,
                SubCatalogConstants.HANDLE_REENTER_TRANSLATE,
                stateMap
        );
    }

    /**
     * Handles re-entry for translate to language operation.
     * Extracts the re-entered values and calls the translation service.
     *
     * @param inputMap Map containing State Id and re-entered values
     * @return ExtensionResponse with translation result
     */
    public ExtensionResponse handleReentry(Map<String, Object> inputMap) {
        LOGGER.info("Handling translate re-entry with input: {}", inputMap);

        String stateId = (String) inputMap.get(SubCatalogConstants.STATE_ID);
        
        // Retrieve saved state to get original values
        Map<String, Object> savedState = stateManager.getState(stateId);
        if (savedState == null) {
            LOGGER.warn("No saved state found for stateId: {}", stateId);
            return createErrorResponse(Constants.SESSION_EXPIRED);
        }

        // Get re-entered values (or use saved values if not re-entered)
        String text = getStringValue(inputMap, SubCatalogConstants.TEXT, savedState);
        String sourceLanguage = getStringValue(inputMap, SubCatalogConstants.SOURCE_LANGUAGE, savedState);
        String targetLanguage = getStringValue(inputMap, SubCatalogConstants.TARGET_LANGUAGE, savedState);

        LOGGER.info("Re-entering translation with text length: {}, source: {}, target: {}",
                text != null ? text.length() : 0, sourceLanguage, targetLanguage);

        // Clean up state after use
        stateManager.removeState(stateId);

        // Call the translation service with re-entered values
        return translationResponseService.translateToLanguage(text, sourceLanguage, targetLanguage, true);
    }

    /**
     * Handles confirmation for detect language re-entry.
     *
     * @param inputMap Map containing REENTER flag and State Id
     * @return ExtensionResponse with state data or empty response
     */
    public ExtensionResponse handleDetectConfirmation(Map<String, Object> inputMap) {
        LOGGER.info("Handling detect language confirmation with input: {}", inputMap);

        Boolean reenter = (Boolean) inputMap.get(SubCatalogConstants.REENTER);
        String stateId = (String) inputMap.get(SubCatalogConstants.STATE_ID);

        // Retrieve saved state
        Map<String, Object> savedState = stateManager.getState(stateId);

        if (savedState == null) {
            LOGGER.warn("No saved state found for stateId: {}", stateId);
            return createEmptyResponse();
        }

        // Get validation results from saved state
        @SuppressWarnings("unchecked")
        List<ValidationOrchestrator.ValidationResult> validationResults =
            (List<ValidationOrchestrator.ValidationResult>) savedState.get(SubCatalogConstants.VALIDATION_RESULTS);

        if (Boolean.FALSE.equals(reenter)) {
            LOGGER.info("User chose not to re-enter text for detection. Returning fetch deny response.");
            return responseGenerator.generateFetchDenyResponse(
                    ExtensionResponse.Error.ExceptionType.INPUT_ERROR,
                    validationResults,
                    null,
                    Map.of()
            );
        }

        LOGGER.info("User chose to re-enter text for detection. Preparing fetch response.");

        // Prepare state map with current values
        Map<String, Object> stateMap = new HashMap<>();
        stateMap.put(SubCatalogConstants.STATE_ID, stateId);
        stateMap.put(SubCatalogConstants.TEXT, inputMap.get(SubCatalogConstants.TEXT));

        // Return fetch response asking user to provide corrected field values
        return responseGenerator.generateFetchResponse(
                ExtensionResponse.Error.ExceptionType.INPUT_ERROR,
                validationResults,
                SubCatalogConstants.HANDLE_REENTER_DETECT,
                stateMap
        );
    }

    /**
     * Handles re-entry for detect language operation.
     *
     * @param inputMap Map containing State Id and re-entered text
     * @return ExtensionResponse with detection result
     */
    public ExtensionResponse handleDetectReentry(Map<String, Object> inputMap) {
        LOGGER.info("Handling detect language re-entry with input: {}", inputMap);

        String stateId = (String) inputMap.get(SubCatalogConstants.STATE_ID);
        
        // Retrieve saved state
        Map<String, Object> savedState = stateManager.getState(stateId);
        if (savedState == null) {
            LOGGER.warn("No saved state found for stateId: {}", stateId);
            return createErrorResponse(Constants.SESSION_EXPIRED);
        }

        // Get re-entered text (or use saved value if not re-entered)
        String text = getStringValue(inputMap, SubCatalogConstants.TEXT, savedState);

        LOGGER.info("Re-entering detection with text length: {}", text != null ? text.length() : 0);

        // Clean up state after use
        stateManager.removeState(stateId);

        // Call the detection service with re-entered text
        // Always enable retry for re-entry flow (user is already in retry mode)
        return translationResponseService.detectLanguage(text, true);
    }

    /**
     * Gets a string value from inputMap, falling back to savedState if not present
     */
    private String getStringValue(Map<String, Object> inputMap, String key, Map<String, Object> savedState) {
        Object value = inputMap.get(key);
        if (value != null) {
            return value.toString();
        }
        
        value = savedState.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Creates an empty success response
     */
    private ExtensionResponse createEmptyResponse() {
        return new ExtensionResponse(
            ExtensionResponse.Result.SUCCESS,
            null,
            null,
            null,
            null
        );
    }

    /**
     * Creates an error response
     */
    private ExtensionResponse createErrorResponse(String message) {
        ExtensionResponse.Error error = new ExtensionResponse.Error(
            message,
            System.currentTimeMillis(),
            ExtensionResponse.Error.ExceptionType.LOGIC_ERROR,
            null
        );

        return new ExtensionResponse(
            ExtensionResponse.Result.FAILURE,
            null,
            error,
            null,
            null
        );
    }
}

