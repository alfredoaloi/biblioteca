package notificationManager;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SMTPServerManager {
	
	private Session session;
	private Properties prop;
	
	public SMTPServerManager() {
		prop = System.getProperties();
		prop.setProperty("mail.smtp.host", "smtp.gmail.com");
		prop.setProperty("mail.smtp.port", "587");
		prop.setProperty("mail.smtp.auth", "true");
		prop.setProperty("mail.smtp.starttls.enable", "true");
        
		session = Session.getInstance(prop);
	}
	
	public void sendEmail(String email, String destination) {
		try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("francy4699@gmail.com","noreply@biblioteca.it"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(destination)
            );
            message.setSubject("Notifica libri");
            message.setContent(email, "text/html");

            Transport.send(message, "francy4699@gmail.com", "sdxzevjyptuawkla");

        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
