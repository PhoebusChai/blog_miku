<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="isOpen" class="modal-overlay" @click="handleOverlayClick">
        <div class="modal-container" @click.stop>
          <button class="modal-close" @click="closeModal" aria-label="关闭">
            <X :size="20" />
          </button>

          <div class="modal-header">
            <div class="modal-icon">
              <LogIn :size="32" />
            </div>
            <h2 class="modal-title">欢迎回来</h2>
            <p class="modal-subtitle">登录以继续使用所有功能</p>
          </div>

          <form class="modal-form" @submit.prevent="handleLogin">
            <div class="form-group">
              <label for="email" class="form-label">
                <Mail :size="16" />
                邮箱
              </label>
              <input
                id="email"
                v-model="formData.email"
                type="email"
                class="form-input"
                placeholder="your@email.com"
                required
                :disabled="loading"
              />
            </div>

            <div class="form-group">
              <label for="password" class="form-label">
                <Lock :size="16" />
                密码
              </label>
              <div class="password-input-wrapper">
                <input
                  id="password"
                  v-model="formData.password"
                  :type="showPassword ? 'text' : 'password'"
                  class="form-input"
                  placeholder="••••••••"
                  required
                  :disabled="loading"
                />
                <button
                  type="button"
                  class="password-toggle"
                  @click="showPassword = !showPassword"
                  :aria-label="showPassword ? '隐藏密码' : '显示密码'"
                >
                  <Eye v-if="!showPassword" :size="18" />
                  <EyeOff v-else :size="18" />
                </button>
              </div>
            </div>

            <div class="form-options">
              <label class="checkbox-label">
                <input v-model="formData.remember" type="checkbox" class="checkbox-input" />
                <span class="checkbox-text">记住我</span>
              </label>
              <a href="#" class="forgot-link" @click="handleForgotPassword">忘记密码？</a>
            </div>

            <div v-if="error" class="form-error">
              <AlertCircle :size="16" />
              {{ error }}
            </div>

            <button type="submit" class="submit-btn" :disabled="loading">
              <Loader v-if="loading" :size="18" class="btn-spinner" />
              <span v-else>登录</span>
            </button>
          </form>

          <div class="modal-footer">
            <p class="footer-text">
              还没有账号？
              <a href="/register" class="footer-link" @click="handleRegisterClick">立即注册</a>
            </p>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { X, LogIn, Mail, Lock, Eye, EyeOff, AlertCircle, Loader } from 'lucide-vue-next'
import { useUserStore } from '@/stores/user'
import { message } from '@/utils/message'

const router = useRouter()

interface Props {
  isOpen: boolean
}

interface Emits {
  (e: 'close'): void
  (e: 'success'): void
  (e: 'forgot-password'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const formData = ref({
  email: '',
  password: '',
  remember: false
})

const showPassword = ref(false)
const loading = ref(false)
const error = ref('')

// 监听弹窗打开/关闭，控制body滚动
watch(() => props.isOpen, (isOpen) => {
  if (isOpen) {
    document.body.style.overflow = 'hidden'
  } else {
    document.body.style.overflow = ''
    // 重置表单
    formData.value = {
      email: '',
      password: '',
      remember: false
    }
    error.value = ''
    showPassword.value = false
  }
})

function closeModal() {
  emit('close')
}

function handleOverlayClick() {
  closeModal()
}

async function handleLogin() {
  error.value = ''
  loading.value = true

  try {
    const { loginUser } = useUserStore()
    const result = await loginUser(formData.value)

    // 登录成功 - 使用后端返回的消息
    message.success(result.message || '登录成功！')
    emit('success')
    closeModal()
  } catch (err: any) {
    error.value = err.message || '登录失败，请检查邮箱和密码'
    message.error(error.value)
  } finally {
    loading.value = false
  }
}

function handleRegisterClick(e: Event) {
  e.preventDefault()
  closeModal()
  router.push('/register')
}

function handleForgotPassword(e: Event) {
  e.preventDefault()
  emit('forgot-password')
  closeModal()
}
</script>

<style scoped>
/* 模态框遮罩 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--spacing-lg);
}

/* 模态框容器 */
.modal-container {
  position: relative;
  width: 100%;
  max-width: 440px;
  background: var(--color-white);
  border-radius: var(--radius-lg);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: var(--spacing-3xl);
  max-height: 90vh;
  overflow-y: auto;
}

/* 关闭按钮 */
.modal-close {
  position: absolute;
  top: var(--spacing-lg);
  right: var(--spacing-lg);
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-gray-100);
  border: none;
  border-radius: 6px;
  color: var(--color-gray-600);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.modal-close:hover {
  background: var(--color-gray-200);
  color: var(--color-gray-900);
}

/* 模态框头部 */
.modal-header {
  text-align: center;
  margin-bottom: var(--spacing-2xl);
}

.modal-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, var(--color-miku-100), var(--color-cyan-100));
  border-radius: 50%;
  margin-bottom: var(--spacing-lg);
}

.modal-icon svg {
  color: var(--color-miku-600);
}

.modal-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0 0 var(--spacing-sm) 0;
}

.modal-subtitle {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  margin: 0;
}

/* 表单 */
.modal-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.form-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-700);
}

.form-label svg {
  color: var(--color-miku-500);
}

.form-input {
  width: 100%;
  padding: var(--spacing-md) 0;
  font-size: var(--text-base);
  border: none;
  border-bottom: 2px solid var(--color-gray-300);
  border-radius: 0;
  background: transparent;
  transition: all var(--transition-fast);
}

.form-input:focus {
  outline: none;
  border-bottom-color: var(--color-miku-500);
}

.form-input:disabled {
  background: transparent;
  opacity: 0.6;
  cursor: not-allowed;
}

.form-input::placeholder {
  color: var(--color-gray-400);
}

.password-input-wrapper {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  color: var(--color-gray-500);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.password-toggle:hover {
  color: var(--color-miku-600);
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
}

.checkbox-input {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.checkbox-text {
  font-size: var(--text-sm);
  color: var(--color-gray-700);
}

.forgot-link {
  font-size: var(--text-sm);
  color: var(--color-miku-600);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.forgot-link:hover {
  color: var(--color-miku-700);
}

.form-error {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--color-red-50);
  border: 1px solid var(--color-red-200);
  border-radius: 6px;
  color: var(--color-red-700);
  font-size: var(--text-xs);
}

.form-error svg {
  flex-shrink: 0;
}

.submit-btn {
  width: 100%;
  padding: var(--spacing-md) var(--spacing-xl);
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--color-white);
  background: linear-gradient(135deg, var(--color-miku-500), var(--color-cyan-500));
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.3);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 模态框底部 */
.modal-footer {
  margin-top: var(--spacing-xl);
  text-align: center;
}

.footer-text {
  font-size: var(--text-sm);
  color: var(--color-gray-600);
  margin: 0;
}

.footer-link {
  color: var(--color-miku-600);
  font-weight: var(--font-semibold);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.footer-link:hover {
  color: var(--color-miku-700);
}

/* 动画 */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-active .modal-container,
.modal-leave-active .modal-container {
  transition: transform 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-container,
.modal-leave-to .modal-container {
  transform: scale(0.9);
}

/* 响应式 */
@media (max-width: 480px) {
  .modal-container {
    padding: var(--spacing-2xl);
  }

  .modal-icon {
    width: 56px;
    height: 56px;
  }

  .modal-title {
    font-size: var(--text-xl);
  }
}
</style>
