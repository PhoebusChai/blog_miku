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
    try {
      await logout()
    } finally {
      user.value = null
      token.value = ''
      isLoggedIn.value = false
    }
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
      // 清除无效的登录状态
      user.value = null
      token.value = ''
      isLoggedIn.value = false
    }
  }

  // 检查是否是管理员
  const isAdmin = () => {
    return user.value?.role === 1
  }

  // 设置用户信息
  const setUser = (newUser: UserInfo) => {
    user.value = newUser
  }

  return {
    user,
    token,
    isLoggedIn,
    loginUser,
    logoutUser,
    initUser,
    isAdmin,
    setUser
  }
})
