<template>
  <div class="guestbook">
    <AppHeader />
    
    <main class="guestbook__main">
      <!-- 页面标题 - Z型顶部横线 -->
      <div class="guestbook-header z-top">
        <h1 class="guestbook-title">留言</h1>
        <p class="guestbook-subtitle">欢迎留下你的足迹 ✍️</p>
      </div>

      <!-- 左右布局容器 -->
      <div class="guestbook-layout">
        <!-- 左侧：留言墙 -->
        <div class="messages-wall">
          <h3 class="wall-title">留言墙 ({{ messages.length }})</h3>
          <div v-if="loading" class="loading">
            <div class="loading-spinner"></div>
            <p>加载中...</p>
          </div>
          <div v-else class="messages">
            <article
              v-for="message in messages"
              :key="message.id"
              class="message-item"
            >
              <div class="message-header">
                <div class="message-avatar">
                  <img :src="message.avatar" :alt="message.name" />
                </div>
                <div class="message-info">
                  <span class="message-name">{{ message.name }}</span>
                  <span class="message-date">{{ formatDate(message.createdAt) }}</span>
                </div>
              </div>
              <p class="message-text">{{ message.content }}</p>
            </article>
          </div>
        </div>

        <!-- 右侧：固定输入区域 -->
        <div class="input-panel">
          <div class="message-form">
            <h3 class="form-title">发表留言</h3>
            <form @submit.prevent="submitMessage">
              <div class="form-group">
                <input
                  v-model="form.name"
                  type="text"
                  placeholder="你的昵称"
                  class="form-input"
                  required
                />
              </div>
              <div class="form-group">
                <input
                  v-model="form.email"
                  type="email"
                  placeholder="邮箱（选填）"
                  class="form-input"
                />
              </div>
              <div class="form-group">
                <textarea
                  v-model="form.content"
                  placeholder="写下你想说的话..."
                  class="form-textarea"
                  rows="6"
                  required
                ></textarea>
              </div>
              <button type="submit" class="submit-btn">
                <Send :size="16" />
                发送留言
              </button>
            </form>
          </div>
        </div>
      </div>
    </main>
    
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Send } from 'lucide-vue-next'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'

const loading = ref(true)

interface Message {
  id: number
  name: string
  email?: string
  content: string
  avatar: string
  createdAt: string
}

const form = ref({
  name: '',
  email: '',
  content: ''
})

const messages = ref<Message[]>([
  {
    id: 1,
    name: '张三',
    content: '很棒的博客，期待更多精彩内容！',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Zhang',
    createdAt: '2024-01-15'
  },
  {
    id: 2,
    name: '李四',
    content: '文章写得很好，学到了很多东西。',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Li',
    createdAt: '2024-01-14'
  },
  {
    id: 3,
    name: '王五',
    content: '界面设计很漂亮，用户体验很棒！',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Wang',
    createdAt: '2024-01-13'
  },
  {
    id: 4,
    name: '赵六',
    content: '技术分享很实用，收藏了！',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Zhao',
    createdAt: '2024-01-12'
  },
  {
    id: 5,
    name: '小明',
    content: '代码示例很清晰，感谢分享！',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Ming',
    createdAt: '2024-01-11'
  },
  {
    id: 6,
    name: '小红',
    content: '博客内容质量很高，持续关注中~',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Hong',
    createdAt: '2024-01-10'
  }
])

function submitMessage() {
  console.log('Submit message:', form.value)
  // TODO: 实现留言提交
  form.value = { name: '', email: '', content: '' }
}

function formatDate(date: string) {
  return new Date(date).toLocaleDateString('zh-CN')
}

onMounted(() => {
  setTimeout(() => {
    loading.value = false
  }, 500)
})
</script>

<style scoped>
.guestbook {
  min-height: 100vh;
  padding-top: 60px;
  overflow-x: hidden;
  background: linear-gradient(to bottom, #fafafa 0%, #f5f5f5 100%);
}

.guestbook__main {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-3xl) var(--spacing-2xl);
}

/* 页面标题 */
.guestbook-header.z-top {
  text-align: center;
  margin-bottom: var(--spacing-3xl);
  padding: var(--spacing-3xl) 0 var(--spacing-2xl);
}

.guestbook-title {
  font-size: clamp(var(--text-3xl), 5vw, 48px);
  font-weight: var(--font-bold);
  margin: 0 0 var(--spacing-md) 0;
  letter-spacing: -0.02em;
  line-height: 1.2;
  background: linear-gradient(135deg, var(--color-miku-500), var(--color-cyan-500));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.guestbook-subtitle {
  font-size: var(--text-xl);
  color: var(--color-gray-600);
  margin: 0;
  font-weight: var(--font-medium);
  line-height: 1.6;
}

/* 左右布局容器 */
.guestbook-layout {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: var(--spacing-3xl);
  align-items: start;
}

/* 左侧留言墙 */
.messages-wall {
  width: 100%;
}

.wall-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0 0 var(--spacing-xl) 0;
  padding-bottom: var(--spacing-md);
  border-bottom: 2px solid var(--color-gray-200);
  position: relative;
}

.wall-title::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 60px;
  height: 2px;
  background: linear-gradient(90deg, var(--color-miku-400), var(--color-cyan-400));
}

/* 右侧固定输入面板 */
.input-panel {
  position: sticky;
  top: calc(60px + var(--spacing-xl));
  width: 360px;
}

/* 留言表单 - 参考相册风格 */
.message-form {
  background: var(--color-white);
  padding: var(--spacing-2xl);
  border-radius: var(--radius-lg);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.08),
    0 8px 32px rgba(0, 0, 0, 0.04);
  border: 1px solid var(--color-gray-200);
  transition: all var(--transition-base);
}

.message-form:hover {
  box-shadow: 
    0 8px 24px rgba(0, 0, 0, 0.12),
    0 16px 48px rgba(0, 0, 0, 0.08);
}

.form-title {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0 0 var(--spacing-xl) 0;
  padding-bottom: var(--spacing-md);
  border-bottom: 2px solid var(--color-gray-200);
  position: relative;
}

.form-title::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 40px;
  height: 2px;
  background: linear-gradient(90deg, var(--color-miku-400), var(--color-cyan-400));
}

.form-group {
  margin-bottom: var(--spacing-lg);
}

.form-input,
.form-textarea {
  width: 100%;
  padding: var(--spacing-md) var(--spacing-lg);
  font-size: var(--text-base);
  border: 2px solid var(--color-gray-200);
  border-radius: var(--radius-md);
  background: var(--color-white);
  transition: all var(--transition-fast);
  font-family: inherit;
}

.form-input:hover,
.form-textarea:hover {
  border-color: var(--color-gray-300);
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--color-miku-400);
  box-shadow: 0 0 0 4px rgba(57, 197, 187, 0.1);
  background: var(--color-white);
}

.form-textarea {
  resize: vertical;
  min-height: 120px;
  line-height: 1.6;
}

.submit-btn {
  width: 100%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-xl);
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--color-white);
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
  box-shadow: 0 2px 8px rgba(57, 197, 187, 0.2);
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(57, 197, 187, 0.3);
}

.submit-btn:active {
  transform: translateY(0);
}

/* 加载状态 */
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-3xl);
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
  to { transform: rotate(360deg); }
}

/* 留言网格 - 每行4个 */
.messages {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-xl);
}

/* 留言卡片 - 白色便签纸 + 主题色装饰 */
.message-item {
  position: relative;
  background: var(--color-white);
  padding: var(--spacing-xl);
  border-radius: 2px;
  box-shadow: 
    0 1px 3px rgba(0, 0, 0, 0.12),
    0 1px 2px rgba(0, 0, 0, 0.08);
  transition: all var(--transition-base);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  min-height: 160px;
  border-left: 3px solid var(--color-miku-400);
}

/* 轻微旋转效果 */
.message-item:nth-child(6n+1) {
  transform: rotate(-1deg);
  border-left-color: var(--color-miku-400);
}

.message-item:nth-child(6n+2) {
  transform: rotate(0.5deg);
  border-left-color: var(--color-cyan-400);
}

.message-item:nth-child(6n+3) {
  transform: rotate(-0.5deg);
  border-left-color: #ec4899;
}

.message-item:nth-child(6n+4) {
  transform: rotate(1deg);
  border-left-color: #10b981;
}

.message-item:nth-child(6n+5) {
  transform: rotate(-0.8deg);
  border-left-color: #8b5cf6;
}

.message-item:nth-child(6n) {
  transform: rotate(0.8deg);
  border-left-color: #f59e0b;
}

.message-item:hover {
  transform: translateY(-8px) rotate(0deg) !important;
  box-shadow: 
    0 10px 25px rgba(0, 0, 0, 0.15),
    0 6px 12px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

/* 顶部图钉效果 - 使用主题色 */
.message-item::before {
  content: '';
  position: absolute;
  top: -6px;
  left: 50%;
  transform: translateX(-50%);
  width: 12px;
  height: 12px;
  background: radial-gradient(circle, var(--color-miku-500) 0%, var(--color-miku-600) 100%);
  border-radius: 50%;
  box-shadow: 
    0 2px 4px rgba(0, 0, 0, 0.2),
    inset 0 1px 2px rgba(255, 255, 255, 0.3);
}

/* 纸张纹理效果 */
.message-item::after {
  content: '';
  position: absolute;
  inset: 0;
  background: 
    repeating-linear-gradient(
      0deg,
      transparent,
      transparent 2px,
      rgba(0, 0, 0, 0.01) 2px,
      rgba(0, 0, 0, 0.01) 4px
    );
  pointer-events: none;
  border-radius: 2px;
}

/* 头部信息 - 横向布局 */
.message-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px dashed var(--color-gray-200);
  position: relative;
  z-index: 1;
}

/* 头像 */
.message-avatar {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
}

.message-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--color-miku-200);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 用户信息 */
.message-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.message-name {
  font-size: var(--text-base);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.message-date {
  font-size: var(--text-xs);
  color: var(--color-gray-500);
  font-weight: var(--font-medium);
}

/* 留言内容 */
.message-text {
  font-size: var(--text-base);
  color: var(--color-gray-700);
  line-height: 1.7;
  margin: 0;
  word-break: break-word;
  position: relative;
  z-index: 1;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .guestbook-layout {
    grid-template-columns: 1fr 320px;
    gap: var(--spacing-2xl);
  }

  .input-panel {
    width: 320px;
  }

  .messages {
    grid-template-columns: repeat(3, 1fr);
    gap: var(--spacing-lg);
  }
}

@media (max-width: 768px) {
  .guestbook__main {
    padding: var(--spacing-xl) var(--spacing-lg);
  }

  .guestbook-header.z-top {
    padding: var(--spacing-2xl) 0;
    margin-bottom: var(--spacing-2xl);
  }

  .guestbook-title {
    font-size: var(--text-2xl);
  }

  .guestbook-subtitle {
    font-size: var(--text-lg);
  }

  .guestbook-layout {
    grid-template-columns: 1fr;
    gap: var(--spacing-2xl);
  }

  .input-panel {
    position: static;
    width: 100%;
    order: -1;
  }

  .messages-wall {
    order: 1;
  }

  .wall-title {
    font-size: var(--text-xl);
  }

  .message-form {
    padding: var(--spacing-xl);
  }

  .form-title {
    font-size: var(--text-lg);
  }

  .messages {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-md);
  }

  .message-item {
    padding: var(--spacing-lg);
  }

  .message-header {
    padding-bottom: var(--spacing-sm);
  }

  .message-avatar {
    width: 36px;
    height: 36px;
  }

  .message-name {
    font-size: var(--text-sm);
  }

  .message-text {
    font-size: var(--text-sm);
  }
}

@media (max-width: 480px) {
  .guestbook__main {
    padding: var(--spacing-lg) var(--spacing-md);
  }

  .messages {
    grid-template-columns: 1fr;
  }

  .message-form {
    padding: var(--spacing-lg);
  }

  .form-input,
  .form-textarea {
    padding: var(--spacing-sm) var(--spacing-md);
    font-size: var(--text-sm);
  }

  .submit-btn {
    padding: var(--spacing-sm) var(--spacing-lg);
    font-size: var(--text-sm);
  }
}
</style>
