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

package com.kristasoft.extensions.ai.nlp.gcp.catalog;

import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.errorhandlers.ExtensionResponseGenerator;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.service.TranslationOperationsFacade;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static java.util.Collections.singletonMap;

@Domain(id = "catEntryDomain_8a8d7d63-5223-450f-95c2-649d2ff36cf5",
        name = "NLP",
        ecosystemId = "catEntryEcosystem_2141418b-7dd8-4c04-865e-8763ba9aadcf",
        ecosystemName = "AI",
        ecosystemVersion = "a1ad12d7-d937-4d2d-b24b-1d8eaf7ed38e")
public class TranslationArea {

    private final TranslationOperationsFacade translationOperationsFacade;
    private final ExtensionResponseGenerator responseGenerator;

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationArea.class);

    @Inject
    public TranslationArea(
            TranslationOperationsFacade translationOperationsFacade,
            ExtensionResponseGenerator responseGenerator) {
        this.translationOperationsFacade = translationOperationsFacade;
        this.responseGenerator = responseGenerator;
    }

    @CatalogRequest(
            id = "localDomainRequest_18481381-b8d1-4099-93e5-e12038a14aef",
            name = "Detect Language",
            description = "Automatically detects the language of the provided text using Google Cloud's machine learning model.",
            area = "Translation",
            type = CatalogRequest.Type.QUERY_SYSTEM, tool = true)
    @Field(name = "Detected Language", type = "Paragraph", required = false,
            attributes = {@Attribute(name = "visualWidth", value = "L")})
    public ExtensionResponse detectLanguage(
            @Field(name = "Text",
                    type = "Paragraph",
                    attributes = {
                            @Attribute(name = "visualWidth", value = "L"),
                            @Attribute(name = "toolTip", value = "'Enter the text in any language. The system will identify which language it is written in. Maximum 30,000 characters.'")
                    }
            ) String text,
            @Field.Boolean(name = Constants.ENABLE_ERROR_HANDLING, required = false,
                    attributes = {
                            @Attribute(name = "toolTip", value = "'Enable to allow re-entry of values if validation fails'")
                    }
            ) Boolean allowRetry) {

        LOGGER.info("CatalogRequest detectLanguage start with text length: {}, allowRetry: {}",
                Constants.safeString(text != null ? String.valueOf(text.length()) : "null"),
                allowRetry);

        return translationOperationsFacade.detectLanguage(text, allowRetry);
    }

    @CatalogRequest(
            id = "localDomainRequest_83751c02-6d3f-48df-9dda-adf4fa1729cd",
            name = "Get Supported Languages",
            description = "Retrieves the complete list of languages supported by Google Cloud Translation API.",
            area = "Translation",
            type = CatalogRequest.Type.QUERY_SYSTEM, tool = true)
    @Field.Desc(name = "Supported Languages", type = "[ Text ]", required = false)
    public ExtensionResponse getSupportedLanguages() {

        LOGGER.info("CatalogRequest getSupportedLanguages start");

        List<String> languages = translationOperationsFacade.getSupportedLanguages();
        return responseGenerator.generateSuccessResponse(singletonMap("Supported Languages", languages)
        );
    }

    @CatalogRequest(
            id = "localDomainRequest_29776275-d6c1-46cf-9dda-adf4fa1729cd",
            name = "Translate To Language",
            description = "Translates text from one language to another using Google Cloud's Neural Machine Translation model.",
            area = "Translation",
            type = CatalogRequest.Type.QUERY_SYSTEM, tool = true)
    @Field(name = "Translated Text", type = "Paragraph", required = false,
            attributes = {@Attribute(name = "visualWidth", value = "L")})
    public ExtensionResponse translateToLanguage(
            @Field(name = "Text",
                    type = "Paragraph",
                    attributes = {
                            @Attribute(name = "visualWidth", value = "L"),
                            @Attribute(name = "toolTip", value = "'Enter the text you want to translate. The text can be in any supported language. Maximum 30,000 characters.'")
                    }
            ) String text,
            @Field.Text(name = "Source Language",
                    required = false,
                    attributes = {
                            @Attribute(name = "visualWidth", value = "S"),
                            @Attribute(name = "toolTip", value = "'Select the language of your input text, or leave empty for automatic detection. Use \\'Get Supported Languages\\' to see all options.'")
                    }
            ) String sourceLanguage,
            @Field.Text(name = "Target Language",
                    attributes = {
                            @Attribute(name = "visualWidth", value = "S"),
                            @Attribute(name = "toolTip", value = "'Select the language you want to translate to. Use \\'Get Supported Languages\\' to see all available options.'")
                    }
            ) String targetLanguage,
            @Field.Boolean(name = Constants.ENABLE_ERROR_HANDLING, required = false,
                    attributes = {
                            @Attribute(name = "toolTip", value = "'Enable to allow re-entry of values if validation fails'")
                    }
            ) Boolean allowRetry) {

        LOGGER.info("CatalogRequest translateToLanguage start with text length: {}, sourceLanguage: {}, targetLanguage: {}, allowRetry: {}",
                Constants.safeString(text != null ? String.valueOf(text.length()) : "null"),
                Constants.safeString(sourceLanguage),
                Constants.safeString(targetLanguage),
                allowRetry);

        return translationOperationsFacade.translateToLanguage(text, sourceLanguage, targetLanguage, allowRetry);
    }
}