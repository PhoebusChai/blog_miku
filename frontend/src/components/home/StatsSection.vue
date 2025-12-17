<template>
  <section class="stats-section">
    <div class="stats-grid">
      <div class="stat-item">
        <div class="stat-number">{{ stats.articles }}</div>
        <div class="stat-label">文章</div>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <div class="stat-number">{{ stats.tags }}</div>
        <div class="stat-label">标签</div>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <div class="stat-number">{{ stats.views }}</div>
        <div class="stat-label">访问</div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const stats = ref({
  articles: 42,
  tags: 12,
  views: '1.2k'
})
</script>

<style scoped>
.stats-section {
  margin: var(--spacing-3xl) 0;
}

.stats-grid {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--spacing-3xl);
  padding: var(--spacing-2xl) 0;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-xs);
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  font-weight: var(--font-medium);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: var(--color-gray-300);
}

@media (max-width: 768px) {
  .stats-grid {
    gap: var(--spacing-xl);
    padding: var(--spacing-xl);
  }

  .stat-number {
    font-size: var(--text-3xl);
  }

  .stat-divider {
    height: 30px;
  }
}
</style>
