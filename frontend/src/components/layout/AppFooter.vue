<template>
  <footer class="app-footer">
    <!-- 顶部分隔线 -->
    <div class="app-footer__divider"></div>
    
    <div class="app-footer__container">
      <!-- 左侧：网站信息 -->
      <div class="app-footer__info">
        <h3 class="footer-title">落叶无痕的博客</h3>
        <p class="footer-description">分享技术、记录生活、探索世界</p>
      </div>

      <!-- 中间：快速链接 -->
      <div class="app-footer__nav">
        <div class="footer-nav-group">
          <h4 class="footer-nav-title">导航</h4>
          <ul class="footer-nav-list">
            <li><a href="/" class="footer-nav-link">首页</a></li>
            <li><a href="/archive" class="footer-nav-link">归档</a></li>
            <li><a href="/projects" class="footer-nav-link">项目</a></li>
            <li><a href="/about" class="footer-nav-link">关于</a></li>
          </ul>
        </div>
        
        <div class="footer-nav-group">
          <h4 class="footer-nav-title">更多</h4>
          <ul class="footer-nav-list">
            <li><a href="/gallery" class="footer-nav-link">相册</a></li>
            <li><a href="/guestbook" class="footer-nav-link">留言板</a></li>
            <li><a href="/archive" class="footer-nav-link">文章归档</a></li>
          </ul>
        </div>
      </div>

      <!-- 右侧：社交链接 -->
      <div class="app-footer__social">
        <h4 class="footer-social-title">关注我</h4>
        <div class="footer-social-links">
          <a href="https://github.com/your-username" target="_blank" rel="noopener noreferrer" class="footer-social-link" aria-label="GitHub">
            <Github :size="20" />
          </a>
          <a href="mailto:your-email@example.com" class="footer-social-link" aria-label="Email">
            <Mail :size="20" />
          </a>
          <a href="https://space.bilibili.com/your-uid" target="_blank" rel="noopener noreferrer" class="footer-social-link" aria-label="Bilibili">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17.813 4.653h.854c1.51.054 2.769.578 3.773 1.574 1.004.995 1.524 2.249 1.56 3.76v7.36c-.036 1.51-.556 2.769-1.56 3.773s-2.262 1.524-3.773 1.56H5.333c-1.51-.036-2.769-.556-3.773-1.56S.036 18.858 0 17.347v-7.36c.036-1.511.556-2.765 1.56-3.76 1.004-.996 2.262-1.52 3.773-1.574h.774l-1.174-1.12a1.234 1.234 0 0 1-.373-.906c0-.356.124-.658.373-.907l.027-.027c.267-.249.573-.373.92-.373.347 0 .653.124.92.373L9.653 4.44c.071.071.134.142.187.213h4.267a.836.836 0 0 1 .16-.213l2.853-2.747c.267-.249.573-.373.92-.373.347 0 .662.151.929.4.267.249.391.551.391.907 0 .355-.124.657-.373.906zM5.333 7.24c-.746.018-1.373.276-1.88.773-.506.498-.769 1.13-.786 1.894v7.52c.017.764.28 1.395.786 1.893.507.498 1.134.756 1.88.773h13.334c.746-.017 1.373-.275 1.88-.773.506-.498.769-1.129.786-1.893v-7.52c-.017-.765-.28-1.396-.786-1.894-.507-.497-1.134-.755-1.88-.773zM8 11.107c.373 0 .684.124.933.373.25.249.383.569.4.96v1.173c-.017.391-.15.711-.4.96-.249.25-.56.374-.933.374s-.684-.125-.933-.374c-.25-.249-.383-.569-.4-.96V12.44c0-.373.129-.689.386-.947.258-.257.574-.386.947-.386zm8 0c.373 0 .684.124.933.373.25.249.383.569.4.96v1.173c-.017.391-.15.711-.4.96-.249.25-.56.374-.933.374s-.684-.125-.933-.374c-.25-.249-.383-.569-.4-.96V12.44c.017-.391.15-.711.4-.96.249-.249.56-.373.933-.373Z"/>
            </svg>
          </a>
        </div>
      </div>
    </div>

    <!-- 底部版权信息 -->
    <div class="app-footer__bottom">
      <div class="footer-bottom-divider"></div>
      <div class="footer-bottom-content">
        <p class="footer-copyright">© 2024 落叶无痕. All rights reserved.</p>
        <div class="footer-meta">
          <span class="footer-meta-item">Made with <Heart :size="14" class="heart-icon" /> by 落叶无痕</span>
          <span class="footer-meta-divider">|</span>
          <span class="footer-meta-item">Powered by Vue 3 + TypeScript</span>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { Github, Mail, Heart } from 'lucide-vue-next'
</script>

<style scoped>
/* 页脚主容器 */
.app-footer {
  margin-top: var(--spacing-3xl);
  background: var(--color-white);
}

/* 顶部分隔线 - 极简设计 */
.app-footer__divider {
  height: 2px;
  background: linear-gradient(90deg, var(--color-pink-500) 0%, var(--color-blue-500) 100%);
  margin-bottom: var(--spacing-2xl);
}

/* 主内容区 */
.app-footer__container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--spacing-2xl) var(--spacing-2xl);
  display: grid;
  grid-template-columns: 2fr 2fr 1fr;
  gap: var(--spacing-3xl);
  position: relative;
}

/* 左侧：网站信息 */
.app-footer__info {
  position: relative;
  padding-left: var(--spacing-md);
}

/* 左侧强调线 - 粉色 */
.app-footer__info::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 3px;
  height: 100%;
  background: var(--color-pink-500);
}

.footer-title {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-sm);
  letter-spacing: -0.02em;
}

.footer-description {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  line-height: 1.6;
  margin: 0;
}

/* 中间：导航链接 */
.app-footer__nav {
  display: flex;
  gap: var(--spacing-3xl);
}

.footer-nav-group {
  flex: 1;
}

.footer-nav-title {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-md);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.footer-nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.footer-nav-link {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  text-decoration: none;
  transition: all var(--transition-fast);
  display: inline-block;
  position: relative;
  padding-left: 0;
}

/* 链接悬停效果 - 使用线条而非背景 */
.footer-nav-link::before {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 0;
  height: 1px;
  background: var(--color-blue-500);
  transition: width var(--transition-base);
}

.footer-nav-link:hover {
  color: var(--color-blue-600);
  padding-left: var(--spacing-sm);
}

.footer-nav-link:hover::before {
  width: 100%;
}

/* 右侧：社交链接 */
.app-footer__social {
  position: relative;
  padding-left: var(--spacing-md);
}

/* 左侧强调线 - 蓝色 */
.app-footer__social::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 3px;
  height: 100%;
  background: var(--color-blue-500);
}

.footer-social-title {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-md);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.footer-social-links {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.footer-social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  color: var(--color-gray-600);
  border: 1px solid var(--color-gray-200);
  transition: all var(--transition-fast);
  position: relative;
  overflow: hidden;
}

/* 社交图标悬停效果 - 线条从底部升起 */
.footer-social-link::before {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 0;
  background: var(--color-pink-50);
  transition: height var(--transition-base);
  z-index: 0;
}

.footer-social-link:hover::before {
  height: 100%;
}

.footer-social-link:hover {
  color: var(--color-pink-600);
  border-color: var(--color-pink-300);
}

.footer-social-link svg {
  position: relative;
  z-index: 1;
  stroke-width: 2;
}

/* 底部版权区 */
.app-footer__bottom {
  margin-top: var(--spacing-xl);
}

.footer-bottom-divider {
  height: 1px;
  background: var(--color-gray-200);
  margin-bottom: var(--spacing-lg);
}

.footer-bottom-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--spacing-2xl) var(--spacing-xl);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-copyright {
  font-size: var(--text-xs);
  color: var(--color-gray-500);
  margin: 0;
}

.footer-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--text-xs);
  color: var(--color-gray-500);
}

.footer-meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.heart-icon {
  color: var(--color-pink-500);
  animation: heartbeat 1.5s ease-in-out infinite;
}

@keyframes heartbeat {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.footer-meta-divider {
  color: var(--color-gray-300);
}

.footer-meta-link {
  color: var(--color-gray-500);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.footer-meta-link:hover {
  color: var(--color-blue-600);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .app-footer__container {
    grid-template-columns: 1fr 1fr;
    gap: var(--spacing-2xl);
  }

  .app-footer__social {
    grid-column: 1 / -1;
  }
}

@media (max-width: 768px) {
  .app-footer__container {
    grid-template-columns: 1fr;
    gap: var(--spacing-xl);
    padding: 0 var(--spacing-lg) var(--spacing-xl);
  }

  .app-footer__nav {
    flex-direction: column;
    gap: var(--spacing-xl);
  }

  .app-footer__social {
    grid-column: 1;
  }

  .footer-bottom-content {
    flex-direction: column;
    gap: var(--spacing-md);
    text-align: center;
    padding: 0 var(--spacing-lg) var(--spacing-lg);
  }

  .footer-meta {
    flex-wrap: wrap;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .app-footer__divider {
    height: 2px;
  }

  .footer-title {
    font-size: var(--text-lg);
  }

  .footer-social-links {
    justify-content: flex-start;
  }

  .footer-social-link {
    width: 36px;
    height: 36px;
  }
}
</style>
