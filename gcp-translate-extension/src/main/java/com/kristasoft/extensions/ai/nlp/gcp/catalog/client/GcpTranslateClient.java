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

package com.kristasoft.extensions.ai.nlp.gcp.catalog.client;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.v3.*;
import com.kristasoft.extensions.ai.nlp.gcp.GcpTranslateExtensionSettings;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.Constants;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GcpTranslateClient {

    private static final Logger log = LoggerFactory.getLogger(GcpTranslateClient.class);
    private List<SupportedLanguage> supportedLanguages;
    private TranslationServiceClient client;
    private String projectId;
    private final GcpTranslateExtensionSettings settings;

    @Inject
    public GcpTranslateClient(GcpTranslateExtensionSettings settings) {
        this.settings = settings;
    }

    /**
     * Constructor for direct initialization with credentials file.
     * Used for validation and testing.
     */
    public GcpTranslateClient(File jsonCredentials) throws IOException {
        this.settings = null;
        this.client = TranslationServiceClient.create(setCredentials(jsonCredentials));
        this.projectId = getProjectId(new FileInputStream(jsonCredentials));
        loadSupportedLanguages();
        log.info("GcpTranslateClient initialized successfully with direct credentials");
    }

    /**
     * Initialize the client with credentials.
     * This is called lazily when the client is first used.
     */
    private void ensureInitialized() {
        if (client == null) {
            try {
                if (settings == null) {
                    throw new IllegalStateException("GcpTranslateClient not properly initialized");
                }
                File jsonCredentials = settings.getServiceAccountJson();
                if (jsonCredentials == null) {
                    throw new IllegalStateException("Service account credentials not configured");
                }
                this.client = TranslationServiceClient.create(setCredentials(jsonCredentials));
                this.projectId = getProjectId(new FileInputStream(jsonCredentials));
                // Load supported languages on first initialization
                loadSupportedLanguages();
                log.info("GcpTranslateClient initialized successfully");
            } catch (IOException e) {
                log.error("Failed to initialize GcpTranslateClient: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to initialize GCP Translate Client", e);
            }
        }
    }

    /**
     * Internal method to load supported languages during initialization
     */
    private void loadSupportedLanguages() {
        LocationName parent = LocationName.of(this.projectId, Constants.GCP_LOCATION_GLOBAL);
        SupportedLanguages localSupportedLanguages = this.client.getSupportedLanguages(GetSupportedLanguagesRequest.newBuilder()
                .setParent(parent.toString())
                .setDisplayLanguageCode("en")
                .build());
        this.supportedLanguages = localSupportedLanguages.getLanguagesList();
    }

    private TranslationServiceSettings setCredentials(File serviceAccountCredentials) throws IOException {
        return (TranslationServiceSettings.newBuilder().setCredentialsProvider(() -> GoogleCredentials.fromStream(new FileInputStream(serviceAccountCredentials)).createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"))))
                .build();
    }

    public String translateText(String sourceLanguage, String targetLanguage, String text) {
        ensureInitialized();
        text = (text == null || text.isBlank()) ? Constants.GCP_NULL_TEXT_PLACEHOLDER : text;
        StringBuilder translatedText = new StringBuilder();
        String targetLanguageCode = getLanguageCodeByDisplayName(targetLanguage);
        log.info("Target Lang Code: {}", targetLanguageCode);
        String sourceLanguageCode = getLanguageCodeByDisplayName(sourceLanguage);
        log.info("Translating: {} To {}", text, targetLanguage);
        LocationName parent = LocationName.of(this.projectId, Constants.GCP_LOCATION_GLOBAL);
        log.info("Preparing Requests...");
        TranslateTextResponse response = this.client.translateText(TranslateTextRequest.newBuilder()
                .setParent(parent.toString())
                .setMimeType(Constants.GCP_MIME_TYPE_TEXT_PLAIN)
                .setSourceLanguageCode(sourceLanguageCode)
                .setTargetLanguageCode(targetLanguageCode)
                .addContents(text)
                .build());
        for (Translation translation : response.getTranslationsList()) {
            translatedText.append(translation.getTranslatedText());
            log.info("Translated text: {}", translatedText);
        }
        return translatedText.toString();
    }

    public List<String> getSupportedLanguages() {
        ensureInitialized();
        LocationName parent = LocationName.of(this.projectId, Constants.GCP_LOCATION_GLOBAL);
        SupportedLanguages localSupportedLanguages = this.client.getSupportedLanguages(GetSupportedLanguagesRequest.newBuilder()
                .setParent(parent.toString())
                .setDisplayLanguageCode("en")
                .build());
        List<String> languages = new ArrayList<>();
        this.supportedLanguages = localSupportedLanguages.getLanguagesList();
        for (SupportedLanguage language : this.supportedLanguages)
            languages.add(language.getDisplayName());  // Return display names (e.g., "Hindi") for user-friendly display
        return languages;
    }

    public String detect(String text) {
        ensureInitialized();
        text = (text == null || text.isBlank()) ? Constants.GCP_NULL_TEXT_PLACEHOLDER : text;
        LocationName parent = LocationName.of(this.projectId, Constants.GCP_LOCATION_GLOBAL);
        DetectLanguageResponse response = this.client.detectLanguage(DetectLanguageRequest.newBuilder()
                .setParent(parent.toString())
                .setMimeType(Constants.GCP_MIME_TYPE_TEXT_PLAIN)
                .setContent(text)
                .build());
        return getDisplayNameByLanguageCode(response.getLanguages(0).getLanguageCode());
    }

    public String getProjectId(FileInputStream json) {
        return getFileContent(json).split(Constants.GCP_PROJECT_ID_KEY)[1]
                .split(Constants.GCP_PROJECT_ID_DELIMITER)[0]
                .replace(Constants.GCP_QUOTE, "");
    }

    private static String getFileContent(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.info("Error reading input stream", e);
        }
        return sb.toString();
    }

    private String getDisplayNameByLanguageCode(String code) {
        List<String> languageCodes = getLanguageCodes();
        if (languageCodes.contains(code)) {
            int index = languageCodes.indexOf(code);
            return (this.supportedLanguages.get(index)).getDisplayName();
        }
        return Constants.GCP_DEFAULT_LANGUAGE;
    }

    private String getLanguageCodeByDisplayName(String language) {
        List<String> languagesDisplayName = getLanguageNames();
        if (languagesDisplayName.contains(language)) {
            int index = languagesDisplayName.indexOf(language);
            return (this.supportedLanguages.get(index)).getLanguageCode();
        }
        return "";
    }

    private List<String> getLanguageNames() {
        List<String> languageDisplayName = new ArrayList<>();
        for (SupportedLanguage language : this.supportedLanguages) {
            languageDisplayName.add(language.getDisplayName());
        }
        return languageDisplayName;
    }

    private List<String> getLanguageCodes() {
        List<String> languageDisplayName = new ArrayList<>();
        for (SupportedLanguage language : this.supportedLanguages) {
            languageDisplayName.add(language.getLanguageCode());
        }
        return languageDisplayName;
    }
}
