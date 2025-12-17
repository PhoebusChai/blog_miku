<template>
  <div class="project-gallery">
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
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Code2, Layout, BarChart3, Terminal, Palette, Database } from 'lucide-vue-next'

interface Project {
  id: number
  title: string
  description: string
  icon: any
  tags: string[]
}

const projects = ref<Project[]>([
  {
    id: 1,
    title: '个人博客系统',
    description: 'Vue 3 + TypeScript 构建的现代化博客',
    icon: Layout,
    tags: ['Vue', 'TypeScript']
  },
  {
    id: 2,
    title: '任务管理工具',
    description: '高效的团队协作平台',
    icon: Code2,
    tags: ['React', 'Node.js']
  },
  {
    id: 3,
    title: '数据可视化看板',
    description: '实时数据分析与展示',
    icon: BarChart3,
    tags: ['D3.js', 'ECharts']
  },
  {
    id: 4,
    title: '在线代码编辑器',
    description: '支持多语言的Web IDE',
    icon: Terminal,
    tags: ['Monaco', 'WebSocket']
  },
  {
    id: 5,
    title: 'UI组件库',
    description: '轻量级的组件库',
    icon: Palette,
    tags: ['Vue', 'CSS']
  },
  {
    id: 6,
    title: 'API管理平台',
    description: 'RESTful API文档与测试',
    icon: Database,
    tags: ['Express', 'Swagger']
  }
])
</script>

<style scoped>
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
}
</style>
