<template>
  <div class="project-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-bar-left">
        <div class="tab-group">
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'all' }"
            @click="switchTab('all')"
          >
            全部 ({{ stats.total }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'active' }"
            @click="switchTab('active')"
          >
            进行中 ({{ stats.active }})
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'completed' }"
            @click="switchTab('completed')"
          >
            已完成 ({{ stats.completed }})
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

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <Loader2 :size="32" class="loading-icon" />
      <p>加载中...</p>
    </div>

    <!-- 项目列表 -->
    <div v-else class="project-list-wrapper">
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
          <tr v-for="project in filteredProjects" :key="project.id" class="project-row">
            <td class="col-name">
              <div class="name-cell">
                <div class="project-name">{{ project.title }}</div>
                <div class="project-description">{{ project.description }}</div>
              </div>
            </td>
            <td class="col-tech">
              <div class="tech-tags">
                <span v-for="tech in parseTechStack(project.techStack)" :key="tech" class="tech-tag">
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
              <a v-if="project.demoUrl" :href="project.demoUrl" target="_blank" class="project-link">
                <ExternalLink :size="14" />
                访问
              </a>
              <span v-else class="no-link">-</span>
            </td>
            <td class="col-date">{{ formatDate(project.createdAt) }}</td>
            <td class="col-actions">
              <div class="actions-cell">
                <button type="button" class="action-icon-btn" title="编辑" @click.stop="handleEdit(project)">
                  <Edit :size="16" />
                </button>
                <button type="button" class="action-icon-btn action-icon-btn--delete" title="删除" @click.stop="handleDelete(project.id)">
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
import { ref, computed, onMounted } from 'vue'
import { Search, Plus, Edit, Trash2, Briefcase, ExternalLink, Loader2 } from 'lucide-vue-next'
import ProjectFormModal from './ProjectFormModal.vue'
import { getProjects, getProjectStats, createProject, updateProject, deleteProject } from '@/api/project'
import { message } from '@/utils/message'
import { ElMessageBox } from 'element-plus'

const loading = ref(false)
const activeTab = ref('all')
const searchKeyword = ref('')
const showModal = ref(false)
const isEditMode = ref(false)
const editingProject = ref<any>(null)
const editingId = ref<number | null>(null)

const projects = ref<any[]>([])
const stats = ref({ total: 0, active: 0, completed: 0, archived: 0 })

// 加载项目列表
async function loadProjects() {
  loading.value = true
  try {
    const status = activeTab.value === 'active' ? 0 : activeTab.value === 'completed' ? 1 : undefined
    projects.value = await getProjects(status)
  } catch (error: any) {
    message.error(error.message || '加载项目失败')
  } finally {
    loading.value = false
  }
}

// 加载统计
async function loadStats() {
  try {
    stats.value = await getProjectStats()
  } catch (error: any) {
    console.error('加载统计失败:', error)
  }
}

// 切换标签
function switchTab(tab: string) {
  activeTab.value = tab
  loadProjects()
}

// 筛选项目
const filteredProjects = computed(() => {
  if (!searchKeyword.value) return projects.value
  const keyword = searchKeyword.value.toLowerCase()
  return projects.value.filter(
    (p) => p.title?.toLowerCase().includes(keyword) || p.description?.toLowerCase().includes(keyword)
  )
})

// 解析技术栈
function parseTechStack(techStack: string | null): string[] {
  if (!techStack) return []
  try {
    return JSON.parse(techStack)
  } catch {
    return techStack.split(',').map((s) => s.trim())
  }
}

function getStatusText(status: number): string {
  const statusMap: Record<number, string> = { 0: '进行中', 1: '已完成', 2: '已归档' }
  return statusMap[status] || '未知'
}

function formatDate(dateStr: string): string {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

function handleCreate() {
  isEditMode.value = false
  editingProject.value = null
  editingId.value = null
  showModal.value = true
}

function handleEdit(project: any) {
  isEditMode.value = true
  editingId.value = project.id
  editingProject.value = {
    title: project.title,
    description: project.description,
    coverImage: project.coverImage,
    demoUrl: project.demoUrl,
    githubUrl: project.githubUrl,
    techStack: parseTechStack(project.techStack),
    status: project.status
  }
  showModal.value = true
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定要删除这个项目吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteProject(id)
    message.success('项目已删除')
    loadProjects()
    loadStats()
  } catch (error: any) {
    if (error !== 'cancel') {
      message.error(error.message || '删除失败')
    }
  }
}

function handleSearch() {
  // 搜索通过 computed 自动处理
}

async function handleModalSubmit(data: any) {
  try {
    const projectData = {
      title: data.title,
      description: data.description,
      coverImage: data.coverImage,
      demoUrl: data.demoUrl,
      githubUrl: data.githubUrl,
      techStack: JSON.stringify(data.techStack || []),
      status: data.status
    }

    if (isEditMode.value && editingId.value) {
      await updateProject(editingId.value, projectData)
      message.success('项目已更新')
    } else {
      await createProject(projectData)
      message.success('项目已创建')
    }
    showModal.value = false
    loadProjects()
    loadStats()
  } catch (error: any) {
    message.error(error.message || '操作失败')
  }
}

onMounted(() => {
  loadProjects()
  loadStats()
})
</script>

<style scoped>
.project-management {
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
}

.action-bar-left { flex-shrink: 0; }
.action-bar-center { flex: 1; display: flex; gap: var(--spacing-md); }
.action-bar-right { flex-shrink: 0; }

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

.tab-btn:hover { color: var(--color-gray-900); }
.tab-btn.active { background: var(--color-miku-500); color: var(--color-white); }

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--color-gray-50);
  border: 1px solid var(--color-gray-200);
  border-radius: 6px;
}

.search-box:focus-within {
  border-color: var(--color-miku-500);
  box-shadow: 0 0 0 3px rgba(57, 197, 187, 0.1);
}

.search-box svg { color: var(--color-gray-400); flex-shrink: 0; }

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: var(--text-sm);
  color: var(--color-gray-900);
  background: transparent;
}

.search-input::placeholder { color: var(--color-gray-400); }

.search-btn {
  padding: var(--spacing-xs) var(--spacing-md);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-white);
  background: var(--color-miku-500);
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.search-btn:hover { background: var(--color-miku-600); }

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
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.3);
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl);
  color: var(--color-gray-500);
}

.loading-icon { animation: spin 1s linear infinite; margin-bottom: var(--spacing-md); }

@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

.project-list-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow-y: auto;
}

.project-table { width: 100%; border-collapse: collapse; background: var(--color-white); }

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
}

.project-table td {
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--color-gray-100);
  vertical-align: middle;
}

.project-row:hover { background: var(--color-gray-50); }

.col-name { width: 30%; }
.col-tech { width: 25%; }
.col-status { width: 10%; }
.col-link { width: 10%; }
.col-date { width: 12%; }
.col-actions { width: 13%; }

.name-cell { display: flex; flex-direction: column; gap: var(--spacing-xs); }
.project-name { font-size: var(--text-base); font-weight: var(--font-semibold); color: var(--color-gray-900); }
.project-description { font-size: var(--text-sm); color: var(--color-gray-600); }

.tech-tags { display: flex; flex-wrap: wrap; gap: var(--spacing-xs); }
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
}

.status-badge.status-0 { background: #dbeafe; color: #1e40af; }
.status-badge.status-1 { background: #d1fae5; color: #065f46; }
.status-badge.status-2 { background: var(--color-gray-100); color: var(--color-gray-700); }

.project-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: var(--text-sm);
  color: var(--color-miku-600);
  text-decoration: none;
}

.project-link:hover { color: var(--color-miku-700); text-decoration: underline; }
.no-link { font-size: var(--text-sm); color: var(--color-gray-400); }

.actions-cell { display: flex; gap: var(--spacing-xs); }

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
}

.action-icon-btn:hover { background: var(--color-miku-100); color: var(--color-miku-600); }
.action-icon-btn--delete:hover { background: var(--color-red-100); color: var(--color-red-600); }
.action-icon-btn svg { pointer-events: none; }

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl);
  text-align: center;
  color: var(--color-gray-500);
}

.empty-icon { margin-bottom: var(--spacing-lg); opacity: 0.5; }
.empty-state p { margin: 0 0 var(--spacing-xl) 0; font-size: var(--text-base); }
</style>
