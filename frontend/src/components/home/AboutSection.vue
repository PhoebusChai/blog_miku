<template>
  <div class="about-section">
    <h3 class="about-section__title">关于我</h3>

    <!-- 头像和基本信息 -->
    <div class="about-section__profile">
      <div class="about-section__avatar">
        <img :src="avatarUrl" :alt="name" />
      </div>
      <div class="about-section__info">
        <h4 class="about-section__name">{{ name }}</h4>
        <p class="about-section__role">{{ role }}</p>
      </div>
    </div>

    <!-- 个人简介 -->
    <p class="about-section__bio">{{ bio }}</p>

    <!-- 技能标签 -->
    <div class="about-section__skills">
      <span v-for="skill in skills" :key="skill" class="skill-tag">
        {{ skill }}
      </span>
    </div>

    <!-- 社交链接 -->
    <div class="about-section__social">
      <a href="https://github.com" target="_blank" class="social-link" title="GitHub">
        <svg viewBox="0 0 24 24" fill="currentColor">
          <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
        </svg>
      </a>
      <a href="https://space.bilibili.com" target="_blank" class="social-link" title="哔哩哔哩">
        <svg viewBox="0 0 24 24" fill="currentColor">
          <path d="M17.813 4.653h.854c1.51.054 2.769.578 3.773 1.574 1.004.995 1.524 2.249 1.56 3.76v7.36c-.036 1.51-.556 2.769-1.56 3.773s-2.262 1.524-3.773 1.56H5.333c-1.51-.036-2.769-.556-3.773-1.56S.036 18.858 0 17.347v-7.36c.036-1.511.556-2.765 1.56-3.76 1.004-.996 2.262-1.52 3.773-1.574h.774l-1.174-1.12a1.234 1.234 0 0 1-.373-.906c0-.356.124-.658.373-.907l.027-.027c.267-.249.573-.373.92-.373.347 0 .653.124.92.373L9.653 4.44c.071.071.134.142.187.213h4.267a.836.836 0 0 1 .16-.213l2.853-2.747c.267-.249.573-.373.92-.373.347 0 .662.151.929.4.267.249.391.551.391.907 0 .355-.124.657-.373.906zM5.333 7.24c-.746.018-1.373.276-1.88.773-.506.498-.769 1.13-.786 1.894v7.52c.017.764.28 1.395.786 1.893.507.498 1.134.756 1.88.773h13.334c.746-.017 1.373-.275 1.88-.773.506-.498.769-1.129.786-1.893v-7.52c-.017-.765-.28-1.396-.786-1.894-.507-.497-1.134-.755-1.88-.773zM8 11.107c.373 0 .684.124.933.373.25.249.383.569.4.96v1.173c-.017.391-.15.711-.4.96-.249.25-.56.374-.933.374s-.684-.125-.933-.374c-.25-.249-.383-.569-.4-.96V12.44c0-.373.129-.689.386-.947.258-.257.574-.386.947-.386zm8 0c.373 0 .684.124.933.373.25.249.383.569.4.96v1.173c-.017.391-.15.711-.4.96-.249.25-.56.374-.933.374s-.684-.125-.933-.374c-.25-.249-.383-.569-.4-.96V12.44c.017-.391.15-.711.4-.96.249-.249.56-.373.933-.373Z"/>
        </svg>
      </a>
      <a href="mailto:contact@example.com" class="social-link" title="Email">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
          <polyline points="22,6 12,13 2,6"/>
        </svg>
      </a>
    </div>

    <!-- 统计数据 -->
    <div class="about-section__stats">
      <div v-for="stat in stats" :key="stat.label" class="stat-item">
        <span class="stat-item__value">{{ stat.value }}</span>
        <span class="stat-item__label">{{ stat.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 关于我组件
 * @description 展示个人信息、技能、社交链接和统计数据
 */

interface Stat {
  label: string
  value: string
}

interface Props {
  /** 头像 URL */
  avatarUrl?: string
  /** 姓名 */
  name?: string
  /** 职位 */
  role?: string
  /** 个人简介 */
  bio?: string
  /** 技能列表 */
  skills?: string[]
  /** 统计数据 */
  stats?: Stat[]
}

const props = withDefaults(defineProps<Props>(), {
  avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix',
  name: '落叶无痕',
  role: '全栈开发工程师',
  bio: '热爱技术，喜欢分享。专注于前端开发和用户体验设计，致力于创造优雅的代码和美好的产品。',
  skills: () => ['Vue.js', 'TypeScript', 'Node.js', 'UI/UX'],
  stats: () => [
    { label: '文章', value: '42' },
    { label: '访问', value: '1.2k' },
    { label: '评论', value: '156' }
  ]
})
</script>

<style scoped>
.about-section {
  padding: var(--spacing-xl) 0;
}

.about-section__title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin-bottom: var(--spacing-md);
  color: var(--color-gray-900);
}

.about-section__profile {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.about-section__avatar {
  position: relative;
  width: 64px;
  height: 64px;
  flex-shrink: 0;
}

.about-section__avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--color-white);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform var(--transition-base);
}

.about-section__avatar:hover img {
  transform: scale(1.05);
}

.about-section__info {
  flex: 1;
  min-width: 0;
}

.about-section__name {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0 0 var(--spacing-xs) 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.about-section__role {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.about-section__bio {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  line-height: 1.7;
  margin-bottom: var(--spacing-lg);
}

.about-section__skills {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  margin-bottom: var(--spacing-lg);
}

.skill-tag {
  font-size: var(--text-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  background: linear-gradient(135deg, var(--color-miku-50), var(--color-cyan-50));
  color: var(--color-miku-600);
  border: 1px solid var(--color-miku-200);
  border-radius: var(--radius-md);
  font-weight: var(--font-medium);
  transition: all var(--transition-fast);
  cursor: default;
}

.skill-tag:hover {
  background: linear-gradient(135deg, var(--color-miku-100), var(--color-cyan-100));
  border-color: var(--color-miku-300);
  transform: translateY(-1px);
}

.about-section__social {
  display: flex;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-lg);
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  color: var(--color-gray-600);
  background: var(--color-gray-100);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
  text-decoration: none;
}

.social-link :deep(svg) {
  width: 18px;
  height: 18px;
}

.social-link:hover {
  color: var(--color-white);
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.3);
}

.about-section__stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: linear-gradient(135deg, var(--color-miku-50), var(--color-cyan-50));
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-miku-100);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xs);
}

.stat-item__value {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--color-miku-600);
  line-height: 1;
}

.stat-item__label {
  font-size: var(--text-xs);
  color: var(--color-gray-600);
  font-weight: var(--font-medium);
}
</style>
