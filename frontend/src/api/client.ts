import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const client = axios.create({
  baseURL: '',
  headers: { 'Content-Type': 'application/json' }
})

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
      ElMessage.error('Session expired. Please log in again.')
    }
    return Promise.reject(error)
  }
)

export default client
