<template>
  <div class="about">
    <AppHeader />
    
    <main class="about__main">
      <!-- 页面标题 -->
      <div class="about-header">
        <h1 class="about-title">关于我</h1>
        <p class="about-subtitle">Hello! 很高兴认识你 ✨</p>
      </div>

      <!-- 主要内容区 -->
      <div class="about-content">
        <!-- 个人简介 -->
        <section class="profile-section">
          <div class="profile-main">
            <div class="profile-avatar">
              <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix" alt="头像" />
            </div>
            <div class="profile-info">
              <h2 class="profile-name">落叶无痕</h2>
              <div class="profile-meta">
                <span class="meta-item">
                  <Code :size="16" />
                  全栈开发工程师
                </span>
                <span class="meta-divider">·</span>
                <span class="meta-item">
                  <MapPin :size="14" />
                  中国 · 北京
                </span>
              </div>
            </div>
          </div>

          <div class="profile-bio">
            <p>
              热爱编程，喜欢探索新技术。专注于前端开发，对用户体验和界面设计有着浓厚的兴趣。
              相信技术能够改变世界，代码可以创造美好。
            </p>
            <p>
              工作之余喜欢写博客分享技术心得，记录生活点滴。
              希望通过这个小站，能与更多志同道合的朋友交流学习。
            </p>
          </div>

          <!-- 社交链接 -->
          <div class="profile-social">
            <a href="https://github.com" target="_blank" class="social-link" title="GitHub">
              <Github :size="18" />
            </a>
            <a href="mailto:contact@example.com" class="social-link" title="Email">
              <Mail :size="18" />
            </a>
            <a href="https://space.bilibili.com" target="_blank" class="social-link" title="哔哩哔哩">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17.813 4.653h.854c1.51.054 2.769.578 3.773 1.574 1.004.995 1.524 2.249 1.56 3.76v7.36c-.036 1.51-.556 2.769-1.56 3.773s-2.262 1.524-3.773 1.56H5.333c-1.51-.036-2.769-.556-3.773-1.56S.036 18.858 0 17.347v-7.36c.036-1.511.556-2.765 1.56-3.76 1.004-.996 2.262-1.52 3.773-1.574h.774l-1.174-1.12a1.234 1.234 0 0 1-.373-.906c0-.356.124-.658.373-.907l.027-.027c.267-.249.573-.373.92-.373.347 0 .653.124.92.373L9.653 4.44c.071.071.134.142.187.213h4.267a.836.836 0 0 1 .16-.213l2.853-2.747c.267-.249.573-.373.92-.373.347 0 .662.151.929.4.267.249.391.551.391.907 0 .355-.124.657-.373.906zM5.333 7.24c-.746.018-1.373.276-1.88.773-.506.498-.769 1.13-.786 1.894v7.52c.017.764.28 1.395.786 1.893.507.498 1.134.756 1.88.773h13.334c.746-.017 1.373-.275 1.88-.773.506-.498.769-1.129.786-1.893v-7.52c-.017-.765-.28-1.396-.786-1.894-.507-.497-1.134-.755-1.88-.773zM8 11.107c.373 0 .684.124.933.373.25.249.383.569.4.96v1.173c-.017.391-.15.711-.4.96-.249.25-.56.374-.933.374s-.684-.125-.933-.374c-.25-.249-.383-.569-.4-.96V12.44c0-.373.129-.689.386-.947.258-.257.574-.386.947-.386zm8 0c.373 0 .684.124.933.373.25.249.383.569.4.96v1.173c-.017.391-.15.711-.4.96-.249.25-.56.374-.933.374s-.684-.125-.933-.374c-.25-.249-.383-.569-.4-.96V12.44c.017-.391.15-.711.4-.96.249-.249.56-.373.933-.373Z"/>
              </svg>
            </a>
          </div>

          <!-- 统计数据 -->
          <div class="profile-stats">
            <div class="stat-item">
              <span class="stat-value">42</span>
              <span class="stat-label">文章</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-value">12.5K</span>
              <span class="stat-label">访问</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-value">856</span>
              <span class="stat-label">点赞</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-value">234</span>
              <span class="stat-label">评论</span>
            </div>
          </div>
        </section>

        <!-- 兴趣爱好 -->
        <section class="content-section">
          <h3 class="section-title">兴趣爱好</h3>
          <div class="interests-list">
            <span v-for="interest in interests" :key="interest.name" class="interest-tag">
              <component :is="interest.icon" :size="16" />
              {{ interest.name }}
            </span>
          </div>
        </section>

        <!-- 时间线 -->
        <section class="content-section">
          <h3 class="section-title">成长历程</h3>
          <div class="timeline">
            <div v-for="(item, index) in timeline" :key="index" class="timeline-item">
              <div class="timeline-marker">
                <div class="timeline-dot"></div>
                <div class="timeline-year">{{ item.date }}</div>
              </div>
              <div class="timeline-content">
                <h4 class="timeline-title">{{ item.title }}</h4>
                <p class="timeline-desc">{{ item.desc }}</p>
              </div>
            </div>
          </div>
        </section>

        <!-- 联系方式 -->
        <section class="content-section">
          <h3 class="section-title">联系我</h3>
          <p class="section-desc">
            如果你有任何问题或建议，欢迎通过以下方式联系我。期待与你的交流！
          </p>
          <div class="contact-list">
            <a href="mailto:contact@example.com" class="contact-link">
              <Mail :size="20" />
              <span>contact@example.com</span>
            </a>
            <a href="https://github.com" target="_blank" class="contact-link">
              <Github :size="20" />
              <span>@username</span>
            </a>
          </div>
        </section>
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
import { ref, onMounted, onUnmounted } from 'vue'
import { 
  Code, MapPin, Github, Mail, 
  FileText, Eye, Heart, MessageCircle, 
  Calendar, Send, ArrowUp, Camera, Music, 
  Book, Gamepad2, Plane
} from 'lucide-vue-next'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'

const showBackToTop = ref(false)

// 兴趣爱好
const interests = ref([
  { name: '编程', icon: Code },
  { name: '摄影', icon: Camera },
  { name: '音乐', icon: Music },
  { name: '阅读', icon: Book },
  { name: '游戏', icon: Gamepad2 },
  { name: '旅行', icon: Plane }
])

// 时间线数据
const timeline = ref([
  {
    date: '2024',
    title: '全栈开发工程师',
    desc: '专注于前端技术栈，探索现代化开发方式'
  },
  {
    date: '2023',
    title: '开始写博客',
    desc: '创建个人技术博客，分享学习心得'
  },
  {
    date: '2022',
    title: '前端开发工程师',
    desc: '深入学习 Vue.js 和 React 生态'
  },
  {
    date: '2021',
    title: '开始学习编程',
    desc: '接触前端开发，开启技术之旅'
  }
])

// 滚动处理
function handleScroll() {
  showBackToTop.value = window.pageYOffset > 300
}

function scrollToTop() {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.about {
  min-height: 100vh;
  padding-top: 60px;
  overflow-x: hidden;
  background: var(--color-white);
}

.about__main {
  max-width: 1000px;
  margin: 0 auto;
  padding: var(--spacing-3xl) var(--spacing-2xl);
}

/* 页面标题 */
.about-header {
  text-align: center;
  margin-bottom: var(--spacing-3xl);
  padding: var(--spacing-3xl) 0 var(--spacing-2xl);
}

.about-title {
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

.about-subtitle {
  font-size: var(--text-xl);
  color: var(--color-gray-600);
  margin: 0;
  font-weight: var(--font-medium);
  line-height: 1.6;
}

/* 内容区 */
.about-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3xl);
}

/* 个人简介区 */
.profile-section {
  padding-bottom: var(--spacing-3xl);
  border-bottom: 1px solid var(--color-gray-200);
}

.profile-main {
  display: flex;
  align-items: center;
  gap: var(--spacing-2xl);
  margin-bottom: var(--spacing-2xl);
}

.profile-avatar {
  width: 100px;
  height: 100px;
  flex-shrink: 0;
}

.profile-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--color-miku-400);
}

.profile-info {
  flex: 1;
}

.profile-name {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0 0 var(--spacing-sm) 0;
}

.profile-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: var(--text-sm);
  color: var(--color-gray-600);
}

.meta-item svg {
  color: var(--color-miku-500);
  flex-shrink: 0;
}

.meta-divider {
  color: var(--color-gray-400);
}

.profile-bio {
  margin-bottom: var(--spacing-2xl);
}

.profile-bio p {
  font-size: var(--text-base);
  line-height: 1.8;
  color: var(--color-gray-700);
  margin: 0 0 var(--spacing-md) 0;
}

.profile-bio p:last-child {
  margin-bottom: 0;
}

/* 社交链接 */
.profile-social {
  display: flex;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-2xl);
}

.social-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  color: var(--color-gray-600);
  border: 1px solid var(--color-gray-300);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
  text-decoration: none;
}

.social-link:hover {
  color: var(--color-miku-600);
  border-color: var(--color-miku-400);
  background: var(--color-miku-50);
}

/* 统计数据 */
.profile-stats {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  line-height: 1;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: var(--color-gray-300);
}

/* 通用内容区块 */
.content-section {
  padding-bottom: var(--spacing-3xl);
  border-bottom: 1px solid var(--color-gray-200);
}

.content-section:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.section-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0 0 var(--spacing-xl) 0;
  position: relative;
  padding-bottom: var(--spacing-sm);
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 40px;
  height: 3px;
  background: linear-gradient(90deg, var(--color-miku-400), var(--color-cyan-400));
  border-radius: var(--radius-full);
}

.section-desc {
  font-size: var(--text-base);
  line-height: 1.8;
  color: var(--color-gray-700);
  margin: 0 0 var(--spacing-xl) 0;
}

/* 兴趣爱好 */
.interests-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.interest-tag {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-lg);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--color-gray-700);
  border: 1px solid var(--color-gray-300);
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);
  cursor: default;
}

.interest-tag:hover {
  color: var(--color-miku-600);
  border-color: var(--color-miku-400);
  background: var(--color-miku-50);
}

.interest-tag svg {
  color: var(--color-miku-500);
  flex-shrink: 0;
}

/* 时间线 */
.timeline {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2xl);
}

.timeline-item {
  display: flex;
  gap: var(--spacing-xl);
  position: relative;
}

.timeline-item::before {
  content: '';
  position: absolute;
  left: 31px;
  top: 48px;
  bottom: -32px;
  width: 2px;
  background: var(--color-gray-200);
}

.timeline-item:last-child::before {
  display: none;
}

.timeline-marker {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  flex-shrink: 0;
}

.timeline-dot {
  width: 12px;
  height: 12px;
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  border: 3px solid var(--color-white);
  border-radius: 50%;
  box-shadow: 0 0 0 1px var(--color-gray-300);
  position: relative;
  z-index: 1;
}

.timeline-year {
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
  color: var(--color-miku-600);
  white-space: nowrap;
}

.timeline-content {
  flex: 1;
  padding-top: 2px;
}

.timeline-title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
  margin: 0 0 var(--spacing-xs) 0;
}

.timeline-desc {
  font-size: var(--text-base);
  color: var(--color-gray-600);
  line-height: 1.6;
  margin: 0;
}

/* 联系方式 */
.contact-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.contact-link {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md) 0;
  font-size: var(--text-base);
  color: var(--color-gray-700);
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: all var(--transition-fast);
}

.contact-link:hover {
  color: var(--color-miku-600);
  padding-left: var(--spacing-sm);
}

.contact-link svg {
  color: var(--color-miku-500);
  flex-shrink: 0;
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
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(57, 197, 187, 0.3);
  transition: all var(--transition-base);
  z-index: 100;
}

.back-to-top:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(57, 197, 187, 0.4);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-base);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .about__main {
    padding: var(--spacing-xl) var(--spacing-lg);
  }

  .about-header {
    padding: var(--spacing-2xl) 0;
  }

  .about-title {
    font-size: var(--text-2xl);
  }

  .about-subtitle {
    font-size: var(--text-lg);
  }

  .profile-main {
    flex-direction: column;
    text-align: center;
  }

  .profile-avatar {
    width: 80px;
    height: 80px;
  }

  .profile-name {
    font-size: var(--text-2xl);
  }

  .profile-meta {
    justify-content: center;
  }

  .profile-social {
    justify-content: center;
  }

  .profile-stats {
    flex-wrap: wrap;
    justify-content: center;
  }

  .timeline-item {
    gap: var(--spacing-md);
  }

  .timeline-item::before {
    left: 23px;
  }

  .back-to-top {
    bottom: var(--spacing-xl);
    right: var(--spacing-lg);
    width: 44px;
    height: 44px;
  }
}

@media (max-width: 480px) {
  .profile-stats {
    flex-direction: column;
    align-items: center;
  }

  .stat-divider {
    width: 100px;
    height: 1px;
  }
}
</style>
