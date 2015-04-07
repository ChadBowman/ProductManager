package net.orthus.pm;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailManager {
	
	
	public static final int ORTHS = 0;
	public static final int GMAIL = 1;
	
	private int state;
	
	public EmailManager(int state){
		this.state = state;
	}
	
	private void sendEmail(String recipient, String subject, String body){
		switch(state){
			case ORTHS: sendOrthusMail(recipient, subject, body); break;
			case GMAIL: sendGmail(recipient, subject, body);      break; 
		}
	}
	
	public void purchaseNotice(String recipient, String name){
		
		String txt = String.format(
				"<p style=\"color:#696969;\">Dear %s,<br><br>Thank you for purchasing one of our " +
				"Nintendo DS/3DS repair services. At your earliest convenience, please send your console to:<br>" +
				"<br>Orthus Repairs<br>1104 S Montana Ave #C8<br>Bozeman, MT 59715<br><br>Please be sure " +
				"to have tracking with your shipping service to ensure your console does not " +
				"get lost in the post. We recommend saving some time and money by <a style=\"color:#A37A00;" +
				"text-decoration:none;\" href=\"https://cns.usps.com/go\">purchasing flat rate postage with " +
				"USPS online</a>. " +
				"Once received and diagnosed, another email will be sent here. " +
				"Feel free to respond to this email to ask any questions.<br><br>Thank you,<br><br>" +
				"Chad Bowman<br>Orthus Technology</p><br><br>" +
				"<a href=\"http://www.orthus.net\">" +
				"<center><img src=\"http://orthus.net/images/Signature.jpg\"></center></a>",
				name);
		
		sendEmail(recipient, "Repair Purchased", txt);
		
	}
	
	public void aquisitionNotice(String recipient, String name){
		
		String txt = String.format(
				"<p style=\"color:#696969;\">Dear %s,<br><br>Your " +
				"console has arrived and a final diagnosis has been completed. Please " +
				"look out for an itemized invoice via Paypal within the next hour. Information " +
				"regarding the diagnosis and reasons for repair can be found in the note section of the " +
				"invoice. As " +
				"soon as payment has been received, the repair(s) will be made and a confirmation " +
				"email will be sent with your tracking number. Feel free to respond to this " +
				"email to ask any questions or voice any concerns.<br><br>Thank you for your support," +
				"<br><br>Chad Bowman<br>" +
				"Orthus Technology</p><br><br>" +
				"<a href=\"http://www.orthus.net\">" +
				"<center><img src=\"http://orthus.net/images/Signature.jpg\"></center></a>",
				name); 
		
		
		String cpy = String.format(
				"Dear %s,%n%nYour console(s) have arrived and been diagnosed. Please check your %s email" +
				" for your invoice and further information.%n%nThanks!%nChad", 
				name, recipient.replaceAll("@.*", ""));
		
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(recipient), null);
		
		sendEmail(recipient, "Console Received", txt);
	}
	
	public void completionNotice(String recipient, String name, String tracking){
		
		Date d = Date.getTodaysDate();
		Date tom = d.addDays(1);
		Date two = d.addDays(2);
		Date three = d.addDays(3);
		
		String time;
		
		if(d.getDayOfTheWeek().equals(Date.SATURDAY) && d.getHour() > 16){
			
			if(two.isUSPSHoliday())
				time = "Tuesday, " + three.getMonthString() + " " + three.getDay() +
					" due to the holiday";
			else
				time = "Monday, " + two.getMonthString() + " " + two.getDay();
			
		}else if(!d.getDayOfTheWeek().equals(Date.SUNDAY) && d.getHour() < 13){
			
			if(d.isUSPSHoliday())
				time = "tomorrow, " + tom.getMonthString() + " " + tom.getDay() + 
					" due to the holiday";
			else
				time = "today, " + d.getMonthString() + " " + d.getDay();
			
		}else
			time = "tomorrow, " + tom.getMonthString() + " " + tom.getDay();
		
		String txt = String.format(
				"<p style=\"color:#696969;\">Dear %s,<br><br>The repair was a success and will ship %s. " +
			    "Your USPS tracking number is <a style=\"color:#A37A00; text-decoration:none;\" href=\"" +
				"https://tools.usps.com/go/TrackConfirmAction.action?tRef=fullpage&tLc=1&tLabels=%s\">" +
			    "%s</a>.<br><br>If you used Ebay, save money on your next repair by using our website at <a href='http://www.orthus.net' " +
			    "style='color:#A37A00; text-decoration:none;'>orthus.net</a>.<br><br>We really appreciate your business " +
				"and seek your 5-star positive feedback. If there are any problems or concerns please contact us " +
			    " so we can make it right. Thanks again and have a great day!<br>" +
				"<br>Sincerely,<br><br>Chad Bowman<br>Orthus Technology</p><br><br><a href='http://www.orthus.net'>" +
				"<center><img src=\"http://orthus.net/images/Signature.jpg\"></center></a>",
				name, time, tracking, tracking);
		
		sendEmail(recipient, "Repair Completed", txt);
	}
	
	public void sendGmail(String recipient, String subject, String body){
		
		
		final String username = "chad.bowman0@gmail.com";
       	final String password = "Cer6eru$";

        Properties props = new Properties();
        
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        
        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
            	return new PasswordAuthentication(username, password);
            }
          });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("chad.bowman0@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);
            
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
		
        System.out.println("Email sent to " + recipient);
	}
	
	public void sendOrthusMail(String recipient, String subject, String body){
		
	    Properties props = new Properties();
	    props.put("mail.smtp.host", "mail.orthus.net");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.user", "orthus");
	    props.put("mail.smtp.password", "0rthu$Tech");
	    props.put("mail.smtp.port", "26" );
	
	    Session session = Session.getInstance(props,
	            new javax.mail.Authenticator() {
	              protected PasswordAuthentication getPasswordAuthentication() {
	              	return new PasswordAuthentication("orthus", "0rthu$Tech");
	              }
	            });
	    
	    Message msg = new MimeMessage(session);
	
	    try{
		    msg.setFrom(new InternetAddress("chad@orthus.net"));
		    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		    msg.setSubject(subject);
		    msg.setContent(body, "text/html; charset=utf-8");
		    Transport.send(msg);
		    
	    }catch(MessagingException e){
	    	
	    	GUIEngine.setUtilityText("Email to " + recipient + " was not sent!");
	    	
	    }
	    
	    System.out.println("Email sent to " + recipient);
	}
}
