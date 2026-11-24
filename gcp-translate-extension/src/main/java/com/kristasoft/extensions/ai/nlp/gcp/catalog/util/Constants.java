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
 * Application-wide constants for the GCP Translate Extension.
 * Contains error messages, validation messages, limits, and other constant values.
 */
public class Constants {

    // ==================== Credential Validation ====================
    public static final String PLEASE_ENTER_VALID_CREDENTIALS = "We couldn't verify your credentials. Please check your Google Cloud service account JSON file and try again.";

    public static final String ENABLE_ERROR_HANDLING = "Allow Retry";

    // Input Validation Errors
    public static final String TEXT_REQUIRED = "Please enter some text to translate or detect. The text field cannot be empty.";
    public static final String TEXT_TOO_LONG = "Your text is too long (%,d characters). Please shorten it to %,d characters or less, or split it into multiple requests.";
    public static final String LANGUAGE_REQUIRED = "Please select a %s. This field is required to proceed.";
    public static final String LANGUAGE_INVALID = "We don't recognize '%s' as a supported language. Please use the 'Get Supported Languages' request to see all available options, or try entering a standard language code like 'en' for English or 'es' for Spanish.";
    public static final String LANGUAGE_SAME = "Your source and target languages are the same. Please choose a different target language to translate your text.";

    // System Errors
    public static final String SESSION_EXPIRED = "Your session has timed out. Please refresh the page and try your request again.";

    // Re-entry Messages
    public static final String FETCH_DENY_PREFIX = "We still need the following information: ";
    public static final String FETCH_DENY_SUFFIX = ". Please provide these values to continue.";
    public static final String FETCH_DENY_SEPARATOR = ", ";

    // Limits
    public static final int MAX_TEXT_LENGTH = 30000; // GCP Translation API limit
    public static final int MAX_STACK_TRACE_ELEMENTS = 5; // Number of stack trace elements to include in error responses

    // Telemetry
    public static final String INSTRUMENTATION_NAME = "gcp-translate-extension";
    public static final String METRIC_REQUESTS = "gcp.translate.requests";
    public static final String METRIC_DURATION = "gcp.translate.duration";
    public static final String METRIC_REQUESTS_DESC = "Total number of translation requests";
    public static final String METRIC_DURATION_DESC = "Duration of translation operations";
    public static final String METRIC_UNIT_COUNT = "1";
    public static final String METRIC_UNIT_MS = "ms";

    // Telemetry metric names for operations
    public static final String METRIC_TRANSLATE_TO_LANGUAGE = "gcp.translate.translateToLanguage";
    public static final String METRIC_DETECT_LANGUAGE = "gcp.translate.detectLanguage";
    public static final String METRIC_GET_SUPPORTED_LANGUAGES = "gcp.translate.getSupportedLanguages";
    public static final String METRIC_GET_SUPPORTED_LANGUAGES_SUCCESS = "gcp.translate.getSupportedLanguages.success";
    public static final String METRIC_GET_SUPPORTED_LANGUAGES_ERROR = "gcp.translate.getSupportedLanguages.error";

    // Telemetry Tags
    public static final String TAG_OPERATION = "operation";
    public static final String TAG_TARGET_LANGUAGE = "target_language";
    public static final String TAG_SOURCE_LANGUAGE = "source_language";
    public static final String TAG_TEXT_LENGTH_RANGE = "text_length_range";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_VALUE_UNKNOWN = "unknown";
    public static final String TAG_VALUE_AUTO_DETECT = "auto-detect";
    public static final String TAG_VALUE_TRANSLATE = "translate";
    public static final String TAG_VALUE_DETECT_LANGUAGE = "detect_language";

    // Text Length Ranges
    public static final String TEXT_LENGTH_RANGE_0_100 = "0-100";
    public static final String TEXT_LENGTH_RANGE_101_1000 = "101-1000";
    public static final String TEXT_LENGTH_RANGE_1001_10000 = "1001-10000";
    public static final String TEXT_LENGTH_RANGE_10001_PLUS = "10001+";
    public static final int TEXT_LENGTH_THRESHOLD_100 = 100;
    public static final int TEXT_LENGTH_THRESHOLD_1000 = 1000;
    public static final int TEXT_LENGTH_THRESHOLD_10000 = 10000;

    // Field Type Logical Names
    public static final String FIELD_TYPE_PREFIX = "com.krista.fields.";
    public static final String FIELD_TYPE_SWITCH = "com.krista.fields.Switch";
    public static final String FIELD_TYPE_TEXT = "com.krista.fields.Text";
    public static final String FIELD_TYPE_PARAGRAPH = "com.krista.fields.Paragraph";

    // Response Field Names
    public static final String RESPONSE_FIELD_TRANSLATED_TEXT = "Translated Text";
    public static final String RESPONSE_FIELD_DETECTED_LANGUAGE = "Detected Language";

    //  Operation Names
    public static final String OPERATION_TRANSLATE_TO_LANGUAGE = "translateToLanguage";
    public static final String OPERATION_DETECT_LANGUAGE = "detectLanguage";

    // GCP API Constants
    public static final String GCP_LOCATION_GLOBAL = "global";
    public static final String GCP_MIME_TYPE_TEXT_PLAIN = "text/plain";
    public static final String GCP_DEFAULT_LANGUAGE = "English";
    public static final String GCP_NULL_TEXT_PLACEHOLDER = "null";
    public static final String GCP_PROJECT_ID_KEY = "\"project_id\": ";
    public static final String GCP_PROJECT_ID_DELIMITER = ",";
    public static final String GCP_QUOTE = "\"";

    // Telemetry Attribute Keys
    public static final String TELEMETRY_ATTR_OPERATION = "operation";
    public static final String TELEMETRY_ATTR_VALIDATION_FAILED = "validation_failed";
    public static final String TELEMETRY_ATTR_VALIDATION_ERRORS = "validation_errors";
    public static final String TELEMETRY_ATTR_SUCCESS = "success";
    public static final String TELEMETRY_ATTR_DURATION_MS = "duration_ms";
    public static final String TELEMETRY_ATTR_ERROR = "error";
    public static final String TELEMETRY_ATTR_ERROR_MESSAGE = "error_message";

    // Telemetry Metric Suffixes
    public static final String METRIC_SUFFIX_VALIDATION_FAILED = ".validation_failed";
    public static final String METRIC_SUFFIX_DURATION = ".duration";
    public static final String METRIC_SUFFIX_SUCCESS = ".success";
    public static final String METRIC_SUFFIX_ERROR = ".error";

    // Error Messages (User-Facing)
    public static final String ERROR_UNEXPECTED = "Something unexpected happened while processing your request. This might be a temporary issue. Would you like to try again?";
    public static final String ERROR_INVALID_ARGUMENT = "The language you entered isn't supported or the format is incorrect. Please use the 'Get Supported Languages' request to find valid language codes (like 'en', 'es', 'fr'), then try again. Would you like to retry?";
    public static final String ERROR_AUTHENTICATION_FAILED = "We couldn't authenticate with Google Cloud. Please verify that:\n• Your service account JSON file is correct\n• The service account hasn't been deleted or disabled\n• The credentials haven't expired\n\nWould you like to try again after checking these?";
    public static final String ERROR_SERVICE_UNAVAILABLE = "Google Cloud Translation service is currently unavailable. This is usually temporary. Please wait a few minutes and try again. You can check Google Cloud's status at https://status.cloud.google.com. Would you like to retry now?";
    public static final String ERROR_QUOTA_EXCEEDED = "You've reached your Google Cloud Translation API quota limit. To resolve this:\n• Check your quota usage in Google Cloud Console\n• Request a quota increase if needed\n• Wait until your quota resets (usually monthly)\n\nWould you like to try again?";
    public static final String ERROR_RESOURCE_NOT_FOUND = "We couldn't find the requested resource in your Google Cloud project. Please verify:\n• Your project ID is correct in the service account credentials\n• The Translation API is enabled for your project\n• You're using the right Google Cloud project\n\nWould you like to try again?";
    public static final String ERROR_WITH_MESSAGE_PREFIX = "We encountered an issue: ";
    public static final String ERROR_WITH_MESSAGE_SUFFIX = "\n\nWould you like to try again?";

    //  GCP Error Code Keywords
    public static final String GCP_ERROR_INVALID_ARGUMENT = "INVALID_ARGUMENT";
    public static final String GCP_ERROR_UNAUTHENTICATED = "UNAUTHENTICATED";
    public static final String GCP_ERROR_PERMISSION_DENIED = "PERMISSION_DENIED";
    public static final String GCP_ERROR_UNAVAILABLE = "UNAVAILABLE";
    public static final String GCP_ERROR_DEADLINE_EXCEEDED = "DEADLINE_EXCEEDED";
    public static final String GCP_ERROR_RESOURCE_EXHAUSTED = "RESOURCE_EXHAUSTED";
    public static final String GCP_ERROR_NOT_FOUND = "NOT_FOUND";
    public static final String GCP_ERROR_CREDENTIALS = "credentials";

    /**
     * Returns a safe string representation of a value for logging.
     * Returns "null" if the value is null.
     */
    public static String safeString(String value) {
        return value != null ? value : "null";
    }

    private Constants() {
        // Private constructor to prevent instantiation
    }
}
