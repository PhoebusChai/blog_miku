<template>
  <div class="site-settings">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>加载配置中...</p>
    </div>

    <template v-else>
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
            <input v-model="settings.site_name" type="text" class="form-input" />
          </div>
          <div class="form-group">
            <label class="form-label">网站描述</label>
            <textarea v-model="settings.site_description" class="form-textarea" rows="3" />
          </div>
          <div class="form-group">
            <label class="form-label">网站关键词</label>
            <input v-model="settings.site_keywords" type="text" class="form-input" />
            <span class="form-hint">多个关键词用逗号分隔</span>
          </div>
          <div class="form-group">
            <label class="form-label">网站创建日期</label>
            <input v-model="settings.site_start_date" type="date" class="form-input" />
            <span class="form-hint">用于计算网站运行天数</span>
          </div>
          <div class="form-group">
            <label class="form-label">版权信息</label>
            <input v-model="settings.site_copyright" type="text" class="form-input" />
          </div>
        </div>
      </div>

      <!-- 博主信息 -->
      <div v-if="activeTab === 'blogger'" class="settings-section">
        <h2 class="section-title">博主信息</h2>
        <div class="settings-form">
          <div class="form-group">
            <label class="form-label">头像 URL</label>
            <input v-model="settings.blogger_avatar" type="text" class="form-input" />
          </div>
          <div class="form-group">
            <label class="form-label">昵称</label>
            <input v-model="settings.blogger_name" type="text" class="form-input" />
          </div>
          <div class="form-group">
            <label class="form-label">职位/角色</label>
            <input v-model="settings.blogger_role" type="text" class="form-input" />
          </div>
          <div class="form-group">
            <label class="form-label">所在地</label>
            <input v-model="settings.blogger_location" type="text" class="form-input" />
          </div>
          <div class="form-group">
            <label class="form-label">个人简介</label>
            <textarea v-model="settings.blogger_bio" class="form-textarea" rows="4" />
          </div>
          <div class="form-group">
            <label class="form-label">技能标签</label>
            <input v-model="settings.blogger_skills" type="text" class="form-input" />
            <span class="form-hint">多个技能用逗号分隔，如：Vue.js, TypeScript, Node.js</span>
          </div>
          <div class="form-group">
            <label class="form-label">兴趣爱好</label>
            <input v-model="settings.blogger_interests" type="text" class="form-input" />
            <span class="form-hint">多个爱好用逗号分隔，如：编程, 摄影, 音乐</span>
          </div>
        </div>
      </div>

      <!-- 社交媒体 -->
      <div v-if="activeTab === 'social'" class="settings-section">
        <h2 class="section-title">社交媒体</h2>
        <div class="settings-form">
          <div class="form-group">
            <label class="form-label">GitHub</label>
            <input v-model="settings.social_github" type="text" class="form-input" placeholder="https://github.com/username" />
          </div>
          <div class="form-group">
            <label class="form-label">哔哩哔哩</label>
            <input v-model="settings.social_bilibili" type="text" class="form-input" placeholder="https://space.bilibili.com/uid" />
          </div>
          <div class="form-group">
            <label class="form-label">邮箱</label>
            <input v-model="settings.social_email" type="email" class="form-input" placeholder="contact@example.com" />
          </div>
          <div class="form-group">
            <label class="form-label">Twitter / X</label>
            <input v-model="settings.social_twitter" type="text" class="form-input" placeholder="https://twitter.com/username" />
          </div>
          <div class="form-group">
            <label class="form-label">微博</label>
            <input v-model="settings.social_weibo" type="text" class="form-input" placeholder="https://weibo.com/username" />
          </div>
          <div class="form-group">
            <label class="form-label">知乎</label>
            <input v-model="settings.social_zhihu" type="text" class="form-input" placeholder="https://zhihu.com/people/username" />
          </div>
        </div>
      </div>

      <!-- 保存按钮 -->
      <div class="settings-actions">
        <button class="btn-secondary" @click="handleReset" :disabled="saving">重置</button>
        <button class="btn-primary" @click="handleSave" :disabled="saving">
          {{ saving ? '保存中...' : '保存设置' }}
        </button>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Globe, Share2, User } from 'lucide-vue-next'
import { getAllConfig, saveConfigs, type SiteConfig } from '@/api/config'
import { useConfigStore } from '@/stores/config'
import { message } from '@/utils/message'

const configStore = useConfigStore()
const activeTab = ref('basic')
const loading = ref(true)
const saving = ref(false)

const tabs = [
  { id: 'basic', label: '基本设置', icon: Globe },
  { id: 'blogger', label: '博主信息', icon: User },
  { id: 'social', label: '社交媒体', icon: Share2 }
]

const settings = ref<SiteConfig>({
  // 基本设置
  site_name: '',
  site_description: '',
  site_keywords: '',
  site_start_date: '',
  site_copyright: '',
  // 博主信息
  blogger_avatar: '',
  blogger_name: '',
  blogger_role: '',
  blogger_location: '',
  blogger_bio: '',
  blogger_skills: '',
  blogger_interests: '',
  // 社交媒体
  social_github: '',
  social_bilibili: '',
  social_email: '',
  social_twitter: '',
  social_weibo: '',
  social_zhihu: ''
})

// 加载配置
async function loadSettings() {
  loading.value = true
  try {
    const data = await getAllConfig()
    // 合并配置
    Object.keys(settings.value).forEach(key => {
      if (data[key] !== undefined) {
        settings.value[key] = data[key]
      }
    })
  } catch (error: any) {
    message.error(error.message || '加载配置失败')
  } finally {
    loading.value = false
  }
}

// 保存配置
async function handleSave() {
  saving.value = true
  try {
    await saveConfigs(settings.value)
    // 更新全局配置
    configStore.updateConfig(settings.value)
    message.success('设置已保存')
  } catch (error: any) {
    message.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

// 重置配置
function handleReset() {
  if (confirm('确定要重置所有设置吗？')) {
    loadSettings()
  }
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.site-settings {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  overflow: hidden;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: var(--spacing-md);
  color: var(--color-gray-500);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--color-gray-200);
  border-top-color: var(--color-miku-500);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
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

.btn-secondary:hover:not(:disabled) {
  background: var(--color-gray-50);
  border-color: var(--color-gray-400);
}

.btn-primary {
  color: var(--color-white);
  background: linear-gradient(135deg, var(--color-miku-500), var(--color-cyan-500));
  box-shadow: 0 2px 8px rgba(57, 197, 187, 0.2);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(57, 197, 187, 0.3);
}

.btn-primary:disabled,
.btn-secondary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
