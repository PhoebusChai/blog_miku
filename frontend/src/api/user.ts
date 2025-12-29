import request from './request'

export interface User {
  id?: number
  email: string
  password?: string
  name: string
  avatar?: string
  role?: number
  status?: number
  createdAt?: string
  updatedAt?: string
  lastLoginAt?: string
}

/**
 * 获取所有用户（管理员）
 */
export function getAdminUsers(params?: { role?: number; status?: number }) {
  return request<User[]>({
    url: '/users/admin/list',
    method: 'get',
    params
  })
}

/**
 * 获取用户详情（管理员）
 */
export function getAdminUserById(id: number) {
  return request<User>({
    url: `/users/admin/${id}`,
    method: 'get'
  })
}

/**
 * 获取用户统计（管理员）
 */
export function getUserStats() {
  return request<{
    total: number
    admins: number
    users: number
    active: number
    disabled: number
  }>({
    url: '/users/admin/stats',
    method: 'get'
  })
}

/**
 * 创建用户（管理员）
 */
export function createUser(data: Partial<User>) {
  return request<User>({
    url: '/users/admin',
    method: 'post',
    data
  })
}

/**
 * 更新用户（管理员）
 */
export function updateUser(id: number, data: Partial<User>) {
  return request<void>({
    url: `/users/admin/${id}`,
    method: 'put',
    data
  })
}

/**
 * 启用用户（管理员）
 */
export function enableUser(id: number) {
  return request<void>({
    url: `/users/admin/${id}/enable`,
    method: 'put'
  })
}

/**
 * 禁用用户（管理员）
 */
export function disableUser(id: number) {
  return request<void>({
    url: `/users/admin/${id}/disable`,
    method: 'put'
  })
}

/**
 * 删除用户（管理员）
 */
export function deleteUser(id: number) {
  return request<void>({
    url: `/users/admin/${id}`,
    method: 'delete'
  })
}
