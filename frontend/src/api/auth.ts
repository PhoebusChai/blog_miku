// Mock 用户数据
const mockUsers = [
  {
    id: 1,
    email: 'admin@example.com',
    password: '123456',
    name: '管理员',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin',
    role: 'admin'
  },
  {
    id: 2,
    email: 'user@example.com',
    password: '123456',
    name: '普通用户',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=user',
    role: 'user'
  }
]

export interface LoginData {
  email: string
  password: string
  remember?: boolean
}

export interface UserInfo {
  id: number
  email: string
  name: string
  avatar: string
  role?: string
}

// Mock 登录接口
export const login = async (data: LoginData): Promise<{ token: string; user: UserInfo }> => {
  // 模拟网络延迟
  await new Promise(resolve => setTimeout(resolve, 1000))

  const user = mockUsers.find(u => u.email === data.email && u.password === data.password)

  if (!user) {
    throw new Error('邮箱或密码错误')
  }

  // 生成 mock token
  const token = `mock_token_${user.id}_${Date.now()}`

  // 如果选择记住我，保存到 localStorage
  if (data.remember) {
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify({
      id: user.id,
      email: user.email,
      name: user.name,
      avatar: user.avatar,
      role: user.role
    }))
  }

  return {
    token,
    user: {
      id: user.id,
      email: user.email,
      name: user.name,
      avatar: user.avatar,
      role: user.role
    }
  }
}

// Mock 登出接口
export const logout = async (): Promise<void> => {
  await new Promise(resolve => setTimeout(resolve, 500))
  localStorage.removeItem('token')
  localStorage.removeItem('user')
}

// Mock 获取当前用户信息
export const getCurrentUser = async (): Promise<UserInfo | null> => {
  await new Promise(resolve => setTimeout(resolve, 300))
  
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')
  
  if (!token || !userStr) {
    return null
  }

  return JSON.parse(userStr)
}
