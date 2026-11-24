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

import app.krista.ksdk.files.FileHandle;
import app.krista.ksdk.files.FileRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class KristaFileHandler implements FileHandler {

    private final FileRepository repository;

    public KristaFileHandler(FileRepository repository) {
        this.repository = repository;
    }

    @Override
    public File convertFile(app.krista.model.base.File file) throws IOException {
        final FileHandle inputFileHandle = this.repository.getFile(file);

        final File tmpDir = new File(System.getProperty("java.io.tmpDir", "/tmp"));

        if (!tmpDir.exists() || !tmpDir.canWrite()) {
            throw new IllegalArgumentException("Unable to write to temporary directory: " + tmpDir.getAbsolutePath());
        }

        final File input = new File(tmpDir, file.getFileName());

        try (InputStream inputStream = inputFileHandle.getContent();
             OutputStream outputStream = new FileOutputStream(input)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return input;
    }
}
