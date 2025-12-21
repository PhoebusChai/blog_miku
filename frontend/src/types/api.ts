// API 响应的统一格式
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页结果
export interface PageResult<T> {
  total: number
  records: T[]
}
