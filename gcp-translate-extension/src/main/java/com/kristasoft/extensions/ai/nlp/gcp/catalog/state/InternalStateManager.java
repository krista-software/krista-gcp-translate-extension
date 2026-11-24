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

import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages internal state for re-entry flows.
 * Stores state information when validation fails so it can be retrieved
 * when the user re-enters values.
 */
@Service
public class InternalStateManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalStateManager.class);
    
    // In production, this could be replaced with a distributed cache like Redis
    private final Map<String, Map<String, Object>> stateStore = new ConcurrentHashMap<>();

    /**
     * Generates a unique state ID
     *
     * @return Unique state ID
     */
    public String generateStateId() {
        String stateId = UUID.randomUUID().toString();
        LOGGER.debug("Generated state ID: {}", stateId);
        return stateId;
    }

    /**
     * Saves state for later retrieval
     *
     * @param stateId State ID
     * @param state   State data to save
     */
    public void saveState(String stateId, Map<String, Object> state) {
        stateStore.put(stateId, state);
        LOGGER.debug("Saved state for ID: {} with {} entries", stateId, state.size());
    }

    /**
     * Retrieves state by ID
     *
     * @param stateId State ID
     * @return State data, or null if not found
     */
    public Map<String, Object> getState(String stateId) {
        Map<String, Object> state = stateStore.get(stateId);
        if (state != null) {
            LOGGER.debug("Retrieved state for ID: {} with {} entries", stateId, state.size());
        } else {
            LOGGER.warn("No state found for ID: {}", stateId);
        }
        return state;
    }

    /**
     * Removes state after it's been used
     *
     * @param stateId State ID
     */
    public void removeState(String stateId) {
        Map<String, Object> removed = stateStore.remove(stateId);
        if (removed != null) {
            LOGGER.debug("Removed state for ID: {}", stateId);
        } else {
            LOGGER.warn("Attempted to remove non-existent state for ID: {}", stateId);
        }
    }
}

