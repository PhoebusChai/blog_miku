import request from './request'

export interface Comment {
  id?: number
  articleId?: number
  userId?: number
  parentId?: number
  guestName?: string
  guestEmail?: string
  content: string
  ipAddress?: string
  likeCount?: number
  status?: number
  createdAt?: string
  updatedAt?: string
  // 关联数据
  userName?: string
  userAvatar?: string
}

export interface CommentListResponse {
  comments: Comment[]
  total: number
}

/**
 * 获取文章评论列表
 */
export function getArticleComments(articleId: number) {
  return request<CommentListResponse>({
    url: `/comments/article/${articleId}`,
    method: 'get'
  })
}

/**
 * 获取留言板评论列表
 */
export function getGuestbookComments() {
  return request<CommentListResponse>({
    url: '/comments/guestbook',
    method: 'get'
  })
}

/**
 * 创建评论
 */
export function createComment(data: Partial<Comment>) {
  return request<Comment>({
    url: '/comments',
    method: 'post',
    data
  })
}

/**
 * 点赞评论
 */
export function likeComment(id: number) {
  return request({
    url: `/comments/${id}/like`,
    method: 'post'
  })
}

/**
 * 取消点赞
 */
export function unlikeComment(id: number) {
  return request({
    url: `/comments/${id}/unlike`,
    method: 'post'
  })
}

/**
 * 删除评论
 */
export function deleteComment(id: number) {
  return request({
    url: `/comments/${id}`,
    method: 'delete'
  })
}

/**
 * 获取所有评论（管理员）
 */
export function getAdminComments(params: {
  page?: number
  pageSize?: number
  status?: number
  articleId?: number
  keyword?: string
}) {
  return request<{
    records: any[]
    total: number
    page: number
    pageSize: number
  }>({
    url: '/comments/admin/list',
    method: 'get',
    params
  })
}

/**
 * 获取评论统计（管理员）
 */
export function getCommentStats() {
  return request<{
    total: number
    approved: number
    pending: number
    rejected: number
    spam: number
  }>({
    url: '/comments/admin/stats',
    method: 'get'
  })
}

/**
 * 审核评论 - 通过
 */
export function approveComment(id: number) {
  return request<void>({
    url: `/comments/${id}/approve`,
    method: 'put'
  })
}

/**
 * 审核评论 - 拒绝
 */
export function rejectComment(id: number) {
  return request<void>({
    url: `/comments/${id}/reject`,
    method: 'put'
  })
}

/**
 * 标记为垃圾评论
 */
export function markCommentAsSpam(id: number) {
  return request<void>({
    url: `/comments/${id}/spam`,
    method: 'put'
  })
}

/**
 * 批量删除评论
 */
export function batchDeleteComments(ids: number[]) {
  return request<void>({
    url: '/comments/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 批量审核评论
 */
export function batchApproveComments(ids: number[]) {
  return request<void>({
    url: '/comments/batch/approve',
    method: 'put',
    data: ids
  })
}

/**
 * 批量标记为垃圾评论
 */
export function batchMarkCommentsAsSpam(ids: number[]) {
  return request<void>({
    url: '/comments/batch/spam',
    method: 'put',
    data: ids
  })
}
