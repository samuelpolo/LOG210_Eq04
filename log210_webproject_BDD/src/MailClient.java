import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
public class MailClient {

	//public static void main(String[] args) {
		public MailClient(String destinataire, String subject , String text){
		//Liste des variables
		String smtpHost = "smtp.gmail.com";
	    String from = "cooplog210@gmail.com";
	    String to = destinataire;
	    String username = "cooplog210@gmail.com";
	    String password = "cooplog21";
	    
	    //Le setting des propriétés du SMTP
	    Properties props = new Properties();
	    props.put("mail.smtp.host", smtpHost);
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.socketFactory.port", "465");
	    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    props.put("mail.smtp.port", "465");
	    
	    Authenticator authenticator = new javax.mail.Authenticator() 
        {
            protected PasswordAuthentication getPasswordAuthentication() 
            {
                return new PasswordAuthentication(username, password);
            }
        };
        
	    Session session = Session.getInstance(props,authenticator);
	    session.setDebug(true);
	    
	    //Création du Message à envoyer
	    MimeMessage message = new MimeMessage(session);  
	    try {
	    message.setFrom(new InternetAddress(from));
	    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	    message.setSubject(subject);
	    message.setText(text);
	    
	    //Envoi du message
	    Transport.send(message);

	    
	    } catch (AddressException e) {
	    	e.printStackTrace();
	    	}
	    catch (MessagingException e) {
			e.printStackTrace();
			}
		}
	//}

}
