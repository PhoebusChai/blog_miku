<template>
  <div class="gallery-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-bar-left">
        <div class="stats-info">
          <span class="stat-label">图片总数：</span>
          <span class="stat-value">{{ stats.total }}</span>
        </div>
      </div>
      <div class="action-bar-center">
        <div class="search-box">
          <Search :size="18" />
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索图片名称..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">搜索</button>
        </div>
        <select v-model="categoryFilter" class="filter-select" @change="handleCategoryChange">
          <option value="">全部分类</option>
          <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
        </select>
      </div>
      <div class="action-bar-right">
        <div class="view-toggle">
          <button
            class="view-btn"
            :class="{ active: viewMode === 'grid' }"
            title="网格视图"
            @click="viewMode = 'grid'"
          >
            <Grid :size="18" />
          </button>
          <button
            class="view-btn"
            :class="{ active: viewMode === 'list' }"
            title="列表视图"
            @click="viewMode = 'list'"
          >
            <List :size="18" />
          </button>
        </div>
        <button class="primary-btn" @click="handleUpload">
          <Upload :size="18" />
          <span>上传图片</span>
        </button>
      </div>
    </div>

    <!-- 图片网格视图 -->
    <div v-if="viewMode === 'grid'" class="gallery-grid-wrapper">
      <div class="gallery-grid">
        <div v-for="image in paginatedImages" :key="image.id" class="image-card">
          <div class="image-preview">
            <img :src="image.imageUrl" :alt="image.title" />
            <div class="image-overlay">
              <button class="overlay-btn" title="查看" @click="handleView(image.id!)">
                <Eye :size="18" />
              </button>
              <button class="overlay-btn" title="复制链接" @click="handleCopy(image.imageUrl)">
                <Copy :size="18" />
              </button>
              <button class="overlay-btn" title="删除" @click="handleDelete(image.id!)">
                <Trash2 :size="18" />
              </button>
            </div>
          </div>
          <div class="image-info">
            <div class="image-name">{{ image.title }}</div>
            <div class="image-meta">
              <span class="image-category">{{ image.category }}</span>
              <span class="image-date">{{ image.createdAt }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredImages.length === 0" class="empty-state">
        <Image :size="48" class="empty-icon" />
        <p>暂无图片</p>
        <button class="primary-btn" @click="handleUpload">
          <Upload :size="18" />
          <span>上传第一张图片</span>
        </button>
      </div>
    </div>

    <!-- 图片列表视图 -->
    <div v-else class="gallery-list-wrapper">
      <table class="gallery-table">
        <thead>
          <tr>
            <th class="col-preview">预览</th>
            <th class="col-name">名称</th>
            <th class="col-category">分类</th>
            <th class="col-size">尺寸</th>
            <th class="col-date">上传时间</th>
            <th class="col-actions">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="image in paginatedImages" :key="image.id" class="image-row">
            <td class="col-preview">
              <div class="preview-thumbnail">
                <img :src="image.thumbnailUrl" :alt="image.title" />
              </div>
            </td>
            <td class="col-name">
              <div class="name-cell">
                <div class="image-title">{{ image.title }}</div>
                <div class="image-description">{{ image.description }}</div>
              </div>
            </td>
            <td class="col-category">
              <span class="category-badge">{{ image.category }}</span>
            </td>
            <td class="col-size">{{ (image as any).size || '未知' }}</td>
            <td class="col-date">{{ image.createdAt }}</td>
            <td class="col-actions">
              <div class="actions-cell">
                <button class="action-icon-btn" title="查看" @click="handleView(image.id!)">
                  <Eye :size="16" />
                </button>
                <button
                  class="action-icon-btn"
                  title="复制链接"
                  @click="handleCopy(image.imageUrl)"
                >
                  <Copy :size="16" />
                </button>
                <button class="action-icon-btn" title="删除" @click="handleDelete(image.id!)">
                  <Trash2 :size="16" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-if="filteredImages.length === 0" class="empty-state">
        <Image :size="48" class="empty-icon" />
        <p>暂无图片</p>
        <button class="primary-btn" @click="handleUpload">
          <Upload :size="18" />
          <span>上传第一张图片</span>
        </button>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="filteredImages.length > 0" class="pagination">
      <div class="pagination-info">共 {{ filteredImages.length }} 条，每页 {{ pageSize }} 条</div>
      <div class="pagination-controls">
        <button class="pagination-btn" :disabled="currentPage === 1" @click="currentPage--">
          <ChevronLeft :size="16" />
        </button>
        <button
          v-for="page in displayPages"
          :key="page"
          class="pagination-btn"
          :class="{ active: page === currentPage }"
          :disabled="typeof page === 'string'"
          @click="typeof page === 'number' && (currentPage = page)"
        >
          {{ page }}
        </button>
        <button
          class="pagination-btn"
          :disabled="currentPage === totalPages"
          @click="currentPage++"
        >
          <ChevronRight :size="16" />
        </button>
      </div>
    </div>

    <!-- 图片上传弹窗 -->
    <ImageUploadModal :visible="showModal" @close="showModal = false" @submit="handleModalSubmit" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  Search,
  Upload,
  Eye,
  Copy,
  Trash2,
  Image,
  Grid,
  List,
  ChevronLeft,
  ChevronRight
} from 'lucide-vue-next'
import ImageUploadModal from './ImageUploadModal.vue'
import {
  getAdminGallery,
  getGalleryCategories,
  getGalleryStats,
  createGallery,
  deleteGallery,
  type Gallery
} from '@/api/gallery'
import { message } from '@/utils/message'

// 状态
const searchKeyword = ref('')
const categoryFilter = ref('')
const viewMode = ref<'grid' | 'list'>('grid')
const currentPage = ref(1)
const pageSize = ref(12)
const showModal = ref(false)
const loading = ref(false)
const categories = ref<string[]>([])
const stats = ref({ total: 0, categories: 0 })

// 图片数据
const images = ref<Gallery[]>([])

// 加载图片列表
async function loadImages() {
  loading.value = true
  try {
    const res = await getAdminGallery(categoryFilter.value || undefined)
    images.value = res || []
  } catch (error) {
    console.error('加载图片失败:', error)
    message.error('加载图片失败')
  } finally {
    loading.value = false
  }
}

// 加载分类列表
async function loadCategories() {
  try {
    const res = await getGalleryCategories()
    categories.value = res || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

// 加载统计数据
async function loadStats() {
  try {
    const res = await getGalleryStats()
    stats.value = res || { total: 0, categories: 0 }
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

// 初始化
onMounted(() => {
  loadImages()
  loadCategories()
  loadStats()
})

// 筛选图片
const filteredImages = computed(() => {
  let result = images.value

  if (categoryFilter.value) {
    result = result.filter((img) => img.category === categoryFilter.value)
  }

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(
      (img) =>
        (img.title && img.title.toLowerCase().includes(keyword)) ||
        (img.description && img.description.toLowerCase().includes(keyword))
    )
  }

  return result
})

// 分页
const totalPages = computed(() => Math.ceil(filteredImages.value.length / pageSize.value))

const paginatedImages = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredImages.value.slice(start, end)
})

const displayPages = computed(() => {
  const pages: (number | string)[] = []
  const total = totalPages.value
  const current = currentPage.value

  if (total <= 7) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 4) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    } else if (current >= total - 3) {
      pages.push(1)
      pages.push('...')
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      pages.push(1)
      pages.push('...')
      for (let i = current - 1; i <= current + 1; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    }
  }

  return pages
})

// 方法
function handleSearch() {
  currentPage.value = 1
}

function handleCategoryChange() {
  currentPage.value = 1
  loadImages()
}

function handleUpload() {
  showModal.value = true
}

function handleView(id: number) {
  const image = images.value.find((img) => img.id === id)
  if (image) {
    window.open(image.imageUrl, '_blank')
  }
}

async function handleCopy(url: string) {
  try {
    await navigator.clipboard.writeText(url)
    message.success('图片链接已复制到剪贴板')
  } catch {
    message.error('复制失败')
  }
}

async function handleDelete(id: number) {
  if (confirm('确定要删除这张图片吗？')) {
    try {
      await deleteGallery(id)
      message.success('删除成功')
      loadImages()
      loadStats()
    } catch (error) {
      console.error('删除失败:', error)
      message.error('删除失败')
    }
  }
}

async function handleModalSubmit(data: any) {
  try {
    await createGallery({
      title: data.title || '未命名图片',
      description: data.description,
      imageUrl: data.imageUrl,
      thumbnailUrl: data.thumbnailUrl || data.imageUrl,
      category: data.category || '其他',
      status: data.status ?? 1
    })
    message.success('上传成功')
    showModal.value = false
    loadImages()
    loadStats()
    loadCategories()
  } catch (error) {
    console.error('上传失败:', error)
    message.error('上传失败')
  }
}
</script>

<style scoped>
.gallery-management {
  display: flex;
  flex-direction: column;
  flex: 1;
  width: 100%;
  min-height: 0;
}

.action-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
  padding: var(--spacing-lg) 2%;
  background: var(--color-white);
  border-bottom: 1px solid var(--color-gray-200);
}

.action-bar-left {
  flex-shrink: 0;
}

.action-bar-center {
  flex: 1;
  display: flex;
  gap: var(--spacing-md);
}

.action-bar-right {
  flex-shrink: 0;
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
}

.stats-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-lg);
  background: var(--color-miku-50);
  border-radius: 8px;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
}

.stat-value {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--color-miku-600);
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--color-gray-50);
  border: 1px solid var(--color-gray-200);
  border-radius: 6px;
  transition: all var(--transition-fast);
}

.search-box:focus-within {
  border-color: var(--color-miku-500);
  box-shadow: 0 0 0 3px rgba(57, 197, 187, 0.1);
}

.search-box svg {
  color: var(--color-gray-400);
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: var(--text-sm);
  color: var(--color-gray-900);
  background: transparent;
}

.search-input::placeholder {
  color: var(--color-gray-400);
}

.search-btn {
  padding: var(--spacing-xs) var(--spacing-md);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-white);
  background: var(--color-miku-500);
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all var(--transition-fast);
  white-space: nowrap;
  flex-shrink: 0;
}

.search-btn:hover {
  background: var(--color-miku-600);
}

.filter-select {
  min-width: 140px;
  padding: var(--spacing-sm) var(--spacing-md);
  font-size: var(--text-sm);
  color: var(--color-gray-700);
  background: var(--color-gray-50);
  border: 1px solid var(--color-gray-200);
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.filter-select:focus {
  outline: none;
  border-color: var(--color-miku-500);
  box-shadow: 0 0 0 3px rgba(57, 197, 187, 0.1);
}

.view-toggle {
  display: flex;
  background: var(--color-white);
  border: 1px solid var(--color-gray-200);
  border-radius: 6px;
  overflow: hidden;
}

.view-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  color: var(--color-gray-600);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.view-btn:hover {
  background: var(--color-gray-50);
}

.view-btn.active {
  background: var(--color-miku-500);
  color: var(--color-white);
}

.view-btn:not(:last-child) {
  border-right: 1px solid var(--color-gray-200);
}

.primary-btn {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-white);
  background: linear-gradient(135deg, var(--color-miku-500), var(--color-cyan-500));
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.3);
}

.gallery-grid-wrapper {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-xl) 2%;
}

.gallery-grid-wrapper::-webkit-scrollbar {
  width: 6px;
}

.gallery-grid-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.gallery-grid-wrapper::-webkit-scrollbar-thumb {
  background: var(--color-gray-300);
  border-radius: 3px;
}

.gallery-grid-wrapper::-webkit-scrollbar-thumb:hover {
  background: var(--color-gray-400);
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: var(--spacing-xl);
}

.image-card {
  background: var(--color-white);
  border: 1px solid var(--color-gray-200);
  border-radius: 12px;
  overflow: hidden;
  transition: all var(--transition-fast);
}

.image-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.image-preview {
  position: relative;
  width: 100%;
  padding-top: 75%;
  background: var(--color-gray-100);
  overflow: hidden;
}

.image-preview img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  background: rgba(0, 0, 0, 0.6);
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.image-card:hover .image-overlay {
  opacity: 1;
}

.overlay-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-white);
  border: none;
  border-radius: 8px;
  color: var(--color-gray-700);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.overlay-btn:hover {
  background: var(--color-miku-500);
  color: var(--color-white);
  transform: scale(1.1);
}

.image-info {
  padding: var(--spacing-md);
}

.image-name {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-xs);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.image-meta {
  display: flex;
  justify-content: space-between;
  font-size: var(--text-xs);
  color: var(--color-gray-600);
}

.image-category {
  padding: 2px 8px;
  background: var(--color-miku-50);
  color: var(--color-miku-700);
  border-radius: 4px;
}

/* 列表视图样式 */
.gallery-list-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow-y: auto;
}

.gallery-list-wrapper::-webkit-scrollbar {
  width: 6px;
}

.gallery-list-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.gallery-list-wrapper::-webkit-scrollbar-thumb {
  background: var(--color-gray-300);
  border-radius: 3px;
}

.gallery-list-wrapper::-webkit-scrollbar-thumb:hover {
  background: var(--color-gray-400);
}

.gallery-table {
  width: 100%;
  border-collapse: collapse;
  background: var(--color-white);
}

.gallery-table thead {
  position: sticky;
  top: 0;
  background: var(--color-gray-50);
  z-index: 10;
}

.gallery-table th {
  padding: var(--spacing-md) var(--spacing-lg);
  text-align: left;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-700);
  border-bottom: 2px solid var(--color-gray-200);
  white-space: nowrap;
}

.gallery-table th:first-child {
  padding-left: var(--spacing-lg);
}

.gallery-table th:last-child {
  padding-right: var(--spacing-lg);
}

.gallery-table td {
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--color-gray-100);
  vertical-align: middle;
}

.gallery-table td:first-child {
  padding-left: var(--spacing-lg);
}

.gallery-table td:last-child {
  padding-right: var(--spacing-lg);
}

.image-row:hover {
  background: var(--color-gray-50);
}

.col-preview {
  width: 80px;
}

.col-name {
  width: 35%;
}

.col-category {
  width: 12%;
}

.col-size {
  width: 12%;
}

.col-date {
  width: 15%;
}

.col-actions {
  width: 10%;
}

.preview-thumbnail {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  background: var(--color-gray-100);
}

.preview-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.name-cell {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.image-title {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
}

.image-description {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.category-badge {
  display: inline-block;
  padding: 4px 12px;
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  background: var(--color-miku-50);
  color: var(--color-miku-700);
  border-radius: 12px;
  white-space: nowrap;
}

.col-size,
.col-date {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
}

.actions-cell {
  display: flex;
  gap: var(--spacing-xs);
}

.action-icon-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: 6px;
  color: var(--color-gray-600);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.action-icon-btn:hover {
  background: var(--color-miku-100);
  color: var(--color-miku-600);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl);
  text-align: center;
  color: var(--color-gray-500);
}

.empty-icon {
  margin-bottom: var(--spacing-lg);
  opacity: 0.5;
}

.empty-state p {
  margin: 0 0 var(--spacing-xl) 0;
  font-size: var(--text-base);
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-lg) 2%;
  background: var(--color-white);
  border-top: 1px solid var(--color-gray-200);
}

.pagination-info {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
}

.pagination-controls {
  display: flex;
  gap: var(--spacing-xs);
}

.pagination-btn {
  min-width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 var(--spacing-sm);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-700);
  background: var(--color-white);
  border: 1px solid var(--color-gray-200);
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.pagination-btn:hover:not(:disabled):not(.active) {
  border-color: var(--color-miku-500);
  color: var(--color-miku-600);
}

.pagination-btn.active {
  background: var(--color-miku-500);
  border-color: var(--color-miku-500);
  color: var(--color-white);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
