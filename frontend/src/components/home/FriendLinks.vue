<template>
  <div class="friend-links">
    <h3 class="friend-links__title">友情链接</h3>
    
    <!-- 空状态 -->
    <div v-if="links.length === 0" class="links-empty">
      <div class="links-empty-icon">
        <Link2 :size="28" />
      </div>
      <p class="links-empty-text">暂无链接</p>
    </div>

    <!-- 链接列表 -->
    <div v-else class="friend-links__list">
      <a
        v-for="link in links"
        :key="link.name"
        :href="link.url"
        target="_blank"
        class="friend-link"
      >
        <span class="friend-link__dot"></span>
        <span class="friend-link__text">{{ link.name }}</span>
        <svg
          class="friend-link__arrow"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <path d="M7 17L17 7M17 7H7M17 7V17" />
        </svg>
      </a>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 友情链接组件
 * @description 展示友情链接列表
 */
import { Link2 } from 'lucide-vue-next'

interface FriendLink {
  name: string
  url: string
}

interface Props {
  /** 链接列表 */
  links?: FriendLink[]
}

const props = withDefaults(defineProps<Props>(), {
  links: () => []
})
</script>

<style scoped>
.friend-links {
  padding: var(--spacing-lg) 0;
}

.friend-links__title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin-bottom: var(--spacing-md);
  color: var(--color-gray-900);
}

/* 空状态 */
.links-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl) var(--spacing-lg);
  border-radius: var(--radius-md);
  text-align: center;
  min-height: 120px;
}

.links-empty-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, rgba(57, 197, 187, 0.1), rgba(57, 197, 187, 0.05));
  border-radius: 50%;
  margin-bottom: var(--spacing-md);
}

.links-empty-icon svg {
  color: var(--color-miku-400);
  opacity: 0.8;
}

.links-empty-text {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-500);
  margin: 0;
}

.friend-links__list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.friend-link {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) 0;
  text-decoration: none;
  transition: all var(--transition-fast);
  position: relative;
  border-bottom: 1px solid transparent;
}

.friend-link:not(:last-child) {
  border-bottom-color: var(--color-gray-100);
}

.friend-link__dot {
  width: 6px;
  height: 6px;
  background: var(--color-gray-400);
  border-radius: 50%;
  flex-shrink: 0;
  transition: all var(--transition-fast);
}

.friend-link__text {
  flex: 1;
  font-size: var(--text-sm);
  color: var(--color-gray-700);
  transition: all var(--transition-fast);
}

.friend-link__arrow {
  width: 14px;
  height: 14px;
  color: var(--color-gray-400);
  opacity: 0;
  transform: translateX(-4px);
  transition: all var(--transition-fast);
  flex-shrink: 0;
}

.friend-link:hover {
  padding-left: var(--spacing-xs);
}

.friend-link:hover .friend-link__dot {
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  transform: scale(1.3);
}

.friend-link:hover .friend-link__text {
  color: var(--color-miku-600);
  font-weight: var(--font-medium);
}

.friend-link:hover .friend-link__arrow {
  opacity: 1;
  transform: translateX(0);
  color: var(--color-miku-500);
}
</style>
