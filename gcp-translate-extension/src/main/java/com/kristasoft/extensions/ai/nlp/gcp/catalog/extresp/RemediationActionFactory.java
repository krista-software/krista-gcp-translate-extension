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

import java.util.List;

/**
 * Factory class for creating RemediationAction instances.
 * Provides methods to create Ask and Inform actions for user interaction.
 */
public class RemediationActionFactory {

    private RemediationActionFactory() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates an Ask action that prompts the active user with a message and fields to fill.
     *
     * @param message The message to display to the user
     * @param fields  The fields for the user to fill in
     * @return RemediationAction configured as an Ask action
     */
    public static RemediationAction createAskAction(String message, List<NamedField> fields) {
        return AskAPersonAction.create(message, RemediationAction.RecipientType.ACTIVE_USER, fields);
    }

    /**
     * Creates an Inform action that displays a message to all participants.
     *
     * @param message The message to display
     * @param fields  The fields to display with values
     * @return RemediationAction configured as an Inform action for all participants
     */
    public static RemediationAction createInformActionAllParticipants(String message, List<NamedValuedField> fields) {
        return InformAPersonAction.create(message, RemediationAction.RecipientType.ALL_PARTICIPANTS, fields);
    }
}

