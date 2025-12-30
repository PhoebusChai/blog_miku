<template>
  <Transition name="fade">
    <div v-if="visible" class="loading-overlay">
      <div class="loading-container">
        <!-- Icon Loading 动画 -->
        <div class="loading-icon">
          <Music :size="48" class="icon-spin" />
        </div>
        
        <p class="loading-subtitle">{{ loadingText }}</p>
        
        <!-- 简约电池格子进度条 -->
        <div class="battery-bar">
          <div 
            v-for="i in 10" 
            :key="i" 
            class="battery-cell"
            :class="{ 'battery-cell--active': progress >= i * 10 }"
          ></div>
        </div>
        
        <p class="loading-text">{{ progress }}%</p>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { ref, watch, onUnmounted } from 'vue'
import { Music } from 'lucide-vue-next'

const props = defineProps<{
  loading: boolean
}>()

const emit = defineEmits<{
  complete: []
}>()

const visible = ref(false)
const progress = ref(0)
const loadingText = ref('初始化中...')

// 加载文字列表
const loadingMessages = [
  '(๑•̀ㅂ•́)و✧ 系统启动中...',
  '(｡･ω･｡) 召唤初音中...',
  '(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ 加载葱葱...',
  '(๑˃ᴗ˂)ﻭ 准备唱歌...',
  '(★ ω ★) 调试音符...',
  '(ﾉ≧∀≦)ﾉ 马上好了...',
  '(๑>ᴗ<๑) 完成啦！'
]

let progressInterval: number | null = null

function startProgress() {
  progress.value = 0
  visible.value = true
  
  // 快速增长到 90%，然后等待真实加载完成
  const fastPhase = setInterval(() => {
    if (progress.value < 90) {
      progress.value += Math.random() * 15 + 5
      progress.value = Math.min(progress.value, 90)
      
      const messageIndex = Math.floor((progress.value / 100) * (loadingMessages.length - 1))
      loadingText.value = loadingMessages[messageIndex]
    }
  }, 100)
  
  progressInterval = fastPhase as unknown as number
}

function completeProgress() {
  if (progressInterval) {
    clearInterval(progressInterval)
    progressInterval = null
  }
  
  // 快速完成到 100%
  const completeInterval = setInterval(() => {
    progress.value += 5
    if (progress.value >= 100) {
      progress.value = 100
      loadingText.value = loadingMessages[loadingMessages.length - 1]
      clearInterval(completeInterval)
      
      // 短暂延迟后隐藏
      setTimeout(() => {
        visible.value = false
        emit('complete')
      }, 200)
    }
  }, 30)
}

watch(() => props.loading, (newVal, oldVal) => {
  if (newVal && !oldVal) {
    // 开始加载
    startProgress()
  } else if (!newVal && oldVal) {
    // 加载完成
    completeProgress()
  }
}, { immediate: true })

onUnmounted(() => {
  if (progressInterval) {
    clearInterval(progressInterval)
  }
})
</script>

<style scoped>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f9f7 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xl);
  animation: fadeInScale 0.6s ease-out;
}

.loading-icon {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  border-radius: 50%;
  box-shadow: 0 8px 24px rgba(57, 197, 187, 0.3);
  animation: iconPulse 2s ease-in-out infinite;
}

.icon-spin {
  color: var(--color-white);
  animation: spin 2s linear infinite;
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 8px 24px rgba(57, 197, 187, 0.3);
  }
  50% {
    transform: scale(1.1);
    box-shadow: 0 12px 32px rgba(57, 197, 187, 0.5);
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loading-subtitle {
  font-size: var(--text-lg);
  color: var(--color-miku-500);
  font-weight: var(--font-semibold);
  letter-spacing: 0.5px;
  text-align: center;
  min-height: 28px;
}

.battery-bar {
  display: flex;
  gap: 4px;
  padding: 4px;
  background: transparent;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-gray-300);
}

.battery-cell {
  width: 20px;
  height: 28px;
  background: var(--color-gray-200);
  border-radius: 2px;
  transition: all 0.3s ease-out;
}

.battery-cell--active {
  background: linear-gradient(180deg, var(--color-miku-400), var(--color-cyan-500));
  box-shadow: 0 0 6px rgba(57, 197, 187, 0.4);
}

.loading-text {
  font-size: var(--text-base);
  color: var(--color-gray-600);
  font-weight: var(--font-medium);
  letter-spacing: 1px;
  font-family: 'Courier New', monospace;
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 640px) {
  .loading-icon {
    width: 64px;
    height: 64px;
  }
  
  .loading-icon svg {
    width: 36px;
    height: 36px;
  }
  
  .loading-subtitle {
    font-size: var(--text-base);
  }
  
  .battery-bar {
    gap: 3px;
    padding: 3px;
  }
  
  .battery-cell {
    width: 16px;
    height: 24px;
  }
  
  .loading-text {
    font-size: var(--text-sm);
  }
}
</style>
