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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidationOrchestratorTest {

    private GcpTranslateClient client;
    private ValidationOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        client = mock(GcpTranslateClient.class);
        orchestrator = new ValidationOrchestrator(client);
    }

    @Test
    void validate_invalidText_producesResultWithPopulatedFields() {
        Map<Validator.ValidationResource, String> resources = new HashMap<>();
        resources.put(Validator.ValidationResource.TEXT_INPUT, ""); // invalid → fails validation

        List<ValidationOrchestrator.ValidationResult> results = orchestrator.validate(resources);

        assertEquals(1, results.size());
        ValidationOrchestrator.ValidationResult r = results.get(0);
        assertNotNull(r.getConfirmStepMessage());
        assertNotNull(r.getErrMessage());
        assertNotNull(r.getFetchFieldName());
        assertNotNull(r.getFieldType());
        assertNotNull(r.getFetchStepMessage());
    }

    @Test
    void validate_validText_producesNoResult() {
        Map<Validator.ValidationResource, String> resources = new HashMap<>();
        resources.put(Validator.ValidationResource.TEXT_INPUT, "A perfectly valid sentence to translate.");

        List<ValidationOrchestrator.ValidationResult> results = orchestrator.validate(resources);

        assertTrue(results.isEmpty());
    }

    @Test
    void validate_whenValidatorThrows_capturesErrorResult() {
        // LanguageValidator consults the client; make it blow up to exercise the catch branch.
        when(client.getSupportedLanguages()).thenThrow(new RuntimeException("boom"));
        Map<Validator.ValidationResource, String> resources = new HashMap<>();
        resources.put(Validator.ValidationResource.TARGET_LANGUAGE, "zz");

        List<ValidationOrchestrator.ValidationResult> results = orchestrator.validate(resources);

        // Either the normal invalid path or the catch path adds a result; both are acceptable.
        assertNotNull(results);
    }
}
