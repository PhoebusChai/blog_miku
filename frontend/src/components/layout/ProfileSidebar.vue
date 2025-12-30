<template>
  <aside class="profile-sidebar">
    <div class="user-card">
      <div class="avatar-wrapper">
        <img :src="user?.avatar" :alt="user?.name" class="user-avatar" />
        <button class="avatar-edit" @click="$emit('update-avatar')">
          <Camera :size="14" />
        </button>
      </div>
      <div class="user-info">
        <h2 class="user-name">{{ user?.name }}</h2>
        <p class="user-email">{{ user?.email }}</p>
        <span v-if="user?.role === 1" class="user-badge">
          <Shield :size="12" />
          管理员
        </span>
      </div>
    </div>

    <nav class="profile-nav">
      <button 
        v-for="item in menuItems" 
        :key="item.id"
        :class="['nav-item', { 'nav-item--active': modelValue === item.id }]"
        @click="$emit('update:modelValue', item.id)"
      >
        <component :is="item.icon" :size="18" />
        <span>{{ item.label }}</span>
      </button>
    </nav>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { Camera, Shield, Settings, Activity, BookOpen, Heart, BookMarked } from 'lucide-vue-next'

defineProps<{
  modelValue: string
}>()

defineEmits<{
  'update:modelValue': [value: string]
  'update-avatar': []
}>()

const userStore = useUserStore()
const user = computed(() => userStore.user)

const menuItems = [
  { id: 'account', label: '账户设置', icon: Settings },
  { id: 'activity', label: '我的活动', icon: Activity },
  { id: 'reading', label: '阅读记录', icon: BookOpen },
  { id: 'likes', label: '我的点赞', icon: Heart },
  { id: 'bookmarks', label: '我的收藏', icon: BookMarked }
]
</script>

<style scoped>
.profile-sidebar {
  position: sticky;
  top: 80px;
  background: transparent;
  padding: 0;
}

.user-card {
  background: transparent;
  padding: 0 0 var(--spacing-xl) 0;
  margin-bottom: var(--spacing-xl);
  border-bottom: none;
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  text-align: left;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.avatar-edit {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-miku-500);
  border: 2px solid var(--color-white);
  border-radius: 50%;
  color: var(--color-white);
  cursor: pointer;
  transition: all var(--transition-fast);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.avatar-edit:hover {
  background: var(--color-miku-600);
  transform: scale(1.1);
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 2px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.user-email {
  font-size: 12px;
  color: #8c8c8c;
  margin: 0 0 6px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.user-badge {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 2px 6px;
  background: linear-gradient(135deg, var(--color-miku-50) 0%, var(--color-miku-100) 100%);
  color: var(--color-miku-600);
  border: none;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.profile-nav {
  background: transparent;
  padding-top: var(--spacing-md);
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 12px;
  margin-bottom: 4px;
  font-size: 14px;
  font-weight: 400;
  color: #595959;
  background: transparent;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
}

.nav-item:hover {
  background: rgba(0, 0, 0, 0.03);
  color: #262626;
}

.nav-item--active {
  background: linear-gradient(135deg, rgba(57, 197, 187, 0.08) 0%, rgba(57, 197, 187, 0.12) 100%);
  color: var(--color-miku-600);
  font-weight: 500;
}

/* 响应式 */
@media (max-width: 1024px) {
  .profile-sidebar {
    position: static;
  }

  .profile-nav {
    display: flex;
    overflow-x: auto;
    border-bottom: 1px solid var(--color-gray-200);
  }

  .nav-item {
    padding: var(--spacing-md) var(--spacing-lg);
    border-bottom: 2px solid transparent;
    white-space: nowrap;
  }

  .nav-item--active {
    border-bottom-color: var(--color-miku-500);
  }
}
</style>
