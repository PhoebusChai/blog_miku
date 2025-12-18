<template>
  <div class="error-page">
    <AppHeader />
    
    <main class="error-main">
      <div class="error-content">
        <div class="error-illustration">
          <div class="error-icon">
            <AlertTriangle :size="100" />
          </div>
        </div>
        
        <h1 class="error-title">出错了</h1>
        <p class="error-description">
          {{ errorMessage || '抱歉，页面加载时遇到了问题' }}
        </p>
      </div>
    </main>
    
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { AlertTriangle } from 'lucide-vue-next'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'

const route = useRoute()
const errorMessage = ref(route.query.message as string || '')
</script>

<style scoped>
.error-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--color-white);
  overflow: hidden;
}

.error-main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 2vw;
  overflow-y: auto;
}

.error-content {
  max-width: 600px;
  text-align: center;
  padding: var(--spacing-3xl);
}

.error-illustration {
  margin-bottom: var(--spacing-3xl);
}

.error-icon {
  display: flex;
  justify-content: center;
  color: var(--color-red-400);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.05);
  }
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
  .error-content {
    padding: var(--spacing-xl);
  }

  .error-icon svg {
    width: 80px;
    height: 80px;
  }

  .error-title {
    font-size: var(--text-2xl);
  }
}
</style>
