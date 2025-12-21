<template>
  <div v-if="visible" class="modal-overlay" @click.self="handleClose">
    <div class="modal-container">
      <div class="modal-header">
        <h3 class="modal-title">{{ isEdit ? '编辑友链' : '添加友链' }}</h3>
        <button class="close-btn" @click="handleClose">
          <X :size="20" />
        </button>
      </div>

      <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label class="form-label required">网站名称</label>
            <input
              v-model="formData.name"
              type="text"
              class="form-input"
              placeholder="请输入网站名称"
              maxlength="50"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label required">网站地址</label>
            <input
              v-model="formData.url"
              type="url"
              class="form-input"
              placeholder="https://example.com"
              maxlength="200"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">网站头像</label>
            <div class="avatar-upload-section">
              <div class="avatar-preview-large" @click="triggerAvatarInput">
                <img v-if="avatarPreview" :src="avatarPreview" alt="头像预览" />
                <div v-else class="avatar-placeholder">
                  <Globe :size="32" />
                  <span>点击上传</span>
                </div>
                <div v-if="avatarPreview" class="avatar-overlay">
                  <Upload :size="20" />
                  <span>更换</span>
                </div>
              </div>
              <input
                ref="avatarFileInput"
                type="file"
                accept="image/*"
                class="file-input"
                @change="handleAvatarFileChange"
              />
            </div>
            <div class="url-input-section">
              <span class="url-label">或输入图片URL：</span>
              <input
                v-model="formData.avatar"
                type="url"
                class="form-input"
                placeholder="https://example.com/avatar.jpg"
                maxlength="500"
                @input="handleAvatarUrlChange"
              />
            </div>
            <span class="form-hint">建议尺寸：100x100 像素，支持 JPG、PNG、GIF 格式</span>
          </div>

          <div class="form-group">
            <label class="form-label">网站描述</label>
            <textarea
              v-model="formData.description"
              class="form-textarea"
              placeholder="请输入网站描述"
              maxlength="200"
              rows="3"
            ></textarea>
          </div>

          <div class="form-group">
            <label class="form-label required">审核状态</label>
            <select v-model.number="formData.status" class="form-select" required>
              <option :value="0">待审核</option>
              <option :value="1">已通过</option>
              <option :value="2">已拒绝</option>
            </select>
          </div>

          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="handleClose">取消</button>
            <button type="submit" class="btn-primary" :disabled="loading">
              <Loader v-if="loading" :size="16" class="spinning" />
              <span>{{ loading ? '保存中...' : '保存' }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { X, Globe, Loader, Upload } from 'lucide-vue-next'

interface LinkFormData {
  name: string
  url: string
  avatar: string
  description: string
  status: number
}

interface Props {
  visible: boolean
  isEdit?: boolean
  initialData?: LinkFormData | null
}

const props = withDefaults(defineProps<Props>(), {
  isEdit: false,
  initialData: null
})

const emit = defineEmits<{
  close: []
  submit: [data: LinkFormData]
}>()

const loading = ref(false)
const avatarFileInput = ref<HTMLInputElement>()
const avatarPreview = ref('')
const avatarFile = ref<File | null>(null)

const formData = ref<LinkFormData>({
  name: '',
  url: '',
  avatar: '',
  description: '',
  status: 0
})

watch(
  () => props.initialData,
  (newData) => {
    if (newData) {
      formData.value = { ...newData }
      avatarPreview.value = newData.avatar || ''
      avatarFile.value = null
    } else {
      resetForm()
    }
  },
  { immediate: true }
)

function resetForm() {
  formData.value = {
    name: '',
    url: '',
    avatar: '',
    description: '',
    status: 0
  }
  avatarPreview.value = ''
  avatarFile.value = null
  if (avatarFileInput.value) {
    avatarFileInput.value.value = ''
  }
}

function triggerAvatarInput() {
  avatarFileInput.value?.click()
}

function handleAvatarFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]

  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    alert('请选择图片文件')
    return
  }

  // 验证文件大小（2MB）
  if (file.size > 2 * 1024 * 1024) {
    alert('图片大小不能超过 2MB')
    return
  }

  avatarFile.value = file

  // 生成预览
  const reader = new FileReader()
  reader.onload = (e) => {
    avatarPreview.value = e.target?.result as string
    formData.value.avatar = '' // 清空URL输入
  }
  reader.readAsDataURL(file)
}

function handleAvatarUrlChange() {
  if (formData.value.avatar) {
    avatarPreview.value = formData.value.avatar
    avatarFile.value = null
    if (avatarFileInput.value) {
      avatarFileInput.value.value = ''
    }
  }
}

function handleClose() {
  emit('close')
  resetForm()
}

async function handleSubmit() {
  loading.value = true
  try {
    await new Promise((resolve) => setTimeout(resolve, 500))
    
    // 如果有上传的文件，这里应该先上传文件获取URL
    if (avatarFile.value) {
      // TODO: 实际项目中应该调用上传API
      console.log('上传头像文件:', avatarFile.value)
      // formData.value.avatar = await uploadFile(avatarFile.value)
    }
    
    emit('submit', { ...formData.value })
    handleClose()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

.modal-container {
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  background: var(--color-white);
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.3s ease;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-xl);
  border-bottom: 1px solid var(--color-gray-200);
  flex-shrink: 0;
}

.modal-title {
  margin: 0;
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
}

.close-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: 6px;
  color: var(--color-gray-600);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.close-btn:hover {
  background: var(--color-gray-100);
  color: var(--color-gray-900);
}

.modal-body {
  padding: var(--spacing-xl);
  overflow-y: auto;
}

.modal-body::-webkit-scrollbar {
  width: 6px;
}

.modal-body::-webkit-scrollbar-thumb {
  background: var(--color-gray-300);
  border-radius: 3px;
}

.form-group {
  margin-bottom: var(--spacing-lg);
}

.form-label {
  display: block;
  margin-bottom: var(--spacing-sm);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-700);
}

.form-label.required::after {
  content: ' *';
  color: #ef4444;
}

.form-input,
.form-textarea,
.form-select {
  width: 100%;
  padding: var(--spacing-sm) var(--spacing-md);
  font-size: var(--text-sm);
  color: var(--color-gray-900);
  background: var(--color-white);
  border: 1px solid var(--color-gray-300);
  border-radius: 6px;
  transition: all var(--transition-fast);
}

.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  outline: none;
  border-color: var(--color-miku-500);
  box-shadow: 0 0 0 3px rgba(57, 197, 187, 0.1);
}

.form-textarea {
  resize: vertical;
  font-family: inherit;
}

.file-input {
  display: none;
}

.avatar-upload-section {
  display: flex;
  justify-content: center;
  margin-bottom: var(--spacing-md);
}

.avatar-preview-large {
  position: relative;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-gray-100);
  border: 2px dashed var(--color-gray-300);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.avatar-preview-large:hover {
  border-color: var(--color-miku-500);
  background: var(--color-miku-50);
}

.avatar-preview-large img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xs);
  color: var(--color-gray-500);
}

.avatar-placeholder span {
  font-size: var(--text-xs);
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
  background: rgba(0, 0, 0, 0.6);
  color: var(--color-white);
  opacity: 0;
  transition: opacity var(--transition-fast);
  font-size: var(--text-xs);
}

.avatar-preview-large:hover .avatar-overlay {
  opacity: 1;
}

.url-input-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.url-label {
  font-size: var(--text-xs);
  color: var(--color-gray-600);
}

.form-hint {
  display: block;
  margin-top: var(--spacing-xs);
  font-size: var(--text-xs);
  color: var(--color-gray-500);
}

.form-actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: flex-end;
  margin-top: var(--spacing-xl);
  padding-top: var(--spacing-xl);
  border-top: 1px solid var(--color-gray-200);
}

.btn-secondary,
.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-xl);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-secondary {
  color: var(--color-gray-700);
  background: var(--color-white);
  border: 1px solid var(--color-gray-300);
}

.btn-secondary:hover {
  background: var(--color-gray-50);
}

.btn-primary {
  color: var(--color-white);
  background: var(--color-miku-500);
}

.btn-primary:hover:not(:disabled) {
  background: var(--color-miku-600);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
