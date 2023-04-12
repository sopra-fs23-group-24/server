package ch.uzh.ifi.hase.soprafs23.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class ZipDataURL {
    public static String zip(String dataURL) throws IOException {
        // Decode the data URL into a byte array
        byte[] data = java.util.Base64.getDecoder().decode(dataURL.substring(dataURL.indexOf(",") + 1));

        // Compress the byte array using Deflater
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(outputStream, deflater);
        deflaterOutputStream.write(data);
        deflaterOutputStream.close();

        // Encode the compressed byte array as a new data URL
        String compressedDataURL = "data:application/zip;base64," + java.util.Base64.getEncoder().encodeToString(outputStream.toByteArray());
        return compressedDataURL;
    }
}
