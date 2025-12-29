<template>
  <div class="link-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-bar-left">
        <div class="tab-group">
          <button class="tab-btn" :class="{ active: activeTab === 'all' }" @click="switchTab('all')">
            全部 ({{ stats.total }})
          </button>
          <button class="tab-btn" :class="{ active: activeTab === 'approved' }" @click="switchTab('approved')">
            已通过 ({{ stats.approved }})
          </button>
          <button class="tab-btn" :class="{ active: activeTab === 'pending' }" @click="switchTab('pending')">
            待审核 ({{ stats.pending }})
          </button>
        </div>
      </div>
      <div class="action-bar-center">
        <div class="search-box">
          <Search :size="18" />
          <input v-model="searchKeyword" type="text" placeholder="搜索网站名称或链接..." class="search-input" @keyup.enter="handleSearch" />
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

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <Loader2 :size="32" class="loading-icon" />
      <p>加载中...</p>
    </div>

    <!-- 友链列表 -->
    <div v-else class="link-list-wrapper">
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
          <tr v-for="link in filteredLinks" :key="link.id" class="link-row">
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
            <td class="col-date">{{ formatDate(link.createdAt) }}</td>
            <td class="col-actions">
              <div class="actions-cell">
                <button v-if="link.status === 0" type="button" class="action-icon-btn action-icon-btn--approve" title="通过" @click.stop="handleApprove(link.id)">
                  <Check :size="16" />
                </button>
                <button v-if="link.status === 0" type="button" class="action-icon-btn action-icon-btn--reject" title="拒绝" @click.stop="handleReject(link.id)">
                  <X :size="16" />
                </button>
                <button type="button" class="action-icon-btn" title="编辑" @click.stop="handleEdit(link)">
                  <Edit :size="16" />
                </button>
                <button type="button" class="action-icon-btn action-icon-btn--delete" title="删除" @click.stop="handleDelete(link.id)">
                  <Trash2 :size="16" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="filteredLinks.length === 0" class="empty-state">
        <Link :size="48" class="empty-icon" />
        <p>暂无友链</p>
        <button class="primary-btn" @click="handleCreate">
          <Plus :size="18" />
          <span>添加第一个友链</span>
        </button>
      </div>
    </div>

    <LinkFormModal :visible="showModal" :is-edit="isEditMode" :initial-data="editingLink" @close="showModal = false" @submit="handleModalSubmit" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Search, Plus, Edit, Trash2, Link, Globe, ExternalLink, Check, X, Loader2 } from 'lucide-vue-next'
import LinkFormModal from './LinkFormModal.vue'
import { getAdminLinks, getLinkStats, createLink, updateLink, approveLink, rejectLink, deleteLink } from '@/api/link'
import { message } from '@/utils/message'
import { ElMessageBox } from 'element-plus'

const loading = ref(false)
const activeTab = ref('all')
const searchKeyword = ref('')
const showModal = ref(false)
const isEditMode = ref(false)
const editingLink = ref<any>(null)
const editingId = ref<number | null>(null)

const links = ref<any[]>([])
const stats = ref({ total: 0, pending: 0, approved: 0, rejected: 0 })

async function loadLinks() {
  loading.value = true
  try {
    const status = activeTab.value === 'approved' ? 1 : activeTab.value === 'pending' ? 0 : undefined
    links.value = await getAdminLinks(status)
  } catch (error: any) {
    message.error(error.message || '加载友链失败')
  } finally {
    loading.value = false
  }
}

async function loadStats() {
  try {
    stats.value = await getLinkStats()
  } catch (error: any) {
    console.error('加载统计失败:', error)
  }
}

function switchTab(tab: string) {
  activeTab.value = tab
  loadLinks()
}

const filteredLinks = computed(() => {
  if (!searchKeyword.value) return links.value
  const keyword = searchKeyword.value.toLowerCase()
  return links.value.filter((l) => l.name?.toLowerCase().includes(keyword) || l.url?.toLowerCase().includes(keyword))
})

function getStatusText(status: number): string {
  const statusMap: Record<number, string> = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
  return statusMap[status] || '未知'
}

function formatDate(dateStr: string): string {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

function handleCreate() {
  isEditMode.value = false
  editingLink.value = null
  editingId.value = null
  showModal.value = true
}

function handleEdit(link: any) {
  isEditMode.value = true
  editingId.value = link.id
  editingLink.value = { name: link.name, url: link.url, avatar: link.avatar, description: link.description, status: link.status }
  showModal.value = true
}

async function handleApprove(id: number) {
  try {
    await approveLink(id)
    message.success('友链已通过')
    loadLinks()
    loadStats()
  } catch (error: any) {
    message.error(error.message || '操作失败')
  }
}

async function handleReject(id: number) {
  try {
    await ElMessageBox.confirm('确定要拒绝这个友链申请吗？', '拒绝确认', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await rejectLink(id)
    message.success('友链已拒绝')
    loadLinks()
    loadStats()
  } catch (error: any) {
    if (error !== 'cancel') message.error(error.message || '操作失败')
  }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定要删除这个友链吗？', '删除确认', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await deleteLink(id)
    message.success('友链已删除')
    loadLinks()
    loadStats()
  } catch (error: any) {
    if (error !== 'cancel') message.error(error.message || '删除失败')
  }
}

function handleSearch() {}

async function handleModalSubmit(data: any) {
  try {
    if (isEditMode.value && editingId.value) {
      await updateLink(editingId.value, data)
      message.success('友链已更新')
    } else {
      await createLink(data)
      message.success('友链已创建')
    }
    showModal.value = false
    loadLinks()
    loadStats()
  } catch (error: any) {
    message.error(error.message || '操作失败')
  }
}

onMounted(() => {
  loadLinks()
  loadStats()
})
</script>

<style scoped>
.link-management { display: flex; flex-direction: column; flex: 1; width: 100%; min-height: 0; }

.action-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
  padding: var(--spacing-lg) 2%;
  background: var(--color-white);
  border-bottom: 1px solid var(--color-gray-200);
}

.action-bar-left, .action-bar-right { flex-shrink: 0; }
.action-bar-center { flex: 1; display: flex; gap: var(--spacing-md); }

.tab-group { display: inline-flex; background: var(--color-white); border-radius: 8px; padding: 4px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04); }

.tab-btn {
  padding: var(--spacing-sm) var(--spacing-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-600);
  background: transparent;
  border: none;
  border-radius: 6px;
  cursor: pointer;
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

.search-box:focus-within { border-color: var(--color-miku-500); box-shadow: 0 0 0 3px rgba(57, 197, 187, 0.1); }
.search-box svg { color: var(--color-gray-400); flex-shrink: 0; }
.search-input { flex: 1; border: none; outline: none; font-size: var(--text-sm); color: var(--color-gray-900); background: transparent; }
.search-input::placeholder { color: var(--color-gray-400); }
.search-btn { padding: var(--spacing-xs) var(--spacing-md); font-size: var(--text-sm); font-weight: var(--font-medium); color: var(--color-white); background: var(--color-miku-500); border: none; border-radius: 4px; cursor: pointer; }
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

.primary-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(57, 197, 187, 0.3); }

.loading-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: var(--spacing-3xl); color: var(--color-gray-500); }
.loading-icon { animation: spin 1s linear infinite; margin-bottom: var(--spacing-md); }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

.link-list-wrapper { flex: 1; display: flex; flex-direction: column; min-height: 0; overflow-y: auto; }
.link-table { width: 100%; border-collapse: collapse; background: var(--color-white); }
.link-table thead { position: sticky; top: 0; background: var(--color-gray-50); z-index: 10; }
.link-table th { padding: var(--spacing-md) var(--spacing-lg); text-align: left; font-size: var(--text-sm); font-weight: var(--font-semibold); color: var(--color-gray-700); border-bottom: 2px solid var(--color-gray-200); }
.link-table td { padding: var(--spacing-lg); border-bottom: 1px solid var(--color-gray-100); vertical-align: middle; }
.link-row:hover { background: var(--color-gray-50); }

.col-site { width: 30%; }
.col-url { width: 30%; }
.col-status { width: 10%; }
.col-date { width: 15%; }
.col-actions { width: 15%; }

.site-cell { display: flex; align-items: center; gap: var(--spacing-md); }
.site-avatar { width: 48px; height: 48px; display: flex; align-items: center; justify-content: center; background: var(--color-gray-100); border-radius: 8px; overflow: hidden; flex-shrink: 0; }
.site-avatar img { width: 100%; height: 100%; object-fit: cover; }
.site-avatar svg { color: var(--color-gray-400); }
.site-info { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.site-name { font-size: var(--text-base); font-weight: var(--font-semibold); color: var(--color-gray-900); }
.site-description { font-size: var(--text-sm); color: var(--color-gray-600); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.link-url { display: inline-flex; align-items: center; gap: 4px; font-size: var(--text-sm); color: var(--color-miku-600); text-decoration: none; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.link-url:hover { color: var(--color-miku-700); text-decoration: underline; }

.status-badge { display: inline-block; padding: 4px 12px; font-size: var(--text-xs); font-weight: var(--font-medium); border-radius: 12px; }
.status-badge.status-0 { background: #fef3c7; color: #92400e; }
.status-badge.status-1 { background: #d1fae5; color: #065f46; }
.status-badge.status-2 { background: #fee2e2; color: #991b1b; }

.actions-cell { display: flex; gap: var(--spacing-xs); }
.action-icon-btn { width: 32px; height: 32px; display: flex; align-items: center; justify-content: center; background: transparent; border: none; border-radius: 6px; color: var(--color-gray-600); cursor: pointer; }
.action-icon-btn:hover { background: var(--color-miku-100); color: var(--color-miku-600); }
.action-icon-btn--approve:hover { background: var(--color-green-100); color: var(--color-green-600); }
.action-icon-btn--reject:hover { background: var(--color-orange-100); color: var(--color-orange-600); }
.action-icon-btn--delete:hover { background: var(--color-red-100); color: var(--color-red-600); }
.action-icon-btn svg { pointer-events: none; }

.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: var(--spacing-3xl); text-align: center; color: var(--color-gray-500); }
.empty-icon { margin-bottom: var(--spacing-lg); opacity: 0.5; }
.empty-state p { margin: 0 0 var(--spacing-xl) 0; font-size: var(--text-base); }
</style>
