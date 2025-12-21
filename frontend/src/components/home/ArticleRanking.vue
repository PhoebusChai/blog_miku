<template>
  <div class="article-ranking">
    <h3 class="article-ranking__title">文章排行</h3>
    
    <!-- 空状态 -->
    <div v-if="articles.length === 0" class="ranking-empty">
      <div class="ranking-empty-icon">
        <TrendingUp :size="32" />
      </div>
      <p class="ranking-empty-text">暂无排行数据</p>
    </div>
    
    <!-- 文章列表 -->
    <div v-else class="article-ranking__list">
      <a
        v-for="(article, index) in articles"
        :key="article.id"
        :href="article.url || `#article-${article.id}`"
        class="ranking-item"
      >
        <span
          :class="[
            'ranking-item__number',
            { 'ranking-item__number--top': index < 3 }
          ]"
        >
          {{ index + 1 }}
        </span>
        <div class="ranking-item__content">
          <p class="ranking-item__title">{{ article.title }}</p>
          <div class="ranking-item__meta">
            <span class="ranking-item__views">
              <Eye :size="14" />
              {{ article.views }}
            </span>
          </div>
        </div>
      </a>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 文章排行榜组件
 * @description 展示热门文章排行
 */
import { Eye, TrendingUp } from 'lucide-vue-next'

interface RankingArticle {
  id: number
  title: string
  views: string | number
  url?: string
}

interface Props {
  /** 文章列表 */
  articles?: RankingArticle[]
}

const props = withDefaults(defineProps<Props>(), {
  articles: () => []
})
</script>

<style scoped>
.article-ranking {
  padding: var(--spacing-lg) 0;
}

.article-ranking__title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin-bottom: var(--spacing-md);
  color: var(--color-gray-900);
}

/* 空状态 */
.ranking-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-2xl) var(--spacing-lg);
  text-align: center;
  border-radius: var(--radius-md);
  min-height: 140px;
}

.ranking-empty-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, rgba(57, 197, 187, 0.1), rgba(57, 197, 187, 0.05));
  border-radius: 50%;
  margin-bottom: var(--spacing-md);
}

.ranking-empty-icon svg {
  color: var(--color-miku-400);
  opacity: 0.8;
}

.ranking-empty-text {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-500);
  margin: 0;
}

.article-ranking__list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.ranking-item {
  display: flex;
  gap: var(--spacing-md);
  text-decoration: none;
  padding: var(--spacing-sm);
  margin: calc(var(--spacing-xs) * -1);
  border-radius: var(--radius-md);
  transition: all var(--transition-base);
}

.ranking-item:hover {
  background: var(--color-gray-50);
  transform: translateX(2px);
}

.ranking-item__number {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
  color: var(--color-gray-500);
  background: var(--color-gray-100);
  border-radius: var(--radius-sm);
  flex-shrink: 0;
  transition: all var(--transition-fast);
}

.ranking-item__number--top {
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  color: var(--color-white);
  box-shadow: 0 2px 8px rgba(57, 197, 187, 0.3);
}

.ranking-item:hover .ranking-item__number {
  transform: scale(1.1);
}

.ranking-item:hover .ranking-item__number--top {
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.4);
}

.ranking-item__content {
  flex: 1;
  min-width: 0;
}

.ranking-item__title {
  font-size: var(--text-sm);
  color: var(--color-gray-800);
  line-height: 1.5;
  margin-bottom: var(--spacing-xs);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color var(--transition-fast);
}

.ranking-item:hover .ranking-item__title {
  color: var(--color-miku-600);
}

.ranking-item__meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.ranking-item__views {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--text-xs);
  color: var(--color-gray-500);
}

@media (max-width: 768px) {
  .ranking-empty {
    padding: var(--spacing-lg) var(--spacing-md);
    min-height: 120px;
  }

  .ranking-empty-icon {
    width: 40px;
    height: 40px;
  }

  .ranking-empty-icon svg {
    width: 20px;
    height: 20px;
  }

  .ranking-empty-text {
    font-size: var(--text-xs);
  }
}
</style>
