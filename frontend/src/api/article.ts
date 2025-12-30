import request from './request'

export interface Article {
  id?: number
  title: string
  summary?: string
  content: string
  coverImage?: string
  authorId?: number
  viewCount?: number
  likeCount?: number
  commentCount?: number
  status: number
  isTop?: number
  publishedAt?: string
  createdAt?: string
  updatedAt?: string
  author?: {
    id: number
    name: string
    avatar: string
  }
  tags?: Array<{
    id: number
    name: string
    slug: string
  }>
}

export interface ArticleListParams {
  page?: number
  pageSize?: number
}

export interface PageResult<T> {
  total: number
  records: T[]
}

/**
 * 获取文章列表
 */
export function getArticleList(params: ArticleListParams = {}) {
  return request<PageResult<Article>>({
    url: '/articles',
    method: 'get',
    params
  })
}

/**
 * 获取我的文章列表
 */
export function getMyArticles(params: ArticleListParams = {}) {
  return request<PageResult<Article>>({
    url: '/articles/my',
    method: 'get',
    params
  })
}

/**
 * 获取文章详情
 */
export function getArticleDetail(id: number) {
  return request<Article>({
    url: `/articles/${id}`,
    method: 'get'
  })
}

/**
 * 创建文章
 */
export function createArticle(data: Partial<Article>) {
  return request<Article>({
    url: '/articles',
    method: 'post',
    data
  })
}

/**
 * 更新文章
 */
export function updateArticle(id: number, data: Partial<Article>) {
  return request<Article>({
    url: `/articles/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除文章
 */
export function deleteArticle(id: number) {
  return request<void>({
    url: `/articles/${id}`,
    method: 'delete'
  })
}

/**
 * 获取文章发布热力图数据
 */
export function getArticleHeatmap() {
  return request<Record<string, number>>({
    url: '/articles/heatmap',
    method: 'get'
  })
}

/**
 * 获取网站公开统计数据
 */
export function getPublicStats() {
  return request<{ articles: number; views: number; likes: number; comments: number }>({
    url: '/articles/stats/public',
    method: 'get'
  })
}

/**
 * 获取文章排行榜
 */
export function getArticleRanking(limit: number = 10) {
  return request<Array<{ id: number; title: string; viewCount: number }>>({
    url: '/articles/ranking',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取精选文章
 * 优先返回置顶文章，不足时用浏览数最多的文章补充
 */
export function getFeaturedArticles(limit: number = 3) {
  return request<Article[]>({
    url: '/articles/featured',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取归档文章列表
 * 支持搜索、标签筛选、排序
 */
export function getArchiveArticles(params: {
  keyword?: string
  tagIds?: number[]
  sortBy?: 'date-desc' | 'date-asc' | 'views' | 'likes'
} = {}) {
  return request<Article[]>({
    url: '/articles/archive',
    method: 'get',
    params
  })
}

/**
 * 获取文章状态（点赞、收藏）
 */
export function getArticleStatus(id: number) {
  return request<{ isLiked: boolean; isBookmarked: boolean }>({
    url: `/articles/${id}/status`,
    method: 'get'
  })
}

/**
 * 点赞文章
 */
export function likeArticle(id: number) {
  return request<void>({
    url: `/articles/${id}/like`,
    method: 'post'
  })
}

/**
 * 取消点赞文章
 */
export function unlikeArticle(id: number) {
  return request<void>({
    url: `/articles/${id}/like`,
    method: 'delete'
  })
}

/**
 * 收藏文章
 */
export function bookmarkArticle(id: number) {
  return request<void>({
    url: `/articles/${id}/bookmark`,
    method: 'post'
  })
}

/**
 * 取消收藏文章
 */
export function unbookmarkArticle(id: number) {
  return request<void>({
    url: `/articles/${id}/bookmark`,
    method: 'delete'
  })
}

/**
 * 获取所有标签
 */
export function getAllTags() {
  return request<Array<{ id: number; name: string; slug: string; articleCount: number }>>({
    url: '/tags',
    method: 'get'
  })
}

/**
 * 创建标签
 */
export function createTag(data: { name: string; slug?: string }) {
  return request({
    url: '/tags',
    method: 'post',
    data
  })
}

/**
 * 更新标签
 */
export function updateTag(id: number, data: { name: string; slug?: string }) {
  return request({
    url: `/tags/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除标签
 */
export function deleteTag(id: number) {
  return request({
    url: `/tags/${id}`,
    method: 'delete'
  })
}

/**
 * 上传图片
 */
export function uploadImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request<{ url: string }>({
    url: '/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
