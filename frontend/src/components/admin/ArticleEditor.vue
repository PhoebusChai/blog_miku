<template>
  <div class="article-editor">
    <!-- 左右布局：左边设置面板，右边编辑器 -->
    <div class="editor-layout">
      <!-- 左侧设置面板 -->
      <aside class="settings-panel">
        <!-- 顶部标题 -->
        <div class="panel-header">
          <button class="back-btn" @click="handleBack">
            <ArrowLeft :size="18" />
          </button>
          <h2 class="panel-title">{{ isEdit ? '编辑文章' : '创建文章' }}</h2>
        </div>

        <div class="panel-content">
          <!-- 标题 -->
          <div class="form-section">
            <div class="section-header">
              <label class="section-label">
                <AlignLeft :size="16" />
                文章标题
              </label>
              <button class="ai-btn" @click="generateTitle" :disabled="aiGenerating.title">
                <Sparkles :size="14" />
                <span>{{ aiGenerating.title ? '生成中...' : 'AI生成' }}</span>
              </button>
            </div>
            <input
              v-model="article.title"
              type="text"
              class="title-input"
              placeholder="请输入文章标题..."
            />
          </div>

          <!-- 封面图上传 -->
          <div class="form-section">
            <div class="section-header">
              <label class="section-label">
                <Image :size="16" />
                封面图片
              </label>
              <button class="ai-btn" @click="generateCover" :disabled="aiGenerating.cover">
                <Sparkles :size="14" />
                <span>{{ aiGenerating.cover ? '生成中...' : 'AI生成' }}</span>
              </button>
            </div>
            <div class="cover-upload-area">
              <!-- 已上传的封面图预览 -->
              <div v-if="article.coverImage" class="cover-preview">
                <img :src="article.coverImage" alt="封面图" />
                <div class="cover-overlay">
                  <button class="overlay-btn" @click="handleChangeCover">
                    <Upload :size="16" />
                  </button>
                  <button class="overlay-btn danger" @click="handleRemoveCover">
                    <X :size="16" />
                  </button>
                </div>
              </div>

              <!-- 上传区域 -->
              <div v-else class="cover-upload-box" @click="triggerFileInput">
                <input
                  ref="fileInputRef"
                  type="file"
                  accept="image/*"
                  style="display: none"
                  @change="handleFileChange"
                />
                <div class="upload-content">
                  <Upload :size="32" />
                  <p class="upload-text">点击上传封面</p>
                  <p class="upload-hint">建议 1200x630</p>
                </div>
              </div>

              <!-- 上传进度 -->
              <div v-if="uploadProgress > 0 && uploadProgress < 100" class="upload-progress">
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
                </div>
                <span class="progress-text">{{ uploadProgress }}%</span>
              </div>
            </div>
          </div>

          <!-- 标签 -->
          <div class="form-section">
            <label class="section-label">
              <Tag :size="16" />
              文章标签
            </label>

            <!-- 已选标签 -->
            <div v-if="selectedTags.length > 0" class="selected-tags">
              <span
                v-for="tag in selectedTags"
                :key="tag.id"
                class="tag-chip"
              >
                <span class="tag-text">{{ tag.name }}</span>
                <button class="tag-remove" @click="removeTag(tag.id)">
                  <X :size="12" />
                </button>
              </span>
            </div>

            <!-- 标签选择 -->
            <div class="tag-selector">
              <button class="tag-add-btn" @click="toggleTagDropdown">
                <Plus :size="16" />
                <span>添加标签</span>
                <ChevronDown :size="14" class="dropdown-icon" :class="{ open: showTagDropdown }" />
              </button>

              <!-- 标签下拉列表 -->
              <div v-if="showTagDropdown" class="tag-dropdown">
                <!-- 搜索框 -->
                <div class="tag-search">
                  <input
                    v-model="tagSearchQuery"
                    type="text"
                    class="tag-search-input"
                    placeholder="搜索标签..."
                    @input="handleTagSearch"
                  />
                </div>

                <div v-if="filteredAvailableTags.length === 0" class="tag-empty">
                  <p>{{ tagSearchQuery ? '未找到匹配的标签' : '没有可选标签' }}</p>
                </div>
                <div v-else class="tag-list">
                  <button
                    v-for="tag in filteredAvailableTags"
                    :key="tag.id"
                    class="tag-option"
                    @click="selectTag(tag)"
                  >
                    <span class="tag-name">{{ tag.name }}</span>
                    <span v-if="tag.articleCount" class="tag-count">{{ tag.articleCount }}</span>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 摘要 -->
          <div class="form-section">
            <div class="section-header">
              <label class="section-label">
                <FileText :size="16" />
                文章摘要
              </label>
              <button class="ai-btn" @click="generateSummary" :disabled="aiGenerating.summary">
                <Sparkles :size="14" />
                <span>{{ aiGenerating.summary ? '生成中...' : 'AI生成' }}</span>
              </button>
            </div>
            <textarea
              v-model="article.summary"
              class="form-textarea"
              placeholder="文章摘要，用于列表展示和 SEO"
              rows="4"
            ></textarea>
          </div>

          <!-- 文章设置 -->
          <div class="form-section">
            <label class="section-label">
              <Settings :size="16" />
              发布设置
            </label>
            <div class="settings-list">
              <label class="setting-item">
                <input v-model="article.allowComment" type="checkbox" />
                <MessageSquare :size="16" />
                <span>允许评论</span>
              </label>
              <label class="setting-item">
                <input v-model="article.isTop" type="checkbox" />
                <Pin :size="16" />
                <span>置顶文章</span>
              </label>
              <label class="setting-item">
                <input v-model="article.isOriginal" type="checkbox" />
                <Award :size="16" />
                <span>原创声明</span>
              </label>
            </div>
          </div>
        </div>

        <!-- 底部操作按钮 -->
        <div class="panel-footer">
          <button class="btn-secondary" @click="handleSaveDraft">
            <Save :size="18" />
            <span>保存草稿</span>
          </button>
          <button class="btn-primary" @click="handlePublish">
            <Send :size="18" />
            <span>发布文章</span>
          </button>
        </div>
      </aside>

      <!-- 右侧编辑器 -->
      <main class="editor-main">
        <div class="editor-wrapper">
          <!-- Vditor 编辑器容器 -->
          <div ref="vditorRef" class="vditor-container"></div>

          <!-- 如果没有安装 Vditor，显示提示 -->
          <div v-if="!vditorLoaded" class="editor-placeholder">
            <AlertCircle :size="48" />
            <p>Markdown 编辑器加载中...</p>
            <p class="hint">
              请先安装 Vditor: <code>npm install vditor</code>
            </p>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import {
  ArrowLeft,
  Save,
  Send,
  Tag,
  Image,
  FileText,
  AlertCircle,
  Upload,
  X,
  AlignLeft,
  Settings,
  MessageSquare,
  Pin,
  Award,
  ChevronDown,
  Sparkles,
  Plus
} from 'lucide-vue-next'

import Vditor from 'vditor'
import 'vditor/dist/index.css'
import { uploadImage, getAllTags, getArticleDetail } from '@/api/article'
import { message } from '@/utils/message'

interface Props {
  articleId?: number
  isEdit?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  isEdit: false
})

const emit = defineEmits<{
  back: []
  save: [article: typeof article.value & { tagIds: number[] }]
  publish: [article: typeof article.value & { tagIds: number[] }]
}>()

// 编辑器实例
const vditorRef = ref<HTMLElement>()
let vditor: any = null
const vditorLoaded = ref(false)

// 文件上传
const fileInputRef = ref<HTMLInputElement>()
const uploadProgress = ref(0)

// 标签相关
interface TagItem {
  id: number
  name: string
  slug: string
  color?: string
  articleCount?: number
}

const allTags = ref<TagItem[]>([])
const selectedTags = ref<TagItem[]>([])
const tagSearchQuery = ref('')
const showTagDropdown = ref(false)
const tagsLoading = ref(false)

// AI 生成状态
const aiGenerating = ref({
  title: false,
  cover: false,
  summary: false
})

// 过滤可用标签（排除已选标签）
const filteredAvailableTags = computed(() => {
  const selectedIds = selectedTags.value.map((t) => t.id)
  let available = allTags.value.filter((t) => !selectedIds.includes(t.id))

  // 如果有搜索关键词，进行过滤
  if (tagSearchQuery.value.trim()) {
    const query = tagSearchQuery.value.toLowerCase()
    available = available.filter((t) => t.name.toLowerCase().includes(query))
  }

  return available
})

// 选择标签
function selectTag(tag: TagItem) {
  if (!selectedTags.value.find((t) => t.id === tag.id)) {
    selectedTags.value.push(tag)
  }
  tagSearchQuery.value = ''
  showTagDropdown.value = false
}

// 移除标签
function removeTag(tagId: number) {
  selectedTags.value = selectedTags.value.filter((t) => t.id !== tagId)
}

// 切换标签下拉
function toggleTagDropdown() {
  showTagDropdown.value = !showTagDropdown.value
}

// 处理标签搜索
function handleTagSearch() {
  showTagDropdown.value = true
}

// AI 生成功能
async function generateTitle() {
  if (!article.value.content) {
    message.warning('请先输入文章内容，AI 将根据内容生成标题')
    return
  }

  aiGenerating.value.title = true
  try {
    // TODO: 调用 AI API 生成标题
    await new Promise((resolve) => setTimeout(resolve, 2000))
    article.value.title = '基于 Vue 3 和 TypeScript 的现代化博客系统开发实践'
    message.success('标题已生成')
  } catch (error) {
    console.error('生成标题失败:', error)
    message.error('生成失败，请重试')
  } finally {
    aiGenerating.value.title = false
  }
}

async function generateCover() {
  if (!article.value.title) {
    message.warning('请先输入文章标题，AI 将根据标题生成封面')
    return
  }

  aiGenerating.value.cover = true
  try {
    // TODO: 调用 AI API 生成封面图
    await new Promise((resolve) => setTimeout(resolve, 2000))
    article.value.coverImage = 'https://via.placeholder.com/1200x630/39c5bb/ffffff?text=AI+Generated+Cover'
    message.success('封面已生成')
  } catch (error) {
    console.error('生成封面失败:', error)
    message.error('生成失败，请重试')
  } finally {
    aiGenerating.value.cover = false
  }
}

async function generateSummary() {
  if (!article.value.content) {
    message.warning('请先输入文章内容，AI 将根据内容生成摘要')
    return
  }

  aiGenerating.value.summary = true
  try {
    // TODO: 调用 AI API 生成摘要
    await new Promise((resolve) => setTimeout(resolve, 2000))
    article.value.summary =
      '本文详细介绍了如何使用 Vue 3 和 TypeScript 构建现代化的博客系统，包括项目架构设计、核心功能实现、性能优化等方面的实践经验。'
    message.success('摘要已生成')
  } catch (error) {
    console.error('生成摘要失败:', error)
    message.error('生成失败，请重试')
  } finally {
    aiGenerating.value.summary = false
  }
}

// 文章数据
const article = ref({
  title: '',
  content: '',
  tags: '',
  coverImage: '',
  summary: '',
  allowComment: true,
  isTop: false,
  isOriginal: true
})

// 初始化编辑器
onMounted(() => {
  initVditor()
  loadTags()

  // 如果是编辑模式，加载文章数据
  if (props.isEdit && props.articleId) {
    loadArticle(props.articleId)
  }

  // 点击外部关闭标签下拉
  document.addEventListener('click', handleClickOutside)
})

// 加载标签列表
async function loadTags() {
  try {
    tagsLoading.value = true
    const tags = await getAllTags()
    allTags.value = tags.map(tag => ({
      id: tag.id,
      name: tag.name,
      slug: tag.slug,
      articleCount: tag.articleCount
    }))
  } catch (error) {
    console.error('加载标签失败:', error)
  } finally {
    tagsLoading.value = false
  }
}

// 初始化 Vditor 编辑器
function initVditor() {
  if (!vditorRef.value) return

  try {
    vditor = new Vditor(vditorRef.value, {
      height: '100%',
      placeholder: '开始编写你的文章内容...\n\n支持 Markdown 语法，可以使用工具栏快速插入格式',
      theme: 'classic',
      icon: 'material',
      mode: 'ir', // 即时渲染模式
      toolbarConfig: {
        pin: true,
        hide: false,
        tooltipDirection: 's' // 提示信息显示在下方
      },
      outline: {
        enable: false // 关闭大纲，节省空间
      },
      cache: {
        enable: false
      },
      counter: {
        enable: true,
        type: 'markdown'
      },
      preview: {
        theme: {
          current: 'light',
          path: 'https://cdn.jsdelivr.net/npm/vditor/dist/css/content-theme'
        },
        hljs: {
          style: 'github', // 使用 GitHub 主题，适合浅色背景
          lineNumber: true,
          enable: true
        },
        markdown: {
          toc: true,
          mark: true,
          footnotes: true,
          autoSpace: true,
          codeBlockPreview: true
        }
      },
      toolbar: [
        'emoji',
        'headings',
        'bold',
        'italic',
        'strike',
        '|',
        'line',
        'quote',
        'list',
        'ordered-list',
        'check',
        '|',
        'code',
        'inline-code',
        'link',
        'table',
        '|',
        'upload',
        '|',
        'undo',
        'redo',
        '|',
        'fullscreen',
        'preview',
        'help'
      ],
      upload: {
        accept: 'image/*',
        max: 5 * 1024 * 1024, // 5MB
        fieldName: 'file',
        filename: (name) => name.replace(/[^(a-zA-Z0-9\u4e00-\u9fa5\.)]/g, ''),
        // 自定义上传处理
        handler: async (files: File[]) => {
          const succMap: { [key: string]: string } = {}
          const errFiles: string[] = []
          
          for (const file of files) {
            try {
              // 验证文件类型
              if (!file.type.startsWith('image/')) {
                message.error(`${file.name} 不是图片文件`)
                errFiles.push(file.name)
                continue
              }
              
              // 验证文件大小
              if (file.size > 5 * 1024 * 1024) {
                message.error(`${file.name} 大小超过 5MB`)
                errFiles.push(file.name)
                continue
              }
              
              // 调用上传 API
              const data = await uploadImage(file)
              
              // 拼接完整的图片 URL
              const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
              const fullUrl = baseURL + data.url
              
              // 记录成功上传的文件
              succMap[file.name] = fullUrl
              
              message.success(`${file.name} 上传成功`)
            } catch (error) {
              console.error(`上传 ${file.name} 失败:`, error)
              message.error(`上传 ${file.name} 失败`)
              errFiles.push(file.name)
            }
          }
          
          // 返回 null，让 Vditor 使用 succMap 自动插入图片
          // 不要返回 JSON 字符串，否则会被插入到编辑器中
          if (Object.keys(succMap).length > 0) {
            // 手动插入图片到编辑器
            for (const [filename, url] of Object.entries(succMap)) {
              const imageMarkdown = `![${filename}](${url})\n`
              vditor?.insertValue(imageMarkdown)
            }
          }
          
          return null
        }
      },
      after: () => {
        vditorLoaded.value = true
        if (article.value.content) {
          vditor.setValue(article.value.content)
        }
      }
    })
  } catch (error) {
    console.error('Vditor 初始化失败:', error)
  }
}

// 加载文章数据
async function loadArticle(id: number) {
  try {
    const data = await getArticleDetail(id)
    
    // 设置文章数据
    article.value = {
      title: data.title || '',
      content: data.content || '',
      tags: '', // 不再使用字符串，改用 selectedTags
      coverImage: data.coverImage || '',
      summary: data.summary || '',
      allowComment: true, // 根据实际字段调整
      isTop: data.isTop === 1,
      isOriginal: true // 根据实际字段调整
    }

    // 设置已选标签
    if (data.tags && data.tags.length > 0) {
      selectedTags.value = data.tags.map(tag => ({
        id: tag.id,
        name: tag.name,
        slug: tag.slug,
        articleCount: 0
      }))
    }

    // 设置编辑器内容 - 等待编辑器加载完成
    if (vditor && vditorLoaded.value) {
      vditor.setValue(article.value.content)
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    message.error('加载文章失败，请重试')
  }
}

// 获取编辑器内容
function getEditorContent(): string {
  if (vditor) {
    return vditor.getValue()
  }
  return article.value.content
}

// 返回
function handleBack() {
  if (confirm('确定要离开吗？未保存的内容将丢失。')) {
    emit('back')
  }
}

// 保存草稿
function handleSaveDraft() {
  article.value.content = getEditorContent()

  if (!article.value.title) {
    message.warning('请输入文章标题')
    return
  }

  // 获取选中的标签ID
  const tagIds = selectedTags.value.map(t => t.id)
  
  emit('save', { ...article.value, tagIds })
}

// 发布文章
function handlePublish() {
  article.value.content = getEditorContent()

  if (!article.value.title) {
    message.warning('请输入文章标题')
    return
  }

  if (!article.value.content) {
    message.warning('请输入文章内容')
    return
  }

  if (confirm('确定要发布这篇文章吗？')) {
    // 获取选中的标签ID
    const tagIds = selectedTags.value.map(t => t.id)
    
    emit('publish', { ...article.value, tagIds })
  }
}

// 触发文件选择
function triggerFileInput() {
  fileInputRef.value?.click()
}

// 处理文件选择
async function handleFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    message.error('请选择图片文件')
    return
  }
  
  // 验证文件大小（5MB）
  if (file.size > 5 * 1024 * 1024) {
    message.error('图片大小不能超过 5MB')
    return
  }
  
  await uploadCoverImage(file)
  
  // 清空 input，允许重复选择同一文件
  target.value = ''
}

// 上传封面图
async function uploadCoverImage(file: File) {
  try {
    uploadProgress.value = 0
    
    // 模拟上传进度
    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += 10
      }
    }, 200)
    
    // 调用上传 API
    const data = await uploadImage(file)
    
    clearInterval(progressInterval)
    
    // 拼接完整的图片 URL（指向后端服务器）
    const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
    article.value.coverImage = baseURL + data.url
    uploadProgress.value = 100
    
    message.success('封面上传成功')
    
    setTimeout(() => {
      uploadProgress.value = 0
    }, 1000)
  } catch (error) {
    console.error('上传封面图失败:', error)
    message.error('上传失败，请重试')
    uploadProgress.value = 0
  }
}

// 更换封面
function handleChangeCover() {
  triggerFileInput()
}

// 删除封面
function handleRemoveCover() {
  if (confirm('确定要删除封面图吗？')) {
    article.value.coverImage = ''
  }
}

// 清理
onBeforeUnmount(() => {
  if (vditor) {
    vditor.destroy()
  }
  document.removeEventListener('click', handleClickOutside)
})

// 点击外部关闭下拉
function handleClickOutside(event: MouseEvent) {
  const target = event.target as HTMLElement
  if (!target.closest('.tag-selector')) {
    showTagDropdown.value = false
  }
}
</script>

<style scoped>
.article-editor {
  display: flex;
  height: 100%;
  width:100%;
  background: var(--color-white);
}

/* 左右布局 */
.editor-layout {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 左侧设置面板 */
.settings-panel {
  width: 360px;
  background: var(--color-gray-50);
  border-right: 1px solid var(--color-gray-200);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  position: relative;
  z-index: 0;
}

/* 面板顶部 */
.panel-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg) var(--spacing-xl);
  background: var(--color-white);
  border-bottom: 1px solid var(--color-gray-200);
  flex-shrink: 0;
}

.back-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  color: var(--color-gray-700);
  background: var(--color-gray-100);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.back-btn:hover {
  background: var(--color-gray-200);
  color: var(--color-gray-900);
}

.panel-title {
  flex: 1;
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0;
}

/* 面板内容区 */
.panel-content {
  flex: 1;
  padding: var(--spacing-xl);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  overflow-y: auto;
}

.panel-content::-webkit-scrollbar {
  width: 6px;
}

.panel-content::-webkit-scrollbar-track {
  background: transparent;
}

.panel-content::-webkit-scrollbar-thumb {
  background: var(--color-gray-300);
  border-radius: 3px;
}

/* 面板底部操作按钮 */
.panel-footer {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  padding: var(--spacing-lg) var(--spacing-xl);
  background: var(--color-white);
  border-top: 1px solid var(--color-gray-200);
  flex-shrink: 0;
}

.btn-secondary,
.btn-primary {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-secondary {
  color: var(--color-gray-700);
  background: var(--color-white);
  border: 2px solid var(--color-gray-300);
}

.btn-secondary:hover {
  background: var(--color-gray-50);
  border-color: var(--color-gray-400);
}

.btn-primary {
  color: var(--color-white);
  background: linear-gradient(135deg, var(--color-miku-500), var(--color-cyan-500));
  box-shadow: 0 2px 8px rgba(57, 197, 187, 0.2);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(57, 197, 187, 0.3);
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.section-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-700);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-sm);
}

.ai-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  color: var(--color-white);
  background: linear-gradient(135deg, var(--color-miku-500), var(--color-cyan-500));
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
  box-shadow: 0 2px 6px rgba(57, 197, 187, 0.2);
}

.ai-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.4);
}

.ai-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.title-input {
  padding: var(--spacing-md) var(--spacing-lg);
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
  background: var(--color-white);
  border: 2px solid var(--color-gray-200);
  border-radius: 8px;
  transition: all var(--transition-fast);
}

.title-input:hover {
  border-color: var(--color-gray-300);
}

.title-input:focus {
  outline: none;
  border-color: var(--color-miku-500);
  box-shadow: 0 0 0 3px rgba(57, 197, 187, 0.1);
}

.title-input::placeholder {
  color: var(--color-gray-400);
}

/* 封面图上传 */
.cover-upload-area {
  position: relative;
}

.cover-preview {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  border-radius: 8px;
  overflow: hidden;
  background: var(--color-gray-100);
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  background: rgba(0, 0, 0, 0.6);
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.cover-preview:hover .cover-overlay {
  opacity: 1;
}

.overlay-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  color: var(--color-white);
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid var(--color-white);
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
  backdrop-filter: blur(4px);
}

.overlay-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
}

.overlay-btn.danger {
  border-color: #ef4444;
  background: rgba(239, 68, 68, 0.3);
}

.overlay-btn.danger:hover {
  background: rgba(239, 68, 68, 0.5);
}

.cover-upload-box {
  width: 100%;
  aspect-ratio: 16 / 9;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-white);
  border: 2px dashed var(--color-gray-300);
  border-radius: 8px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.cover-upload-box:hover {
  border-color: var(--color-miku-500);
  background: var(--color-miku-50);
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  color: var(--color-gray-500);
}

.cover-upload-box:hover .upload-content {
  color: var(--color-miku-600);
}

.upload-text {
  margin: 0;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
}

.upload-hint {
  margin: 0;
  font-size: var(--text-xs);
  color: var(--color-gray-400);
}

.upload-progress {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-sm);
  padding: var(--spacing-sm);
  background: var(--color-miku-50);
  border-radius: 6px;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: var(--color-gray-200);
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-miku-500), var(--color-cyan-500));
  transition: width var(--transition-fast);
}

.progress-text {
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  color: var(--color-miku-600);
  min-width: 35px;
  text-align: right;
}

.form-input,
.form-textarea {
  padding: var(--spacing-md) var(--spacing-lg);
  font-size: var(--text-sm);
  color: var(--color-gray-900);
  background: var(--color-white);
  border: 2px solid var(--color-gray-200);
  border-radius: 8px;
  transition: all var(--transition-fast);
  font-family: inherit;
}

.form-input:hover,
.form-textarea:hover {
  border-color: var(--color-gray-300);
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--color-miku-500);
  box-shadow: 0 0 0 3px rgba(57, 197, 187, 0.1);
}

.form-textarea {
  resize: vertical;
  line-height: 1.6;
}

/* 标签选择器 */
.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  margin-bottom: var(--spacing-sm);
}

.tag-chip {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  background: var(--color-white);
  border: 2px solid var(--color-gray-200);
  border-radius: 6px;
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  color: var(--color-gray-700);
  transition: all var(--transition-fast);
}

.tag-chip:hover {
  border-color: var(--color-miku-300);
  background: var(--color-miku-50);
}

.tag-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.tag-text {
  flex: 1;
}

.tag-remove {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  background: var(--color-gray-200);
  border: none;
  border-radius: 50%;
  color: var(--color-gray-600);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.tag-remove:hover {
  background: var(--color-red-500);
  color: var(--color-white);
  transform: scale(1.1);
}

.tag-selector {
  position: relative;
}

.tag-add-btn {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: var(--spacing-sm) var(--spacing-md);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-700);
  background: var(--color-white);
  border: 2px dashed var(--color-gray-300);
  border-radius: 8px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.tag-add-btn:hover {
  border-color: var(--color-miku-500);
  background: var(--color-miku-50);
  color: var(--color-miku-700);
}

.tag-add-btn span:first-of-type {
  flex: 1;
  text-align: left;
  margin-left: var(--spacing-xs);
}

.dropdown-icon {
  color: var(--color-gray-500);
  transition: transform var(--transition-fast);
  flex-shrink: 0;
}

.dropdown-icon.open {
  transform: rotate(180deg);
}

.tag-dropdown {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  right: 0;
  max-height: 280px;
  background: var(--color-white);
  border: 2px solid var(--color-gray-200);
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  overflow: hidden;
  z-index: 10;
}

.tag-search {
  padding: var(--spacing-sm);
  border-bottom: 1px solid var(--color-gray-200);
}

.tag-search-input {
  width: 100%;
  padding: var(--spacing-sm) var(--spacing-md);
  font-size: var(--text-sm);
  color: var(--color-gray-900);
  background: var(--color-gray-50);
  border: 1px solid var(--color-gray-200);
  border-radius: 6px;
  transition: all var(--transition-fast);
}

.tag-search-input:focus {
  outline: none;
  border-color: var(--color-miku-500);
  background: var(--color-white);
}

.tag-empty {
  padding: var(--spacing-xl);
  text-align: center;
  color: var(--color-gray-500);
  font-size: var(--text-sm);
}

.tag-empty p {
  margin: 0;
}

.tag-list {
  max-height: 200px;
  overflow-y: auto;
  padding: var(--spacing-xs);
}

.tag-list::-webkit-scrollbar {
  width: 6px;
}

.tag-list::-webkit-scrollbar-track {
  background: transparent;
}

.tag-list::-webkit-scrollbar-thumb {
  background: var(--color-gray-300);
  border-radius: 3px;
}

.tag-option {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  width: 100%;
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--color-white);
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
  text-align: left;
}

.tag-option:hover {
  background: var(--color-miku-50);
}

.tag-color {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.tag-name {
  flex: 1;
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-900);
}

.tag-count {
  padding: 2px 6px;
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  color: var(--color-gray-600);
  background: var(--color-gray-100);
  border-radius: 4px;
}

/* 文章设置 */
.settings-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.setting-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md);
  background: var(--color-white);
  border: 2px solid var(--color-gray-200);
  border-radius: 8px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.setting-item:hover {
  border-color: var(--color-miku-300);
  background: var(--color-miku-50);
}

.setting-item input[type='checkbox'] {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: var(--color-miku-500);
}

.setting-item span {
  flex: 1;
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-700);
}

/* 右侧编辑器 */
.editor-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--color-white);
  position: relative;
  z-index: 1;
}

.editor-wrapper {
  flex: 1;
  position: relative;
  overflow: hidden;
  z-index: 1;
}

.vditor-container {
  width: 100%;
  height: 100%;
  position: relative;
  z-index: 1;
}

.editor-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--color-gray-500);
  text-align: center;
}

.editor-placeholder p {
  margin: var(--spacing-md) 0;
  font-size: var(--text-base);
}

.editor-placeholder .hint {
  font-size: var(--text-sm);
  color: var(--color-gray-400);
}

.editor-placeholder code {
  padding: 2px 8px;
  background: var(--color-gray-100);
  border-radius: 4px;
  font-family: 'Courier New', monospace;
}

/* Vditor 样式覆盖 */
:deep(.vditor) {
  border: none;
  position: relative;
  z-index: 1;
}

/* ==================== 预览模式代码块样式 ==================== */

:deep(.vditor-preview pre) {
  margin: 20px 0;
  padding: 16px;
  background: #f6f8fa;
  border-radius: 8px;
  border: 1px solid var(--color-gray-200);
  overflow-x: auto;
}

:deep(.vditor-preview pre code) {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  color: #24292e;
}

:deep(.vditor-preview code) {
  padding: 2px 6px;
  background: #f6f8fa;
  border-radius: 3px;
  font-size: 0.9em;
}

:deep(.vditor-preview pre code) {
  padding: 0;
  background: transparent;
}

/* 预览模式语法高亮 */
:deep(.vditor-preview .hljs-keyword) { color: #d73a49; }
:deep(.vditor-preview .hljs-string) { color: #032f62; }
:deep(.vditor-preview .hljs-comment) { color: #6a737d; font-style: italic; }
:deep(.vditor-preview .hljs-function) { color: #6f42c1; }
:deep(.vditor-preview .hljs-number) { color: #005cc5; }
:deep(.vditor-preview .hljs-title) { color: #6f42c1; }

:deep(.vditor-toolbar) {
  background: linear-gradient(to bottom, var(--color-white), var(--color-gray-50));
  border-bottom: 2px solid var(--color-gray-200);
  padding: var(--spacing-sm) var(--spacing-md);
  position: relative;
  z-index: 2;
}

:deep(.vditor-toolbar__item) {
  color: var(--color-gray-700);
  border-radius: 6px;
  transition: all var(--transition-fast);
}

:deep(.vditor-toolbar__item:hover) {
  background: var(--color-miku-100);
  color: var(--color-miku-700);
}

:deep(.vditor-toolbar__item--current) {
  background: var(--color-miku-500);
  color: var(--color-white);
}

:deep(.vditor-toolbar__divider) {
  height: 20px;
  margin: 0 var(--spacing-xs);
  background: var(--color-gray-300);
}

:deep(.vditor-ir) {
  padding: var(--spacing-xl);
  background: var(--color-white);
}

/* IR 模式整体样式 */
:deep(.vditor-ir pre.vditor-reset) {
  font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 15px;
  line-height: 1.7;
  color: var(--color-gray-900);
}

/* IR 模式行内代码样式 */
:deep(.vditor-ir pre.vditor-reset code:not(.hljs)) {
  padding: 3px 8px;
  background: var(--color-miku-50);
  border: 1px solid var(--color-miku-200);
  border-radius: 6px;
  color: var(--color-miku-600);
  font-weight: 600;
  font-size: 0.9em;
  transition: all var(--transition-fast);
}

:deep(.vditor-ir pre.vditor-reset code:not(.hljs):hover) {
  background: var(--color-miku-100);
  border-color: var(--color-miku-300);
}

:deep(.vditor-ir pre.vditor-reset blockquote) {
  position: relative;
  border-left: 4px solid var(--color-miku-400);
  background: linear-gradient(135deg, var(--color-miku-50), var(--color-cyan-50));
  padding: var(--spacing-lg) var(--spacing-xl);
  margin: var(--spacing-lg) 0;
  border-radius: var(--radius-md);
  box-shadow: 0 2px 8px rgba(57, 197, 187, 0.1);
  border: 1px solid var(--color-miku-200);
}

:deep(.vditor-ir pre.vditor-reset blockquote::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(180deg, var(--color-miku-400), var(--color-cyan-400));
  border-radius: var(--radius-md) 0 0 var(--radius-md);
  box-shadow: 0 0 8px rgba(57, 197, 187, 0.4);
}

:deep(.vditor-ir pre.vditor-reset h1),
:deep(.vditor-ir pre.vditor-reset h2),
:deep(.vditor-ir pre.vditor-reset h3),
:deep(.vditor-ir pre.vditor-reset h4),
:deep(.vditor-ir pre.vditor-reset h5),
:deep(.vditor-ir pre.vditor-reset h6) {
  color: var(--color-gray-900);
  font-weight: var(--font-bold);
  border-bottom: 2px solid var(--color-miku-200);
  padding-bottom: var(--spacing-sm);
  margin-top: var(--spacing-xl);
  margin-bottom: var(--spacing-md);
}

:deep(.vditor-ir pre.vditor-reset a) {
  color: var(--color-miku-600);
  text-decoration: none;
  border-bottom: 1px solid var(--color-miku-300);
  transition: all var(--transition-fast);
}

:deep(.vditor-ir pre.vditor-reset a:hover) {
  color: var(--color-miku-700);
  border-bottom-color: var(--color-miku-500);
}

:deep(.vditor-counter) {
  color: var(--color-gray-600);
  font-size: var(--text-xs);
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--color-gray-50);
  border-top: 1px solid var(--color-gray-200);
}

/* Vditor 提示框样式 */
:deep(.vditor-hint) {
  z-index: 100 !important;
  background: var(--color-white);
  border: 2px solid var(--color-gray-200);
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

:deep(.vditor-hint__item) {
  color: var(--color-gray-700);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: 6px;
  transition: all var(--transition-fast);
}

:deep(.vditor-hint__item:hover),
:deep(.vditor-hint__item--current) {
  background: var(--color-miku-100);
  color: var(--color-miku-700);
}

:deep(.vditor-tooltipped) {
  position: relative;
}

:deep(.vditor-tooltipped::after) {
  z-index: 9999 !important;
  background: var(--color-gray-900);
  color: var(--color-white);
  font-size: var(--text-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.vditor-tooltipped::before) {
  z-index: 101 !important;
}

/* Vditor 上传面板 */
:deep(.vditor-upload) {
  z-index: 100 !important;
}

/* Vditor 菜单 */
:deep(.vditor-menu) {
  z-index: 100 !important;
  background: var(--color-white);
  border: 2px solid var(--color-gray-200);
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

:deep(.vditor-menu__item) {
  color: var(--color-gray-700);
  transition: all var(--transition-fast);
}

:deep(.vditor-menu__item:hover) {
  background: var(--color-miku-100);
  color: var(--color-miku-700);
}

/* Vditor 面板 */
:deep(.vditor-panel) {
  z-index: 100 !important;
  background: var(--color-white);
  border: 2px solid var(--color-gray-200);
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

:deep(.vditor-panel__input) {
  border: 2px solid var(--color-gray-200);
  border-radius: 6px;
  transition: all var(--transition-fast);
}

:deep(.vditor-panel__input:focus) {
  border-color: var(--color-miku-500);
  box-shadow: 0 0 0 3px rgba(57, 197, 187, 0.1);
}

:deep(.vditor-panel__btn) {
  background: var(--color-miku-500);
  color: var(--color-white);
  border-radius: 6px;
  transition: all var(--transition-fast);
}

:deep(.vditor-panel__btn:hover) {
  background: var(--color-miku-600);
  transform: translateY(-1px);
}
</style>

<style>
/* Vditor 全局样式 - 提示框等挂载到 body 的元素 */
.vditor-hint {
  z-index: 99999 !important;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.vditor-hint__item {
  color: #374151;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.2s;
}

.vditor-hint__item:hover,
.vditor-hint__item--current {
  background: #e0f7f5;
  color: #39c5bb;
}

/* 强制提示框显示在下方 */
.vditor-tooltipped::after {
  z-index: 100000 !important;
  background: #1f2937;
  color: white;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  top: 100% !important;
  bottom: auto !important;
  margin-top: 8px !important;
  margin-bottom: 0 !important;
}

.vditor-tooltipped::before {
  z-index: 100000 !important;
  top: 100% !important;
  bottom: auto !important;
  margin-top: 2px !important;
  margin-bottom: 0 !important;
  border-bottom-color: #1f2937 !important;
  border-top-color: transparent !important;
}

/* 针对不同方向的提示框 */
.vditor-tooltipped--n::after,
.vditor-tooltipped--ne::after,
.vditor-tooltipped--nw::after {
  top: 100% !important;
  bottom: auto !important;
  margin-top: 8px !important;
  margin-bottom: 0 !important;
}

.vditor-tooltipped--n::before,
.vditor-tooltipped--ne::before,
.vditor-tooltipped--nw::before {
  top: 100% !important;
  bottom: auto !important;
  margin-top: 2px !important;
  margin-bottom: 0 !important;
  border-bottom-color: #1f2937 !important;
  border-top-color: transparent !important;
  transform: rotate(180deg);
}

.vditor-upload {
  z-index: 99999 !important;
}

.vditor-menu {
  z-index: 99999 !important;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.vditor-menu__item {
  color: #374151;
  transition: all 0.2s;
}

.vditor-menu__item:hover {
  background: #e0f7f5;
  color: #39c5bb;
}

.vditor-panel {
  z-index: 99999 !important;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.vditor-panel__input {
  border: 2px solid #e5e7eb;
  border-radius: 6px;
  transition: all 0.2s;
}

.vditor-panel__input:focus {
  border-color: #39c5bb;
  box-shadow: 0 0 0 3px rgba(57, 197, 187, 0.1);
  outline: none;
}

.vditor-panel__btn {
  background: #39c5bb;
  color: white;
  border-radius: 6px;
  transition: all 0.2s;
  border: none;
  padding: 8px 16px;
  cursor: pointer;
}

.vditor-panel__btn:hover {
  background: #2da89f;
  transform: translateY(-1px);
}

.vditor-tip {
  z-index: 99999 !important;
}

/* Vditor 所有可能的弹出层 */
.vditor-panel--none {
  z-index: 99999 !important;
}

.vditor__panel {
  z-index: 99999 !important;
}



/* 响应式 */
@media (max-width: 1024px) {
  .settings-panel {
    width: 300px;
  }
}

@media (max-width: 768px) {
  .editor-layout {
    flex-direction: column;
  }

  .settings-panel {
    width: 100%;
    max-height: 50vh;
    border-right: none;
    border-bottom: 1px solid var(--color-gray-200);
  }

  .panel-footer {
    flex-direction: row;
  }

  .btn-secondary,
  .btn-primary {
    flex: 1;
  }
}
</style>
