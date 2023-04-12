package ch.uzh.ifi.hase.soprafs23.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class UnzipDataURL {
    public static String unzip(String compressedDataURL) throws IOException {
        // Decode the compressed data URL into a byte array
        byte[] compressedData = java.util.Base64.getDecoder().decode(compressedDataURL.substring(compressedDataURL.indexOf(",") + 1));

        // Decompress the byte array using Inflater
        Inflater inflater = new Inflater();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(compressedData.length);
        InflaterInputStream inflaterInputStream = new InflaterInputStream(new ByteArrayInputStream(compressedData), inflater);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inflaterInputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inflaterInputStream.close();
        outputStream.close();

        // Encode the decompressed byte array as a new data URL
        String dataURL = "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(outputStream.toByteArray());
        return dataURL;
    }
}
