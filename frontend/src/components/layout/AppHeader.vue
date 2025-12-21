<template>
  <header class="app-header" :class="{ 'app-header--scrolled': isScrolled }">
    <div class="app-header__container">
      <!-- 左侧：Logo + 网站名称 -->
      <RouterLink to="/" class="app-header__brand">
        <div class="brand-icon">
          <Sparkles :size="24" />
        </div>
        <span class="brand-name">落叶无痕</span>
      </RouterLink>

      <!-- 右侧：导航标签页 -->
      <nav class="app-header__nav">
        <RouterLink to="/" class="nav-link">
          <Home :size="18" />
          <span>首页</span>
        </RouterLink>
        <RouterLink to="/archive" class="nav-link">
          <Archive :size="18" />
          <span>归档</span>
        </RouterLink>
        <RouterLink to="/gallery" class="nav-link">
          <Image :size="18" />
          <span>相册</span>
        </RouterLink>
        <RouterLink to="/projects" class="nav-link">
          <FolderGit2 :size="18" />
          <span>项目</span>
        </RouterLink>
        <RouterLink to="/guestbook" class="nav-link">
          <MessageSquare :size="18" />
          <span>留言</span>
        </RouterLink>
        <RouterLink to="/about" class="nav-link">
          <User :size="18" />
          <span>关于</span>
        </RouterLink>

        <!-- 登录按钮 / 用户菜单 -->
        <button v-if="!isLoggedIn" class="login-btn" @click="openLoginModal">
          <LogIn :size="18" />
          <span>登录</span>
        </button>

        <!-- 用户菜单 -->
        <div v-else class="user-menu-wrapper">
          <button class="user-menu-trigger" @click="toggleUserMenu">
            <img :src="currentUser?.avatar" :alt="currentUser?.name" class="user-avatar" />
            <span class="user-name">{{ currentUser?.name }}</span>
            <ChevronDown :size="16" class="chevron-icon" :class="{ 'chevron-icon--open': showUserMenu }" />
          </button>

          <!-- 下拉菜单 -->
          <Transition name="dropdown">
            <div v-if="showUserMenu" class="user-dropdown">
              <div class="user-dropdown__menu">
                <button class="dropdown-item" @click="goToProfile">
                  <User :size="16" />
                  <span>个人中心</span>
                </button>
                <button v-if="isAdmin" class="dropdown-item dropdown-item--admin" @click="goToAdmin">
                  <Shield :size="16" />
                  <span>管理后台</span>
                </button>
                <button class="dropdown-item dropdown-item--danger" @click="handleLogout">
                  <LogOut :size="16" />
                  <span>退出登录</span>
                </button>
              </div>
            </div>
          </Transition>
        </div>
      </nav>
    </div>

    <!-- 登录弹窗 -->
    <LoginModal 
      :is-open="showLoginModal" 
      @close="showLoginModal = false" 
      @success="handleLoginSuccess"
      @forgot-password="handleForgotPassword"
    />
    
    <!-- 忘记密码弹窗 -->
    <ForgotPasswordModal
      :is-open="showForgotPasswordModal"
      @close="showForgotPasswordModal = false"
      @success="handleResetSuccess"
      @back-to-login="handleBackToLogin"
    />
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { Sparkles, Home, Archive, Image, FolderGit2, MessageSquare, User, LogIn, LogOut, Settings, ChevronDown, Shield } from 'lucide-vue-next'
import LoginModal from '@/components/auth/LoginModal.vue'
import ForgotPasswordModal from '@/components/auth/ForgotPasswordModal.vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const showLoginModal = ref(false)
const showForgotPasswordModal = ref(false)
const showUserMenu = ref(false)
const isScrolled = ref(false)

const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)
const currentUser = computed(() => userStore.user)
const isAdmin = computed(() => currentUser.value?.role === 1)

function handleScroll() {
  isScrolled.value = window.scrollY > 50
}

function openLoginModal() {
  showLoginModal.value = true
}

function handleLoginSuccess() {
  console.log('Login successful')
}

function handleForgotPassword() {
  showForgotPasswordModal.value = true
}

function handleResetSuccess() {
  console.log('Password reset successful')
  // 可以显示成功提示，然后打开登录模态框
  setTimeout(() => {
    showLoginModal.value = true
  }, 500)
}

function handleBackToLogin() {
  showLoginModal.value = true
}

function toggleUserMenu() {
  showUserMenu.value = !showUserMenu.value
}

function closeUserMenu() {
  showUserMenu.value = false
}

function goToProfile() {
  router.push('/profile')
  closeUserMenu()
}

function goToAdmin() {
  router.push('/admin')
  closeUserMenu()
}

async function handleLogout() {
  await userStore.logoutUser()
  closeUserMenu()
  router.push('/')
}

// 点击外部关闭菜单
function handleClickOutside(event: MouseEvent) {
  const target = event.target as HTMLElement
  if (!target.closest('.user-menu-wrapper')) {
    closeUserMenu()
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  document.addEventListener('click', handleClickOutside)
  // 初始化用户信息
  userStore.initUser()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: var(--z-sticky);
  height: 60px;
  background: transparent;
  border-bottom: 1px solid transparent;
  transition: all var(--transition-base);
}

.app-header--scrolled {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border-bottom-color: rgba(0, 0, 0, 0.06);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.app-header--scrolled .user-menu-trigger {
  background: rgba(255, 255, 255, 0.6);
  border-color: rgba(0, 0, 0, 0.06);
}

.app-header--scrolled .user-menu-trigger:hover {
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(57, 197, 187, 0.2);
}

.app-header__container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 2vw;
}

/* 左侧品牌区 */
.app-header__brand {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  transition: opacity var(--transition-fast);
}

.app-header__brand:hover {
  opacity: 0.8;
}

.brand-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  color: var(--color-miku-400);
  transition: transform var(--transition-fast);
}

.app-header__brand:hover .brand-icon {
  transform: scale(1.1);
}

.brand-name {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  letter-spacing: -0.3px;
}

/* 右侧导航标签页 */
.app-header__nav {
  display: flex;
  gap: var(--spacing-lg);
  align-items: center;
  height: 100%;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-600);
  padding: 0 var(--spacing-xs);
  height: 100%;
  transition: color var(--transition-fast);
  position: relative;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--color-miku-400);
  transform: scaleX(0);
  transform-origin: center;
  transition: transform var(--transition-base);
}

.nav-link:hover {
  color: var(--color-miku-500);
}

.nav-link:hover::after {
  transform: scaleX(1);
}

.nav-link.router-link-active {
  color: var(--color-miku-500);
}

.nav-link.router-link-active::after {
  transform: scaleX(1);
}

/* 登录按钮 */
.login-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-white);
  background: linear-gradient(135deg, var(--color-miku-500), var(--color-cyan-500));
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
  margin-left: var(--spacing-md);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.3);
}

/* 用户菜单 */
.user-menu-wrapper {
  position: relative;
  margin-left: var(--spacing-md);
}

.user-menu-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 10px 4px 4px;
  background: transparent;
  border: 1px solid transparent;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  min-height: 36px;
}

.user-menu-trigger:hover {
  background: rgba(0, 0, 0, 0.03);
  border-color: rgba(0, 0, 0, 0.08);
}

.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
}

.user-name {
  font-size: 13px;
  font-weight: 500;
  color: #262626;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.3;
}

.chevron-icon {
  color: #8c8c8c;
  transition: transform 0.2s ease;
  flex-shrink: 0;
}

.chevron-icon--open {
  transform: rotate(180deg);
  color: var(--color-miku-500);
}

/* 下拉菜单 */
.user-dropdown {
  position: absolute;
  top: calc(100% + 12px);
  right: 0;
  min-width: 160px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  overflow: hidden;
  z-index: 100;
}

.user-dropdown__menu {
  padding: 6px;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 12px;
  font-size: 13px;
  font-weight: 400;
  color: #595959;
  background: transparent;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
}

.dropdown-item:hover {
  background: rgba(0, 0, 0, 0.04);
  color: #262626;
}

.dropdown-item--admin {
  color: var(--color-miku-600);
}

.dropdown-item--admin:hover {
  background: linear-gradient(135deg, rgba(57, 197, 187, 0.08) 0%, rgba(57, 197, 187, 0.12) 100%);
  color: var(--color-miku-700);
}

.dropdown-item--danger {
  color: #ff4d4f;
}

.dropdown-item--danger:hover {
  background: rgba(255, 77, 79, 0.08);
  color: #ff4d4f;
}

/* 下拉动画 */
.dropdown-enter-active {
  transition: all 0.25s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-12px) scale(0.95);
}

@media (max-width: 768px) {
  .app-header {
    height: 56px;
  }

  .app-header__container {
    padding: 0 4vw;
  }

  .brand-icon {
    width: 32px;
    height: 32px;
  }

  .brand-name {
    font-size: var(--text-base);
  }

  .app-header__nav {
    gap: var(--spacing-md);
  }

  .nav-link {
    font-size: var(--text-xs);
  }

  .nav-link span {
    display: none;
  }

  .login-btn {
    padding: var(--spacing-sm);
    margin-left: var(--spacing-sm);
  }

  .login-btn span {
    display: none;
  }

  .user-menu-wrapper {
    margin-left: var(--spacing-sm);
  }

  .user-name {
    display: none;
  }
}
</style>
