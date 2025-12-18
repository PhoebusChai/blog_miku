import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, logout, getCurrentUser, type LoginData, type UserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const user = ref<UserInfo | null>(null)
  const token = ref<string>('')
  const isLoggedIn = ref(false)

  // 登录
  const loginUser = async (data: LoginData) => {
    try {
      const result = await login(data)
      token.value = result.token
      user.value = result.user
      isLoggedIn.value = true
      return result
    } catch (error) {
      throw error
    }
  }

  // 登出
  const logoutUser = async () => {
    await logout()
    user.value = null
    token.value = ''
    isLoggedIn.value = false
  }

  // 初始化用户信息（从 localStorage 恢复）
  const initUser = async () => {
    try {
      const currentUser = await getCurrentUser()
      if (currentUser) {
        user.value = currentUser
        token.value = localStorage.getItem('token') || ''
        isLoggedIn.value = true
      }
    } catch (error) {
      console.error('初始化用户信息失败:', error)
    }
  }

  return {
    user,
    token,
    isLoggedIn,
    loginUser,
    logoutUser,
    initUser
  }
})
