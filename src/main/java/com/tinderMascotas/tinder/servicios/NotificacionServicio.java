package com.tinderMascotas.tinder.servicios;

import com.tinderMascotas.tinder.entidades.Usuario;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificacionServicio {

    
    
    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${spring.mail.password}")
    private String mailPassword;
    
    
/////Metodo para notificar el registro exitoso de un usuario.    
    public void notificar(Usuario usuario) {

		try {
		
			if (usuario != null) {
				enviarHTML(usuario);

				
			} else {
				
			}
		} catch (Exception e) {
			System.out.println("ERROR con " + e.getMessage());	
		}
	}
    
    
    
/////Metodo para enviar email de registro exitoso en formato HTML.    
    @Async
	private void enviarHTML(Usuario usuario) {

		Properties prop = new Properties();
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", 587);
		prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

		Session session = Session.getInstance(prop, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailFrom, mailPassword);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(mailFrom));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(usuario.getMail()));
			message.setSubject("Mail Subject");

			String msg = "<table style= 'background-color: #0f0f0f; max-width: 600px; padding: 10px; margin:0 auto; border-collapse: collapse;'>"+
                           "<tr>"+
                            "<td style='background-color: #47350e; text-align: left; padding: 0'>"+
                             "<img  height=\"70%\" width=\"70%\" style=\"display:block; margin: 1.5% 15%\" src=\"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaNHspWu3XmGU5b1uaWFSF41dL-ThQxMLqww&usqp=CAU\">"+
                            "</td>"+
                           "</tr>"+
                           "<tr>"+
                            "<td style=\"padding: 0\">"+
                             "<h3>a</h3>"+
                            "</td>"+
                           "</tr>"+
                           "<tr>"+
                            "<td style=\"background-color: #ecf0f1\">"+
                             "<div style=\"color: #2b3744; margin: 4% 10% 2%; text-align: justify;font-family: sans-serif\">"+
                                "<h2 style=\" text-align: center; font-family:Arial, Helvetica, sans-serif; color: #c08403; margin: 0 0 7px\">¡Bienvenido "+usuario.getNombre()+" "+usuario.getApellido()+", que disfrutes mucho tu paso por Adopción Patitas! "+"</h2>"+
                                "<p style=\"margin: 2px; font-size: 15px\">"+
                                  "Somos una asociación que se encarga de la adopción de gatitos, damos a conocer los michis que estan buscando hogar, !Y si "+
                                  "usted tiene algún gatito al que no pueda darle hogar tampoco dude en consultarnos! "+
                                   "Nuestro sitio web cuenta con la posibilidad de: </p>"+
                                "<ul style=\"font-size: 15px;  margin: 10px 0\">"+
                                  "<li>Adoptar gatitos.</li>"+
                                  "<li>Dar en adopción gatitos.</li>"+
                                  "<li>Comunicarse con el propietario del gatito.</li>"+
                       
                                "</ul>"+
                                "<div style=\"width: 100%;margin:20px 0; display: inline-block;text-align: center\">"+
                                  "<img style=\"padding: 0; width: 200px; margin: 5px\" src=\"https://i2.wp.com/www.lenda.net/wp-content/uploads/2020/04/gatitos-lenda-min.jpg?resize=1080%2C675&ssl=1\">"+
                                  "<img style=\"padding: 0; height: 125px; width: 200px; margin: 5px\" src=\"https://static.eldiario.es/clip/619daede-4610-479e-9710-d2a9c372bfbb_16-9-aspect-ratio_default_0.jpg\">"+
                                "</div>"+
                                "<div style=\"width: 100%; text-align: center\">"+
                                  "<a style=\"text-decoration: none; border-radius: 5px; padding: 11px 23px; color: #fdfcf9; background-color: #3498db\" href=\"http://localhost:8080/\">¡Ir a nuestra página!</a>"+
                                "</div>"+
                                "<p style=\"color: #b3b3b3; font-size: 12px; text-align: center;margin: 30px 0 0\">Patitas desde 1993</p>"+
                               "</div>"+
                              "</td>"+
                             "</tr>"+ 
                           "</table>";
			
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(msg, "text/html");

			// DESCOMENTAR PARA ENVIAR ARCHIVOS DESDE EL SERVIDOR O BUCKET
			// MimeBodyPart attachmentBodyPart = new MimeBodyPart();
			// attachmentBodyPart.attachFile(new File("pom.xml"));

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);
			// multipart.addBodyPart(attachmentBodyPart);

			message.setContent(multipart);
                        message.setSubject("Adopciones Patitas");
                        
			Transport.send(message);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    
}


