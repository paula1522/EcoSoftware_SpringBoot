import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
  vus: 50,
  duration: '30s',
};

export default function () {

  const res = http.post(
    'https://ecosoftware-spring-boot.azurewebsites.net/api/auth/login',
    JSON.stringify({
      correo: 'admin1234@gmail.com',
      contrasena: 'Admin123+'
    }),
    {
      headers: { 'Content-Type': 'application/json' }
    }
  );

  console.log('STATUS:', res.status);

  sleep(1);
}