import request from './request'
import type { Article, ArticleListParams, ArticleListResponse } from '@/types/article'

export function getArticleList(params: ArticleListParams) {
  return request.get<any, ArticleListResponse>('/articles', { params })
}

export function getArticleDetail(id: number) {
  return request.get<any, Article>(`/articles/${id}`)
}
