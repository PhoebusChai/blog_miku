<template>
  <div class="project-gallery-wrapper">
    <!-- 空状态 -->
    <div v-if="projects.length === 0" class="project-empty">
      <div class="project-empty-icon">
        <FolderOpen :size="48" />
      </div>
      <p class="project-empty-text">暂无项目</p>
      <p class="project-empty-hint">敬请期待精彩项目 (๑•̀ㅂ•́)و✧</p>
    </div>

    <!-- 项目列表 -->
    <div v-else class="project-gallery">
      <div class="project-card" v-for="project in projects" :key="project.id">
        <div class="project-icon">
          <component :is="project.icon" :size="32" />
        </div>
        <div class="project-content">
          <h4 class="project-title">{{ project.title }}</h4>
          <p class="project-desc">{{ project.description }}</p>
          <div class="project-tags">
            <span v-for="tag in project.tags" :key="tag" class="project-tag">{{ tag }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { FolderOpen } from 'lucide-vue-next'

interface Project {
  id: number
  title: string
  description: string
  icon: any
  tags: string[]
}

interface Props {
  projects?: Project[]
}

const props = withDefaults(defineProps<Props>(), {
  projects: () => []
})

const projects = ref<Project[]>(props.projects)
</script>

<style scoped>
.project-gallery-wrapper {
  width: 100%;
}

/* 空状态 */
.project-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl) var(--spacing-2xl);
  min-height: 280px;
  border-radius: var(--radius-lg);
  text-align: center;
}

.project-empty-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  background: linear-gradient(135deg, rgba(57, 197, 187, 0.1), rgba(57, 197, 187, 0.05));
  border-radius: 50%;
  margin-bottom: var(--spacing-lg);
}

.project-empty-icon svg {
  color: var(--color-miku-400);
  opacity: 0.8;
}

.project-empty-text {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--color-gray-600);
  margin: 0 0 var(--spacing-xs) 0;
}

.project-empty-hint {
  font-size: var(--text-sm);
  color: var(--color-gray-400);
  margin: 0;
}

.project-gallery {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-lg);
}

.project-card {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
  background: var(--color-white);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-gray-200);
  cursor: pointer;
  transition: all var(--transition-base);
}

.project-card:hover {
  border-color: var(--color-gray-300);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.project-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  flex-shrink: 0;
  background: var(--color-gray-50);
  border-radius: var(--radius-md);
  color: var(--color-gray-600);
  transition: all var(--transition-base);
}

.project-card:hover .project-icon {
  background: var(--color-miku-50);
  color: var(--color-miku-500);
}

.project-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  flex: 1;
  min-width: 0;
}

.project-title {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0;
}

.project-desc {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  line-height: 1.6;
  margin: 0;
}

.project-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-xs);
}

.project-tag {
  font-size: var(--text-xs);
  padding: 4px var(--spacing-sm);
  background: var(--color-gray-100);
  color: var(--color-gray-600);
  border-radius: var(--radius-sm);
}

@media (max-width: 1024px) {
  .project-gallery {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .project-gallery {
    grid-template-columns: 1fr;
    gap: var(--spacing-md);
  }
  
  .project-card {
    padding: var(--spacing-lg);
  }
  
  .project-icon {
    width: 48px;
    height: 48px;
  }
  
  .project-icon svg {
    width: 24px;
    height: 24px;
  }
}

@media (max-width: 480px) {
  .project-gallery {
    grid-template-columns: 1fr;
  }
  
  .project-card {
    flex-direction: column;
  }

  .project-empty {
    padding: var(--spacing-2xl) var(--spacing-lg);
    min-height: 220px;
  }

  .project-empty-icon {
    width: 56px;
    height: 56px;
  }

  .project-empty-icon svg {
    width: 32px;
    height: 32px;
  }
}
</style>
