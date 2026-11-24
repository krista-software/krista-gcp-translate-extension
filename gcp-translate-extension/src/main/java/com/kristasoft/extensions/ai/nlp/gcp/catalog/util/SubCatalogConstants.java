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

package com.kristasoft.extensions.ai.nlp.gcp.catalog.util;

/**
 * Constants for sub-catalog request names.
 * These are used for re-entry flows when validation fails.
 */
public class SubCatalogConstants {

    // Translate To Language sub-catalogs
    public static final String CONFIRM_REENTER_TRANSLATE = "Confirm Re-enter Translate To Language";
    public static final String HANDLE_REENTER_TRANSLATE = "Handle Re-enter Translate To Language";

    // Detect Language sub-catalogs
    public static final String CONFIRM_REENTER_DETECT = "Confirm Re-enter Detect Language";
    public static final String HANDLE_REENTER_DETECT = "Handle Re-enter Detect Language";

    // Field names for re-entry
    public static final String REENTER = "Re-enter";
    public static final String STATE_ID = "State Id";
    public static final String TEXT = "Text";
    public static final String SOURCE_LANGUAGE = "Source Language";
    public static final String TARGET_LANGUAGE = "Target Language";
    public static final String VALIDATION_RESULTS = "Validation Results";

    private SubCatalogConstants() {
        // Private constructor to prevent instantiation
    }
}

