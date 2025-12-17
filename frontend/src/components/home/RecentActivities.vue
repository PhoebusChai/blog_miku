<template>
  <div class="recent-activities">
    <h3 class="recent-activities__title">最新动态</h3>
    <div class="recent-activities__list">
      <div
        v-for="(activity, index) in activities"
        :key="index"
        class="activity-item"
        @click="handleActivityClick(activity)"
      >
        <div :class="['activity-item__icon', `activity-item__icon--${activity.type}`]">
          <component :is="getActivityIcon(activity.type)" />
        </div>
        <div class="activity-item__content">
          <p class="activity-item__text">{{ activity.text }}</p>
          <p class="activity-item__time">{{ activity.time }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 最新动态组件
 * @description 展示博主的最新活动时间线
 */
import { FileText, Code, Star } from 'lucide-vue-next'
import { h } from 'vue'

type ActivityType = 'article' | 'code' | 'star'

interface Activity {
  type: ActivityType
  text: string
  time: string
  url?: string
}

interface Props {
  /** 活动列表 */
  activities?: Activity[]
}

interface Emits {
  /** 活动点击事件 */
  (e: 'activity-click', activity: Activity): void
}

const props = withDefaults(defineProps<Props>(), {
  activities: () => [
    {
      type: 'article',
      text: '发布了新文章《Vue 3 性能优化实践》',
      time: '2 小时前'
    },
    {
      type: 'code',
      text: '更新了项目 personal-blog 的代码',
      time: '1 天前'
    },
    {
      type: 'star',
      text: '收藏了文章《TypeScript 类型体操》',
      time: '3 天前'
    }
  ]
})

const emit = defineEmits<Emits>()

function getActivityIcon(type: ActivityType) {
  const iconMap = {
    article: FileText,
    code: Code,
    star: Star
  }
  return iconMap[type]
}

function handleActivityClick(activity: Activity) {
  emit('activity-click', activity)
}
</script>

<style scoped>
.recent-activities {
  padding: var(--spacing-lg) 0;
}

.recent-activities__title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin-bottom: var(--spacing-md);
  color: var(--color-gray-900);
}

.recent-activities__list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.activity-item {
  display: flex;
  gap: var(--spacing-md);
  position: relative;
  padding: var(--spacing-sm);
  margin: calc(var(--spacing-xs) * -1);
  border-radius: var(--radius-md);
  transition: all var(--transition-base);
  cursor: pointer;
}

.activity-item:hover {
  background: var(--color-gray-50);
  transform: translateX(2px);
}

.activity-item:not(:last-child)::after {
  content: '';
  position: absolute;
  left: calc(var(--spacing-sm) + 15px);
  top: calc(var(--spacing-sm) + 32px);
  bottom: calc(var(--spacing-md) * -1);
  width: 2px;
  background: linear-gradient(180deg, var(--color-gray-200), transparent);
}

.activity-item__icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
  z-index: 1;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all var(--transition-base);
}

.activity-item:hover .activity-item__icon {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.activity-item__icon :deep(svg) {
  width: 16px;
  height: 16px;
  color: var(--color-white);
}

.activity-item__icon--article {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.activity-item__icon--code {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
}

.activity-item__icon--star {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.activity-item__content {
  flex: 1;
  min-width: 0;
  padding-top: 2px;
}

.activity-item__text {
  font-size: var(--text-sm);
  color: var(--color-gray-700);
  line-height: 1.6;
  margin-bottom: var(--spacing-xs);
  transition: color var(--transition-fast);
}

.activity-item:hover .activity-item__text {
  color: var(--color-gray-900);
}

.activity-item__time {
  font-size: var(--text-xs);
  color: var(--color-gray-500);
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.activity-item__time::before {
  content: '';
  width: 4px;
  height: 4px;
  background: var(--color-gray-400);
  border-radius: 50%;
}
</style>
