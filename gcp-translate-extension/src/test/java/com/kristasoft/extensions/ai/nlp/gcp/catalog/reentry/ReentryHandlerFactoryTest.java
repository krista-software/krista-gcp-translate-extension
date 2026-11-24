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

package com.kristasoft.extensions.ai.nlp.gcp.catalog.reentry;

import com.kristasoft.extensions.ai.nlp.gcp.catalog.errorhandlers.ExtensionResponseGenerator;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.service.TranslationResponseService;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.state.InternalStateManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class ReentryHandlerFactoryTest {

    @Test
    void getTranslationReentryHandler_returnsHandler() {
        ReentryHandlerFactory factory = new ReentryHandlerFactory(
            mock(InternalStateManager.class),
            mock(TranslationResponseService.class),
            mock(ExtensionResponseGenerator.class));

        TranslationReentryHandler handler = factory.getTranslationReentryHandler();

        assertNotNull(handler);
    }
}
