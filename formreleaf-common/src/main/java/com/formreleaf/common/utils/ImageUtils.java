package com.formreleaf.common.utils;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Bazlur Rahman Rokon
 * @date 7/15/15.
 */
public class ImageUtils {
    public static byte[] convert(String base64) throws IOException {
        String imageDataBytes = base64.substring(base64.indexOf(",") + 1);
        ByteArrayInputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes()));

        return IOUtils.toByteArray(stream);
    }
}
