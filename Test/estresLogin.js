import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';

export const options = {
  stages: [
    { duration: '30s', target: 30 },   // arranque suave
    { duration: '1m', target: 60 },    // aumento intermedio
    { duration: '2m', target: 100 },   // pico de 100 VUs
    { duration: '30s', target: 0 },    // descenso
  ],
  thresholds: {
    http_req_duration: ['p(95)<3000'], // 95% de peticiones < 3 segundos
    http_req_failed: ['rate<0.01'],    // menos del 1% de errores
  },
};

const BASE_URL = 'https://ecosoftware-spring-boot.azurewebsites.net';

// Credenciales reales (5 usuarios diferentes)
const usuarios = new SharedArray('usuarios', function () {
  return [
    { correo: 'jaiandroaber@gmail.com', contrasena: 'Administrador#2026' },
    { correo: 'admin@gmail.com', contrasena: 'Admin#2026' },
    { correo: 'paula06sepulveda@gmail.com', contrasena: 'Ciudadano#2026' },
    { correo: 'danacastro2014@gmail.com', contrasena: 'Empresa#2026' },
    { correo: 'ecosoftware2025@gmail.com', contrasena: 'Recicla#2026' },
  ];
});

export default function () {
  // Distribución equitativa de los usuarios
  const user = usuarios[__VU % usuarios.length];
  const payload = JSON.stringify({
    correo: user.correo,
    contrasena: user.contrasena,
  });

  const res = http.post(`${BASE_URL}/api/auth/login`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });

  // Validaciones
  let tokenOk = false;
  try {
    const body = res.json();
    const token = body.token || body.access_token || body.data?.token;
    tokenOk = token && token.length > 10;
  } catch (e) {}

  const ok = check(res, {
    'status 200': (r) => r.status === 200,
    'token presente': () => tokenOk,
    'tiempo < 3s': (r) => r.timings.duration < 3000,
  });

  if (!ok) {
    console.error(`❌ Fallo para ${user.correo}: status=${res.status}, tiempo=${res.timings.duration}ms, tokenOk=${tokenOk}`);
  }

  // Pausa entre 0.5 y 1.5 segundos para evitar saturación artificial
  sleep(Math.random() * 1 + 0.5);
}