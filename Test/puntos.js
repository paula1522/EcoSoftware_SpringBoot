/*import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
  vus: 20,
  duration: '30s',
};

const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbjEyMzRAZ21haWwuY29tIiwiaWF0IjoxNzc0MDMzMDY0LCJleHAiOjE3NzQxMTk0NjR9.7RY4XL_zzper9bDPxnj8gsgZjYYGtSuMXgmJn4wEzxA';

export default function () {

  let res = http.get(
    'https://ecosoftware-spring-boot.azurewebsites.net/api/puntos',
    {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    }
  );

  console.log('STATUS:', res.status);

  sleep(1);
}*/




import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
  vus: 100,
  duration: '30s',
};

const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbjEyMzRAZ21haWwuY29tIiwiaWF0IjoxNzc0MDMzMDY0LCJleHAiOjE3NzQxMTk0NjR9.7RY4XL_zzper9bDPxnj8gsgZjYYGtSuMXgmJn4wEzxA';

export default function () {

  let res = http.get(
    'https://ecosoftware-spring-boot.azurewebsites.net/api/puntos',
    {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    }
  );

  console.log('STATUS:', res.status);

  sleep(1);
}