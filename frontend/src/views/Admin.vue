<template>
  <div class="admin-page">
    <div class="admin-container">
      <!-- 页面标题 -->
      <div class="admin-header">
        <div class="header-content">
          <Shield :size="32" class="header-icon" />
          <div>
            <h1 class="admin-title">管理后台</h1>
            <p class="admin-subtitle">系统管理与数据统计</p>
          </div>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon stat-icon--blue">
            <Users :size="24" />
          </div>
          <div class="stat-content">
            <div class="stat-label">总用户数</div>
            <div class="stat-value">2</div>
            <div class="stat-trend stat-trend--up">
              <TrendingUp :size="14" />
              <span>+0%</span>
            </div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon stat-icon--green">
            <FileText :size="24" />
          </div>
          <div class="stat-content">
            <div class="stat-label">文章总数</div>
            <div class="stat-value">0</div>
            <div class="stat-trend stat-trend--up">
              <TrendingUp :size="14" />
              <span>+0%</span>
            </div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon stat-icon--purple">
            <MessageSquare :size="24" />
          </div>
          <div class="stat-content">
            <div class="stat-label">留言总数</div>
            <div class="stat-value">0</div>
            <div class="stat-trend stat-trend--up">
              <TrendingUp :size="14" />
              <span>+0%</span>
            </div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon stat-icon--orange">
            <Eye :size="24" />
          </div>
          <div class="stat-content">
            <div class="stat-label">总访问量</div>
            <div class="stat-value">0</div>
            <div class="stat-trend stat-trend--up">
              <TrendingUp :size="14" />
              <span>+0%</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 管理功能 -->
      <div class="admin-sections">
        <div class="section-card">
          <div class="section-header">
            <FileText :size="20" />
            <h2>内容管理</h2>
          </div>
          <div class="section-content">
            <button class="action-btn">
              <Plus :size="18" />
              <span>新建文章</span>
            </button>
            <button class="action-btn">
              <List :size="18" />
              <span>文章列表</span>
            </button>
            <button class="action-btn">
              <FolderOpen :size="18" />
              <span>分类管理</span>
            </button>
          </div>
        </div>

        <div class="section-card">
          <div class="section-header">
            <Users :size="20" />
            <h2>用户管理</h2>
          </div>
          <div class="section-content">
            <button class="action-btn">
              <List :size="18" />
              <span>用户列表</span>
            </button>
            <button class="action-btn">
              <UserPlus :size="18" />
              <span>添加用户</span>
            </button>
            <button class="action-btn">
              <Shield :size="18" />
              <span>权限管理</span>
            </button>
          </div>
        </div>

        <div class="section-card">
          <div class="section-header">
            <Settings :size="20" />
            <h2>系统设置</h2>
          </div>
          <div class="section-content">
            <button class="action-btn">
              <Globe :size="18" />
              <span>网站设置</span>
            </button>
            <button class="action-btn">
              <Palette :size="18" />
              <span>主题设置</span>
            </button>
            <button class="action-btn">
              <Database :size="18" />
              <span>数据备份</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Shield, Users, FileText, MessageSquare, Eye, TrendingUp, Plus, List, FolderOpen, UserPlus, Settings, Globe, Palette, Database } from 'lucide-vue-next'
</script>

<style scoped>
.admin-page {
  min-height: calc(100vh - 60px);
  padding: 80px 2vw 40px;
  background: linear-gradient(to bottom, var(--color-miku-50), var(--color-white));
}

.admin-container {
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面标题 */
.admin-header {
  background: linear-gradient(135deg, var(--color-miku-500), var(--color-cyan-500));
  border-radius: 16px;
  padding: var(--spacing-3xl);
  margin-bottom: var(--spacing-2xl);
  box-shadow: 0 4px 16px rgba(57, 197, 187, 0.2);
}

.header-content {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
  color: var(--color-white);
}

.header-icon {
  flex-shrink: 0;
}

.admin-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  margin: 0 0 var(--spacing-xs) 0;
}

.admin-subtitle {
  font-size: var(--text-base);
  margin: 0;
  opacity: 0.9;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-xl);
  margin-bottom: var(--spacing-2xl);
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

/* 管理功能 */
.admin-sections {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-xl);
}

.section-card {
  background: var(--color-white);
  border-radius: 12px;
  padding: var(--spacing-2xl);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.section-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-xl);
  color: var(--color-gray-900);
}

.section-header h2 {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  margin: 0;
}

.section-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-700);
  background: var(--color-gray-50);
  border: 1px solid var(--color-gray-200);
  border-radius: 8px;
  cursor: pointer;
  transition: all var(--transition-fast);
  text-align: left;
}

.action-btn:hover {
  background: var(--color-miku-50);
  border-color: var(--color-miku-300);
  color: var(--color-miku-700);
  transform: translateX(4px);
}

/* 响应式 */
@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .admin-sections {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .admin-page {
    padding: 70px 4vw 30px;
  }

  .admin-header {
    padding: var(--spacing-2xl);
  }

  .header-content {
    gap: var(--spacing-lg);
  }

  .admin-title {
    font-size: var(--text-2xl);
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .stat-card {
    padding: var(--spacing-lg);
  }
}
</style>
