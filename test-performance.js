import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 100,
    duration: '30s',
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<1000'],
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const DASHBOARD_PATH = __ENV.DASHBOARD_PATH || '/api/v1/dashboard/stats';
const AUTH_TOKEN = __ENV.AUTH_TOKEN;

export default function () {
    const headers = {
        'Content-Type': 'application/json',
    };

    if (AUTH_TOKEN) {
        headers.Authorization = `Bearer ${AUTH_TOKEN}`;
    }

    const response = http.get(`${BASE_URL}${DASHBOARD_PATH}`, { headers });

    check(response, {
        'status is 200': (res) => res.status === 200,
    });

    sleep(0.2);
}
