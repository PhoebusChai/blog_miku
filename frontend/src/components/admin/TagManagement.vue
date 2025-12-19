<template>
  <div class="tag-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-bar-left">
        <div class="stats-info">
          <span class="stat-label">标签总数：</span>
          <span class="stat-value">{{ tags.length }}</span>
        </div>
      </div>
      <div class="action-bar-center">
        <div class="search-box">
          <Search :size="18" />
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索标签名称..."
            class="search-input"
          />
        </div>
      </div>
      <div class="action-bar-right">
        <button class="primary-btn" @click="handleCreate">
          <Plus :size="18" />
          <span>新建标签</span>
        </button>
      </div>
    </div>

    <!-- 标签列表 -->
    <div class="tag-list-wrapper">
      <table class="tag-table">
        <thead>
          <tr>
            <th class="col-name">标签名称</th>
            <th class="col-slug">别名</th>
            <th class="col-count">文章数</th>
            <th class="col-date">创建时间</th>
            <th class="col-actions">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="tag in paginatedTags" :key="tag.id" class="tag-row">
            <td class="col-name">
              <div class="tag-name">{{ tag.name }}</div>
            </td>
            <td class="col-slug">
              <code class="slug-code">{{ tag.slug }}</code>
            </td>
            <td class="col-count">{{ tag.articleCount }}</td>
            <td class="col-date">{{ tag.createdAt }}</td>
            <td class="col-actions">
              <div class="actions-cell">
                <button class="action-icon-btn" title="编辑" @click="handleEdit(tag.id)">
                  <Edit :size="16" />
                </button>
                <button class="action-icon-btn" title="删除" @click="handleDelete(tag.id)">
                  <Trash2 :size="16" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-if="filteredTags.length === 0" class="empty-state">
        <Tag :size="48" class="empty-icon" />
        <p>暂无标签</p>
        <button class="primary-btn" @click="handleCreate">
          <Plus :size="18" />
          <span>创建第一个标签</span>
        </button>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="filteredTags.length > 0" class="pagination">
      <div class="pagination-info">共 {{ filteredTags.length }} 条，每页 {{ pageSize }} 条</div>
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
import { ref, computed } from 'vue'
import { Search, Plus, Edit, Trash2, Tag, ChevronLeft, ChevronRight } from 'lucide-vue-next'

// Emits
const emit = defineEmits<{
  create: []
  edit: [id: number]
  delete: [id: number]
}>()

// 状态
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

// Mock 数据
const tags = ref([
  {
    id: 1,
    name: 'Vue.js',
    slug: 'vuejs',
    articleCount: 5,
    createdAt: '2024-12-01'
  },
  {
    id: 2,
    name: 'TypeScript',
    slug: 'typescript',
    articleCount: 3,
    createdAt: '2024-12-01'
  },
  {
    id: 3,
    name: 'JavaScript',
    slug: 'javascript',
    articleCount: 8,
    createdAt: '2024-12-01'
  },
  {
    id: 4,
    name: '前端开发',
    slug: 'frontend',
    articleCount: 12,
    createdAt: '2024-12-01'
  }
])

// 筛选标签
const filteredTags = computed(() => {
  if (!searchKeyword.value) return tags.value

  const keyword = searchKeyword.value.toLowerCase()
  return tags.value.filter(
    (tag) =>
      tag.name.toLowerCase().includes(keyword) || tag.slug.toLowerCase().includes(keyword)
  )
})

// 分页
const totalPages = computed(() => Math.ceil(filteredTags.value.length / pageSize.value))

const paginatedTags = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredTags.value.slice(start, end)
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
function handleCreate() {
  emit('create')
}

function handleEdit(id: number) {
  emit('edit', id)
}

function handleDelete(id: number) {
  if (confirm('确定要删除这个标签吗？')) {
    emit('delete', id)
  }
}
</script>

<style scoped>
.tag-management {
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

.stats-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-lg);
  background: var(--color-miku-50);
  border-radius: 8px;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
}

.stat-value {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--color-miku-600);
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

.tag-list-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow-y: auto;
}

.tag-list-wrapper::-webkit-scrollbar {
  width: 6px;
}

.tag-list-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.tag-list-wrapper::-webkit-scrollbar-thumb {
  background: var(--color-gray-300);
  border-radius: 3px;
}

.tag-list-wrapper::-webkit-scrollbar-thumb:hover {
  background: var(--color-gray-400);
}

.tag-table {
  width: 100%;
  border-collapse: collapse;
  background: var(--color-white);
}

.tag-table thead {
  position: sticky;
  top: 0;
  background: var(--color-gray-50);
  z-index: 10;
}

.tag-table th {
  padding: var(--spacing-md) var(--spacing-lg);
  text-align: left;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-700);
  border-bottom: 2px solid var(--color-gray-200);
  white-space: nowrap;
}

.tag-table th:first-child {
  padding-left: var(--spacing-lg);
}

.tag-table th:last-child {
  padding-right: var(--spacing-lg);
}

.tag-table td {
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--color-gray-100);
  vertical-align: middle;
}

.tag-table td:first-child {
  padding-left: var(--spacing-lg);
}

.tag-table td:last-child {
  padding-right: var(--spacing-lg);
}

.tag-row:hover {
  background: var(--color-gray-50);
}

.col-name {
  width: 30%;
}

.col-slug {
  width: 30%;
}

.col-count {
  width: 15%;
}

.col-date {
  width: 15%;
}

.col-actions {
  width: 10%;
}

.tag-name {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
}

.slug-code {
  padding: 4px 8px;
  font-size: var(--text-xs);
  font-family: 'Courier New', monospace;
  color: var(--color-gray-700);
  background: var(--color-gray-100);
  border-radius: 4px;
}

.col-count {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
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
  padding: var(--spacing-lg) 0;
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
