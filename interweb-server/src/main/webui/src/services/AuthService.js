import axios from 'axios'

const API_URL = '/api'

class AuthService {
  login(username, password) {
    return axios
      .post(`${API_URL}/login`, { username, password })
      .then(response => {
        if (response.data.token) {
          localStorage.setItem('token', response.data.token)
        }
        return response.data
      })
  }

  logout() {
    localStorage.removeItem('token')
  }

  isAuthenticated() {
    return !!localStorage.getItem('token')
  }
}

export default new AuthService()
