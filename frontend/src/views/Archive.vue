<template>
  <div class="archive">
    <AppHeader />
    
    <main class="archive__main">
      <!-- 页面标题 -->
      <div class="archive-header">
        <div class="header-decoration">
          <Sparkles :size="32" class="header-icon" />
        </div>
        <h1 class="archive-title">文章归档</h1>
        <p class="archive-subtitle">时光荏苒，记录每一个精彩瞬间</p>
      </div>

      <!-- 搜索和筛选区 -->
      <div class="archive-filters">
        <!-- 搜索框 -->
        <div class="search-section">
          <div class="search-section-header">
            <div class="header-left">
              <Search :size="16" />
              <span class="search-section-title">搜索文章</span>
            </div>
          </div>
          <div class="search-box">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="搜索文章标题或内容..."
              class="search-input"
              @input="handleSearch"
            />
            <button v-if="searchQuery" class="search-clear" @click="clearSearch">
              <X :size="16" />
            </button>
          </div>
        </div>

        <!-- 标签筛选 - 横向展开，支持多选 -->
        <div class="filter-section">
          <div class="filter-section-header">
            <div class="header-left">
              <Tag :size="16" />
              <span class="filter-section-title">标签筛选</span>
            </div>
            <div class="header-right">
              <span v-if="selectedTags.length > 0" class="selected-count">
                已选 {{ selectedTags.length }} 个
              </span>
              <button v-if="hasActiveFilters" class="clear-filters-btn" @click="clearAllFilters">
                <X :size="14" />
                清除筛选
              </button>
            </div>
          </div>
          <div class="filter-tags-horizontal">
            <button
              class="filter-tag-btn"
              :class="{ 'is-active': selectedTags.length === 0 }"
              @click="clearTags"
            >
              <span class="tag-text">全部</span>
            </button>
            <button
              v-for="tag in allTags"
              :key="tag.id"
              class="filter-tag-btn"
              :class="{ 'is-active': selectedTags.includes(tag.id) }"
              @click="toggleTag(tag.id)"
            >
              <span class="tag-text">{{ tag.name }}</span>
              <span class="tag-count">({{ tag.articleCount }})</span>
              <span v-if="selectedTags.includes(tag.id)" class="tag-check">
                <Check :size="12" />
              </span>
            </button>
          </div>
        </div>
      </div>

      <!-- 文章列表标题栏 -->
      <div class="articles-header">
        <div class="articles-info">
          <span class="articles-count">共 {{ filteredArticles.length }} 篇文章</span>
        </div>
        <div class="articles-sort">
          <button
            v-for="option in sortOptions"
            :key="option.value"
            class="sort-btn"
            :class="{ 'is-active': sortBy === option.value }"
            @click="selectSort(option.value)"
          >
            {{ option.label }}
          </button>
        </div>
      </div>

      <!-- 文章列表 -->
      <div class="archive-content">
        <div v-if="loading" class="loading">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
        
        <div v-else-if="filteredArticles.length === 0" class="empty-state">
          <FileX :size="64" class="empty-icon" />
          <p class="empty-text">没有找到相关文章</p>
          <button class="empty-btn" @click="clearAllFilters">清除筛选条件</button>
        </div>
        
        <div v-else class="archive-list">
          <!-- 按年份分组 -->
          <div v-for="(group, year) in groupedArticles" :key="year" class="year-group">
            <div class="year-header">
              <span class="year-title">{{ year }}</span>
              <span class="year-count">{{ group.length }} 篇</span>
            </div>
            
            <div class="articles-list">
              <article
                v-for="article in group"
                :key="article.id"
                class="article-item"
                :class="{ 'has-cover': article.coverImage }"
                @click="goToArticle(article.id)"
              >
                <div class="article-main">
                  <div class="article-date">
                    <span class="date-text">{{ formatFullDate(article.publishedAt || article.createdAt) }}</span>
                  </div>
                  
                  <!-- 封面图片 -->
                  <div v-if="article.coverImage" class="article-cover">
                    <img :src="article.coverImage" :alt="article.title" />
                  </div>
                  
                  <div class="article-content">
                    <h3 class="article-title">{{ article.title }}</h3>
                    <p class="article-summary">{{ article.summary }}</p>
                    
                    <div class="article-footer">
                      <div class="article-meta">
                        <span class="meta-item">
                          <Eye :size="14" />
                          {{ article.viewCount || 0 }}
                        </span>
                        <span class="meta-item">
                          <Heart :size="14" />
                          {{ article.likeCount || 0 }}
                        </span>
                      </div>
                      
                      <div v-if="article.tags && article.tags.length" class="article-tags">
                        <span v-for="tag in article.tags" :key="tag.id" class="tag">
                          {{ tag.name }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="article-divider"></div>
              </article>
            </div>
          </div>
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
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Search, X, Tag, Calendar, Eye, Heart, FileX, ArrowUp, Check, Sparkles 
} from 'lucide-vue-next'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import { getArchiveArticles, getAllTags } from '@/api/article'
import type { Article } from '@/api/article'
import { message } from '@/utils/message'

const router = useRouter()
const loading = ref(true)
const showBackToTop = ref(false)

// 搜索和筛选状态
const searchQuery = ref('')
const selectedTags = ref<number[]>([]) // 存储标签ID
const sortBy = ref<'date-desc' | 'date-asc' | 'views' | 'likes'>('date-desc')

// 排序选项
const sortOptions = [
  { label: '最新发布', value: 'date-desc' as const },
  { label: '最早发布', value: 'date-asc' as const },
  { label: '最多浏览', value: 'views' as const },
  { label: '最多点赞', value: 'likes' as const }
]

// 数据
const allArticles = ref<Article[]>([])
const allTags = ref<Array<{ id: number; name: string; slug: string; articleCount: number }>>([])

// 是否有活动筛选
const hasActiveFilters = computed(() => {
  return !!(searchQuery.value || selectedTags.value.length > 0)
})

// 筛选后的文章（前端再次筛选搜索关键词）
const filteredArticles = computed(() => {
  let result = [...allArticles.value]

  // 前端搜索筛选（实时）
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(article =>
      article.title.toLowerCase().includes(query) ||
      (article.summary && article.summary.toLowerCase().includes(query))
    )
  }

  return result
})

// 按年份分组
const groupedArticles = computed(() => {
  const groups: Record<string, Article[]> = {}
  
  filteredArticles.value.forEach(article => {
    const date = article.publishedAt || article.createdAt
    if (date) {
      const year = new Date(date).getFullYear().toString()
      if (!groups[year]) {
        groups[year] = []
      }
      groups[year].push(article)
    }
  })
  
  return groups
})

// 加载文章数据
async function loadArticles() {
  try {
    loading.value = true
    const data = await getArchiveArticles({
      tagIds: selectedTags.value.length > 0 ? selectedTags.value : undefined,
      sortBy: sortBy.value
    })
    allArticles.value = data
  } catch (error) {
    console.error('加载文章失败:', error)
    message.error('加载文章失败')
  } finally {
    loading.value = false
  }
}

// 加载标签数据
async function loadTags() {
  try {
    const data = await getAllTags()
    allTags.value = data
  } catch (error) {
    console.error('加载标签失败:', error)
  }
}

// 搜索处理（实时搜索，不需要重新加载）
function handleSearch() {
  // 实时搜索，无需额外处理
}

function clearSearch() {
  searchQuery.value = ''
}

// 标签筛选 - 支持多选
function toggleTag(tagId: number) {
  const index = selectedTags.value.indexOf(tagId)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tagId)
  }
  // 标签变化时重新加载
  loadArticles()
}

function clearTags() {
  selectedTags.value = []
  loadArticles()
}

// 排序
function selectSort(sort: typeof sortBy.value) {
  sortBy.value = sort
  loadArticles()
}

// 清除所有筛选
function clearAllFilters() {
  searchQuery.value = ''
  selectedTags.value = []
  loadArticles()
}

// 日期格式化
function formatFullDate(date?: string) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', { 
    month: 'long', 
    day: 'numeric' 
  })
}

// 跳转到文章详情
function goToArticle(id?: number) {
  if (id) {
    router.push(`/article/${id}`)
  }
}

// 滚动处理
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
  loadArticles()
  loadTags()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.archive {
  min-height: 100vh;
  padding-top: 60px;
  overflow-x: hidden;
  background: var(--color-white);
}

.archive__main {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-3xl) var(--spacing-2xl);
}

/* 页面标题 */
.archive-header {
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

.archive-title {
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

.archive-subtitle {
  font-size: var(--text-xl);
  color: var(--color-gray-600);
  margin: 0;
  font-weight: var(--font-medium);
  line-height: 1.6;
}

/* 搜索和筛选区 */
.archive-filters {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-2xl);
}

/* 搜索区域 */
.search-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding: var(--spacing-lg) 0;
  border-bottom: 1px solid var(--color-gray-200);
}

.search-section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xs);
}

.search-section-header .header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.search-section-header svg {
  color: var(--color-miku-500);
  flex-shrink: 0;
}

.search-section-title {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-700);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.search-input {
  width: 100%;
  padding: var(--spacing-md) var(--spacing-lg);
  font-size: var(--text-base);
  border: 1px solid var(--color-gray-300);
  border-radius: 8px;
  background: var(--color-white);
  transition: all var(--transition-fast);
}

.search-input:focus {
  outline: none;
  border-color: var(--color-miku-400);
  box-shadow: 0 0 0 3px rgba(57, 197, 187, 0.1);
}

.search-input::placeholder {
  color: var(--color-gray-400);
}

.search-clear {
  position: absolute;
  right: var(--spacing-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: transparent;
  border: none;
  border-radius: 6px;
  color: var(--color-gray-400);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.search-clear:hover {
  background: var(--color-gray-100);
  color: var(--color-gray-700);
}



/* 筛选区域 - 简约线条风格 */
.filter-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding: var(--spacing-lg) 0;
  border-bottom: 1px solid var(--color-gray-200);
}

.filter-section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xs);
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.filter-section-header svg {
  color: var(--color-miku-500);
  flex-shrink: 0;
}

.filter-section-title {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-700);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.selected-count {
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  color: var(--color-miku-600);
}

.clear-filters-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px var(--spacing-sm);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  color: var(--color-pink-600);
  background: var(--color-pink-50);
  border: 1px solid var(--color-pink-200);
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
  white-space: nowrap;
}

.clear-filters-btn:hover {
  background: var(--color-pink-100);
  border-color: var(--color-pink-300);
  transform: translateY(-1px);
}

.clear-filters-btn svg {
  flex-shrink: 0;
}

/* 横向标签列表 */
.filter-tags-horizontal {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.filter-tag-btn {
  position: relative;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px var(--spacing-md);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-600);
  background: transparent;
  border: 1px solid var(--color-gray-300);
  border-radius: 8px;
  cursor: pointer;
  transition: all var(--transition-fast);
  white-space: nowrap;
}

.filter-tag-btn:hover {
  border-color: var(--color-miku-400);
  color: var(--color-miku-600);
  background: var(--color-miku-50);
}

.filter-tag-btn.is-active {
  background: var(--color-miku-500);
  color: var(--color-white);
  border-color: var(--color-miku-500);
  font-weight: var(--font-semibold);
}

.filter-tag-btn.is-active:hover {
  background: var(--color-miku-600);
  border-color: var(--color-miku-600);
}

.tag-text {
  line-height: 1;
}

.tag-count {
  font-size: var(--text-xs);
  color: var(--color-gray-400);
  margin-left: 2px;
}

.tag-check {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.tag-check svg {
  stroke-width: 3;
}

/* 文章列表标题栏 - 简洁线条风格 */
.articles-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-lg) 0;
  margin-bottom: var(--spacing-xl);
  border-bottom: 1px solid var(--color-gray-200);
}

.articles-info {
  display: flex;
  align-items: center;
}

.articles-count {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-600);
}

.articles-sort {
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
  white-space: nowrap;
  position: relative;
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

/* 文章列表 */
.archive-content {
  min-height: 400px;
}

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

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-3xl);
  text-align: center;
}

.empty-icon {
  color: var(--color-gray-300);
}

.empty-text {
  font-size: var(--text-lg);
  color: var(--color-gray-500);
  margin: 0;
}

.empty-btn {
  padding: var(--spacing-sm) var(--spacing-xl);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-white);
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.empty-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.3);
}

/* 年份分组 - 简洁风格 */
.year-group {
  margin-bottom: var(--spacing-3xl);
}

.year-header {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-sm);
  border-bottom: 1px solid var(--color-gray-200);
}

.year-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0;
}

.year-count {
  font-size: var(--text-sm);
  color: var(--color-gray-500);
  font-weight: var(--font-medium);
}

/* 文章列表 - 参考首页风格 */
.articles-list {
  display: flex;
  flex-direction: column;
}

.article-item {
  padding: var(--spacing-2xl) 0 var(--spacing-2xl) var(--spacing-md);
  cursor: pointer;
  position: relative;
  transition: all var(--transition-base);
}

/* 左侧强调线 */
.article-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 2px;
  height: 0;
  background: var(--color-miku-400);
  transition: height var(--transition-base);
}

.article-item:hover::before {
  height: 100%;
}

.article-main {
  display: flex;
  gap: var(--spacing-xl);
}

.article-date {
  display: flex;
  align-items: center;
  min-width: 80px;
  flex-shrink: 0;
}

.date-text {
  font-size: var(--text-sm);
  color: var(--color-gray-500);
  font-weight: var(--font-medium);
}

/* 封面图片 */
.article-cover {
  width: 200px;
  height: 140px;
  flex-shrink: 0;
  overflow: hidden;
  border-radius: 8px;
  border: 1px solid var(--color-gray-200);
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.article-item:hover .article-cover img {
  transform: scale(1.05);
}

.article-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.article-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
  line-height: 1.4;
  transition: color var(--transition-fast);
  margin: 0;
}

.article-item:hover .article-title {
  color: var(--color-cyan-500);
}

.article-summary {
  font-size: var(--text-base);
  color: var(--color-gray-600);
  line-height: 1.8;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-md);
  margin-top: auto;
}

.article-meta {
  display: flex;
  gap: var(--spacing-lg);
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--text-sm);
  color: var(--color-gray-500);
  font-weight: var(--font-medium);
}

.meta-item svg {
  color: var(--color-miku-400);
  flex-shrink: 0;
}

.article-tags {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.tag {
  font-size: var(--text-xs);
  padding: var(--spacing-xs) var(--spacing-md);
  color: var(--color-miku-500);
  border: 1px solid var(--color-miku-200);
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);
}

.article-item:hover .tag {
  background: var(--color-miku-50);
  border-color: var(--color-miku-400);
}

/* 分隔线 */
.article-divider {
  height: 1px;
  background: var(--color-gray-200);
  margin-top: var(--spacing-2xl);
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

.back-to-top svg {
  stroke-width: 2.5;
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
  .archive__main {
    padding: var(--spacing-xl) var(--spacing-lg);
  }

  .archive-header {
    margin-bottom: var(--spacing-xl);
  }

  .archive-header {
    padding: var(--spacing-2xl) 0;
  }

  .archive-title {
    font-size: var(--text-2xl);
  }

  .archive-subtitle {
    font-size: var(--text-lg);
  }

  .search-section {
    padding: var(--spacing-md) 0;
  }

  .search-section-title {
    font-size: var(--text-xs);
  }

  .filter-section {
    padding: var(--spacing-md) 0;
  }

  .filter-section-header {
    flex-wrap: wrap;
  }

  .header-left {
    flex: 1;
  }

  .filter-section-title {
    font-size: var(--text-xs);
  }

  .filter-tags-horizontal {
    gap: var(--spacing-xs);
  }

  .filter-tag-btn {
    font-size: var(--text-xs);
    padding: 6px var(--spacing-sm);
  }

  .articles-header {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: stretch;
    padding: var(--spacing-md);
  }

  .articles-info {
    justify-content: center;
  }

  .articles-sort {
    flex-wrap: wrap;
    justify-content: center;
  }

  .sort-btn {
    flex: 1;
    min-width: 80px;
  }

  .article-item {
    padding: var(--spacing-lg) 0;
  }

  .article-main {
    flex-direction: column;
    gap: var(--spacing-sm);
  }

  .article-date {
    min-width: auto;
  }

  .date-text {
    font-size: var(--text-xs);
  }

  .article-cover {
    width: 100%;
    height: 180px;
  }

  .article-title {
    font-size: var(--text-xl);
  }

  .article-summary {
    font-size: var(--text-sm);
  }

  .article-footer {
    flex-direction: column;
    align-items: flex-start;
  }

  .article-meta {
    gap: var(--spacing-md);
  }

  .meta-item {
    font-size: var(--text-xs);
  }

  .back-to-top {
    bottom: var(--spacing-xl);
    right: var(--spacing-lg);
    width: 44px;
    height: 44px;
  }
}
</style>
