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

package com.kristasoft.extensions.ai.nlp.gcp.util.media;

import app.krista.ksdk.files.FileRepository;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MediaFileHandlerTest {

    private static void inject(Object target, String field, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void fileHandlerFactory_getFileHandler_returnsHandler() throws Exception {
        FileHandlerFactory factory = new FileHandlerFactory();
        inject(factory, "repository", mock(FileRepository.class));

        FileHandler handler = factory.getFileHandler();

        assertNotNull(handler);
        assertTrue(handler instanceof KristaFileHandler);
    }

    @Test
    void javaFileHandleHelper_delegatesToFileHandler() throws Exception {
        File tmp = File.createTempFile("gcp-media", ".txt");
        tmp.deleteOnExit();

        FileHandler fileHandler = mock(FileHandler.class);
        when(fileHandler.convertFile(any())).thenReturn(tmp);
        FileHandlerFactory factory = mock(FileHandlerFactory.class);
        when(factory.getFileHandler()).thenReturn(fileHandler);

        JavaFileHandleHelper helper = new JavaFileHandleHelper();
        inject(helper, "fileHandlerFactory", factory);

        File result = helper.convertFile(mock(app.krista.model.base.File.class));

        assertEquals(tmp, result);
        verify(factory).getFileHandler();
    }
}
