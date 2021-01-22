package com.chester.michat.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ZipUtils {

    public static byte[] zip(byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(data);
        gzip.close();
        out.close();
        return out.toByteArray();
    }

    public static byte[] unzip(byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        GZIPInputStream ginzip = new GZIPInputStream(in);
        byte[] buffer = new byte[1024];
        int offset = -1;
        while ((offset = ginzip.read(buffer)) != -1) {
            out.write(buffer, 0, offset);
        }
        ginzip.close();
        in.close();
        out.close();
        return out.toByteArray();
    }

}
