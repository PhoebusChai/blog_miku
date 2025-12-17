<template>
  <div class="tags-cloud">
    <h3 class="tags-cloud__title">热门标签</h3>
    <div class="tags-cloud__list">
      <span
        v-for="tag in tags"
        :key="tag"
        class="tag-item"
        @click="handleTagClick(tag)"
      >
        {{ tag }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 热门标签云组件
 * @description 展示热门标签，支持点击筛选
 */

interface Props {
  /** 标签列表 */
  tags?: string[]
}

interface Emits {
  /** 标签点击事件 */
  (e: 'tag-click', tag: string): void
}

const props = withDefaults(defineProps<Props>(), {
  tags: () => [
    'Vue',
    'TypeScript',
    'CSS',
    'JavaScript',
    'Node.js',
    'React',
    'Vite',
    'Pinia',
    'Web Components',
    'Performance'
  ]
})

const emit = defineEmits<Emits>()

function handleTagClick(tag: string) {
  emit('tag-click', tag)
}
</script>

<style scoped>
.tags-cloud {
  padding: var(--spacing-lg) 0;
}

.tags-cloud__title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin-bottom: var(--spacing-md);
  color: var(--color-gray-900);
}

.tags-cloud__list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-sm);
}

.tag-item {
  font-size: var(--text-sm);
  padding: var(--spacing-xs) var(--spacing-md);
  color: var(--color-miku-500);
  border: 1px solid var(--color-miku-200);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all var(--transition-fast);
  background: transparent;
}

.tag-item:hover {
  background: var(--color-miku-50);
  border-color: var(--color-miku-400);
  transform: translateY(-2px);
}
</style>
