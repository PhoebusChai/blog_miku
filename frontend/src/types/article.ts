export interface Article {
  id: number
  title: string
  summary: string
  content: string
  author: string
  tags: string[]
  createdAt: string
  updatedAt: string
}

export interface ArticleListParams {
  page: number
  pageSize: number
  tag?: string
}

export interface ArticleListResponse {
  articles: Article[]
  total: number
}
