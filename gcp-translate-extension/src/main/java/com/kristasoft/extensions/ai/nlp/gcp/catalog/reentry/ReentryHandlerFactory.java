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
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

/**
 * Factory for creating reentry handlers.
 * Provides centralized creation of handlers with proper dependency injection.
 */
@Service
public class ReentryHandlerFactory {

    private final InternalStateManager stateManager;
    private final TranslationResponseService translationResponseService;
    private final ExtensionResponseGenerator responseGenerator;

    @Inject
    public ReentryHandlerFactory(InternalStateManager stateManager,
                                TranslationResponseService translationResponseService,
                                ExtensionResponseGenerator responseGenerator) {
        this.stateManager = stateManager;
        this.translationResponseService = translationResponseService;
        this.responseGenerator = responseGenerator;
    }

    /**
     * Creates a TranslationReentryHandler instance
     *
     * @return TranslationReentryHandler with injected dependencies
     */
    public TranslationReentryHandler getTranslationReentryHandler() {
        return new TranslationReentryHandler(stateManager, translationResponseService, responseGenerator);
    }
}

