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

import com.kristasoft.extensions.ai.nlp.gcp.catalog.validators.Validator;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to create validation maps for different operations.
 * Validation maps define which resources need to be validated for each operation.
 */
public class ValidationMapHelper {

    private ValidationMapHelper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates validation map for Translate To Language operation
     *
     * @param text           The text to translate
     * @param sourceLanguage The source language (can be null for auto-detect)
     * @param targetLanguage The target language
     * @return Validation map
     */
    public static Map<Validator.ValidationResource, String> createTranslateValidationMap(
            String text, String sourceLanguage, String targetLanguage) {
        
        Map<Validator.ValidationResource, String> validationMap = new HashMap<>();
        
        // Text is always required
        validationMap.put(Validator.ValidationResource.TEXT_INPUT, text);
        
        // Target language is always required
        validationMap.put(Validator.ValidationResource.TARGET_LANGUAGE, targetLanguage);
        
        // Source language is optional (null means auto-detect)
        // But if provided, it needs to be validated
        if (sourceLanguage != null && !sourceLanguage.trim().isEmpty()) {
            validationMap.put(Validator.ValidationResource.SOURCE_LANGUAGE, sourceLanguage);
        }
        
        return validationMap;
    }

    /**
     * Creates validation map for Detect Language operation
     *
     * @param text The text whose language to detect
     * @return Validation map
     */
    public static Map<Validator.ValidationResource, String> createDetectLanguageValidationMap(String text) {
        Map<Validator.ValidationResource, String> validationMap = new HashMap<>();
        
        // Only text is required for language detection
        validationMap.put(Validator.ValidationResource.TEXT_INPUT, text);
        
        return validationMap;
    }

}

