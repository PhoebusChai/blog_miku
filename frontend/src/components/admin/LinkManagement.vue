<template>
  <div class="link-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-bar-left">
        <div class="tab-group">
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'all' }"
            @click="activeTab = 'all'"
          >
            全部 ({{ links.length }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'approved' }"
            @click="activeTab = 'approved'"
          >
            已通过
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'pending' }"
            @click="activeTab = 'pending'"
          >
            待审核
          </button>
        </div>
      </div>
      <div class="action-bar-center">
        <div class="search-box">
          <Search :size="18" />
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索网站名称或链接..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">搜索</button>
        </div>
      </div>
      <div class="action-bar-right">
        <button class="primary-btn" @click="handleCreate">
          <Plus :size="18" />
          <span>添加友链</span>
        </button>
      </div>
    </div>

    <!-- 友链列表 -->
    <div class="link-list-wrapper">
      <table class="link-table">
        <thead>
          <tr>
            <th class="col-site">网站信息</th>
            <th class="col-url">链接</th>
            <th class="col-status">状态</th>
            <th class="col-date">添加时间</th>
            <th class="col-actions">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="link in paginatedLinks" :key="link.id" class="link-row">
            <td class="col-site">
              <div class="site-cell">
                <div class="site-avatar">
                  <img v-if="link.avatar" :src="link.avatar" :alt="link.name" />
                  <Globe v-else :size="24" />
                </div>
                <div class="site-info">
                  <div class="site-name">{{ link.name }}</div>
                  <div class="site-description">{{ link.description }}</div>
                </div>
              </div>
            </td>
            <td class="col-url">
              <a :href="link.url" target="_blank" class="link-url">
                <ExternalLink :size="14" />
                {{ link.url }}
              </a>
            </td>
            <td class="col-status">
              <span class="status-badge" :class="`status-${link.status}`">
                {{ getStatusText(link.status) }}
              </span>
            </td>
            <td class="col-date">{{ link.createdAt }}</td>
            <td class="col-actions">
              <div class="actions-cell">
                <button
                  v-if="link.status === 0"
                  class="action-icon-btn"
                  title="通过"
                  @click="handleApprove(link.id)"
                >
                  <Check :size="16" />
                </button>
                <button class="action-icon-btn" title="编辑" @click="handleEdit(link.id)">
                  <Edit :size="16" />
                </button>
                <button class="action-icon-btn" title="删除" @click="handleDelete(link.id)">
                  <Trash2 :size="16" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-if="filteredLinks.length === 0" class="empty-state">
        <Link :size="48" class="empty-icon" />
        <p>暂无友链</p>
        <button class="primary-btn" @click="handleCreate">
          <Plus :size="18" />
          <span>添加第一个友链</span>
        </button>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="filteredLinks.length > 0" class="pagination">
      <div class="pagination-info">共 {{ filteredLinks.length }} 条，每页 {{ pageSize }} 条</div>
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

    <!-- 友链表单弹窗 -->
    <LinkFormModal
      :visible="showModal"
      :is-edit="isEditMode"
      :initial-data="editingLink"
      @close="showModal = false"
      @submit="handleModalSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  Search,
  Plus,
  Edit,
  Trash2,
  Link,
  Globe,
  ExternalLink,
  Check,
  ChevronLeft,
  ChevronRight
} from 'lucide-vue-next'
import LinkFormModal from './LinkFormModal.vue'

// 状态
const activeTab = ref('all')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const showModal = ref(false)
const isEditMode = ref(false)
const editingLink = ref<any>(null)

// Mock 数据
const links = ref([
  {
    id: 1,
    name: '技术博客',
    url: 'https://example.com',
    description: '专注前端技术分享',
    avatar: 'https://via.placeholder.com/48',
    status: 1, // 0-待审核，1-已通过，2-已拒绝
    createdAt: '2024-12-15'
  },
  {
    id: 2,
    name: '开发者社区',
    url: 'https://dev.example.com',
    description: '开发者交流平台',
    avatar: '',
    status: 0,
    createdAt: '2024-12-18'
  }
])

// 筛选友链
const filteredLinks = computed(() => {
  let result = links.value

  if (activeTab.value === 'approved') {
    result = result.filter((l) => l.status === 1)
  } else if (activeTab.value === 'pending') {
    result = result.filter((l) => l.status === 0)
  }

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(
      (l) =>
        l.name.toLowerCase().includes(keyword) ||
        l.url.toLowerCase().includes(keyword) ||
        l.description.toLowerCase().includes(keyword)
    )
  }

  return result
})

// 分页
const totalPages = computed(() => Math.ceil(filteredLinks.value.length / pageSize.value))

const paginatedLinks = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredLinks.value.slice(start, end)
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
    2: '已拒绝'
  }
  return statusMap[status] || '未知'
}

function handleCreate() {
  isEditMode.value = false
  editingLink.value = null
  showModal.value = true
}

function handleEdit(id: number) {
  const link = links.value.find((l) => l.id === id)
  if (link) {
    isEditMode.value = true
    editingLink.value = {
      name: link.name,
      url: link.url,
      avatar: link.avatar,
      description: link.description,
      status: link.status
    }
    showModal.value = true
  }
}

function handleApprove(id: number) {
  const link = links.value.find((l) => l.id === id)
  if (link) {
    link.status = 1
    console.log('通过友链:', id)
  }
}

function handleDelete(id: number) {
  if (confirm('确定要删除这个友链吗？')) {
    const index = links.value.findIndex((l) => l.id === id)
    if (index !== -1) {
      links.value.splice(index, 1)
      console.log('删除友链:', id)
    }
  }
}

function handleSearch() {
  console.log('搜索关键词:', searchKeyword.value)
}

function handleModalSubmit(data: any) {
  if (isEditMode.value) {
    console.log('更新友链:', data)
    // TODO: 调用API更新友链
  } else {
    const newLink = {
      id: links.value.length + 1,
      ...data,
      createdAt: new Date().toISOString().split('T')[0]
    }
    links.value.push(newLink)
    console.log('创建友链:', newLink)
  }
  showModal.value = false
}
</script>

<style scoped>
.link-management {
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

.link-list-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow-y: auto;
}

.link-list-wrapper::-webkit-scrollbar {
  width: 6px;
}

.link-list-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.link-list-wrapper::-webkit-scrollbar-thumb {
  background: var(--color-gray-300);
  border-radius: 3px;
}

.link-list-wrapper::-webkit-scrollbar-thumb:hover {
  background: var(--color-gray-400);
}

.link-table {
  width: 100%;
  border-collapse: collapse;
  background: var(--color-white);
}

.link-table thead {
  position: sticky;
  top: 0;
  background: var(--color-gray-50);
  z-index: 10;
}

.link-table th {
  padding: var(--spacing-md) var(--spacing-lg);
  text-align: left;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-700);
  border-bottom: 2px solid var(--color-gray-200);
  white-space: nowrap;
}

.link-table th:first-child {
  padding-left: var(--spacing-lg);
}

.link-table th:last-child {
  padding-right: var(--spacing-lg);
}

.link-table td {
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--color-gray-100);
  vertical-align: middle;
}

.link-table td:first-child {
  padding-left: var(--spacing-lg);
}

.link-table td:last-child {
  padding-right: var(--spacing-lg);
}

.link-row:hover {
  background: var(--color-gray-50);
}

.col-site {
  width: 35%;
}

.col-url {
  width: 30%;
}

.col-status {
  width: 10%;
}

.col-date {
  width: 15%;
}

.col-actions {
  width: 10%;
}

.site-cell {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.site-avatar {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-gray-100);
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.site-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.site-avatar svg {
  color: var(--color-gray-400);
}

.site-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.site-name {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
}

.site-description {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.link-url {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: var(--text-sm);
  color: var(--color-miku-600);
  text-decoration: none;
  transition: color var(--transition-fast);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.link-url:hover {
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
  margin: 0 0 var(--spacing-xl) 0;
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
