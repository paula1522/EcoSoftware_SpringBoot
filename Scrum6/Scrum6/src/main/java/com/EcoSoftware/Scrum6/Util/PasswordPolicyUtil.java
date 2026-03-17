package com.EcoSoftware.Scrum6.Util;

import java.util.regex.Pattern;

public final class PasswordPolicyUtil {

    private static final Pattern PATRON_CONTRASENA_SEGURA = Pattern
            .compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$");

    private PasswordPolicyUtil() {
    }

    public static boolean esSegura(String contrasena) {
        return contrasena != null && PATRON_CONTRASENA_SEGURA.matcher(contrasena).matches();
    }

    public static void validar(String contrasena) {
        if (!esSegura(contrasena)) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener mínimo 8 caracteres e incluir mayúscula, minúscula, número y un carácter especial.");
        }
    }
}
