import request from './request'

export interface FriendLink {
  id?: number
  name: string
  url: string
  avatar?: string
  description?: string
  status?: number
  sortOrder?: number
  createdAt?: string
  updatedAt?: string
}

/**
 * 获取已通过的友链（公开）
 */
export function getApprovedLinks() {
  return request<FriendLink[]>({
    url: '/links',
    method: 'get'
  })
}

/**
 * 获取所有友链（管理员）
 */
export function getAdminLinks(status?: number) {
  return request<FriendLink[]>({
    url: '/links/admin/list',
    method: 'get',
    params: status !== undefined ? { status } : undefined
  })
}

/**
 * 获取友链详情
 */
export function getLinkById(id: number) {
  return request<FriendLink>({
    url: `/links/${id}`,
    method: 'get'
  })
}

/**
 * 获取友链统计（管理员）
 */
export function getLinkStats() {
  return request<{
    total: number
    pending: number
    approved: number
    rejected: number
  }>({
    url: '/links/admin/stats',
    method: 'get'
  })
}

/**
 * 创建友链（管理员）
 */
export function createLink(data: Partial<FriendLink>) {
  return request<FriendLink>({
    url: '/links',
    method: 'post',
    data
  })
}

/**
 * 申请友链（公开）
 */
export function applyLink(data: Partial<FriendLink>) {
  return request<FriendLink>({
    url: '/links/apply',
    method: 'post',
    data
  })
}

/**
 * 更新友链（管理员）
 */
export function updateLink(id: number, data: Partial<FriendLink>) {
  return request<void>({
    url: `/links/${id}`,
    method: 'put',
    data
  })
}

/**
 * 审核通过友链（管理员）
 */
export function approveLink(id: number) {
  return request<void>({
    url: `/links/${id}/approve`,
    method: 'put'
  })
}

/**
 * 拒绝友链（管理员）
 */
export function rejectLink(id: number) {
  return request<void>({
    url: `/links/${id}/reject`,
    method: 'put'
  })
}

/**
 * 删除友链（管理员）
 */
export function deleteLink(id: number) {
  return request<void>({
    url: `/links/${id}`,
    method: 'delete'
  })
}
