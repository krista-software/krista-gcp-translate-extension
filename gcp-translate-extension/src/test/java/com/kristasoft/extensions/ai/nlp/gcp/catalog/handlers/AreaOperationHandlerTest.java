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
import com.kristasoft.extensions.ai.nlp.gcp.catalog.validators.ValidationOrchestrator;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.validators.Validator;
import io.opentelemetry.api.trace.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

/**
 * Unit tests for AreaOperationHandler
 */
class AreaOperationHandlerTest {

    @Mock
    private ValidationOrchestrator validationOrchestrator;

    @Mock
    private InternalStateManager stateManager;

    @Mock
    private ExtensionResponseGenerator responseGenerator;

    @Mock
    private TelemetryHelper telemetryHelper;

    @Mock
    private Span span;

    private AreaOperationHandler<String> handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new AreaOperationHandler<>(
            validationOrchestrator,
            stateManager,
            responseGenerator,
            telemetryHelper
        );
    }

    @Test
    void testExecuteSuccess() {
        // Arrange
        String operationName = "testOperation";
        Map<Validator.ValidationResource, String> validationMap = new HashMap<>();
        validationMap.put(Validator.ValidationResource.TEXT_INPUT, "test text");

        // Mock validation success (empty list)
        when(validationOrchestrator.validate(validationMap)).thenReturn(Collections.emptyList());

        // Mock business logic
        Supplier<String> businessLogic = () -> "Success Result";

        // Mock result mapper
        Function<String, Map<String, Object>> resultMapper = result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("result", result);
            return map;
        };

        // Mock telemetry
        when(telemetryHelper.startSpan(operationName)).thenReturn(span);

        // Mock success response
        ExtensionResponse successResponse = mock(ExtensionResponse.class);
        when(successResponse.getResult()).thenReturn(ExtensionResponse.Result.SUCCESS);
        when(responseGenerator.generateSuccessResponse(anyMap())).thenReturn(successResponse);

        // Act
        ExtensionResponse response = handler.execute(
            operationName,
            validationMap,
            "ConfirmSubCatalog",
            stateId -> Map.of("stateId", stateId),
            businessLogic,
            resultMapper,
            Map.of("tag1", "value1"),
            true  // allowRetry
        );

        // Assert
        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.SUCCESS, response.getResult());

        verify(validationOrchestrator, times(1)).validate(validationMap);
        verify(responseGenerator, times(1)).generateSuccessResponse(anyMap());
        verify(telemetryHelper, times(1)).incrementCount(eq(operationName + ".success"), anyMap());
        verify(telemetryHelper, times(1)).recordDuration(eq(operationName + ".duration"), anyLong(), anyMap());
        verify(telemetryHelper, times(1)).endSpan(span, true);
    }

    @Test
    void testExecuteValidationFailure() {
        // Arrange
        String operationName = "testOperation";
        Map<Validator.ValidationResource, String> validationMap = new HashMap<>();

        // Mock validation failure
        ValidationOrchestrator.ValidationResult validationError = 
            new ValidationOrchestrator.ValidationResult(
                "Confirm message",
                "Text",
                "Fetch message",
                "Validation error",
                "Paragraph"
            );
        when(validationOrchestrator.validate(validationMap))
            .thenReturn(Arrays.asList(validationError));

        // Mock state manager
        when(stateManager.generateStateId()).thenReturn("state-123");

        // Mock telemetry
        when(telemetryHelper.startSpan(operationName)).thenReturn(span);

        // Mock error response with remediation actions
        ExtensionResponse errorResponse = mock(ExtensionResponse.class);
        when(errorResponse.getResult()).thenReturn(ExtensionResponse.Result.FAILURE);
        when(errorResponse.getRemediationActions()).thenReturn(mock(app.krista.extension.executor.RemediationActions.class));
        when(responseGenerator.generateConfirmationResponse(any(), anyList(), anyString(), anyMap()))
            .thenReturn(errorResponse);

        // Business logic should not be called
        Supplier<String> businessLogic = () -> {
            fail("Business logic should not be called on validation failure");
            return null;
        };

        // Act
        ExtensionResponse response = handler.execute(
            operationName,
            validationMap,
            "ConfirmSubCatalog",
            stateId -> {
                Map<String, Object> state = new HashMap<>();
                state.put("stateId", stateId);
                return state;
            },
            businessLogic,
            result -> Map.of(),
            Map.of(),
            true  // allowRetry
        );

        // Assert
        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());
        assertNotNull(response.getRemediationActions());  // Should have remediation actions now

        verify(validationOrchestrator, times(1)).validate(validationMap);
        verify(stateManager, times(1)).generateStateId();
        verify(stateManager, times(1)).saveState(eq("state-123"), anyMap());
        verify(responseGenerator, times(1)).generateConfirmationResponse(any(), anyList(), eq("ConfirmSubCatalog"), anyMap());
        verify(telemetryHelper, times(1)).incrementCount(eq(operationName + ".validation_failed"), anyMap());
        verify(telemetryHelper, times(1)).endSpan(span, false);
    }

    @Test
    void testExecuteBusinessLogicException() {
        // Arrange
        String operationName = "testOperation";
        Map<Validator.ValidationResource, String> validationMap = new HashMap<>();

        // Mock validation success
        when(validationOrchestrator.validate(validationMap)).thenReturn(Collections.emptyList());

        // Mock business logic throwing exception
        RuntimeException businessException = new RuntimeException("Business logic failed");
        Supplier<String> businessLogic = () -> {
            throw businessException;
        };

        // Mock telemetry
        when(telemetryHelper.startSpan(operationName)).thenReturn(span);

        // Mock state manager
        when(stateManager.generateStateId()).thenReturn("state-456");

        // Mock error response
        ExtensionResponse errorResponse = mock(ExtensionResponse.class);
        when(errorResponse.getResult()).thenReturn(ExtensionResponse.Result.FAILURE);
        when(responseGenerator.generateSystemErrorResponse(anyString(), eq(businessException), anyMap()))
            .thenReturn(errorResponse);

        // Act
        ExtensionResponse response = handler.execute(
            operationName,
            validationMap,
            "ConfirmSubCatalog",
            stateId -> Map.of("stateId", stateId),
            businessLogic,
            result -> Map.of(),
            Map.of(),
            true  // allowRetry
        );

        // Assert
        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());

        verify(stateManager, times(1)).generateStateId();
        verify(stateManager, times(1)).saveState(eq("state-456"), anyMap());
        verify(responseGenerator, times(1)).generateSystemErrorResponse(anyString(), eq(businessException), anyMap());
        verify(telemetryHelper, times(1)).incrementCount(eq(operationName + ".error"), anyMap());
        verify(telemetryHelper, times(1)).recordException(span, businessException);
        verify(telemetryHelper, times(1)).endSpan(span, false);
    }

    @Test
    void testExecuteAuthorizationError() {
        // Arrange
        String operationName = "testOperation";
        Map<Validator.ValidationResource, String> validationMap = new HashMap<>();

        when(validationOrchestrator.validate(validationMap)).thenReturn(Collections.emptyList());

        // Mock business logic throwing auth exception
        RuntimeException authException = new RuntimeException("UNAUTHENTICATED: Invalid credentials");
        Supplier<String> businessLogic = () -> {
            throw authException;
        };

        when(telemetryHelper.startSpan(operationName)).thenReturn(span);

        // Mock state manager
        when(stateManager.generateStateId()).thenReturn("state-789");

        // Mock authorization error response
        ExtensionResponse errorResponse = mock(ExtensionResponse.class);
        when(errorResponse.getResult()).thenReturn(ExtensionResponse.Result.FAILURE);
        when(responseGenerator.generateAuthorizationErrorResponse(anyString(), anyMap()))
            .thenReturn(errorResponse);

        // Act
        ExtensionResponse response = handler.execute(
            operationName,
            validationMap,
            "ConfirmSubCatalog",
            stateId -> Map.of("stateId", stateId),
            businessLogic,
            result -> Map.of(),
            Map.of(),
            true  // allowRetry
        );

        // Assert
        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());

        verify(stateManager, times(1)).generateStateId();
        verify(stateManager, times(1)).saveState(eq("state-789"), anyMap());
        verify(responseGenerator, times(1)).generateAuthorizationErrorResponse(anyString(), anyMap());
    }

    @Test
    void testExecuteRecordsDuration() {
        // Arrange
        when(validationOrchestrator.validate(anyMap())).thenReturn(Collections.emptyList());
        when(telemetryHelper.startSpan(anyString())).thenReturn(span);
        
        ExtensionResponse successResponse = mock(ExtensionResponse.class);
        when(responseGenerator.generateSuccessResponse(anyMap())).thenReturn(successResponse);

        Supplier<String> businessLogic = () -> {
            try {
                Thread.sleep(10); // Small delay to ensure duration > 0
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "result";
        };

        // Act
        handler.execute(
            "testOp",
            Map.of(),
            "SubCatalog",
            stateId -> Map.of(),
            businessLogic,
            result -> Map.of("result", result),
            Map.of(),
            true  // allowRetry
        );

        // Assert
        verify(telemetryHelper, times(1)).recordDuration(
            eq("testOp.duration"),
            longThat(duration -> duration >= 0),
            anyMap()
        );
    }

    @Test
    void testExecuteWithNullSpan() {
        // Arrange - telemetry returns null span
        when(validationOrchestrator.validate(anyMap())).thenReturn(Collections.emptyList());
        when(telemetryHelper.startSpan(anyString())).thenReturn(null);
        
        ExtensionResponse successResponse = mock(ExtensionResponse.class);
        when(responseGenerator.generateSuccessResponse(anyMap())).thenReturn(successResponse);

        // Act - should not throw NPE
        ExtensionResponse response = handler.execute(
            "testOp",
            Map.of(),
            "SubCatalog",
            stateId -> Map.of(),
            () -> "result",
            result -> Map.of(),
            Map.of(),
            true  // allowRetry
        );

        // Assert
        assertNotNull(response);
        verify(telemetryHelper, never()).endSpan(any(), anyBoolean());
    }
}

