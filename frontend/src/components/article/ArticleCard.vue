<template>
  <article class="article-card" :class="{ 'article-card--with-image': showImage && article.cover }" @click="goToArticle">
    <!-- 图片模式 -->
    <div v-if="showImage && article.cover" class="article-card__image">
      <img :src="article.cover" :alt="article.title" />
    </div>
    
    <div class="article-card__content">
      <h3 class="article-card__title">{{ article.title }}</h3>
      <p class="article-card__summary">{{ article.summary }}</p>
      <div class="article-card__footer">
        <div class="article-card__meta">
          <span class="meta-item">
            <User :size="14" />
            {{ article.author }}
          </span>
          <span class="meta-item">
            <Calendar :size="14" />
            {{ formatDate(article.createdAt) }}
          </span>
          <span class="meta-item">
            <Eye :size="14" />
            {{ article.views }}
          </span>
          <span class="meta-item">
            <Heart :size="14" />
            {{ article.likes }}
          </span>
        </div>
        <div class="article-card__tags">
          <span v-for="tag in article.tags" :key="tag" class="tag">
            {{ tag }}
          </span>
        </div>
      </div>
    </div>
    <div class="article-card__divider"></div>
  </article>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { User, Calendar, Eye, Heart } from 'lucide-vue-next'
import type { Article } from '@/types/article'

interface Props {
  article: Article
  showImage?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showImage: false
})

const router = useRouter()

function goToArticle() {
  router.push(`/article/${props.article.id}`)
}

function formatDate(date: Date) {
  return new Date(date).toLocaleDateString('zh-CN', {
    month: 'long',
    day: 'numeric'
  })
}
</script>

<style scoped>
.article-card {
  padding: var(--spacing-2xl) 0 var(--spacing-2xl) var(--spacing-md);
  cursor: pointer;
  position: relative;
  transition: all var(--transition-base);
  display: flex;
  gap: var(--spacing-xl);
}

.article-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 2px;
  height: 0;
  background: var(--color-miku-400);
  transition: height var(--transition-base);
}

.article-card:hover::before {
  height: 100%;
}

.article-card:hover .article-card__title {
  color: var(--color-cyan-500);
}

/* 图片模式 */
.article-card--with-image {
  flex-direction: row;
}

.article-card__image {
  width: 200px;
  height: 140px;
  flex-shrink: 0;
  overflow: hidden;
  border-radius: var(--radius-sm);
}

.article-card__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.article-card:hover .article-card__image img {
  transform: scale(1.1);
}

.article-card__content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  flex: 1;
  min-width: 0;
}

.article-card__title {
  font-size: var(--text-2xl);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
  line-height: 1.4;
  transition: color var(--transition-fast);
}

.article-card__summary {
  color: var(--color-gray-600);
  font-size: var(--text-base);
  line-height: 1.8;
  margin-bottom: var(--spacing-sm);
}

.article-card__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-md);
  margin-top: auto;
}

.article-card__meta {
  display: flex;
  gap: var(--spacing-lg);
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--text-sm);
  color: var(--color-gray-500);
  font-weight: var(--font-medium);
}

.meta-item svg {
  color: var(--color-miku-400);
  flex-shrink: 0;
}

.article-card__tags {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.tag {
  font-size: var(--text-xs);
  padding: var(--spacing-xs) var(--spacing-md);
  color: var(--color-miku-500);
  border: 1px solid var(--color-miku-200);
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);
}

.tag:hover {
  background: var(--color-miku-50);
  border-color: var(--color-miku-400);
}

.article-card__divider {
  height: 1px;
  background: var(--color-gray-200);
  margin-top: var(--spacing-2xl);
}

@media (max-width: 768px) {
  .article-card {
    padding: var(--spacing-lg) 0;
    flex-direction: column;
  }

  .article-card__image {
    width: 100%;
    height: 180px;
  }

  .article-card__title {
    font-size: var(--text-xl);
  }

  .article-card__meta {
    gap: var(--spacing-md);
  }

  .meta-item {
    font-size: var(--text-xs);
  }

  .article-card__footer {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
