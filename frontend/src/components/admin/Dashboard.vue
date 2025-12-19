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
  </div>
</template>

<script setup lang="ts">
import { Users, FileText, MessageSquare, Eye, TrendingUp, Plus, Upload, Clock } from 'lucide-vue-next'

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
</style>
