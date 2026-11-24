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

package com.kristasoft.extensions.ai.nlp.gcp.catalog.telemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TelemetryHelperTest {

    private TelemetryHelper helper;

    @BeforeEach
    void setUp() {
        // Real SDK tracer -> spans are recording, so endSpan/recordException inner paths run.
        OpenTelemetry otel = OpenTelemetrySdk.builder()
            .setTracerProvider(SdkTracerProvider.builder().build())
            .build();
        helper = new TelemetryHelper(otel);
    }

    @Test
    void defaultConstructor_usesGlobalOpenTelemetry() {
        assertDoesNotThrow(() -> { new TelemetryHelper(); });
    }

    @Test
    void incrementCount_noTags_doesNotThrow() {
        assertDoesNotThrow(() -> helper.incrementCount("requests"));
    }

    @Test
    void incrementCount_withTags_doesNotThrow() {
        assertDoesNotThrow(() -> helper.incrementCount("requests", Map.of("op", "translate")));
    }

    @Test
    void recordDuration_doesNotThrow() {
        assertDoesNotThrow(() -> helper.recordDuration("duration", 42L, Map.of("op", "detect")));
    }

    @Test
    void startSpan_noAttributes_returnsSpan() {
        Span span = helper.startSpan("op");
        assertNotNull(span);
        helper.endSpan(span, true);
    }

    @Test
    void startSpan_withAttributes_andEndAndException() {
        Span span = helper.startSpan("op", Map.of("k", "v"));
        assertNotNull(span);
        assertTrue(span.isRecording());
        helper.recordException(span, new RuntimeException("boom"));
        helper.endSpan(span, false);
        assertFalse(span.isRecording());
    }

    @Test
    void endSpan_nullSpan_doesNotThrow() {
        assertDoesNotThrow(() -> helper.endSpan(null, true));
    }

    @Test
    void recordException_nullSpan_doesNotThrow() {
        assertDoesNotThrow(() -> helper.recordException(null, new RuntimeException("x")));
    }
}
