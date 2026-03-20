
import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
  vus: 50,
  duration: '20s',
};

export default function () {
  http.get('https://ecosoftware.azurewebsites.net/');
  sleep(1);
}