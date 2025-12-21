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
            <div v-if="article.tags && article.tags.length > 0" class="article-category">
              <FileText :size="14" />
              {{ article.tags[0].name }}
            </div>
            <h1 class="article-title">{{ article.title }}</h1>
            <div class="article-meta">
              <span class="meta-item">
                <Calendar :size="14" />
                {{ formatDate(article.publishedAt || article.createdAt || '') }}
              </span>
              <span class="meta-item">
                <Clock :size="14" />
                {{ calculateReadTime(article.content) }}分钟
              </span>
              <span class="meta-item">
                <Eye :size="14" />
                {{ article.viewCount || 0 }}
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
          <div ref="articleBodyRef" class="article-body"></div>

          <!-- 标签 -->
          <div v-if="article.tags && article.tags.length" class="article-tags">
            <span class="tags-label">标签：</span>
            <span v-for="tag in article.tags" :key="tag.id" class="tag">
              #{{ tag.name }}
            </span>
          </div>

          <!-- 文章底部操作 -->
          <div class="article-actions">
            <button 
              :class="['action-btn', 'like-btn', { 'active': articleStatus.isLiked }]"
              @click="toggleLike"
              :disabled="!isLoggedIn"
            >
              <Heart :size="18" :fill="articleStatus.isLiked ? 'currentColor' : 'none'" />
              <span>{{ article.likeCount || 0 }}</span>
            </button>
            <button class="action-btn share-btn" @click="shareArticle">
              <Share2 :size="18" />
              <span>分享</span>
            </button>
            <button 
              :class="['action-btn', 'bookmark-btn', { 'active': articleStatus.isBookmarked }]"
              @click="toggleBookmark"
              :disabled="!isLoggedIn"
            >
              <Bookmark :size="18" :fill="articleStatus.isBookmarked ? 'currentColor' : 'none'" />
              <span>{{ articleStatus.isBookmarked ? '已收藏' : '收藏' }}</span>
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
                <div class="stat-value">{{ article.viewCount || 0 }}</div>
                <div class="stat-label">浏览</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ article.likeCount || 0 }}</div>
                <div class="stat-label">点赞</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ article.commentCount || 0 }}</div>
                <div class="stat-label">评论</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">0</div>
                <div class="stat-label">分享</div>
              </div>
            </div>
          </div>

          <!-- 目录 -->
          <div v-if="tableOfContents.length > 0" class="sidebar-card glass-card toc-card">
            <h3 class="sidebar-card__title">
              <List :size="16" />
              目录
            </h3>
            <nav class="toc">
              <ul class="toc-list">
                <li v-for="(heading, index) in tableOfContents" :key="index" 
                    :class="['toc-item', `toc-level-${heading.level}`, { 'toc-active': activeHeadingId === heading.id }]">
                  <a :href="`#${heading.id}`" class="toc-link" @click.prevent="scrollToHeading(heading.id)">
                    {{ heading.text }}
                  </a>
                </li>
              </ul>
            </nav>
          </div>
          <div v-else class="sidebar-card glass-card toc-card">
            <h3 class="sidebar-card__title">
              <List :size="16" />
              目录
            </h3>
            <div class="empty-state">
              <FileText :size="32" class="empty-icon" />
              <p class="empty-text">暂无目录</p>
            </div>
          </div>

          <!-- 相关文章 -->
          <div class="sidebar-card glass-card related-card">
            <h3 class="sidebar-card__title">
              <Link :size="16" />
              相关文章
            </h3>
            <ul v-if="relatedArticles.length > 0" class="related-list">
              <li v-for="related in relatedArticles" :key="related.id" class="related-item">
                <a :href="`/article/${related.id}`" class="related-link">
                  <div class="related-info">
                    <h4 class="related-title">{{ related.title }}</h4>
                    <p class="related-date">{{ formatDate(related.publishedAt || related.createdAt || '') }}</p>
                  </div>
                </a>
              </li>
            </ul>
            <div v-else class="empty-state">
              <Link :size="32" class="empty-icon" />
              <p class="empty-text">暂无相关文章</p>
            </div>
          </div>

          <!-- 评论区 -->
          <div class="sidebar-card glass-card comments-card">
            <h3 class="sidebar-card__title">
              <MessageCircle :size="16" />
              评论 ({{ comments.length }})
            </h3>
            
            <!-- 评论输入 -->
            <div class="comment-form">
              <textarea 
                v-model="newComment.content" 
                placeholder="写下你的评论..." 
                class="glass-input"
                rows="3"
              ></textarea>
              <div class="comment-form-footer">
                <label v-if="isLoggedIn" class="anonymous-checkbox">
                  <input type="checkbox" v-model="newComment.anonymous" />
                  <span>匿名评论</span>
                </label>
                <button class="btn-submit-small" @click="submitComment">
                  <Send :size="14" />
                  发表
                </button>
              </div>
            </div>

            <!-- 评论列表 -->
            <div class="comments-list-sidebar">
              <div v-if="comments.length === 0" class="no-comments-small">
                <MessageCircle :size="32" />
                <p>暂无评论</p>
              </div>
              <div v-else class="comment-items">
                <div v-for="comment in comments.slice(0, 3)" :key="comment.id" class="comment-item-small">
                  <div class="comment-avatar-small">
                    <img v-if="comment.avatar" :src="comment.avatar" :alt="(comment as any).name" />
                    <User v-else :size="16" />
                  </div>
                  <div class="comment-content-small">
                    <div class="comment-header-small">
                      <span class="comment-author-small">{{ (comment as any).name }}</span>
                      <span class="comment-date-small">{{ formatCommentDate(comment.createdAt || '') }}</span>
                    </div>
                    <p class="comment-text-small">{{ comment.content }}</p>
                    <div class="comment-actions-small">
                      <button 
                        :class="['comment-action-small', { 'comment-action-liked': isLiked(comment) }]" 
                        @click="likeComment(comment.id!)"
                      >
                        <Heart :size="12" :fill="isLiked(comment) ? 'currentColor' : 'none'" />
                        {{ comment.likeCount || 0 }}
                      </button>
                      <button class="comment-action-small" @click="replyComment(comment.id!)">
                        <MessageCircle :size="12" />
                        回复
                      </button>
                      <button 
                        v-if="canDeleteComment(comment)" 
                        class="comment-action-small comment-action-delete" 
                        @click="deleteComment(comment.id!)"
                      >
                        <Trash2 :size="12" />
                        删除
                      </button>
                    </div>
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
                <img v-if="comment.avatar" :src="comment.avatar" :alt="(comment as any).name" />
                <User v-else :size="24" />
              </div>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-author">{{ (comment as any).name }}</span>
                  <span class="comment-date">{{ formatCommentDate(comment.createdAt || '') }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <div class="comment-footer">
                  <button 
                    :class="['comment-action', { 'comment-action-liked': isLiked(comment) }]" 
                    @click="likeComment(comment.id!)"
                  >
                    <Heart :size="14" :fill="isLiked(comment) ? 'currentColor' : 'none'" />
                    <span>{{ comment.likeCount || 0 }}</span>
                  </button>
                  <button class="comment-action" @click="replyComment(comment.id!)">
                    <MessageCircle :size="14" />
                    <span>回复</span>
                  </button>
                  <button 
                    v-if="canDeleteComment(comment)" 
                    class="comment-action comment-action-delete" 
                    @click="deleteComment(comment.id!)"
                  >
                    <Trash2 :size="14" />
                    <span>删除</span>
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
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, Calendar, Clock, Eye, FileText, Heart, Share2, Bookmark, ArrowUp, ArrowLeft, List, Link, BarChart3, MessageCircle, Send, Trash2 } from 'lucide-vue-next'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import { useUserStore } from '@/stores/user'
import { getArticleDetail, getArticleList, getArticleStatus, likeArticle, unlikeArticle, bookmarkArticle, unbookmarkArticle } from '@/api/article'
import { getArticleComments, createComment, likeComment as apiLikeComment, unlikeComment as apiUnlikeComment, deleteComment as apiDeleteComment, type Comment } from '@/api/comment'
import { message } from '@/utils/message'
import type { Article as ArticleType } from '@/api/article'
import Vditor from 'vditor'
import 'vditor/dist/index.css'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const article = ref<ArticleType | null>(null)
const loading = ref(true)
const tableOfContents = ref<Array<{ id: string; text: string; level: number }>>([])
const relatedArticles = ref<ArticleType[]>([])
const showBackToTop = ref(false)
const readingProgress = ref(0)
const articleBodyRef = ref<HTMLElement | null>(null)
const activeHeadingId = ref<string>('')

// 用户登录状态
const isLoggedIn = computed(() => userStore.isLoggedIn)
const currentUser = computed(() => userStore.user)

// 文章状态（点赞、收藏）
const articleStatus = ref({
  isLiked: false,
  isBookmarked: false
})

// 加载文章状态
const loadArticleStatus = async () => {
  if (!article.value?.id || !isLoggedIn.value) return
  
  try {
    const status = await getArticleStatus(article.value.id)
    articleStatus.value = status
  } catch (error) {
    console.error('加载文章状态失败:', error)
  }
}

// 切换点赞
const toggleLike = async () => {
  if (!isLoggedIn.value) {
    message.warning('请先登录')
    return
  }
  
  if (!article.value?.id) return
  
  try {
    if (articleStatus.value.isLiked) {
      // 取消点赞
      await unlikeArticle(article.value.id)
      articleStatus.value.isLiked = false
      if (article.value.likeCount) {
        article.value.likeCount = Math.max(0, article.value.likeCount - 1)
      }
      message.success('已取消点赞')
    } else {
      // 点赞
      await likeArticle(article.value.id)
      articleStatus.value.isLiked = true
      article.value.likeCount = (article.value.likeCount || 0) + 1
      message.success('点赞成功')
    }
  } catch (error: any) {
    console.error('点赞操作失败:', error)
    message.error(error.message || '操作失败')
  }
}

// 切换收藏
const toggleBookmark = async () => {
  if (!isLoggedIn.value) {
    message.warning('请先登录')
    return
  }
  
  if (!article.value?.id) return
  
  try {
    if (articleStatus.value.isBookmarked) {
      // 取消收藏
      await unbookmarkArticle(article.value.id)
      articleStatus.value.isBookmarked = false
      message.success('已取消收藏')
    } else {
      // 收藏
      await bookmarkArticle(article.value.id)
      articleStatus.value.isBookmarked = true
      message.success('收藏成功')
    }
  } catch (error: any) {
    console.error('收藏操作失败:', error)
    message.error(error.message || '操作失败')
  }
}

// 分享文章
const shareArticle = () => {
  if (navigator.share) {
    navigator.share({
      title: article.value?.title,
      text: article.value?.summary,
      url: window.location.href
    }).catch(err => console.log('分享失败:', err))
  } else {
    // 复制链接到剪贴板
    navigator.clipboard.writeText(window.location.href)
      .then(() => message.success('链接已复制到剪贴板'))
      .catch(() => message.error('复制失败'))
  }
}

// 评论相关
const comments = ref<Comment[]>([])
const commentLikes = ref<Set<number>>(new Set()) // 记录用户点赞的评论ID

const newComment = ref({
  content: '',
  anonymous: false
})

// 加载评论列表
const loadComments = async () => {
  if (!article.value?.id) return
  
  try {
    const response = await getArticleComments(article.value.id)
    comments.value = response.comments.map(c => ({
      ...c,
      name: c.userName || c.guestName || '匿名用户',
      avatar: c.userAvatar
    })) as any
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

// 格式化评论日期
const formatCommentDate = (date: string) => {
  if (!date) return 'Invalid Date'
  
  try {
    const d = new Date(date)
    
    // 检查日期是否有效
    if (isNaN(d.getTime())) {
      return 'Invalid Date'
    }
    
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
  } catch (error) {
    console.error('日期格式化错误:', error, date)
    return 'Invalid Date'
  }
}

// 提交评论
const submitComment = async () => {
  if (!newComment.value.content.trim()) {
    message.warning('请填写评论内容')
    return
  }

  try {
    const commentData: any = {
      articleId: article.value?.id,
      content: newComment.value.content
    }

    // 如果未登录或选择匿名，需要提供游客信息
    if (!isLoggedIn.value || newComment.value.anonymous) {
      // 这里可以弹出对话框让用户输入昵称和邮箱
      // 暂时使用默认值
      commentData.guestName = '匿名用户'
    }

    await createComment(commentData)
    message.success('评论成功')
    
    // 更新文章评论数
    if (article.value) {
      article.value.commentCount = (article.value.commentCount || 0) + 1
    }
    
    // 清空表单
    newComment.value = {
      content: '',
      anonymous: false
    }
    
    // 重新加载评论列表
    await loadComments()
  } catch (error: any) {
    console.error('评论失败:', error)
    message.error(error.message || '评论失败')
  }
}

// 点赞评论
const likeComment = async (commentId: number) => {
  try {
    if (commentLikes.value.has(commentId)) {
      // 取消点赞
      await apiUnlikeComment(commentId)
      commentLikes.value.delete(commentId)
      
      // 更新本地数据
      const comment = comments.value.find(c => c.id === commentId)
      if (comment && comment.likeCount) {
        comment.likeCount = Math.max(0, comment.likeCount - 1)
      }
    } else {
      // 点赞
      await apiLikeComment(commentId)
      commentLikes.value.add(commentId)
      
      // 更新本地数据
      const comment = comments.value.find(c => c.id === commentId)
      if (comment) {
        comment.likeCount = (comment.likeCount || 0) + 1
      }
    }
  } catch (error) {
    console.error('点赞失败:', error)
    message.error('操作失败')
  }
}

// 检查是否已点赞
const isLiked = (comment: Comment) => {
  return comment.id ? commentLikes.value.has(comment.id) : false
}

// 删除评论
const deleteComment = async (commentId: number) => {
  if (!confirm('确定要删除这条评论吗？')) {
    return
  }
  
  try {
    await apiDeleteComment(commentId)
    message.success('删除成功')
    
    // 更新文章评论数
    if (article.value && article.value.commentCount && article.value.commentCount > 0) {
      article.value.commentCount = article.value.commentCount - 1
    }
    
    // 重新加载评论列表
    await loadComments()
  } catch (error: any) {
    console.error('删除失败:', error)
    message.error(error.message || '删除失败')
  }
}

// 检查是否可以删除评论
const canDeleteComment = (comment: Comment) => {
  if (!isLoggedIn.value) return false
  
  // 管理员可以删除任何评论
  if (currentUser.value?.role === 1) return true
  
  // 匿名评论（没有 userId）只能管理员删除
  if (!comment.userId) return false
  
  // 用户只能删除自己的评论
  return comment.userId === currentUser.value?.id
}

// 回复评论
const replyingTo = ref<number | null>(null)
const replyComment = (commentId: number) => {
  replyingTo.value = commentId
  const comment = comments.value.find(c => c.id === commentId)
  if (comment) {
    const name = (comment as any).name || '用户'
    newComment.value.content = `@${name} `
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
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

// 计算阅读时间（基于字数）
const calculateReadTime = (content: string) => {
  if (!content) return 5
  // 移除HTML标签
  const text = content.replace(/<[^>]*>/g, '')
  // 中文字符数 + 英文单词数
  const chineseChars = text.match(/[\u4e00-\u9fa5]/g)?.length || 0
  const englishWords = text.match(/[a-zA-Z]+/g)?.length || 0
  const totalChars = chineseChars + englishWords
  // 假设每分钟阅读300字
  return Math.max(1, Math.ceil(totalChars / 300))
}

// 渲染Markdown内容
const renderMarkdown = async (content: string) => {
  if (!articleBodyRef.value) return
  
  try {
    console.log('渲染Markdown内容，长度:', content.length)
    console.log('articleBodyRef:', articleBodyRef.value)
    
    // 使用 Vditor.preview 渲染
    Vditor.preview(articleBodyRef.value, content, {
      cdn: 'https://unpkg.com/vditor@3.11.2',
      mode: 'light',
      hljs: { 
        style: 'github',
        enable: true,
        lineNumber: false
      },
      markdown: {
        toc: true,
        mark: true,
        footnotes: true,
        autoSpace: true
      },
      after: () => {
        console.log('Vditor.preview 渲染完成')
        // 渲染完成后提取目录
        extractTableOfContents()
        // 为标题添加ID
        addHeadingIds()
      }
    })
  } catch (error) {
    console.error('Markdown渲染失败:', error)
    // 如果渲染失败，直接显示原始内容
    if (articleBodyRef.value) {
      articleBodyRef.value.innerHTML = `<div class="error-message" style="color: red; padding: 20px; background: #fee; border-radius: 8px; margin-bottom: 20px;">
        <strong>Markdown 渲染失败</strong><br>
        错误信息: ${error}
      </div><pre style="white-space: pre-wrap; word-wrap: break-word;">${content}</pre>`
    }
  }
}

// 提取目录
const extractTableOfContents = () => {
  if (!articleBodyRef.value) return
  
  const headings = articleBodyRef.value.querySelectorAll('h1, h2, h3, h4, h5, h6')
  tableOfContents.value = Array.from(headings).map((heading, index) => {
    const level = parseInt(heading.tagName.substring(1))
    const text = heading.textContent || ''
    const id = `heading-${index}`
    
    return { id, text, level }
  })
}

// 为标题添加ID以支持锚点跳转
const addHeadingIds = () => {
  if (!articleBodyRef.value) return
  
  const headings = articleBodyRef.value.querySelectorAll('h1, h2, h3, h4, h5, h6')
  headings.forEach((heading, index) => {
    heading.id = `heading-${index}`
  })
}

// 监听滚动更新当前激活的标题
const updateActiveHeading = () => {
  if (!articleBodyRef.value) return
  
  const headings = articleBodyRef.value.querySelectorAll('h1, h2, h3, h4, h5, h6')
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  
  let currentHeading = ''
  headings.forEach((heading) => {
    const rect = heading.getBoundingClientRect()
    if (rect.top <= 100) {
      currentHeading = heading.id
    }
  })
  
  activeHeadingId.value = currentHeading
}

// 加载文章数据
const loadArticle = async () => {
  loading.value = true
  const id = route.params.id
  
  try {
    // 调用 API 获取文章详情
    const response = await getArticleDetail(Number(id))
    article.value = response
    
    console.log('文章数据:', article.value)
    console.log('文章内容:', article.value?.content)
    console.log('内容类型:', typeof article.value?.content)
    
    // 先设置 loading 为 false，让 DOM 渲染
    loading.value = false
    
    // 等待DOM更新后渲染Markdown
    await nextTick()
    await nextTick() // 多等一个 tick 确保 DOM 完全渲染
    
    console.log('articleBodyRef.value:', articleBodyRef.value)
    
    if (article.value?.content && articleBodyRef.value) {
      console.log('开始渲染Markdown...')
      await renderMarkdown(article.value.content)
      console.log('Markdown渲染完成')
    } else {
      console.warn('文章内容为空或 DOM 未就绪', {
        hasContent: !!article.value?.content,
        hasRef: !!articleBodyRef.value
      })
    }
    
    // 加载相关文章（获取最新的3篇文章作为相关文章）
    try {
      const relatedResponse = await getArticleList({ page: 1, pageSize: 4 })
      // 过滤掉当前文章
      relatedArticles.value = relatedResponse.records
        .filter(item => item.id !== article.value?.id)
        .slice(0, 3)
    } catch (err) {
      console.error('加载相关文章失败:', err)
    }
    
    // 加载评论列表
    await loadComments()
    
    // 加载文章状态（点赞、收藏）
    await loadArticleStatus()
  } catch (error: any) {
    console.error('加载文章失败:', error)
    message.error('加载文章失败')
    loading.value = false
    
    // 如果文章不存在，跳转到404页面
    if (error.response?.status === 404) {
      router.push('/404')
    }
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
  
  // 更新当前激活的标题
  updateActiveHeading()
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

// 滚动到指定标题
const scrollToHeading = (id: string) => {
  const element = document.getElementById(id)
  if (element) {
    const top = element.offsetTop - 100
    window.scrollTo({
      top,
      behavior: 'smooth'
    })
  }
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

.toc-item.toc-active .toc-link {
  color: var(--color-miku-600);
  border-left-color: var(--color-miku-500);
  background: var(--color-miku-50);
  font-weight: var(--font-semibold);
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

.comment-form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.anonymous-checkbox {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--color-gray-600);
  cursor: pointer;
  user-select: none;
}

.anonymous-checkbox input[type="checkbox"] {
  width: 14px;
  height: 14px;
  cursor: pointer;
  accent-color: var(--color-miku-500);
}

.anonymous-checkbox:hover {
  color: var(--color-gray-900);
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
  gap: 0;
}

.comment-item-small {
  display: flex;
  gap: 10px;
  padding: 12px 10px;
  background: transparent;
  border-radius: 0;
  border: none;
  border-bottom: 1px solid var(--color-gray-200);
  transition: all var(--transition-fast);
}

.comment-item-small:last-child {
  border-bottom: none;
}

.comment-item-small:hover {
  background: var(--color-miku-50);
}

.comment-avatar-small {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, var(--color-miku-100), var(--color-cyan-100));
  color: var(--color-miku-600);
  border-radius: 50%;
  flex-shrink: 0;
  overflow: hidden;
}

.comment-avatar-small img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.comment-content-small {
  flex: 1;
  min-width: 0;
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

.comment-action-small.comment-action-liked {
  color: var(--color-red-500);
}

.comment-action-small.comment-action-liked:hover {
  color: var(--color-red-600);
  background: var(--color-red-50);
}

.comment-action-small.comment-action-delete {
  color: var(--color-red-500);
}

.comment-action-small.comment-action-delete:hover {
  color: var(--color-red-600);
  background: var(--color-red-50);
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
  padding: 20px 0;
  background: transparent;
  border: none;
  border-bottom: 1px solid var(--color-gray-200);
  border-radius: 0;
  transition: all var(--transition-fast);
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-item:hover {
  background: var(--color-miku-50);
  padding-left: 12px;
  padding-right: 12px;
  margin-left: -12px;
  margin-right: -12px;
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
  overflow: hidden;
}

.comment-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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

.comment-action.comment-action-liked {
  color: var(--color-red-500);
}

.comment-action.comment-action-liked:hover {
  color: var(--color-red-600);
  background: var(--color-red-50);
}

.comment-action.comment-action-delete {
  color: var(--color-red-500);
}

.comment-action.comment-action-delete:hover {
  color: var(--color-red-600);
  background: var(--color-red-50);
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

/* 空状态样式 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 16px;
  color: var(--color-gray-400);
  text-align: center;
}

.empty-icon {
  color: var(--color-gray-300);
  margin-bottom: 12px;
  stroke-width: 1.5;
}

.empty-text {
  font-size: 13px;
  color: var(--color-gray-500);
  margin: 0;
}

/* Vditor 预览样式优化 */
.article-body :deep(.vditor-reset) {
  font-size: 16px;
  line-height: 1.8;
  color: var(--color-gray-800);
}

.article-body :deep(.vditor-reset h1),
.article-body :deep(.vditor-reset h2),
.article-body :deep(.vditor-reset h3),
.article-body :deep(.vditor-reset h4),
.article-body :deep(.vditor-reset h5),
.article-body :deep(.vditor-reset h6) {
  scroll-margin-top: 100px;
}

.article-body :deep(.vditor-reset h2) {
  font-size: 22px;
  font-weight: var(--font-bold);
  margin-top: 48px;
  margin-bottom: 16px;
  color: var(--color-gray-900);
  letter-spacing: -0.01em;
}

.article-body :deep(.vditor-reset h3) {
  font-size: 18px;
  font-weight: var(--font-semibold);
  margin-top: 32px;
  margin-bottom: 12px;
  color: var(--color-gray-800);
}

.article-body :deep(.vditor-reset p) {
  margin-bottom: 20px;
  text-align: justify;
}

.article-body :deep(.vditor-reset blockquote) {
  position: relative;
  margin: 32px 0;
  padding: 24px 32px;
  background: var(--color-gray-50);
  border-left: 3px solid var(--color-miku-500);
  border-radius: 0 6px 6px 0;
  font-style: italic;
  line-height: 1.7;
}

.article-body :deep(.vditor-reset code) {
  padding: 2px 6px;
  background: var(--color-gray-100);
  border-radius: 3px;
  font-size: 0.9em;
  color: var(--color-pink-600);
}

.article-body :deep(.vditor-reset pre) {
  margin: 24px 0;
  border-radius: 6px;
  overflow: hidden;
}

.article-body :deep(.vditor-reset pre code) {
  padding: 0;
  background: transparent;
  color: inherit;
}

.article-body :deep(.vditor-reset table) {
  margin: 24px 0;
  border-collapse: collapse;
  width: 100%;
}

.article-body :deep(.vditor-reset table th),
.article-body :deep(.vditor-reset table td) {
  padding: 12px;
  border: 1px solid var(--color-gray-200);
}

.article-body :deep(.vditor-reset table th) {
  background: var(--color-gray-50);
  font-weight: var(--font-semibold);
}

.article-body :deep(.vditor-reset img) {
  max-width: 100%;
  height: auto;
  border-radius: 6px;
  margin: 24px 0;
}
</style>
