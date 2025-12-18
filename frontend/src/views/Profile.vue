<template>
  <div class="profile-page">
    <div class="profile-header">
      <button class="back-btn" @click="$router.back()">
        <ArrowLeft :size="20" />
        <span>返回</span>
      </button>
    </div>
    <div class="profile-container">
      <!-- 左侧：用户信息和菜单 -->
      <ProfileSidebar v-model="activeMenu" />

      <!-- 右侧：功能操作区 -->
      <main class="profile-main">
        <!-- 账户设置 -->
        <section v-if="activeMenu === 'account'" class="content-section">
          <h1 class="section-title">账户设置</h1>
          <div class="form-group">
            <label class="form-label">用户名</label>
            <div class="input-group">
              <input type="text" :value="user?.name" class="form-input" />
              <button class="input-btn">修改</button>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">邮箱地址</label>
            <div class="input-group">
              <input type="email" :value="user?.email" class="form-input" />
              <button class="input-btn">修改</button>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">密码</label>
            <div class="input-group">
              <input type="password" value="••••••••" class="form-input" readonly />
              <button class="input-btn">修改</button>
            </div>
          </div>
        </section>

        <!-- 我的活动 -->
        <section v-if="activeMenu === 'activity'" class="content-section">
          <h1 class="section-title">我的活动</h1>
          <div class="stats-grid">
            <div class="stat-card">
              <MessageSquare :size="24" class="stat-icon" />
              <div class="stat-value">0</div>
              <div class="stat-label">留言数</div>
            </div>
            <div class="stat-card">
              <Heart :size="24" class="stat-icon" />
              <div class="stat-value">0</div>
              <div class="stat-label">点赞数</div>
            </div>
            <div class="stat-card">
              <BookMarked :size="24" class="stat-icon" />
              <div class="stat-value">0</div>
              <div class="stat-label">收藏数</div>
            </div>
          </div>
        </section>

        <!-- 阅读记录 -->
        <section v-if="activeMenu === 'reading'" class="content-section reading-section">
          <h1 class="section-title">阅读记录</h1>
          <div class="reading-list">
            <div class="empty-state">
              <BookOpen :size="48" class="empty-icon" />
              <p class="empty-text">暂无阅读记录</p>
              <p class="empty-desc">开始阅读文章，记录将会显示在这里</p>
            </div>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { MessageSquare, Heart, BookMarked, BookOpen, ArrowLeft } from 'lucide-vue-next'
import ProfileSidebar from '@/components/layout/ProfileSidebar.vue'

const userStore = useUserStore()
const user = computed(() => userStore.user)
const activeMenu = ref('account')
</script>

<style scoped>
.profile-page {
  height: 100vh;
  padding: 80px 2vw 40px;
  background: var(--color-white);
  overflow-y: auto;
}

.profile-header {
  max-width: 1200px;
  margin: 0 auto var(--spacing-2xl);
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-md);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: #666;
  background: transparent;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.back-btn:hover {
  background: #f5f5f5;
  color: #333;
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: var(--spacing-2xl);
  align-items: start;
}

/* 右侧主内容区 */
.profile-main {
  background: transparent;
  padding: 0 var(--spacing-3xl);
  min-height: 400px;
}

.content-section {
  max-width: 600px;
}

.reading-section {
  max-width: 100%;
}

.section-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-semibold);
  color: #333;
  margin: 0 0 var(--spacing-2xl) 0;
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid #f0f0f0;
}

/* 表单 */
.form-group {
  margin-bottom: var(--spacing-2xl);
}

.form-label {
  display: block;
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: #666;
  margin-bottom: var(--spacing-sm);
}

.input-group {
  display: flex;
  gap: var(--spacing-md);
}

.form-input {
  flex: 1;
  padding: var(--spacing-sm) 0;
  font-size: var(--text-sm);
  color: #333;
  background: transparent;
  border: none;
  border-bottom: 1px solid #e8e8e8;
  border-radius: 0;
  transition: all var(--transition-fast);
}

.form-input:focus {
  outline: none;
  border-bottom-color: var(--color-miku-400);
}

.input-btn {
  padding: var(--spacing-sm) var(--spacing-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-miku-500);
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.input-btn:hover {
  color: var(--color-miku-600);
  text-decoration: underline;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-3xl);
}

.stat-card {
  padding: var(--spacing-xl) 0;
  text-align: center;
}

.stat-icon {
  color: var(--color-miku-500);
  margin-bottom: var(--spacing-sm);
}

.stat-value {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: #333;
  margin-bottom: var(--spacing-xs);
}

.stat-label {
  font-size: var(--text-sm);
  color: #999;
}

/* 阅读记录 */
.reading-list {
  min-height: 400px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-4xl) var(--spacing-2xl);
  text-align: center;
}

.empty-icon {
  color: var(--color-miku-300);
  margin-bottom: var(--spacing-xl);
}

.empty-text {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--color-miku-600);
  margin: 0 0 var(--spacing-sm) 0;
}

.empty-desc {
  font-size: var(--text-sm);
  color: var(--color-gray-500);
  margin: 0;
}

/* 响应式 */
@media (max-width: 1024px) {
  .profile-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .profile-page {
    padding: 70px 4vw 30px;
  }

  .profile-main {
    padding: var(--spacing-xl);
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
