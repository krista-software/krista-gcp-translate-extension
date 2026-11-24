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
import com.kristasoft.extensions.ai.nlp.gcp.catalog.validators.ValidationOrchestrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ExtensionResponseGenerator
 */
class ExtensionResponseGeneratorTest {

    private ExtensionResponseGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new ExtensionResponseGenerator();
    }

    @Test
    void testGenerateSuccessResponse() {
        Map<String, Object> data = new HashMap<>();
        data.put("result", "test value");
        data.put("count", 42);
        
        ExtensionResponse response = generator.generateSuccessResponse(data);
        
        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.SUCCESS, response.getResult());
        assertNull(response.getError());
    }

    @Test
    void testGenerateSuccessResponseWithEmptyData() {
        Map<String, Object> data = new HashMap<>();
        
        ExtensionResponse response = generator.generateSuccessResponse(data);
        
        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.SUCCESS, response.getResult());
    }

    @Test
    void testGenerateValidationErrorResponse() {
        // Create validation results
        List<ValidationOrchestrator.ValidationResult> validationResults = new ArrayList<>();
        validationResults.add(new ValidationOrchestrator.ValidationResult(
            "Confirm message",
            "Text",
            "Fetch message",
            "Text is required",
            "Paragraph"
        ));

        Map<String, Object> state = new HashMap<>();
        state.put("stateId", "test-123");

        ExtensionResponse response = generator.generateValidationErrorResponse(
            validationResults,
            "ConfirmSubCatalog",
            state
        );

        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());
        assertNotNull(response.getError());
        assertEquals(ExtensionResponse.Error.ExceptionType.INPUT_ERROR, response.getError().getExceptionType());
    }

    @Test
    void testGenerateValidationErrorResponseMultipleErrors() {
        // Create multiple validation results
        List<ValidationOrchestrator.ValidationResult> validationResults = new ArrayList<>();
        validationResults.add(new ValidationOrchestrator.ValidationResult(
            "Confirm 1",
            "Field1",
            "Fetch 1",
            "Error 1",
            "Text"
        ));
        validationResults.add(new ValidationOrchestrator.ValidationResult(
            "Confirm 2",
            "Field2",
            "Fetch 2",
            "Error 2",
            "Text"
        ));

        Map<String, Object> state = new HashMap<>();

        ExtensionResponse response = generator.generateValidationErrorResponse(
            validationResults,
            "ConfirmSubCatalog",
            state
        );

        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());
        assertNotNull(response.getError());
    }

    @Test
    void testGenerateSystemErrorResponse() {
        String errorMessage = "Database connection failed";
        Exception exception = new RuntimeException("Connection timeout");
        Map<String, Object> state = new HashMap<>();
        state.put("stateId", "test-123");

        ExtensionResponse response = generator.generateSystemErrorResponse(errorMessage, exception, state);

        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());
        assertNotNull(response.getError());
        assertEquals(ExtensionResponse.Error.ExceptionType.SYSTEM_ERROR, response.getError().getExceptionType());
        assertNotNull(response.getError().getStackTrace());
        assertNotNull(response.getState());
    }

    @Test
    void testGenerateSystemErrorResponseWithoutException() {
        String errorMessage = "System error occurred";

        ExtensionResponse response = generator.generateSystemErrorResponse(errorMessage, null, null);

        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());
        assertNotNull(response.getError());
    }

    @Test
    void testGenerateLogicErrorResponse() {
        String errorMessage = "Business rule violation";
        
        ExtensionResponse response = generator.generateLogicErrorResponse(errorMessage);
        
        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());
        assertNotNull(response.getError());
        assertEquals(ExtensionResponse.Error.ExceptionType.LOGIC_ERROR, response.getError().getExceptionType());
    }

    @Test
    void testGenerateAuthorizationErrorResponse() {
        String errorMessage = "Invalid credentials";
        Map<String, Object> state = new HashMap<>();
        state.put("stateId", "test-123");

        ExtensionResponse response = generator.generateAuthorizationErrorResponse(errorMessage, state);

        assertNotNull(response);
        assertEquals(ExtensionResponse.Result.FAILURE, response.getResult());
        assertNotNull(response.getError());
        // Note: Uses SYSTEM_ERROR since AUTHORIZATION_ERROR doesn't exist
        assertEquals(ExtensionResponse.Error.ExceptionType.SYSTEM_ERROR, response.getError().getExceptionType());
        assertNotNull(response.getState());
    }

    @Test
    void testErrorResponsesHaveTimestamp() {
        ExtensionResponse response = generator.generateSystemErrorResponse("Test error", null, null);

        assertNotNull(response.getError());
        assertTrue(response.getError().getTimestamp() > 0);
        assertTrue(response.getError().getTimestamp() <= System.currentTimeMillis());
    }
}

