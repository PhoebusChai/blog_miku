<template>
  <div class="article-detail">
    <!-- 阅读进度条 -->
    <div class="reading-progress">
      <div class="reading-progress__bar" :style="{ width: `${readingProgress}%` }"></div>
    </div>
    
    <AppHeader />
    
    <!-- 返回按钮 -->
    <button class="back-button" @click="goBack" title="返回">
      <ArrowLeft :size="20" />
      <span>返回</span>
    </button>
    
    <main class="article-detail__main">
      <div v-if="loading" class="loading">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      
      <div v-else-if="article" class="article-layout">
        <!-- 主内容区 -->
        <article class="article-content glass-card">
          <!-- 文章标题和元信息 -->
          <header class="article-header">
            <div class="article-category">
              <FileText :size="14" />
              {{ article.category }}
            </div>
            <h1 class="article-title">{{ article.title }}</h1>
            <div class="article-meta">
              <span class="meta-item">
                <Calendar :size="14" />
                {{ formatDate(article.createdAt) }}
              </span>
              <span class="meta-item">
                <Clock :size="14" />
                {{ article.readTime || '5' }}分钟
              </span>
              <span class="meta-item">
                <Eye :size="14" />
                {{ article.views || 0 }}
              </span>
            </div>
          </header>

          <!-- 文章摘要 -->
          <div v-if="article.summary" class="article-summary">
            <div class="summary-icon">
              <FileText :size="20" />
            </div>
            <p>{{ article.summary }}</p>
          </div>

          <!-- 文章正文 -->
          <div class="article-body" v-html="article.content"></div>

          <!-- 标签 -->
          <div v-if="article.tags && article.tags.length" class="article-tags">
            <span class="tags-label">标签：</span>
            <span v-for="tag in article.tags" :key="tag" class="tag">
              #{{ tag }}
            </span>
          </div>

          <!-- 文章底部操作 -->
          <div class="article-actions">
            <button class="action-btn like-btn">
              <Heart :size="18" />
              <span>{{ article.likes || 0 }}</span>
            </button>
            <button class="action-btn share-btn">
              <Share2 :size="18" />
              <span>分享</span>
            </button>
            <button class="action-btn bookmark-btn">
              <Bookmark :size="18" />
              <span>收藏</span>
            </button>
          </div>
        </article>

        <!-- 侧边栏 -->
        <aside class="article-sidebar">
          <!-- 统计信息 -->
          <div class="sidebar-card glass-card stats-card">
            <h3 class="sidebar-card__title">
              <BarChart3 :size="16" />
              文章统计
            </h3>
            <div class="stats-grid">
              <div class="stat-item">
                <div class="stat-value">{{ article.views || 0 }}</div>
                <div class="stat-label">浏览</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ article.likes || 0 }}</div>
                <div class="stat-label">点赞</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ article.comments || 0 }}</div>
                <div class="stat-label">评论</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ article.shares || 0 }}</div>
                <div class="stat-label">分享</div>
              </div>
            </div>
          </div>

          <!-- 目录 -->
          <div class="sidebar-card glass-card toc-card">
            <h3 class="sidebar-card__title">
              <List :size="16" />
              目录
            </h3>
            <nav class="toc">
              <ul class="toc-list">
                <li v-for="(heading, index) in tableOfContents" :key="index" 
                    :class="['toc-item', `toc-level-${heading.level}`]">
                  <a :href="`#${heading.id}`" class="toc-link">{{ heading.text }}</a>
                </li>
              </ul>
            </nav>
          </div>

          <!-- 相关文章 -->
          <div class="sidebar-card glass-card related-card">
            <h3 class="sidebar-card__title">
              <Link :size="16" />
              相关文章
            </h3>
            <ul class="related-list">
              <li v-for="related in relatedArticles" :key="related.id" class="related-item">
                <a :href="`/article/${related.id}`" class="related-link">
                  <div class="related-thumb" v-if="related.cover">
                    <img :src="related.cover" :alt="related.title" />
                  </div>
                  <div class="related-info">
                    <h4 class="related-title">{{ related.title }}</h4>
                    <p class="related-date">{{ formatDate(related.createdAt) }}</p>
                  </div>
                </a>
              </li>
            </ul>
          </div>

          <!-- 评论区 -->
          <div class="sidebar-card glass-card comments-card">
            <h3 class="sidebar-card__title">
              <MessageCircle :size="16" />
              评论 ({{ comments.length }})
            </h3>
            
            <!-- 评论输入 -->
            <div class="comment-form">
              <input 
                v-model="newComment.name" 
                type="text" 
                placeholder="昵称" 
                class="glass-input"
              />
              <textarea 
                v-model="newComment.content" 
                placeholder="写下你的评论..." 
                class="glass-input"
                rows="3"
              ></textarea>
              <button class="btn-submit-small" @click="submitComment">
                <Send :size="14" />
                发表
              </button>
            </div>

            <!-- 评论列表 -->
            <div class="comments-list-sidebar">
              <div v-if="comments.length === 0" class="no-comments-small">
                <MessageCircle :size="32" />
                <p>暂无评论</p>
              </div>
              <div v-else class="comment-items">
                <div v-for="comment in comments.slice(0, 3)" :key="comment.id" class="comment-item-small">
                  <div class="comment-header-small">
                    <span class="comment-author-small">{{ comment.name }}</span>
                    <span class="comment-date-small">{{ formatCommentDate(comment.createdAt) }}</span>
                  </div>
                  <p class="comment-text-small">{{ comment.content }}</p>
                  <div class="comment-actions-small">
                    <button class="comment-action-small" @click="likeComment(comment.id)">
                      <Heart :size="12" />
                      {{ comment.likes || 0 }}
                    </button>
                    <button class="comment-action-small" @click="replyComment(comment.id)">
                      <MessageCircle :size="12" />
                      回复
                    </button>
                  </div>
                </div>
              </div>
            </div>
            
            <button v-if="comments.length > 3" class="view-all-btn" @click="toggleAllComments">
              {{ showAllComments ? '收起评论' : `查看全部 ${comments.length} 条评论` }}
            </button>
          </div>
        </aside>

        <!-- 全部评论区（展开时显示） -->
        <section v-if="showAllComments && comments.length > 5" id="all-comments" class="all-comments-section glass-card">
          <h2 class="all-comments-title">
            <MessageCircle :size="24" />
            全部评论 ({{ comments.length }})
          </h2>
          
          <div class="all-comments-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-avatar">
                <User :size="24" />
              </div>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-author">{{ comment.name }}</span>
                  <span class="comment-date">{{ formatCommentDate(comment.createdAt) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <div class="comment-footer">
                  <button class="comment-action" @click="likeComment(comment.id)">
                    <Heart :size="14" />
                    <span>{{ comment.likes || 0 }}</span>
                  </button>
                  <button class="comment-action" @click="replyComment(comment.id)">
                    <MessageCircle :size="14" />
                    <span>回复</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </main>
    
    <AppFooter />

    <!-- 返回顶部按钮 -->
    <transition name="fade">
      <button 
        v-if="showBackToTop" 
        class="back-to-top"
        @click="scrollToTop"
        title="返回顶部"
      >
        <ArrowUp :size="24" />
      </button>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, Calendar, Clock, Eye, FileText, Heart, Share2, Bookmark, ArrowUp, ArrowLeft, List, Link, BarChart3, MessageCircle, Send } from 'lucide-vue-next'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import type { Article } from '@/types/article'

const route = useRoute()
const router = useRouter()
const article = ref<Article | null>(null)
const loading = ref(true)
const tableOfContents = ref<Array<{ id: string; text: string; level: number }>>([])
const relatedArticles = ref<Article[]>([])
const showBackToTop = ref(false)
const readingProgress = ref(0)

// 评论相关
interface Comment {
  id: string
  name: string
  email?: string
  content: string
  createdAt: string
  likes: number
}

const comments = ref<Comment[]>([
  {
    id: '1',
    name: '小明',
    content: '写得很好，学到了很多！期待更多这样的文章。',
    createdAt: '2024-12-16T10:30:00',
    likes: 5
  },
  {
    id: '2',
    name: '技术爱好者',
    content: '文章深入浅出，对前端工程化的理解很到位。特别是关于性能优化的部分，很有启发。',
    createdAt: '2024-12-16T14:20:00',
    likes: 3
  }
])

const newComment = ref({
  name: '',
  email: '',
  content: ''
})

// 格式化评论日期
const formatCommentDate = (date: string) => {
  const d = new Date(date)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes} 分钟前`
  if (hours < 24) return `${hours} 小时前`
  if (days < 7) return `${days} 天前`
  return d.toLocaleDateString('zh-CN', { month: 'long', day: 'numeric' })
}

// 提交评论
const submitComment = () => {
  if (!newComment.value.name.trim() || !newComment.value.content.trim()) {
    alert('请填写昵称和评论内容')
    return
  }

  const comment: Comment = {
    id: Date.now().toString(),
    name: newComment.value.name,
    email: newComment.value.email,
    content: newComment.value.content,
    createdAt: new Date().toISOString(),
    likes: 0
  }

  comments.value.unshift(comment)
  
  // 清空表单
  newComment.value = {
    name: '',
    email: '',
    content: ''
  }
}

// 点赞评论
const likeComment = (commentId: string) => {
  const comment = comments.value.find(c => c.id === commentId)
  if (comment) {
    comment.likes = (comment.likes || 0) + 1
  }
}

// 回复评论
const replyingTo = ref<string | null>(null)
const replyComment = (commentId: string) => {
  replyingTo.value = commentId
  const comment = comments.value.find(c => c.id === commentId)
  if (comment) {
    newComment.value.content = `@${comment.name} `
  }
}

// 查看全部评论
const showAllComments = ref(false)
const toggleAllComments = () => {
  showAllComments.value = !showAllComments.value
  if (showAllComments.value) {
    // 滚动到全部评论区
    setTimeout(() => {
      document.getElementById('all-comments')?.scrollIntoView({ behavior: 'smooth' })
    }, 100)
  }
}

// 格式化日期
const formatDate = (date: string) => {
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

// 提取目录
const extractTableOfContents = (content: string) => {
  const parser = new DOMParser()
  const doc = parser.parseFromString(content, 'text/html')
  const headings = doc.querySelectorAll('h1, h2, h3, h4')
  
  return Array.from(headings).map((heading, index) => ({
    id: `heading-${index}`,
    text: heading.textContent || '',
    level: parseInt(heading.tagName.substring(1))
  }))
}

// 加载文章数据
const loadArticle = async () => {
  loading.value = true
  const id = route.params.id
  
  try {
    // TODO: 调用 API 获取文章详情
    // const response = await getArticleById(id)
    // article.value = response.data
    
    // 模拟数据
    setTimeout(() => {
      article.value = {
        id: id as string,
        title: '探索现代前端开发的艺术与科学',
        category: '技术分享',
        cover: 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=1200&h=600&fit=crop',
        author: '落叶无痕',
        authorAvatar: '',
        authorBio: '全栈开发工程师，热爱技术分享与开源贡献',
        createdAt: '2024-12-15',
        readTime: '8',
        views: 1234,
        likes: 89,
        comments: 23,
        shares: 15,
        summary: '本文深入探讨了现代前端开发的核心理念，从组件化设计到性能优化，从用户体验到工程化实践，带你领略前端开发的魅力。',
        tags: ['Vue3', 'TypeScript', '前端工程化', '性能优化'],
        content: `
          <p class="drop-cap">在当今快速发展的互联网时代，前端开发已经从简单的页面制作演变成了一门复杂而精妙的工程学科。它不仅需要扎实的技术功底，更需要对用户体验的深刻理解和对性能优化的不懈追求。</p>
          
          <h2>组件化设计的哲学</h2>
          <p>组件化是现代前端开发的基石。通过将复杂的界面拆分成可复用的独立单元，我们不仅提高了代码的可维护性，更建立了一套清晰的开发范式。</p>
          
          <blockquote class="magazine-quote">
            <p>"好的组件设计就像乐高积木，每一块都有其独特的功能，但又能完美地组合在一起。"</p>
            <cite>— 前端架构师的智慧</cite>
          </blockquote>
          
          <h2>性能优化的艺术</h2>
          <p>性能优化是一个永恒的话题。从代码分割到懒加载，从虚拟滚动到防抖节流，每一个细节都可能影响用户的体验。</p>
          
          <div class="highlight-box">
            <h3>关键性能指标</h3>
            <ul>
              <li><strong>FCP (First Contentful Paint)</strong>: 首次内容绘制时间</li>
              <li><strong>LCP (Largest Contentful Paint)</strong>: 最大内容绘制时间</li>
              <li><strong>TTI (Time to Interactive)</strong>: 可交互时间</li>
              <li><strong>CLS (Cumulative Layout Shift)</strong>: 累积布局偏移</li>
            </ul>
          </div>
          
          <h2>用户体验的追求</h2>
          <p>技术最终是为用户服务的。一个优秀的前端开发者不仅要写出高质量的代码，更要站在用户的角度思考问题，创造流畅、直观、令人愉悦的交互体验。</p>
          
          <p>从微交互到动画效果，从响应式设计到无障碍访问，每一个细节都体现着对用户的尊重和关怀。</p>
          
          <h2>工程化实践</h2>
          <p>随着项目规模的增长，工程化变得越来越重要。构建工具、代码规范、自动化测试、持续集成，这些都是保证项目质量和团队协作效率的关键。</p>
          
          <p>现代前端开发已经不再是单打独斗，而是需要整个团队的协作和配合。良好的工程化实践能够让团队更加高效地工作。</p>
        `
      } as Article
      
      if (article.value?.content) {
        tableOfContents.value = extractTableOfContents(article.value.content)
      }
      
      // 模拟相关文章
      relatedArticles.value = [
        {
          id: '2',
          title: 'Vue 3 组合式 API 最佳实践',
          createdAt: '2024-12-10',
          cover: ''
        },
        {
          id: '3',
          title: 'TypeScript 类型体操进阶',
          createdAt: '2024-12-08',
          cover: ''
        },
        {
          id: '4',
          title: '前端性能优化实战指南',
          createdAt: '2024-12-05',
          cover: ''
        }
      ] as Article[]
      
      loading.value = false
    }, 800)
  } catch (error) {
    console.error('加载文章失败:', error)
    loading.value = false
  }
}

// 监听滚动事件
const handleScroll = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  const windowHeight = window.innerHeight
  const documentHeight = document.documentElement.scrollHeight
  
  showBackToTop.value = scrollTop > 300
  
  // 计算阅读进度
  const scrollableHeight = documentHeight - windowHeight
  if (scrollableHeight > 0) {
    readingProgress.value = Math.min((scrollTop / scrollableHeight) * 100, 100)
  }
}

// 返回顶部
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

// 返回上一页
const goBack = () => {
  router.back()
}

onMounted(() => {
  loadArticle()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.article-detail__main {
  width: 100%;
  padding: var(--spacing-2xl) 2vw;
}

.article-content {
  padding: var(--spacing-2xl);
  max-width: 800px;
}

.article-content__title {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--spacing-lg);
  background: linear-gradient(135deg, var(--color-pink-500), var(--color-blue-500));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.article-content__meta {
  display: flex;
  gap: var(--spacing-md);
  color: var(--color-gray-500);
  font-size: var(--text-sm);
  margin-bottom: var(--spacing-xl);
}

.article-content__body {
  line-height: 1.8;
  color: var(--color-gray-700);
}

.loading {
  text-align: center;
  padding: var(--spacing-3xl);
  color: var(--color-gray-500);
}
</style>

<style scoped>
/* 主布局 */
.article-detail__main {
  width: 100%;
  margin: 0 auto;
  padding: 40px 0;
  min-height: calc(100vh - 200px);
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-3xl);
  color: var(--color-gray-500);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--color-gray-200);
  border-top-color: var(--color-miku-500);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 双栏布局 */
.article-layout {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px 0 24px;
  display: grid !important;
  grid-template-columns: minmax(0, 780px) 320px !important;
  gap: 32px;
  align-items: start;
}

/* 强制对齐 - 移除任何可能的偏移 */
.article-layout > * {
  margin-top: 0 !important;
  margin-bottom: 0 !important;
}

/* 明确指定文章内容和侧边栏的位置 */
.article-layout > .article-content {
  grid-column: 1;
  grid-row: 1;
  align-self: start;
}

.article-layout > .article-sidebar {
  grid-column: 2;
  grid-row: 1;
  align-self: start;
}

.article-layout > .all-comments-section {
  grid-column: 1;
  grid-row: 2;
}

/* 文章内容 - 主栏 */
.article-content {
  grid-column: 1;
  padding: 24px 40px 40px 40px;
  min-height: 600px;
  background: white;
  border: 1px solid var(--color-gray-200);
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  box-sizing: border-box;
  position: relative;
  top: 0;
}

/* 移除玻璃效果 */
.article-content.glass-card {
  margin: 0;
  background: white !important;
  backdrop-filter: none !important;
  -webkit-backdrop-filter: none !important;
}

.article-content.glass-card:hover {
  transform: none;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

/* 文章头部 */
.article-header {
  margin-top: 0;
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--color-gray-200);
}

.article-category {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  background: var(--color-miku-50);
  color: var(--color-miku-600);
  border-radius: 4px;
  font-size: 13px;
  font-weight: var(--font-medium);
  margin-bottom: var(--spacing-md);
}

.article-category svg {
  stroke-width: 2;
  width: 14px;
  height: 14px;
}

.article-title {
  font-size: 28px;
  font-weight: var(--font-bold);
  line-height: 1.4;
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-md);
  letter-spacing: -0.02em;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
  color: var(--color-gray-500);
  font-size: 13px;
}

.article-meta .meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.article-meta .meta-item svg {
  color: var(--color-gray-400);
  stroke-width: 2;
  width: 14px;
  height: 14px;
}

/* 侧边栏 - 右侧固定（在下面有完整定义，这里移除重复） */

/* 全部评论区 - 主栏第二行（可选） */
.all-comments-section {
  grid-column: 1;
  padding: 40px;
  background: white;
  border: 1px solid var(--color-gray-200);
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

/* 移除玻璃效果 */
.all-comments-section.glass-card {
  background: white !important;
  backdrop-filter: none !important;
  -webkit-backdrop-filter: none !important;
}

.all-comments-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin-bottom: 24px;
}

.all-comments-title svg {
  color: var(--color-miku-500);
  stroke-width: 2;
}

.all-comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 文章摘要 */
.article-summary {
  display: flex;
  gap: 12px;
  padding: 16px;
  margin-bottom: var(--spacing-xl);
  background: #f0f9ff;
  border-left: 3px solid var(--color-miku-500);
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.7;
  color: var(--color-gray-700);
}

.summary-icon {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 2px;
  flex-shrink: 0;
  color: var(--color-miku-500);
}

.summary-icon svg {
  stroke-width: 2;
  width: 20px;
  height: 20px;
}

.article-summary p {
  margin: 0;
}

/* 文章正文 - 杂志风格 */
.article-body {
  font-size: 16px;
  line-height: 1.8;
  color: var(--color-gray-800);
  max-width: 820px;
}

/* 首字下沉效果 - 初音绿渐变 */
.article-body :deep(.drop-cap::first-letter) {
  float: left;
  font-size: 4em;
  line-height: 0.9;
  margin: 0.05em 0.15em 0 0;
  font-weight: var(--font-bold);
  color: var(--color-miku-600);
}

.article-body :deep(h2) {
  font-size: 22px;
  font-weight: var(--font-bold);
  margin-top: 48px;
  margin-bottom: 16px;
  color: var(--color-gray-900);
  position: relative;
  padding-left: 0;
  letter-spacing: -0.01em;
}

.article-body :deep(h2::before) {
  display: none;
}

.article-body :deep(h3) {
  font-size: 18px;
  font-weight: var(--font-semibold);
  margin-top: 32px;
  margin-bottom: 12px;
  color: var(--color-gray-800);
}

.article-body :deep(p) {
  margin-bottom: 20px;
  text-align: justify;
}

/* 杂志风格引用块 */
.article-body :deep(.magazine-quote) {
  position: relative;
  margin: 32px 0;
  padding: 24px 32px;
  background: var(--color-gray-50);
  border-left: 3px solid var(--color-miku-500);
  border-radius: 0 6px 6px 0;
  font-size: 16px;
  font-style: italic;
  line-height: 1.7;
}

.article-body :deep(.magazine-quote::before) {
  content: '"';
  position: absolute;
  top: 10px;
  left: 12px;
  font-size: 48px;
  font-family: Georgia, serif;
  color: var(--color-miku-300);
  opacity: 0.5;
  line-height: 1;
}

.article-body :deep(.magazine-quote p) {
  margin: 0;
  padding-left: 24px;
  color: var(--color-gray-700);
}

.article-body :deep(.magazine-quote cite) {
  display: block;
  margin-top: 12px;
  padding-left: 24px;
  font-size: 14px;
  font-style: normal;
  color: var(--color-gray-500);
}

/* 高亮框 */
.article-body :deep(.highlight-box) {
  margin: 24px 0;
  padding: 20px;
  background: #f0f9ff;
  border-radius: 6px;
  border: 1px solid var(--color-miku-200);
}

.article-body :deep(.highlight-box h3) {
  margin-top: 0;
  margin-bottom: 12px;
  font-size: 16px;
  color: var(--color-miku-600);
}

.article-body :deep(.highlight-box ul) {
  margin: 12px 0 0 0;
  padding-left: 24px;
}

.article-body :deep(.highlight-box li) {
  margin-bottom: 8px;
  line-height: 1.7;
}

.article-body :deep(.highlight-box strong) {
  color: var(--color-miku-600);
  font-weight: var(--font-semibold);
}

/* 标签 */
.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  margin-top: 48px;
  padding-top: 24px;
  border-top: 1px solid var(--color-gray-200);
}

.tags-label {
  font-weight: var(--font-medium);
  font-size: 14px;
  color: var(--color-gray-600);
}

.tag {
  padding: 4px 12px;
  background: var(--color-miku-50);
  color: var(--color-miku-600);
  border-radius: 4px;
  font-size: 13px;
  font-weight: var(--font-medium);
  transition: all var(--transition-fast);
  cursor: pointer;
}

.tag:hover {
  background: var(--color-miku-100);
  color: var(--color-miku-700);
}

/* 文章操作按钮 */
.article-actions {
  display: flex;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid var(--color-gray-200);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: white;
  border: 1px solid var(--color-gray-200);
  border-radius: 6px;
  font-size: 14px;
  font-weight: var(--font-medium);
  color: var(--color-gray-700);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.action-btn:hover {
  background: var(--color-miku-50);
  color: var(--color-miku-600);
  border-color: var(--color-miku-300);
}

.action-btn svg {
  flex-shrink: 0;
  stroke-width: 2;
  width: 16px;
  height: 16px;
}

/* 侧边栏 - 固定不滚动 */
.article-sidebar {
  grid-column: 2;
  grid-row: 1;
  position: sticky;
  top: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-self: start;
  margin-top: 0;
}

.sidebar-card {
  padding: 24px 18px 18px 18px;
  transition: all var(--transition-base);
  box-sizing: border-box;
  background: white;
  border: 1px solid var(--color-gray-200);
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

/* 移除玻璃效果 */
.sidebar-card.glass-card {
  background: white !important;
  backdrop-filter: none !important;
  -webkit-backdrop-filter: none !important;
}

.sidebar-card:hover {
  transform: none;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.sidebar-card.glass-card:hover {
  transform: none;
}

.sidebar-card__title {
  font-size: 15px;
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--color-gray-200);
}

.sidebar-card__title svg {
  color: var(--color-miku-500);
  flex-shrink: 0;
  stroke-width: 2;
  width: 16px;
  height: 16px;
}

/* 目录 - 紧凑样式 */
.toc-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.toc-item {
  margin-bottom: 6px;
}

.toc-level-2 {
  padding-left: 0;
}

.toc-level-3 {
  padding-left: 12px;
}

.toc-level-4 {
  padding-left: 24px;
}

.toc-link {
  display: block;
  padding: 6px 8px;
  color: var(--color-gray-600);
  text-decoration: none;
  font-size: 13px;
  border-left: 2px solid transparent;
  transition: all 0.2s ease;
  line-height: 1.5;
  border-radius: 4px;
}

.toc-link:hover {
  color: var(--color-miku-600);
  border-left-color: var(--color-miku-500);
  background: var(--color-miku-50);
}

/* 相关文章 - 紧凑列表 */
.related-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.related-item {
  margin-bottom: 8px;
}

.related-item:last-child {
  margin-bottom: 0;
}

.related-link {
  display: block;
  padding: 10px;
  text-decoration: none;
  border-radius: 6px;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.related-link:hover {
  background: var(--color-miku-50);
  border-color: var(--color-miku-200);
}

.related-info {
  width: 100%;
}

.related-title {
  font-size: 13px;
  font-weight: var(--font-medium);
  color: var(--color-gray-800);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.5;
}

.related-link:hover .related-title {
  color: var(--color-miku-600);
}

.related-date {
  font-size: 11px;
  color: var(--color-gray-500);
}

/* 统计卡片 - 紧凑网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.stat-item {
  text-align: center;
  padding: 12px 8px;
  background: var(--color-gray-50);
  border-radius: 6px;
  transition: all var(--transition-fast);
}

.stat-item:hover {
  background: var(--color-miku-50);
}

.stat-value {
  font-size: 20px;
  font-weight: var(--font-bold);
  color: var(--color-miku-600);
  margin-bottom: 2px;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: var(--color-gray-600);
}

/* 侧边栏评论区样式 */
.comments-card {
  /* 移除额外的 margin-top */
}

.comment-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: var(--spacing-md);
}

.comment-form .glass-input {
  font-size: 13px;
  padding: 8px 10px;
  border: 1px solid var(--color-gray-200);
  border-radius: 6px;
  background: white;
  transition: all var(--transition-fast);
}

.comment-form .glass-input:focus {
  outline: none;
  border-color: var(--color-miku-400);
  box-shadow: 0 0 0 3px rgba(57, 197, 187, 0.1);
}

.comment-form textarea {
  resize: vertical;
  min-height: 60px;
  font-family: inherit;
}

.btn-submit-small {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 16px;
  background: var(--color-miku-500);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-submit-small:hover {
  background: var(--color-miku-600);
  transform: translateY(-1px);
}

.btn-submit-small svg {
  stroke-width: 2;
  width: 14px;
  height: 14px;
}

.comments-list-sidebar {
  margin-top: 12px;
  max-height: 400px;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: var(--color-miku-300) transparent;
}

.comments-list-sidebar::-webkit-scrollbar {
  width: 4px;
}

.comments-list-sidebar::-webkit-scrollbar-track {
  background: transparent;
}

.comments-list-sidebar::-webkit-scrollbar-thumb {
  background: var(--color-miku-300);
  border-radius: 2px;
}

.comments-list-sidebar::-webkit-scrollbar-thumb:hover {
  background: var(--color-miku-400);
}

.no-comments-small {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 24px;
  color: var(--color-gray-400);
  text-align: center;
}

.no-comments-small svg {
  color: var(--color-gray-300);
}

.no-comments-small p {
  font-size: 13px;
  margin: 0;
}

.comment-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.comment-item-small {
  padding: 10px;
  background: white;
  border-radius: 6px;
  border: 1px solid var(--color-gray-200);
  transition: all var(--transition-fast);
}

.comment-item-small:hover {
  border-color: var(--color-miku-300);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.comment-header-small {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.comment-author-small {
  font-size: 13px;
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
}

.comment-date-small {
  font-size: 11px;
  color: var(--color-gray-500);
}

.comment-text-small {
  font-size: 13px;
  color: var(--color-gray-700);
  line-height: 1.6;
  margin: 0;
  word-wrap: break-word;
}

.comment-actions-small {
  display: flex;
  gap: 8px;
  margin-top: 6px;
}

.comment-action-small {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: transparent;
  border: none;
  color: var(--color-gray-500);
  font-size: 11px;
  cursor: pointer;
  border-radius: 4px;
  transition: all var(--transition-fast);
}

.comment-action-small:hover {
  color: var(--color-miku-600);
  background: var(--color-miku-50);
}

.comment-action-small svg {
  stroke-width: 2;
  width: 12px;
  height: 12px;
}

.view-all-btn {
  display: block;
  width: 100%;
  text-align: center;
  padding: 8px;
  margin-top: 12px;
  color: var(--color-miku-600);
  font-size: 13px;
  font-weight: var(--font-medium);
  background: white;
  border: 1px solid var(--color-gray-200);
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.view-all-btn:hover {
  background: var(--color-miku-50);
  color: var(--color-miku-600);
  border-color: var(--color-miku-300);
}

.comment-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: var(--color-gray-50);
  border: 1px solid var(--color-gray-200);
  border-radius: 8px;
  transition: all var(--transition-fast);
}

.comment-item:hover {
  background: white;
  border-color: var(--color-miku-200);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.comment-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--color-miku-100), var(--color-cyan-100));
  color: var(--color-miku-600);
  border-radius: 50%;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-sm);
}

.comment-author {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--color-gray-900);
}

.comment-date {
  font-size: var(--text-sm);
  color: var(--color-gray-500);
}

.comment-text {
  font-size: var(--text-base);
  color: var(--color-gray-700);
  line-height: 1.6;
  margin-bottom: var(--spacing-md);
  word-wrap: break-word;
}

.comment-footer {
  display: flex;
  gap: var(--spacing-lg);
}

.comment-action {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  background: transparent;
  border: none;
  color: var(--color-gray-600);
  font-size: var(--text-sm);
  cursor: pointer;
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
}

.comment-action:hover {
  color: var(--color-miku-500);
  background: var(--color-miku-50);
}

.comment-action svg {
  stroke-width: 2;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .article-layout {
    grid-template-columns: 1fr !important;
    padding: 0 20px;
  }

  .article-layout > .article-content,
  .article-layout > .article-sidebar,
  .article-layout > .all-comments-section {
    grid-column: 1 !important;
    grid-row: auto !important;
    margin-top: 16px;
  }

  .article-sidebar {
    position: static;
    max-height: none;
    overflow-y: visible;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .article-detail__main {
    padding: 24px 0;
  }

  .article-layout {
    padding: 0 16px;
  }

  .article-content {
    padding: 24px;
    margin-top: 16px;
  }

  .all-comments-section {
    padding: 24px;
  }

  .article-body :deep(.drop-cap::first-letter) {
    font-size: 3em;
  }

  .article-body :deep(.magazine-quote) {
    padding: 16px 20px;
    font-size: 15px;
  }

  .article-sidebar {
    grid-template-columns: 1fr;
    margin-top: 16px;
  }

  .article-actions {
    flex-wrap: wrap;
  }

  .action-btn {
    flex: 1;
    min-width: 120px;
    justify-content: center;
  }
}

/* 返回按钮 */
.back-button {
  position: fixed;
  top: 100px;
  left: 32px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: white;
  color: var(--color-gray-700);
  border: 1px solid var(--color-gray-200);
  border-radius: 8px;
  font-size: 14px;
  font-weight: var(--font-medium);
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all var(--transition-fast);
  z-index: var(--z-fixed);
}

.back-button svg {
  flex-shrink: 0;
  stroke-width: 2;
}

.back-button:hover {
  background: var(--color-miku-50);
  color: var(--color-miku-600);
  border-color: var(--color-miku-300);
  transform: translateX(-2px);
}

.back-button:active {
  transform: translateX(0);
}

/* 返回顶部按钮 - 与首页风格一致 */
.back-to-top {
  position: fixed;
  bottom: var(--spacing-2xl);
  right: var(--spacing-2xl);
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-miku-400), var(--color-cyan-400));
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(57, 197, 187, 0.3);
  transition: all var(--transition-base);
  z-index: var(--z-fixed);
}

.back-to-top svg {
  flex-shrink: 0;
  stroke-width: 2.5;
}

.back-to-top:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(57, 197, 187, 0.4);
}

.back-to-top:active {
  transform: translateY(-2px);
}

/* 淡入淡出过渡 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-base);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .back-button {
    top: 80px;
    left: 16px;
    padding: 8px 12px;
    font-size: 13px;
  }

  .back-button span {
    display: none;
  }

  .back-to-top {
    bottom: var(--spacing-xl);
    right: var(--spacing-lg);
    width: 44px;
    height: 44px;
  }
}

/* 阅读进度条 - 底部 */
.reading-progress {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--color-gray-200);
  z-index: var(--z-fixed);
}

.reading-progress__bar {
  height: 100%;
  background: linear-gradient(90deg, var(--color-miku-400), var(--color-cyan-400));
  transition: width 0.1s ease-out;
  box-shadow: 0 0 10px rgba(57, 197, 187, 0.5);
}
</style>
