import apiClient from '../config';

class ApiKeysService {
    list() {
        return apiClient.get('/api_keys');
    }

    create(apiKeyData) {
        return apiClient.post('/api_keys', apiKeyData);
    }

    delete(id) {
        return apiClient.delete(`/api_keys?id=${id}`);
    }

    usage(id) {
        return apiClient.get(`/api_keys/usage?id=${id}`);
    }
}

export default new ApiKeysService();
