<template>
  <section class="article-list">
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="articles.length === 0" class="empty">暂无文章</div>
    <div v-else class="article-list__items">
      <ArticleCard
        v-for="article in articles"
        :key="article.id"
        :article="article"
        :show-image="!!article.cover"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import ArticleCard from './ArticleCard.vue'
import type { Article } from '@/types/article'

interface Props {
  articles: Article[]
  loading?: boolean
}

defineProps<Props>()
</script>

<style scoped>
.article-list__items {
  display: flex;
  flex-direction: column;
}

.loading,
.empty {
  text-align: center;
  padding: var(--spacing-3xl);
  color: var(--color-gray-500);
  font-size: var(--text-lg);
}
</style>
