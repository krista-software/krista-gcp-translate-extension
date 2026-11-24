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

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to create telemetry tags for different operations.
 * Tags are used to add context to metrics and traces.
 */
public class TelemetryTagsHelper {

    private TelemetryTagsHelper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates telemetry tags for translation operations
     *
     * @param sourceLanguage The source language (can be null for auto-detect)
     * @param targetLanguage The target language
     * @return Map of telemetry tags
     */
    public static Map<String, String> createTranslationTags(String sourceLanguage, String targetLanguage) {
        Map<String, String> tags = new HashMap<>();

        tags.put(Constants.TAG_TARGET_LANGUAGE, targetLanguage != null ? targetLanguage : Constants.TAG_VALUE_UNKNOWN);
        tags.put(Constants.TAG_SOURCE_LANGUAGE, sourceLanguage != null && !sourceLanguage.trim().isEmpty()
            ? sourceLanguage : Constants.TAG_VALUE_AUTO_DETECT);
        tags.put(Constants.TAG_OPERATION, Constants.TAG_VALUE_TRANSLATE);

        return tags;
    }

    /**
     * Creates telemetry tags for language detection operations
     *
     * @param textLength The length of the text being analyzed
     * @return Map of telemetry tags
     */
    public static Map<String, String> createDetectionTags(int textLength) {
        Map<String, String> tags = new HashMap<>();

        tags.put(Constants.TAG_OPERATION, Constants.TAG_VALUE_DETECT_LANGUAGE);
        tags.put(Constants.TAG_TEXT_LENGTH_RANGE, getTextLengthRange(textLength));

        return tags;
    }

    /**
     * Gets the text length range for categorization
     *
     * @param length The text length
     * @return Range category (e.g., "0-100", "101-1000", etc.)
     */
    private static String getTextLengthRange(int length) {
        if (length <= Constants.TEXT_LENGTH_THRESHOLD_100) {
            return Constants.TEXT_LENGTH_RANGE_0_100;
        } else if (length <= Constants.TEXT_LENGTH_THRESHOLD_1000) {
            return Constants.TEXT_LENGTH_RANGE_101_1000;
        } else if (length <= Constants.TEXT_LENGTH_THRESHOLD_10000) {
            return Constants.TEXT_LENGTH_RANGE_1001_10000;
        } else {
            return Constants.TEXT_LENGTH_RANGE_10001_PLUS;
        }
    }
}

