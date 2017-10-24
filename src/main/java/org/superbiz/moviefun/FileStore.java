package org.superbiz.moviefun;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;

import java.io.*;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;

public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {

        File targetFile = new File(blob.name);
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(IOUtils.toByteArray(blob.inputStream));
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        Optional<Blob> image = Optional.empty();

        File coverFile = new File(name);
        if (coverFile.exists()) {
            image = Optional.of(new Blob(name, new BufferedInputStream(new FileInputStream(coverFile))
                    , new Tika().detect(coverFile)));
        } 

        return image;
    }
}