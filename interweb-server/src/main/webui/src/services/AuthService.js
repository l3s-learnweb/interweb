import apiClient from '../config';

class AuthService {
    sendLink(email) {
        return apiClient.post(`/login`, {email}).then(response => {
            return response.data;
        });
    }

    exchangeToken(token) {
        return apiClient.get(`/jwt?token=${token}`).then(response => {
            this.setToken(response.data);
        });
    }

    userInfo() {
        return apiClient.get(`/users/me`).then(response => {
            return response.data;
        });
    }

    userUsage() {
        return apiClient.get(`/users/usage`).then(response => {
            return response.data;
        });
    }

    setToken(token) {
        localStorage.setItem('jwtToken', token);
    }

    logout() {
        localStorage.removeItem('jwtToken');
    }

    isAuthenticated() {
        return !!localStorage.getItem('jwtToken');
    }
}

export default new AuthService();
