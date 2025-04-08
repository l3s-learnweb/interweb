import {createRouter, createWebHistory} from 'vue-router'
import LoginView from '../views/LoginView.vue'
import ApiKeysView from '../views/ApiKeysView.vue'
import AuthService from '../services/AuthService'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        component: LoginView
    },
    {
        path: '/api-keys',
        component: ApiKeysView,
        meta: {requiresAuth: true}
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    if (to.meta.requiresAuth && !AuthService.isAuthenticated()) {
        next('/login')
    } else {
        next()
    }
})

export default router
