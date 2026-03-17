package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.util.Map;

import org.thymeleaf.context.Context;

import java.util.List;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void enviarCorreo(String to, String subject, String htmlContent) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indica que es HTML

            mailSender.send(mensaje);
        } catch (Exception e) {
            System.err.println("Error al enviar correo HTML: " + e.getMessage());
        }
    }

    @Override
    public void enviarCorreosMasivos(List<String> recipients, String subject, String htmlContent) {
        if (recipients == null || recipients.isEmpty()) return;

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(from);

            String[] destinatariosArray = recipients.toArray(new String[0]);
            helper.setTo(from); // visible
            helper.setBcc(destinatariosArray); // ocultos

            helper.setSubject(subject);
            helper.setText(htmlContent, true); // HTML

            mailSender.send(mensaje);
        } catch (Exception e) {
            System.err.println("Error al enviar correo masivo HTML: " + e.getMessage());
        }
    }

    @Autowired
private SpringTemplateEngine templateEngine;

@Override
public void enviarCorreoConTemplate(String para, String asunto, String templateNombre, Map<String, Object> variables) {

    try {
        Context context = new Context();
        context.setVariables(variables);

        // Carga el archivo de la carpeta templates/
        String htmlContent = templateEngine.process(templateNombre, context);

        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

        helper.setFrom(from);
        helper.setTo(para);
        helper.setSubject(asunto);
        helper.setText(htmlContent, true);

        mailSender.send(mensaje);

    } catch (Exception e) {
        System.err.println("Error al enviar correo con template: " + e.getMessage());
    }
}
@Override
public void enviarCorreoConTemplateBCC(List<String> bccRecipients, String subject, String templateNombre, Map<String, Object> variables) {
    if (bccRecipients == null || bccRecipients.isEmpty()) return;

    try {
        // Crear contenido HTML con Thymeleaf
        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = templateEngine.process(templateNombre, context);

        // Crear mensaje
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

        helper.setFrom(from);
        helper.setTo(from); // tu propio correo como visible
        helper.setBcc(bccRecipients.toArray(new String[0])); // todos los destinatarios ocultos
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // HTML

        // Enviar correo
        mailSender.send(mensaje);

    } catch (Exception e) {
        System.err.println("Error al enviar correo masivo con template: " + e.getMessage());
    }
}



}
