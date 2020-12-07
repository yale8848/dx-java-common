package com.daoxuehao.java.dxcommon.logback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Yale
 * create at: 2020-12-07 14:28
 **/
public class IOUtils {


    public static byte[] toBytes(final InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int count;
        byte[] buf = new byte[512];

        while((count = is.read(buf, 0, buf.length)) != -1) {
            baos.write(buf, 0, count);
        }
        baos.flush();

        return baos.toByteArray();
    }
}
