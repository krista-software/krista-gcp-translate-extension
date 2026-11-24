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

import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.Constants;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Helper class for telemetry operations including metrics and tracing.
 * Provides a simplified interface for recording metrics and creating spans.
 * Uses GlobalOpenTelemetry if no OpenTelemetry instance is injected.
 */
@Service
public class TelemetryHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryHelper.class);

    private final Tracer tracer;
    private final LongCounter requestCounter;
    private final LongHistogram durationHistogram;

    /**
     * Default constructor that uses GlobalOpenTelemetry.
     * This is used when OpenTelemetry is not available via dependency injection.
     */
    public TelemetryHelper() {
        this(GlobalOpenTelemetry.get());
    }

    /**
     * Constructor with OpenTelemetry instance.
     * This allows for custom OpenTelemetry configuration.
     */
    public TelemetryHelper(OpenTelemetry openTelemetry) {
        try {
            this.tracer = openTelemetry.getTracer(Constants.INSTRUMENTATION_NAME);
            Meter meter = openTelemetry.getMeter(Constants.INSTRUMENTATION_NAME);

            // Initialize metrics
            this.requestCounter = meter
                .counterBuilder(Constants.METRIC_REQUESTS)
                .setDescription(Constants.METRIC_REQUESTS_DESC)
                .setUnit(Constants.METRIC_UNIT_COUNT)
                .build();

            this.durationHistogram = meter
                .histogramBuilder(Constants.METRIC_DURATION)
                .setDescription(Constants.METRIC_DURATION_DESC)
                .setUnit(Constants.METRIC_UNIT_MS)
                .ofLongs()
                .build();

            LOGGER.info("TelemetryHelper initialized with OpenTelemetry");
        } catch (Exception e) {
            LOGGER.warn("Failed to initialize telemetry, telemetry will be disabled: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize telemetry", e);
        }
    }

    /**
     * Increments a counter metric
     *
     * @param metricName Name of the metric
     */
    public void incrementCount(String metricName) {
        incrementCount(metricName, Map.of());
    }

    /**
     * Increments a counter metric with tags
     *
     * @param metricName Name of the metric
     * @param tags       Tags to add to the metric
     */
    public void incrementCount(String metricName, Map<String, String> tags) {
        try {
            Attributes attributes = buildAttributes(tags);
            requestCounter.add(1, attributes);
            LOGGER.debug("Incremented counter: {} with tags: {}", metricName, tags);
        } catch (Exception e) {
            LOGGER.warn("Failed to increment counter: {}", metricName, e);
        }
    }

    /**
     * Records a duration metric
     *
     * @param metricName Name of the metric
     * @param duration   Duration in milliseconds
     * @param tags       Tags to add to the metric
     */
    public void recordDuration(String metricName, long duration, Map<String, String> tags) {
        try {
            Attributes attributes = buildAttributes(tags);
            durationHistogram.record(duration, attributes);
            LOGGER.debug("Recorded duration: {} = {}ms with tags: {}", metricName, duration, tags);
        } catch (Exception e) {
            LOGGER.warn("Failed to record duration: {}", metricName, e);
        }
    }

    /**
     * Starts a new span for tracing
     *
     * @param spanName Name of the span
     * @return The created span
     */
    public Span startSpan(String spanName) {
        return startSpan(spanName, Map.of());
    }

    /**
     * Starts a new span with attributes
     *
     * @param spanName   Name of the span
     * @param attributes Attributes to add to the span
     * @return The created span
     */
    public Span startSpan(String spanName, Map<String, String> attributes) {
        try {
            Span span = tracer.spanBuilder(spanName).startSpan();
            
            // Add attributes to span
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                span.setAttribute(entry.getKey(), entry.getValue());
            }
            
            LOGGER.debug("Started span: {} with attributes: {}", spanName, attributes);
            return span;
        } catch (Exception e) {
            LOGGER.warn("Failed to start span: {}", spanName, e);
            return Span.getInvalid();
        }
    }

    /**
     * Ends a span
     *
     * @param span    The span to end
     * @param success Whether the operation was successful
     */
    public void endSpan(Span span, boolean success) {
        try {
            if (span != null && span.isRecording()) {
                span.setAttribute(Constants.TAG_SUCCESS, success);
                span.end();
                LOGGER.debug("Ended span with success: {}", success);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to end span", e);
        }
    }

    /**
     * Records an exception in a span
     *
     * @param span      The span to record the exception in
     * @param exception The exception to record
     */
    public void recordException(Span span, Exception exception) {
        try {
            if (span != null && span.isRecording()) {
                span.recordException(exception);
                LOGGER.debug("Recorded exception in span: {}", exception.getMessage());
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to record exception in span", e);
        }
    }

    /**
     * Builds OpenTelemetry Attributes from a map of tags
     *
     * @param tags Map of tag key-value pairs
     * @return OpenTelemetry Attributes
     */
    private Attributes buildAttributes(Map<String, String> tags) {
        var builder = Attributes.builder();
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            builder.put(AttributeKey.stringKey(entry.getKey()), entry.getValue());
        }
        return builder.build();
    }
}

