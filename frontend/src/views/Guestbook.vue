<template>
  <div class="guestbook">
    <AppHeader />
    
    <main class="guestbook__main">
      <!-- 页面标题 -->
      <div class="guestbook-header">
        <div class="header-decoration">
          <PenLine :size="32" class="header-icon" />
        </div>
        <h1 class="guestbook-title">留言</h1>
        <p class="guestbook-subtitle">欢迎留下你的足迹</p>
      </div>

      <!-- 顶部区域：左边表单 + 右边统计 -->
      <div class="top-section">
        <!-- 左侧：留言表单 -->
        <div class="form-card">
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

        <!-- 右侧：统计信息 -->
        <div class="stats-section">
          <div class="stat-card">
            <div class="stat-icon">
              <MessageSquare :size="24" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ messages.length }}</div>
              <div class="stat-label">总留言数</div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon">
              <Users :size="24" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ uniqueVisitors }}</div>
              <div class="stat-label">访客数</div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon">
              <Clock :size="24" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ recentMessages }}</div>
              <div class="stat-label">今日留言</div>
            </div>
          </div>

          <div class="recent-visitors">
            <h4 class="recent-title">最近访客</h4>
            <div class="visitor-list">
              <div
                v-for="visitor in recentVisitorsList"
                :key="visitor.id"
                class="visitor-item"
              >
                <img :src="visitor.avatar" :alt="visitor.name" class="visitor-avatar" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 留言墙区域 -->
      <div class="messages-section">
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
              </div>
            </div>
            <p class="message-text">{{ message.content }}</p>
            <div class="message-footer">
              <span class="message-time">{{ formatDate(message.createdAt) }}</span>
              <button 
                class="like-btn" 
                :class="{ 'liked': message.liked }"
                @click.stop="toggleLike(message.id)"
              >
                <Heart :size="16" :fill="message.liked ? 'currentColor' : 'none'" />
                <span>{{ message.likes }}</span>
              </button>
            </div>
          </article>
        </div>
        
        <!-- 加载更多指示器 -->
        <div v-if="loadingMore" class="loading-more">
          <div class="loading-spinner"></div>
          <p>加载更多...</p>
        </div>
        
        <!-- 没有更多数据提示 -->
        <div v-if="!hasMore && messages.length > 0" class="no-more">
          <p>没有更多留言了 ~</p>
        </div>
      </div>
    </main>
    
    <AppFooter />

    <!-- 返回顶部按钮 -->
    <transition name="fade">
      <button 
        v-if="showBackToTop" 
        class="back-to-top"
        @click="scrollToTop"
        title="返回顶部"
      >
        <ArrowUp :size="24" />
      </button>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { Send, ArrowUp, MessageSquare, Users, Clock, Heart, PenLine } from 'lucide-vue-next'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'

const loading = ref(true)
const loadingMore = ref(false)
const hasMore = ref(true)
const page = ref(1)
const showBackToTop = ref(false)

// 统计数据
const uniqueVisitors = ref(128)
const recentMessages = ref(8)
const recentVisitorsList = ref([
  { id: 1, name: '访客1', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Visitor1' },
  { id: 2, name: '访客2', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Visitor2' },
  { id: 3, name: '访客3', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Visitor3' },
  { id: 4, name: '访客4', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Visitor4' },
  { id: 5, name: '访客5', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Visitor5' },
  { id: 6, name: '访客6', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Visitor6' }
])

interface Message {
  id: number
  name: string
  email?: string
  content: string
  avatar: string
  createdAt: string
  likes: number
  liked: boolean
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
    createdAt: '2024-01-15',
    likes: 12,
    liked: false
  },
  {
    id: 2,
    name: '李四',
    content: '文章写得很好，学到了很多东西。',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Li',
    createdAt: '2024-01-14',
    likes: 8,
    liked: false
  },
  {
    id: 3,
    name: '王五',
    content: '界面设计很漂亮，用户体验很棒！',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Wang',
    createdAt: '2024-01-13',
    likes: 15,
    liked: false
  },
  {
    id: 4,
    name: '赵六',
    content: '技术分享很实用，收藏了！',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Zhao',
    createdAt: '2024-01-12',
    likes: 6,
    liked: false
  },
  {
    id: 5,
    name: '小明',
    content: '代码示例很清晰，感谢分享！',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Ming',
    createdAt: '2024-01-11',
    likes: 20,
    liked: false
  },
  {
    id: 6,
    name: '小红',
    content: '博客内容质量很高，持续关注中~',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Hong',
    createdAt: '2024-01-10',
    likes: 10,
    liked: false
  }
])

// 点赞功能
function toggleLike(messageId: number) {
  const message = messages.value.find(m => m.id === messageId)
  if (message) {
    if (message.liked) {
      message.likes--
      message.liked = false
    } else {
      message.likes++
      message.liked = true
    }
  }
}

// 模拟加载更多数据
const mockNames = ['小刚', '小美', '小华', '小丽', '小强', '小芳', '小军', '小雪']
const mockContents = [
  '1非常实用的内容，感谢分享！非常实用的内容，感谢分享！非常实用的内容，感谢分享！非常实用的内容，感谢分享！',
  '学到了很多新知识，继续加油！',
  '写得很详细，很有帮助！',
  '期待更多优质内容！',
  '界面很美观，体验很好！',
  '内容很有深度，值得学习！',
  '代码质量很高，赞一个！',
  '文章结构清晰，易于理解！'
]

function loadMoreMessages() {
  if (loadingMore.value || !hasMore.value) return

  loadingMore.value = true
  
  // 模拟API请求
  setTimeout(() => {
    const newMessages: Message[] = []
    const startId = messages.value.length + 1
    
    // 每次加载6条
    for (let i = 0; i < 6; i++) {
      const id = startId + i
      const name = mockNames[Math.floor(Math.random() * mockNames.length)]
      const content = mockContents[Math.floor(Math.random() * mockContents.length)]
      const date = new Date()
      date.setDate(date.getDate() - (messages.value.length + i + 1))
      
      newMessages.push({
        id,
        name,
        content,
        avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${name}${id}`,
        createdAt: date.toISOString().split('T')[0],
        likes: Math.floor(Math.random() * 20),
        liked: false
      })
    }
    
    messages.value.push(...newMessages)
    page.value++
    loadingMore.value = false
    
    // 模拟最多加载3页
    if (page.value >= 4) {
      hasMore.value = false
    }
  }, 1000)
}

// 滚动监听
function handleScroll() {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  const windowHeight = window.innerHeight
  const documentHeight = document.documentElement.scrollHeight
  
  // 显示/隐藏返回顶部按钮（滚动超过300px时显示）
  showBackToTop.value = scrollTop > 300
  
  // 距离底部200px时触发加载
  if (scrollTop + windowHeight >= documentHeight - 200) {
    loadMoreMessages()
  }
}

// 返回顶部
function scrollToTop() {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

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
  
  // 添加滚动监听
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  // 移除滚动监听
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.guestbook {
  min-height: 100vh;
  padding-top: 60px;
  overflow-x: hidden;
  background: var(--color-white);
}

.guestbook__main {
  max-width: 1400px;
  width: 80%;
  margin: 0 auto;
  padding: var(--spacing-3xl) var(--spacing-2xl);
  overflow: visible;
}

/* 页面标题 */
.guestbook-header {
  text-align: center;
  margin-bottom: var(--spacing-3xl);
  padding: var(--spacing-3xl) 0 var(--spacing-2xl);
}

.header-decoration {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  margin-bottom: var(--spacing-lg);
  background: linear-gradient(135deg, var(--color-miku-100), var(--color-cyan-100));
  border-radius: 50%;
}

.header-icon {
  color: var(--color-miku-500);
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

/* 顶部区域：左右布局 */
.top-section {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: var(--spacing-3xl);
  margin-bottom: var(--spacing-4xl);
}

/* 留言墙区域 */
.messages-section {
  width: 100%;
  margin-top: var(--spacing-2xl);
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

/* 左侧表单卡片 - 简洁风格 */
.form-card {
  background: transparent;
  padding: 0;
}

.form-title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--color-gray-700);
  margin: 0 0 var(--spacing-xl) 0;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.form-title::before {
  content: '';
  width: 4px;
  height: 20px;
  background: var(--color-miku-500);
  border-radius: 2px;
}

.form-group {
  margin-bottom: var(--spacing-lg);
}

.form-input,
.form-textarea {
  width: 100%;
  padding: var(--spacing-md) var(--spacing-lg);
  font-size: var(--text-base);
  border: 1px solid var(--color-gray-200);
  border-radius: var(--radius-lg);
  background: var(--color-white);
  transition: all var(--transition-fast);
  font-family: inherit;
}

.form-input:hover,
.form-textarea:hover {
  border-color: var(--color-gray-300);
  background: var(--color-gray-50);
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--color-miku-400);
  background: var(--color-white);
}

.form-input::placeholder,
.form-textarea::placeholder {
  color: var(--color-gray-400);
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
  font-weight: var(--font-medium);
  color: var(--color-white);
  background: var(--color-miku-500);
  border: none;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.submit-btn:hover {
  background: var(--color-miku-600);
}

.submit-btn:active {
  transform: scale(0.98);
}

/* 右侧统计区域 - 简洁风格 */
.stats-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.stat-card {
  background: var(--color-white);
  padding: var(--spacing-lg);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-gray-200);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  transition: all var(--transition-fast);
}

.stat-card:hover {
  border-color: var(--color-miku-300);
  background: var(--color-gray-50);
}

.stat-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-miku-100);
  color: var(--color-miku-600);
  border-radius: var(--radius-md);
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  line-height: 1.2;
  margin-bottom: 2px;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--color-gray-500);
  font-weight: var(--font-normal);
}

/* 最近访客 */
.recent-visitors {
  background: var(--color-white);
  padding: var(--spacing-lg);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-gray-200);
}

.recent-title {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-700);
  margin: 0 0 var(--spacing-md) 0;
}

.visitor-list {
  display: flex;
  gap: var(--spacing-xs);
  flex-wrap: wrap;
}

.visitor-item {
  position: relative;
}

.visitor-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 2px solid var(--color-white);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all var(--transition-fast);
}

.visitor-avatar:hover {
  transform: scale(1.15);
  z-index: 10;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
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

/* 加载更多 */
.loading-more {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-2xl) 0;
  color: var(--color-gray-500);
}

.loading-more .loading-spinner {
  width: 32px;
  height: 32px;
}

/* 没有更多 */
.no-more {
  text-align: center;
  padding: var(--spacing-2xl) 0;
  color: var(--color-gray-400);
  font-size: var(--text-sm);
}

/* 留言网格 - Grid布局（固定位置） */
.messages {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-xl);
  padding: calc(var(--spacing-2xl) + 10px) 0 var(--spacing-2xl) 0;
}

/* 留言卡片 - 白色便签纸 + 主题色装饰 */
.message-item {
  position: relative;
  background: var(--color-white);
  padding: var(--spacing-xl);
  padding-top: calc(var(--spacing-xl) + 8px);
  border-radius: 2px;
  box-shadow: 
    0 1px 3px rgba(0, 0, 0, 0.12),
    0 1px 2px rgba(0, 0, 0, 0.08);
  transition: all var(--transition-base);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  border-left: 3px solid var(--color-miku-400);
  margin-top: 12px;
  height: fit-content;
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

/* 留言底部 */
.message-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: var(--spacing-md);
  border-top: 1px dashed var(--color-gray-200);
  position: relative;
  z-index: 1;
}

.message-time {
  font-size: var(--text-xs);
  color: var(--color-gray-400);
  font-weight: var(--font-medium);
}

/* 点赞按钮 */
.like-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-500);
  background: transparent;
  border: 1px solid var(--color-gray-200);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.like-btn:hover {
  color: var(--color-miku-500);
  border-color: var(--color-miku-300);
  background: var(--color-miku-50);
}

.like-btn.liked {
  color: var(--color-miku-500);
  border-color: var(--color-miku-400);
  background: var(--color-miku-50);
}

.like-btn.liked:hover {
  color: var(--color-miku-600);
  border-color: var(--color-miku-500);
  background: var(--color-miku-100);
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .messages {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 1024px) {
  .guestbook__main {
    width: 90%;
  }

  .messages {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-lg);
  }
}

@media (max-width: 768px) {
  .guestbook__main {
    width: 95%;
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

  .top-section {
    grid-template-columns: 1fr;
    gap: var(--spacing-2xl);
  }

  .form-card {
    padding: var(--spacing-xl);
  }

  .stat-card {
    padding: var(--spacing-lg);
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

  .message-item {
    margin-bottom: var(--spacing-lg);
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

/* 返回顶部按钮 */
.back-to-top {
  position: fixed;
  bottom: var(--spacing-2xl);
  right: var(--spacing-2xl);
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  color: var(--color-white);
  border: none;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.3);
  transition: all var(--transition-base);
  z-index: 100;
}

.back-to-top:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(57, 197, 187, 0.4);
}

.back-to-top:active {
  transform: translateY(-2px);
}

/* 淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-base);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .back-to-top {
    bottom: var(--spacing-xl);
    right: var(--spacing-lg);
    width: 44px;
    height: 44px;
  }
}
</style>
