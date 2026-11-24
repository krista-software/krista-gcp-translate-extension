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

import app.krista.extension.executor.Invoker;
import app.krista.model.field.util.GsonJsonMapper;
import com.google.gson.reflect.TypeToken;
import com.kristasoft.extensions.ai.nlp.gcp.util.media.JavaFileHandleHelper;
import jdk.jfr.Name;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

@Service
public class GcpTranslateExtensionSettings {

    public static final String SERVICE_ACCOUNT_JSON = "Service Account Credentials";
    private static final Logger logger = LoggerFactory.getLogger(GcpTranslateExtensionSettings.class);
    public static final String URL = "URL";
    private Map<String, Object> attributes;
    private File serviceAccountJson;
    private String baseUrl = "";

    @Inject
    private JavaFileHandleHelper javaFileHandleHelper;

    @Inject
    @Name("self")
    Invoker self;

    @Inject
    public GcpTranslateExtensionSettings(Invoker invoker) {
        this.attributes = invoker.getAttributes();
    }

    public GcpTranslateExtensionSettings(File serviceAccountJson) {
        this.serviceAccountJson = serviceAccountJson;
    }

    public String getBaseUrl() {
        if (this.self != null && this.self.getAttributes().containsKey(URL) && this.self.getAttributes().get(URL) != null) {
            return this.self.getAttributes().get(URL).toString();
        } else {
            logger.warn("Could not find required GCP URL in invoker attributes.");
            return baseUrl;
        }
    }

    public File getServiceAccountJson() {
        return serviceAccountJson;
    }

    public void setAttributes(Map<String, Object> attributes) throws IOException {
        this.attributes = attributes;
        setServiceAccountJson(attributes);
    }

    public void setServiceAccountJson(Map<String, Object> attributes) throws IOException {
        Type fileType = TypeToken.get(app.krista.model.base.File.class).getType();
        app.krista.model.base.File kristaFile = GsonJsonMapper.getInstance().fromString(GsonJsonMapper.getInstance()
                .toString(attributes.getOrDefault(SERVICE_ACCOUNT_JSON, null)), fileType);
        logger.info("File found successfully in attributes: [{}, {}, {}, {}]", kristaFile.getFileName(), kristaFile.getExtension(),
                kristaFile.getMediaId(), kristaFile.getLength());
        this.serviceAccountJson = javaFileHandleHelper.convertFile(kristaFile);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setServiceAccountJsonFromInvoker() throws IOException {
        Type fileType = TypeToken.get(app.krista.model.base.File.class).getType();
        app.krista.model.base.File kristaFile = GsonJsonMapper.getInstance().fromString(GsonJsonMapper.getInstance()
                .toString(self.getAttributes().get(SERVICE_ACCOUNT_JSON)), fileType);
        this.serviceAccountJson = javaFileHandleHelper.convertFile(kristaFile);
    }
}
