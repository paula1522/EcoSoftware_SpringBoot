

import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
  vus: 20,
  duration: '30s',
};

export default function () {

  let res = http.get(
    'https://ecosoftware-spring-boot.azurewebsites.net/api/capacitaciones'
  );

  console.log('STATUS:', res.status);

  sleep(1);
}