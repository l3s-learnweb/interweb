import axios from 'axios'

const API_URL = '/api'

// Create axios instance with auth header
const apiClient = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json'
    }
})

apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
})

class ApiKeyService {
    getApiKeys() {
        return apiClient.get('/api_keys')
    }

    createApiKey(name) {
        return apiClient.post('/api_keys', {name})
    }

    deleteApiKey(keyId) {
        return apiClient.delete(`/api_keys/${keyId}`)
    }
}

export default new ApiKeyService()
