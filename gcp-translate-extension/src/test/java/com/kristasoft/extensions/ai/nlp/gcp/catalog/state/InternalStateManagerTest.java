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

package com.kristasoft.extensions.ai.nlp.gcp.catalog.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InternalStateManagerTest {

    private InternalStateManager manager;

    @BeforeEach
    void setUp() {
        manager = new InternalStateManager();
    }

    @Test
    void generateStateId_returnsUniqueNonNullIds() {
        String a = manager.generateStateId();
        String b = manager.generateStateId();
        assertNotNull(a);
        assertNotNull(b);
        assertNotEquals(a, b);
    }

    @Test
    void saveAndGetState_roundTrips() {
        Map<String, Object> state = new HashMap<>();
        state.put("text", "hello");
        state.put("target", "fr");
        String id = manager.generateStateId();

        manager.saveState(id, state);
        Map<String, Object> loaded = manager.getState(id);

        assertNotNull(loaded);
        assertEquals("hello", loaded.get("text"));
        assertEquals("fr", loaded.get("target"));
    }

    @Test
    void getState_returnsNullWhenMissing() {
        assertNull(manager.getState("does-not-exist"));
    }

    @Test
    void removeState_removesExisting() {
        String id = manager.generateStateId();
        manager.saveState(id, Map.of("k", "v"));
        manager.removeState(id);
        assertNull(manager.getState(id));
    }

    @Test
    void removeState_onMissingIdDoesNotThrow() {
        assertDoesNotThrow(() -> manager.removeState("missing"));
    }
}
