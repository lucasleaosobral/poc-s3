package com.lucasleao.pocaws;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.net.URI;
import java.nio.file.Paths;

public class Utils {

    public static String getFileIdByUrl(String url) {
        URI uri = URI.create(url);
        return Paths.get(uri.getPath()).getFileName().toString();
    }

    public static MockMultipartFile generateMockFile() {

        return new MockMultipartFile(
                "file",
                "test-file.txt",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "This is a test file".getBytes());
    }
}
