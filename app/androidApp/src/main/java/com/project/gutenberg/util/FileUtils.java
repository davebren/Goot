package com.project.gutenberg.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static void writeStreamToFile(InputStream inputStream, String path) throws IOException {
        FileOutputStream writer = new FileOutputStream(path);
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            writer.write(buffer, 0, len);
        }
        writer.close();
        inputStream.close();
    }

}
