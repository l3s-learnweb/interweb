import axios from 'axios';

const getApiUrl = () => {
    // Check if we're running on localhost:3000 (development environment)
    if (typeof window !== 'undefined' && window.location.host.startsWith('localhost:300')) {
        return 'http://localhost:8080';
    }

    // Production environment - use relative URLs
    return '';
};

export const API_URL = getApiUrl();

// Create axios instance with base URL
const apiClient = axios.create({
    baseURL: API_URL
});

// Add request interceptor to include auth token in all requests
apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
}, error => Promise.reject(error));

// Add response interceptor to handle expired JWT tokens
apiClient.interceptors.response.use(response => response, error => {
    if (error.response && error.response.status === 401) {
        console.log('JWT token expired or invalid. Removing token...');
        localStorage.removeItem('jwtToken');
    }

    return Promise.reject(error);
});

export default apiClient;
