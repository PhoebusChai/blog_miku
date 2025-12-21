import request from '@/utils/request'

export interface Tag {
  id?: number
  name: string
  slug: string
  articleCount?: number
  createdAt?: string
  updatedAt?: string
}

export interface TagFormData {
  name: string
  slug: string
}

// 获取所有标签
export const getTags = () => {
  return request<Tag[]>({
    url: '/tags',
    method: 'get'
  })
}

// 获取单个标签
export const getTagById = (id: number) => {
  return request<Tag>({
    url: `/tags/${id}`,
    method: 'get'
  })
}

// 创建标签
export const createTag = (data: TagFormData) => {
  return request<Tag>({
    url: '/tags',
    method: 'post',
    data
  })
}

// 更新标签
export const updateTag = (id: number, data: TagFormData) => {
  return request<Tag>({
    url: `/tags/${id}`,
    method: 'put',
    data
  })
}

// 删除标签
export const deleteTag = (id: number) => {
  return request<void>({
    url: `/tags/${id}`,
    method: 'delete'
  })
}
