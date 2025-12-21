<template>
  <div class="dashboard-view">
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon stat-icon--blue">
          <Users :size="24" />
        </div>
        <div class="stat-content">
          <div class="stat-label">总用户数</div>
          <div class="stat-value">{{ stats.users }}</div>
          <div class="stat-trend stat-trend--up">
            <TrendingUp :size="14" />
            <span>本月 +{{ stats.newUsers }}</span>
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon stat-icon--green">
          <FileText :size="24" />
        </div>
        <div class="stat-content">
          <div class="stat-label">文章总数</div>
          <div class="stat-value">{{ stats.articles }}</div>
          <div class="stat-trend stat-trend--up">
            <TrendingUp :size="14" />
            <span>已发布 {{ stats.publishedArticles }}</span>
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon stat-icon--purple">
          <MessageSquare :size="24" />
        </div>
        <div class="stat-content">
          <div class="stat-label">评论总数</div>
          <div class="stat-value">{{ stats.comments }}</div>
          <div class="stat-trend stat-trend--up">
            <TrendingUp :size="14" />
            <span>待审核 {{ stats.pendingComments }}</span>
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon stat-icon--orange">
          <Eye :size="24" />
        </div>
        <div class="stat-content">
          <div class="stat-label">总访问量</div>
          <div class="stat-value">{{ stats.visits }}</div>
          <div class="stat-trend stat-trend--up">
            <TrendingUp :size="14" />
            <span>今日 +{{ stats.todayVisits }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h2 class="section-title">快捷操作</h2>
      <div class="action-grid">
        <button class="quick-action-btn" @click="$emit('action', 'article-create')">
          <Plus :size="20" />
          <span>新建文章</span>
        </button>
        <button class="quick-action-btn" @click="$emit('action', 'project-create')">
          <Plus :size="20" />
          <span>新建项目</span>
        </button>
        <button class="quick-action-btn" @click="$emit('action', 'gallery-upload')">
          <Upload :size="20" />
          <span>上传图片</span>
        </button>
        <button class="quick-action-btn" @click="$emit('action', 'comment-pending')">
          <Clock :size="20" />
          <span>审核评论</span>
        </button>
      </div>
    </div>

    <!-- 最近活动 -->
    <div class="recent-activity">
      <h2 class="section-title">最近活动</h2>
      <div class="activity-list">
        <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
          <div class="activity-icon" :class="`activity-icon--${activity.type}`">
            <component :is="getActivityIcon(activity.type)" :size="16" />
          </div>
          <div class="activity-content">
            <div class="activity-text">{{ activity.text }}</div>
            <div class="activity-time">{{ activity.time }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 系统状态 -->
    <div class="system-status">
      <h2 class="section-title">系统状态</h2>
      <div class="status-grid">
        <div class="status-item">
          <div class="status-header">
            <span class="status-label">服务器状态</span>
            <span class="status-badge status-badge--success">正常</span>
          </div>
          <div class="status-bar">
            <div class="status-bar-fill" style="width: 95%"></div>
          </div>
          <div class="status-info">运行时间: 15天 8小时</div>
        </div>
        <div class="status-item">
          <div class="status-header">
            <span class="status-label">数据库</span>
            <span class="status-badge status-badge--success">正常</span>
          </div>
          <div class="status-bar">
            <div class="status-bar-fill" style="width: 88%"></div>
          </div>
          <div class="status-info">连接数: 12/100</div>
        </div>
        <div class="status-item">
          <div class="status-header">
            <span class="status-label">磁盘空间</span>
            <span class="status-badge status-badge--warning">注意</span>
          </div>
          <div class="status-bar">
            <div class="status-bar-fill status-bar-fill--warning" style="width: 72%"></div>
          </div>
          <div class="status-info">已使用: 72.5 GB / 100 GB</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {
  Users,
  FileText,
  MessageSquare,
  Eye,
  TrendingUp,
  Plus,
  Upload,
  Clock,
  Edit,
  Trash2,
  UserPlus
} from 'lucide-vue-next'

interface Props {
  stats: {
    users: number
    newUsers: number
    articles: number
    publishedArticles: number
    comments: number
    pendingComments: number
    visits: number
    todayVisits: number
  }
}

defineProps<Props>()
defineEmits<{
  action: [action: string]
}>()

const recentActivities = ref([
  { id: 1, type: 'article', text: '发布了新文章《Vue 3 Composition API 实践》', time: '2分钟前' },
  { id: 2, type: 'comment', text: '张三 评论了文章《欢迎来到我的博客》', time: '15分钟前' },
  { id: 3, type: 'user', text: '新用户 李四 注册了账号', time: '1小时前' },
  { id: 4, type: 'article', text: '更新了文章《TypeScript 入门指南》', time: '2小时前' },
  { id: 5, type: 'delete', text: '删除了评论 #123', time: '3小时前' }
])

function getActivityIcon(type: string) {
  const icons: Record<string, any> = {
    article: FileText,
    comment: MessageSquare,
    user: UserPlus,
    edit: Edit,
    delete: Trash2
  }
  return icons[type] || FileText
}
</script>

<style scoped>
.dashboard-view {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2xl);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-xl);
}

.stat-card {
  background: var(--color-white);
  border-radius: 12px;
  padding: var(--spacing-xl);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  display: flex;
  gap: var(--spacing-lg);
  transition: all var(--transition-fast);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  flex-shrink: 0;
}

.stat-icon--blue {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: var(--color-white);
}

.stat-icon--green {
  background: linear-gradient(135deg, #10b981, #059669);
  color: var(--color-white);
}

.stat-icon--purple {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  color: var(--color-white);
}

.stat-icon--orange {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: var(--color-white);
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  margin-bottom: var(--spacing-xs);
}

.stat-value {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-xs);
}

.stat-trend {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
}

.stat-trend--up {
  color: #10b981;
}

.quick-actions {
  background: var(--color-white);
  border-radius: 12px;
  padding: var(--spacing-2xl);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.section-title {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
  margin: 0 0 var(--spacing-xl) 0;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-lg);
}

.quick-action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-xl);
  background: var(--color-gray-50);
  border: 2px solid var(--color-gray-200);
  border-radius: 12px;
  color: var(--color-gray-700);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.quick-action-btn:hover {
  background: var(--color-miku-50);
  border-color: var(--color-miku-500);
  color: var(--color-miku-700);
  transform: translateY(-4px);
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .action-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .action-grid {
    grid-template-columns: 1fr;
  }
}

.recent-activity,
.system-status {
  background: var(--color-white);
  border-radius: 12px;
  padding: var(--spacing-2xl);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.activity-item {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--color-gray-50);
  border-radius: 8px;
  transition: all var(--transition-fast);
}

.activity-item:hover {
  background: var(--color-gray-100);
}

.activity-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  flex-shrink: 0;
}

.activity-icon--article {
  background: var(--color-miku-100);
  color: var(--color-miku-600);
}

.activity-icon--comment {
  background: #dbeafe;
  color: #2563eb;
}

.activity-icon--user {
  background: #d1fae5;
  color: #059669;
}

.activity-icon--edit {
  background: #fef3c7;
  color: #d97706;
}

.activity-icon--delete {
  background: #fee2e2;
  color: #dc2626;
}

.activity-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.activity-text {
  font-size: var(--text-sm);
  color: var(--color-gray-900);
}

.activity-time {
  font-size: var(--text-xs);
  color: var(--color-gray-500);
}

.status-grid {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.status-item {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-label {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-700);
}

.status-badge {
  padding: 2px 8px;
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  border-radius: 12px;
}

.status-badge--success {
  background: #d1fae5;
  color: #065f46;
}

.status-badge--warning {
  background: #fef3c7;
  color: #92400e;
}

.status-bar {
  height: 8px;
  background: var(--color-gray-200);
  border-radius: 4px;
  overflow: hidden;
}

.status-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-miku-500), var(--color-cyan-500));
  border-radius: 4px;
  transition: width var(--transition-fast);
}

.status-bar-fill--warning {
  background: linear-gradient(90deg, #f59e0b, #d97706);
}

.status-info {
  font-size: var(--text-xs);
  color: var(--color-gray-600);
}
</style>
