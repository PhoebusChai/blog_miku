<template>
  <div class="user-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-bar-left">
        <div class="tab-group">
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'all' }"
            @click="activeTab = 'all'"
          >
            全部 ({{ users.length }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'admin' }"
            @click="activeTab = 'admin'"
          >
            管理员
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'user' }"
            @click="activeTab = 'user'"
          >
            普通用户
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
          />
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
          <tr v-for="user in filteredUsers" :key="user.id" class="user-row">
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
              <span class="role-badge" :class="`role-${user.role}`">
                {{ getRoleText(user.role) }}
              </span>
            </td>
            <td class="col-stats">
              <div class="stats-cell">
                <span class="stat-item">
                  <FileText :size="14" />
                  {{ user.articleCount }} 篇文章
                </span>
                <span class="stat-item">
                  <MessageSquare :size="14" />
                  {{ user.commentCount }} 条评论
                </span>
              </div>
            </td>
            <td class="col-status">
              <span class="status-badge" :class="`status-${user.status}`">
                {{ getStatusText(user.status) }}
              </span>
            </td>
            <td class="col-date">{{ user.createdAt }}</td>
            <td class="col-actions">
              <div class="actions-cell">
                <button class="action-icon-btn" title="编辑" @click="handleEdit(user.id)">
                  <Edit :size="16" />
                </button>
                <button
                  v-if="user.status === 1"
                  class="action-icon-btn"
                  title="禁用"
                  @click="handleDisable(user.id)"
                >
                  <Ban :size="16" />
                </button>
                <button
                  v-else
                  class="action-icon-btn"
                  title="启用"
                  @click="handleEnable(user.id)"
                >
                  <CheckCircle :size="16" />
                </button>
                <button class="action-icon-btn" title="删除" @click="handleDelete(user.id)">
                  <Trash2 :size="16" />
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
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
  MessageSquare
} from 'lucide-vue-next'

// Emits
const emit = defineEmits<{
  create: []
  edit: [id: number]
  disable: [id: number]
  enable: [id: number]
  delete: [id: number]
}>()

// 状态
const activeTab = ref('all')
const searchKeyword = ref('')

// Mock 数据
const users = ref([
  {
    id: 1,
    name: '管理员',
    email: 'admin@example.com',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin',
    role: 1, // 0-普通用户，1-管理员
    status: 1, // 0-禁用，1-正常
    articleCount: 12,
    commentCount: 45,
    createdAt: '2024-01-01'
  },
  {
    id: 2,
    name: '张三',
    email: 'zhangsan@example.com',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhangsan',
    role: 0,
    status: 1,
    articleCount: 0,
    commentCount: 8,
    createdAt: '2024-12-15'
  },
  {
    id: 3,
    name: '李四',
    email: 'lisi@example.com',
    avatar: '',
    role: 0,
    status: 0,
    articleCount: 0,
    commentCount: 2,
    createdAt: '2024-12-10'
  }
])

// 筛选用户
const filteredUsers = computed(() => {
  let result = users.value

  if (activeTab.value === 'admin') {
    result = result.filter((u) => u.role === 1)
  } else if (activeTab.value === 'user') {
    result = result.filter((u) => u.role === 0)
  }

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(
      (u) => u.name.toLowerCase().includes(keyword) || u.email.toLowerCase().includes(keyword)
    )
  }

  return result
})

// 方法
function getRoleText(role: number): string {
  const roleMap: Record<number, string> = {
    0: '普通用户',
    1: '管理员'
  }
  return roleMap[role] || '未知'
}

function getStatusText(status: number): string {
  const statusMap: Record<number, string> = {
    0: '已禁用',
    1: '正常'
  }
  return statusMap[status] || '未知'
}

function handleCreate() {
  emit('create')
}

function handleEdit(id: number) {
  emit('edit', id)
}

function handleDisable(id: number) {
  if (confirm('确定要禁用这个用户吗？')) {
    emit('disable', id)
  }
}

function handleEnable(id: number) {
  emit('enable', id)
}

function handleDelete(id: number) {
  if (confirm('确定要删除这个用户吗？此操作不可恢复。')) {
    emit('delete', id)
  }
}
</script>

<style scoped>
.user-management {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.action-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
  padding: var(--spacing-lg) 0;
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
</style>
