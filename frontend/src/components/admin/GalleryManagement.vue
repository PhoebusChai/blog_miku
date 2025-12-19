<template>
  <div class="gallery-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-bar-left">
        <div class="stats-info">
          <span class="stat-label">图片总数：</span>
          <span class="stat-value">{{ images.length }}</span>
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
          />
        </div>
        <select v-model="categoryFilter" class="filter-select">
          <option value="">全部分类</option>
          <option value="头像">头像</option>
          <option value="横幅">横幅</option>
          <option value="文章配图">文章配图</option>
        </select>
      </div>
      <div class="action-bar-right">
        <button class="primary-btn" @click="handleUpload">
          <Upload :size="18" />
          <span>上传图片</span>
        </button>
      </div>
    </div>

    <!-- 图片网格 -->
    <div class="gallery-grid-wrapper">
      <div class="gallery-grid">
        <div v-for="image in filteredImages" :key="image.id" class="image-card">
          <div class="image-preview">
            <img :src="image.imageUrl" :alt="image.title" />
            <div class="image-overlay">
              <button class="overlay-btn" title="查看" @click="handleView(image.id)">
                <Eye :size="18" />
              </button>
              <button class="overlay-btn" title="复制链接" @click="handleCopy(image.imageUrl)">
                <Copy :size="18" />
              </button>
              <button class="overlay-btn" title="删除" @click="handleDelete(image.id)">
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Search, Upload, Eye, Copy, Trash2, Image } from 'lucide-vue-next'

// Emits
const emit = defineEmits<{
  upload: []
  view: [id: number]
  copy: [url: string]
  delete: [id: number]
}>()

// 状态
const searchKeyword = ref('')
const categoryFilter = ref('')

// Mock 数据
const images = ref([
  {
    id: 1,
    title: '个人头像',
    description: '用于个人资料的头像图片',
    imageUrl: 'https://via.placeholder.com/300',
    thumbnailUrl: 'https://via.placeholder.com/150',
    category: '头像',
    status: 1, // 0-隐藏，1-显示，2-删除
    createdAt: '2024-12-19'
  },
  {
    id: 2,
    title: '网站横幅',
    description: '首页展示的横幅图片',
    imageUrl: 'https://via.placeholder.com/800x400',
    thumbnailUrl: 'https://via.placeholder.com/400x200',
    category: '横幅',
    status: 1,
    createdAt: '2024-12-18'
  },
  {
    id: 3,
    title: '文章封面',
    description: '博客文章的封面配图',
    imageUrl: 'https://via.placeholder.com/600x400',
    thumbnailUrl: 'https://via.placeholder.com/300x200',
    category: '文章配图',
    status: 1,
    createdAt: '2024-12-17'
  }
])

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

// 方法
function handleUpload() {
  emit('upload')
}

function handleView(id: number) {
  emit('view', id)
}

function handleCopy(url: string) {
  navigator.clipboard.writeText(url)
  alert('图片链接已复制到剪贴板')
  emit('copy', url)
}

function handleDelete(id: number) {
  if (confirm('确定要删除这张图片吗？')) {
    emit('delete', id)
  }
}
</script>

<style scoped>
.gallery-management {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.action-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
  padding: var(--spacing-lg) 0;
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
  padding: var(--spacing-xl) 0;
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
</style>
