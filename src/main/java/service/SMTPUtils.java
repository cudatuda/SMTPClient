package service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Random;

public class SMTPUtils {
    private final static char[] MULTIPART_CHARS =
            "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    .toCharArray();

    public static String generateBoundary() {
        StringBuilder buffer = new StringBuilder();
        Random rand = new Random();
        int count = rand.nextInt(11) + 30; // a random size from 30 to 40
        for (int i = 0; i < count; i++) {
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }


    public static String encodeFileToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
        }
    }
    public static String getAttachmentType(String filename) {
        switch (filename.split("\\.")[1]) {
            case "jpeg", "jpg" -> {
                return "image/jpeg;";
            }
            case "png" -> {
                return "image/png;";
            }
            case "pdf" -> {
                return "application/pdf;";
            }
            case "zip" -> {
                return "application/zip;";
            }
        }
        return null;
    }
}
