<template>
  <div class="home">
    <!-- 全屏背景 Hero 区域 -->
    <section class="hero-fullscreen">
      <AppHeader />
      <div class="hero-content">
        <h1 class="hero-title">欢迎来到落叶无痕的博客</h1>
        <p class="hero-subtitle">分享技术、记录生活、探索世界</p>
        
        <!-- 实时数据小组件 -->
        <div class="hero-widgets">
          <!-- 时间组件 -->
          <div class="widget-item">
            <div class="widget-content">
              <span class="widget-label">{{ currentDate }}</span>
              <span class="widget-value">{{ currentTime }}</span>
            </div>
            <div class="widget-icon">
              <Clock :size="24" />
            </div>
          </div>

          <!-- 天气组件 -->
          <div class="widget-item">
            <div class="widget-content">
              <span class="widget-label">{{ weather.location }} · {{ weather.condition }}</span>
              <span class="widget-value">{{ weather.temp }}°C</span>
            </div>
            <div class="widget-icon">
              <Cloud :size="24" />
            </div>
          </div>

          <!-- 网站存在天数 -->
          <div class="widget-item widget-days">
            <div class="widget-content">
              <span class="widget-label">网站已运行</span>
              <span class="widget-value">{{ siteDays }} 天</span>
            </div>
            <div class="widget-icon">
              <Calendar :size="24" />
            </div>
          </div>

          <!-- 访问统计 -->
          <div class="widget-item">
            <div class="widget-content">
              <span class="widget-label">{{ stats.articles }} 篇 · {{ stats.comments }} 评论</span>
              <span class="widget-value">{{ stats.visits }}</span>
            </div>
            <div class="widget-icon">
              <Users :size="24" />
            </div>
          </div>
        </div>

      </div>
      
      <button class="hero-button" @click="scrollToContent">
        探索更多
        <ArrowDown :size="18" />
      </button>
      
      <div class="scroll-indicator">
        <div class="scroll-line"></div>
      </div>
    </section>

    <!-- F型布局内容区 -->
    <main class="home__main">


      <!-- 横向特色文章区域 (F型顶部横线) -->
      <section class="featured-section">
        <div class="section-header">
          <h2 class="section-title">精选文章</h2>
          <a href="/archive" class="section-more">
            查看更多
            <ArrowRight :size="16" />
          </a>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="featured-loading">
          <div class="loading-spinner"></div>
          <span>加载中...</span>
        </div>
        
        <!-- 空状态 -->
        <div v-else-if="featuredArticles.length === 0" class="featured-empty">
          <div class="empty-icon">
            <FileText :size="48" />
          </div>
          <p class="empty-text">暂无精选文章</p>
          <p class="empty-hint">管理员还没有发布文章哦 (｡･ω･｡)</p>
        </div>
        
        <!-- 文章列表 -->
        <div v-else class="featured-articles">
          <FeaturedArticle
            v-for="article in featuredArticles"
            :key="article.id"
            :article="article"
          />
        </div>
      </section>

      <!-- F型主体布局 -->
      <div class="f-layout">
        <!-- 左侧主内容区 (F型竖线) -->
        <div class="f-layout__main">
          <!-- 项目展示区域 -->
          <section class="content-section">
            <div class="section-header">
              <h2 class="section-title">最新项目</h2>
              <a href="/projects" class="section-more">
                查看更多
                <ArrowRight :size="16" />
              </a>
            </div>
            <ProjectGallery />
          </section>

          <!-- 生活图片区域 -->
          <section class="content-section">
            <div class="section-header">
              <h2 class="section-title">生活瞬间</h2>
              <a href="/gallery" class="section-more">
                查看更多
                <ArrowRight :size="16" />
              </a>
            </div>
            <LifeGallery />
          </section>

          <section class="latest-section">
            <h2 class="section-title">最新文章</h2>
            
            <!-- 空状态 -->
            <div v-if="!loading && articles.length === 0 && !hasMore" class="articles-empty">
              <div class="empty-icon">
                <FileText :size="64" />
              </div>
              <p class="empty-text">还没有文章</p>
              <p class="empty-hint">期待管理员的第一篇文章 ✨</p>
            </div>
            
            <!-- 文章列表 -->
            <ArticleList v-else :articles="articles" :loading="loading" />
            
            <!-- 加载更多状态 -->
            <div v-if="loadingMore" class="loading-more">
              <div class="loading-spinner"></div>
              <span>加载中...</span>
            </div>
            
            <!-- 没有更多数据 -->
            <div v-if="!hasMore && articles.length > 0" class="no-more">
              <span>没有更多文章了 (๑•̀ㅂ•́)و✧</span>
            </div>
          </section>
        </div>

        <!-- 右侧边栏 -->
        <aside class="f-layout__sidebar">
          <AboutSection />

          <div class="sidebar-divider"></div>

          <ArticleHeatmap :data="heatmapData" />

          <div class="sidebar-divider"></div>

          <TagsCloud :tags="popularTags" @tag-click="handleTagClick" />

          <div class="sidebar-divider"></div>

          <RecentActivities @activity-click="handleActivityClick" />

          <div class="sidebar-divider"></div>

          <FriendLinks />

          <div class="sidebar-divider"></div>

          <ArticleRanking :articles="rankingArticles" />
        </aside>
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
import { ref, onMounted, onUnmounted } from 'vue'
import { ArrowDown, ArrowUp, ArrowRight, Clock, Cloud, Calendar, Users, FileText } from 'lucide-vue-next'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import FeaturedArticle from '@/components/article/FeaturedArticle.vue'
import ArticleList from '@/components/article/ArticleList.vue'
import ProjectGallery from '@/components/home/ProjectGallery.vue'
import LifeGallery from '@/components/home/LifeGallery.vue'
import AboutSection from '@/components/home/AboutSection.vue'
import TagsCloud from '@/components/home/TagsCloud.vue'
import RecentActivities from '@/components/home/RecentActivities.vue'
import FriendLinks from '@/components/home/FriendLinks.vue'
import ArticleRanking from '@/components/home/ArticleRanking.vue'
import ArticleHeatmap from '@/components/home/ArticleHeatmap.vue'
import { getArticleList, getAllTags, getArticleHeatmap, getArticleRanking, getFeaturedArticles, getPublicStats } from '@/api/article'
import { useConfigStore } from '@/stores/config'
import { message } from '@/utils/message'
import type { Article } from '@/api/article'

const configStore = useConfigStore()

const articles = ref<Article[]>([])
const featuredArticles = ref<Article[]>([])
const loading = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = 6
const totalArticles = ref(0)
const showBackToTop = ref(false)

const popularTags = ref<string[]>([])
const heatmapData = ref<Record<string, number>>({})
const rankingArticles = ref<Array<{ id: number; title: string; views: number }>>([])

// 侧边栏事件处理
function handleTagClick(tag: string) {
  console.log('Tag clicked:', tag)
  // TODO: 实现标签筛选功能
}

function handleActivityClick(activity: any) {
  console.log('Activity clicked:', activity)
  // TODO: 实现活动跳转功能
}

// 实时数据
const currentTime = ref('')
const currentDate = ref('')
const weather = ref({ temp: '--', condition: '获取中', location: '定位中' })
const siteDays = ref(0)
const stats = ref({ visits: '0', articles: '0', comments: '0' })

// 格式化数字
function formatNumber(num: number): string {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'W'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toLocaleString()
}

// 计算网站存在天数
function calculateSiteDays() {
  const startDateStr = configStore.siteStartDate || '2024-01-01'
  const startDate = new Date(startDateStr)
  
  // 检查日期是否有效
  if (isNaN(startDate.getTime())) {
    // 如果日期无效，使用默认日期
    const defaultDate = new Date('2024-01-01')
    const now = new Date()
    const diffTime = Math.abs(now.getTime() - defaultDate.getTime())
    siteDays.value = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    return
  }
  
  const now = new Date()
  const diffTime = Math.abs(now.getTime() - startDate.getTime())
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  siteDays.value = diffDays
}

let timeInterval: number | null = null

function updateTime() {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit',
    second: '2-digit'
  })
  currentDate.value = now.toLocaleDateString('zh-CN', {
    month: 'long',
    day: 'numeric',
    weekday: 'short'
  })
}

function scrollToContent() {
  window.scrollTo({
    top: window.innerHeight,
    behavior: 'smooth'
  })
}

// 加载更多文章
async function loadMoreArticles() {
  if (loadingMore.value || !hasMore.value) return

  loadingMore.value = true

  try {
    const result = await getArticleList({
      page: currentPage.value,
      pageSize
    })

    if (result.records && result.records.length > 0) {
      articles.value.push(...result.records)
      currentPage.value++
      
      // 检查是否还有更多数据
      hasMore.value = articles.value.length < totalArticles.value
    } else {
      hasMore.value = false
    }
  } catch (error: any) {
    message.error(error.message || '加载文章失败')
    hasMore.value = false
  } finally {
    loadingMore.value = false
  }
}

// 加载标签
async function loadTags() {
  try {
    const tags = await getAllTags()
    popularTags.value = tags.slice(0, 10).map(tag => tag.name)
  } catch (error: any) {
    console.error('加载标签失败:', error)
  }
}

// 加载热力图数据
async function loadHeatmap() {
  try {
    heatmapData.value = await getArticleHeatmap()
  } catch (error: any) {
    console.error('加载热力图失败:', error)
  }
}

// 加载排行榜数据
async function loadRanking() {
  try {
    const data = await getArticleRanking(5)
    rankingArticles.value = data.map(item => ({
      id: item.id,
      title: item.title,
      views: item.viewCount
    }))
  } catch (error: any) {
    console.error('加载排行榜失败:', error)
  }
}

// 加载统计数据
async function loadStats() {
  try {
    const data = await getPublicStats()
    stats.value = {
      visits: formatNumber(data.views),
      articles: data.articles.toString(),
      comments: data.comments.toString()
    }
  } catch (error: any) {
    console.error('加载统计数据失败:', error)
  }
}

// 获取天气数据（使用免费的天气API）
async function loadWeather() {
  try {
    // 先获取用户位置
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        async (position) => {
          const { latitude, longitude } = position.coords
          // 使用 Open-Meteo 免费天气 API
          const response = await fetch(
            `https://api.open-meteo.com/v1/forecast?latitude=${latitude}&longitude=${longitude}&current_weather=true&timezone=auto`
          )
          const data = await response.json()
          if (data.current_weather) {
            const weatherCode = data.current_weather.weathercode
            weather.value = {
              temp: Math.round(data.current_weather.temperature).toString(),
              condition: getWeatherCondition(weatherCode),
              location: '当前位置'
            }
          }
        },
        () => {
          // 定位失败，使用默认位置（北京）
          loadDefaultWeather()
        },
        { timeout: 5000 }
      )
    } else {
      loadDefaultWeather()
    }
  } catch (error) {
    console.error('获取天气失败:', error)
    loadDefaultWeather()
  }
}

// 加载默认天气（北京）
async function loadDefaultWeather() {
  try {
    const response = await fetch(
      'https://api.open-meteo.com/v1/forecast?latitude=39.9042&longitude=116.4074&current_weather=true&timezone=auto'
    )
    const data = await response.json()
    if (data.current_weather) {
      const weatherCode = data.current_weather.weathercode
      weather.value = {
        temp: Math.round(data.current_weather.temperature).toString(),
        condition: getWeatherCondition(weatherCode),
        location: '北京'
      }
    }
  } catch (error) {
    weather.value = { temp: '--', condition: '未知', location: '北京' }
  }
}

// 根据天气代码获取天气描述
function getWeatherCondition(code: number): string {
  const conditions: Record<number, string> = {
    0: '晴天',
    1: '晴朗',
    2: '多云',
    3: '阴天',
    45: '雾',
    48: '雾凇',
    51: '小雨',
    53: '中雨',
    55: '大雨',
    61: '小雨',
    63: '中雨',
    65: '大雨',
    71: '小雪',
    73: '中雪',
    75: '大雪',
    80: '阵雨',
    81: '阵雨',
    82: '暴雨',
    95: '雷暴',
    96: '雷暴冰雹',
    99: '雷暴冰雹'
  }
  return conditions[code] || '未知'
}

// 监听滚动事件
function handleScroll() {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  const windowHeight = window.innerHeight
  const documentHeight = document.documentElement.scrollHeight

  // 显示/隐藏返回顶部按钮（滚动超过300px时显示）
  showBackToTop.value = scrollTop > 300

  // 距离底部200px时触发加载
  if (scrollTop + windowHeight >= documentHeight - 200) {
    loadMoreArticles()
  }
}

// 返回顶部
function scrollToTop() {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

onMounted(async () => {
  loading.value = true
  
  // 更新时间和网站天数
  updateTime()
  calculateSiteDays()
  timeInterval = window.setInterval(() => {
    updateTime()
    calculateSiteDays()
  }, 1000)
  
  // 加载天气数据
  loadWeather()
  
  // 加载统计数据
  loadStats()
  
  // 加载真实数据
  try {
    // 加载标签
    await loadTags()
    
    // 加载热力图数据
    loadHeatmap()
    
    // 加载排行榜数据
    loadRanking()
    
    // 加载精选文章（置顶文章或浏览数最多的文章）
    const featured = await getFeaturedArticles(3)
    featuredArticles.value = featured
    
    // 加载最新文章列表
    const result = await getArticleList({ page: 1, pageSize })
    
    if (result.records && result.records.length > 0) {
      articles.value = result.records
      totalArticles.value = result.total
      currentPage.value = 2
      
      // 检查是否还有更多
      hasMore.value = articles.value.length < result.total
    }
  } catch (error: any) {
    message.error(error.message || '加载数据失败')
  } finally {
    loading.value = false
  }

  // 添加滚动监听
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
  // 移除滚动监听
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  overflow-x: hidden;
}

/* 全屏 Hero 区域 */
.hero-fullscreen {
  position: relative;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-image: url('@/assets/image/background.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  overflow: hidden;
}

.hero-fullscreen::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    to bottom,
    rgba(255, 255, 255, 0.1) 0%,
    rgba(255, 255, 255, 0.3) 50%,
    rgba(255, 255, 255, 0.5) 100%
  );
  pointer-events: none;
}

.hero-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding: var(--spacing-xl);
  position: relative;
  z-index: 1;
}

.hero-title {
  font-size: clamp(var(--text-3xl), 5vw, 64px);
  font-weight: var(--font-bold);
  margin-bottom: var(--spacing-lg);
  color: var(--color-gray-900);
  text-shadow: 0 2px 8px rgba(255, 255, 255, 0.8);
  animation: fadeInUp 0.8s ease-out;
}

.hero-subtitle {
  font-size: clamp(var(--text-lg), 2vw, var(--text-2xl));
  color: var(--color-gray-700);
  margin-bottom: var(--spacing-2xl);
  text-shadow: 0 1px 4px rgba(255, 255, 255, 0.6);
  animation: fadeInUp 0.8s ease-out 0.2s both;
}

.hero-button {
  position: absolute;
  bottom: 12vh;
  left: 0;
  right: 0;
  margin: 0 auto;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  animation: fadeInUp 0.8s ease-out 0.4s both;
  padding: var(--spacing-md) var(--spacing-2xl);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: var(--radius-full);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  width: fit-content;
  z-index: 2;
}

.hero-button:hover {
  background: rgba(255, 255, 255, 1);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.hero-button svg {
  transition: transform var(--transition-fast);
}

.hero-button:hover svg {
  transform: translateY(2px);
}

@media (max-width: 768px) {
  .hero-button {
    bottom: 10vh;
  }
}

/* 实时数据小组件 */
.hero-widgets {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-2xl);
  animation: fadeInUp 0.8s ease-out 0.3s both;
  max-width: 800px;
}

.widget-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-lg) var(--spacing-xl);
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: var(--radius-lg);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all var(--transition-base);
  cursor: pointer;
}

.widget-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  background: rgba(255, 255, 255, 0.95);
}

.widget-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  min-width: 0;
  flex: 1;
}

.widget-label {
  font-size: var(--text-xs);
  color: var(--color-gray-500);
  font-weight: var(--font-medium);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.widget-value {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.widget-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  border-radius: var(--radius-md);
  flex-shrink: 0;
  transition: transform var(--transition-fast);
}

.widget-icon svg {
  color: var(--color-white);
}

.widget-item:hover .widget-icon {
  transform: scale(1.05) rotate(5deg);
}

/* 网站天数组件特殊样式 */
.widget-days:hover .widget-icon {
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1) rotate(0deg);
  }
  50% {
    transform: scale(1.1) rotate(5deg);
  }
}

@media (max-width: 1024px) {
  .hero-widgets {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .hero-widgets {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-sm);
  }

  .widget-item {
    padding: var(--spacing-md);
    gap: var(--spacing-md);
  }

  .widget-icon {
    width: 40px;
    height: 40px;
  }

  .widget-icon svg {
    width: 20px;
    height: 20px;
  }

  .widget-label {
    font-size: 10px;
  }

  .widget-value {
    font-size: var(--text-sm);
  }
}

.scroll-indicator {
  position: absolute;
  bottom: var(--spacing-xl);
  left: 50%;
  transform: translateX(-50%);
  animation: bounce 2s infinite;
  z-index: 2;
}

.scroll-line {
  width: 2px;
  height: 40px;
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.5);
}

/* F型布局 */
.home__main {
  width: 100%;
  padding: var(--spacing-3xl) max(2vw, 120px); /* 左右边距相同，确保右侧有足够空间给悬浮按钮 */
}

/* 特色文章区域 (F型顶部横线) */
.featured-section {
  margin-bottom: var(--spacing-3xl);
}

/* 内容展示区域 */
.content-section {
  margin-bottom: var(--spacing-3xl);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-2xl);
}

.section-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  position: relative;
  padding-bottom: var(--spacing-md);
  margin: 0;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 40px;
  height: 2px;
  background: var(--color-miku-400);
}

.section-more {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.section-more:hover {
  color: var(--color-miku-500);
}

.section-more svg {
  transition: transform var(--transition-fast);
}

.section-more:hover svg {
  transform: translateX(4px);
}

.featured-articles {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-xl);
}

/* F型主体布局 */
.f-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: var(--spacing-2xl);
  align-items: start;
  max-width: 100%;
}

.f-layout__main {
  min-width: 0;
}

.f-layout__sidebar {
  position: sticky;
  top: calc(var(--spacing-lg) + 80px);
  display: flex;
  flex-direction: column;
  gap: 0;
  padding-bottom: var(--spacing-3xl); /* 底部留出空间 */
}

.sidebar-divider {
  height: 1px;
  background: var(--color-gray-200);
  margin: var(--spacing-xl) 0;
}

.sidebar-section {
  padding: var(--spacing-lg) 0;
}

.sidebar-title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin-bottom: var(--spacing-md);
  color: var(--color-gray-900);
}

.sidebar-text {
  font-size: var(--text-base);
  color: var(--color-gray-600);
  line-height: 1.8;
}

/* 关于我部分样式优化 */
.about-section {
  padding: var(--spacing-xl) 0;
}

.about-profile {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.about-avatar {
  position: relative;
  width: 64px;
  height: 64px;
  flex-shrink: 0;
}

.about-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--color-white);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform var(--transition-base);
}

.about-avatar:hover img {
  transform: scale(1.05);
}

.about-info {
  flex: 1;
  min-width: 0;
}

.about-name {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0 0 var(--spacing-xs) 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.about-role {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.about-bio {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  line-height: 1.7;
  margin-bottom: var(--spacing-lg);
}

/* 技能标签 */
.about-skills {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  margin-bottom: var(--spacing-lg);
}

.skill-tag {
  font-size: var(--text-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  background: linear-gradient(135deg, var(--color-miku-50), var(--color-cyan-50));
  color: var(--color-miku-600);
  border: 1px solid var(--color-miku-200);
  border-radius: var(--radius-md);
  font-weight: var(--font-medium);
  transition: all var(--transition-fast);
  cursor: default;
}

.skill-tag:hover {
  background: linear-gradient(135deg, var(--color-miku-100), var(--color-cyan-100));
  border-color: var(--color-miku-300);
  transform: translateY(-1px);
}

/* 社交链接 */
.about-social {
  display: flex;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-lg);
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  color: var(--color-gray-600);
  background: var(--color-gray-100);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
  text-decoration: none;
}

.social-link svg {
  width: 18px;
  height: 18px;
}

.social-link:hover {
  color: var(--color-white);
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.3);
}

/* 统计数据 */
.about-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: linear-gradient(135deg, var(--color-miku-50), var(--color-cyan-50));
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-miku-100);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xs);
}

.stat-value {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--color-miku-600);
  line-height: 1;
}

.stat-label {
  font-size: var(--text-xs);
  color: var(--color-gray-600);
  font-weight: var(--font-medium);
}

.tags-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-sm);
}

.tag-item {
  font-size: var(--text-sm);
  padding: var(--spacing-xs) var(--spacing-md);
  color: var(--color-miku-500);
  border: 1px solid var(--color-miku-200);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all var(--transition-fast);
  background: transparent;
}

.tag-item:hover {
  background: var(--color-miku-50);
  border-color: var(--color-miku-400);
  transform: translateY(-2px);
}

/* 最新动态 */
.recent-activities {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.activity-item {
  display: flex;
  gap: var(--spacing-md);
  position: relative;
  padding: var(--spacing-sm);
  margin: calc(var(--spacing-xs) * -1);
  border-radius: var(--radius-md);
  transition: all var(--transition-base);
  cursor: pointer;
}

.activity-item:hover {
  background: var(--color-gray-50);
  transform: translateX(2px);
}

.activity-item:not(:last-child)::after {
  content: '';
  position: absolute;
  left: calc(var(--spacing-sm) + 15px);
  top: calc(var(--spacing-sm) + 32px);
  bottom: calc(var(--spacing-md) * -1);
  width: 2px;
  background: linear-gradient(180deg, var(--color-gray-200), transparent);
}

.activity-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
  z-index: 1;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all var(--transition-base);
}

.activity-item:hover .activity-icon {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.activity-icon svg {
  width: 16px;
  height: 16px;
  color: var(--color-white);
}

.activity-icon--article {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.activity-icon--code {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
}

.activity-icon--star {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.activity-content {
  flex: 1;
  min-width: 0;
  padding-top: 2px;
}

.activity-text {
  font-size: var(--text-sm);
  color: var(--color-gray-700);
  line-height: 1.6;
  margin-bottom: var(--spacing-xs);
  transition: color var(--transition-fast);
}

.activity-item:hover .activity-text {
  color: var(--color-gray-900);
}

.activity-time {
  font-size: var(--text-xs);
  color: var(--color-gray-500);
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.activity-time::before {
  content: '';
  width: 4px;
  height: 4px;
  background: var(--color-gray-400);
  border-radius: 50%;
}

/* 友情链接 */
.friend-links {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.friend-link {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) 0;
  text-decoration: none;
  transition: all var(--transition-fast);
  position: relative;
  border-bottom: 1px solid transparent;
}

.friend-link:not(:last-child) {
  border-bottom-color: var(--color-gray-100);
}

.friend-link-dot {
  width: 6px;
  height: 6px;
  background: var(--color-gray-400);
  border-radius: 50%;
  flex-shrink: 0;
  transition: all var(--transition-fast);
}

.friend-link-text {
  flex: 1;
  font-size: var(--text-sm);
  color: var(--color-gray-700);
  transition: all var(--transition-fast);
}

.friend-link-arrow {
  width: 14px;
  height: 14px;
  color: var(--color-gray-400);
  opacity: 0;
  transform: translateX(-4px);
  transition: all var(--transition-fast);
  flex-shrink: 0;
}

.friend-link:hover {
  padding-left: var(--spacing-xs);
}

.friend-link:hover .friend-link-dot {
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  transform: scale(1.3);
}

.friend-link:hover .friend-link-text {
  color: var(--color-miku-600);
  font-weight: var(--font-medium);
}

.friend-link:hover .friend-link-arrow {
  opacity: 1;
  transform: translateX(0);
  color: var(--color-miku-500);
}

/* 文章排行 */
.article-ranking {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.ranking-item {
  display: flex;
  gap: var(--spacing-md);
  text-decoration: none;
  padding: var(--spacing-sm);
  margin: calc(var(--spacing-xs) * -1);
  border-radius: var(--radius-md);
  transition: all var(--transition-base);
}

.ranking-item:hover {
  background: var(--color-gray-50);
  transform: translateX(2px);
}

.ranking-number {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
  color: var(--color-gray-500);
  background: var(--color-gray-100);
  border-radius: var(--radius-sm);
  flex-shrink: 0;
  transition: all var(--transition-fast);
}

.ranking-number--top {
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  color: var(--color-white);
  box-shadow: 0 2px 8px rgba(57, 197, 187, 0.3);
}

.ranking-item:hover .ranking-number {
  transform: scale(1.1);
}

.ranking-item:hover .ranking-number--top {
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.4);
}

.ranking-content {
  flex: 1;
  min-width: 0;
}

.ranking-title {
  font-size: var(--text-sm);
  color: var(--color-gray-800);
  line-height: 1.5;
  margin-bottom: var(--spacing-xs);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color var(--transition-fast);
}

.ranking-item:hover .ranking-title {
  color: var(--color-miku-600);
}

.ranking-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.ranking-views {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--text-xs);
  color: var(--color-gray-500);
}

.ranking-views svg {
  width: 14px;
  height: 14px;
}

/* 加载更多状态 */
.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-md);
  padding: var(--spacing-3xl) 0;
  color: var(--color-gray-600);
  font-size: var(--text-base);
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid var(--color-gray-300);
  border-top-color: var(--color-miku-400);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.no-more {
  display: flex;
  justify-content: center;
  padding: var(--spacing-3xl) 0;
  color: var(--color-gray-500);
  font-size: var(--text-base);
  border-top: 1px solid var(--color-gray-200);
  margin-top: var(--spacing-xl);
}

/* 返回顶部按钮 */
.back-to-top {
  position: fixed;
  bottom: 40px;
  right: 40px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  color: var(--color-white);
  border: none;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.3);
  transition: all var(--transition-base);
  z-index: var(--z-fixed);
}

.back-to-top:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(57, 197, 187, 0.4);
}

.back-to-top:active {
  transform: translateY(-2px);
}

/* 淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-base), transform var(--transition-base);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

@media (max-width: 768px) {
  .back-to-top {
    bottom: 24px;
    right: 24px;
    width: 44px;
    height: 44px;
  }
}

/* 响应式布局 */
@media (max-width: 1024px) {
  .f-layout {
    grid-template-columns: 1fr;
  }

  .f-layout__sidebar {
    position: static;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    display: grid;
    padding-bottom: 0;
  }

  .home__main {
    padding: var(--spacing-3xl) max(2vw, 100px); /* 平板设备左右边距相同 */
  }
}

@media (max-width: 1024px) {
  .featured-articles {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .home__main {
    padding: var(--spacing-2xl) max(4vw, 90px); /* 移动端左右边距相同 */
  }

  .featured-articles {
    grid-template-columns: 1fr;
  }
}

/* 动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateX(-50%) translateY(0);
  }
  50% {
    transform: translateX(-50%) translateY(-10px);
  }
}

/* 精选文章加载和空状态 */
.featured-loading,
.featured-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl) var(--spacing-2xl);
  min-height: 280px;
  border-radius: var(--radius-lg);
}

.featured-loading {
  gap: var(--spacing-lg);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--color-gray-200);
  border-top-color: var(--color-miku-500);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.featured-loading span {
  font-size: var(--text-sm);
  color: var(--color-gray-500);
}

.featured-empty,
.articles-empty {
  text-align: center;
}

.empty-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, rgba(57, 197, 187, 0.1), rgba(57, 197, 187, 0.05));
  border-radius: 50%;
  margin-bottom: var(--spacing-lg);
}

.empty-icon svg {
  color: var(--color-miku-400);
  opacity: 0.8;
}

.empty-text {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--color-gray-600);
  margin: 0 0 var(--spacing-xs) 0;
}

.empty-hint {
  font-size: var(--text-sm);
  color: var(--color-gray-400);
  margin: 0;
}

/* 最新文章空状态 */
.articles-empty {
  padding: var(--spacing-3xl) var(--spacing-2xl);
  min-height: 260px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg);
  margin: var(--spacing-xl) 0;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .featured-empty,
  .articles-empty {
    padding: var(--spacing-2xl) var(--spacing-lg);
    min-height: 200px;
  }

  .empty-icon {
    width: 56px;
    height: 56px;
  }

  .empty-icon svg {
    width: 28px;
    height: 28px;
  }

  .empty-text {
    font-size: var(--text-sm);
  }

  .empty-hint {
    font-size: var(--text-xs);
  }
}
</style>
