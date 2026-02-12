const API_BASE = 'http://localhost:8081/api';

const getHeaders = () => {
    const token = localStorage.getItem('token');
    const headers = { 'Content-Type': 'application/json' };
    if (token) headers['Authorization'] = `Bearer ${token}`;
    return headers;
};

const request = async (url, options = {}) => {
    const res = await fetch(`${API_BASE}${url}`, {
        ...options,
        headers: getHeaders(),
    });
    if (res.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
        return;
    }
    if (!res.ok) {
        const err = await res.json().catch(() => ({ message: 'Request failed' }));
        throw new Error(err.message || `HTTP ${res.status}`);
    }
    return res.json();
};

const get = (url) => request(url);
const post = (url, body) => request(url, { method: 'POST', body: JSON.stringify(body) });
const put = (url, body) => request(url, { method: 'PUT', body: JSON.stringify(body) });
const del = (url) => request(url, { method: 'DELETE' });

// Auth
export const login = (data) => post('/auth/login', data);
export const register = (data) => post('/auth/register', data);

// Employees
export const getEmployees = (page = 0, size = 20) => get(`/employees?page=${page}&size=${size}`);
export const getEmployee = (id) => get(`/employees/${id}`);
export const createEmployee = (data) => post('/employees', data);
export const updateEmployee = (id, data) => put(`/employees/${id}`, data);
export const deleteEmployee = (id) => del(`/employees/${id}`);

// Ride Requests
export const getRideRequests = (page = 0, size = 20) => get(`/ride-requests?page=${page}&size=${size}`);
export const createRideRequest = (data) => post('/ride-requests', data);
export const cancelRideRequest = (id) => put(`/ride-requests/${id}/cancel`);

// Ride Plans
export const getRidePlans = (page = 0, size = 20) => get(`/ride-plans?page=${page}&size=${size}`);
export const getRidePlan = (id) => get(`/ride-plans/${id}`);
export const generateRidePlans = (date) => post(`/ride-plans/generate?date=${date}`);
export const updateRidePlanStatus = (id, status) => put(`/ride-plans/${id}/status?status=${status}`);

// Safety
export const getSafetyEvents = (page = 0, size = 20) => get(`/safety/events?page=${page}&size=${size}`);
export const reportSafetyEvent = (data) => post('/safety/events', data);

// Analytics
export const getDashboardAnalytics = () => get('/analytics/dashboard');
