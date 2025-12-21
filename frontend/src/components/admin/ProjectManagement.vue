<template>
  <div class="project-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-bar-left">
        <div class="tab-group">
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'all' }"
            @click="activeTab = 'all'"
          >
            全部 ({{ projects.length }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'active' }"
            @click="activeTab = 'active'"
          >
            进行中
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'completed' }"
            @click="activeTab = 'completed'"
          >
            已完成
          </button>
        </div>
      </div>
      <div class="action-bar-center">
        <div class="search-box">
          <Search :size="18" />
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索项目名称或描述..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">搜索</button>
        </div>
      </div>
      <div class="action-bar-right">
        <button class="primary-btn" @click="handleCreate">
          <Plus :size="18" />
          <span>新建项目</span>
        </button>
      </div>
    </div>

    <!-- 项目列表 -->
    <div class="project-list-wrapper">
      <table class="project-table">
        <thead>
          <tr>
            <th class="col-name">项目名称</th>
            <th class="col-tech">技术栈</th>
            <th class="col-status">状态</th>
            <th class="col-link">链接</th>
            <th class="col-date">创建时间</th>
            <th class="col-actions">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="project in paginatedProjects" :key="project.id" class="project-row">
            <td class="col-name">
              <div class="name-cell">
                <div class="project-name">{{ project.title }}</div>
                <div class="project-description">{{ project.description }}</div>
              </div>
            </td>
            <td class="col-tech">
              <div class="tech-tags">
                <span v-for="tech in project.tech" :key="tech" class="tech-tag">
                  {{ tech }}
                </span>
              </div>
            </td>
            <td class="col-status">
              <span class="status-badge" :class="`status-${project.status}`">
                {{ getStatusText(project.status) }}
              </span>
            </td>
            <td class="col-link">
              <a
                v-if="project.demoUrl"
                :href="project.demoUrl"
                target="_blank"
                class="project-link"
              >
                <ExternalLink :size="14" />
                访问
              </a>
              <span v-else class="no-link">-</span>
            </td>
            <td class="col-date">{{ project.createdAt }}</td>
            <td class="col-actions">
              <div class="actions-cell">
                <button class="action-icon-btn" title="编辑" @click="handleEdit(project.id)">
                  <Edit :size="16" />
                </button>
                <button class="action-icon-btn" title="删除" @click="handleDelete(project.id)">
                  <Trash2 :size="16" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-if="filteredProjects.length === 0" class="empty-state">
        <Briefcase :size="48" class="empty-icon" />
        <p>暂无项目</p>
        <button class="primary-btn" @click="handleCreate">
          <Plus :size="18" />
          <span>创建第一个项目</span>
        </button>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="filteredProjects.length > 0" class="pagination">
      <div class="pagination-info">共 {{ filteredProjects.length }} 条，每页 {{ pageSize }} 条</div>
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

    <!-- 项目表单弹窗 -->
    <ProjectFormModal
      :visible="showModal"
      :is-edit="isEditMode"
      :initial-data="editingProject"
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
  Briefcase,
  ExternalLink,
  ChevronLeft,
  ChevronRight
} from 'lucide-vue-next'
import ProjectFormModal from './ProjectFormModal.vue'

// 状态
const activeTab = ref('all')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const showModal = ref(false)
const isEditMode = ref(false)
const editingProject = ref<any>(null)

// Mock 数据
const projects = ref([
  {
    id: 1,
    title: '个人博客系统',
    description: '基于 Vue 3 和 TypeScript 的现代化博客系统',
    tech: ['Vue 3', 'TypeScript', 'Vite'],
    status: 0, // 0-进行中，1-已完成，2-已归档
    demoUrl: 'https://example.com',
    githubUrl: 'https://github.com/example/blog',
    createdAt: '2024-12-01'
  },
  {
    id: 2,
    title: 'Todo 应用',
    description: '简洁高效的任务管理工具',
    tech: ['React', 'Node.js'],
    status: 1,
    demoUrl: 'https://example.com/todo',
    githubUrl: 'https://github.com/example/todo',
    createdAt: '2024-11-15'
  }
])

// 筛选项目
const filteredProjects = computed(() => {
  let result = projects.value

  if (activeTab.value === 'active') {
    result = result.filter((p) => p.status === 0)
  } else if (activeTab.value === 'completed') {
    result = result.filter((p) => p.status === 1)
  }

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(
      (p) =>
        p.title.toLowerCase().includes(keyword) || p.description.toLowerCase().includes(keyword)
    )
  }

  return result
})

// 分页
const totalPages = computed(() => Math.ceil(filteredProjects.value.length / pageSize.value))

const paginatedProjects = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredProjects.value.slice(start, end)
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
    0: '进行中',
    1: '已完成',
    2: '已归档'
  }
  return statusMap[status] || '未知'
}

function handleCreate() {
  isEditMode.value = false
  editingProject.value = null
  showModal.value = true
}

function handleEdit(id: number) {
  const project = projects.value.find((p) => p.id === id)
  if (project) {
    isEditMode.value = true
    editingProject.value = {
      title: project.title,
      description: project.description,
      coverImage: '',
      demoUrl: project.demoUrl,
      githubUrl: project.githubUrl,
      techStack: [...project.tech],
      status: project.status
    }
    showModal.value = true
  }
}

function handleDelete(id: number) {
  if (confirm('确定要删除这个项目吗？')) {
    const index = projects.value.findIndex((p) => p.id === id)
    if (index !== -1) {
      projects.value.splice(index, 1)
      console.log('删除项目:', id)
    }
  }
}

function handleSearch() {
  console.log('搜索关键词:', searchKeyword.value)
}

function handleModalSubmit(data: any) {
  if (isEditMode.value) {
    console.log('更新项目:', data)
    // TODO: 调用API更新项目
  } else {
    const newProject = {
      id: projects.value.length + 1,
      title: data.title,
      description: data.description,
      tech: data.techStack,
      status: data.status,
      demoUrl: data.demoUrl,
      githubUrl: data.githubUrl,
      createdAt: new Date().toISOString().split('T')[0]
    }
    projects.value.push(newProject)
    console.log('创建项目:', newProject)
  }
  showModal.value = false
}
</script>

<style scoped>
.project-management {
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

.project-list-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow-y: auto;
}

.project-list-wrapper::-webkit-scrollbar {
  width: 6px;
}

.project-list-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.project-list-wrapper::-webkit-scrollbar-thumb {
  background: var(--color-gray-300);
  border-radius: 3px;
}

.project-list-wrapper::-webkit-scrollbar-thumb:hover {
  background: var(--color-gray-400);
}

.project-table {
  width: 100%;
  border-collapse: collapse;
  background: var(--color-white);
}

.project-table thead {
  position: sticky;
  top: 0;
  background: var(--color-gray-50);
  z-index: 10;
}

.project-table th {
  padding: var(--spacing-md) var(--spacing-lg);
  text-align: left;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-700);
  border-bottom: 2px solid var(--color-gray-200);
  white-space: nowrap;
}

.project-table th:first-child {
  padding-left: var(--spacing-lg);
}

.project-table th:last-child {
  padding-right: var(--spacing-lg);
}

.project-table td {
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--color-gray-100);
  vertical-align: middle;
}

.project-table td:first-child {
  padding-left: var(--spacing-lg);
}

.project-table td:last-child {
  padding-right: var(--spacing-lg);
}

.project-row:hover {
  background: var(--color-gray-50);
}

.col-name {
  width: 35%;
}

.col-tech {
  width: 25%;
}

.col-status {
  width: 10%;
}

.col-link {
  width: 10%;
}

.col-date {
  width: 12%;
}

.col-actions {
  width: 8%;
}

.name-cell {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.project-name {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
}

.project-description {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
}

.tech-tag {
  display: inline-block;
  padding: 2px 8px;
  font-size: var(--text-xs);
  color: var(--color-miku-700);
  background: var(--color-miku-50);
  border-radius: 4px;
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
  background: #dbeafe;
  color: #1e40af;
}

.status-badge.status-1 {
  background: #d1fae5;
  color: #065f46;
}

.status-badge.status-2 {
  background: var(--color-gray-100);
  color: var(--color-gray-700);
}

.project-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: var(--text-sm);
  color: var(--color-miku-600);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.project-link:hover {
  color: var(--color-miku-700);
  text-decoration: underline;
}

.no-link {
  font-size: var(--text-sm);
  color: var(--color-gray-400);
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
