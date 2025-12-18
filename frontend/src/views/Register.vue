<template>
  <div class="register">
    <main class="register__main">
      <!-- 左侧宣传区域 -->
      <div class="register-promo">
        <div class="promo-content">
          <div class="promo-icon">
            <Sparkles :size="48" />
          </div>
          <h1 class="promo-title">欢迎加入</h1>
          <p class="promo-subtitle">落叶无痕的博客</p>
          
          <div class="promo-features">
            <div class="feature-item">
              <div class="feature-icon">
                <BookOpen :size="24" />
              </div>
              <div class="feature-text">
                <h3>技术分享</h3>
                <p>探索前沿技术，分享开发经验</p>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-icon">
                <Users :size="24" />
              </div>
              <div class="feature-text">
                <h3>社区互动</h3>
                <p>与志同道合的开发者交流</p>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-icon">
                <Zap :size="24" />
              </div>
              <div class="feature-text">
                <h3>持续成长</h3>
                <p>记录学习历程，见证成长轨迹</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧注册表单 -->
      <div class="register-form-section">
        <div class="form-container">
          <div class="form-header">
            <h2 class="form-title">创建账号</h2>
            <p class="form-subtitle">填写信息，开启精彩旅程</p>
          </div>

          <form class="register-form" @submit.prevent="handleRegister">
            <div class="form-group">
              <label for="username" class="form-label">
                <User :size="16" />
                用户名
              </label>
              <input
                id="username"
                v-model="formData.username"
                type="text"
                class="form-input"
                placeholder="请输入用户名"
                required
                :disabled="loading"
              />
            </div>

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
              <label for="verifyCode" class="form-label">
                <Shield :size="16" />
                验证码
              </label>
              <div class="verify-code-wrapper">
                <input
                  id="verifyCode"
                  v-model="formData.verifyCode"
                  type="text"
                  class="form-input"
                  placeholder="请输入验证码"
                  required
                  :disabled="loading"
                />
                <button
                  type="button"
                  class="verify-code-btn"
                  :disabled="loading || countdown > 0"
                  @click="sendVerifyCode"
                >
                  {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                </button>
              </div>
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
                  placeholder="至少8个字符"
                  required
                  minlength="8"
                  :disabled="loading"
                />
                <button
                  type="button"
                  class="password-toggle"
                  @click="showPassword = !showPassword"
                >
                  <Eye v-if="!showPassword" :size="18" />
                  <EyeOff v-else :size="18" />
                </button>
              </div>
            </div>

            <div class="form-group">
              <label for="confirmPassword" class="form-label">
                <Lock :size="16" />
                确认密码
              </label>
              <div class="password-input-wrapper">
                <input
                  id="confirmPassword"
                  v-model="formData.confirmPassword"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  class="form-input"
                  placeholder="再次输入密码"
                  required
                  :disabled="loading"
                />
                <button
                  type="button"
                  class="password-toggle"
                  @click="showConfirmPassword = !showConfirmPassword"
                >
                  <Eye v-if="!showConfirmPassword" :size="18" />
                  <EyeOff v-else :size="18" />
                </button>
              </div>
            </div>

            <div class="form-group">
              <label class="checkbox-label">
                <input v-model="formData.agree" type="checkbox" class="checkbox-input" required />
                <span class="checkbox-text">
                  我已阅读并同意服务条款和隐私政策
                </span>
              </label>
            </div>

            <div v-if="error" class="form-error">
              <AlertCircle :size="16" />
              {{ error }}
            </div>

            <button type="submit" class="submit-btn" :disabled="loading">
              <Loader v-if="loading" :size="18" class="btn-spinner" />
              <span v-else>注册</span>
            </button>
          </form>

          <div class="register-footer">
            <p class="footer-text">
              已有账号？
              <a href="/" class="footer-link" @click="handleLoginClick">立即登录</a>
            </p>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Mail, Lock, Eye, EyeOff, AlertCircle, Loader, Shield, Sparkles, BookOpen, Users, Zap } from 'lucide-vue-next'

const router = useRouter()

const formData = ref({
  username: '',
  email: '',
  verifyCode: '',
  password: '',
  confirmPassword: '',
  agree: false
})

const showPassword = ref(false)
const showConfirmPassword = ref(false)
const loading = ref(false)
const error = ref('')
const countdown = ref(0)
let countdownTimer: number | null = null

async function handleRegister() {
  error.value = ''

  // 验证密码
  if (formData.value.password !== formData.value.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return
  }

  if (formData.value.password.length < 8) {
    error.value = '密码长度至少为8个字符'
    return
  }

  if (!formData.value.agree) {
    error.value = '请同意服务条款和隐私政策'
    return
  }

  loading.value = true

  try {
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 1500))

    // TODO: 实际的注册逻辑
    console.log('Register:', formData.value)

    // 注册成功，跳转到首页
    router.push('/')
  } catch (err) {
    error.value = '注册失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function handleLoginClick(e: Event) {
  e.preventDefault()
  router.push('/')
}

async function sendVerifyCode() {
  if (!formData.value.email) {
    error.value = '请先输入邮箱地址'
    return
  }

  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(formData.value.email)) {
    error.value = '请输入有效的邮箱地址'
    return
  }

  error.value = ''

  try {
    // 模拟发送验证码
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // TODO: 实际的发送验证码逻辑
    console.log('Send verify code to:', formData.value.email)

    // 开始倒计时
    countdown.value = 60
    countdownTimer = window.setInterval(() => {
      countdown.value--
      if (countdown.value <= 0 && countdownTimer) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }, 1000)
  } catch (err) {
    error.value = '发送验证码失败，请稍后重试'
  }
}

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style scoped>
.register {
  min-height: 100vh;
  background: var(--color-white);
}

.register__main {
  display: flex;
  min-height: 100vh;
}

/* 左侧宣传区域 */
.register-promo {
  flex: 1;
  background: linear-gradient(135deg, var(--color-miku-500), var(--color-cyan-500));
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl);
  position: relative;
  overflow: hidden;
}

.register-promo::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  animation: float 20s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(-20px, -20px); }
}

.promo-content {
  max-width: 500px;
  color: var(--color-white);
  position: relative;
  z-index: 1;
}

.promo-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 50%;
  margin-bottom: var(--spacing-xl);
}

.promo-title {
  font-size: clamp(var(--text-3xl), 5vw, 56px);
  font-weight: var(--font-bold);
  margin: 0 0 var(--spacing-md) 0;
  line-height: 1.2;
}

.promo-subtitle {
  font-size: var(--text-xl);
  margin: 0 0 var(--spacing-3xl) 0;
  opacity: 0.9;
}

.promo-features {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.feature-item {
  display: flex;
  gap: var(--spacing-lg);
  align-items: flex-start;
}

.feature-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: var(--radius-md);
  flex-shrink: 0;
}

.feature-text h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin: 0 0 var(--spacing-xs) 0;
}

.feature-text p {
  font-size: var(--text-sm);
  margin: 0;
  opacity: 0.9;
}

/* 右侧表单区域 */
.register-form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl) var(--spacing-2xl);
  background: var(--color-white);
  overflow-y: auto;
}

.form-container {
  width: 100%;
  max-width: 440px;
}

.form-header {
  margin-bottom: var(--spacing-3xl);
}

.form-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin: 0 0 var(--spacing-sm) 0;
}

.form-subtitle {
  font-size: var(--text-base);
  color: var(--color-gray-600);
  margin: 0;
}

/* 表单 */
.register-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
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

/* 验证码输入 */
.verify-code-wrapper {
  display: flex;
  gap: var(--spacing-md);
  align-items: flex-end;
}

.verify-code-wrapper .form-input {
  flex: 1;
}

.verify-code-btn {
  padding: var(--spacing-md) var(--spacing-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-miku-600);
  background: var(--color-miku-50);
  border: 1px solid var(--color-miku-200);
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
  white-space: nowrap;
  flex-shrink: 0;
}

.verify-code-btn:hover:not(:disabled) {
  background: var(--color-miku-100);
  border-color: var(--color-miku-300);
}

.verify-code-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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

.checkbox-label {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-sm);
  cursor: pointer;
}

.checkbox-input {
  width: 16px;
  height: 16px;
  margin-top: 2px;
  cursor: pointer;
  flex-shrink: 0;
}

.checkbox-text {
  font-size: var(--text-sm);
  color: var(--color-gray-700);
  line-height: 1.5;
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
  margin-top: var(--spacing-md);
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

/* 底部 */
.register-footer {
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

/* 响应式 */
@media (max-width: 1024px) {
  .register__main {
    flex-direction: column;
  }

  .register-promo {
    min-height: 40vh;
  }

  .promo-features {
    display: none;
  }

  .register-form-section {
    min-height: 60vh;
  }
}

@media (max-width: 768px) {
  .register-promo {
    min-height: 30vh;
    padding: var(--spacing-2xl);
  }

  .promo-icon {
    width: 64px;
    height: 64px;
  }

  .promo-title {
    font-size: var(--text-2xl);
  }

  .promo-subtitle {
    font-size: var(--text-lg);
  }

  .register-form-section {
    padding: var(--spacing-2xl) var(--spacing-lg);
  }

  .form-title {
    font-size: var(--text-2xl);
  }
}

@media (max-width: 480px) {
  .register-promo {
    padding: var(--spacing-xl);
  }

  .register-form-section {
    padding: var(--spacing-xl) var(--spacing-md);
  }

  .verify-code-wrapper {
    flex-direction: column;
    align-items: stretch;
  }

  .verify-code-btn {
    width: 100%;
  }
}
</style>
