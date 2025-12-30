<template>
  <div class="gallery">
    <AppHeader />
    
    <main class="gallery__main">
      <!-- 页面标题 -->
      <div class="gallery-header">
        <div class="header-decoration">
          <Camera :size="32" class="header-icon" />
        </div>
        <h1 class="gallery-title">相册</h1>
        <p class="gallery-subtitle">记录生活中的美好瞬间</p>
      </div>

      <!-- 相册内容 -->
      <div class="gallery-content">
        <div v-if="initialLoading" class="loading">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
        
        <template v-else>
          <!-- 按时间分组显示 -->
          <div
            v-for="(group, period) in groupedPhotos"
            :key="period"
            class="time-group"
          >
            <div class="time-header">
              <span class="time-title">{{ period }}</span>
              <span class="time-count">{{ group.length }} 张</span>
            </div>

            <div class="waterfall-container">
              <!-- 瀑布流列 -->
              <div
                v-for="(column, index) in distributePhotos(group)"
                :key="index"
                class="waterfall-column"
              >
                <div
                  v-for="photo in column"
                  :key="photo.id"
                  class="photo-item"
                  @click="openPhoto(photo)"
                >
                  <div class="photo-image">
                    <img 
                      :src="photo.url" 
                      :alt="photo.title"
                      @load="onImageLoad"
                    />
                  </div>
                  <!-- 图片信息 -->
                  <div class="photo-info">
                    <div class="photo-header">
                      <h3 class="photo-title">{{ photo.title }}</h3>
                      <p class="photo-meta">{{ photo.description }}</p>
                    </div>
                    <!-- 图片文字描述 -->
                    <p v-if="photo.caption" class="photo-caption">{{ photo.caption }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>

        <!-- 加载更多状态 -->
        <div v-if="loadingMore" class="loading-more">
          <div class="loading-spinner"></div>
          <span>加载更多...</span>
        </div>

        <!-- 没有更多数据 -->
        <div v-if="!hasMore && photos.length > 0" class="no-more">
          <span>没有更多照片了 (๑•̀ㅂ•́)و✧</span>
        </div>
      </div>
    </main>
    
    <AppFooter />

    <!-- 返回顶部按钮 -->
    <transition name="fade">
      <button 
        v-if="showBackToTop" 
        class="back-to-top"
        @click="scrollToTop"
        title="返回顶部"
      >
        <ArrowUp :size="24" />
      </button>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ArrowUp, Camera } from 'lucide-vue-next'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import { getVisibleGallery, type Gallery } from '@/api/gallery'
import { message } from '@/utils/message'

const initialLoading = ref(true)
const loadingMore = ref(false)
const showBackToTop = ref(false)
const hasMore = ref(false) // 一次性加载所有数据
const columnCount = ref(3) // 瀑布流列数

interface Photo {
  id: number
  title: string
  description: string
  url: string
  aspectRatio: string
  date: string
  caption?: string
}

const photos = ref<Photo[]>([])

// 按时间分组照片
const groupedPhotos = computed(() => {
  const groups: Record<string, Photo[]> = {}
  
  photos.value.forEach(photo => {
    const date = new Date(photo.date)
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const period = `${year}年${month}月`
    
    if (!groups[period]) {
      groups[period] = []
    }
    groups[period].push(photo)
  })
  
  return groups
})

// 优化的瀑布流分配算法 - 简单均匀分配
function distributePhotos(photoList: Photo[]) {
  const cols: Photo[][] = Array.from({ length: columnCount.value }, () => [])
  
  photoList.forEach((photo, index) => {
    // 简单轮询分配到各列
    const colIndex = index % columnCount.value
    cols[colIndex].push(photo)
  })
  
  return cols
}

// 转换API数据为Photo格式
function convertGalleryToPhoto(gallery: Gallery): Photo {
  const date = gallery.createdAt ? new Date(gallery.createdAt) : new Date()
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  
  return {
    id: gallery.id || 0,
    title: gallery.title || '未命名',
    description: `${year}年${month}月 · ${gallery.category || '生活'}`,
    url: gallery.imageUrl,
    aspectRatio: 'auto', // 使用 auto 保持原始比例
    date: gallery.createdAt || new Date().toISOString(),
    caption: gallery.description
  }
}

// 加载照片数据
async function loadPhotos() {
  initialLoading.value = true
  try {
    const data = await getVisibleGallery()
    photos.value = data.map(convertGalleryToPhoto)
  } catch (error: any) {
    message.error(error.message || '加载相册失败')
  } finally {
    initialLoading.value = false
  }
}

function openPhoto(photo: Photo) {
  console.log('Open photo:', photo)
  // TODO: 实现图片查看器
}

function onImageLoad() {
  // 图片加载完成后的处理
}

// 监听滚动事件
function handleScroll() {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop

  // 显示/隐藏返回顶部按钮
  showBackToTop.value = scrollTop > 300
}

function scrollToTop() {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

// 响应式调整列数
function updateColumnCount() {
  const width = window.innerWidth
  if (width < 768) {
    columnCount.value = 2
  } else if (width < 1024) {
    columnCount.value = 3
  } else {
    columnCount.value = 4
  }
}

onMounted(async () => {
  // 初始化列数
  updateColumnCount()
  window.addEventListener('resize', updateColumnCount)
  
  // 加载数据
  await loadPhotos()

  // 添加滚动监听
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('resize', updateColumnCount)
})
</script>

<style scoped>
.gallery {
  min-height: 100vh;
  padding-top: 60px;
  overflow-x: hidden;
  background: var(--color-white);
}

.gallery__main {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-3xl) var(--spacing-2xl);
}

/* 页面标题 */
.gallery-header {
  text-align: center;
  margin-bottom: var(--spacing-3xl);
  padding: var(--spacing-3xl) 0 var(--spacing-2xl);
}

.header-decoration {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  margin-bottom: var(--spacing-lg);
  background: linear-gradient(135deg, var(--color-miku-100), var(--color-cyan-100));
  border-radius: 50%;
}

.header-icon {
  color: var(--color-miku-500);
}

.gallery-title {
  font-size: clamp(var(--text-3xl), 5vw, 48px);
  font-weight: var(--font-bold);
  margin: 0 0 var(--spacing-md) 0;
  letter-spacing: -0.02em;
  line-height: 1.2;
  background: linear-gradient(135deg, var(--color-miku-500), var(--color-cyan-500));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.gallery-subtitle {
  font-size: var(--text-xl);
  color: var(--color-gray-600);
  margin: 0;
  font-weight: var(--font-medium);
  line-height: 1.6;
}

/* 加载状态 */
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-md);
  padding: var(--spacing-3xl);
  color: var(--color-gray-500);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--color-gray-200);
  border-top-color: var(--color-miku-500);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 时间分组 */
.time-group {
  margin-bottom: var(--spacing-3xl);
}

.time-header {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-sm);
  border-bottom: 2px solid var(--color-gray-200);
}

.time-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
}

.time-count {
  font-size: var(--text-sm);
  color: var(--color-gray-500);
  font-weight: var(--font-medium);
}

/* 瀑布流容器 */
.waterfall-container {
  display: flex;
  gap: var(--spacing-lg);
  align-items: flex-start;
}

.waterfall-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

/* 相纸样式 */
.photo-item {
  background: var(--color-white);
  padding: var(--spacing-md);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all var(--transition-base);
  box-shadow: 
    0 2px 8px rgba(0, 0, 0, 0.08),
    0 4px 16px rgba(0, 0, 0, 0.04);
}

.photo-item:hover {
  transform: translateY(-8px);
  box-shadow: 
    0 8px 24px rgba(0, 0, 0, 0.12),
    0 16px 48px rgba(0, 0, 0, 0.08);
  z-index: 10;
}

.photo-image {
  position: relative;
  overflow: hidden;
  background: var(--color-gray-100);
  border-radius: 2px;
}

.photo-image img {
  width: 100%;
  height: auto;
  display: block;
  object-fit: contain;
}

/* 图片信息区域 - 相纸底部 */
.photo-info {
  padding: var(--spacing-lg) var(--spacing-md) var(--spacing-md);
  min-height: 60px;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.photo-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--spacing-md);
}

.photo-title {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
  margin: 0;
  line-height: 1.4;
  flex: 1;
  text-align: left;
}

.photo-meta {
  font-size: var(--text-xs);
  color: var(--color-gray-500);
  margin: 0;
  white-space: nowrap;
  text-align: right;
  line-height: 1.4;
}

/* 图片文字描述 */
.photo-caption {
  font-size: var(--text-sm);
  color: var(--color-gray-700);
  line-height: 1.6;
  margin: 0;
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-gray-200);
  text-align: center;
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
}

/* 加载更多状态 */
.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-md);
  padding: var(--spacing-2xl);
  color: var(--color-gray-500);
}

.loading-more .loading-spinner {
  width: 24px;
  height: 24px;
  border-width: 2px;
}

/* 没有更多数据 */
.no-more {
  text-align: center;
  padding: var(--spacing-2xl);
  color: var(--color-gray-500);
  font-size: var(--text-sm);
}

/* 返回顶部按钮 */
.back-to-top {
  position: fixed;
  bottom: var(--spacing-2xl);
  right: var(--spacing-2xl);
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(57, 197, 187, 0.3);
  transition: all var(--transition-base);
  z-index: 100;
}

.back-to-top:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(57, 197, 187, 0.4);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-base);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .gallery__main {
    padding: var(--spacing-xl) var(--spacing-lg);
  }

  .gallery-header {
    padding: var(--spacing-2xl) 0;
  }

  .gallery-title {
    font-size: var(--text-2xl);
  }

  .gallery-subtitle {
    font-size: var(--text-lg);
  }

  .time-group {
    margin-bottom: var(--spacing-2xl);
  }

  .time-header {
    margin-bottom: var(--spacing-lg);
  }

  .time-title {
    font-size: var(--text-xl);
  }

  .time-count {
    font-size: var(--text-xs);
  }

  .waterfall-container {
    gap: var(--spacing-md);
  }

  .waterfall-column {
    gap: var(--spacing-md);
  }

  .photo-item {
    padding: var(--spacing-sm);
  }

  .photo-info {
    padding: var(--spacing-md) var(--spacing-sm) var(--spacing-sm);
    min-height: 50px;
    gap: var(--spacing-sm);
  }

  .photo-header {
    flex-direction: column;
    gap: var(--spacing-xs);
  }

  .photo-title {
    font-size: var(--text-sm);
    text-align: center;
  }

  .photo-meta {
    font-size: var(--text-xs);
    text-align: center;
    white-space: normal;
  }

  .photo-caption {
    font-size: var(--text-xs);
    padding-top: var(--spacing-sm);
  }

  .back-to-top {
    bottom: var(--spacing-xl);
    right: var(--spacing-lg);
    width: 44px;
    height: 44px;
  }
}
</style>
