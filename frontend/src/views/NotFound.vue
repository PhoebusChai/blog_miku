<template>
  <div class="not-found-page">
    <AppHeader />
    
    <main class="not-found-main">
      <div class="not-found-content">
        <div class="error-illustration">
          <div class="error-code">404</div>
          <div class="error-icon">
            <SearchX :size="80" />
          </div>
        </div>
        
        <h1 class="error-title">页面未找到</h1>
        <p class="error-description">
          抱歉，您访问的页面不存在或已被移除
        </p>
      </div>
    </main>
    
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { SearchX } from 'lucide-vue-next'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
</script>

<style scoped>
.not-found-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--color-white);
  overflow: hidden;
}

.not-found-main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 2vw;
  overflow-y: auto;
}

.not-found-content {
  max-width: 600px;
  text-align: center;
  padding: var(--spacing-3xl);
}

.error-illustration {
  position: relative;
  margin-bottom: var(--spacing-3xl);
}

.error-code {
  font-size: 120px;
  font-weight: var(--font-bold);
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  line-height: 1;
  margin-bottom: var(--spacing-lg);
  letter-spacing: -0.05em;
}

.error-icon {
  display: flex;
  justify-content: center;
  color: var(--color-miku-300);
  opacity: 0.6;
}

.error-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0 0 var(--spacing-md) 0;
}

.error-description {
  font-size: var(--text-base);
  color: var(--color-gray-600);
  line-height: 1.6;
  margin: 0;
}

@media (max-width: 768px) {
  .not-found-main {
    padding: 70px 4vw 30px;
  }

  .not-found-content {
    padding: var(--spacing-xl);
  }

  .error-code {
    font-size: 80px;
  }

  .error-icon svg {
    width: 60px;
    height: 60px;
  }

  .error-title {
    font-size: var(--text-2xl);
  }
}
</style>
