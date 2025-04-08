<script setup></script>

<template>
    <div id="app">
        <nav v-if="isAuthenticated" class="nav-menu">
            <router-link to="/dashboard">Dashboard</router-link>
            <router-link to="/api-keys">API Keys</router-link>
            <router-link to="/api-keys/create">Create API Key</router-link>
            <a @click="logout" class="logout-link">Logout</a>
        </nav>

        <main>
            <router-view/>
        </main>
    </div>
</template>

<script>
import AuthService from './services/AuthService'
import router from './router' // Make sure this import exists

export default {
    name: 'App',
    data() {
        return {
            isAuthenticated: false
        }
    },
    created() {
        this.checkAuth()
    },
    watch: {
        $route() {
            this.checkAuth()
        }
    },
    methods: {
        checkAuth() {
            this.isAuthenticated = AuthService.isAuthenticated()
            // Redirect to login if not authenticated and not already on login page
            if (!this.isAuthenticated && this.$route.path !== '/login') {
                this.$router.push('/login')
            }
        },
        logout() {
            AuthService.logout()
            this.$router.push('/login')
        }
    }
}
</script>

<style>
#app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

.nav-menu {
    display: flex;
    justify-content: flex-end;
    padding: 10px 0;
    margin-bottom: 20px;
    border-bottom: 1px solid #eee;
}

.nav-menu a {
    margin-left: 15px;
    color: #2c3e50;
    text-decoration: none;
    cursor: pointer;
}

.nav-menu a.router-link-active {
    color: hsla(160, 100%, 37%, 1);
    font-weight: bold;
}

.logout-link {
    color: #666;
}

main {
    padding: 20px 0;
}
</style>
