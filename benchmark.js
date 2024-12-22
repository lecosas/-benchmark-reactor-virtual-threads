import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 1000 }, // ramp up to 100 requests
    { duration: '30s', target: 1000 }, // hold at 100 requests for 2 minutes
    { duration: '30s', target: 0 },   // ramp down to 0 requests
  ],
};

export default function () {
  // Replace with the appropriate URL for your endpoints
  const baseUrl = 'http://localhost:8080/api';

    let res;

  // Benchmark WebFlux (Reactive)
  res = http.get(`${baseUrl}/reactor`);
  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  // Benchmark Virtual Threads
//  res = http.get(`${baseUrl}/virtual-thread`);
//  check(res, {
//    'status is 200': (r) => r.status === 200,
//  });

  sleep(1); // Simulate user think time
}
