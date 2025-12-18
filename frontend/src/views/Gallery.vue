<template>
  <div class="gallery">
    <AppHeader />
    
    <main class="gallery__main">
      <!-- 页面标题 -->
      <div class="gallery-header">
        <h1 class="gallery-title">相册</h1>
        <p class="gallery-subtitle">记录生活中的美好瞬间 📷</p>
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
                      :style="{ aspectRatio: photo.aspectRatio }"
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
import { ArrowUp } from 'lucide-vue-next'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'

const initialLoading = ref(true)
const loadingMore = ref(false)
const showBackToTop = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = 12
const columnCount = ref(3) // 瀑布流列数

interface Photo {
  id: number
  title: string
  description: string
  url: string
  aspectRatio: string
  date: string
  caption?: string // 可选的图片文字描述
}

const photos = ref<Photo[]>([])

// 模拟所有照片数据
const allPhotosData: Photo[] = [
  {
    id: 1,
    title: '夕阳下的城市',
    description: '2024年1月 · 北京',
    url: 'https://images.unsplash.com/photo-1514565131-fce0801e5785?w=600',
    aspectRatio: '4/3',
    date: '2024-01-15',
    caption: '落日余晖洒满整个城市，高楼大厦在金色的光芒中显得格外壮观。这一刻，时间仿佛静止了。'
  },
  {
    id: 2,
    title: '樱花盛开',
    description: '2024年3月 · 东京',
    url: 'https://images.unsplash.com/photo-1522383225653-ed111181a951?w=600',
    aspectRatio: '3/4',
    date: '2024-03-20',
    caption: '春天的东京，樱花如约而至。漫步在樱花树下，感受着春天的气息，心情也变得明朗起来。'
  },
  {
    id: 3,
    title: '海边日落',
    description: '2023年8月 · 青岛',
    url: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600',
    aspectRatio: '16/9',
    date: '2023-08-15',
    caption: '海浪轻轻拍打着沙滩，夕阳将天空染成了橙红色。这是属于夏天的浪漫。'
  },
  {
    id: 4,
    title: '山间云海',
    description: '2023年10月 · 黄山',
    url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600',
    aspectRatio: '4/3',
    date: '2023-10-10'
  },
  {
    id: 5,
    title: '城市夜景',
    description: '2023年12月 · 上海',
    url: 'https://images.unsplash.com/photo-1519501025264-65ba15a82390?w=600',
    aspectRatio: '3/4',
    date: '2023-12-20',
    caption: '华灯初上，城市的夜晚充满了活力与魅力。'
  },
  {
    id: 6,
    title: '秋日枫叶',
    description: '2023年11月 · 京都',
    url: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600',
    aspectRatio: '1/1',
    date: '2023-11-15'
  },
  {
    id: 7,
    title: '雪山风光',
    description: '2023年12月 · 瑞士',
    url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600',
    aspectRatio: '16/9',
    date: '2023-12-25'
  },
  {
    id: 8,
    title: '古镇小巷',
    description: '2023年9月 · 乌镇',
    url: 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600',
    aspectRatio: '3/4',
    date: '2023-09-10'
  },
  {
    id: 9,
    title: '星空银河',
    description: '2023年7月 · 青海',
    url: 'https://images.unsplash.com/photo-1419242902214-272b3f66ee7a?w=600',
    aspectRatio: '4/3',
    date: '2023-07-20'
  },
  {
    id: 10,
    title: '田园风光',
    description: '2023年5月 · 婺源',
    url: 'https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=600',
    aspectRatio: '16/9',
    date: '2023-05-15'
  },
  {
    id: 11,
    title: '都市霓虹',
    description: '2023年11月 · 香港',
    url: 'https://images.unsplash.com/photo-1519501025264-65ba15a82390?w=600',
    aspectRatio: '3/4',
    date: '2023-11-20'
  },
  {
    id: 12,
    title: '湖光山色',
    description: '2023年6月 · 杭州',
    url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600',
    aspectRatio: '4/3',
    date: '2023-06-10'
  },
  {
    id: 13,
    title: '沙漠驼影',
    description: '2023年10月 · 敦煌',
    url: 'https://images.unsplash.com/photo-1509316785289-025f5b846b35?w=600',
    aspectRatio: '16/9',
    date: '2023-10-05'
  },
  {
    id: 14,
    title: '竹林幽径',
    description: '2023年4月 · 安吉',
    url: 'https://images.unsplash.com/photo-1523712999610-f77fbcfc3843?w=600',
    aspectRatio: '3/4',
    date: '2023-04-20'
  },
  {
    id: 15,
    title: '晨雾缭绕',
    description: '2023年3月 · 黄山',
    url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600',
    aspectRatio: '4/3',
    date: '2023-03-15'
  },
  {
    id: 16,
    title: '渔舟唱晚',
    description: '2023年8月 · 千岛湖',
    url: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600',
    aspectRatio: '16/9',
    date: '2023-08-25'
  },
  {
    id: 17,
    title: '梯田层叠',
    description: '2023年5月 · 元阳',
    url: 'https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=600',
    aspectRatio: '4/3',
    date: '2023-05-20'
  },
  {
    id: 18,
    title: '冰川奇观',
    description: '2023年12月 · 冰岛',
    url: 'https://images.unsplash.com/photo-1483728642387-6c3bdd6c93e5?w=600',
    aspectRatio: '3/4',
    date: '2023-12-30'
  }
]

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

// 优化的瀑布流分配算法 - 最短列优先
function distributePhotos(photoList: Photo[]) {
  const cols: Photo[][] = Array.from({ length: columnCount.value }, () => [])
  const colHeights: number[] = Array(columnCount.value).fill(0)
  
  photoList.forEach(photo => {
    // 计算图片高度（基于宽高比）
    const [width, height] = photo.aspectRatio.split('/').map(Number)
    const photoHeight = height / width
    
    // 找到当前最短的列
    const minHeight = Math.min(...colHeights)
    const minIndex = colHeights.indexOf(minHeight)
    
    // 将照片添加到最短的列
    cols[minIndex].push(photo)
    colHeights[minIndex] += photoHeight
  })
  
  return cols
}

// 加载更多照片
async function loadMorePhotos() {
  if (loadingMore.value || !hasMore.value) return

  loadingMore.value = true

  // 模拟API请求延迟
  await new Promise(resolve => setTimeout(resolve, 800))

  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  const newPhotos = allPhotosData.slice(start, end)

  if (newPhotos.length > 0) {
    photos.value.push(...newPhotos)
    currentPage.value++
  }

  // 检查是否还有更多数据
  if (end >= allPhotosData.length) {
    hasMore.value = false
  }

  loadingMore.value = false
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
  const windowHeight = window.innerHeight
  const documentHeight = document.documentElement.scrollHeight

  // 显示/隐藏返回顶部按钮
  showBackToTop.value = scrollTop > 300

  // 距离底部300px时触发加载
  if (scrollTop + windowHeight >= documentHeight - 300) {
    loadMorePhotos()
  }
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
  
  // 加载第一页数据
  await loadMorePhotos()
  initialLoading.value = false

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
  background: linear-gradient(to bottom, #fafafa 0%, #f5f5f5 100%);
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
