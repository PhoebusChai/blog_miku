<template>
  <div v-if="visible" class="modal-overlay" @click.self="handleClose">
    <div class="modal-container">
      <div class="modal-header">
        <h3 class="modal-title">{{ isEdit ? '编辑标签' : '新建标签' }}</h3>
        <button class="close-btn" @click="handleClose">
          <X :size="20" />
        </button>
      </div>

      <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label class="form-label required">标签名称</label>
            <input
              v-model="formData.name"
              type="text"
              class="form-input"
              placeholder="请输入标签名称"
              maxlength="50"
              required
            />
            <span class="form-hint">标签的显示名称，最多50个字符</span>
          </div>

          <div class="form-group">
            <label class="form-label required">标签别名</label>
            <input
              v-model="formData.slug"
              type="text"
              class="form-input"
              placeholder="请输入标签别名（URL友好）"
              maxlength="50"
              pattern="[a-z0-9-]+"
              required
            />
            <span class="form-hint">用于URL，只能包含小写字母、数字和连字符</span>
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
import { X, Loader } from 'lucide-vue-next'

interface TagFormData {
  name: string
  slug: string
}

interface Props {
  visible: boolean
  isEdit?: boolean
  initialData?: TagFormData | null
}

const props = withDefaults(defineProps<Props>(), {
  isEdit: false,
  initialData: null
})

const emit = defineEmits<{
  close: []
  submit: [data: TagFormData]
}>()

const loading = ref(false)
const formData = ref<TagFormData>({
  name: '',
  slug: ''
})

// 监听初始数据变化
watch(
  () => props.initialData,
  (newData) => {
    if (newData) {
      formData.value = { ...newData }
    } else {
      resetForm()
    }
  },
  { immediate: true }
)

// 自动生成 slug
watch(
  () => formData.value.name,
  (newName) => {
    if (!props.isEdit && newName) {
      formData.value.slug = newName
        .toLowerCase()
        .replace(/\s+/g, '-')
        .replace(/[^a-z0-9-]/g, '')
    }
  }
)

function resetForm() {
  formData.value = {
    name: '',
    slug: ''
  }
}

function handleClose() {
  emit('close')
  resetForm()
}

function handleSubmit() {
  emit('submit', { ...formData.value })
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

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-container {
  width: 90%;
  max-width: 500px;
  background: var(--color-white);
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.3s ease;
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

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-xl);
  border-bottom: 1px solid var(--color-gray-200);
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
}

.form-group {
  margin-bottom: var(--spacing-lg);
}

.form-group:last-of-type {
  margin-bottom: 0;
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

.form-input {
  width: 100%;
  padding: var(--spacing-sm) var(--spacing-md);
  font-size: var(--text-sm);
  color: var(--color-gray-900);
  background: var(--color-white);
  border: 1px solid var(--color-gray-300);
  border-radius: 6px;
  transition: all var(--transition-fast);
}

.form-input:focus {
  outline: none;
  border-color: var(--color-miku-500);
  box-shadow: 0 0 0 3px rgba(57, 197, 187, 0.1);
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
</style>
