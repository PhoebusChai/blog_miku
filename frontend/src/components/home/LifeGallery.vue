<template>
  <div class="life-gallery">
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
</template>

<script setup lang="ts">
import { ref } from 'vue'

interface Photo {
  id: number
  url: string
  location: string
  date: string
}

const photos = ref<Photo[]>([
  {
    id: 1,
    url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=500&fit=crop',
    location: '喜马拉雅山脉',
    date: '2024.01.15'
  },
  {
    id: 2,
    url: 'https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?w=400&h=300&fit=crop',
    location: '马尔代夫',
    date: '2024.01.12'
  },
  {
    id: 3,
    url: 'https://images.unsplash.com/photo-1511884642898-4c92249e20b6?w=400&h=450&fit=crop',
    location: '咖啡馆',
    date: '2024.01.10'
  },
  {
    id: 4,
    url: 'https://images.unsplash.com/photo-1495954484750-af469f2f9be5?w=400&h=350&fit=crop',
    location: '图书馆',
    date: '2024.01.08'
  },
  {
    id: 5,
    url: 'https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=400&h=550&fit=crop',
    location: '挪威峡湾',
    date: '2024.01.05'
  },
  {
    id: 6,
    url: 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400&h=400&fit=crop',
    location: '新西兰',
    date: '2024.01.03'
  },
  {
    id: 7,
    url: 'https://images.unsplash.com/photo-1472214103451-9374bd1c798e?w=400&h=480&fit=crop',
    location: '森林深处',
    date: '2023.12.28'
  },
  {
    id: 8,
    url: 'https://images.unsplash.com/photo-1501594907352-04cda38ebc29?w=400&h=320&fit=crop',
    location: '湖畔小屋',
    date: '2023.12.25'
  }
])
</script>

<style scoped>
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
}
</style>
