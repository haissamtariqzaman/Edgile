
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailThread extends Thread {
    
    private String sec_name;
    private String email;
    
    public EmailThread(String s, String e){
        sec_name=s;
        email=e;
    }
    
    @Override
    public void run() {
        String email1="noreplyedgile@gmail.com";
        String password1="justforproject";    
        String to=this.email;
        
        Properties properties = new Properties();  
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
    
        Session session = Session.getInstance(properties, new Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(email1,password1);
            }
        });
  
        try {  
            Message message = new MimeMessage(session);  
            message.setFrom(new InternetAddress(email1));  
            message.setRecipient(Message.RecipientType.TO,new InternetAddress(to));  
            message.setSubject("Section Available to Register");  
            message.setText("You subscribed for the section: "+sec_name+". This is to inform you that it is now available for registeration.");  
 
            Transport.send(message);  
            System.out.println("message sent successfully...");
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
