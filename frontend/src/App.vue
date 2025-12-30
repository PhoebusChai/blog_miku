<template>
  <div id="app" class="app-container">
    <PageLoading :loading="isLoading" />
    <RouterView v-slot="{ Component }">
      <Transition name="page" mode="out-in">
        <component :is="Component" />
      </Transition>
    </RouterView>
    <Live2DWidget />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { RouterView, useRouter } from 'vue-router'
import Live2DWidget from './components/layout/Live2DWidget.vue'
import PageLoading from './components/common/PageLoading.vue'

const router = useRouter()
const isLoading = ref(false)
const hasLoadedHome = ref(false)

// 监听路由变化 - 仅首页第一次访问显示加载动画
router.beforeEach((to) => {
  if (to.name === 'home' && !hasLoadedHome.value) {
    isLoading.value = true
  }
})

router.afterEach((to) => {
  if (to.name === 'home' && !hasLoadedHome.value) {
    // 给页面一点时间渲染
    setTimeout(() => {
      isLoading.value = false
      hasLoadedHome.value = true
    }, 100)
  }
})
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  background: linear-gradient(135deg, 
    var(--color-pink-50) 0%, 
    var(--color-blue-50) 100%);
}

/* 页面切换动画 */
.page-enter-active,
.page-leave-active {
  transition: opacity 0.3s ease;
}

.page-enter-from,
.page-leave-to {
  opacity: 0;
}
</style>
