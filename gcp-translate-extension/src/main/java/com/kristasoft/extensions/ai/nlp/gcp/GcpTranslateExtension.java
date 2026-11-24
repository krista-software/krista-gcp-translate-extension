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

package com.kristasoft.extensions.ai.nlp.gcp;

import app.krista.extension.impl.anno.ChangeLog;
import app.krista.extension.impl.anno.Extension;
import app.krista.extension.impl.anno.Field.File;
import app.krista.extension.impl.anno.InvokerRequest;
import app.krista.extension.impl.anno.Java;
import app.krista.extension.impl.anno.StaticResource;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.client.GcpTranslateClient;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Java(version = Java.Version.JAVA_21)
@File(name = "Service Account Credentials")
@Extension(version = "2.0.6", name = "Google Translation")
@StaticResource(path = "docs", file = "documentation")
@ChangeLog(file = "documentation/pages/ReleaseNotes.md")
public class GcpTranslateExtension {

    private static final Logger logger = LoggerFactory.getLogger(GcpTranslateExtension.class);

    private final GcpTranslateExtensionSettings gcpSettings;

    @Inject
    public GcpTranslateExtension(GcpTranslateExtensionSettings attributes) {
        this.gcpSettings = attributes;
    }

    @InvokerRequest(InvokerRequest.Type.INVOKER_LOADED)
    public void loadedInvoker() throws IOException {
        logger.info("Invoker loaded...");
        this.gcpSettings.setServiceAccountJsonFromInvoker();
    }

    @InvokerRequest(InvokerRequest.Type.CUSTOM_TABS)
    public Map<String, String> customTabs() {
        return Map.of("Documentation", "static/docs");
    }

    @InvokerRequest(InvokerRequest.Type.VALIDATE_ATTRIBUTES)
    public void validateAttributes(Map<String, Object> attributes) throws IOException {
        try {
            checkValidCredentials(attributes);
        } catch (Exception cause) {
            throw new IllegalArgumentException(cause);
        }
        this.gcpSettings.setAttributes(attributes);
    }

    @InvokerRequest(InvokerRequest.Type.TEST_CONNECTION)
    public void testConnection() throws IOException {
        validateAttributes(this.gcpSettings.getAttributes());
    }

    private void checkValidCredentials(Map<String, Object> attributes) {
        try {
            gcpSettings.setServiceAccountJson(attributes);
            GcpTranslateClient gcpTranslateClient = new GcpTranslateClient(gcpSettings.getServiceAccountJson());
            List<String> supportedLanguagelist = gcpTranslateClient.getSupportedLanguages();
            if (supportedLanguagelist.isEmpty()) {
                logger.error("Unable to validate credential because supported language is empty..");
                throw new IllegalArgumentException(Constants.PLEASE_ENTER_VALID_CREDENTIALS);
            }
        } catch (Exception cause) {
            logger.error("Unable to validate credential - {}",  cause.getMessage());
            throw new IllegalArgumentException(Constants.PLEASE_ENTER_VALID_CREDENTIALS);
        }
    }

}
