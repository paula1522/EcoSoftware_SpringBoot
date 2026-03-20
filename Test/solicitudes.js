import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
  vus: 20,
  duration: '40s',
};

export default function () {

  let loginRes = http.post(
    'https://ecosoftware-spring-boot.azurewebsites.net/api/auth/login',
    JSON.stringify({
      correo: 'admin1234@gmail.com',
      contrasena: 'Admin123+'
    }),
    {
      headers: { 'Content-Type': 'application/json' }
    }
  );

  let body = loginRes.json();
  let token = body.token || body.access_token || body.data?.token;

  if (!token) {
    console.log('❌ NO SE OBTUVO TOKEN');
    return;
  }

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

  let res = http.post(
    'https://ecosoftware-spring-boot.azurewebsites.net/api/solicitudes',
    JSON.stringify(solicitud),
    {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    }
  );

  console.log('STATUS SOLICITUD:', res.status);

  sleep(1);
}


/*
import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
  vus: 100,
  duration: '30s',
};

const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbjEyMzRAZ21haWwuY29tIiwiaWF0IjoxNzc0MDMzMDY0LCJleHAiOjE3NzQxMTk0NjR9.7RY4XL_zzper9bDPxnj8gsgZjYYGtSuMXgmJn4wEzxA';

export default function () {

  let res = http.get(
    'https://ecosoftware-spring-boot.azurewebsites.net/api/solicitudes',
    {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    }
  );

  console.log('STATUS:', res.status);

  sleep(1);
}*/