<template>
  <div class="about">
    <AppHeader />
    <main class="about__main">
      <div class="glass-card about-content">
        <h1 class="about-content__title">关于我</h1>
        <p class="about-content__text">
          这是一个个人博客项目，用于分享技术文章、生活感悟和个人作品。
        </p>
      </div>
    </main>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
</script>

<style scoped>
.about__main {
  width: 100%;
  padding: var(--spacing-2xl) 2vw;
}

.about-content {
  padding: var(--spacing-2xl);
  max-width: 800px;
}

.about-content__title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--spacing-lg);
}

.about-content__text {
  line-height: 1.8;
  color: var(--color-gray-700);
}
</style>
