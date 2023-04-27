package service;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

public class MessageService {
    private static HierarchicalINIConfiguration config;

    static {
        try {
            config = new HierarchicalINIConfiguration("message.ini");
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }


    private static final String[] recipients = config.getStringArray("recipients");

    private static final String theme = config.getString("theme");
    private static final String[] attachmentsUri = config.getStringArray("attachments");

    private static final InputStream messageInputStream = MessageService.class.getClassLoader().getResourceAsStream("message.txt");

    public static String[] getRecipients() {
        return recipients;
    }

    public static String getTheme() {
        return theme;
    }

    public static String[] getAttachmentsUri() {
        return attachmentsUri;
    }

    public static String getText()  {
        StringBuilder result = new StringBuilder();

        Scanner scanner = null;

        assert messageInputStream != null;
        try {
            scanner =  new Scanner(new File("src/main/resources/message.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found.");
        }

        while (scanner.hasNextLine()) {
            result.append(scanner.nextLine()).append("\n");
        }
        return result.toString();
    }

    public static String formMessage() {
        String boundary = SMTPUtils.generateBoundary();
        String text = "";
        text += "From: ifconfig@internet.ru\n";
        text += "To: " + String.join(",", MessageService.getRecipients()) + "\n";
        text += "Subject: " + "=?UTF-8?B?"
                + Base64.getEncoder().encodeToString(MessageService.getTheme().getBytes(StandardCharsets.UTF_8))
                + "?=" + '\n';

        if (MessageService.getAttachmentsUri().length > 0) {
            text += "Content-Type: multipart/mixed; boundary =" + boundary + "\n\n\n";
            text += "--" + boundary + '\n';
            text += "Content-Type: text/plain; charset=\"UTF-8\"\n";
            text += "Content-Transfer-Encoding: base64\n\n";
            text += Base64.getEncoder().encodeToString(MessageService.getText().getBytes(StandardCharsets.UTF_8));
            for (String attachmentUri : MessageService.getAttachmentsUri()) {

                String name_base64 = "\"=?UTF-8?B?"
                        + Base64.getEncoder().encodeToString(attachmentUri.getBytes(StandardCharsets.UTF_8))
                        + "?=\"";
                text += "--" + boundary + "\n";
                text += "Content-Disposition: attachment; filename=" + name_base64 + '\n';
                text += "Content-Transfer-Encoding: base64\n";
                text += "Content-Type: " + SMTPUtils.getAttachmentType(
                        attachmentUri) + " name=" + name_base64 + "\n\n";
                text += SMTPUtils.encodeFileToBase64(new File("src/main/resources/"+attachmentUri)) + '\n';
            }
            text += "--" + boundary + "--";
        }
        else {
            text += "Content-Type: text/plain; charset=\"UTF-8\"\n";
            text += "Content-Transfer-Encoding: base64\n\n";
            text += Base64.getEncoder().encodeToString("Привет из JAVA".getBytes(StandardCharsets.UTF_8));
        }
        return text + "\n.";
    }
}
