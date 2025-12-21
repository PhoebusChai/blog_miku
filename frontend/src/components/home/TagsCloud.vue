<template>
  <div class="tags-cloud">
    <h3 class="tags-cloud__title">热门标签</h3>
    
    <!-- 空状态 -->
    <div v-if="tags.length === 0" class="tags-empty">
      <div class="tags-empty-icon">
        <Tag :size="28" />
      </div>
      <p class="tags-empty-text">暂无标签</p>
    </div>

    <!-- 标签列表 -->
    <div v-else class="tags-cloud__list">
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
import { Tag } from 'lucide-vue-next'

interface Props {
  /** 标签列表 */
  tags?: string[]
}

interface Emits {
  /** 标签点击事件 */
  (e: 'tag-click', tag: string): void
}

const props = withDefaults(defineProps<Props>(), {
  tags: () => []
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

/* 空状态 */
.tags-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-2xl) var(--spacing-lg);
  border-radius: var(--radius-md);
  text-align: center;
  min-height: 120px;
}

.tags-empty-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, rgba(57, 197, 187, 0.1), rgba(57, 197, 187, 0.05));
  border-radius: 50%;
  margin-bottom: var(--spacing-md);
}

.tags-empty-icon svg {
  color: var(--color-miku-400);
  opacity: 0.8;
}

.tags-empty-text {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-500);
  margin: 0;
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
