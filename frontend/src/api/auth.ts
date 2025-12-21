import request from './request'

export interface LoginData {
  email: string
  password: string
  remember?: boolean
}

export interface RegisterData {
  email: string
  password: string
  name: string
}

export interface RegisterWithCodeData {
  email: string
  code: string
  password: string
  name: string
}

export interface UserInfo {
  id: number
  email: string
  name: string
  avatar: string
  role?: number
  status?: number
}

export interface LoginResponse {
  token: string
  user: UserInfo
  message?: string
}

/**
 * 登录
 */
export const login = async (data: LoginData): Promise<LoginResponse> => {
  const result = await request<LoginResponse>({
    url: '/auth/login',
    method: 'post',
    data: {
      email: data.email,
      password: data.password
    }
  })

  // 保存 token 和用户信息
  if (result.token) {
    localStorage.setItem('token', result.token)
    localStorage.setItem('user', JSON.stringify(result.user))
  }

  return result
}

/**
 * 注册（不需要验证码）
 */
export const register = async (data: RegisterData): Promise<void> => {
  await request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

/**
 * 使用验证码注册
 */
export const registerWithCode = async (data: RegisterWithCodeData): Promise<void> => {
  await request({
    url: '/auth/register-with-code',
    method: 'post',
    data
  })
}

/**
 * 登出
 */
export const logout = async (): Promise<void> => {
  try {
    await request({
      url: '/auth/logout',
      method: 'post'
    })
  } finally {
    // 无论请求是否成功，都清除本地数据
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
}

/**
 * 获取当前用户信息
 */
export const getCurrentUser = async (): Promise<UserInfo | null> => {
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')

  if (!token || !userStr) {
    return null
  }

  try {
    // 验证 token 是否有效
    const user = await request<UserInfo>({
      url: '/auth/current',
      method: 'get'
    })
    
    // 更新本地存储的用户信息
    localStorage.setItem('user', JSON.stringify(user))
    
    return user
  } catch (error) {
    // token 无效，清除本地数据
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    return null
  }
}
