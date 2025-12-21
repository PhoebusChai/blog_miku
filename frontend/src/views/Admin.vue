<template>
  <div class="admin-wrapper">
    <AppHeader />
    <div class="admin-page">
      <!-- 侧边栏 -->
      <aside class="admin-sidebar" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
        <div class="sidebar-header">
          <div class="sidebar-logo">
            <Shield :size="24" />
            <span v-if="!sidebarCollapsed" class="logo-text">管理后台</span>
          </div>
          <button class="sidebar-toggle" @click="toggleSidebar">
            <Menu :size="20" />
          </button>
        </div>

        <nav class="sidebar-nav">
          <div
            v-for="menu in menus"
            :key="menu.id"
            class="nav-item"
            :class="{ active: activeMenu === menu.id }"
            @click="selectMenu(menu.id)"
          >
            <component :is="menu.icon" :size="20" class="nav-icon" />
            <span v-if="!sidebarCollapsed" class="nav-text">{{ menu.label }}</span>
            <span v-if="!sidebarCollapsed && menu.badge" class="nav-badge">{{ menu.badge }}</span>
          </div>
        </nav>
      </aside>

      <!-- 主内容区 -->
      <main class="admin-main">
        <!-- 顶部栏 -->
        <div class="admin-topbar">
          <div class="topbar-left">
            <h1 class="page-title">{{ currentMenuLabel }}</h1>
            <p class="page-subtitle">{{ currentMenuSubtitle }}</p>
          </div>
          <div class="topbar-right">
            <button class="topbar-btn" @click="handleAction('refresh')">
              <RefreshCw :size="18" />
            </button>
            <button class="topbar-btn" @click="handleAction('notification')">
              <Bell :size="18" />
              <span v-if="stats.pendingComments > 0" class="notification-dot"></span>
            </button>
          </div>
        </div>

        <!-- 内容区域 -->
        <div class="admin-content">
          <!-- 文章编辑器 -->
          <div v-if="showArticleEditor" class="content-view content-view--full">
            <ArticleEditor
              :article-id="editingArticleId"
              :is-edit="isEditingArticle"
              @back="closeArticleEditor"
              @save="handleSaveArticle"
              @publish="handlePublishArticle"
            />
          </div>

          <!-- 概览页面 -->
          <div v-else-if="activeMenu === 'dashboard'" class="content-wrapper">
            <Dashboard :stats="stats" @action="handleAction" />
          </div>

          <!-- 文章管理页面 -->
          <div v-else-if="activeMenu === 'articles'" class="content-view content-view--full">
            <ArticleManagement
              :key="articleListKey"
              :stats="{
                total: stats.articles,
                published: stats.publishedArticles,
                draft: stats.draftArticles
              }"
              @create="handleCreateArticle"
              @edit="handleEditArticle"
              @view="handleViewArticle"
              @delete="handleDeleteArticle"
              @restore="handleRestoreArticle"
              @stats-update="handleArticleStatsUpdate"
            />
          </div>

          <!-- 评论管理页面 -->
          <div v-else-if="activeMenu === 'comments'" class="content-view content-view--full">
            <CommentManagement
              :stats="{
                total: stats.comments,
                approved: stats.comments - stats.pendingComments,
                pending: stats.pendingComments
              }"
              @approve="handleApproveComment"
              @unapprove="handleUnapproveComment"
              @reply="handleReplyComment"
              @markSpam="handleMarkSpam"
              @delete="handleDeleteComment"
            />
          </div>

          <!-- 标签管理页面 -->
          <div v-else-if="activeMenu === 'tags'" class="content-view content-view--full">
            <TagManagement
              @create="handleAction('createTag')"
              @edit="(id) => handleAction(`editTag-${id}`)"
              @delete="(id) => handleAction(`deleteTag-${id}`)"
            />
          </div>

          <!-- 项目管理页面 -->
          <div v-else-if="activeMenu === 'projects'" class="content-view content-view--full">
            <ProjectManagement
              @create="handleAction('createProject')"
              @edit="(id) => handleAction(`editProject-${id}`)"
              @delete="(id) => handleAction(`deleteProject-${id}`)"
            />
          </div>

          <!-- 图库管理页面 -->
          <div v-else-if="activeMenu === 'gallery'" class="content-view content-view--full">
            <GalleryManagement
              @upload="handleAction('uploadImage')"
              @view="(id) => handleAction(`viewImage-${id}`)"
              @copy="(url) => handleAction(`copyImage-${url}`)"
              @delete="(id) => handleAction(`deleteImage-${id}`)"
            />
          </div>

          <!-- 友链管理页面 -->
          <div v-else-if="activeMenu === 'links'" class="content-view content-view--full">
            <LinkManagement
              @create="handleAction('createLink')"
              @edit="(id) => handleAction(`editLink-${id}`)"
              @approve="(id) => handleAction(`approveLink-${id}`)"
              @delete="(id) => handleAction(`deleteLink-${id}`)"
            />
          </div>

          <!-- 用户管理页面 -->
          <div v-else-if="activeMenu === 'users'" class="content-view content-view--full">
            <UserManagement
              @create="handleAction('createUser')"
              @edit="(id) => handleAction(`editUser-${id}`)"
              @disable="(id) => handleAction(`disableUser-${id}`)"
              @enable="(id) => handleAction(`enableUser-${id}`)"
              @delete="(id) => handleAction(`deleteUser-${id}`)"
            />
          </div>

          <!-- 网站配置页面 -->
          <div v-else-if="activeMenu === 'settings'" class="content-view content-view--full">
            <SiteSettings @save="handleSaveSettings" @reset="handleResetSettings" />
          </div>

          <!-- 系统工具页面 -->
          <div v-else-if="activeMenu === 'tools'" class="content-view content-view--full">
            <SystemTools @action="handleAction" />
          </div>

          <!-- 其他页面内容 -->
          <div v-else class="content-view">
            <PlaceholderView :icon="currentMenuIcon" :title="currentMenuLabel" />
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Shield,
  Users,
  FileText,
  MessageSquare,
  Tag,
  Briefcase,
  Image,
  Link,
  Settings,
  Wrench,
  Menu,
  RefreshCw,
  Bell,
  LayoutDashboard
} from 'lucide-vue-next'
import AppHeader from '@/components/layout/AppHeader.vue'
import Dashboard from '@/components/admin/Dashboard.vue'
import ArticleManagement from '@/components/admin/ArticleManagement.vue'
import ArticleEditor from '@/components/admin/ArticleEditor.vue'
import CommentManagement from '@/components/admin/CommentManagement.vue'
import TagManagement from '@/components/admin/TagManagement.vue'
import ProjectManagement from '@/components/admin/ProjectManagement.vue'
import GalleryManagement from '@/components/admin/GalleryManagement.vue'
import LinkManagement from '@/components/admin/LinkManagement.vue'
import UserManagement from '@/components/admin/UserManagement.vue'
import SiteSettings from '@/components/admin/SiteSettings.vue'
import SystemTools from '@/components/admin/SystemTools.vue'
import PlaceholderView from '@/components/admin/PlaceholderView.vue'
import { createArticle, updateArticle, getArticleDetail, deleteArticle } from '@/api/article'
import { message } from '@/utils/message'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 侧边栏状态
const sidebarCollapsed = ref(false)
const activeMenu = ref('dashboard')

// 从路由路径获取菜单ID
function getMenuIdFromPath(path: string): string {
  const match = path.match(/\/admin\/(\w+)/)
  return match ? match[1] : 'dashboard'
}

// 初始化和监听路由变化
onMounted(() => {
  activeMenu.value = getMenuIdFromPath(route.path)
})

watch(() => route.path, (newPath) => {
  if (newPath.startsWith('/admin/')) {
    activeMenu.value = getMenuIdFromPath(newPath)
  }
})

// 文章编辑器状态
const showArticleEditor = ref(false)
const editingArticleId = ref<number>()
const isEditingArticle = ref(false)
const isSaving = ref(false)
const articleListKey = ref(0) // 用于强制刷新文章列表

// 统计数据
const stats = ref({
  users: 2,
  newUsers: 0,
  articles: 2,
  publishedArticles: 2,
  draftArticles: 0,
  comments: 3,
  pendingComments: 1,
  visits: 0,
  todayVisits: 0,
  tags: 10,
  projects: 0,
  images: 0,
  friendLinks: 0,
  pendingLinks: 0
})

// 菜单配置
const menus = computed(() => [
  { id: 'dashboard', label: '概览', icon: LayoutDashboard, subtitle: '数据统计与快捷操作' },
  {
    id: 'articles',
    label: '文章管理',
    icon: FileText,
    badge: stats.value.draftArticles || undefined,
    subtitle: '管理文章、草稿和回收站'
  },
  { id: 'tags', label: '标签管理', icon: Tag, subtitle: '管理文章标签' },
  {
    id: 'comments',
    label: '评论管理',
    icon: MessageSquare,
    badge: stats.value.pendingComments || undefined,
    subtitle: '审核和管理评论'
  },
  { id: 'projects', label: '项目管理', icon: Briefcase, subtitle: '管理项目展示' },
  { id: 'gallery', label: '图库管理', icon: Image, subtitle: '管理图片资源' },
  {
    id: 'links',
    label: '友链管理',
    icon: Link,
    badge: stats.value.pendingLinks || undefined,
    subtitle: '管理友情链接'
  },
  { id: 'users', label: '用户管理', icon: Users, subtitle: '管理用户和阅读记录' },
  { id: 'settings', label: '网站配置', icon: Settings, subtitle: '配置网站基本信息' },
  { id: 'tools', label: '系统工具', icon: Wrench, subtitle: '备份、缓存和日志' }
])

// 当前菜单信息
const currentMenuLabel = computed(() => {
  const menu = menus.value.find((m) => m.id === activeMenu.value)
  return menu?.label || ''
})

const currentMenuSubtitle = computed(() => {
  const menu = menus.value.find((m) => m.id === activeMenu.value)
  return menu?.subtitle || ''
})

const currentMenuIcon = computed(() => {
  const menu = menus.value.find((m) => m.id === activeMenu.value)
  return menu?.icon || LayoutDashboard
})

// 切换侧边栏
function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

// 选择菜单
function selectMenu(menuId: string) {
  // 更新路由
  router.push(`/admin/${menuId}`)
}

// 文章操作
function handleCreateArticle() {
  showArticleEditor.value = true
  isEditingArticle.value = false
  editingArticleId.value = undefined
}

function handleEditArticle(id: number) {
  showArticleEditor.value = true
  isEditingArticle.value = true
  editingArticleId.value = id
}

function closeArticleEditor() {
  showArticleEditor.value = false
  editingArticleId.value = undefined
  isEditingArticle.value = false
}

async function handleSaveArticle(articleData: any) {
  if (isSaving.value) return // 防止重复提交
  
  try {
    isSaving.value = true
    
    // 准备文章数据，状态设为草稿 (0)
    const data = {
      title: articleData.title,
      content: articleData.content,
      summary: articleData.summary,
      coverImage: articleData.coverImage,
      status: 0, // 草稿
      isTop: articleData.isTop ? 1 : 0,
      tagIds: articleData.tagIds || [] // 标签ID数组
    }

    if (isEditingArticle.value && editingArticleId.value) {
      // 更新文章
      await updateArticle(editingArticleId.value, data)
      message.success('草稿已保存')
    } else {
      // 创建新文章
      const result = await createArticle(data)
      message.success('草稿已保存')
      // 保存成功后，切换到编辑模式
      isEditingArticle.value = true
      editingArticleId.value = result.id
    }
  } catch (error: any) {
    console.error('保存文章失败:', error)
    message.error(error.message || '保存失败，请重试')
  } finally {
    isSaving.value = false
  }
}

async function handlePublishArticle(articleData: any) {
  if (isSaving.value) return // 防止重复提交
  
  try {
    isSaving.value = true
    
    // 准备文章数据，状态设为已发布 (1)
    const data = {
      title: articleData.title,
      content: articleData.content,
      summary: articleData.summary,
      coverImage: articleData.coverImage,
      status: 1, // 已发布
      isTop: articleData.isTop ? 1 : 0,
      tagIds: articleData.tagIds || [] // 标签ID数组
    }

    if (isEditingArticle.value && editingArticleId.value) {
      // 更新文章
      await updateArticle(editingArticleId.value, data)
      message.success('文章已发布')
    } else {
      // 创建新文章
      await createArticle(data)
      message.success('文章已发布')
    }
    
    closeArticleEditor()
    // 刷新文章列表（通过切换到文章管理页面）
    articleListKey.value++
    selectMenu('articles')
  } catch (error: any) {
    console.error('发布文章失败:', error)
    message.error(error.message || '发布失败，请重试')
  } finally {
    isSaving.value = false
  }
}

function handleViewArticle(id: number) {
  // 在新标签页打开文章详情
  window.open(`/article/${id}`, '_blank')
}

async function handleDeleteArticle(id: number) {
  try {
    await ElMessageBox.confirm(
      '确定要删除这篇文章吗？删除后可以在回收站中恢复。',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteArticle(id)
    message.success('文章已移至回收站')
    
    // 刷新文章列表
    articleListKey.value++
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除文章失败:', error)
      message.error(error.message || '删除失败，请重试')
    }
  }
}

function handleRestoreArticle(id: number) {
  // TODO: 实现恢复文章功能
  message.info('恢复功能开发中')
}

function handleArticleStatsUpdate(articleStats: { total: number; published: number; draft: number }) {
  stats.value.articles = articleStats.total
  stats.value.publishedArticles = articleStats.published
  stats.value.draftArticles = articleStats.draft
}

// 评论操作
function handleApproveComment(id: number) {
  alert(`评论 ID: ${id} 已通过审核`)
  stats.value.pendingComments = Math.max(0, stats.value.pendingComments - 1)
}

function handleUnapproveComment(id: number) {
  alert(`评论 ID: ${id} 已取消通过`)
  stats.value.pendingComments++
}

function handleReplyComment(id: number) {
  alert(`回复评论 ID: ${id}`)
}

function handleMarkSpam(id: number) {
  alert(`评论 ID: ${id} 已标记为垃圾评论`)
}

function handleDeleteComment(id: number) {
  alert(`评论 ID: ${id} 已删除`)
  stats.value.comments--
}

// 处理操作
function handleAction(action: string) {
  console.log('执行操作:', action)
  alert(`功能开发中: ${action}`)
}

// 网站配置操作
function handleSaveSettings(settings: any) {
  console.log('保存设置:', settings)
  alert('设置已保存')
}

function handleResetSettings() {
  alert('设置已重置')
}
</script>

<style scoped>
.admin-wrapper {
  min-height: 100vh;
  background: var(--color-white);
}

.admin-page {
  position: fixed;
  top: 60px;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  background: var(--color-white);
  overflow: hidden;
}

/* 侧边栏 */
.admin-sidebar {
  width: 260px;
  background: var(--color-white);
  border-right: 1px solid var(--color-gray-200);
  border-top: 1px solid var(--color-gray-200);
  display: flex;
  flex-direction: column;
  transition: width var(--transition-normal);
  flex-shrink: 0;
  overflow: hidden;
}

.admin-sidebar.sidebar-collapsed {
  width: 70px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-xl);
  border-bottom: 1px solid var(--color-gray-200);
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  color: var(--color-miku-600);
  font-weight: var(--font-bold);
  font-size: var(--text-lg);
}

.logo-text {
  white-space: nowrap;
}

.sidebar-toggle {
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

.sidebar-toggle:hover {
  background: var(--color-gray-100);
  color: var(--color-gray-900);
}

.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-md);
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--spacing-lg);
  margin-bottom: var(--spacing-xs);
  border-radius: 8px;
  color: var(--color-gray-700);
  cursor: pointer;
  transition: all var(--transition-fast);
  position: relative;
}

.nav-item:hover {
  background: var(--color-miku-50);
  color: var(--color-miku-700);
}

.nav-item.active {
  background: linear-gradient(135deg, var(--color-miku-500), var(--color-cyan-500));
  color: var(--color-white);
}

.nav-icon {
  flex-shrink: 0;
}

.nav-text {
  flex: 1;
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  white-space: nowrap;
}

.nav-badge {
  padding: 2px 8px;
  background: var(--color-red-500);
  color: var(--color-white);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  border-radius: 10px;
}

.sidebar-collapsed .nav-item {
  justify-content: center;
  padding: var(--spacing-md);
}

/* 主内容区 */
.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-top: 1px solid var(--color-gray-200);
}

/* 顶部栏 */
.admin-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-xl) var(--spacing-2xl);
  background: var(--color-white);
  border-bottom: 1px solid var(--color-gray-200);
  flex-shrink: 0;
  position: relative;
  z-index: 10;
}

.topbar-left {
  flex: 1;
}

.page-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0 0 var(--spacing-xs) 0;
}

.page-subtitle {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  margin: 0;
}

.topbar-right {
  display: flex;
  gap: var(--spacing-sm);
}

.topbar-btn {
  position: relative;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-gray-100);
  border: none;
  border-radius: 8px;
  color: var(--color-gray-600);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.topbar-btn:hover {
  background: var(--color-miku-100);
  color: var(--color-miku-600);
}

.notification-dot {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 8px;
  height: 8px;
  background: var(--color-red-500);
  border-radius: 50%;
  border: 2px solid var(--color-white);
}

/* 内容区域 */
.admin-content {
  flex: 1;
  padding: 0;
  overflow-y: auto;
  background: var(--color-white);
}

.content-wrapper {
  padding: var(--spacing-2xl);
}

.content-view--full {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.content-view {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

/* 响应式 */
@media (max-width: 1024px) {
  .admin-sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 200;
    transform: translateX(-100%);
  }

  .admin-sidebar.sidebar-collapsed {
    transform: translateX(0);
    width: 70px;
  }
}

@media (max-width: 768px) {
  .admin-sidebar {
    width: 100%;
  }

  .admin-sidebar.sidebar-collapsed {
    transform: translateX(-100%);
  }

  .admin-topbar {
    padding: var(--spacing-lg) var(--spacing-xl);
  }

  .page-title {
    font-size: var(--text-xl);
  }

  .content-wrapper {
    padding: var(--spacing-xl);
  }
}
</style>
