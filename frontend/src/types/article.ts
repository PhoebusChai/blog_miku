export interface Tag {
  id: number
  name: string
  slug: string
  articleCount?: number
}

export interface Author {
  id: number
  name: string
  avatar: string
}

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
  status?: number
  isTop?: number
  publishedAt?: string
  createdAt?: string
  updatedAt?: string
  author?: Author
  tags?: Tag[]
}

export interface ArticleListParams {
  page?: number
  pageSize?: number
  tag?: string
}

export interface ArticleListResponse {
  articles: Article[]
  total: number
}

