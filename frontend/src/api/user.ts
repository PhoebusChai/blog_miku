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

export interface ReadingHistory {
  id: number
  userId: number
  articleId: number
  isLiked: number
  isBookmarked: number
  createdAt: string
  updatedAt: string
  articleTitle?: string
  articleSummary?: string
  articleCover?: string
}

export interface UserActivity {
  commentCount: number
  likeCount: number
  bookmarkCount: number
}

// ========== 个人中心 API ==========

/**
 * 获取当前用户信息
 */
export function getCurrentUser() {
  return request<User>({
    url: '/users/me',
    method: 'get'
  })
}

/**
 * 更新当前用户信息
 */
export function updateProfile(data: { name?: string; avatar?: string }) {
  return request<User>({
    url: '/users/me',
    method: 'put',
    data
  })
}

/**
 * 修改密码
 */
export function changePassword(data: { oldPassword: string; newPassword: string }) {
  return request<void>({
    url: '/users/me/password',
    method: 'put',
    data
  })
}

/**
 * 获取用户活动统计
 */
export function getUserActivity() {
  return request<UserActivity>({
    url: '/users/me/activity',
    method: 'get'
  })
}

/**
 * 获取阅读记录
 */
export function getReadingHistory(limit = 20) {
  return request<ReadingHistory[]>({
    url: '/users/me/reading-history',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取点赞的文章
 */
export function getLikedArticles() {
  return request<ReadingHistory[]>({
    url: '/users/me/likes',
    method: 'get'
  })
}

/**
 * 获取收藏的文章
 */
export function getBookmarkedArticles() {
  return request<ReadingHistory[]>({
    url: '/users/me/bookmarks',
    method: 'get'
  })
}

// ========== 管理员 API ==========

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
