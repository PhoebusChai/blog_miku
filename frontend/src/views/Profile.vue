<template>
  <div class="profile-page">
    <div class="profile-header">
      <button class="back-btn" @click="$router.back()">
        <ArrowLeft :size="20" />
        <span>返回</span>
      </button>
    </div>
    <div class="profile-container">
      <ProfileSidebar v-model="activeMenu" @update-avatar="showAvatarModal = true" />
      <main class="profile-main">
        <!-- 账户设置 -->
        <section v-if="activeMenu === 'account'" class="content-section">
          <h1 class="section-title">账户设置</h1>
          <div class="form-group">
            <label class="form-label">用户名</label>
            <div class="input-group">
              <input v-model="editForm.name" type="text" class="form-input" :disabled="!isEditingName" />
              <button v-if="!isEditingName" class="input-btn" @click="startEditName">修改</button>
              <template v-else>
                <button class="input-btn save" @click="saveName">保存</button>
                <button class="input-btn cancel" @click="cancelEditName">取消</button>
              </template>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">邮箱地址</label>
            <div class="input-group">
              <input type="email" :value="user?.email" class="form-input" disabled />
              <span class="input-hint">邮箱不可修改</span>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">密码</label>
            <div class="input-group">
              <input type="password" value="••••••••" class="form-input" readonly />
              <button class="input-btn" @click="showPasswordModal = true">修改</button>
            </div>
          </div>
        </section>
        <!-- 我的活动 -->
        <section v-if="activeMenu === 'activity'" class="content-section">
          <h1 class="section-title">我的活动</h1>
          <div class="stats-grid">
            <div class="stat-card"><MessageSquare :size="24" class="stat-icon" /><div class="stat-value">{{ activity.commentCount }}</div><div class="stat-label">留言数</div></div>
            <div class="stat-card" @click="activeMenu = 'likes'"><Heart :size="24" class="stat-icon" /><div class="stat-value">{{ activity.likeCount }}</div><div class="stat-label">点赞数</div></div>
            <div class="stat-card" @click="activeMenu = 'bookmarks'"><BookMarked :size="24" class="stat-icon" /><div class="stat-value">{{ activity.bookmarkCount }}</div><div class="stat-label">收藏数</div></div>
          </div>
        </section>
        <!-- 阅读记录 -->
        <section v-if="activeMenu === 'reading'" class="content-section reading-section">
          <h1 class="section-title">阅读记录</h1>
          <div v-if="loadingHistory" class="loading-state"><Loader2 :size="32" class="loading-icon" /><p>加载中...</p></div>
          <div v-else-if="readingHistory.length === 0" class="empty-state"><BookOpen :size="48" class="empty-icon" /><p class="empty-text">暂无阅读记录</p><p class="empty-desc">开始阅读文章，记录将会显示在这里</p></div>
          <div v-else class="article-list">
            <div v-for="item in readingHistory" :key="item.id" class="article-item" @click="goToArticle(item.articleId)">
              <img v-if="item.articleCover" :src="item.articleCover" :alt="item.articleTitle" class="article-cover" />
              <div class="article-info"><h3 class="article-title">{{ item.articleTitle }}</h3><p class="article-summary">{{ item.articleSummary }}</p><span class="article-time">{{ formatTime(item.updatedAt) }}</span></div>
            </div>
          </div>
        </section>
        <!-- 点赞的文章 -->
        <section v-if="activeMenu === 'likes'" class="content-section reading-section">
          <h1 class="section-title">我的点赞</h1>
          <div v-if="loadingLikes" class="loading-state"><Loader2 :size="32" class="loading-icon" /><p>加载中...</p></div>
          <div v-else-if="likedArticles.length === 0" class="empty-state"><Heart :size="48" class="empty-icon" /><p class="empty-text">暂无点赞</p><p class="empty-desc">点赞喜欢的文章，它们将会显示在这里</p></div>
          <div v-else class="article-list">
            <div v-for="item in likedArticles" :key="item.id" class="article-item" @click="goToArticle(item.articleId)">
              <img v-if="item.articleCover" :src="item.articleCover" :alt="item.articleTitle" class="article-cover" />
              <div class="article-info"><h3 class="article-title">{{ item.articleTitle }}</h3><p class="article-summary">{{ item.articleSummary }}</p><span class="article-time">{{ formatTime(item.updatedAt) }}</span></div>
            </div>
          </div>
        </section>
        <!-- 收藏的文章 -->
        <section v-if="activeMenu === 'bookmarks'" class="content-section reading-section">
          <h1 class="section-title">我的收藏</h1>
          <div v-if="loadingBookmarks" class="loading-state"><Loader2 :size="32" class="loading-icon" /><p>加载中...</p></div>
          <div v-else-if="bookmarkedArticles.length === 0" class="empty-state"><BookMarked :size="48" class="empty-icon" /><p class="empty-text">暂无收藏</p><p class="empty-desc">收藏喜欢的文章，它们将会显示在这里</p></div>
          <div v-else class="article-list">
            <div v-for="item in bookmarkedArticles" :key="item.id" class="article-item" @click="goToArticle(item.articleId)">
              <img v-if="item.articleCover" :src="item.articleCover" :alt="item.articleTitle" class="article-cover" />
              <div class="article-info"><h3 class="article-title">{{ item.articleTitle }}</h3><p class="article-summary">{{ item.articleSummary }}</p><span class="article-time">{{ formatTime(item.updatedAt) }}</span></div>
            </div>
          </div>
        </section>
      </main>
    </div>
    <!-- 修改密码弹窗 -->
    <div v-if="showPasswordModal" class="modal-overlay" @click.self="showPasswordModal = false">
      <div class="modal-content">
        <h2 class="modal-title">修改密码</h2>
        <div class="modal-form">
          <div class="form-group"><label class="form-label">原密码</label><input v-model="passwordForm.oldPassword" type="password" class="form-input full" /></div>
          <div class="form-group"><label class="form-label">新密码</label><input v-model="passwordForm.newPassword" type="password" class="form-input full" /></div>
          <div class="form-group"><label class="form-label">确认新密码</label><input v-model="passwordForm.confirmPassword" type="password" class="form-input full" /></div>
        </div>
        <div class="modal-actions">
          <button class="btn btn-secondary" @click="showPasswordModal = false">取消</button>
          <button class="btn btn-primary" :disabled="savingPassword" @click="savePassword">{{ savingPassword ? '保存中...' : '保存' }}</button>
        </div>
      </div>
    </div>
    <!-- 修改头像弹窗 -->
    <div v-if="showAvatarModal" class="modal-overlay" @click.self="showAvatarModal = false">
      <div class="modal-content">
        <h2 class="modal-title">修改头像</h2>
        <div class="modal-form">
          <div class="avatar-preview">
            <img :src="avatarPreview || user?.avatar" alt="头像预览" />
          </div>
          <!-- 上传方式切换 -->
          <div class="upload-tabs">
            <button :class="['tab-btn', { active: uploadMode === 'file' }]" @click="uploadMode = 'file'">上传图片</button>
            <button :class="['tab-btn', { active: uploadMode === 'url' }]" @click="uploadMode = 'url'">图片链接</button>
          </div>
          <!-- 文件上传 -->
          <div v-if="uploadMode === 'file'" class="upload-area">
            <input ref="fileInputRef" type="file" accept="image/*" class="file-input" @change="handleFileSelect" />
            <div v-if="!uploading" class="upload-placeholder" @click="triggerFileInput">
              <Upload :size="32" class="upload-icon" />
              <p class="upload-text">点击或拖拽图片到此处</p>
              <p class="upload-hint">支持 JPG、PNG、GIF，最大 5MB</p>
            </div>
            <div v-else class="upload-loading">
              <Loader2 :size="32" class="loading-icon" />
              <p>上传中...</p>
            </div>
          </div>
          <!-- URL 输入 -->
          <div v-else class="form-group">
            <label class="form-label">头像URL</label>
            <input v-model="avatarUrl" type="text" class="form-input full" placeholder="输入图片URL" @input="avatarPreview = avatarUrl" />
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn btn-secondary" @click="closeAvatarModal">取消</button>
          <button class="btn btn-primary" :disabled="savingAvatar || !avatarPreview" @click="saveAvatar">{{ savingAvatar ? '保存中...' : '保存' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { MessageSquare, Heart, BookMarked, BookOpen, ArrowLeft, Loader2, Upload } from 'lucide-vue-next'
import ProfileSidebar from '@/components/layout/ProfileSidebar.vue'
import { updateProfile, changePassword, getUserActivity, getReadingHistory, getLikedArticles, getBookmarkedArticles, type ReadingHistory, type UserActivity } from '@/api/user'
import { uploadFile } from '@/api/upload'
import { showMessage } from '@/utils/message'

const router = useRouter()
const userStore = useUserStore()
const user = computed(() => userStore.user)
const activeMenu = ref('account')
const isEditingName = ref(false)
const editForm = ref({ name: '' })
const showPasswordModal = ref(false)
const savingPassword = ref(false)
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })
const showAvatarModal = ref(false)
const savingAvatar = ref(false)
const avatarUrl = ref('')
const avatarPreview = ref('')
const uploadMode = ref<'file' | 'url'>('file')
const uploading = ref(false)
const fileInputRef = ref<HTMLInputElement | null>(null)
const activity = ref<UserActivity>({ commentCount: 0, likeCount: 0, bookmarkCount: 0 })
const loadingHistory = ref(false)
const readingHistory = ref<ReadingHistory[]>([])
const loadingLikes = ref(false)
const likedArticles = ref<ReadingHistory[]>([])
const loadingBookmarks = ref(false)
const bookmarkedArticles = ref<ReadingHistory[]>([])

onMounted(() => {
  if (user.value) {
    editForm.value.name = user.value.name
    loadActivity()
  }
})

watch(activeMenu, (newMenu) => {
  if (newMenu === 'reading' && readingHistory.value.length === 0) loadReadingHistory()
  else if (newMenu === 'likes' && likedArticles.value.length === 0) loadLikedArticles()
  else if (newMenu === 'bookmarks' && bookmarkedArticles.value.length === 0) loadBookmarkedArticles()
})

async function loadActivity() {
  try { activity.value = await getUserActivity() } catch (error) { console.error('加载活动统计失败:', error) }
}
async function loadReadingHistory() {
  loadingHistory.value = true
  try { readingHistory.value = await getReadingHistory(50) } catch (error) { console.error('加载阅读记录失败:', error) } finally { loadingHistory.value = false }
}
async function loadLikedArticles() {
  loadingLikes.value = true
  try { likedArticles.value = await getLikedArticles() } catch (error) { console.error('加载点赞文章失败:', error) } finally { loadingLikes.value = false }
}
async function loadBookmarkedArticles() {
  loadingBookmarks.value = true
  try { bookmarkedArticles.value = await getBookmarkedArticles() } catch (error) { console.error('加载收藏文章失败:', error) } finally { loadingBookmarks.value = false }
}
function startEditName() { editForm.value.name = user.value?.name || ''; isEditingName.value = true }
function cancelEditName() { editForm.value.name = user.value?.name || ''; isEditingName.value = false }
async function saveName() {
  if (!editForm.value.name.trim()) { showMessage.warning('用户名不能为空'); return }
  try { const updatedUser = await updateProfile({ name: editForm.value.name }); userStore.setUser(updatedUser as any); isEditingName.value = false; showMessage.success('用户名修改成功') } catch (error: any) { showMessage.error(error.message || '修改失败') }
}
async function savePassword() {
  if (!passwordForm.value.oldPassword) { showMessage.warning('请输入原密码'); return }
  if (!passwordForm.value.newPassword || passwordForm.value.newPassword.length < 6) { showMessage.warning('新密码长度不能少于6位'); return }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) { showMessage.warning('两次输入的密码不一致'); return }
  savingPassword.value = true
  try { await changePassword({ oldPassword: passwordForm.value.oldPassword, newPassword: passwordForm.value.newPassword }); showMessage.success('密码修改成功'); showPasswordModal.value = false; passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' } } catch (error: any) { showMessage.error(error.message || '修改失败') } finally { savingPassword.value = false }
}

// 触发文件选择
function triggerFileInput() {
  fileInputRef.value?.click()
}

// 处理文件选择
async function handleFileSelect(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    showMessage.warning('请选择图片文件')
    return
  }
  
  // 验证文件大小 (5MB)
  if (file.size > 5 * 1024 * 1024) {
    showMessage.warning('图片大小不能超过 5MB')
    return
  }
  
  uploading.value = true
  try {
    const result = await uploadFile(file)
    avatarPreview.value = result.url
    avatarUrl.value = result.url
    showMessage.success('图片上传成功')
  } catch (error: any) {
    showMessage.error(error.message || '上传失败')
  } finally {
    uploading.value = false
    // 清空 input，允许重复选择同一文件
    input.value = ''
  }
}

// 关闭头像弹窗
function closeAvatarModal() {
  showAvatarModal.value = false
  avatarUrl.value = ''
  avatarPreview.value = ''
  uploadMode.value = 'file'
}

async function saveAvatar() {
  if (!avatarPreview.value.trim()) { showMessage.warning('请上传或输入头像'); return }
  savingAvatar.value = true
  try { 
    const updatedUser = await updateProfile({ avatar: avatarPreview.value })
    userStore.setUser(updatedUser as any)
    showMessage.success('头像修改成功')
    closeAvatarModal()
  } catch (error: any) { 
    showMessage.error(error.message || '修改失败') 
  } finally { 
    savingAvatar.value = false 
  }
}

function goToArticle(articleId: number) { router.push(`/article/${articleId}`) }
function formatTime(time: string) {
  if (!time) return ''
  const date = new Date(time), now = new Date(), diff = now.getTime() - date.getTime()
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  return date.toLocaleDateString()
}
</script>

<style scoped>
.profile-page { height: 100vh; padding: 80px 2vw 40px; background: var(--color-white); overflow-y: auto; }
.profile-header { max-width: 1200px; margin: 0 auto var(--spacing-2xl); }
.back-btn { display: inline-flex; align-items: center; gap: var(--spacing-xs); padding: var(--spacing-sm) var(--spacing-md); font-size: var(--text-sm); font-weight: var(--font-medium); color: #666; background: transparent; border: none; border-radius: 6px; cursor: pointer; transition: all var(--transition-fast); }
.back-btn:hover { background: #f5f5f5; color: #333; }
.profile-container { max-width: 1200px; margin: 0 auto; display: grid; grid-template-columns: 280px 1fr; gap: var(--spacing-2xl); align-items: start; }
.profile-main { background: transparent; padding: 0 var(--spacing-3xl); min-height: 400px; }
.content-section { max-width: 600px; }
.reading-section { max-width: 100%; }
.section-title { font-size: var(--text-2xl); font-weight: var(--font-semibold); color: #333; margin: 0 0 var(--spacing-2xl) 0; padding-bottom: var(--spacing-lg); border-bottom: 1px solid #f0f0f0; }
.form-group { margin-bottom: var(--spacing-2xl); }
.form-label { display: block; font-size: var(--text-sm); font-weight: var(--font-medium); color: #666; margin-bottom: var(--spacing-sm); }
.input-group { display: flex; align-items: center; gap: var(--spacing-md); }
.form-input { flex: 1; padding: var(--spacing-sm) 0; font-size: var(--text-sm); color: #333; background: transparent; border: none; border-bottom: 1px solid #e8e8e8; border-radius: 0; transition: all var(--transition-fast); }
.form-input:focus { outline: none; border-bottom-color: var(--color-miku-400); }
.form-input:disabled { color: #999; background: transparent; }
.form-input.full { width: 100%; padding: var(--spacing-sm) var(--spacing-md); border: 1px solid #e8e8e8; border-radius: 6px; }
.form-input.full:focus { border-color: var(--color-miku-400); }
.input-btn { padding: var(--spacing-sm) var(--spacing-lg); font-size: var(--text-sm); font-weight: var(--font-medium); color: var(--color-miku-500); background: transparent; border: none; cursor: pointer; transition: all var(--transition-fast); }
.input-btn:hover { color: var(--color-miku-600); text-decoration: underline; }
.input-btn.save { color: var(--color-miku-500); }
.input-btn.cancel { color: #999; }
.input-hint { font-size: var(--text-xs); color: #999; }
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: var(--spacing-3xl); }
.stat-card { padding: var(--spacing-xl) 0; text-align: center; cursor: pointer; border-radius: 8px; transition: all var(--transition-fast); }
.stat-card:hover { background: #f9f9f9; }
.stat-icon { color: var(--color-miku-500); margin-bottom: var(--spacing-sm); }
.stat-value { font-size: var(--text-3xl); font-weight: var(--font-bold); color: #333; margin-bottom: var(--spacing-xs); }
.stat-label { font-size: var(--text-sm); color: #999; }
.article-list { display: flex; flex-direction: column; gap: var(--spacing-md); }
.article-item { display: flex; gap: var(--spacing-lg); padding: var(--spacing-lg); background: #fafafa; border-radius: 8px; cursor: pointer; transition: all var(--transition-fast); }
.article-item:hover { background: #f0f0f0; }
.article-cover { width: 120px; height: 80px; object-fit: cover; border-radius: 6px; flex-shrink: 0; }
.article-info { flex: 1; min-width: 0; }
.article-title { font-size: var(--text-base); font-weight: var(--font-medium); color: #333; margin: 0 0 var(--spacing-xs) 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.article-summary { font-size: var(--text-sm); color: #666; margin: 0 0 var(--spacing-sm) 0; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.article-time { font-size: var(--text-xs); color: #999; }
.loading-state, .empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: var(--spacing-4xl) var(--spacing-2xl); text-align: center; }
.loading-icon { color: var(--color-miku-400); animation: spin 1s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
.empty-icon { color: var(--color-miku-300); margin-bottom: var(--spacing-xl); }
.empty-text { font-size: var(--text-lg); font-weight: var(--font-semibold); color: var(--color-miku-600); margin: 0 0 var(--spacing-sm) 0; }
.empty-desc { font-size: var(--text-sm); color: var(--color-gray-500); margin: 0; }
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-content { background: white; border-radius: 12px; padding: var(--spacing-2xl); width: 400px; max-width: 90vw; }
.modal-title { font-size: var(--text-xl); font-weight: var(--font-semibold); color: #333; margin: 0 0 var(--spacing-xl) 0; }
.modal-form { margin-bottom: var(--spacing-xl); }
.modal-form .form-group { margin-bottom: var(--spacing-lg); }
.modal-actions { display: flex; justify-content: flex-end; gap: var(--spacing-md); }
.btn { padding: var(--spacing-sm) var(--spacing-xl); font-size: var(--text-sm); font-weight: var(--font-medium); border-radius: 6px; cursor: pointer; transition: all var(--transition-fast); }
.btn-secondary { color: #666; background: #f5f5f5; border: none; }
.btn-secondary:hover { background: #e8e8e8; }
.btn-primary { color: white; background: var(--color-miku-500); border: none; }
.btn-primary:hover { background: var(--color-miku-600); }
.btn-primary:disabled { background: #ccc; cursor: not-allowed; }
.avatar-preview { width: 100px; height: 100px; margin: 0 auto var(--spacing-xl); border-radius: 50%; overflow: hidden; background: #f5f5f5; }
.avatar-preview img { width: 100%; height: 100%; object-fit: cover; }
.upload-tabs { display: flex; gap: var(--spacing-sm); margin-bottom: var(--spacing-lg); }
.tab-btn { flex: 1; padding: var(--spacing-sm) var(--spacing-md); font-size: var(--text-sm); color: #666; background: #f5f5f5; border: none; border-radius: 6px; cursor: pointer; transition: all var(--transition-fast); }
.tab-btn:hover { background: #e8e8e8; }
.tab-btn.active { color: white; background: var(--color-miku-500); }
.upload-area { position: relative; }
.file-input { position: absolute; width: 0; height: 0; opacity: 0; }
.upload-placeholder { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: var(--spacing-2xl); border: 2px dashed #e0e0e0; border-radius: 8px; cursor: pointer; transition: all var(--transition-fast); }
.upload-placeholder:hover { border-color: var(--color-miku-400); background: rgba(57, 197, 187, 0.05); }
.upload-icon { color: #999; margin-bottom: var(--spacing-sm); }
.upload-text { font-size: var(--text-sm); color: #666; margin: 0 0 var(--spacing-xs) 0; }
.upload-hint { font-size: var(--text-xs); color: #999; margin: 0; }
.upload-loading { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: var(--spacing-2xl); border: 2px dashed var(--color-miku-300); border-radius: 8px; background: rgba(57, 197, 187, 0.05); }
.upload-loading .loading-icon { color: var(--color-miku-500); animation: spin 1s linear infinite; }
.upload-loading p { font-size: var(--text-sm); color: var(--color-miku-600); margin: var(--spacing-sm) 0 0 0; }
@media (max-width: 1024px) { .profile-container { grid-template-columns: 1fr; } }
@media (max-width: 768px) { .profile-page { padding: 70px 4vw 30px; } .profile-main { padding: var(--spacing-xl); } .stats-grid { grid-template-columns: 1fr; } .article-item { flex-direction: column; } .article-cover { width: 100%; height: 150px; } }
</style>
