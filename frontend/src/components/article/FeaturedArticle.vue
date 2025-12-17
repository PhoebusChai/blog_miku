<template>
  <article class="featured-article" @click="goToArticle">
    <div class="featured-article__image">
      <img 
        v-if="article.cover" 
        :src="article.cover" 
        :alt="article.title"
        class="article-image"
      />
      <div v-else class="image-placeholder"></div>
    </div>
    <div class="featured-article__content">
      <div class="featured-badge">精选</div>
      <h3 class="featured-article__title">{{ article.title }}</h3>
      <p class="featured-article__summary">{{ article.summary }}</p>
      <div class="featured-article__meta">
        <span class="featured-article__date">{{ formatDate(article.createdAt) }}</span>
        <div class="featured-article__stats">
          <span class="stat-item">
            <Eye :size="14" />
            {{ article.views }}
          </span>
          <span class="stat-item">
            <Heart :size="14" />
            {{ article.likes }}
          </span>
        </div>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Eye, Heart } from 'lucide-vue-next'
import type { Article } from '@/types/article'

interface Props {
  article: Article
}

const props = defineProps<Props>()
const router = useRouter()

function goToArticle() {
  router.push(`/article/${props.article.id}`)
}

function formatDate(date: Date) {
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>

<style scoped>
.featured-article {
  cursor: pointer;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--color-white);
  transition: all var(--transition-base);
  border: 1px solid var(--color-gray-200);
  position: relative;
}

.featured-article::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 3px;
  height: 0;
  background: var(--color-miku-400);
  transition: height var(--transition-base);
}

.featured-article:hover::before {
  height: 100%;
}

.featured-article:hover {
  border-color: var(--color-gray-300);
}

.featured-article__image {
  width: 100%;
  height: 240px;
  overflow: hidden;
  position: relative;
}

.article-image,
.image-placeholder {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.image-placeholder {
  background: linear-gradient(135deg, var(--color-miku-100), var(--color-cyan-100));
}

.featured-article:hover .article-image,
.featured-article:hover .image-placeholder {
  transform: scale(1.08);
}

.featured-article__content {
  padding: var(--spacing-xl);
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.featured-badge {
  display: inline-block;
  width: fit-content;
  font-size: var(--text-xs);
  padding: var(--spacing-xs) var(--spacing-md);
  color: var(--color-miku-500);
  border: 1px solid var(--color-miku-400);
  font-weight: var(--font-semibold);
  letter-spacing: 0.5px;
}

.featured-article__title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  line-height: 1.3;
  transition: color var(--transition-fast);
}

.featured-article:hover .featured-article__title {
  color: var(--color-cyan-500);
}

.featured-article__summary {
  font-size: var(--text-base);
  color: var(--color-gray-600);
  line-height: 1.7;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.featured-article__meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.featured-article__date {
  font-size: var(--text-sm);
  color: var(--color-gray-500);
  font-weight: var(--font-medium);
}

.featured-article__stats {
  display: flex;
  gap: var(--spacing-md);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--text-sm);
  color: var(--color-gray-500);
}

.stat-item svg {
  color: var(--color-miku-400);
}

@media (max-width: 768px) {
  .featured-article__image {
    height: 180px;
  }

  .featured-article__title {
    font-size: var(--text-xl);
  }
}
</style>
