<template>
  <div v-if="visible" class="modal-overlay" @click.self="handleClose">
    <div class="modal-container">
      <div class="modal-header">
        <h3 class="modal-title">上传图片</h3>
        <button class="close-btn" @click="handleClose">
          <X :size="20" />
        </button>
      </div>

      <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <!-- 图片上传区域 -->
          <div class="upload-area" :class="{ 'has-image': previewUrl }">
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              class="file-input"
              @change="handleFileChange"
            />

            <div v-if="!previewUrl" class="upload-placeholder" @click="triggerFileInput">
              <Upload :size="48" class="upload-icon" />
              <p class="upload-text">点击或拖拽图片到此处上传</p>
              <p class="upload-hint">支持 JPG、PNG、GIF 格式，最大 5MB</p>
            </div>

            <div v-else class="image-preview">
              <img :src="previewUrl" alt="预览" />
              <div class="preview-overlay">
                <button type="button" class="preview-btn" @click="triggerFileInput">
                  <Edit :size="18" />
                  <span>更换图片</span>
                </button>
                <button type="button" class="preview-btn" @click="removeImage">
                  <Trash2 :size="18" />
                  <span>删除</span>
                </button>
              </div>
            </div>
          </div>

          <!-- 表单字段 -->
          <div class="form-group">
            <label class="form-label">图片标题</label>
            <input
              v-model="formData.title"
              type="text"
              class="form-input"
              placeholder="请输入图片标题"
              maxlength="100"
            />
          </div>

          <div class="form-group">
            <label class="form-label">图片描述</label>
            <textarea
              v-model="formData.description"
              class="form-textarea"
              placeholder="请输入图片描述"
              rows="3"
            ></textarea>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">分类</label>
              <select v-model="formData.category" class="form-select">
                <option value="">请选择分类</option>
                <option value="头像">头像</option>
                <option value="横幅">横幅</option>
                <option value="文章配图">文章配图</option>
                <option value="其他">其他</option>
              </select>
            </div>

            <div class="form-group">
              <label class="form-label">状态</label>
              <select v-model.number="formData.status" class="form-select">
                <option :value="1">显示</option>
                <option :value="0">隐藏</option>
              </select>
            </div>
          </div>

          <!-- 上传进度 -->
          <div v-if="uploading" class="upload-progress">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: `${uploadProgress}%` }"></div>
            </div>
            <span class="progress-text">{{ uploadProgress }}%</span>
          </div>

          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="handleClose" :disabled="uploading">
              取消
            </button>
            <button type="submit" class="btn-primary" :disabled="!selectedFile || uploading">
              <Loader v-if="uploading" :size="16" class="spinning" />
              <span>{{ uploading ? '上传中...' : '上传' }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { X, Upload, Edit, Trash2, Loader } from 'lucide-vue-next'

interface ImageFormData {
  title: string
  description: string
  category: string
  status: number
}

interface Props {
  visible: boolean
}

defineProps<Props>()

const emit = defineEmits<{
  close: []
  submit: [data: ImageFormData & { file: File }]
}>()

const fileInput = ref<HTMLInputElement>()
const selectedFile = ref<File | null>(null)
const previewUrl = ref('')
const uploading = ref(false)
const uploadProgress = ref(0)

const formData = ref<ImageFormData>({
  title: '',
  description: '',
  category: '',
  status: 1
})

function triggerFileInput() {
  fileInput.value?.click()
}

function handleFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]

  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    alert('请选择图片文件')
    return
  }

  // 验证文件大小（5MB）
  if (file.size > 5 * 1024 * 1024) {
    alert('图片大小不能超过 5MB')
    return
  }

  selectedFile.value = file

  // 生成预览
  const reader = new FileReader()
  reader.onload = (e) => {
    previewUrl.value = e.target?.result as string
  }
  reader.readAsDataURL(file)

  // 自动填充标题
  if (!formData.value.title) {
    formData.value.title = file.name.replace(/\.[^/.]+$/, '')
  }
}

function removeImage() {
  selectedFile.value = null
  previewUrl.value = ''
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

function resetForm() {
  formData.value = {
    title: '',
    description: '',
    category: '',
    status: 1
  }
  removeImage()
  uploadProgress.value = 0
}

function handleClose() {
  if (!uploading.value) {
    emit('close')
    resetForm()
  }
}

async function handleSubmit() {
  if (!selectedFile.value) return

  uploading.value = true
  uploadProgress.value = 0

  try {
    // 模拟上传进度
    const interval = setInterval(() => {
      uploadProgress.value += 10
      if (uploadProgress.value >= 90) {
        clearInterval(interval)
      }
    }, 200)

    // 模拟API调用
    await new Promise((resolve) => setTimeout(resolve, 2000))

    uploadProgress.value = 100
    clearInterval(interval)

    emit('submit', {
      ...formData.value,
      file: selectedFile.value
    })

    handleClose()
  } catch (error) {
    console.error('上传失败:', error)
    alert('上传失败，请重试')
  } finally {
    uploading.value = false
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

.file-input {
  display: none;
}

.upload-area {
  position: relative;
  width: 100%;
  min-height: 240px;
  margin-bottom: var(--spacing-lg);
  border: 2px dashed var(--color-gray-300);
  border-radius: 12px;
  overflow: hidden;
  transition: all var(--transition-fast);
}

.upload-area:not(.has-image):hover {
  border-color: var(--color-miku-500);
  background: var(--color-miku-50);
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 240px;
  cursor: pointer;
}

.upload-icon {
  color: var(--color-gray-400);
  margin-bottom: var(--spacing-md);
}

.upload-text {
  margin: 0 0 var(--spacing-xs) 0;
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--color-gray-700);
}

.upload-hint {
  margin: 0;
  font-size: var(--text-sm);
  color: var(--color-gray-500);
}

.image-preview {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 240px;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: var(--color-gray-50);
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-md);
  background: rgba(0, 0, 0, 0.6);
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.image-preview:hover .preview-overlay {
  opacity: 1;
}

.preview-btn {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-white);
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.preview-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.form-group {
  margin-bottom: var(--spacing-lg);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-md);
}

.form-label {
  display: block;
  margin-bottom: var(--spacing-sm);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-gray-700);
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

.upload-progress {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-md);
  background: var(--color-miku-50);
  border-radius: 8px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: var(--color-gray-200);
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-miku-500), var(--color-cyan-500));
  transition: width 0.3s ease;
}

.progress-text {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--color-miku-600);
  min-width: 45px;
  text-align: right;
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

.btn-secondary:hover:not(:disabled) {
  background: var(--color-gray-50);
}

.btn-primary {
  color: var(--color-white);
  background: var(--color-miku-500);
}

.btn-primary:hover:not(:disabled) {
  background: var(--color-miku-600);
}

.btn-secondary:disabled,
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
