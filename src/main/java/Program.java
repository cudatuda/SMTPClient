import service.MessageService;
import service.SMTPService;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class Program {
    public static void main(String[] args) throws FileNotFoundException {
        try {
            SMTPService.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
