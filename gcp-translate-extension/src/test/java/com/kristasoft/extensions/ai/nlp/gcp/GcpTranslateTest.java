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

import com.kristasoft.extensions.ai.nlp.gcp.catalog.client.GcpTranslateClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Set this env variable for this test to work: GOOGLE_APPLICATION_CREDENTIALS="/[path-to]/service-account-file.json"
 * E.g.: GOOGLE_APPLICATION_CREDENTIALS=/Users/looth/projects/krista/pps/lb-sandbox/gcp/ai-prototypes-385013-f037d56d024b.json
 */
public class GcpTranslateTest {
    private static final Logger log = LoggerFactory.getLogger(GcpTranslateTest.class);


    GcpTranslateClient translateClient;
    String SERVICE_ACCOUNT_JSON_FILENAME = "Cred_B.json";
    File credentials;


    @Test
    @Disabled("Integration test — calls the live Google Translate API (needs valid GCP service-account credentials + quota). Run manually, not in CI.")
    public void translate() throws URISyntaxException, IOException {
        credentials = new File(Objects.requireNonNull(getClass().getResource("/" + SERVICE_ACCOUNT_JSON_FILENAME)).toURI());
        log.info("..." + credentials.toURI());

        this.translateClient = new GcpTranslateClient(credentials);

        String translation = translateClient.translateText("", "French", "hello how are you");
        log.debug("translation: " + translation);
        assertEquals("bonjour comment allez-vous", translation, "bonjour comment allez-vous");
    }

    @Test
    @Disabled("Integration test — calls the live Google Translate API (needs valid GCP service-account credentials + quota). Run manually, not in CI.")
    public void detect() throws URISyntaxException, IOException {
        credentials = new File(Objects.requireNonNull(getClass().getResource("/" + SERVICE_ACCOUNT_JSON_FILENAME)).toURI());
        log.info("..." + credentials.toURI());

        this.translateClient = new GcpTranslateClient(credentials);

        String translation = translateClient.detect("bonjour comment allez-vous");
        log.debug("Detected: " + translation);
        assertEquals("French", translation);
    }
}