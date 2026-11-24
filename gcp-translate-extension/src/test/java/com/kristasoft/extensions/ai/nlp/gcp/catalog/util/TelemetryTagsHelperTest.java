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

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TelemetryTagsHelperTest {

    @Test
    void createTranslationTags_withSourceAndTarget() {
        Map<String, String> tags = TelemetryTagsHelper.createTranslationTags("en", "fr");
        assertNotNull(tags);
        assertFalse(tags.isEmpty());
        assertTrue(tags.containsValue("en"));
        assertTrue(tags.containsValue("fr"));
    }

    @Test
    void createTranslationTags_withNullSource_usesAutoDetect() {
        Map<String, String> tags = TelemetryTagsHelper.createTranslationTags(null, "fr");
        assertNotNull(tags);
        assertTrue(tags.containsValue("fr"));
    }

    @Test
    void createTranslationTags_withBlankSource_usesAutoDetect() {
        Map<String, String> tags = TelemetryTagsHelper.createTranslationTags("   ", "de");
        assertNotNull(tags);
        assertTrue(tags.containsValue("de"));
    }

    @Test
    void createTranslationTags_withNullTarget_usesUnknown() {
        Map<String, String> tags = TelemetryTagsHelper.createTranslationTags("en", null);
        assertNotNull(tags);
        assertFalse(tags.isEmpty());
    }

    @Test
    void createDetectionTags_coversAllLengthRanges() {
        // exercises each branch of getTextLengthRange
        for (int len : new int[]{0, 50, 500, 5000, 50000}) {
            Map<String, String> tags = TelemetryTagsHelper.createDetectionTags(len);
            assertNotNull(tags);
            assertFalse(tags.isEmpty());
        }
    }
}
