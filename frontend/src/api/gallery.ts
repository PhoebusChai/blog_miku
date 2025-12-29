import request from './request'

export interface Gallery {
  id?: number
  title?: string
  description?: string
  imageUrl: string
  thumbnailUrl?: string
  category?: string
  sortOrder?: number
  status?: number
  createdAt?: string
  updatedAt?: string
}

/**
 * 获取可见图片（公开）
 */
export function getVisibleGallery() {
  return request<Gallery[]>({
    url: '/gallery',
    method: 'get'
  })
}

/**
 * 获取所有图片（管理员）
 */
export function getAdminGallery(category?: string) {
  return request<Gallery[]>({
    url: '/gallery/admin/list',
    method: 'get',
    params: category ? { category } : undefined
  })
}

/**
 * 获取图片详情
 */
export function getGalleryById(id: number) {
  return request<Gallery>({
    url: `/gallery/${id}`,
    method: 'get'
  })
}

/**
 * 获取所有分类
 */
export function getGalleryCategories() {
  return request<string[]>({
    url: '/gallery/categories',
    method: 'get'
  })
}

/**
 * 获取图片统计（管理员）
 */
export function getGalleryStats() {
  return request<{
    total: number
    categories: number
  }>({
    url: '/gallery/admin/stats',
    method: 'get'
  })
}

/**
 * 创建图片记录（管理员）
 */
export function createGallery(data: Partial<Gallery>) {
  return request<Gallery>({
    url: '/gallery',
    method: 'post',
    data
  })
}

/**
 * 更新图片信息（管理员）
 */
export function updateGallery(id: number, data: Partial<Gallery>) {
  return request<void>({
    url: `/gallery/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除图片（管理员）
 */
export function deleteGallery(id: number) {
  return request<void>({
    url: `/gallery/${id}`,
    method: 'delete'
  })
}
