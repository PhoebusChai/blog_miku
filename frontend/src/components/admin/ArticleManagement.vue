<template>
  <div class="article-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-bar-left">
        <div class="tab-group">
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'all' }"
            @click="activeTab = 'all'"
          >
            全部 ({{ localStats.total }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'published' }"
            @click="activeTab = 'published'"
          >
            已发布 ({{ localStats.published }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'draft' }"
            @click="activeTab = 'draft'"
          >
            草稿 ({{ localStats.draft }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'deleted' }"
            @click="activeTab = 'deleted'"
          >
            回收站
          </button>
        </div>
      </div>
      <div class="action-bar-center">
        <div class="search-box">
          <Search :size="18" />
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索文章标题或内容..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">搜索</button>
        </div>
        <select 
          v-model="tagFilter" 
          class="filter-select" 
          @change="handleTagFilterChange"
          :disabled="availableTags.length === 0"
        >
          <option value="">
            {{ availableTags.length === 0 ? '暂无标签' : '全部标签' }}
          </option>
          <option v-for="tag in availableTags" :key="tag.id" :value="tag.slug">
            {{ tag.name }} ({{ tag.articleCount }})
          </option>
        </select>
      </div>
      <div class="action-bar-right">
        <button class="primary-btn" @click="handleCreate">
          <Plus :size="18" />
          <span>新建文章</span>
        </button>
      </div>
    </div>

    <!-- 文章列表 -->
    <div class="article-list-wrapper">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>

      <table v-else class="article-table">
        <thead>
          <tr>
            <th class="col-title">标题</th>
            <th class="col-status">状态</th>
            <th class="col-tags">标签</th>
            <th class="col-stats">统计</th>
            <th class="col-date">创建时间</th>
            <th class="col-actions">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="article in paginatedArticles" :key="article.id" class="article-row">
            <td class="col-title">
              <div class="title-cell">
                <div class="article-title">{{ article.title }}</div>
                <div class="article-summary">{{ article.summary }}</div>
              </div>
            </td>
            <td class="col-status">
              <span class="status-badge" :class="`status-${article.status}`">
                {{ getStatusText(article.status) }}
              </span>
            </td>
            <td class="col-tags">
              <div class="tags-cell">
                <span v-for="tag in article.tags" :key="tag.id" class="tag-badge">
                  {{ tag.name }}
                </span>
              </div>
            </td>
            <td class="col-stats">
              <div class="stats-cell">
                <span class="stat-item">
                  <Eye :size="14" />
                  {{ article.viewCount || 0 }}
                </span>
                <span class="stat-item">
                  <MessageSquare :size="14" />
                  {{ article.commentCount || 0 }}
                </span>
              </div>
            </td>
            <td class="col-date">{{ formatDate(article.createdAt) }}</td>
            <td class="col-actions">
              <div class="actions-cell">
                <button class="action-icon-btn" @click="handleEdit(article.id)">
                  <Edit :size="16" />
                </button>
                <button class="action-icon-btn" @click="handleView(article.id)">
                  <Eye :size="16" />
                </button>
                <button
                  v-if="article.status !== 3"
                  class="action-icon-btn"
                  @click="handleDelete(article.id)"
                >
                  <Trash2 :size="16" />
                </button>
                <button v-else class="action-icon-btn" @click="handleRestore(article.id)">
                  <RotateCcw :size="16" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-if="!loading && filteredArticles.length === 0" class="empty-state">
        <FileText :size="48" class="empty-icon" />
        <p>暂无文章</p>
        <button class="primary-btn" @click="handleCreate">
          <Plus :size="18" />
          <span>创建第一篇文章</span>
        </button>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="filteredArticles.length > 0" class="pagination">
      <div class="pagination-info">共 {{ filteredArticles.length }} 条，每页 {{ pageSize }} 条</div>
      <div class="pagination-controls">
        <button class="pagination-btn" :disabled="currentPage === 1" @click="currentPage--">
          <ChevronLeft :size="16" />
        </button>
        <button
          v-for="page in displayPages"
          :key="page"
          class="pagination-btn"
          :class="{ active: page === currentPage }"
          @click="currentPage = page"
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import {
  Search,
  Plus,
  Eye,
  MessageSquare,
  Edit,
  Trash2,
  RotateCcw,
  FileText,
  ChevronLeft,
  ChevronRight
} from 'lucide-vue-next'
import { getMyArticles, deleteArticle, getAllTags, type Article } from '@/api/article'

// Props
interface Props {
  stats: {
    total: number
    published: number
    draft: number
  }
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  create: []
  edit: [id: number]
  view: [id: number]
  delete: [id: number]
  restore: [id: number]
  statsUpdate: [stats: { total: number; published: number; draft: number }]
}>()

// 状态
const activeTab = ref('all')
const searchKeyword = ref('')
const tagFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 文章数据
const articles = ref<Article[]>([])

// 标签列表
const availableTags = ref<Array<{ id: number; name: string; slug: string; articleCount: number }>>([])

// 统计数据（基于实际加载的文章）
const localStats = computed(() => {
  const total = articles.value.length
  const published = articles.value.filter(a => a.status === 1).length
  const draft = articles.value.filter(a => a.status === 0).length
  return { total, published, draft }
})

// 加载标签列表
async function loadTags() {
  try {
    const tags = await getAllTags()
    availableTags.value = tags || []
  } catch (error) {
    console.error('加载标签失败:', error)
  }
}

// 加载文章列表
async function loadArticles() {
  try {
    loading.value = true
    const result = await getMyArticles({
      page: currentPage.value,
      pageSize: pageSize.value
    })
    articles.value = result.records || []
    
    // 更新统计数据
    emit('statsUpdate', localStats.value)
  } catch (error) {
    console.error('加载文章失败:', error)
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(() => {
  loadArticles()
  loadTags()
})

// 监听 activeTab 变化，重置到第一页
watch(activeTab, () => {
  currentPage.value = 1
})

// 筛选文章
const filteredArticles = computed(() => {
  let result = articles.value

  if (activeTab.value === 'published') {
    result = result.filter((a) => a.status === 1)
  } else if (activeTab.value === 'draft') {
    result = result.filter((a) => a.status === 0)
  } else if (activeTab.value === 'deleted') {
    result = result.filter((a) => a.status === 3)
  }

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(
      (a) =>
        a.title.toLowerCase().includes(keyword) || 
        (a.summary && a.summary.toLowerCase().includes(keyword))
    )
  }

  if (tagFilter.value) {
    result = result.filter((a) =>
      a.tags?.some((tag) => 
        tag.slug === tagFilter.value || 
        tag.name.toLowerCase().includes(tagFilter.value.toLowerCase())
      )
    )
  }

  return result
})

// 分页
const totalPages = computed(() => Math.ceil(filteredArticles.value.length / pageSize.value))

const paginatedArticles = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredArticles.value.slice(start, end)
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
function getStatusText(status: number): string {
  const statusMap: Record<number, string> = {
    0: '草稿',
    1: '已发布',
    2: '仅自己可见',
    3: '已删除'
  }
  return statusMap[status] || '未知'
}

function handleCreate() {
  emit('create')
}

function handleEdit(id: number) {
  emit('edit', id)
}

function handleView(id: number) {
  emit('view', id)
}

async function handleDelete(id: number) {
  if (confirm('确定要删除这篇文章吗？')) {
    try {
      await deleteArticle(id)
      // 重新加载文章列表
      await loadArticles()
      emit('delete', id)
    } catch (error) {
      console.error('删除文章失败:', error)
      alert('删除失败，请重试')
    }
  }
}

function handleRestore(id: number) {
  emit('restore', id)
}

function handleSearch() {
  // 搜索逻辑已通过 computed 自动处理
  console.log('搜索关键词:', searchKeyword.value)
  currentPage.value = 1 // 重置到第一页
}

function handleTagFilterChange() {
  currentPage.value = 1 // 重置到第一页
}

// 格式化日期
function formatDate(dateString?: string): string {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}
</script>

<style scoped>
.article-management {
  width:100%;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.action-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
  padding: var(--spacing-lg) 1%;
  background: var(--color-white);
  border-bottom: 1px solid var(--color-gray-200);
  margin: 0;
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

.tab-group {
  display: inline-flex;
  background: var(--color-white);
  border-radius: 8px;
  padding: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.tab-btn {
  padding: var(--spacing-sm) var(--spacing-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-600);
  background: transparent;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
  white-space: nowrap;
}

.tab-btn:hover {
  color: var(--color-gray-900);
}

.tab-btn.active {
  background: var(--color-miku-500);
  color: var(--color-white);
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

.filter-select:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background: var(--color-gray-100);
}

.article-list-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow-y: auto;
}

.article-list-wrapper::-webkit-scrollbar {
  width: 6px;
}

.article-list-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.article-list-wrapper::-webkit-scrollbar-thumb {
  background: var(--color-gray-300);
  border-radius: 3px;
}

.article-list-wrapper::-webkit-scrollbar-thumb:hover {
  background: var(--color-gray-400);
}

.article-table {
  width: 100%;
  border-collapse: collapse;
  background: var(--color-white);
}

.article-table thead {
  position: sticky;
  top: 0;
  background: var(--color-gray-50);
  z-index: 10;
}

.article-table th {
  padding: var(--spacing-md) var(--spacing-lg);
  text-align: left;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-700);
  border-bottom: 2px solid var(--color-gray-200);
  white-space: nowrap;
}

.article-table th:first-child {
  padding-left: var(--spacing-lg);
}

.article-table th:last-child {
  padding-right: var(--spacing-lg);
}

.article-table td {
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--color-gray-100);
  vertical-align: middle;
}

.article-table td:first-child {
  padding-left: var(--spacing-lg);
}

.article-table td:last-child {
  padding-right: var(--spacing-lg);
}

.article-row:hover {
  background: var(--color-gray-50);
}

.col-title {
  width: 35%;
}

.col-status {
  width: 10%;
}

.col-tags {
  width: 20%;
}

.col-stats {
  width: 12%;
}

.col-date {
  width: 13%;
}

.col-actions {
  width: 10%;
}

.title-cell {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.article-title {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
  line-height: 1.4;
}

.article-summary {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.tags-cell {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
}

.tag-badge {
  display: inline-block;
  padding: 2px 8px;
  font-size: var(--text-xs);
  color: var(--color-miku-700);
  background: var(--color-miku-50);
  border-radius: 4px;
}

.stats-cell {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.stat-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: var(--text-sm);
  color: var(--color-gray-600);
}

.stat-item svg {
  flex-shrink: 0;
}

.col-date {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
}

.actions-cell {
  display: flex;
  gap: var(--spacing-xs);
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  border-radius: 12px;
}

.status-badge.status-0 {
  background: var(--color-gray-100);
  color: var(--color-gray-700);
}

.status-badge.status-1 {
  background: #d1fae5;
  color: #065f46;
}

.status-badge.status-2 {
  background: #fef3c7;
  color: #92400e;
}

.status-badge.status-3 {
  background: #fee2e2;
  color: #991b1b;
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

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl);
  text-align: center;
  color: var(--color-gray-500);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--color-gray-200);
  border-top-color: var(--color-miku-500);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: var(--spacing-lg);
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-state p {
  margin: 0;
  font-size: var(--text-base);
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-lg) 1%;
  background: var(--color-white);
  border-top: 1px solid var(--color-gray-200);
  margin: 0;
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
