package service;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SMTPService {
    private static String SMTP_HOST = "smtp.mail.ru";
    private static int SMTP_PORT = 465;
    private static String user = "ifconfig@internet.ru";
    private static String pass = "PASSWORD";
    private static DataOutputStream dataOutputStream;
    private static BufferedReader br = null;


    public static void run() throws Exception {
        String username = Base64.getEncoder().encodeToString(user.getBytes(StandardCharsets.UTF_8));
        String password = Base64.getEncoder().encodeToString(pass.getBytes(StandardCharsets.UTF_8));
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(SMTP_HOST, SMTP_PORT);

        br = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

        dataOutputStream = new DataOutputStream(sslSocket.getOutputStream());

        send("EHLO smtpclient.ru\r\n",1);
        send("AUTH LOGIN\r\n",1);
        send(username + "\r\n",1);
        send(password + "\r\n",1);
        send("MAIL FROM: ifconfig@internet.ru\r\n",1);
        for (String recipient : MessageService.getRecipients()) {
            send("RCPT TO: " + recipient + "\r\n",1);
        }
        send("DATA\r\n",1);
        send(MessageService.formMessage() + "\r\n", 0);
        send("QUIT\r\n",1);
    }
    private static void send(String s, int no_of_response_line) throws Exception
    {
        dataOutputStream.writeBytes(s);
        System.out.println("CLIENT: "+s);
        Thread.sleep(1000);

        for (int i = 0; i < no_of_response_line; i++) {
            System.out.println("SERVER : " +br.readLine());
        }
    }
}

