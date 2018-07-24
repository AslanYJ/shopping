import com.taotao.search.utils.SendMail;
import org.junit.Test;

import javax.mail.MessagingException;

public class TestSendMail {
    @Test
    public void testSendMail(){
        try {
            SendMail.sendMail("This is the subjuect line","This is the actual message");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
