import http from 'k6/http';
import { sleep, check } from 'k6';

export let options = {
    stages: [
        { duration: '10s', target: 50 },
        { duration: '40s', target: 100 },
        { duration: '10s', target: 0 },
    ],
};

const usuarios = [
    { correo: 'jaiandroaber@gmail.com', contrasena: 'Administrador#2026', rol: 'Administrador' },
    { correo: 'paula06sepulveda@gmail.com', contrasena: 'Ciudadano#2026', rol: 'Ciudadano' },
    { correo: 'danacastro2014@gmail.com', contrasena: 'Empresa#2026', rol: 'Empresa' },
    { correo: 'ecosoftware2025@gmail.com', contrasena: 'Recicla#2026', rol: 'Reciclador' },
];

export default function () {
    let usuario = usuarios[Math.floor(Math.random() * usuarios.length)];

    let loginRes = http.post(
        'https://ecosoftware-spring-boot.azurewebsites.net/api/auth/login',
        JSON.stringify({ correo: usuario.correo, contrasena: usuario.contrasena }),
        { headers: { 'Content-Type': 'application/json' } }
    );

    check(loginRes, { 'login exitoso': (r) => r.status === 200 });

    let body = loginRes.json();
    let token = body.token || body.access_token || body.data?.token;

    if (!token) return;

    if (usuario.rol === 'Ciudadano') {
        // Crear solicitud
        let solicitud = {
            tipoResiduo: "Vidrio",
            cantidad: 5,
            descripcion: "Botellas reciclables",
            localidad: "Suba",
            ubicacion: "Calle 123 #45-67",
            latitude: 4.756,
            longitude: -74.123,
            fechaProgramada: "2026-02-28T10:00:00",
            evidencia: "imagen.png"
        };
        let resSolicitud = http.post(
            'https://ecosoftware-spring-boot.azurewebsites.net/api/solicitudes',
            JSON.stringify(solicitud),
            { headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` } }
        );
        check(resSolicitud, { 'solicitud creada': (r) => r.status === 200 });

        // Consulta de capacitaciones
        let resCap = http.get(
            'https://ecosoftware-spring-boot.azurewebsites.net/api/capacitaciones',
            { headers: { 'Authorization': `Bearer ${token}` } }
        );
        check(resCap, { 'consultó capacitaciones': (r) => r.status === 200 });

        // Consulta de puntos de reciclaje
        let resPuntos = http.get(
            'https://ecosoftware-spring-boot.azurewebsites.net/api/puntos',
            { headers: { 'Authorization': `Bearer ${token}` } }
        );
        check(resPuntos, { 'consultó puntos': (r) => r.status === 200 });
    }

    sleep(1);
}