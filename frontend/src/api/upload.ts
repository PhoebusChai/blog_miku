import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

/**
 * 上传图片文件
 */
export async function uploadFile(file: File): Promise<{ url: string; filename: string; path: string }> {
  const formData = new FormData()
  formData.append('file', file)

  const token = localStorage.getItem('token')
  
  const response = await axios.post(`${baseURL}/upload/image`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
      ...(token ? { Authorization: token } : {})
    }
  })

  if (response.data.code !== 200) {
    throw new Error(response.data.message || '上传失败')
  }

  return response.data.data
}

// 别名导出
export const uploadImage = uploadFile
