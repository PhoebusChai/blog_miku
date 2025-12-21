<template>
  <div class="article-heatmap">
    <h3 class="article-heatmap__title">文章发布记录</h3>
    
    <div class="article-heatmap__stats">
      <span class="stat-text">过去一年共发布 <strong>{{ totalArticles }}</strong> 篇文章</span>
    </div>

    <div class="article-heatmap__grid">
      <div class="heatmap-months">
        <span
          v-for="(month, index) in months"
          :key="index"
          class="month-label"
          :style="{ left: `calc((100% - 24px) * ${month.position / 18} + 24px - 0.3em)` }"
        >
          {{ month.label }}
        </span>
      </div>
      
      <div class="heatmap-container">
        <div class="heatmap-weeks">
          <span v-for="day in weekDays" :key="day" class="week-label">
            {{ day }}
          </span>
        </div>
        
        <div class="heatmap-cells">
          <div
            v-for="(cell, index) in heatmapData"
            :key="index"
            :class="['heatmap-cell', `heatmap-cell--level-${cell.level}`]"
            :title="cell.date ? `${cell.date}: ${cell.count} 篇文章` : ''"
          ></div>
        </div>
      </div>
    </div>

    <div class="article-heatmap__legend">
      <span class="legend-label">少</span>
      <div class="legend-cells">
        <div
          v-for="level in 5"
          :key="level"
          :class="['legend-cell', `legend-cell--level-${level - 1}`]"
        ></div>
      </div>
      <span class="legend-label">多</span>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 文章发布热力图组件
 * @description 类似 GitHub 贡献图，展示文章发布记录
 */
import { computed } from 'vue'

interface HeatmapCell {
  date: string
  count: number
  level: number
}

interface Props {
  /** 文章发布数据 */
  data?: Record<string, number>
}

const props = withDefaults(defineProps<Props>(), {
  data: () => ({})
})

const weekDays = ['一', '三', '五', '日']

// 动态生成月份标签（只显示1号出现的月份）
const months = computed(() => {
  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const monthLabels: { label: string; position: number }[] = []
  
  // 计算起始日期
  const todayWeekDay = today.getDay()
  const todayWeekDayAdjusted = todayWeekDay === 0 ? 7 : todayWeekDay
  const weeksToShow = 18
  const daysBack = (weeksToShow - 1) * 7 + (todayWeekDayAdjusted - 1)
  const startDate = new Date(today)
  startDate.setDate(startDate.getDate() - daysBack)
  
  // 遍历所有天，找到每个月1号的位置
  const totalDays = weeksToShow * 7
  
  for (let day = 0; day < totalDays; day++) {
    const currentDate = new Date(startDate)
    currentDate.setDate(currentDate.getDate() + day)
    
    // 只处理今天及之前的日期，并且是1号
    if (currentDate <= today && currentDate.getDate() === 1) {
      // 计算这一天在第几列（每列7天）
      const columnIndex = Math.floor(day / 7)
      monthLabels.push({
        label: `${currentDate.getMonth() + 1}月`,
        position: columnIndex
      })
    }
  }
  
  return monthLabels
})

// 生成过去4个月的热力图数据
const heatmapData = computed<HeatmapCell[]>(() => {
  const cells: HeatmapCell[] = []
  
  // 获取本地时间的今天（不受时区影响）
  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  
  // 计算今天是星期几 (0=周日, 1=周一, ..., 6=周六)
  const todayWeekDay = today.getDay()
  // 转换为周一为1，周日为7
  const todayWeekDayAdjusted = todayWeekDay === 0 ? 7 : todayWeekDay
  
  // 计算需要显示多少天（18周完整的格子，约4个月）
  const weeksToShow = 18
  const totalCells = weeksToShow * 7
  
  // 计算起始日期：确保今天在最后一列的正确位置
  // 从今天往前推到第一列的同一行
  const daysBack = (weeksToShow - 1) * 7 + (todayWeekDayAdjusted - 1)
  const startDate = new Date(today)
  startDate.setDate(startDate.getDate() - daysBack)
  
  // 生成数据
  for (let i = 0; i < totalCells; i++) {
    const date = new Date(startDate)
    date.setDate(date.getDate() + i)
    
    // 获取今天的时间戳用于比较（包含今天）
    const todayTimestamp = today.getTime()
    const dateTimestamp = date.getTime()
    
    // 只为今天及之前的日期生成数据
    if (dateTimestamp <= todayTimestamp) {
      // 格式化日期为 YYYY-MM-DD（使用本地时间）
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const dateStr = `${year}-${month}-${day}`
      
      // 只使用传入的数据，不生成随机数据
      const count = props.data[dateStr] || 0
      
      // 根据文章数量计算等级 (0-4)
      let level = 0
      if (count > 0) level = 1
      if (count >= 2) level = 2
      if (count >= 3) level = 3
      if (count >= 4) level = 4

      cells.push({ date: dateStr, count, level })
    } else {
      // 未来的日期显示为空
      cells.push({ date: '', count: 0, level: -1 })
    }
  }

  return cells
})

// 计算总文章数（只统计有效的日期，排除未来日期）
const totalArticles = computed(() => {
  return heatmapData.value
    .filter(cell => cell.level >= 0) // 只统计有效日期
    .reduce((sum, cell) => sum + cell.count, 0)
})
</script>

<style scoped>
.article-heatmap {
  padding: var(--spacing-lg) 0;
}

.article-heatmap__title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin-bottom: var(--spacing-sm);
  color: var(--color-gray-900);
}

.article-heatmap__stats {
  margin-bottom: var(--spacing-md);
}

.stat-text {
  font-size: var(--text-xs);
  color: var(--color-gray-600);
}

.stat-text strong {
  color: var(--color-miku-600);
  font-weight: var(--font-bold);
}

.article-heatmap__grid {
  margin-bottom: var(--spacing-md);
  width: 100%;
}

.heatmap-months {
  position: relative;
  height: 16px;
  margin-bottom: var(--spacing-xs);
  width: 100%;
}

.month-label {
  position: absolute;
  font-size: var(--text-xs);
  color: var(--color-gray-500);
  white-space: nowrap;
}

.heatmap-container {
  display: flex;
  gap: var(--spacing-sm);
  width: 100%;
}

.heatmap-weeks {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 16px;
  flex-shrink: 0;
  padding-top: 1px;
  padding-bottom: 1px;
}

.week-label {
  font-size: 10px;
  color: var(--color-gray-500);
  line-height: 1;
  display: flex;
  align-items: center;
}

.heatmap-cells {
  display: grid;
  grid-template-columns: repeat(18, 1fr);
  grid-template-rows: repeat(7, 1fr);
  gap: 2px;
  grid-auto-flow: column;
  flex: 1;
  aspect-ratio: 18 / 7;
}

.heatmap-cell {
  width: 100%;
  height: 100%;
  background: var(--color-gray-100);
  border-radius: 2px;
  transition: all var(--transition-fast);
  cursor: pointer;
}

.heatmap-cell:hover {
  transform: scale(1.15);
  box-shadow: 0 0 0 1px var(--color-gray-300);
  z-index: 1;
}

.heatmap-cell--level--1 {
  background: transparent;
  border: none;
  cursor: default;
}

.heatmap-cell--level--1:hover {
  transform: none;
  box-shadow: none;
}

.heatmap-cell--level-0 {
  background: var(--color-gray-100);
}

.heatmap-cell--level-1 {
  background: #c7f0db;
}

.heatmap-cell--level-2 {
  background: #7ee0b0;
}

.heatmap-cell--level-3 {
  background: #39c5bb;
}

.heatmap-cell--level-4 {
  background: var(--color-miku-500);
}

.article-heatmap__legend {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: var(--spacing-xs);
}

.legend-label {
  font-size: var(--text-xs);
  color: var(--color-gray-500);
}

.legend-cells {
  display: flex;
  gap: 3px;
}

.legend-cell {
  width: 10px;
  height: 10px;
  border-radius: 2px;
}

.legend-cell--level-0 {
  background: var(--color-gray-100);
}

.legend-cell--level-1 {
  background: #c7f0db;
}

.legend-cell--level-2 {
  background: #7ee0b0;
}

.legend-cell--level-3 {
  background: #39c5bb;
}

.legend-cell--level-4 {
  background: var(--color-miku-500);
}

/* 响应式优化 */
@media (max-width: 768px) {
  .heatmap-cells {
    min-height: 80px;
  }
}
</style>
