import request from './request'

export interface Project {
  id?: number
  title: string
  description?: string
  coverImage?: string
  demoUrl?: string
  githubUrl?: string
  techStack?: string
  status?: number
  sortOrder?: number
  viewCount?: number
  createdAt?: string
  updatedAt?: string
}

/**
 * 获取所有项目
 */
export function getProjects(status?: number) {
  return request<Project[]>({
    url: '/projects',
    method: 'get',
    params: status !== undefined ? { status } : undefined
  })
}

/**
 * 获取项目详情
 */
export function getProjectById(id: number) {
  return request<Project>({
    url: `/projects/${id}`,
    method: 'get'
  })
}

/**
 * 获取项目统计（管理员）
 */
export function getProjectStats() {
  return request<{
    total: number
    active: number
    completed: number
    archived: number
  }>({
    url: '/projects/admin/stats',
    method: 'get'
  })
}

/**
 * 创建项目（管理员）
 */
export function createProject(data: Partial<Project>) {
  return request<Project>({
    url: '/projects',
    method: 'post',
    data
  })
}

/**
 * 更新项目（管理员）
 */
export function updateProject(id: number, data: Partial<Project>) {
  return request<void>({
    url: `/projects/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除项目（管理员）
 */
export function deleteProject(id: number) {
  return request<void>({
    url: `/projects/${id}`,
    method: 'delete'
  })
}
