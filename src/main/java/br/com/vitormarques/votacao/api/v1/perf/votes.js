import http from 'k6/http';
import {check} from 'k6';

// Cada VU dispara votos em loop durante a duração; cada iteração usa um CPF válido e único.
// Rode a aplicação com ELIGIBILITY_ABLE_RATE=1 para que o sorteio do bônus 1 não rejeite metade.
export const options = {
    vus: 100,
    duration: '30s',
    thresholds: {
        http_req_duration: ['p(95)<200'],
        http_req_failed: ['rate<0.01'],
    },
};

const BASE = __ENV.BASE_URL || 'http://localhost:8080';
const JSON_HEADERS = {headers: {'Content-Type': 'application/json'}};

export function setup() {
    const topic = http.post(`${BASE}/api/v1/topics`,
        JSON.stringify({title: 'Teste de carga'}), JSON_HEADERS).json();
    http.post(`${BASE}/api/v1/topics/${topic.id}/session`,
        JSON.stringify({durationMinutes: 30}), JSON_HEADERS);
    return {topicId: topic.id};
}

export default function (data) {
    const memberId = cpfFromBase(__VU * 100000 + __ITER);
    const choice = __ITER % 2 === 0 ? 'YES' : 'NO';

    const res = http.post(`${BASE}/api/v1/topics/${data.topicId}/votes`,
        JSON.stringify({memberId, choice}), JSON_HEADERS);

    check(res, {'voto registrado (201)': (r) => r.status === 201});
}

export function teardown(data) {
    const result = http.get(`${BASE}/api/v1/topics/${data.topicId}/result`).json();
    console.log(`Resultado: yes=${result.yes} no=${result.no} total=${result.total}`);
}

// Gera CPF válido.
function cpfFromBase(base) {
    const digits = String(base).padStart(9, '0');
    const first = checkDigit(digits);
    const second = checkDigit(digits + first);
    return digits + first + second;
}

function checkDigit(digits) {
    let weight = digits.length + 1;
    let sum = 0;
    for (const c of digits) {
        sum += Number(c) * weight--;
    }
    const remainder = sum % 11;
    return remainder < 2 ? 0 : 11 - remainder;
}