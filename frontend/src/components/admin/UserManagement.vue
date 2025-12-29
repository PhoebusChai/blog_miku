<template>
  <div class="user-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-bar-left">
        <div class="tab-group">
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'all' }"
            @click="handleTabChange('all')"
          >
            全部 ({{ stats.total }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'admin' }"
            @click="handleTabChange('admin')"
          >
            管理员 ({{ stats.admins }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'user' }"
            @click="handleTabChange('user')"
          >
            普通用户 ({{ stats.users }})
          </button>
        </div>
      </div>
      <div class="action-bar-center">
        <div class="search-box">
          <Search :size="18" />
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索用户名或邮箱..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">搜索</button>
        </div>
      </div>
      <div class="action-bar-right">
        <button class="primary-btn" @click="handleCreate">
          <UserPlus :size="18" />
          <span>添加用户</span>
        </button>
      </div>
    </div>

    <!-- 用户列表 -->
    <div class="user-list-wrapper">
      <table class="user-table">
        <thead>
          <tr>
            <th class="col-user">用户信息</th>
            <th class="col-role">角色</th>
            <th class="col-stats">统计</th>
            <th class="col-status">状态</th>
            <th class="col-date">注册时间</th>
            <th class="col-actions">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in paginatedUsers" :key="user.id" class="user-row">
            <td class="col-user">
              <div class="user-cell">
                <div class="user-avatar">
                  <img v-if="user.avatar" :src="user.avatar" :alt="user.name" />
                  <User v-else :size="24" />
                </div>
                <div class="user-info">
                  <div class="user-name">{{ user.name }}</div>
                  <div class="user-email">{{ user.email }}</div>
                </div>
              </div>
            </td>
            <td class="col-role">
              <span class="role-badge" :class="`role-${user.role ?? 0}`">
                {{ getRoleText(user.role) }}
              </span>
            </td>
            <td class="col-stats">
              <div class="stats-cell">
                <span class="stat-item">
                  <FileText :size="14" />
                  {{ (user as any).articleCount ?? 0 }} 篇文章
                </span>
                <span class="stat-item">
                  <MessageSquare :size="14" />
                  {{ (user as any).commentCount ?? 0 }} 条评论
                </span>
              </div>
            </td>
            <td class="col-status">
              <span class="status-badge" :class="`status-${user.status ?? 0}`">
                {{ getStatusText(user.status) }}
              </span>
            </td>
            <td class="col-date">{{ user.createdAt }}</td>
            <td class="col-actions">
              <div class="actions-cell">
                <button class="action-icon-btn" type="button" title="编辑" @click.stop="handleEdit(user.id!)">
                  <Edit :size="16" style="pointer-events: none" />
                </button>
                <button
                  v-if="user.status === 1"
                  class="action-icon-btn"
                  type="button"
                  title="禁用"
                  @click.stop="handleDisable(user.id!)"
                >
                  <Ban :size="16" style="pointer-events: none" />
                </button>
                <button
                  v-else
                  class="action-icon-btn"
                  type="button"
                  title="启用"
                  @click.stop="handleEnable(user.id!)"
                >
                  <CheckCircle :size="16" style="pointer-events: none" />
                </button>
                <button class="action-icon-btn" type="button" title="删除" @click.stop="handleDelete(user.id!)">
                  <Trash2 :size="16" style="pointer-events: none" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-if="filteredUsers.length === 0" class="empty-state">
        <Users :size="48" class="empty-icon" />
        <p>暂无用户</p>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="filteredUsers.length > 0" class="pagination">
      <div class="pagination-info">共 {{ filteredUsers.length }} 条，每页 {{ pageSize }} 条</div>
      <div class="pagination-controls">
        <button class="pagination-btn" :disabled="currentPage === 1" @click="currentPage--">
          <ChevronLeft :size="16" />
        </button>
        <button
          v-for="page in displayPages"
          :key="page"
          class="pagination-btn"
          :class="{ active: page === currentPage }"
          :disabled="typeof page === 'string'"
          @click="typeof page === 'number' && (currentPage = page)"
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
import { ref, computed, onMounted } from 'vue'
import {
  Search,
  UserPlus,
  User,
  Users,
  Edit,
  Trash2,
  Ban,
  CheckCircle,
  FileText,
  MessageSquare,
  ChevronLeft,
  ChevronRight
} from 'lucide-vue-next'
import {
  getAdminUsers,
  getUserStats,
  enableUser,
  disableUser,
  deleteUser as deleteUserApi,
  type User as UserType
} from '@/api/user'
import { message } from '@/utils/message'

// Emits
const emit = defineEmits<{
  create: []
  edit: [id: number]
}>()

// 状态
const activeTab = ref('all')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const stats = ref({
  total: 0,
  admins: 0,
  users: 0,
  active: 0,
  disabled: 0
})

// 用户数据
const users = ref<UserType[]>([])

// 加载用户列表
async function loadUsers() {
  loading.value = true
  try {
    const params: { role?: number; status?: number } = {}
    if (activeTab.value === 'admin') {
      params.role = 1
    } else if (activeTab.value === 'user') {
      params.role = 0
    }
    const res = await getAdminUsers(params)
    users.value = res || []
  } catch (error) {
    console.error('加载用户失败:', error)
    message.error('加载用户失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
async function loadStats() {
  try {
    const res = await getUserStats()
    stats.value = res || { total: 0, admins: 0, users: 0, active: 0, disabled: 0 }
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

// 初始化
onMounted(() => {
  loadUsers()
  loadStats()
})

// 筛选用户
const filteredUsers = computed(() => {
  let result = users.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(
      (u) => u.name.toLowerCase().includes(keyword) || u.email.toLowerCase().includes(keyword)
    )
  }

  return result
})

// 分页
const totalPages = computed(() => Math.ceil(filteredUsers.value.length / pageSize.value))

const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredUsers.value.slice(start, end)
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
function getRoleText(role: number | undefined): string {
  const roleMap: Record<number, string> = {
    0: '普通用户',
    1: '管理员'
  }
  return roleMap[role ?? 0] || '未知'
}

function getStatusText(status: number | undefined): string {
  const statusMap: Record<number, string> = {
    0: '已禁用',
    1: '正常'
  }
  return statusMap[status ?? 0] || '未知'
}

function handleTabChange(tab: string) {
  activeTab.value = tab
  currentPage.value = 1
  loadUsers()
}

function handleCreate() {
  emit('create')
}

function handleEdit(id: number) {
  emit('edit', id)
}

async function handleDisable(id: number) {
  if (confirm('确定要禁用这个用户吗？')) {
    try {
      await disableUser(id)
      message.success('用户已禁用')
      loadUsers()
      loadStats()
    } catch (error) {
      console.error('禁用失败:', error)
      message.error('禁用失败')
    }
  }
}

async function handleEnable(id: number) {
  try {
    await enableUser(id)
    message.success('用户已启用')
    loadUsers()
    loadStats()
  } catch (error) {
    console.error('启用失败:', error)
    message.error('启用失败')
  }
}

async function handleDelete(id: number) {
  if (confirm('确定要删除这个用户吗？此操作不可恢复。')) {
    try {
      await deleteUserApi(id)
      message.success('删除成功')
      loadUsers()
      loadStats()
    } catch (error) {
      console.error('删除失败:', error)
      message.error('删除失败')
    }
  }
}

function handleSearch() {
  currentPage.value = 1
}
</script>

<style scoped>
.user-management {
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
}

.action-bar-left,
.action-bar-right {
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

.user-list-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow-y: auto;
}

.user-list-wrapper::-webkit-scrollbar {
  width: 6px;
}

.user-list-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.user-list-wrapper::-webkit-scrollbar-thumb {
  background: var(--color-gray-300);
  border-radius: 3px;
}

.user-list-wrapper::-webkit-scrollbar-thumb:hover {
  background: var(--color-gray-400);
}

.user-table {
  width: 100%;
  border-collapse: collapse;
  background: var(--color-white);
}

.user-table thead {
  position: sticky;
  top: 0;
  background: var(--color-gray-50);
  z-index: 10;
}

.user-table th {
  padding: var(--spacing-md) var(--spacing-lg);
  text-align: left;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-700);
  border-bottom: 2px solid var(--color-gray-200);
  white-space: nowrap;
}

.user-table th:first-child {
  padding-left: var(--spacing-lg);
}

.user-table th:last-child {
  padding-right: var(--spacing-lg);
}

.user-table td {
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--color-gray-100);
  vertical-align: middle;
}

.user-table td:first-child {
  padding-left: var(--spacing-lg);
}

.user-table td:last-child {
  padding-right: var(--spacing-lg);
}

.user-row:hover {
  background: var(--color-gray-50);
}

.col-user {
  width: 30%;
}

.col-role {
  width: 12%;
}

.col-stats {
  width: 20%;
}

.col-status {
  width: 10%;
}

.col-date {
  width: 15%;
}

.col-actions {
  width: 13%;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.user-avatar {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-miku-100);
  color: var(--color-miku-600);
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.user-name {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
}

.user-email {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.role-badge {
  display: inline-block;
  padding: 4px 12px;
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  border-radius: 12px;
  white-space: nowrap;
}

.role-badge.role-0 {
  background: #dbeafe;
  color: #1e40af;
}

.role-badge.role-1 {
  background: #fef3c7;
  color: #92400e;
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

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  border-radius: 12px;
  white-space: nowrap;
}

.status-badge.status-0 {
  background: #fee2e2;
  color: #991b1b;
}

.status-badge.status-1 {
  background: #d1fae5;
  color: #065f46;
}

.col-date {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
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
