<template>
  <div class="home">
    <!-- 加载进度条 -->
    <div v-if="imageLoading" class="loading-overlay">
      <div class="loading-container">
        <!-- Icon Loading 动画 -->
        <div class="loading-icon">
          <Music :size="48" class="icon-spin" />
        </div>
        
        <p class="loading-subtitle">{{ loadingText }}</p>
        
        <!-- 简约电池格子进度条 -->
        <div class="battery-bar">
          <div 
            v-for="i in 10" 
            :key="i" 
            class="battery-cell"
            :class="{ 'battery-cell--active': loadingProgress >= i * 10 }"
          ></div>
        </div>
        
        <p class="loading-text">{{ loadingProgress }}%</p>
      </div>
    </div>

    <!-- 全屏背景 Hero 区域 -->
    <section class="hero-fullscreen" :class="{ 'hero-loaded': !imageLoading }">
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

          <!-- 音乐组件 -->
          <div class="widget-item widget-music" :class="{ 'is-playing': music.isPlaying }" @click="toggleMusic">
            <div class="widget-content">
              <span class="widget-label">{{ music.artist }}</span>
              <span class="widget-value">{{ music.title }}</span>
            </div>
            <div class="widget-icon">
              <Music :size="24" />
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
        <h2 class="section-title">精选文章</h2>
        <div class="featured-articles">
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
              <a href="#" class="section-more">
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
              <a href="#" class="section-more">
                查看更多
                <ArrowRight :size="16" />
              </a>
            </div>
            <LifeGallery />
          </section>

          <section class="latest-section">
            <h2 class="section-title">最新文章</h2>
            <ArticleList :articles="articles" :loading="loading" />
            
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

          <ArticleHeatmap />

          <div class="sidebar-divider"></div>

          <TagsCloud :tags="popularTags" @tag-click="handleTagClick" />

          <div class="sidebar-divider"></div>

          <RecentActivities @activity-click="handleActivityClick" />

          <div class="sidebar-divider"></div>

          <FriendLinks />

          <div class="sidebar-divider"></div>

          <ArticleRanking />
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
import { ArrowDown, ArrowUp, ArrowRight, Clock, Cloud, Music, Users } from 'lucide-vue-next'
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
import type { Article } from '@/types/article'

const articles = ref<Article[]>([])
const featuredArticles = ref<Article[]>([])
const loading = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = 6
const showBackToTop = ref(false)

const popularTags = ref([
  'Vue', 'TypeScript', 'CSS', 'JavaScript', 'Node.js', 
  'React', 'Vite', 'Pinia', 'Web Components', 'Performance'
])

// 社交链接数据
const socialLinks = ref([
  {
    name: 'GitHub',
    url: 'https://github.com',
    icon: 'github'
  },
  {
    name: '哔哩哔哩',
    url: 'https://space.bilibili.com',
    icon: 'bilibili'
  },
  {
    name: 'Email',
    url: 'mailto:contact@example.com',
    icon: 'mail'
  }
])

// 侧边栏事件处理
function handleTagClick(tag: string) {
  console.log('Tag clicked:', tag)
  // TODO: 实现标签筛选功能
}

function handleActivityClick(activity: any) {
  console.log('Activity clicked:', activity)
  // TODO: 实现活动跳转功能
}

// 图片加载状态
const imageLoading = ref(true)
const loadingProgress = ref(0)
const loadingText = ref('初始化中...')

// 加载文字列表 - 二次元搞怪风格
const loadingMessages = [
  '(๑•̀ㅂ•́)و✧ 系统启动中...',
  '(｡･ω･｡) 召唤初音中...',
  '(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ 加载葱葱...',
  '(๑˃ᴗ˂)ﻭ 准备唱歌...',
  '(★ ω ★) 调试音符...',
  '(ﾉ≧∀≦)ﾉ 马上好了...',
  '(๑>ᴗ<๑) 完成啦！'
]

// 实时数据
const currentTime = ref('')
const currentDate = ref('')
const weather = ref({ temp: '25', condition: '晴天', location: '北京' })
const music = ref({ title: '千本樱', artist: '初音ミク', isPlaying: false })
const stats = ref({ visits: '1,234', articles: '42', comments: '156' })

let timeInterval: number | null = null
let progressInterval: number | null = null

// 模拟文章数据库
const allArticlesData = [
  {
    id: 4,
    title: 'Vite 5.0 新特性全面解读',
    summary: 'Vite 5.0 带来了许多令人兴奋的新特性，包括性能优化、API 改进等，让我们一起来看看。',
    cover: 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800&h=600&fit=crop',
    author: '落叶无痕',
    createdAt: new Date('2024-01-20'),
    tags: ['Vite', 'Build Tools'],
    views: 654,
    likes: 45
  },
  {
    id: 5,
    title: 'Pinia 状态管理最佳实践',
    summary: '探索 Pinia 在大型项目中的应用，分享状态管理的设计模式和优化技巧。',
    cover: 'https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=800&h=600&fit=crop',
    author: '落叶无痕',
    createdAt: new Date('2024-01-18'),
    tags: ['Vue', 'Pinia'],
    views: 543,
    likes: 38
  },
  {
    id: 6,
    title: '前端性能优化实战指南',
    summary: '从加载优化、渲染优化到运行时优化，全方位提升前端应用性能。',
    cover: '',
    author: '落叶无痕',
    createdAt: new Date('2024-01-16'),
    tags: ['Performance', 'Optimization'],
    views: 789,
    likes: 67
  },
  {
    id: 7,
    title: 'Web Components 入门与实践',
    summary: '学习 Web Components 标准，创建可复用的自定义元素，实现真正的组件化开发。',
    cover: 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800&h=600&fit=crop',
    author: '落叶无痕',
    createdAt: new Date('2024-01-12'),
    tags: ['Web Components', 'JavaScript'],
    views: 432,
    likes: 29
  },
  {
    id: 8,
    title: 'React Server Components 详解',
    summary: 'React Server Components 改变了我们构建应用的方式，了解其工作原理和使用场景。',
    cover: '',
    author: '落叶无痕',
    createdAt: new Date('2024-01-08'),
    tags: ['React', 'SSR'],
    views: 678,
    likes: 52
  },
  {
    id: 9,
    title: 'Node.js 微服务架构实践',
    summary: '使用 Node.js 构建微服务架构，探讨服务拆分、通信、部署等关键问题。',
    cover: '',
    author: '落叶无痕',
    createdAt: new Date('2024-01-03'),
    tags: ['Node.js', 'Microservices'],
    views: 567,
    likes: 41
  },
  {
    id: 10,
    title: 'Docker 容器化部署实战',
    summary: '使用 Docker 容器化部署前端应用，提升开发效率和部署一致性。',
    cover: 'https://images.unsplash.com/photo-1605745341112-85968b19335b?w=800&h=600&fit=crop',
    author: '落叶无痕',
    createdAt: new Date('2023-12-28'),
    tags: ['Docker', 'DevOps'],
    views: 423,
    likes: 35
  },
  {
    id: 11,
    title: 'GraphQL API 设计指南',
    summary: '深入理解 GraphQL 的设计理念，构建高效灵活的 API 接口。',
    cover: '',
    author: '落叶无痕',
    createdAt: new Date('2023-12-25'),
    tags: ['GraphQL', 'API'],
    views: 389,
    likes: 28
  },
  {
    id: 12,
    title: 'Tailwind CSS 实用技巧',
    summary: '掌握 Tailwind CSS 的高级用法，快速构建美观的用户界面。',
    cover: 'https://images.unsplash.com/photo-1555949963-ff9fe0c870eb?w=800&h=600&fit=crop',
    author: '落叶无痕',
    createdAt: new Date('2023-12-20'),
    tags: ['CSS', 'Tailwind'],
    views: 512,
    likes: 42
  },
  {
    id: 13,
    title: 'Jest 单元测试最佳实践',
    summary: '编写高质量的单元测试，提升代码可维护性和稳定性。',
    cover: '',
    author: '落叶无痕',
    createdAt: new Date('2023-12-15'),
    tags: ['Testing', 'Jest'],
    views: 345,
    likes: 26
  },
  {
    id: 14,
    title: 'Webpack 5 配置优化',
    summary: '优化 Webpack 配置，提升构建速度和打包体积。',
    cover: '',
    author: '落叶无痕',
    createdAt: new Date('2023-12-10'),
    tags: ['Webpack', 'Build Tools'],
    views: 298,
    likes: 22
  },
  {
    id: 15,
    title: 'MongoDB 数据库设计',
    summary: '学习 MongoDB 的数据建模和查询优化技巧。',
    cover: 'https://images.unsplash.com/photo-1544383835-bda2bc66a55d?w=800&h=600&fit=crop',
    author: '落叶无痕',
    createdAt: new Date('2023-12-05'),
    tags: ['MongoDB', 'Database'],
    views: 267,
    likes: 19
  }
]

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

function toggleMusic() {
  music.value.isPlaying = !music.value.isPlaying
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

  // 模拟API请求延迟
  await new Promise(resolve => setTimeout(resolve, 800))

  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  const newArticles = allArticlesData.slice(start, end)

  if (newArticles.length > 0) {
    articles.value.push(...newArticles)
    currentPage.value++
  }

  // 检查是否还有更多数据
  if (end >= allArticlesData.length) {
    hasMore.value = false
  }

  loadingMore.value = false
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
  
  // 更新时间
  updateTime()
  timeInterval = window.setInterval(updateTime, 1000)
  
  // 模拟图片加载进度（开发环境固定3秒）
  const loadDuration = 3000 // 3秒
  const updateInterval = 30 // 每30ms更新一次
  const totalSteps = loadDuration / updateInterval
  let currentStep = 0
  
  progressInterval = window.setInterval(() => {
    currentStep++
    loadingProgress.value = Math.min(Math.round((currentStep / totalSteps) * 100), 100)
    
    // 根据进度更新文字
    const messageIndex = Math.floor((loadingProgress.value / 100) * (loadingMessages.length - 1))
    loadingText.value = loadingMessages[messageIndex]
    
    if (currentStep >= totalSteps) {
      if (progressInterval) {
        clearInterval(progressInterval)
      }
      // 确保进度条到100%后再隐藏
      setTimeout(() => {
        imageLoading.value = false
      }, 200)
    }
  }, updateInterval)
  
  // 模拟加载文章数据
  setTimeout(() => {
    // 精选文章数据
    featuredArticles.value = [
      {
        id: 1,
        title: 'Vue 3 Composition API 深度解析',
        summary: '深入探讨 Vue 3 Composition API 的设计理念和最佳实践，帮助你更好地理解和使用这个强大的特性。',
        cover: 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=800&h=600&fit=crop',
        author: '落叶无痕',
        createdAt: new Date('2024-01-15'),
        tags: ['Vue', 'TypeScript'],
        views: 1234,
        likes: 89
      },
      {
        id: 2,
        title: 'TypeScript 类型体操实战',
        summary: '通过实际案例学习 TypeScript 高级类型技巧，提升类型编程能力，写出更安全的代码。',
        cover: 'https://images.unsplash.com/photo-1516116216624-53e697fedbea?w=800&h=600&fit=crop',
        author: '落叶无痕',
        createdAt: new Date('2024-01-10'),
        tags: ['TypeScript', 'JavaScript'],
        views: 987,
        likes: 76
      },
      {
        id: 3,
        title: 'CSS 现代布局技术指南',
        summary: '全面介绍 Flexbox、Grid、Container Queries 等现代 CSS 布局技术，打造响应式页面。',
        cover: 'https://images.unsplash.com/photo-1507721999472-8ed4421c4af2?w=800&h=600&fit=crop',
        author: '落叶无痕',
        createdAt: new Date('2024-01-05'),
        tags: ['CSS', 'Web Design'],
        views: 856,
        likes: 65
      }
    ]

    // 加载第一页文章
    loadMoreArticles()
    
    loading.value = false
  }, 1000)

  // 添加滚动监听
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
  if (progressInterval) {
    clearInterval(progressInterval)
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

/* 加载进度条 - 初音未来风格 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f9f7 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xl);
  animation: fadeInScale 0.6s ease-out;
}

/* Icon Loading 动画 */
.loading-icon {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  border-radius: 50%;
  box-shadow: 0 8px 24px rgba(57, 197, 187, 0.3);
  animation: iconPulse 2s ease-in-out infinite;
}

.icon-spin {
  color: var(--color-white);
  animation: spin 2s linear infinite;
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 8px 24px rgba(57, 197, 187, 0.3);
  }
  50% {
    transform: scale(1.1);
    box-shadow: 0 12px 32px rgba(57, 197, 187, 0.5);
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.loading-subtitle {
  font-size: var(--text-lg);
  color: var(--color-miku-500);
  font-weight: var(--font-semibold);
  letter-spacing: 0.5px;
  text-align: center;
  min-height: 28px;
}

/* 简约电池格子进度条 */
.battery-bar {
  display: flex;
  gap: 4px;
  padding: 4px;
  background: transparent;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-gray-300);
}

.battery-cell {
  width: 20px;
  height: 28px;
  background: var(--color-gray-200);
  border-radius: 2px;
  transition: all 0.3s ease-out;
}

.battery-cell--active {
  background: linear-gradient(180deg, var(--color-miku-400), var(--color-cyan-500));
  box-shadow: 0 0 6px rgba(57, 197, 187, 0.4);
}

.loading-text {
  font-size: var(--text-base);
  color: var(--color-gray-600);
  font-weight: var(--font-medium);
  letter-spacing: 1px;
  font-family: 'Courier New', monospace;
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@media (max-width: 640px) {
  .loading-icon {
    width: 64px;
    height: 64px;
  }
  
  .loading-icon svg {
    width: 36px;
    height: 36px;
  }
  
  .loading-subtitle {
    font-size: var(--text-base);
  }
  
  .battery-bar {
    gap: 3px;
    padding: 3px;
  }
  
  .battery-cell {
    width: 16px;
    height: 24px;
  }
  
  .loading-text {
    font-size: var(--text-sm);
  }
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
  opacity: 0;
  transition: opacity 0.5s ease-in;
}

.hero-fullscreen.hero-loaded {
  opacity: 1;
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

/* 音乐组件特殊样式 */
.widget-music.is-playing .widget-icon {
  animation: pulse 1.5s ease-in-out infinite;
}

.widget-music.is-playing {
  border-color: var(--color-miku-400);
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
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
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
</style>
