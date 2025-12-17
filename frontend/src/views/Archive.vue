<template>
  <div class="archive">
    <AppHeader />
    <main class="archive__main">
      <div class="archive-header">
        <h1 class="archive-title">文章归档</h1>
        <p class="archive-subtitle">按时间顺序浏览所有文章</p>
      </div>

      <div class="archive-content">
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else class="archive-list">
          <!-- 归档列表内容 -->
          <p class="empty">暂无归档内容</p>
        </div>
      </div>
    </main>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'

const loading = ref(false)

onMounted(async () => {
  loading.value = true
  // TODO: 调用 API 获取归档列表
  setTimeout(() => {
    loading.value = false
  }, 1000)
})
</script>

<style scoped>
.archive {
  min-height: 100vh;
  padding-top: 60px;
  overflow-x: hidden;
}

.archive__main {
  width: 100%;
  padding: var(--spacing-3xl) 2vw;
}

.archive-header {
  text-align: center;
  margin-bottom: var(--spacing-3xl);
}

.archive-title {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-md);
}

.archive-subtitle {
  font-size: var(--text-lg);
  color: var(--color-gray-600);
}

.archive-content {
  min-height: 400px;
}

.loading,
.empty {
  text-align: center;
  padding: var(--spacing-3xl);
  color: var(--color-gray-500);
  font-size: var(--text-lg);
}
</style>
