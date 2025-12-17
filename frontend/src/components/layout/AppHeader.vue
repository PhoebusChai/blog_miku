<template>
  <header class="app-header" :class="{ 'app-header--scrolled': isScrolled }">
    <div class="app-header__container">
      <!-- 左侧：Logo + 网站名称 -->
      <RouterLink to="/" class="app-header__brand">
        <div class="brand-icon">
          <Sparkles :size="24" />
        </div>
        <span class="brand-name">落叶无痕</span>
      </RouterLink>

      <!-- 右侧：导航标签页 -->
      <nav class="app-header__nav">
        <RouterLink to="/" class="nav-link">
          <Home :size="18" />
          <span>首页</span>
        </RouterLink>
        <RouterLink to="/archive" class="nav-link">
          <Archive :size="18" />
          <span>归档</span>
        </RouterLink>
        <RouterLink to="/about" class="nav-link">
          <User :size="18" />
          <span>关于</span>
        </RouterLink>
      </nav>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { RouterLink } from 'vue-router'
import { Sparkles, Home, Archive, User } from 'lucide-vue-next'

const isScrolled = ref(false)

function handleScroll() {
  isScrolled.value = window.scrollY > 50
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: var(--z-sticky);
  height: 60px;
  background: transparent;
  border-bottom: 1px solid transparent;
  transition: all var(--transition-base);
}

.app-header--scrolled {
  background: var(--color-white);
  border-bottom-color: var(--color-gray-200);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.app-header__container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 2vw;
}

/* 左侧品牌区 */
.app-header__brand {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  transition: opacity var(--transition-fast);
}

.app-header__brand:hover {
  opacity: 0.8;
}

.brand-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  color: var(--color-miku-400);
  transition: transform var(--transition-fast);
}

.app-header__brand:hover .brand-icon {
  transform: scale(1.1);
}

.brand-name {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  letter-spacing: -0.3px;
}

/* 右侧导航标签页 */
.app-header__nav {
  display: flex;
  gap: var(--spacing-lg);
  align-items: center;
  height: 100%;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-600);
  padding: 0 var(--spacing-xs);
  height: 100%;
  transition: color var(--transition-fast);
  position: relative;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--color-miku-400);
  transform: scaleX(0);
  transform-origin: center;
  transition: transform var(--transition-base);
}

.nav-link:hover {
  color: var(--color-miku-500);
}

.nav-link:hover::after {
  transform: scaleX(1);
}

.nav-link.router-link-active {
  color: var(--color-miku-500);
}

.nav-link.router-link-active::after {
  transform: scaleX(1);
}

@media (max-width: 768px) {
  .app-header {
    height: 56px;
  }

  .app-header__container {
    padding: 0 4vw;
  }

  .brand-icon {
    width: 32px;
    height: 32px;
  }

  .brand-name {
    font-size: var(--text-base);
  }

  .app-header__nav {
    gap: var(--spacing-md);
  }

  .nav-link {
    font-size: var(--text-xs);
  }

  .nav-link span {
    display: none;
  }
}
</style>
