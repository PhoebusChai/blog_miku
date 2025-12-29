import request from './request'

export interface SiteConfig {
  // 基本设置
  site_name?: string
  site_description?: string
  site_keywords?: string
  site_logo?: string
  site_favicon?: string
  site_start_date?: string
  site_copyright?: string
  
  // 博主信息
  blogger_avatar?: string
  blogger_name?: string
  blogger_role?: string
  blogger_location?: string
  blogger_bio?: string
  blogger_skills?: string
  blogger_interests?: string
  
  // 社交媒体
  social_github?: string
  social_bilibili?: string
  social_email?: string
  social_twitter?: string
  social_weibo?: string
  social_zhihu?: string
  social_wechat?: string
  
  // 其他配置
  [key: string]: any
}

/**
 * 获取公开配置（无需登录）
 */
export function getPublicConfig() {
  return request<SiteConfig>({
    url: '/config/public',
    method: 'get'
  })
}

/**
 * 获取所有配置（管理员）
 */
export function getAllConfig() {
  return request<SiteConfig>({
    url: '/config',
    method: 'get'
  })
}

/**
 * 获取单个配置
 */
export function getConfig(key: string) {
  return request<any>({
    url: `/config/${key}`,
    method: 'get'
  })
}

/**
 * 批量保存配置（管理员）
 */
export function saveConfigs(configs: SiteConfig) {
  return request<void>({
    url: '/config',
    method: 'post',
    data: configs
  })
}

/**
 * 保存单个配置（管理员）
 */
export function saveConfig(key: string, value: any) {
  return request<void>({
    url: `/config/${key}`,
    method: 'put',
    data: value
  })
}

/**
 * 删除配置（管理员）
 */
export function deleteConfig(key: string) {
  return request<void>({
    url: `/config/${key}`,
    method: 'delete'
  })
}
