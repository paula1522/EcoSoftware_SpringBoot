package com.EcoSoftware.Scrum6.Service;

import java.util.List;
import java.util.Map;

public interface EmailService {
    /** Envía un correo individual (HTML). */
    void enviarCorreo(String para, String asunto, String contenido);

    /** Envía un correo masivo. */
    void enviarCorreosMasivos(List<String> recipients, String subject, String text);

    /** Envía un correo basado en un template HTML con variables. */
    void enviarCorreoConTemplate(
            String para,
            String asunto,
            String templateNombre,
            Map<String, Object> variables
    );
    void enviarCorreoConTemplateBCC(List<String> bccRecipients, String subject, String templateNombre, Map<String, Object> variables);


}
