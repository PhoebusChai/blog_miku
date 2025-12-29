<template>
  <div class="projects">
    <AppHeader />
    
    <main class="projects__main">
      <!-- 页面标题 -->
      <div class="projects-header">
        <div class="header-decoration">
          <Rocket :size="32" class="header-icon" />
        </div>
        <h1 class="projects-title">我的项目</h1>
        <p class="projects-subtitle">探索我的开源项目和作品集</p>
        
      </div>

      <!-- 筛选和排序 -->
      <div class="projects-filters">
        <div class="filter-section">
          <div class="filter-buttons">
            <button 
              v-for="filter in filters" 
              :key="filter.value"
              :class="['filter-btn', { 'is-active': activeFilter === filter.value }]"
              @click="activeFilter = filter.value"
            >
              {{ filter.label }}
            </button>
          </div>
        </div>
        <div class="sort-section">
          <button
            v-for="option in sortOptions"
            :key="option.value"
            :class="['sort-btn', { 'is-active': sortBy === option.value }]"
            @click="sortBy = option.value"
          >
            {{ option.label }}
          </button>
        </div>
      </div>

      <!-- 项目列表 -->
      <div class="projects-content">
        <div v-if="loading" class="loading">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
        
        <transition-group 
          v-else 
          name="project-list" 
          tag="div" 
          class="projects-list"
        >
          <article
            v-for="project in filteredProjects"
            :key="project.id"
            class="project-item"
          >
            <div class="project-thumbnail">
              <img :src="project.cover" :alt="project.name" loading="lazy" />
            </div>
            <div class="project-info">
              <div class="project-main">
                <div class="project-title-row">
                  <h3 class="project-name">{{ project.name }}</h3>
                  <span v-if="project.featured" class="featured-badge">
                    <Sparkles :size="14" />
                    精选
                  </span>
                </div>
                <p class="project-desc">{{ project.description }}</p>
                <div class="project-tags">
                  <span v-for="tag in project.tags" :key="tag" class="tag">
                    {{ tag }}
                  </span>
                </div>
              </div>
              <div class="project-meta">
                <div class="project-stats">
                  <span class="stat-item" :title="`${project.stars} stars`">
                    <Star :size="16" />
                    {{ formatNumber(project.stars) }}
                  </span>
                  <span class="stat-item" :title="`${project.forks} forks`">
                    <GitFork :size="16" />
                    {{ formatNumber(project.forks) }}
                  </span>
                  <span v-if="project.language" class="stat-item">
                    <Code :size="16" />
                    {{ project.language }}
                  </span>
                </div>
                <a :href="project.url" target="_blank" class="project-link">
                  查看项目
                  <ExternalLink :size="16" />
                </a>
              </div>
            </div>
          </article>
        </transition-group>

        <!-- 空状态 -->
        <div v-if="!loading && filteredProjects.length === 0" class="empty-state">
          <Inbox :size="64" />
          <p>暂无匹配的项目</p>
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
import { Star, GitFork, ExternalLink, ArrowUp, Rocket, Sparkles, Code, Inbox } from 'lucide-vue-next'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import { getProjects, type Project } from '@/api/project'
import { message } from '@/utils/message'

const loading = ref(true)
const showBackToTop = ref(false)
const activeFilter = ref('all')
const sortBy = ref('latest')

interface DisplayProject {
  id: number
  name: string
  description: string
  cover: string
  tags: string[]
  stars: number
  forks: number
  url: string
  category?: string
  language?: string
  featured?: boolean
}

const filters = [
  { label: '全部', value: 'all' },
  { label: '进行中', value: 'active' },
  { label: '已完成', value: 'completed' },
  { label: '已归档', value: 'archived' }
]

const sortOptions = [
  { label: '最新', value: 'latest' },
  { label: '最多浏览', value: 'views' },
  { label: '按名称', value: 'name' }
]

const projects = ref<DisplayProject[]>([])

// 转换API数据为显示格式
function convertProject(project: Project): DisplayProject {
  const tags = project.techStack ? project.techStack.split(',').map(t => t.trim()).filter(Boolean) : []
  return {
    id: project.id || 0,
    name: project.title,
    description: project.description || '暂无描述',
    cover: project.coverImage || 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800&h=600&fit=crop',
    tags,
    stars: 0,
    forks: 0,
    url: project.demoUrl || project.githubUrl || '#',
    category: getStatusCategory(project.status),
    language: tags[0] || '',
    featured: project.sortOrder !== undefined && project.sortOrder > 0
  }
}

function getStatusCategory(status?: number): string {
  switch (status) {
    case 1: return 'active'
    case 2: return 'completed'
    case 3: return 'archived'
    default: return 'active'
  }
}

const filteredProjects = computed(() => {
  let result = projects.value

  // 筛选
  if (activeFilter.value !== 'all') {
    result = result.filter(project => project.category === activeFilter.value)
  }

  // 排序
  result = [...result].sort((a, b) => {
    if (sortBy.value === 'views') {
      return b.stars - a.stars
    } else if (sortBy.value === 'name') {
      return a.name.localeCompare(b.name)
    }
    // 默认按最新（id倒序）
    return b.id - a.id
  })

  return result
})

// 加载项目数据
async function loadProjects() {
  loading.value = true
  try {
    const data = await getProjects()
    projects.value = data.map(convertProject)
  } catch (error: any) {
    message.error(error.message || '加载项目失败')
  } finally {
    loading.value = false
  }
}

function formatNumber(num: number): string {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

function handleScroll() {
  showBackToTop.value = window.pageYOffset > 300
}

function scrollToTop() {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

onMounted(async () => {
  await loadProjects()
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

.header-decoration {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  margin-bottom: var(--spacing-lg);
  background: linear-gradient(135deg, var(--color-miku-100), var(--color-cyan-100));
  border-radius: 50%;
  animation: float 3s ease-in-out infinite;
}

.header-icon {
  color: var(--color-miku-600);
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
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
  margin: 0 0 var(--spacing-2xl) 0;
  font-weight: var(--font-medium);
  line-height: 1.6;
}

/* 筛选和排序 - 简洁线条风格 */
.projects-filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg) 0;
  margin-bottom: var(--spacing-xl);
  border-bottom: 1px solid var(--color-gray-200);
  gap: var(--spacing-lg);
}

.filter-section {
  flex: 1;
}

.filter-buttons {
  display: flex;
  gap: var(--spacing-xs);
  flex-wrap: wrap;
}

.filter-btn {
  padding: 6px var(--spacing-md);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-600);
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all var(--transition-fast);
  position: relative;
}

.filter-btn::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--color-miku-400);
  transition: width var(--transition-fast);
}

.filter-btn:hover {
  color: var(--color-miku-600);
}

.filter-btn.is-active {
  color: var(--color-miku-600);
  font-weight: var(--font-semibold);
}

.filter-btn.is-active::after {
  width: 100%;
}

.sort-section {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.sort-btn {
  padding: 6px var(--spacing-md);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-600);
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all var(--transition-fast);
  position: relative;
  white-space: nowrap;
}

.sort-btn::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--color-miku-400);
  transition: width var(--transition-fast);
}

.sort-btn:hover {
  color: var(--color-miku-600);
}

.sort-btn.is-active {
  color: var(--color-miku-600);
  font-weight: var(--font-semibold);
}

.sort-btn.is-active::after {
  width: 100%;
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

/* 项目列表 */
.projects-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.project-item {
  display: flex;
  gap: var(--spacing-xl);
  padding: var(--spacing-2xl) 0 var(--spacing-2xl) var(--spacing-md);
  position: relative;
  transition: all var(--transition-base);
  cursor: pointer;
}

/* 左侧强调线 */
.project-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 2px;
  height: 0;
  background: var(--color-miku-400);
  transition: height var(--transition-base);
}

.project-item:hover::before {
  height: 100%;
}

.project-thumbnail {
  flex-shrink: 0;
  width: 200px;
  height: 140px;
  border-radius: 8px;
  overflow: hidden;
  background: var(--color-gray-100);
  border: 1px solid var(--color-gray-200);
}

.project-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.project-item:hover .project-thumbnail img {
  transform: scale(1.05);
}

.project-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: var(--spacing-md);
  min-width: 0;
}

.project-main {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.project-title-row {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.project-name {
  font-size: var(--text-2xl);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
  margin: 0;
  line-height: 1.4;
  transition: color var(--transition-fast);
}

.project-item:hover .project-name {
  color: var(--color-cyan-500);
}

.featured-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  color: var(--color-yellow-700);
  background: linear-gradient(135deg, var(--color-yellow-100), var(--color-yellow-200));
  border: 1px solid var(--color-yellow-300);
  border-radius: var(--radius-md);
  white-space: nowrap;
}

.project-desc {
  font-size: var(--text-base);
  color: var(--color-gray-600);
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.project-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.tag {
  font-size: var(--text-xs);
  padding: var(--spacing-xs) var(--spacing-md);
  color: var(--color-miku-500);
  border: 1px solid var(--color-miku-200);
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);
}

.project-item:hover .tag {
  background: var(--color-miku-50);
  border-color: var(--color-miku-400);
}

.project-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--spacing-lg);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-gray-200);
}

.project-stats {
  display: flex;
  gap: var(--spacing-lg);
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  font-weight: var(--font-medium);
  transition: color var(--transition-fast);
}

.stat-item:hover {
  color: var(--color-miku-600);
}

.stat-item svg {
  color: var(--color-miku-500);
  flex-shrink: 0;
}

.project-link {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-miku-600);
  background: var(--color-miku-50);
  border: 1px solid var(--color-miku-200);
  border-radius: 6px;
  text-decoration: none;
  transition: all var(--transition-fast);
  white-space: nowrap;
}

.project-link:hover {
  color: var(--color-white);
  background: linear-gradient(135deg, var(--color-miku-500), var(--color-cyan-500));
  border-color: transparent;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.3);
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-3xl);
  color: var(--color-gray-500);
}

.empty-state svg {
  color: var(--color-gray-400);
}

.empty-state p {
  font-size: var(--text-lg);
  margin: 0;
}

/* 列表动画 */
.project-list-move,
.project-list-enter-active,
.project-list-leave-active {
  transition: all 0.5s ease;
}

.project-list-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.project-list-leave-to {
  opacity: 0;
  transform: scale(0.9);
}

.project-list-leave-active {
  position: absolute;
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

  .header-decoration {
    width: 64px;
    height: 64px;
  }

  .header-icon {
    width: 24px;
    height: 24px;
  }

  .projects-title {
    font-size: var(--text-2xl);
  }

  .projects-subtitle {
    font-size: var(--text-lg);
  }

  .projects-filters {
    flex-direction: column;
    align-items: stretch;
    gap: var(--spacing-md);
  }

  .filter-section,
  .sort-section {
    width: 100%;
  }

  .filter-buttons,
  .sort-section {
    justify-content: center;
  }

  .project-item {
    flex-direction: column;
    gap: var(--spacing-md);
  }

  .project-thumbnail {
    width: 100%;
    height: 180px;
  }

  .project-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .project-link {
    width: 100%;
    justify-content: center;
  }

  .back-to-top {
    bottom: var(--spacing-xl);
    right: var(--spacing-lg);
    width: 44px;
    height: 44px;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .project-thumbnail {
    width: 160px;
    height: 120px;
  }
}
</style>
