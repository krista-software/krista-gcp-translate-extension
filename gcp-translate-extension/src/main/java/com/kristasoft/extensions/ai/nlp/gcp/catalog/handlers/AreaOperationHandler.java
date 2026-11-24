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

package com.kristasoft.extensions.ai.nlp.gcp.catalog.handlers;

import app.krista.extension.executor.ExtensionResponse;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.errorhandlers.ExtensionResponseGenerator;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.state.InternalStateManager;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.telemetry.TelemetryHelper;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.Constants;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.SubCatalogConstants;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.validators.ValidationOrchestrator;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.validators.Validator;
import io.opentelemetry.api.trace.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Generic operation handler that orchestrates validation, business logic execution,
 * error handling, and telemetry for all translation operations.
 *
 * @param <T> The type of result returned by the business logic
 */
public class AreaOperationHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AreaOperationHandler.class);

    private final ValidationOrchestrator validationOrchestrator;
    private final InternalStateManager stateManager;
    private final ExtensionResponseGenerator responseGenerator;
    private final TelemetryHelper telemetryHelper;

    public AreaOperationHandler(
            ValidationOrchestrator validationOrchestrator,
            InternalStateManager stateManager,
            ExtensionResponseGenerator responseGenerator,
            TelemetryHelper telemetryHelper) {
        this.validationOrchestrator = validationOrchestrator;
        this.stateManager = stateManager;
        this.responseGenerator = responseGenerator;
        this.telemetryHelper = telemetryHelper;
    }

    /**
     * Executes an operation with validation, error handling, and telemetry
     *
     * @param operationName         Name of the operation for logging/telemetry
     * @param validationMap         Map of validation resources to validate
     * @param confirmSubCatalogName Name of sub-catalog for re-entry confirmation
     * @param stateMapper           Function to create state map from stateId
     * @param businessLogic         Supplier that executes the actual business logic
     * @param resultMapper          Function to map business logic result to response data
     * @param telemetryTags         Tags for telemetry metrics
     * @param allowRetry            Whether to allow retry on validation/error failures
     * @return ExtensionResponse with success or error
     */
    public ExtensionResponse execute(
            String operationName,
            Map<Validator.ValidationResource, String> validationMap,
            String confirmSubCatalogName,
            Function<String, Map<String, Object>> stateMapper,
            Supplier<T> businessLogic,
            Function<T, Map<String, Object>> resultMapper,
            Map<String, String> telemetryTags,
            Boolean allowRetry) {

        long startTime = System.currentTimeMillis();
        Span span = null;

        try {
            // Start telemetry span
            span = telemetryHelper.startSpan(operationName);
            if (span != null) {
                span.setAttribute(Constants.TELEMETRY_ATTR_OPERATION, operationName);
            }

            LOGGER.info("Starting operation: {} with allowRetry: {}", operationName, allowRetry);
            List<ValidationOrchestrator.ValidationResult> validationResults = validationOrchestrator.validate(validationMap);

            if (!validationResults.isEmpty()) {
                LOGGER.warn("Validation failed for operation: {} with {} errors",
                    operationName, validationResults.size());

                telemetryHelper.incrementCount(operationName + Constants.METRIC_SUFFIX_VALIDATION_FAILED, telemetryTags);

                if (span != null) {
                    span.setAttribute(Constants.TELEMETRY_ATTR_VALIDATION_FAILED, true);
                    span.setAttribute(Constants.TELEMETRY_ATTR_VALIDATION_ERRORS, validationResults.size());
                    telemetryHelper.endSpan(span, false);
                }

                // Check if retry is allowed
                if (Boolean.TRUE.equals(allowRetry)) {
                    LOGGER.debug("Allow Retry is enabled, generating confirmation response for re-entry");

                    String stateId = stateManager.generateStateId();
                    Map<String, Object> state = stateMapper.apply(stateId);
                    state.put(SubCatalogConstants.VALIDATION_RESULTS, validationResults);
                    stateManager.saveState(stateId, state);

                    LOGGER.debug("Saved state with ID: {} for re-entry", stateId);

                    // Return confirmation response with remediation action for re-entry
                    return responseGenerator.generateConfirmationResponse(
                            ExtensionResponse.Error.ExceptionType.INPUT_ERROR,
                            validationResults,
                            confirmSubCatalogName,
                            state
                    );
                } else {
                    LOGGER.debug("Allow Retry is disabled, returning direct validation error");

                    // Return direct error response without retry option
                    return responseGenerator.generateValidationErrorResponse(validationResults);
                }
            }

            LOGGER.debug("Validation passed for operation: {}", operationName);
            LOGGER.debug("Executing business logic for: {}", operationName);
            T result = businessLogic.get();
            LOGGER.debug("Business logic completed successfully for: {}", operationName);

            Map<String, Object> mappedResult = resultMapper.apply(result);
            long duration = System.currentTimeMillis() - startTime;
            telemetryHelper.recordDuration(operationName + Constants.METRIC_SUFFIX_DURATION, duration, telemetryTags);
            telemetryHelper.incrementCount(operationName + Constants.METRIC_SUFFIX_SUCCESS, telemetryTags);

            LOGGER.info("Operation {} completed successfully in {}ms", operationName, duration);

            if (span != null) {
                span.setAttribute(Constants.TELEMETRY_ATTR_SUCCESS, true);
                span.setAttribute(Constants.TELEMETRY_ATTR_DURATION_MS, duration);
                telemetryHelper.endSpan(span, true);
            }

            // 5. Return success response
            return responseGenerator.generateSuccessResponse(mappedResult);

        } catch (Exception e) {
            // Handle errors
            long duration = System.currentTimeMillis() - startTime;
            telemetryHelper.recordDuration(operationName + Constants.METRIC_SUFFIX_DURATION, duration, telemetryTags);
            telemetryHelper.incrementCount(operationName + Constants.METRIC_SUFFIX_ERROR, telemetryTags);

            LOGGER.error("Error in operation: {} after {}ms", operationName, duration, e);

            if (span != null) {
                telemetryHelper.recordException(span, e);
                span.setAttribute(Constants.TELEMETRY_ATTR_ERROR, true);
                span.setAttribute(Constants.TELEMETRY_ATTR_ERROR_MESSAGE, e.getMessage());
                span.setAttribute(Constants.TELEMETRY_ATTR_DURATION_MS, duration);
                telemetryHelper.endSpan(span, false);
            }

            // Determine error type and message
            String errorMessage = determineErrorMessage(e);
            if (Boolean.TRUE.equals(allowRetry)) {
                LOGGER.debug("Allow Retry is enabled, generating error response with re-entry option");

                String stateId = stateManager.generateStateId();
                Map<String, Object> state = stateMapper.apply(stateId);
                stateManager.saveState(stateId, state);

                LOGGER.debug("Saved state with ID: {} for re-entry after error", stateId);

                if (isAuthorizationError(e)) {
                    return responseGenerator.generateAuthorizationErrorResponse(errorMessage, state);
                }

                return responseGenerator.generateSystemErrorResponse(errorMessage, e, state);
            } else {
                LOGGER.debug("Allow Retry is disabled, returning direct error response");
                return responseGenerator.generateDirectErrorResponse(errorMessage, e);
            }
        }
    }

    /**
     * Determines a user-friendly error message based on the exception
     */
    private String determineErrorMessage(Exception e) {
        if (e == null || e.getMessage() == null) {
            return Constants.ERROR_UNEXPECTED;
        }

        String message = e.getMessage();

        if (message.contains(Constants.GCP_ERROR_INVALID_ARGUMENT)) {
            return Constants.ERROR_INVALID_ARGUMENT;
        } else if (message.contains(Constants.GCP_ERROR_UNAUTHENTICATED) || message.contains(Constants.GCP_ERROR_PERMISSION_DENIED)) {
            return Constants.ERROR_AUTHENTICATION_FAILED;
        } else if (message.contains(Constants.GCP_ERROR_UNAVAILABLE) || message.contains(Constants.GCP_ERROR_DEADLINE_EXCEEDED)) {
            return Constants.ERROR_SERVICE_UNAVAILABLE;
        } else if (message.contains(Constants.GCP_ERROR_RESOURCE_EXHAUSTED)) {
            return Constants.ERROR_QUOTA_EXCEEDED;
        } else if (message.contains(Constants.GCP_ERROR_NOT_FOUND)) {
            return Constants.ERROR_RESOURCE_NOT_FOUND;
        }

        return Constants.ERROR_WITH_MESSAGE_PREFIX + message + Constants.ERROR_WITH_MESSAGE_SUFFIX;
    }

    /**
     * Checks if the exception is an authorization error
     */
    private boolean isAuthorizationError(Exception e) {
        if (e == null || e.getMessage() == null) {
            return false;
        }
        String message = e.getMessage();
        return message.contains(Constants.GCP_ERROR_UNAUTHENTICATED) ||
               message.contains(Constants.GCP_ERROR_PERMISSION_DENIED) ||
               message.contains(Constants.GCP_ERROR_CREDENTIALS);
    }
}

