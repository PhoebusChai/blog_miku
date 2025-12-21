<template>
  <div class="site-settings">
    <!-- 设置导航 -->
    <div class="settings-nav">
      <button
        v-for="tab in tabs"
        :key="tab.id"
        class="nav-btn"
        :class="{ active: activeTab === tab.id }"
        @click="activeTab = tab.id"
      >
        <component :is="tab.icon" :size="18" />
        <span>{{ tab.label }}</span>
      </button>
    </div>

    <!-- 基本设置 -->
    <div v-if="activeTab === 'basic'" class="settings-section">
      <h2 class="section-title">基本设置</h2>
      <div class="settings-form">
        <div class="form-group">
          <label class="form-label">网站名称</label>
          <input v-model="settings.siteName" type="text" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">网站描述</label>
          <textarea v-model="settings.siteDescription" class="form-textarea" rows="3" />
        </div>
        <div class="form-group">
          <label class="form-label">网站关键词</label>
          <input v-model="settings.siteKeywords" type="text" class="form-input" />
          <span class="form-hint">多个关键词用逗号分隔</span>
        </div>
        <div class="form-group">
          <label class="form-label">网站Logo URL</label>
          <input v-model="settings.siteLogo" type="text" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">网站图标 URL</label>
          <input v-model="settings.siteFavicon" type="text" class="form-input" />
        </div>
      </div>
    </div>

    <!-- 社交媒体 -->
    <div v-if="activeTab === 'social'" class="settings-section">
      <h2 class="section-title">社交媒体</h2>
      <div class="settings-form">
        <div class="form-group">
          <label class="form-label">GitHub</label>
          <input v-model="settings.social.github" type="text" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">Twitter</label>
          <input v-model="settings.social.twitter" type="text" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">微博</label>
          <input v-model="settings.social.weibo" type="text" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">邮箱</label>
          <input v-model="settings.social.email" type="email" class="form-input" />
        </div>
      </div>
    </div>

    <!-- SEO设置 -->
    <div v-if="activeTab === 'seo'" class="settings-section">
      <h2 class="section-title">SEO设置</h2>
      <div class="settings-form">
        <div class="form-group">
          <label class="form-label">百度统计代码</label>
          <textarea v-model="settings.seo.baiduAnalytics" class="form-textarea" rows="3" />
        </div>
        <div class="form-group">
          <label class="form-label">Google Analytics ID</label>
          <input v-model="settings.seo.googleAnalytics" type="text" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">网站验证代码</label>
          <textarea v-model="settings.seo.verification" class="form-textarea" rows="3" />
        </div>
        <div class="form-group">
          <label class="form-checkbox">
            <input v-model="settings.seo.enableSitemap" type="checkbox" />
            <span>启用站点地图</span>
          </label>
        </div>
        <div class="form-group">
          <label class="form-checkbox">
            <input v-model="settings.seo.enableRobots" type="checkbox" />
            <span>启用 robots.txt</span>
          </label>
        </div>
      </div>
    </div>

    <!-- 评论设置 -->
    <div v-if="activeTab === 'comment'" class="settings-section">
      <h2 class="section-title">评论设置</h2>
      <div class="settings-form">
        <div class="form-group">
          <label class="form-checkbox">
            <input v-model="settings.comment.enableComment" type="checkbox" />
            <span>启用评论功能</span>
          </label>
        </div>
        <div class="form-group">
          <label class="form-checkbox">
            <input v-model="settings.comment.requireApproval" type="checkbox" />
            <span>评论需要审核</span>
          </label>
        </div>
        <div class="form-group">
          <label class="form-checkbox">
            <input v-model="settings.comment.requireLogin" type="checkbox" />
            <span>评论需要登录</span>
          </label>
        </div>
        <div class="form-group">
          <label class="form-label">评论最大长度</label>
          <input v-model.number="settings.comment.maxLength" type="number" class="form-input" />
        </div>
      </div>
    </div>

    <!-- 保存按钮 -->
    <div class="settings-actions">
      <button class="btn-secondary" @click="handleReset">重置</button>
      <button class="btn-primary" @click="handleSave">保存设置</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Globe, Share2, Search, MessageSquare } from 'lucide-vue-next'

const activeTab = ref('basic')

const tabs = [
  { id: 'basic', label: '基本设置', icon: Globe },
  { id: 'social', label: '社交媒体', icon: Share2 },
  { id: 'seo', label: 'SEO设置', icon: Search },
  { id: 'comment', label: '评论设置', icon: MessageSquare }
]

const settings = ref({
  siteName: '我的博客',
  siteDescription: '一个基于 Vue 3 的现代化博客系统',
  siteKeywords: 'Vue,TypeScript,博客,前端开发',
  siteLogo: '/logo.png',
  siteFavicon: '/favicon.ico',
  social: {
    github: 'https://github.com/username',
    twitter: 'https://twitter.com/username',
    weibo: 'https://weibo.com/username',
    email: 'contact@example.com'
  },
  seo: {
    baiduAnalytics: '',
    googleAnalytics: '',
    verification: '',
    enableSitemap: true,
    enableRobots: true
  },
  comment: {
    enableComment: true,
    requireApproval: true,
    requireLogin: false,
    maxLength: 500
  }
})

const emit = defineEmits<{
  save: [settings: typeof settings.value]
  reset: []
}>()

function handleSave() {
  emit('save', settings.value)
  alert('设置已保存')
}

function handleReset() {
  if (confirm('确定要重置所有设置吗？')) {
    emit('reset')
  }
}
</script>

<style scoped>
.site-settings {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  overflow: hidden;
}

.settings-nav {
  display: flex;
  gap: var(--spacing-xs);
  padding: var(--spacing-lg) 2%;
  background: var(--color-white);
  border-bottom: 1px solid var(--color-gray-200);
  overflow-x: auto;
  flex-shrink: 0;
}

.settings-nav::-webkit-scrollbar {
  height: 4px;
}

.settings-nav::-webkit-scrollbar-track {
  background: transparent;
}

.settings-nav::-webkit-scrollbar-thumb {
  background: var(--color-gray-300);
  border-radius: 2px;
}

.nav-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-600);
  background: var(--color-white);
  border: 1px solid var(--color-gray-200);
  border-radius: 8px;
  cursor: pointer;
  transition: all var(--transition-fast);
  white-space: nowrap;
}

.nav-btn:hover {
  background: var(--color-gray-50);
  border-color: var(--color-gray-300);
  color: var(--color-gray-900);
}

.nav-btn.active {
  background: linear-gradient(135deg, var(--color-miku-500), var(--color-cyan-500));
  border-color: var(--color-miku-500);
  color: var(--color-white);
}

.settings-section {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-2xl) 2%;
}

.settings-section::-webkit-scrollbar {
  width: 6px;
}

.settings-section::-webkit-scrollbar-track {
  background: transparent;
}

.settings-section::-webkit-scrollbar-thumb {
  background: var(--color-gray-300);
  border-radius: 3px;
}

.settings-section::-webkit-scrollbar-thumb:hover {
  background: var(--color-gray-400);
}

.section-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0 0 var(--spacing-sm) 0;
}

.section-title::after {
  content: '';
  display: block;
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, var(--color-miku-500), var(--color-cyan-500));
  border-radius: 2px;
  margin-top: var(--spacing-md);
}

.settings-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  max-width: 700px;
  margin-top: var(--spacing-2xl);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.form-label {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-700);
}

.form-input,
.form-textarea {
  padding: var(--spacing-md) var(--spacing-lg);
  font-size: var(--text-sm);
  color: var(--color-gray-900);
  background: var(--color-white);
  border: 2px solid var(--color-gray-200);
  border-radius: 8px;
  transition: all var(--transition-fast);
}

.form-input:hover,
.form-textarea:hover {
  border-color: var(--color-gray-300);
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--color-miku-500);
  box-shadow: 0 0 0 4px rgba(57, 197, 187, 0.1);
}

.form-textarea {
  resize: vertical;
  font-family: inherit;
  line-height: 1.6;
}

.form-hint {
  font-size: var(--text-xs);
  color: var(--color-gray-500);
  margin-top: 4px;
}

.form-checkbox {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--color-gray-50);
  border-radius: 8px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.form-checkbox:hover {
  background: var(--color-gray-100);
}

.form-checkbox input[type='checkbox'] {
  width: 20px;
  height: 20px;
  cursor: pointer;
  accent-color: var(--color-miku-500);
}

.form-checkbox span {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-700);
}

.settings-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  padding: var(--spacing-xl) 2%;
  background: var(--color-white);
  border-top: 1px solid var(--color-gray-200);
  flex-shrink: 0;
}

.btn-secondary,
.btn-primary {
  padding: var(--spacing-md) var(--spacing-2xl);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-secondary {
  color: var(--color-gray-700);
  background: var(--color-white);
  border: 2px solid var(--color-gray-300);
}

.btn-secondary:hover {
  background: var(--color-gray-50);
  border-color: var(--color-gray-400);
}

.btn-primary {
  color: var(--color-white);
  background: linear-gradient(135deg, var(--color-miku-500), var(--color-cyan-500));
  box-shadow: 0 2px 8px rgba(57, 197, 187, 0.2);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(57, 197, 187, 0.3);
}

.btn-primary:active {
  transform: translateY(0);
}
</style>
