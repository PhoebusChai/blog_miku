<template>
  <div class="system-tools">
    <!-- 工具卡片网格 -->
    <div class="tools-grid">
      <!-- 缓存管理 -->
      <div class="tool-card">
        <div class="tool-icon tool-icon--blue">
          <Database :size="32" />
        </div>
        <h3 class="tool-title">缓存管理</h3>
        <p class="tool-description">清理系统缓存，提升网站性能</p>
        <div class="tool-stats">
          <span class="stat-item">缓存大小: 128 MB</span>
        </div>
        <button class="tool-btn" @click="handleClearCache">
          <Trash2 :size="16" />
          清理缓存
        </button>
      </div>

      <!-- 数据备份 -->
      <div class="tool-card">
        <div class="tool-icon tool-icon--green">
          <HardDrive :size="32" />
        </div>
        <h3 class="tool-title">数据备份</h3>
        <p class="tool-description">备份网站数据，确保数据安全</p>
        <div class="tool-stats">
          <span class="stat-item">上次备份: 2024-12-19</span>
        </div>
        <button class="tool-btn" @click="handleBackup">
          <Download :size="16" />
          立即备份
        </button>
      </div>

      <!-- 数据恢复 -->
      <div class="tool-card">
        <div class="tool-icon tool-icon--purple">
          <RefreshCw :size="32" />
        </div>
        <h3 class="tool-title">数据恢复</h3>
        <p class="tool-description">从备份文件恢复网站数据</p>
        <div class="tool-stats">
          <span class="stat-item">可用备份: 5 个</span>
        </div>
        <button class="tool-btn" @click="handleRestore">
          <Upload :size="16" />
          恢复数据
        </button>
      </div>

      <!-- 日志查看 -->
      <div class="tool-card">
        <div class="tool-icon tool-icon--orange">
          <FileText :size="32" />
        </div>
        <h3 class="tool-title">系统日志</h3>
        <p class="tool-description">查看系统运行日志和错误信息</p>
        <div class="tool-stats">
          <span class="stat-item">今日日志: 1,234 条</span>
        </div>
        <button class="tool-btn" @click="handleViewLogs">
          <Eye :size="16" />
          查看日志
        </button>
      </div>

      <!-- 性能监控 -->
      <div class="tool-card">
        <div class="tool-icon tool-icon--red">
          <Activity :size="32" />
        </div>
        <h3 class="tool-title">性能监控</h3>
        <p class="tool-description">监控服务器性能和资源使用</p>
        <div class="tool-stats">
          <span class="stat-item">CPU: 45% | 内存: 62%</span>
        </div>
        <button class="tool-btn" @click="handleMonitor">
          <BarChart :size="16" />
          查看详情
        </button>
      </div>

      <!-- 数据库优化 -->
      <div class="tool-card">
        <div class="tool-icon tool-icon--cyan">
          <Zap :size="32" />
        </div>
        <h3 class="tool-title">数据库优化</h3>
        <p class="tool-description">优化数据库表，提升查询速度</p>
        <div class="tool-stats">
          <span class="stat-item">表数量: 12 个</span>
        </div>
        <button class="tool-btn" @click="handleOptimizeDB">
          <Settings :size="16" />
          开始优化
        </button>
      </div>

      <!-- 安全检查 -->
      <div class="tool-card">
        <div class="tool-icon tool-icon--yellow">
          <Shield :size="32" />
        </div>
        <h3 class="tool-title">安全检查</h3>
        <p class="tool-description">检查系统安全漏洞和风险</p>
        <div class="tool-stats">
          <span class="stat-item">安全评分: 95/100</span>
        </div>
        <button class="tool-btn" @click="handleSecurityCheck">
          <AlertCircle :size="16" />
          开始检查
        </button>
      </div>

      <!-- 系统更新 -->
      <div class="tool-card">
        <div class="tool-icon tool-icon--indigo">
          <Package :size="32" />
        </div>
        <h3 class="tool-title">系统更新</h3>
        <p class="tool-description">检查并安装系统更新</p>
        <div class="tool-stats">
          <span class="stat-item">当前版本: v1.0.0</span>
        </div>
        <button class="tool-btn" @click="handleCheckUpdate">
          <Download :size="16" />
          检查更新
        </button>
      </div>
    </div>

    <!-- 系统信息 -->
    <div class="system-info">
      <h2 class="section-title">系统信息</h2>
      <div class="info-grid">
        <div class="info-item">
          <span class="info-label">操作系统</span>
          <span class="info-value">Linux Ubuntu 22.04</span>
        </div>
        <div class="info-item">
          <span class="info-label">服务器软件</span>
          <span class="info-value">Nginx 1.24.0</span>
        </div>
        <div class="info-item">
          <span class="info-label">数据库</span>
          <span class="info-value">MySQL 8.0.35</span>
        </div>
        <div class="info-item">
          <span class="info-label">PHP版本</span>
          <span class="info-value">PHP 8.2.0</span>
        </div>
        <div class="info-item">
          <span class="info-label">磁盘空间</span>
          <span class="info-value">45.2 GB / 100 GB</span>
        </div>
        <div class="info-item">
          <span class="info-label">运行时间</span>
          <span class="info-value">15 天 8 小时</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  Database,
  HardDrive,
  RefreshCw,
  FileText,
  Activity,
  Zap,
  Shield,
  Package,
  Trash2,
  Download,
  Upload,
  Eye,
  BarChart,
  Settings,
  AlertCircle
} from 'lucide-vue-next'

const emit = defineEmits<{
  action: [action: string]
}>()

function handleClearCache() {
  if (confirm('确定要清理所有缓存吗？')) {
    emit('action', 'clear-cache')
    alert('缓存清理成功')
  }
}

function handleBackup() {
  emit('action', 'backup')
  alert('数据备份已开始，请稍候...')
}

function handleRestore() {
  emit('action', 'restore')
}

function handleViewLogs() {
  emit('action', 'view-logs')
}

function handleMonitor() {
  emit('action', 'monitor')
}

function handleOptimizeDB() {
  if (confirm('确定要优化数据库吗？此操作可能需要几分钟。')) {
    emit('action', 'optimize-db')
    alert('数据库优化已开始...')
  }
}

function handleSecurityCheck() {
  emit('action', 'security-check')
  alert('安全检查已开始...')
}

function handleCheckUpdate() {
  emit('action', 'check-update')
  alert('正在检查更新...')
}
</script>

<style scoped>
.system-tools {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2xl);
  width: 100%;
  height: 100%;
  overflow-y: auto;
  padding: var(--spacing-2xl) 2%;
}

.system-tools::-webkit-scrollbar {
  width: 6px;
}

.system-tools::-webkit-scrollbar-track {
  background: transparent;
}

.system-tools::-webkit-scrollbar-thumb {
  background: var(--color-gray-300);
  border-radius: 3px;
}

.system-tools::-webkit-scrollbar-thumb:hover {
  background: var(--color-gray-400);
}

.tools-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-xl);
}

.tool-card {
  background: var(--color-white);
  border: 2px solid var(--color-gray-100);
  border-radius: 16px;
  padding: var(--spacing-2xl);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  transition: all var(--transition-fast);
  position: relative;
  overflow: hidden;
}

.tool-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--color-miku-500), var(--color-cyan-500));
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.tool-card:hover {
  transform: translateY(-6px);
  border-color: var(--color-miku-200);
  box-shadow: 0 12px 24px rgba(57, 197, 187, 0.15);
}

.tool-card:hover::before {
  opacity: 1;
}

.tool-icon {
  width: 72px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  margin-bottom: var(--spacing-sm);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.tool-icon--blue {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: var(--color-white);
}

.tool-icon--green {
  background: linear-gradient(135deg, #10b981, #059669);
  color: var(--color-white);
}

.tool-icon--purple {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  color: var(--color-white);
}

.tool-icon--orange {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: var(--color-white);
}

.tool-icon--red {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: var(--color-white);
}

.tool-icon--cyan {
  background: linear-gradient(135deg, #06b6d4, #0891b2);
  color: var(--color-white);
}

.tool-icon--yellow {
  background: linear-gradient(135deg, #eab308, #ca8a04);
  color: var(--color-white);
}

.tool-icon--indigo {
  background: linear-gradient(135deg, #6366f1, #4f46e5);
  color: var(--color-white);
}

.tool-title {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0;
}

.tool-description {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  margin: 0;
  flex: 1;
  line-height: 1.6;
}

.tool-stats {
  padding: var(--spacing-md) 0;
  border-top: 1px solid var(--color-gray-100);
  border-bottom: 1px solid var(--color-gray-100);
}

.stat-item {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-600);
}

.tool-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-white);
  background: linear-gradient(135deg, var(--color-miku-500), var(--color-cyan-500));
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all var(--transition-fast);
  box-shadow: 0 2px 8px rgba(57, 197, 187, 0.2);
}

.tool-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(57, 197, 187, 0.3);
}

.tool-btn:active {
  transform: translateY(0);
}

.system-info {
  background: var(--color-white);
  border: 2px solid var(--color-gray-100);
  border-radius: 16px;
  padding: var(--spacing-2xl);
}

.section-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0 0 var(--spacing-sm) 0;
}

.section-title::after {
  content: '';
  display: block;
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, var(--color-miku-500), var(--color-cyan-500));
  border-radius: 2px;
  margin-top: var(--spacing-md);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--spacing-lg);
  margin-top: var(--spacing-2xl);
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  padding: var(--spacing-lg);
  background: linear-gradient(135deg, var(--color-gray-50), var(--color-white));
  border: 1px solid var(--color-gray-200);
  border-radius: 12px;
  transition: all var(--transition-fast);
}

.info-item:hover {
  border-color: var(--color-miku-300);
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.1);
}

.info-label {
  font-size: var(--text-xs);
  font-weight: var(--font-bold);
  color: var(--color-gray-500);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-value {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
}

@media (max-width: 1200px) {
  .tools-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
}

@media (max-width: 768px) {
  .tools-grid {
    grid-template-columns: 1fr;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
