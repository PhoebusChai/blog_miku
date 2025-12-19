<template>
  <div class="placeholder-view">
    <component :is="icon" :size="48" class="placeholder-icon" />
    <h3>{{ title }}</h3>
    <p>{{ subtitle }}</p>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'

interface Props {
  icon: Component
  title: string
  subtitle?: string
}

withDefaults(defineProps<Props>(), {
  subtitle: '功能开发中...'
})
</script>

<style scoped>
.placeholder-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  text-align: center;
  color: var(--color-gray-500);
}

.placeholder-icon {
  margin-bottom: var(--spacing-lg);
  opacity: 0.5;
}

.placeholder-view h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  margin: 0 0 var(--spacing-sm) 0;
}

.placeholder-view p {
  font-size: var(--text-sm);
  margin: 0;
}
</style>
