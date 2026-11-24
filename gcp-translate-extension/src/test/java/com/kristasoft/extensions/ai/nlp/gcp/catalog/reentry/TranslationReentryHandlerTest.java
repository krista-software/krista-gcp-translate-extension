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

import app.krista.extension.executor.ExtensionResponse;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.errorhandlers.ExtensionResponseGenerator;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.service.TranslationResponseService;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.state.InternalStateManager;
import com.kristasoft.extensions.ai.nlp.gcp.catalog.util.SubCatalogConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TranslationReentryHandlerTest {

    private InternalStateManager stateManager;
    private TranslationResponseService translationResponseService;
    private ExtensionResponseGenerator responseGenerator;
    private TranslationReentryHandler handler;

    private static ExtensionResponse ok() {
        return new ExtensionResponse(ExtensionResponse.Result.SUCCESS, null, null, null, null);
    }

    @BeforeEach
    void setUp() {
        stateManager = mock(InternalStateManager.class);
        translationResponseService = mock(TranslationResponseService.class);
        responseGenerator = mock(ExtensionResponseGenerator.class);
        handler = new TranslationReentryHandler(stateManager, translationResponseService, responseGenerator);
    }

    // ---------- handleConfirmation ----------

    @Test
    void handleConfirmation_noSavedState_returnsEmptyResponse() {
        when(stateManager.getState(anyString())).thenReturn(null);
        Map<String, Object> in = new HashMap<>();
        in.put(SubCatalogConstants.STATE_ID, "s1");
        in.put(SubCatalogConstants.REENTER, Boolean.TRUE);

        ExtensionResponse resp = handler.handleConfirmation(in);

        assertNotNull(resp);
    }

    @Test
    void handleConfirmation_declineReenter_returnsFetchDeny() {
        when(stateManager.getState("s1")).thenReturn(new HashMap<>());
        when(responseGenerator.generateFetchDenyResponse(any(), any(), any(), any())).thenReturn(ok());
        Map<String, Object> in = new HashMap<>();
        in.put(SubCatalogConstants.STATE_ID, "s1");
        in.put(SubCatalogConstants.REENTER, Boolean.FALSE);

        ExtensionResponse resp = handler.handleConfirmation(in);

        assertNotNull(resp);
        verify(responseGenerator).generateFetchDenyResponse(any(), any(), any(), any());
    }

    @Test
    void handleConfirmation_acceptReenter_returnsFetchResponse() {
        when(stateManager.getState("s1")).thenReturn(new HashMap<>());
        when(responseGenerator.generateFetchResponse(any(), any(), any(), any())).thenReturn(ok());
        Map<String, Object> in = new HashMap<>();
        in.put(SubCatalogConstants.STATE_ID, "s1");
        in.put(SubCatalogConstants.REENTER, Boolean.TRUE);
        in.put(SubCatalogConstants.TEXT, "hi");
        in.put(SubCatalogConstants.TARGET_LANGUAGE, "fr");

        ExtensionResponse resp = handler.handleConfirmation(in);

        assertNotNull(resp);
        verify(responseGenerator).generateFetchResponse(any(), any(), any(), any());
    }

    // ---------- handleReentry ----------

    @Test
    void handleReentry_noSavedState_returnsError() {
        when(stateManager.getState(anyString())).thenReturn(null);
        Map<String, Object> in = new HashMap<>();
        in.put(SubCatalogConstants.STATE_ID, "s1");

        ExtensionResponse resp = handler.handleReentry(in);

        assertNotNull(resp);
    }

    @Test
    void handleReentry_withSavedState_callsTranslate() {
        Map<String, Object> saved = new HashMap<>();
        saved.put(SubCatalogConstants.TEXT, "savedText");
        saved.put(SubCatalogConstants.SOURCE_LANGUAGE, "en");
        saved.put(SubCatalogConstants.TARGET_LANGUAGE, "de");
        when(stateManager.getState("s1")).thenReturn(saved);
        when(translationResponseService.translateToLanguage(anyString(), any(), anyString(), eq(true)))
            .thenReturn(ok());

        Map<String, Object> in = new HashMap<>();
        in.put(SubCatalogConstants.STATE_ID, "s1");
        in.put(SubCatalogConstants.TEXT, "reentered"); // overrides saved (inputMap branch)
        // source/target absent -> falls back to saved (savedState branch)

        ExtensionResponse resp = handler.handleReentry(in);

        assertNotNull(resp);
        verify(stateManager).removeState("s1");
        verify(translationResponseService).translateToLanguage(anyString(), any(), anyString(), eq(true));
    }

    // ---------- handleDetectConfirmation ----------

    @Test
    void handleDetectConfirmation_noSavedState_returnsEmpty() {
        when(stateManager.getState(anyString())).thenReturn(null);
        Map<String, Object> in = new HashMap<>();
        in.put(SubCatalogConstants.STATE_ID, "d1");
        in.put(SubCatalogConstants.REENTER, Boolean.TRUE);

        assertNotNull(handler.handleDetectConfirmation(in));
    }

    @Test
    void handleDetectConfirmation_declineAndAccept() {
        when(stateManager.getState("d1")).thenReturn(new HashMap<>());
        when(responseGenerator.generateFetchDenyResponse(any(), any(), any(), any())).thenReturn(ok());
        when(responseGenerator.generateFetchResponse(any(), any(), any(), any())).thenReturn(ok());

        Map<String, Object> decline = new HashMap<>();
        decline.put(SubCatalogConstants.STATE_ID, "d1");
        decline.put(SubCatalogConstants.REENTER, Boolean.FALSE);
        assertNotNull(handler.handleDetectConfirmation(decline));

        Map<String, Object> accept = new HashMap<>();
        accept.put(SubCatalogConstants.STATE_ID, "d1");
        accept.put(SubCatalogConstants.REENTER, Boolean.TRUE);
        accept.put(SubCatalogConstants.TEXT, "text");
        assertNotNull(handler.handleDetectConfirmation(accept));
    }

    // ---------- handleDetectReentry ----------

    @Test
    void handleDetectReentry_noSavedState_returnsError() {
        when(stateManager.getState(anyString())).thenReturn(null);
        Map<String, Object> in = new HashMap<>();
        in.put(SubCatalogConstants.STATE_ID, "d1");

        assertNotNull(handler.handleDetectReentry(in));
    }

    @Test
    void handleDetectReentry_withSavedState_callsDetect() {
        Map<String, Object> saved = new HashMap<>();
        saved.put(SubCatalogConstants.TEXT, "savedText");
        when(stateManager.getState("d1")).thenReturn(saved);
        when(translationResponseService.detectLanguage(anyString(), eq(true))).thenReturn(ok());

        Map<String, Object> in = new HashMap<>();
        in.put(SubCatalogConstants.STATE_ID, "d1"); // text absent -> from saved

        assertNotNull(handler.handleDetectReentry(in));
        verify(stateManager).removeState("d1");
        verify(translationResponseService).detectLanguage(anyString(), eq(true));
    }
}
