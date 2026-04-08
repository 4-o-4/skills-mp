import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api'
import type { User } from '@/types'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))
  const isAuthenticated = computed(() => !!token.value)

  async function login(username: string, password: string) {
    try {
      const { data } = await authApi.login(username, password)
      token.value = data.token
      user.value = data.user
      localStorage.setItem('token', data.token)
      ElMessage.success('Logged in successfully')
      router.push('/')
    } catch (e: any) {
      ElMessage.error(e.response?.data?.message || 'Login failed')
      throw e
    }
  }

  async function register(payload: { username: string; email: string; password: string; role: string }) {
    try {
      const { data } = await authApi.register(payload)
      token.value = data.token
      user.value = data.user
      localStorage.setItem('token', data.token)
      ElMessage.success('Registration successful')
      router.push('/')
    } catch (e: any) {
      ElMessage.error(e.response?.data?.message || 'Registration failed')
      throw e
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    router.push('/login')
  }

  async function fetchMe() {
    if (!token.value) return
    try {
      const { data } = await authApi.getMe()
      user.value = data
    } catch {
      logout()
    }
  }

  return { user, token, isAuthenticated, login, register, logout, fetchMe }
})
