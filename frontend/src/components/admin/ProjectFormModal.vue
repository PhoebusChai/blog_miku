<template>
  <div v-if="visible" class="modal-overlay" @click.self="handleClose">
    <div class="modal-container">
      <div class="modal-header">
        <h3 class="modal-title">{{ isEdit ? '编辑项目' : '新建项目' }}</h3>
        <button class="close-btn" @click="handleClose">
          <X :size="20" />
        </button>
      </div>

      <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label class="form-label required">项目名称</label>
            <input
              v-model="formData.title"
              type="text"
              class="form-input"
              placeholder="请输入项目名称"
              maxlength="100"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">项目描述</label>
            <textarea
              v-model="formData.description"
              class="form-textarea"
              placeholder="请输入项目描述"
              rows="4"
            ></textarea>
          </div>

          <div class="form-group">
            <label class="form-label">封面图片</label>
            <div class="cover-upload-section">
              <div class="cover-preview" @click="triggerCoverInput">
                <img v-if="coverPreview" :src="coverPreview" alt="封面预览" />
                <div v-else class="cover-placeholder">
                  <ImageIcon :size="48" />
                  <span>点击上传封面图片</span>
                </div>
                <div v-if="coverPreview" class="cover-overlay">
                  <Upload :size="24" />
                  <span>更换封面</span>
                </div>
              </div>
              <input
                ref="coverFileInput"
                type="file"
                accept="image/*"
                class="file-input"
                @change="handleCoverFileChange"
              />
            </div>
            <div class="url-input-section">
              <span class="url-label">或输入图片URL：</span>
              <input
                v-model="formData.coverImage"
                type="url"
                class="form-input"
                placeholder="https://example.com/cover.jpg"
                @input="handleCoverUrlChange"
              />
            </div>
            <span class="form-hint">建议尺寸：800x450 像素，支持 JPG、PNG 格式</span>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">演示地址</label>
              <input
                v-model="formData.demoUrl"
                type="url"
                class="form-input"
                placeholder="https://example.com"
              />
            </div>

            <div class="form-group">
              <label class="form-label">GitHub地址</label>
              <input
                v-model="formData.githubUrl"
                type="url"
                class="form-input"
                placeholder="https://github.com/..."
              />
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">技术栈</label>
            <div class="tech-input-wrapper">
              <input
                v-model="techInput"
                type="text"
                class="form-input"
                placeholder="输入技术名称后按回车添加"
                @keydown.enter.prevent="addTech"
              />
              <button type="button" class="add-tech-btn" @click="addTech">
                <Plus :size="16" />
              </button>
            </div>
            <div v-if="formData.techStack.length > 0" class="tech-tags">
              <span v-for="(tech, index) in formData.techStack" :key="index" class="tech-tag">
                {{ tech }}
                <button type="button" class="remove-tech-btn" @click="removeTech(index)">
                  <X :size="14" />
                </button>
              </span>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label required">项目状态</label>
            <select v-model.number="formData.status" class="form-select" required>
              <option :value="0">进行中</option>
              <option :value="1">已完成</option>
              <option :value="2">已归档</option>
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
import { X, Plus, Loader, Upload, Image as ImageIcon } from 'lucide-vue-next'

interface ProjectFormData {
  title: string
  description: string
  coverImage: string
  demoUrl: string
  githubUrl: string
  techStack: string[]
  status: number
}

interface Props {
  visible: boolean
  isEdit?: boolean
  initialData?: ProjectFormData | null
}

const props = withDefaults(defineProps<Props>(), {
  isEdit: false,
  initialData: null
})

const emit = defineEmits<{
  close: []
  submit: [data: ProjectFormData]
}>()

const loading = ref(false)
const techInput = ref('')
const coverFileInput = ref<HTMLInputElement>()
const coverPreview = ref('')
const coverFile = ref<File | null>(null)

const formData = ref<ProjectFormData>({
  title: '',
  description: '',
  coverImage: '',
  demoUrl: '',
  githubUrl: '',
  techStack: [],
  status: 0
})

watch(
  () => props.initialData,
  (newData) => {
    if (newData) {
      formData.value = { ...newData }
      coverPreview.value = newData.coverImage || ''
      coverFile.value = null
    } else {
      resetForm()
    }
  },
  { immediate: true }
)

function resetForm() {
  formData.value = {
    title: '',
    description: '',
    coverImage: '',
    demoUrl: '',
    githubUrl: '',
    techStack: [],
    status: 0
  }
  techInput.value = ''
  coverPreview.value = ''
  coverFile.value = null
  if (coverFileInput.value) {
    coverFileInput.value.value = ''
  }
}

function triggerCoverInput() {
  coverFileInput.value?.click()
}

function handleCoverFileChange(event: Event) {
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

  coverFile.value = file

  // 生成预览
  const reader = new FileReader()
  reader.onload = (e) => {
    coverPreview.value = e.target?.result as string
    formData.value.coverImage = '' // 清空URL输入
  }
  reader.readAsDataURL(file)
}

function handleCoverUrlChange() {
  if (formData.value.coverImage) {
    coverPreview.value = formData.value.coverImage
    coverFile.value = null
    if (coverFileInput.value) {
      coverFileInput.value.value = ''
    }
  }
}

function addTech() {
  const tech = techInput.value.trim()
  if (tech && !formData.value.techStack.includes(tech)) {
    formData.value.techStack.push(tech)
    techInput.value = ''
  }
}

function removeTech(index: number) {
  formData.value.techStack.splice(index, 1)
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
    if (coverFile.value) {
      // TODO: 实际项目中应该调用上传API
      console.log('上传封面文件:', coverFile.value)
      // formData.value.coverImage = await uploadFile(coverFile.value)
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
  max-width: 700px;
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

.cover-upload-section {
  margin-bottom: var(--spacing-md);
}

.cover-preview {
  position: relative;
  width: 100%;
  height: 200px;
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

.cover-preview:hover {
  border-color: var(--color-miku-500);
  background: var(--color-miku-50);
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
  color: var(--color-gray-500);
}

.cover-placeholder span {
  font-size: var(--text-sm);
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  background: rgba(0, 0, 0, 0.6);
  color: var(--color-white);
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.cover-preview:hover .cover-overlay {
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

.tech-input-wrapper {
  display: flex;
  gap: var(--spacing-sm);
}

.add-tech-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-miku-500);
  border: none;
  border-radius: 6px;
  color: var(--color-white);
  cursor: pointer;
  transition: all var(--transition-fast);
  flex-shrink: 0;
}

.add-tech-btn:hover {
  background: var(--color-miku-600);
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-sm);
}

.tech-tag {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: 4px 12px;
  font-size: var(--text-sm);
  color: var(--color-miku-700);
  background: var(--color-miku-50);
  border-radius: 6px;
}

.remove-tech-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  color: var(--color-miku-600);
  cursor: pointer;
  padding: 0;
  transition: color var(--transition-fast);
}

.remove-tech-btn:hover {
  color: var(--color-miku-800);
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
