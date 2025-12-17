<template>
  <div class="friend-links">
    <h3 class="friend-links__title">友情链接</h3>
    <div class="friend-links__list">
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

interface FriendLink {
  name: string
  url: string
}

interface Props {
  /** 链接列表 */
  links?: FriendLink[]
}

const props = withDefaults(defineProps<Props>(), {
  links: () => [
    { name: 'Vue.js 官网', url: 'https://vuejs.org' },
    { name: 'TypeScript 文档', url: 'https://www.typescriptlang.org' },
    { name: 'MDN Web Docs', url: 'https://developer.mozilla.org' },
    { name: 'GitHub', url: 'https://github.com' }
  ]
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
