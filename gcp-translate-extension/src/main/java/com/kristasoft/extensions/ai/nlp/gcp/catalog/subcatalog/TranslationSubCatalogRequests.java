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

package com.kristasoft.extensions.ai.nlp.gcp.catalog.subcatalog;

import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Field;
import app.krista.extension.impl.anno.SubCatalogRequest;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.reentry.ReentryHandlerFactory;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.reentry.TranslationReentryHandler;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.SubCatalogConstants;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

/**
 * Sub-catalog requests for handling re-entry flows in translation operations.
 * These are triggered when validation fails and user needs to re-entry values.
 */
@Service
public class TranslationSubCatalogRequests {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationSubCatalogRequests.class);

    private final ReentryHandlerFactory reentryHandlerFactory;

    @Inject
    public TranslationSubCatalogRequests(ReentryHandlerFactory reentryHandlerFactory) {
        this.reentryHandlerFactory = reentryHandlerFactory;
    }

    // ==================== Translate To Language Re-entry ====================

    @SubCatalogRequest(name = SubCatalogConstants.CONFIRM_REENTER_TRANSLATE,
            type = CatalogRequest.Type.QUERY_SYSTEM,
            description = "Checks if user wants to re-enter translation data and if yes, sends prompt to do so")
    @SuppressWarnings("unchecked")
    public ExtensionResponse confirmReenterTranslateToLanguage(
            @Field.Desc(name = "inputMap", type = "{ REENTER : Boolean, State Id : Text, Text: Paragraph, " +
                    "Source Language: Text, Target Language: Text }") Map<String, Object> map) {

        LOGGER.info("SubCatalogRequest confirmReenterTranslateToLanguage start: {}", map);

        TranslationReentryHandler handler = reentryHandlerFactory.getTranslationReentryHandler();
        ExtensionResponse response = handler.handleConfirmation(map);

        return response;
    }

    @SubCatalogRequest(name = SubCatalogConstants.HANDLE_REENTER_TRANSLATE,
            type = CatalogRequest.Type.QUERY_SYSTEM,
            description = "Handle re-enter translation data")
    @Field(name = "Translated Text", type = "Paragraph", required = false,
            attributes = {@Attribute(name = "visualWidth", value = "L")})
    @SuppressWarnings("unchecked")
    public ExtensionResponse handleReenterTranslateToLanguage(
            @Field.Desc(name = "inputMap", type = "{State Id: Text, Text: Paragraph, " +
                    "Source Language: Text, Target Language: Text }") Map<String, Object> map) {

        LOGGER.info("SubCatalogRequest handleReenterTranslateToLanguage start: {}", map);

        TranslationReentryHandler handler = reentryHandlerFactory.getTranslationReentryHandler();
        return handler.handleReentry(map);
    }

    // ==================== Detect Language Re-entry ====================

    @SubCatalogRequest(name = SubCatalogConstants.CONFIRM_REENTER_DETECT,
            type = CatalogRequest.Type.QUERY_SYSTEM,
            description = "Checks if user wants to re-enter text for language detection and if yes, sends prompt to do so")
    @SuppressWarnings("unchecked")
    public ExtensionResponse confirmReenterDetectLanguage(
            @Field.Desc(name = "inputMap", type = "{ REENTER : Boolean, State Id : Text, Text: Paragraph }") 
            Map<String, Object> map) {

        LOGGER.info("SubCatalogRequest confirmReenterDetectLanguage start: {}", map);

        TranslationReentryHandler handler = reentryHandlerFactory.getTranslationReentryHandler();
        return handler.handleDetectConfirmation(map);
    }

    @SubCatalogRequest(name = SubCatalogConstants.HANDLE_REENTER_DETECT,
            type = CatalogRequest.Type.QUERY_SYSTEM,
            description = "Handle re-enter text for language detection")
    @Field(name = "Detected Language", type = "Paragraph", required = false,
            attributes = {@Attribute(name = "visualWidth", value = "L")})
    @SuppressWarnings("unchecked")
    public ExtensionResponse handleReenterDetectLanguage(
            @Field.Desc(name = "inputMap", type = "{State Id: Text, Text: Paragraph }") Map<String, Object> map) {

        LOGGER.info("SubCatalogRequest handleReenterDetectLanguage start: {}", map);

        TranslationReentryHandler handler = reentryHandlerFactory.getTranslationReentryHandler();
        return handler.handleDetectReentry(map);
    }
}

