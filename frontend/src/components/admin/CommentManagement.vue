<template>
  <div class="comment-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-bar-left">
        <div class="tab-group">
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'all' }"
            @click="activeTab = 'all'"
          >
            全部 ({{ stats.total }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'approved' }"
            @click="activeTab = 'approved'"
          >
            已通过 ({{ stats.approved }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'pending' }"
            @click="activeTab = 'pending'"
          >
            待审核 ({{ stats.pending }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'spam' }"
            @click="activeTab = 'spam'"
          >
            垃圾评论
          </button>
        </div>
      </div>
      <div class="action-bar-center">
        <div class="search-box">
          <Search :size="18" />
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索评论内容或用户..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">搜索</button>
        </div>
        <select v-model="articleFilter" class="filter-select">
          <option value="">全部文章</option>
          <option value="1">欢迎来到我的博客</option>
          <option value="2">Vue 3 Composition API 实践</option>
        </select>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list-wrapper">
      <table class="comment-table">
        <thead>
          <tr>
            <th class="col-author">作者</th>
            <th class="col-content">评论内容</th>
            <th class="col-article">所属文章</th>
            <th class="col-status">状态</th>
            <th class="col-date">评论时间</th>
            <th class="col-actions">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="comment in paginatedComments" :key="comment.id" class="comment-row">
            <td class="col-author">
              <div class="author-cell">
                <div class="author-avatar">
                  <User :size="20" />
                </div>
                <div class="author-info">
                  <div class="author-name">{{ comment.author }}</div>
                  <div class="author-email">{{ comment.email }}</div>
                </div>
              </div>
            </td>
            <td class="col-content">
              <div class="content-cell">
                <div class="comment-text">{{ comment.content }}</div>
                <div v-if="comment.replyTo" class="reply-info">
                  <CornerDownRight :size="14" />
                  回复 {{ comment.replyTo }}
                </div>
              </div>
            </td>
            <td class="col-article">
              <div class="article-link">{{ comment.articleTitle }}</div>
            </td>
            <td class="col-status">
              <span class="status-badge" :class="`status-${comment.status}`">
                {{ getStatusText(comment.status) }}
              </span>
            </td>
            <td class="col-date">{{ comment.createdAt }}</td>
            <td class="col-actions">
              <div class="actions-cell">
                <button
                  v-if="comment.status === 0"
                  class="action-icon-btn"
                  title="通过"
                  @click="handleApprove(comment.id)"
                >
                  <Check :size="16" />
                </button>
                <button
                  v-if="comment.status === 1"
                  class="action-icon-btn"
                  title="取消通过"
                  @click="handleUnapprove(comment.id)"
                >
                  <X :size="16" />
                </button>
                <button class="action-icon-btn" title="回复" @click="handleReply(comment.id)">
                  <MessageSquare :size="16" />
                </button>
                <button
                  class="action-icon-btn"
                  title="标记为垃圾"
                  @click="handleMarkSpam(comment.id)"
                >
                  <AlertCircle :size="16" />
                </button>
                <button
                  class="action-icon-btn"
                  title="删除"
                  @click="handleDelete(comment.id)"
                >
                  <Trash2 :size="16" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-if="filteredComments.length === 0" class="empty-state">
        <MessageSquare :size="48" class="empty-icon" />
        <p>暂无评论</p>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="filteredComments.length > 0" class="pagination">
      <div class="pagination-info">共 {{ filteredComments.length }} 条，每页 {{ pageSize }} 条</div>
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
import { ref, computed } from 'vue'
import {
  Search,
  User,
  MessageSquare,
  Check,
  X,
  AlertCircle,
  Trash2,
  CornerDownRight,
  ChevronLeft,
  ChevronRight
} from 'lucide-vue-next'

// Props
interface Props {
  stats: {
    total: number
    approved: number
    pending: number
  }
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  approve: [id: number]
  unapprove: [id: number]
  reply: [id: number]
  markSpam: [id: number]
  delete: [id: number]
}>()

// 状态
const activeTab = ref('all')
const searchKeyword = ref('')
const articleFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

// Mock 数据
const comments = ref([
  {
    id: 1,
    author: '张三',
    email: 'zhangsan@example.com',
    content: '写得很好，学到了很多东西！期待更多精彩内容。',
    articleTitle: '欢迎来到我的博客',
    status: 1, // 0: 待审核, 1: 已通过, 2: 垃圾评论
    replyTo: null,
    createdAt: '2024-12-19 10:30'
  },
  {
    id: 2,
    author: '李四',
    email: 'lisi@example.com',
    content: 'Vue 3 的 Composition API 确实很强大，感谢分享！',
    articleTitle: 'Vue 3 Composition API 实践',
    status: 1,
    replyTo: null,
    createdAt: '2024-12-19 14:20'
  },
  {
    id: 3,
    author: '王五',
    email: 'wangwu@example.com',
    content: '请问这个功能在 Vue 2 中可以实现吗？',
    articleTitle: 'Vue 3 Composition API 实践',
    status: 0,
    replyTo: null,
    createdAt: '2024-12-19 16:45'
  }
])

// 筛选评论
const filteredComments = computed(() => {
  let result = comments.value

  if (activeTab.value === 'approved') {
    result = result.filter((c) => c.status === 1)
  } else if (activeTab.value === 'pending') {
    result = result.filter((c) => c.status === 0)
  } else if (activeTab.value === 'spam') {
    result = result.filter((c) => c.status === 2)
  }

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(
      (c) =>
        c.content.toLowerCase().includes(keyword) ||
        c.author.toLowerCase().includes(keyword) ||
        c.email.toLowerCase().includes(keyword)
    )
  }

  if (articleFilter.value) {
    result = result.filter((c) => c.articleTitle.includes(articleFilter.value))
  }

  return result
})

// 分页
const totalPages = computed(() => Math.ceil(filteredComments.value.length / pageSize.value))

const paginatedComments = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredComments.value.slice(start, end)
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
    0: '待审核',
    1: '已通过',
    2: '垃圾评论'
  }
  return statusMap[status] || '未知'
}

function handleApprove(id: number) {
  emit('approve', id)
}

function handleUnapprove(id: number) {
  emit('unapprove', id)
}

function handleReply(id: number) {
  emit('reply', id)
}

function handleMarkSpam(id: number) {
  if (confirm('确定要标记为垃圾评论吗？')) {
    emit('markSpam', id)
  }
}

function handleDelete(id: number) {
  if (confirm('确定要删除这条评论吗？此操作不可恢复。')) {
    emit('delete', id)
  }
}

function handleSearch() {
  // 搜索逻辑已通过 computed 自动处理
  console.log('搜索关键词:', searchKeyword.value)
}
</script>

<style scoped>
.comment-management {
  display: flex;
  flex-direction: column;
  flex: 1;
  width:100%;
  min-height: 0;
}

.action-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
  padding: var(--spacing-lg) 2%;
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
  min-width: 180px;
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

.comment-list-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow-y: auto;
}

.comment-list-wrapper::-webkit-scrollbar {
  width: 6px;
}

.comment-list-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.comment-list-wrapper::-webkit-scrollbar-thumb {
  background: var(--color-gray-300);
  border-radius: 3px;
}

.comment-list-wrapper::-webkit-scrollbar-thumb:hover {
  background: var(--color-gray-400);
}

.comment-table {
  width: 100%;
  border-collapse: collapse;
  background: var(--color-white);
}

.comment-table thead {
  position: sticky;
  top: 0;
  background: var(--color-gray-50);
  z-index: 10;
}

.comment-table th {
  padding: var(--spacing-md) var(--spacing-lg);
  text-align: left;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-700);
  border-bottom: 2px solid var(--color-gray-200);
  white-space: nowrap;
}

.comment-table th:first-child {
  padding-left: var(--spacing-lg);
}

.comment-table th:last-child {
  padding-right: var(--spacing-lg);
}

.comment-table td {
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--color-gray-100);
  vertical-align: middle;
}

.comment-table td:first-child {
  padding-left: var(--spacing-lg);
}

.comment-table td:last-child {
  padding-right: var(--spacing-lg);
}

.comment-row:hover {
  background: var(--color-gray-50);
}

.col-author {
  width: 18%;
}

.col-content {
  width: 35%;
}

.col-article {
  width: 18%;
}

.col-status {
  width: 10%;
}

.col-date {
  width: 12%;
}

.col-actions {
  width: 7%;
}

.author-cell {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.author-avatar {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-miku-100);
  color: var(--color-miku-600);
  border-radius: 50%;
  flex-shrink: 0;
}

.author-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.author-name {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.author-email {
  font-size: var(--text-xs);
  color: var(--color-gray-600);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.content-cell {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.comment-text {
  font-size: var(--text-sm);
  color: var(--color-gray-900);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.reply-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--text-xs);
  color: var(--color-gray-500);
}

.article-link {
  font-size: var(--text-sm);
  color: var(--color-miku-600);
  cursor: pointer;
  transition: color var(--transition-fast);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-link:hover {
  color: var(--color-miku-700);
  text-decoration: underline;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  border-radius: 12px;
  white-space: nowrap;
}

.status-badge.status-0 {
  background: #fef3c7;
  color: #92400e;
}

.status-badge.status-1 {
  background: #d1fae5;
  color: #065f46;
}

.status-badge.status-2 {
  background: #fee2e2;
  color: #991b1b;
}

.col-date {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  white-space: nowrap;
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
  margin: 0;
  font-size: var(--text-base);
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-lg) 2%;
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
