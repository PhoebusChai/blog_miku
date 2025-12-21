<template>
  <div class="life-gallery-wrapper">
    <!-- 空状态 -->
    <div v-if="photos.length === 0" class="life-empty">
      <div class="life-empty-icon">
        <Image :size="48" />
      </div>
      <p class="life-empty-text">暂无照片</p>
      <p class="life-empty-hint">期待分享生活的美好瞬间 ✨</p>
    </div>

    <!-- 照片列表 -->
    <div v-else class="life-gallery">
      <div class="life-item" v-for="photo in photos" :key="photo.id">
        <div class="photo-frame">
          <div class="photo-image">
            <img :src="photo.url" :alt="photo.location" loading="lazy" />
          </div>
          <div class="photo-caption">
            <p class="photo-location">{{ photo.location }}</p>
            <p class="photo-date">{{ photo.date }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Image } from 'lucide-vue-next'

interface Photo {
  id: number
  url: string
  location: string
  date: string
}

interface Props {
  photos?: Photo[]
}

const props = withDefaults(defineProps<Props>(), {
  photos: () => []
})

const photos = ref<Photo[]>(props.photos)
</script>

<style scoped>
.life-gallery-wrapper {
  width: 100%;
}

/* 空状态 */
.life-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl) var(--spacing-2xl);
  min-height: 280px;
  border-radius: var(--radius-lg);
  text-align: center;
}

.life-empty-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  background: linear-gradient(135deg, rgba(57, 197, 187, 0.1), rgba(57, 197, 187, 0.05));
  border-radius: 50%;
  margin-bottom: var(--spacing-lg);
}

.life-empty-icon svg {
  color: var(--color-miku-400);
  opacity: 0.8;
}

.life-empty-text {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--color-gray-600);
  margin: 0 0 var(--spacing-xs) 0;
}

.life-empty-hint {
  font-size: var(--text-sm);
  color: var(--color-gray-400);
  margin: 0;
}

.life-gallery {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: var(--spacing-lg);
  align-items: start;
}

.life-item {
  width: 100%;
}

/* 相纸样式 */
.photo-frame {
  background: var(--color-white);
  padding: 12px;
  padding-bottom: 48px;
  border-radius: 2px;
  box-shadow: 
    0 2px 4px rgba(0, 0, 0, 0.1),
    0 4px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all var(--transition-base);
  transform: rotate(0deg);
  position: relative;
}

.life-item:nth-child(6n + 1) .photo-frame {
  transform: rotate(-1.5deg);
}

.life-item:nth-child(6n + 2) .photo-frame {
  transform: rotate(1deg);
}

.life-item:nth-child(6n + 3) .photo-frame {
  transform: rotate(-0.8deg);
}

.life-item:nth-child(6n + 4) .photo-frame {
  transform: rotate(1.2deg);
}

.life-item:nth-child(6n + 5) .photo-frame {
  transform: rotate(-1deg);
}

.life-item:nth-child(6n + 6) .photo-frame {
  transform: rotate(0.5deg);
}

.photo-frame:hover {
  transform: rotate(0deg) translateY(-6px) scale(1.03);
  box-shadow: 
    0 8px 16px rgba(0, 0, 0, 0.12),
    0 12px 32px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.photo-image {
  position: relative;
  width: 100%;
  padding-bottom: 133.33%; /* 3:4 比例 */
  overflow: hidden;
  background: var(--color-gray-100);
  margin-bottom: var(--spacing-md);
}

.photo-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-base);
}

.photo-frame:hover .photo-image img {
  transform: scale(1.05);
}

/* 相纸底部文字区域 */
.photo-caption {
  position: absolute;
  bottom: 12px;
  left: 12px;
  right: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--spacing-sm);
  font-family: 'Courier New', 'Consolas', monospace;
  color: var(--color-gray-700);
}

.photo-location {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.photo-date {
  font-size: var(--text-xs);
  color: var(--color-gray-500);
  white-space: nowrap;
}

@media (max-width: 1200px) {
  .life-gallery {
    grid-template-columns: repeat(4, 1fr);
    gap: var(--spacing-md);
  }
}

@media (max-width: 1024px) {
  .life-gallery {
    grid-template-columns: repeat(3, 1fr);
    gap: var(--spacing-md);
  }
}

@media (max-width: 768px) {
  .life-gallery {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-md);
  }
  
  .photo-frame {
    padding: 10px;
    padding-bottom: 40px;
  }
  
  .photo-caption {
    bottom: 10px;
    left: 10px;
    right: 10px;
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
  }
  
  .photo-location {
    font-size: var(--text-xs);
  }
  
  .photo-date {
    font-size: 10px;
  }
}

@media (max-width: 480px) {
  .life-gallery {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-sm);
  }
  
  .photo-frame {
    padding: 8px;
    padding-bottom: 36px;
  }
  
  .photo-caption {
    bottom: 8px;
    left: 8px;
    right: 8px;
  }

  .life-empty {
    padding: var(--spacing-2xl) var(--spacing-lg);
    min-height: 220px;
  }

  .life-empty-icon {
    width: 56px;
    height: 56px;
  }

  .life-empty-icon svg {
    width: 32px;
    height: 32px;
  }
}
</style>
