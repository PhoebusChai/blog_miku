<template>
  <div class="comment-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-bar-left">
        <div class="tab-group">
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'all' }"
            @click="switchTab('all')"
          >
            全部 ({{ commentStats.total }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'approved' }"
            @click="switchTab('approved')"
          >
            已通过 ({{ commentStats.approved }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'pending' }"
            @click="switchTab('pending')"
          >
            待审核 ({{ commentStats.pending }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'spam' }"
            @click="switchTab('spam')"
          >
            垃圾评论 ({{ commentStats.spam }})
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
        <select v-model="articleFilter" class="filter-select" @change="handleFilterChange">
          <option value="">全部文章</option>
          <option value="guestbook">留言板</option>
          <option v-for="article in articles" :key="article.id" :value="article.id">
            {{ article.title }}
          </option>
        </select>
      </div>
    </div>

    <!-- 批量操作栏 -->
    <div v-if="selectedIds.length > 0" class="batch-action-bar">
      <span class="selected-count">已选择 {{ selectedIds.length }} 条评论</span>
      <div class="batch-actions">
        <button class="batch-btn batch-btn--approve" @click="handleBatchApprove">
          <Check :size="16" />
          批量通过
        </button>
        <button class="batch-btn batch-btn--spam" @click="handleBatchSpam">
          <AlertCircle :size="16" />
          批量标记垃圾
        </button>
        <button class="batch-btn batch-btn--delete" @click="handleBatchDelete">
          <Trash2 :size="16" />
          批量删除
        </button>
        <button class="batch-btn batch-btn--cancel" @click="clearSelection">
          取消选择
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <Loader2 :size="32" class="loading-icon" />
      <p>加载中...</p>
    </div>

    <!-- 评论列表 -->
    <div v-else class="comment-list-wrapper">
      <table class="comment-table">
        <thead>
          <tr>
            <th class="col-checkbox">
              <input
                type="checkbox"
                :checked="isAllSelected"
                :indeterminate="isIndeterminate"
                @change="toggleSelectAll"
              />
            </th>
            <th class="col-author">作者</th>
            <th class="col-content">评论内容</th>
            <th class="col-article">所属文章</th>
            <th class="col-status">状态</th>
            <th class="col-date">评论时间</th>
            <th class="col-actions">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="comment in comments" :key="comment.id" class="comment-row">
            <td class="col-checkbox">
              <input
                type="checkbox"
                :checked="selectedIds.includes(comment.id)"
                @change="toggleSelect(comment.id)"
              />
            </td>
            <td class="col-author">
              <div class="author-cell">
                <div class="author-avatar">
                  <img v-if="comment.userAvatar" :src="comment.userAvatar" alt="avatar" />
                  <User v-else :size="20" />
                </div>
                <div class="author-info">
                  <div class="author-name">{{ comment.userName || comment.guestName || '匿名用户' }}</div>
                  <div class="author-email">{{ comment.guestEmail || '-' }}</div>
                </div>
              </div>
            </td>
            <td class="col-content">
              <div class="content-cell">
                <div class="comment-text">{{ comment.content }}</div>
                <div v-if="comment.parentId" class="reply-info">
                  <CornerDownRight :size="14" />
                  回复评论 #{{ comment.parentId }}
                </div>
              </div>
            </td>
            <td class="col-article">
              <div class="article-link" @click="viewArticle(comment.articleId)">
                {{ comment.articleTitle || '留言板' }}
              </div>
            </td>
            <td class="col-status">
              <span class="status-badge" :class="`status-${comment.status}`">
                {{ getStatusText(comment.status) }}
              </span>
            </td>
            <td class="col-date">{{ formatDate(comment.createdAt) }}</td>
            <td class="col-actions">
              <div class="actions-cell">
                <button
                  v-if="comment.status !== 1"
                  type="button"
                  class="action-icon-btn action-icon-btn--approve"
                  title="通过"
                  @click.stop="handleApprove(comment.id)"
                >
                  <Check :size="16" />
                </button>
                <button
                  v-if="comment.status === 1"
                  type="button"
                  class="action-icon-btn action-icon-btn--reject"
                  title="取消通过"
                  @click.stop="handleReject(comment.id)"
                >
                  <X :size="16" />
                </button>
                <button
                  type="button"
                  class="action-icon-btn"
                  title="回复"
                  @click.stop="openReplyModal(comment)"
                >
                  <MessageSquare :size="16" />
                </button>
                <button
                  v-if="comment.status !== 3"
                  type="button"
                  class="action-icon-btn action-icon-btn--spam"
                  title="标记为垃圾"
                  @click.stop="handleMarkSpam(comment.id)"
                >
                  <AlertCircle :size="16" />
                </button>
                <button
                  v-if="comment.status === 3"
                  type="button"
                  class="action-icon-btn action-icon-btn--restore"
                  title="恢复为待审核"
                  @click.stop="handleRestore(comment.id)"
                >
                  <RotateCcw :size="16" />
                </button>
                <button
                  type="button"
                  class="action-icon-btn action-icon-btn--delete"
                  title="删除"
                  @click.stop="handleDelete(comment.id)"
                >
                  <Trash2 :size="16" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-if="comments.length === 0" class="empty-state">
        <MessageSquare :size="48" class="empty-icon" />
        <p>暂无评论</p>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="!loading && total > 0" class="pagination">
      <div class="pagination-info">共 {{ total }} 条，每页 {{ pageSize }} 条</div>
      <div class="pagination-controls">
        <button class="pagination-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">
          <ChevronLeft :size="16" />
        </button>
        <button
          v-for="page in displayPages"
          :key="page"
          class="pagination-btn"
          :class="{ active: page === currentPage, ellipsis: page === '...' }"
          :disabled="page === '...'"
          @click="page !== '...' && changePage(page as number)"
        >
          {{ page }}
        </button>
        <button
          class="pagination-btn"
          :disabled="currentPage === totalPages"
          @click="changePage(currentPage + 1)"
        >
          <ChevronRight :size="16" />
        </button>
      </div>
    </div>

    <!-- 回复弹窗 -->
    <div v-if="showReplyModal" class="modal-overlay" @click.self="closeReplyModal">
      <div class="reply-modal">
        <div class="modal-header">
          <h3>回复评论</h3>
          <button class="modal-close" @click="closeReplyModal">
            <X :size="20" />
          </button>
        </div>
        <div class="modal-body">
          <div class="original-comment">
            <div class="original-author">
              <User :size="16" />
              <span>{{ replyingComment?.userName || replyingComment?.guestName || '匿名用户' }}</span>
            </div>
            <div class="original-content">{{ replyingComment?.content }}</div>
          </div>
          <div class="reply-form">
            <textarea
              v-model="replyContent"
              placeholder="输入回复内容..."
              class="reply-textarea"
              rows="4"
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="modal-btn modal-btn--cancel" @click="closeReplyModal">取消</button>
          <button 
            class="modal-btn modal-btn--submit" 
            :disabled="!replyContent.trim() || submittingReply"
            @click="submitReply"
          >
            {{ submittingReply ? '提交中...' : '提交回复' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
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
  ChevronRight,
  Loader2,
  RotateCcw
} from 'lucide-vue-next'
import {
  getAdminComments,
  getCommentStats,
  approveComment,
  rejectComment,
  markCommentAsSpam,
  restoreComment,
  deleteComment,
  batchApproveComments,
  batchMarkCommentsAsSpam,
  batchDeleteComments,
  createComment
} from '@/api/comment'
import { getArticleList } from '@/api/article'
import { message } from '@/utils/message'
import { ElMessageBox } from 'element-plus'

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
  statsUpdate: [stats: { total: number; approved: number; pending: number; spam: number }]
}>()

// 状态
const loading = ref(false)
const activeTab = ref('all')
const searchKeyword = ref('')
const articleFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 评论数据
const comments = ref<any[]>([])
const articles = ref<any[]>([])

// 统计数据
const commentStats = ref({
  total: 0,
  approved: 0,
  pending: 0,
  rejected: 0,
  spam: 0
})

// 选择状态
const selectedIds = ref<number[]>([])

// 回复弹窗状态
const showReplyModal = ref(false)
const replyingComment = ref<any>(null)
const replyContent = ref('')
const submittingReply = ref(false)

// 计算属性
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const isAllSelected = computed(() => {
  return comments.value.length > 0 && selectedIds.value.length === comments.value.length
})

const isIndeterminate = computed(() => {
  return selectedIds.value.length > 0 && selectedIds.value.length < comments.value.length
})

const displayPages = computed(() => {
  const pages: (number | string)[] = []
  const totalP = totalPages.value
  const current = currentPage.value

  if (totalP <= 7) {
    for (let i = 1; i <= totalP; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 4) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(totalP)
    } else if (current >= totalP - 3) {
      pages.push(1)
      pages.push('...')
      for (let i = totalP - 4; i <= totalP; i++) {
        pages.push(i)
      }
    } else {
      pages.push(1)
      pages.push('...')
      for (let i = current - 1; i <= current + 1; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(totalP)
    }
  }

  return pages
})

// 获取状态对应的筛选值
function getStatusFilter(): number | undefined {
  switch (activeTab.value) {
    case 'approved':
      return 1
    case 'pending':
      return 0
    case 'spam':
      return 3
    default:
      return undefined
  }
}

// 加载评论列表
async function loadComments() {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value,
      pageSize: pageSize.value,
      status: getStatusFilter(),
      keyword: searchKeyword.value || undefined
    }
    
    // 处理文章筛选
    if (articleFilter.value === 'guestbook') {
      params.articleId = 0 // 0 表示留言板
    } else if (articleFilter.value) {
      params.articleId = parseInt(articleFilter.value)
    }
    
    const result = await getAdminComments(params)
    comments.value = result.records || []
    total.value = result.total || 0
  } catch (error: any) {
    console.error('加载评论失败:', error)
    message.error(error.message || '加载评论失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
async function loadStats() {
  try {
    const stats = await getCommentStats()
    commentStats.value = stats
    emit('statsUpdate', stats)
  } catch (error: any) {
    console.error('加载统计失败:', error)
  }
}

// 加载文章列表
async function loadArticles() {
  try {
    const result = await getArticleList({ page: 1, pageSize: 100 })
    articles.value = result.records || []
  } catch (error: any) {
    console.error('加载文章列表失败:', error)
  }
}

// 切换标签
function switchTab(tab: string) {
  activeTab.value = tab
  currentPage.value = 1
  selectedIds.value = []
  loadComments()
}

// 搜索
function handleSearch() {
  currentPage.value = 1
  loadComments()
}

// 筛选变化
function handleFilterChange() {
  currentPage.value = 1
  loadComments()
}

// 分页
function changePage(page: number) {
  currentPage.value = page
  selectedIds.value = []
  loadComments()
}

// 选择操作
function toggleSelect(id: number) {
  const index = selectedIds.value.indexOf(id)
  if (index > -1) {
    selectedIds.value.splice(index, 1)
  } else {
    selectedIds.value.push(id)
  }
}

function toggleSelectAll() {
  if (isAllSelected.value) {
    selectedIds.value = []
  } else {
    selectedIds.value = comments.value.map(c => c.id)
  }
}

function clearSelection() {
  selectedIds.value = []
}

// 状态文本
function getStatusText(status: number): string {
  const statusMap: Record<number, string> = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝',
    3: '垃圾评论'
  }
  return statusMap[status] || '未知'
}

// 格式化日期
function formatDate(dateStr: string): string {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 查看文章
function viewArticle(articleId: number | null) {
  if (articleId) {
    window.open(`/article/${articleId}`, '_blank')
  } else {
    window.open('/guestbook', '_blank')
  }
}

// 审核通过
async function handleApprove(id: number) {
  try {
    await approveComment(id)
    message.success('评论已通过')
    loadComments()
    loadStats()
    emit('approve', id)
  } catch (error: any) {
    message.error(error.message || '操作失败')
  }
}

// 取消通过（拒绝）
async function handleReject(id: number) {
  try {
    await rejectComment(id)
    message.success('已取消通过')
    loadComments()
    loadStats()
    emit('unapprove', id)
  } catch (error: any) {
    message.error(error.message || '操作失败')
  }
}

// 标记为垃圾评论
async function handleMarkSpam(id: number) {
  try {
    await ElMessageBox.confirm(
      '标记为垃圾评论后，该评论将不会在前台显示。确定要标记吗？',
      '标记为垃圾评论',
      {
        confirmButtonText: '确定标记',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await markCommentAsSpam(id)
    message.success('已标记为垃圾评论')
    loadComments()
    loadStats()
    emit('markSpam', id)
  } catch (error: any) {
    if (error !== 'cancel') {
      message.error(error.message || '操作失败')
    }
  }
}

// 恢复为待审核状态
async function handleRestore(id: number) {
  try {
    await ElMessageBox.confirm(
      '确定要将此评论恢复为待审核状态吗？',
      '恢复评论',
      {
        confirmButtonText: '确定恢复',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    await restoreComment(id)
    message.success('已恢复为待审核状态')
    loadComments()
    loadStats()
  } catch (error: any) {
    if (error !== 'cancel') {
      message.error(error.message || '操作失败')
    }
  }
}

// 删除评论
async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm(
      '删除后将无法恢复，如果该评论有回复，回复也会被一并删除。确定要删除吗？',
      '永久删除评论',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error',
        confirmButtonClass: 'el-button--danger'
      }
    )

    await deleteComment(id)
    message.success('评论已永久删除')
    loadComments()
    loadStats()
    emit('delete', id)
  } catch (error: any) {
    if (error !== 'cancel') {
      message.error(error.message || '删除失败')
    }
  }
}

// 批量通过
async function handleBatchApprove() {
  if (selectedIds.value.length === 0) return
  
  try {
    await ElMessageBox.confirm(`确定要批量通过 ${selectedIds.value.length} 条评论吗？`, '批量操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await batchApproveComments(selectedIds.value)
    message.success('批量通过成功')
    selectedIds.value = []
    loadComments()
    loadStats()
  } catch (error: any) {
    if (error !== 'cancel') {
      message.error(error.message || '操作失败')
    }
  }
}

// 批量标记垃圾
async function handleBatchSpam() {
  if (selectedIds.value.length === 0) return

  try {
    await ElMessageBox.confirm(
      `确定要将选中的 ${selectedIds.value.length} 条评论标记为垃圾评论吗？标记后这些评论将不会在前台显示。`,
      '批量标记垃圾评论',
      {
        confirmButtonText: '确定标记',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await batchMarkCommentsAsSpam(selectedIds.value)
    message.success(`已将 ${selectedIds.value.length} 条评论标记为垃圾评论`)
    selectedIds.value = []
    loadComments()
    loadStats()
  } catch (error: any) {
    if (error !== 'cancel') {
      message.error(error.message || '操作失败')
    }
  }
}

// 批量删除
async function handleBatchDelete() {
  if (selectedIds.value.length === 0) return

  try {
    await ElMessageBox.confirm(
      `确定要永久删除选中的 ${selectedIds.value.length} 条评论吗？删除后将无法恢复，相关的回复也会被一并删除。`,
      '批量永久删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error',
        confirmButtonClass: 'el-button--danger'
      }
    )

    await batchDeleteComments(selectedIds.value)
    message.success(`已永久删除 ${selectedIds.value.length} 条评论`)
    selectedIds.value = []
    loadComments()
    loadStats()
  } catch (error: any) {
    if (error !== 'cancel') {
      message.error(error.message || '删除失败')
    }
  }
}

// 回复弹窗
function openReplyModal(comment: any) {
  replyingComment.value = comment
  replyContent.value = ''
  showReplyModal.value = true
}

function closeReplyModal() {
  showReplyModal.value = false
  replyingComment.value = null
  replyContent.value = ''
}

async function submitReply() {
  if (!replyContent.value.trim() || !replyingComment.value) return
  
  submittingReply.value = true
  try {
    await createComment({
      articleId: replyingComment.value.articleId,
      parentId: replyingComment.value.id,
      content: replyContent.value.trim()
    })
    message.success('回复成功')
    closeReplyModal()
    loadComments()
    loadStats()
  } catch (error: any) {
    message.error(error.message || '回复失败')
  } finally {
    submittingReply.value = false
  }
}

// 初始化
onMounted(() => {
  loadComments()
  loadStats()
  loadArticles()
})
</script>

<style scoped>
.comment-management {
  display: flex;
  flex-direction: column;
  flex: 1;
  width: 100%;
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

/* 批量操作栏 */
.batch-action-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-md) 2%;
  background: var(--color-miku-50);
  border-bottom: 1px solid var(--color-miku-200);
}

.selected-count {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-miku-700);
}

.batch-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.batch-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-xs) var(--spacing-md);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.batch-btn--approve {
  background: var(--color-green-500);
  color: var(--color-white);
}

.batch-btn--approve:hover {
  background: var(--color-green-600);
}

.batch-btn--spam {
  background: var(--color-orange-500);
  color: var(--color-white);
}

.batch-btn--spam:hover {
  background: var(--color-orange-600);
}

.batch-btn--delete {
  background: var(--color-red-500);
  color: var(--color-white);
}

.batch-btn--delete:hover {
  background: var(--color-red-600);
}

.batch-btn--cancel {
  background: var(--color-gray-200);
  color: var(--color-gray-700);
}

.batch-btn--cancel:hover {
  background: var(--color-gray-300);
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl);
  color: var(--color-gray-500);
}

.loading-icon {
  animation: spin 1s linear infinite;
  margin-bottom: var(--spacing-md);
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
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

.col-checkbox {
  width: 40px;
}

.col-checkbox input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.col-author {
  width: 14%;
}

.col-content {
  width: 28%;
}

.col-article {
  width: 14%;
}

.col-status {
  width: 10%;
}

.col-date {
  width: 12%;
}

.col-actions {
  width: 18%;
  min-width: 180px;
  overflow: visible;
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
  overflow: hidden;
}

.author-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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
  line-clamp: 2;
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
  line-clamp: 2;
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
  background: #fecaca;
  color: #991b1b;
}

.status-badge.status-3 {
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
  flex-wrap: nowrap;
}

.action-icon-btn {
  width: 32px;
  height: 32px;
  min-width: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: 6px;
  color: var(--color-gray-600);
  cursor: pointer;
  transition: all var(--transition-fast);
  flex-shrink: 0;
  position: relative;
}

.action-icon-btn svg {
  pointer-events: none;
}

.action-icon-btn:hover {
  background: var(--color-miku-100);
  color: var(--color-miku-600);
}

.action-icon-btn--approve:hover {
  background: var(--color-green-100);
  color: var(--color-green-600);
}

.action-icon-btn--reject:hover {
  background: var(--color-orange-100);
  color: var(--color-orange-600);
}

.action-icon-btn--spam:hover {
  background: var(--color-orange-100);
  color: var(--color-orange-600);
}

.action-icon-btn--restore:hover {
  background: var(--color-blue-100);
  color: var(--color-blue-600);
}

.action-icon-btn--delete:hover {
  background: var(--color-red-100);
  color: var(--color-red-600);
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

.pagination-btn:hover:not(:disabled):not(.active):not(.ellipsis) {
  border-color: var(--color-miku-500);
  color: var(--color-miku-600);
}

.pagination-btn.active {
  background: var(--color-miku-500);
  border-color: var(--color-miku-500);
  color: var(--color-white);
}

.pagination-btn.ellipsis {
  cursor: default;
  border: none;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 回复弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.reply-modal {
  width: 100%;
  max-width: 500px;
  background: var(--color-white);
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--color-gray-200);
}

.modal-header h3 {
  margin: 0;
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
}

.modal-close {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: 6px;
  color: var(--color-gray-500);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.modal-close:hover {
  background: var(--color-gray-100);
  color: var(--color-gray-700);
}

.modal-body {
  padding: var(--spacing-xl);
}

.original-comment {
  padding: var(--spacing-md);
  background: var(--color-gray-50);
  border-radius: 8px;
  margin-bottom: var(--spacing-lg);
}

.original-author {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-700);
  margin-bottom: var(--spacing-sm);
}

.original-content {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  line-height: 1.5;
}

.reply-textarea {
  width: 100%;
  padding: var(--spacing-md);
  font-size: var(--text-sm);
  color: var(--color-gray-900);
  background: var(--color-white);
  border: 1px solid var(--color-gray-200);
  border-radius: 8px;
  resize: vertical;
  transition: all var(--transition-fast);
}

.reply-textarea:focus {
  outline: none;
  border-color: var(--color-miku-500);
  box-shadow: 0 0 0 3px rgba(57, 197, 187, 0.1);
}

.reply-textarea::placeholder {
  color: var(--color-gray-400);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  padding: var(--spacing-lg) var(--spacing-xl);
  border-top: 1px solid var(--color-gray-200);
}

.modal-btn {
  padding: var(--spacing-sm) var(--spacing-xl);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.modal-btn--cancel {
  background: var(--color-gray-100);
  color: var(--color-gray-700);
}

.modal-btn--cancel:hover {
  background: var(--color-gray-200);
}

.modal-btn--submit {
  background: var(--color-miku-500);
  color: var(--color-white);
}

.modal-btn--submit:hover:not(:disabled) {
  background: var(--color-miku-600);
}

.modal-btn--submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
