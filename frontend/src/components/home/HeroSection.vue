<template>
  <section class="hero gradient-overlay">
    <div class="hero__content glass-card">
      <h1 class="hero__title">欢迎来到我的博客</h1>
      <p class="hero__subtitle">分享技术、记录生活</p>
      <button class="glass-button hero__button">
        <ArrowDown :size="20" />
        探索更多
      </button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ArrowDown } from 'lucide-vue-next'
</script>

<style scoped>
.hero {
  padding: var(--spacing-3xl) 0;
  text-align: center;
}

.hero__content {
  max-width: 600px;
  margin: 0 auto;
  padding: var(--spacing-3xl) var(--spacing-xl);
}

.hero__title {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--spacing-md);
  background: linear-gradient(135deg, var(--color-pink-500), var(--color-blue-500));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero__subtitle {
  font-size: var(--text-xl);
  color: var(--color-gray-600);
  margin-bottom: var(--spacing-xl);
}

.hero__button {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
}
</style>
