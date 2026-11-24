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

package com.kristasoft.extensions.ai.nlp.gcp.catalog.extresp;

import app.krista.extension.executor.RemediationAction;
import app.krista.extension.executor.impl.AskAPersonAction;
import app.krista.extension.executor.impl.InformAPersonAction;
import app.krista.model.field.NamedField;
import app.krista.model.field.NamedValuedField;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RemediationActionFactory
 */
class RemediationActionFactoryTest {

    @Test
    void testCreateAskAction() {
        // Arrange
        String message = "Please provide the required information";
        List<NamedField> fields = List.of(
            NamedFieldFactory.createSwitchField("Confirm"),
            NamedFieldFactory.createFieldWithAttributes("Name", "Text", null)
        );

        // Act
        RemediationAction action = RemediationActionFactory.createAskAction(message, fields);

        // Assert
        assertNotNull(action);
        assertTrue(action instanceof AskAPersonAction);
        assertEquals(RemediationAction.RecipientType.ACTIVE_USER, action.getRecipientType());
    }

    @Test
    void testCreateAskActionWithEmptyFields() {
        // Arrange
        String message = "Please confirm";
        List<NamedField> fields = Collections.emptyList();

        // Act
        RemediationAction action = RemediationActionFactory.createAskAction(message, fields);

        // Assert
        assertNotNull(action);
        assertTrue(action instanceof AskAPersonAction);
    }

    @Test
    void testCreateAskActionWithNullMessage() {
        // Arrange
        List<NamedField> fields = List.of(NamedFieldFactory.createSwitchField("Confirm"));

        // Act & Assert - should handle null message gracefully
        assertDoesNotThrow(() -> {
            RemediationAction action = RemediationActionFactory.createAskAction(null, fields);
            assertNotNull(action);
        });
    }

    @Test
    void testCreateInformActionAllParticipants() {
        // Arrange
        String message = "Operation completed successfully";
        List<NamedValuedField> fields = Collections.emptyList();

        // Act
        RemediationAction action = RemediationActionFactory.createInformActionAllParticipants(message, fields);

        // Assert
        assertNotNull(action);
        assertTrue(action instanceof InformAPersonAction);
        assertEquals(RemediationAction.RecipientType.ALL_PARTICIPANTS, action.getRecipientType());
    }

    @Test
    void testCreateInformActionWithNullMessage() {
        // Arrange
        List<NamedValuedField> fields = Collections.emptyList();

        // Act & Assert - should handle null message gracefully
        assertDoesNotThrow(() -> {
            RemediationAction action = RemediationActionFactory.createInformActionAllParticipants(null, fields);
            assertNotNull(action);
        });
    }

    @Test
    void testCreateInformActionWithNullFields() {
        // Arrange
        String message = "Information message";

        // Act & Assert - should handle null fields gracefully
        assertDoesNotThrow(() -> {
            RemediationAction action = RemediationActionFactory.createInformActionAllParticipants(message, null);
            assertNotNull(action);
        });
    }

    @Test
    void testFactoryMethodsReturnDifferentActionTypes() {
        // Arrange
        String message = "Test message";
        List<NamedField> askFields = List.of(NamedFieldFactory.createSwitchField("Field"));
        List<NamedValuedField> informFields = Collections.emptyList();

        // Act
        RemediationAction askAction = RemediationActionFactory.createAskAction(message, askFields);
        RemediationAction informAction = RemediationActionFactory.createInformActionAllParticipants(message, informFields);

        // Assert
        assertNotEquals(askAction.getClass(), informAction.getClass());
        assertTrue(askAction instanceof AskAPersonAction);
        assertTrue(informAction instanceof InformAPersonAction);
    }

    @Test
    void testFactoryMethodsReturnDifferentRecipientTypes() {
        // Arrange
        String message = "Test message";
        List<NamedField> askFields = List.of(NamedFieldFactory.createSwitchField("Field"));
        List<NamedValuedField> informFields = Collections.emptyList();

        // Act
        RemediationAction askAction = RemediationActionFactory.createAskAction(message, askFields);
        RemediationAction informAction = RemediationActionFactory.createInformActionAllParticipants(message, informFields);

        // Assert
        assertEquals(RemediationAction.RecipientType.ACTIVE_USER, askAction.getRecipientType());
        assertEquals(RemediationAction.RecipientType.ALL_PARTICIPANTS, informAction.getRecipientType());
    }
}

