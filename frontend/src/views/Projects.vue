<template>
  <div class="projects">
    <AppHeader />
    
    <main class="projects__main">
      <!-- 页面标题 -->
      <div class="projects-header">
        <h1 class="projects-title">项目</h1>
        <p class="projects-subtitle">我的开源项目和作品集 🚀</p>
      </div>

      <!-- 项目列表 -->
      <div class="projects-content">
        <div v-if="loading" class="loading">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
        
        <div v-else class="projects-grid">
          <article
            v-for="project in projects"
            :key="project.id"
            class="project-card"
          >
            <div class="project-cover">
              <img :src="project.cover" :alt="project.name" />
            </div>
            <div class="project-content">
              <h3 class="project-name">{{ project.name }}</h3>
              <p class="project-desc">{{ project.description }}</p>
              <div class="project-tags">
                <span v-for="tag in project.tags" :key="tag" class="tag">
                  {{ tag }}
                </span>
              </div>
              <div class="project-footer">
                <div class="project-stats">
                  <span class="stat-item">
                    <Star :size="14" />
                    {{ project.stars }}
                  </span>
                  <span class="stat-item">
                    <GitFork :size="14" />
                    {{ project.forks }}
                  </span>
                </div>
                <a :href="project.url" target="_blank" class="project-link">
                  查看项目
                  <ExternalLink :size="14" />
                </a>
              </div>
            </div>
          </article>
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
import { ref, onMounted, onUnmounted } from 'vue'
import { Star, GitFork, ExternalLink, ArrowUp } from 'lucide-vue-next'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'

const loading = ref(true)
const showBackToTop = ref(false)

interface Project {
  id: number
  name: string
  description: string
  cover: string
  tags: string[]
  stars: number
  forks: number
  url: string
}

const projects = ref<Project[]>([
  {
    id: 1,
    name: '个人博客系统',
    description: '基于 Vue 3 + TypeScript 开发的现代化博客系统，支持文章管理、标签分类、评论互动等功能。',
    cover: 'https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=800&h=600&fit=crop',
    tags: ['Vue 3', 'TypeScript', 'Vite'],
    stars: 128,
    forks: 32,
    url: 'https://github.com'
  },
  {
    id: 2,
    name: 'UI 组件库',
    description: '轻量级的 Vue 3 组件库，提供常用的 UI 组件，支持主题定制和按需引入。',
    cover: 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800&h=600&fit=crop',
    tags: ['Vue 3', 'Components', 'CSS'],
    stars: 256,
    forks: 48,
    url: 'https://github.com'
  },
  {
    id: 3,
    name: '在线代码编辑器',
    description: '支持多语言的在线代码编辑器，提供语法高亮、代码补全、实时预览等功能。',
    cover: 'https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=800&h=600&fit=crop',
    tags: ['React', 'Monaco Editor', 'WebAssembly'],
    stars: 189,
    forks: 41,
    url: 'https://github.com'
  },
  {
    id: 4,
    name: '任务管理工具',
    description: '简洁高效的任务管理工具，支持项目分组、任务标签、进度跟踪等功能。',
    cover: 'https://images.unsplash.com/photo-1484480974693-6ca0a78fb36b?w=800&h=600&fit=crop',
    tags: ['Vue 3', 'Pinia', 'IndexedDB'],
    stars: 94,
    forks: 23,
    url: 'https://github.com'
  },
  {
    id: 5,
    name: 'Markdown 编辑器',
    description: '功能丰富的 Markdown 编辑器，支持实时预览、导出 PDF、图片上传等功能。',
    cover: 'https://images.unsplash.com/photo-1516116216624-53e697fedbea?w=800&h=600&fit=crop',
    tags: ['TypeScript', 'Markdown', 'Electron'],
    stars: 312,
    forks: 67,
    url: 'https://github.com'
  },
  {
    id: 6,
    name: '数据可视化平台',
    description: '基于 ECharts 的数据可视化平台，提供丰富的图表类型和交互功能。',
    cover: 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&h=600&fit=crop',
    tags: ['Vue 3', 'ECharts', 'D3.js'],
    stars: 176,
    forks: 38,
    url: 'https://github.com'
  }
])

function handleScroll() {
  showBackToTop.value = window.pageYOffset > 300
}

function scrollToTop() {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

onMounted(() => {
  setTimeout(() => {
    loading.value = false
  }, 500)
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.projects {
  min-height: 100vh;
  padding-top: 60px;
  overflow-x: hidden;
  background: var(--color-white);
}

.projects__main {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-3xl) var(--spacing-2xl);
}

/* 页面标题 */
.projects-header {
  text-align: center;
  margin-bottom: var(--spacing-3xl);
  padding: var(--spacing-3xl) 0 var(--spacing-2xl);
}

.projects-title {
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

.projects-subtitle {
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

/* 项目网格 */
.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: var(--spacing-2xl);
}

.project-card {
  display: flex;
  flex-direction: column;
  background: var(--color-white);
  border: 1px solid var(--color-gray-200);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: all var(--transition-base);
}

.project-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: var(--color-miku-300);
}

.project-cover {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.project-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.project-card:hover .project-cover img {
  transform: scale(1.05);
}

.project-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding: var(--spacing-xl);
  flex: 1;
}

.project-name {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0;
}

.project-desc {
  font-size: var(--text-base);
  color: var(--color-gray-600);
  line-height: 1.6;
  margin: 0;
  flex: 1;
}

.project-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.tag {
  font-size: var(--text-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  color: var(--color-miku-600);
  background: var(--color-miku-50);
  border: 1px solid var(--color-miku-200);
  border-radius: var(--radius-md);
  font-weight: var(--font-medium);
}

.project-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-gray-200);
}

.project-stats {
  display: flex;
  gap: var(--spacing-lg);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  font-weight: var(--font-medium);
}

.stat-item svg {
  color: var(--color-miku-500);
}

.project-link {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-miku-600);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.project-link:hover {
  color: var(--color-miku-700);
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
  .projects__main {
    padding: var(--spacing-xl) var(--spacing-lg);
  }

  .projects-header {
    padding: var(--spacing-2xl) 0;
  }

  .projects-title {
    font-size: var(--text-2xl);
  }

  .projects-subtitle {
    font-size: var(--text-lg);
  }

  .projects-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-xl);
  }

  .back-to-top {
    bottom: var(--spacing-xl);
    right: var(--spacing-lg);
    width: 44px;
    height: 44px;
  }
}
</style>
