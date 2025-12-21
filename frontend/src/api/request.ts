import axios, { AxiosRequestConfig } from 'axios'
import type { ApiResponse } from '@/types/api'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data as ApiResponse
    
    // 如果返回的状态码不是 200，则认为是错误
    if (res.code !== 200) {
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    // 返回data字段的内容
    return res.data
  },
  error => {
    console.error('请求失败:', error)
    
    // 处理 401 未授权
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/'
    }
    
    const message = error.response?.data?.message || error.message || '请求失败'
    return Promise.reject(new Error(message))
  }
)

// 导出类型化的请求函数
export default <T = any>(config: AxiosRequestConfig): Promise<T> => {
  return request(config)
}

