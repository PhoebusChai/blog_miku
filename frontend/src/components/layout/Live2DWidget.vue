<template>
  <div class="live2d-widget" ref="widgetRef">
    <transition name="bubble-fade">
      <div v-if="showBubble" class="speech-bubble">
        {{ currentMessage }}
      </div>
    </transition>
    
    <transition name="menu-slide">
      <div v-if="showMenu" class="control-menu" @click.stop>
        <div class="menu-item" @click="toggleAutoMove" :title="autoMoveEnabled ? '禁用自动移动' : '启用自动移动'">
          <MoveIcon v-if="autoMoveEnabled" :size="22" />
          <XCircleIcon v-else :size="22" />
        </div>
        <div class="menu-item" @click="toggleDraggable" :title="draggableEnabled ? '禁用拖动' : '启用拖动'">
          <GripIcon v-if="draggableEnabled" :size="22" />
          <LockIcon v-else :size="22" />
        </div>
        <div class="menu-item" @click="changeExpression" title="换表情">
          <SmileIcon :size="22" />
        </div>
        <div class="menu-item" @click="playRandomMotion" title="随机动作">
          <SparklesIcon :size="22" />
        </div>
      </div>
    </transition>
    
    <div 
      class="live2d-canvas-container"
      :class="{ draggable: draggableEnabled, dragging: isDragging }"
      @mousedown="startDrag"
    >
      <canvas ref="canvasRef" width="200" height="450"></canvas>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as PIXI from 'pixi.js'
import { Live2DModel } from 'pixi-live2d-display'
import { MoveIcon, XCircleIcon, SmileIcon, SparklesIcon, GripIcon, LockIcon } from 'lucide-vue-next'

// 注册 PIXI 的 Ticker 和 Application
if (typeof window !== 'undefined') {
  (window as any).PIXI = PIXI
}

const canvasRef = ref<HTMLCanvasElement | null>(null)
const widgetRef = ref<HTMLDivElement | null>(null)
const showBubble = ref(false)
const currentMessage = ref('')
const showMenu = ref(false)
const autoMoveEnabled = ref(false)
const draggableEnabled = ref(false)
const isDragging = ref(false)

let app: PIXI.Application | null = null
let model: Live2DModel | null = null
let bubbleTimer: number | null = null
let mouseHoverTimer: number | null = null
let menuHoverTimer: number | null = null
let isMoving = ref(false)
let targetX: number | null = null

const expressions = ['Chijing', 'Dazhihui', 'Mimiyan', 'Saihong', 'Yanjing', 'liuhan']
const motions = ['02_Happy', '03_Love', '04_Happy', '11_Acting']

const messages = [
  '你好呀~ 欢迎来到我的博客！',
  '今天也要加油哦！',
  '点击我可以看到不同的表情哦~',
  '要不要一起学习呢？',
  '休息一下，喝杯水吧~',
  '代码写累了吗？',
  '记得保护眼睛哦！',
  '今天天气真不错呢~',
  '有什么想对我说的吗？',
  '一起加油吧！💪'
]

const showMessage = (message: string, duration = 3000) => {
  currentMessage.value = message
  showBubble.value = true
  
  if (bubbleTimer) {
    clearTimeout(bubbleTimer)
  }
  
  bubbleTimer = window.setTimeout(() => {
    showBubble.value = false
  }, duration)
}

const getRandomMessage = () => {
  const randomIndex = Math.floor(Math.random() * messages.length)
  return messages[randomIndex]
}

const handleCanvasClick = (e: Event) => {
  e.stopPropagation()
  showMenu.value = !showMenu.value
}

const handleClickOutside = (e: MouseEvent) => {
  if (widgetRef.value && !widgetRef.value.contains(e.target as Node)) {
    showMenu.value = false
  }
}

const toggleAutoMove = () => {
  autoMoveEnabled.value = !autoMoveEnabled.value
  const message = autoMoveEnabled.value ? '已启用自动移动~' : '已禁用自动移动'
  showMessage(message, 2000)
}

const toggleDraggable = () => {
  draggableEnabled.value = !draggableEnabled.value
  const message = draggableEnabled.value ? '已启用拖动~' : '已禁用拖动'
  showMessage(message, 2000)
}

const startDrag = (e: MouseEvent) => {
  if (!draggableEnabled.value || !widgetRef.value) return
  
  e.preventDefault()
  isDragging.value = true
  
  // 播放拖动动作
  try {
    if (model && model.internalModel) {
      model.motion('TapBody', 0) // 使用 Love 或 Acting 动作
    }
  } catch (error) {
    console.warn('Failed to play drag motion:', error)
  }
  
  const rect = widgetRef.value.getBoundingClientRect()
  const offsetX = e.clientX - rect.left
  const offsetY = e.clientY - rect.top
  
  const onMouseMove = (e: MouseEvent) => {
    if (!isDragging.value || !widgetRef.value) return
    
    const x = e.clientX - offsetX
    const y = e.clientY - offsetY
    
    // 计算边界
    const maxX = window.innerWidth - rect.width
    const maxY = window.innerHeight - rect.height
    
    // 限制在屏幕范围内
    const boundedX = Math.max(0, Math.min(x, maxX))
    const boundedY = Math.max(0, Math.min(y, maxY))
    
    // 转换为 right 和 bottom
    const right = window.innerWidth - boundedX - rect.width
    const bottom = window.innerHeight - boundedY - rect.height
    
    widgetRef.value.style.right = `${right}px`
    widgetRef.value.style.bottom = `${bottom}px`
  }
  
  const onMouseUp = () => {
    isDragging.value = false
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
    
    // 拖动结束后恢复待机动作
    try {
      if (model && model.internalModel) {
        model.motion('Idle', 0)
      }
    } catch (error) {
      console.warn('Failed to play idle motion:', error)
    }
  }
  
  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', onMouseUp)
}

const changeExpression = () => {
  if (model) {
    const randomIndex = Math.floor(Math.random() * expressions.length)
    try {
      model.expression(expressions[randomIndex])
      showMessage('换个表情~', 1500)
    } catch (error) {
      console.warn('Failed to change expression:', error)
    }
  }
}

const playRandomMotion = () => {
  if (model && model.internalModel) {
    try {
      model.motion('TapBody', Math.floor(Math.random() * 2))
      showMessage('看我的动作~', 2000)
    } catch (error) {
      console.warn('Failed to play motion:', error)
    }
  }
}

const moveToMouse = (mouseX: number) => {
  if (!widgetRef.value || !model || !autoMoveEnabled.value) return
  
  const screenWidth = window.innerWidth
  const widgetWidth = 200
  
  // 气泡框宽度约250px，在左侧；菜单宽度约60px，在右侧
  const bubbleWidth = 250
  const menuWidth = 60
  const minRight = menuWidth + 20 // 右侧最小距离，保证菜单可见
  const maxRight = screenWidth - widgetWidth - bubbleWidth - 20 // 左侧最大距离，保证气泡可见
  
  // 计算目标位置（从右边距离）
  const targetRight = Math.max(minRight, Math.min(screenWidth - mouseX - widgetWidth / 2, maxRight))
  const currentRight = parseInt(getComputedStyle(widgetRef.value).right) || 80
  
  // 如果距离太近，不移动
  if (Math.abs(targetRight - currentRight) < 50) return
  
  isMoving.value = true
  
  // 添加自动移动类，启用过渡
  widgetRef.value.classList.add('auto-moving')
  
  // 判断移动方向
  const isMovingLeft = targetRight < currentRight
  
  // 根据方向翻转模型
  if (model) {
    if (isMovingLeft) {
      model.scale.x = -Math.abs(model.scale.x)
    } else {
      model.scale.x = Math.abs(model.scale.x)
    }
  }
  
  // 播放走路动画
  try {
    if (model.internalModel) {
      model.motion('Walking', 0)
    }
  } catch (error) {
    console.warn('Failed to play walking motion:', error)
  }
  
  // 显示移动消息
  const moveMessages = ['我来啦~', '等等我！', '马上就到~', '让我过去看看~']
  showMessage(moveMessages[Math.floor(Math.random() * moveMessages.length)], 2000)
  
  // 执行移动动画
  widgetRef.value.style.right = `${targetRight}px`
  
  // 移动完成后恢复状态
  setTimeout(() => {
    isMoving.value = false
    if (model) {
      model.scale.x = Math.abs(model.scale.x)
    }
    try {
      if (model && model.internalModel) {
        model.motion('Idle', 0)
      }
    } catch (error) {
      console.warn('Failed to play idle motion:', error)
    }
    // 移除自动移动类，禁用过渡
    if (widgetRef.value) {
      widgetRef.value.classList.remove('auto-moving')
    }
  }, 4000)
}

onMounted(async () => {
  if (!canvasRef.value) return

  // 等待 Live2D SDK 加载
  const waitForSDK = () => {
    return new Promise<void>((resolve) => {
      const checkSDK = () => {
        if ((window as any).Live2D || (window as any).Live2DCubismCore) {
          resolve()
        } else {
          setTimeout(checkSDK, 100)
        }
      }
      checkSDK()
    })
  }

  await waitForSDK()

  // 初始化 PIXI 应用
  app = new PIXI.Application({
    view: canvasRef.value,
    width: 200,
    height: 450,
    backgroundAlpha: 0,
    antialias: true
  })

  try {
    // 加载 Live2D 模型
    model = await Live2DModel.from('/src/assets/live2d/MIKU/miku.model3.json', {
      autoInteract: false
    })
    
    if (model && app) {
      // 禁用模型的交互性，避免 PIXI 事件系统冲突
      model.eventMode = 'none'
      
      // 设置模型大小和位置
      const scale = 0.1
      model.scale.set(scale)
      
      // 模型站在底部
      model.anchor.set(0.5, 1)
      model.x = 100
      model.y = 450

      // 添加到舞台
      app.stage.addChild(model)

      // 禁用舞台交互
      app.stage.eventMode = 'none'
      app.stage.interactiveChildren = false

      // 添加鼠标跟随效果（使用 DOM 事件）
      const container = canvasRef.value
      if (container) {
        container.addEventListener('mousemove', (e: MouseEvent) => {
          if (model) {
            const rect = container.getBoundingClientRect()
            const x = e.clientX - rect.left
            const y = e.clientY - rect.top
            model.focus(x, y)
          }
        })
        
        // 点击触发菜单
        container.addEventListener('click', handleCanvasClick)

      }
    }
  } catch (error) {
    console.error('Failed to load Live2D model:', error)
  }

  // 首次加载显示欢迎消息
  setTimeout(() => {
    showMessage('你好呀~ 欢迎来到我的博客！', 4000)
  }, 1000)

  // 定时显示随机消息
  const messageInterval = setInterval(() => {
    if (!showBubble.value) {
      showMessage(getRandomMessage(), 3000)
    }
  }, 20000) // 每20秒显示一次

  // 全局鼠标移动监听
  let lastMouseX = 0
  let lastMouseY = 0
  
  const handleGlobalMouseMove = (e: MouseEvent) => {
    lastMouseX = e.clientX
    lastMouseY = e.clientY
    
    if (mouseHoverTimer) {
      clearTimeout(mouseHoverTimer)
    }
    
    if (isMoving.value) return
    
    mouseHoverTimer = window.setTimeout(() => {
      if (!isMoving.value && widgetRef.value) {
        moveToMouse(lastMouseX)
      }
    }, 5000)
  }
  
  // 添加全局鼠标监听
  document.addEventListener('mousemove', handleGlobalMouseMove)
  // 添加点击外部关闭菜单
  document.addEventListener('click', handleClickOutside)
  
  // 清理
  onUnmounted(() => {
    clearInterval(messageInterval)
    document.removeEventListener('mousemove', handleGlobalMouseMove)
    document.removeEventListener('click', handleClickOutside)
  })
})

onUnmounted(() => {
  if (bubbleTimer) clearTimeout(bubbleTimer)
  if (mouseHoverTimer) clearTimeout(mouseHoverTimer)
  if (menuHoverTimer) clearTimeout(menuHoverTimer)
  if (app) {
    app.destroy(true, { children: true })
  }
})
</script>

<style scoped>
.live2d-widget {
  position: fixed;
  right: 80px;
  bottom: 0;
  z-index: 9999;
  user-select: none;
  transition: opacity 0.3s ease;
}

.live2d-widget.auto-moving {
  transition: right 4s ease-in-out, bottom 4s ease-in-out, opacity 0.3s ease;
}

.live2d-canvas-container {
  position: relative;
  background: transparent;
  border-radius: 16px 16px 0 0;
  padding: 10px 10px 0 10px;
}

canvas {
  display: block;
  border-radius: 12px;
}

.speech-bubble {
  position: absolute;
  bottom: 380px;
  left: -180px;
  min-width: 180px;
  max-width: 250px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  color: #333;
  font-size: 14px;
  line-height: 1.5;
  z-index: 10;
  pointer-events: none;
  border: 2px solid rgba(57, 197, 187, 0.3);
}

.speech-bubble::after {
  content: '';
  position: absolute;
  bottom: -8px;
  right: 20px;
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-top: 8px solid rgba(255, 255, 255, 0.95);
}

.bubble-fade-enter-active,
.bubble-fade-leave-active {
  transition: all 0.3s ease;
}

.bubble-fade-enter-from {
  opacity: 0;
  transform: translateY(10px) scale(0.9);
}

.bubble-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.9);
}

@media (max-width: 768px) {
  .live2d-widget {
    right: 70px;
    bottom: 0;
  }

  .live2d-canvas-container {
    padding: 5px;
  }

  canvas {
    width: 150px !important;
    height: 337px !important;
  }

  .speech-bubble {
    bottom: 280px;
    left: -160px;
    min-width: 150px;
    max-width: 200px;
    font-size: 12px;
    padding: 10px 12px;
  }

  .control-menu {
    right: -55px;
  }

  .menu-item {
    width: 40px;
    height: 40px;
  }
}

.control-menu {
  position: absolute;
  right: -60px;
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border-radius: 12px;
  padding: 6px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  z-index: 11;
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  cursor: pointer;
  border-radius: 50%;
  transition: all 0.3s ease;
  color: rgba(57, 197, 187, 0.8);
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(57, 197, 187, 0.2);
}

.menu-item:hover {
  background: rgba(57, 197, 187, 0.2);
  transform: scale(1.1);
  color: #39c5bb;
  border-color: rgba(57, 197, 187, 0.5);
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.3);
}

.menu-item:active {
  transform: scale(0.95);
}

.live2d-canvas-container.draggable {
  cursor: move;
}

.live2d-canvas-container.dragging {
  cursor: grabbing;
}

.menu-slide-enter-active,
.menu-slide-leave-active {
  transition: all 0.3s ease;
}

.menu-slide-enter-from {
  opacity: 0;
  transform: translateY(-50%) translateX(20px);
}

.menu-slide-leave-to {
  opacity: 0;
  transform: translateY(-50%) translateX(20px);
}
</style>
