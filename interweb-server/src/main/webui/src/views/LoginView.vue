<template>
  <div class="login-container">
    <h1>Login</h1>
    <div class="login-form">
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">Username</label>
          <input v-model="username" type="text" id="username" required>
        </div>
        <div class="form-group">
          <label for="password">Password</label>
          <input v-model="password" type="password" id="password" required>
        </div>
        <button type="submit" :disabled="loading">
          {{ loading ? 'Logging in...' : 'Login' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script>
import AuthService from '../services/AuthService'

export default {
  data() {
    return {
      username: '',
      password: '',
      loading: false,
      errorMessage: ''
    }
  },
  methods: {
    handleLogin() {
      this.loading = true
      this.errorMessage = ''

      AuthService.login(this.username, this.password)
        .then(() => {
          this.$router.push('/api-keys')
        })
        .catch(error => {
          this.errorMessage = error.response?.data?.message || 'Login failed'
        })
        .finally(() => {
          this.loading = false
        })
    }
  }
}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
}

.login-form {
  background: var(--color-background-soft);
  padding: 20px;
  border-radius: 8px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 8px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 10px;
  background: hsla(160, 100%, 37%, 1);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.error-message {
  background: rgba(255, 0, 0, 0.1);
  color: red;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
}
</style>
