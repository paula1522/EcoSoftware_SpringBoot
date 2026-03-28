import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 30 },
    { duration: '1m', target: 80 },
    { duration: '2m', target: 150 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'],
    http_req_failed: ['rate<0.01'],
  },
};

const BASE_URL = 'https://ecosoftware-spring-boot.azurewebsites.net';
const PUNTOS_ENDPOINT = '/api/puntos'; // Corregido según Postman

// Usuario con permisos (administrador)
const USER = {
  correo: 'jaiandroaber@gmail.com', // Usuario que funciona
  contrasena: 'Administrador#2026',
};

export function setup() {
  const loginRes = http.post(`${BASE_URL}/api/auth/login`, JSON.stringify(USER), {
    headers: { 'Content-Type': 'application/json' },
  });

  if (loginRes.status !== 200) {
    throw new Error(`Login fallido: ${loginRes.status} - ${loginRes.body}`);
  }

  const token = loginRes.json().token || loginRes.json().access_token;
  if (!token) throw new Error('No se recibió token');

  console.log(`Token obtenido para ${USER.correo}`);
  return { token };
}

export default function (data) {
  const headers = {
    'Authorization': `Bearer ${data.token}`,
    'Content-Type': 'application/json',
  };

  // Petición GET sin cuerpo
  const res = http.get(`${BASE_URL}${PUNTOS_ENDPOINT}`, { headers });

  let isArray = false;
  let hasIdField = false;

  try {
    const body = res.json();
    // Si la respuesta es un array directamente
    if (Array.isArray(body)) {
      isArray = true;
      if (body.length > 0) hasIdField = body[0].hasOwnProperty('id');
    } else if (body && typeof body === 'object') {
      // Posible estructura { data: [...] }
      if (body.data && Array.isArray(body.data)) {
        isArray = true;
        if (body.data.length > 0) hasIdField = body.data[0].hasOwnProperty('id');
      }
    }
  } catch (e) {
    // No es JSON válido
  }

  const checks = check(res, {
    'status 200': (r) => r.status === 200,
    'tiempo < 2s': (r) => r.timings.duration < 2000,
    'respuesta JSON válida': (r) => {
      try { r.json(); return true; } catch (e) { return false; }
    },
    'estructura esperada (array o data.array con id)': () => isArray && hasIdField,
  });

  if (!checks) {
    console.error(`❌ Fallo: status=${res.status} | tiempo=${res.timings.duration}ms | body=${res.body}`);
  }

  sleep(Math.random() * 4 + 1);
}