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
              <KeyRound :size="32" />
            </div>
            <h2 class="modal-title">{{ step === 'email' ? '重置密码' : '验证邮箱' }}</h2>
            <p class="modal-subtitle">
              {{ step === 'email' ? '输入您的邮箱地址，我们将发送重置链接' : '请输入我们发送到您邮箱的验证码' }}
            </p>
          </div>

          <!-- 步骤1: 输入邮箱 -->
          <form v-if="step === 'email'" class="modal-form" @submit.prevent="handleSendCode">
            <div class="form-group">
              <label for="email" class="form-label">
                <Mail :size="16" />
                邮箱地址
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

            <div v-if="error" class="form-error">
              <AlertCircle :size="16" />
              {{ error }}
            </div>

            <button type="submit" class="submit-btn" :disabled="loading">
              <Loader v-if="loading" :size="18" class="btn-spinner" />
              <span v-else>发送验证码</span>
            </button>
          </form>

          <!-- 步骤2: 输入验证码和新密码 -->
          <form v-else class="modal-form" @submit.prevent="handleResetPassword">
            <div class="form-group">
              <label for="code" class="form-label">
                <Shield :size="16" />
                验证码
              </label>
              <input
                id="code"
                v-model="formData.code"
                type="text"
                class="form-input"
                placeholder="请输入6位验证码"
                maxlength="6"
                required
                :disabled="loading"
              />
              <p class="form-hint">验证码已发送至 {{ formData.email }}</p>
            </div>

            <div class="form-group">
              <label for="new-password" class="form-label">
                <Lock :size="16" />
                新密码
              </label>
              <div class="password-input-wrapper">
                <input
                  id="new-password"
                  v-model="formData.newPassword"
                  :type="showPassword ? 'text' : 'password'"
                  class="form-input"
                  placeholder="至少8个字符"
                  minlength="8"
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

            <div class="form-group">
              <label for="confirm-password" class="form-label">
                <Lock :size="16" />
                确认密码
              </label>
              <input
                id="confirm-password"
                v-model="formData.confirmPassword"
                :type="showPassword ? 'text' : 'password'"
                class="form-input"
                placeholder="再次输入新密码"
                required
                :disabled="loading"
              />
            </div>

            <div v-if="error" class="form-error">
              <AlertCircle :size="16" />
              {{ error }}
            </div>

            <div class="form-actions">
              <button type="button" class="back-btn" @click="step = 'email'" :disabled="loading">
                <ArrowLeft :size="16" />
                返回
              </button>
              <button type="submit" class="submit-btn" :disabled="loading">
                <Loader v-if="loading" :size="18" class="btn-spinner" />
                <span v-else>重置密码</span>
              </button>
            </div>
          </form>

          <div class="modal-footer">
            <p class="footer-text">
              记起密码了？
              <a href="#" class="footer-link" @click="handleBackToLogin">返回登录</a>
            </p>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { X, KeyRound, Mail, Lock, Shield, Eye, EyeOff, AlertCircle, Loader, ArrowLeft } from 'lucide-vue-next'

interface Props {
  isOpen: boolean
}

interface Emits {
  (e: 'close'): void
  (e: 'success'): void
  (e: 'back-to-login'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const step = ref<'email' | 'verify'>('email')
const formData = ref({
  email: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})

const showPassword = ref(false)
const loading = ref(false)
const error = ref('')

// 监听弹窗打开/关闭
watch(() => props.isOpen, (isOpen) => {
  if (isOpen) {
    document.body.style.overflow = 'hidden'
  } else {
    document.body.style.overflow = ''
    // 重置表单
    step.value = 'email'
    formData.value = {
      email: '',
      code: '',
      newPassword: '',
      confirmPassword: ''
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

async function handleSendCode() {
  error.value = ''
  loading.value = true

  try {
    // TODO: 调用发送验证码 API
    // await sendResetCode(formData.value.email)
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 成功后进入下一步
    step.value = 'verify'
  } catch (err: any) {
    error.value = err.message || '发送验证码失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

async function handleResetPassword() {
  error.value = ''

  // 验证密码
  if (formData.value.newPassword !== formData.value.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return
  }

  if (formData.value.newPassword.length < 8) {
    error.value = '密码长度至少为8个字符'
    return
  }

  loading.value = true

  try {
    // TODO: 调用重置密码 API
    // await resetPassword({
    //   email: formData.value.email,
    //   code: formData.value.code,
    //   newPassword: formData.value.newPassword
    // })
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 成功
    emit('success')
    closeModal()
  } catch (err: any) {
    error.value = err.message || '重置密码失败，请检查验证码是否正确'
  } finally {
    loading.value = false
  }
}

function handleBackToLogin(e: Event) {
  e.preventDefault()
  emit('back-to-login')
  closeModal()
}
</script>

<style scoped>
/* 复用登录模态框的样式 */
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
  background: linear-gradient(135deg, var(--color-orange-100), var(--color-yellow-100));
  border-radius: 50%;
  margin-bottom: var(--spacing-lg);
}

.modal-icon svg {
  color: var(--color-orange-600);
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
  line-height: 1.5;
}

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

.form-hint {
  font-size: 12px;
  color: var(--color-gray-500);
  margin: 0;
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

.form-error {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md);
  background: var(--color-red-50);
  border: 1px solid var(--color-red-200);
  border-radius: 6px;
  color: var(--color-red-700);
  font-size: var(--text-sm);
}

.form-error svg {
  flex-shrink: 0;
}

.form-actions {
  display: flex;
  gap: var(--spacing-md);
}

.back-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-md) var(--spacing-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-gray-700);
  background: var(--color-white);
  border: 1px solid var(--color-gray-300);
  border-radius: 8px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.back-btn:hover:not(:disabled) {
  background: var(--color-gray-50);
  border-color: var(--color-gray-400);
}

.back-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.submit-btn {
  flex: 1;
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

  .form-actions {
    flex-direction: column;
  }

  .back-btn {
    order: 2;
  }
}
</style>
