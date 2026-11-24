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

package com.kristasoft.extensions.ai.nlp.gcp.catalog.errorhandlers;

import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.ExtensionResponseBuilder;
import app.krista.extension.executor.RemediationActions;
import app.krista.model.field.NamedField;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.extresp.NamedFieldFactory;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.extresp.RemediationActionFactory;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.Constants;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.SubCatalogConstants;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.validators.ValidationOrchestrator;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Generates ExtensionResponse objects for various scenarios including success,
 * validation errors, and system errors.
 */
@Service
public class ExtensionResponseGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionResponseGenerator.class);

    /**
     * Generates a success response with data
     *
     * @param data The response data
     * @return ExtensionResponse with SUCCESS result
     */
    public ExtensionResponse generateSuccessResponse(Map<String, Object> data) {
        LOGGER.debug("Generating success response with data: {}", data.keySet());

        return new ExtensionResponseBuilder()
                .success(data)
                .build();
    }

    /**
     * Generates a confirmation response for validation errors.
     * Creates an interactive prompt with a REENTER switch field that triggers the sub-catalog.
     *
     * @param exceptionType         The type of exception
     * @param validationResults     List of validation failures
     * @param subCatalogRequestName Name of the sub-catalog for re-entry confirmation
     * @param state                 State to preserve for re-entry
     * @return ExtensionResponse with remediation action asking user to re-enter
     */
    public ExtensionResponse generateConfirmationResponse(
            ExtensionResponse.Error.ExceptionType exceptionType,
            List<ValidationOrchestrator.ValidationResult> validationResults,
            String subCatalogRequestName,
            Map<String, Object> state) {

        LOGGER.warn("Generating confirmation response for {} validation failures", validationResults.size());

        // Build error and step messages from validation results
        StringBuilder stepMessage = new StringBuilder();
        StringBuilder errMessage = new StringBuilder();
        for (ValidationOrchestrator.ValidationResult result : validationResults) {
            stepMessage.append(result.getConfirmStepMessage()).append("\n");
            errMessage.append(result.getErrMessage()).append("\n");
        }

        // Create REENTER switch field
        List<NamedField> fields = List.of(NamedFieldFactory.createSwitchField(SubCatalogConstants.REENTER));

        // Create remediation actions
        RemediationActions remediationActions = new RemediationActions(
                List.of(RemediationActionFactory.createAskAction(stepMessage.toString().trim(), fields)),
                subCatalogRequestName
        );

        // Create error
        ExtensionResponse.Error error = new ExtensionResponse.Error(
                errMessage.toString().trim(),
                System.currentTimeMillis(),
                exceptionType,
                ""
        );

        // Build and return response using builder pattern
        return new ExtensionResponseBuilder()
                .failure(error, remediationActions, state)
                .build();
    }

    /**
     * Generates a fetch response asking user to provide corrected field values.
     * Creates an interactive prompt with fields for the user to re-enter.
     *
     * @param exceptionType         The type of exception
     * @param validationResults     List of validation failures
     * @param subCatalogRequestName Name of the sub-catalog to handle the re-entered values
     * @param state                 State to preserve for re-entry
     * @return ExtensionResponse with remediation action asking user to provide field values
     */
    public ExtensionResponse generateFetchResponse(
            ExtensionResponse.Error.ExceptionType exceptionType,
            List<ValidationOrchestrator.ValidationResult> validationResults,
            String subCatalogRequestName,
            Map<String, Object> state) {

        LOGGER.debug("Generating fetch response for {} validation failures", validationResults.size());

        // Build error and step messages, and collect fields to fetch
        StringBuilder stepMessage = new StringBuilder();
        StringBuilder errMessage = new StringBuilder();
        List<NamedField> fields = new ArrayList<>();

        for (ValidationOrchestrator.ValidationResult result : validationResults) {
            stepMessage.append(result.getFetchStepMessage()).append("\n");
            errMessage.append(result.getErrMessage()).append("\n");

            // Create field for user to re-enter the value
            fields.add(NamedFieldFactory.createFieldWithAttributes(
                    result.getFetchFieldName(),
                    result.getFieldType(),
                    null
            ));
        }

        LOGGER.debug("Extension Response state: {}", state);

        // Create remediation actions
        RemediationActions remediationActions = new RemediationActions(
                List.of(RemediationActionFactory.createAskAction(stepMessage.toString().trim(), fields)),
                subCatalogRequestName
        );

        // Create error
        ExtensionResponse.Error error = new ExtensionResponse.Error(
                errMessage.toString().trim(),
                System.currentTimeMillis(),
                exceptionType,
                ""
        );

        // Build and return response using builder pattern
        return new ExtensionResponseBuilder()
                .failure(error, remediationActions, state)
                .build();
    }

    /**
     * Generates a fetch deny response when user declines to provide corrected values.
     *
     * @param exceptionType         The type of exception
     * @param validationResults     List of validation failures
     * @param subCatalogRequestName Name of the sub-catalog (can be null)
     * @param state                 State to preserve
     * @return ExtensionResponse informing user that values were not provided
     */
    public ExtensionResponse generateFetchDenyResponse(
            ExtensionResponse.Error.ExceptionType exceptionType,
            List<ValidationOrchestrator.ValidationResult> validationResults,
            String subCatalogRequestName,
            Map<String, Object> state) {

        StringJoiner stepMessage = new StringJoiner(
                Constants.FETCH_DENY_SEPARATOR,
                Constants.FETCH_DENY_PREFIX,
                Constants.FETCH_DENY_SUFFIX);
        StringBuilder errMessage = new StringBuilder();

        for (ValidationOrchestrator.ValidationResult result : validationResults) {
            stepMessage.add(String.format("'%s'", result.getFetchFieldName()));
            errMessage.append(result.getErrMessage()).append(System.lineSeparator());
        }

        // Create remediation actions
        RemediationActions remediationActions = new RemediationActions(
                List.of(RemediationActionFactory.createInformActionAllParticipants(stepMessage.toString(), List.of())),
                subCatalogRequestName
        );

        // Create error
        ExtensionResponse.Error error = new ExtensionResponse.Error(
                errMessage.toString().trim(),
                System.currentTimeMillis(),
                exceptionType,
                ""
        );

        // Build and return response using builder pattern
        return new ExtensionResponseBuilder()
                .failure(error, remediationActions, state)
                .build();
    }

    /**
     * Generates a validation error response when validation fails.
     * Returns an error response with the validation message.
     * The re-entry flow must be handled separately by the caller.
     *
     * @param validationResults  List of validation failures
     * @param subCatalogRequestName Name of the sub-catalog for re-entry confirmation (not used currently)
     * @param state              State to preserve for re-entry
     * @return ExtensionResponse with validation error
     */
    public ExtensionResponse generateValidationErrorResponse(
            List<ValidationOrchestrator.ValidationResult> validationResults,
            String subCatalogRequestName,
            Map<String, Object> state) {

        LOGGER.warn("Generating validation error response for {} validation failures", validationResults.size());

        // Build error message from validation results
        StringBuilder errorMessage = new StringBuilder();
        for (ValidationOrchestrator.ValidationResult result : validationResults) {
            errorMessage.append(result.getConfirmStepMessage()).append("\n");
        }

        // Create error object
        ExtensionResponse.Error error = new ExtensionResponse.Error(
            errorMessage.toString().trim(),
            System.currentTimeMillis(),
            ExtensionResponse.Error.ExceptionType.INPUT_ERROR,
            null
        );

        // Build and return response using builder pattern
        return new ExtensionResponseBuilder()
                .failure(error, null, state)
                .build();
    }

    /**
     * Generates a direct validation error response without retry option.
     * Used when Allow Retry is disabled.
     *
     * @param validationResults List of validation failures
     * @return ExtensionResponse with validation error (no state for retry)
     */
    public ExtensionResponse generateValidationErrorResponse(
            List<ValidationOrchestrator.ValidationResult> validationResults) {

        LOGGER.warn("Generating direct validation error response (no retry) for {} validation failures",
                validationResults.size());

        // Build error message from validation results
        StringBuilder errorMessage = new StringBuilder();
        for (ValidationOrchestrator.ValidationResult result : validationResults) {
            errorMessage.append(result.getErrMessage()).append("\n");
        }

        // Create error object
        ExtensionResponse.Error error = new ExtensionResponse.Error(
            errorMessage.toString().trim(),
            System.currentTimeMillis(),
            ExtensionResponse.Error.ExceptionType.INPUT_ERROR,
            null
        );

        // Build and return response using builder pattern (no state for retry)
        return new ExtensionResponseBuilder()
                .failure(error)
                .build();
    }

    /**
     * Generates a system error response for unexpected errors
     *
     * @param errorMessage The error message
     * @param e            The exception (can be null)
     * @param state        State to preserve for re-entry (can be null)
     * @return ExtensionResponse with FAILURE result and SYSTEM_ERROR
     */
    public ExtensionResponse generateSystemErrorResponse(String errorMessage, Exception e, Map<String, Object> state) {
        LOGGER.error("Generating system error response: {}", errorMessage, e);

        String stackTrace = null;
        if (e != null && e.getStackTrace() != null && e.getStackTrace().length > 0) {
            // Get first few stack trace elements
            StringBuilder sb = new StringBuilder();
            int limit = Math.min(Constants.MAX_STACK_TRACE_ELEMENTS, e.getStackTrace().length);
            for (int i = 0; i < limit; i++) {
                sb.append(e.getStackTrace()[i].toString()).append("\n");
            }
            stackTrace = sb.toString();
        }

        ExtensionResponse.Error error = new ExtensionResponse.Error(
            errorMessage,
            System.currentTimeMillis(),
            ExtensionResponse.Error.ExceptionType.SYSTEM_ERROR,
            stackTrace
        );

        // Build and return response using builder pattern
        return new ExtensionResponseBuilder()
                .failure(error, null, state)
                .build();
    }

    /**
     * Generates a logic error response for business logic violations
     *
     * @param errorMessage The error message
     * @return ExtensionResponse with FAILURE result and LOGIC_ERROR
     */
    public ExtensionResponse generateLogicErrorResponse(String errorMessage) {
        LOGGER.warn("Generating logic error response: {}", errorMessage);

        // Build and return response using builder pattern
        return new ExtensionResponseBuilder()
                .failure(errorMessage)
                .build();
    }

    /**
     * Generates an authorization error response
     *
     * @param errorMessage The error message
     * @param state        State to preserve for re-entry (can be null)
     * @return ExtensionResponse with FAILURE result and SYSTEM_ERROR
     */
    public ExtensionResponse generateAuthorizationErrorResponse(String errorMessage, Map<String, Object> state) {
        LOGGER.error("Generating authorization error response: {}", errorMessage);

        ExtensionResponse.Error error = new ExtensionResponse.Error(
            errorMessage,
            System.currentTimeMillis(),
            ExtensionResponse.Error.ExceptionType.SYSTEM_ERROR,
            null
        );

        // Build and return response using builder pattern
        return new ExtensionResponseBuilder()
                .failure(error, null, state)
                .build();
    }

    /**
     * Generates a direct error response without retry option.
     * Used when Allow Retry is disabled.
     *
     * @param errorMessage The error message
     * @param e            The exception (can be null)
     * @return ExtensionResponse with FAILURE result (no state for retry)
     */
    public ExtensionResponse generateDirectErrorResponse(String errorMessage, Exception e) {
        LOGGER.error("Generating direct error response (no retry): {}", errorMessage, e);

        // Build and return response using builder pattern (no state for retry)
        if (e != null) {
            return new ExtensionResponseBuilder()
                    .failure(errorMessage, e)
                    .build();
        } else {
            return new ExtensionResponseBuilder()
                    .failure(errorMessage)
                    .build();
        }
    }
}

