import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '1m', target: 200 },   // carga conocida estable
    { duration: '2m', target: 500 },   // superar el pico anterior
    { duration: '2m', target: 800 },   // aumentar agresivamente
    { duration: '2m', target: 1000 },  // forzar límite
    { duration: '1m', target: 1200 },  // intentar romper
    { duration: '1m', target: 0 },     // descenso
  ],
  thresholds: {
    http_req_duration: ['p(95)<10000'], // umbral amplio, solo para observar
    http_req_failed: ['rate<0.2'],     // permitimos hasta 20% errores
  },
};

const BASE_URL = 'https://ecosoftware-spring-boot.azurewebsites.net';
const USER = { correo: 'jaiandroaber@gmail.com', contrasena: 'Administrador#2026' };

export function setup() {
  const loginRes = http.post(`${BASE_URL}/api/auth/login`, JSON.stringify(USER), {
    headers: { 'Content-Type': 'application/json' },
  });
  const token = loginRes.json().token;
  if (!token) throw new Error('No se obtuvo token');
  console.log(`Token obtenido. Iniciando prueba de estrés extrema...`);
  return { token };
}

export default function (data) {
  const res = http.get(`${BASE_URL}/api/puntos`, {
    headers: { 'Authorization': `Bearer ${data.token}` },
  });

  const ok = check(res, {
    'status 200': (r) => r.status === 200,
    'tiempo aceptable': (r) => r.timings.duration < 10000, // 10s como referencia
  });

  if (!ok && res.status !== 200) {
    console.error(`❌ Error ${res.status} a los ${res.timings.duration}ms`);
  }

  // Pausa mínima para maximizar la tasa de peticiones
  sleep(0.1);
}