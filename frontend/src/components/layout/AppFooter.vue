<template>
  <footer class="app-footer">
    <div class="app-footer__divider"></div>
    <div class="app-footer__container">
      <p class="app-footer__text">© 2024 落叶无痕的博客. All rights reserved.</p>
      <div class="app-footer__links">
        <a href="https://github.com" target="_blank" class="footer-link">
          <Github :size="20" />
        </a>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { Github } from 'lucide-vue-next'
</script>

<style scoped>
.app-footer {
  margin-top: var(--spacing-2xl);
  padding: var(--spacing-2xl) 2vw var(--spacing-xl);
}

.app-footer__divider {
  height: 1px;
  background: var(--color-gray-200);
  margin-bottom: var(--spacing-2xl);
}

.app-footer__container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.app-footer__text {
  font-size: var(--text-sm);
  color: var(--color-gray-500);
}

.app-footer__links {
  display: flex;
  gap: var(--spacing-lg);
}

.footer-link {
  color: var(--color-gray-500);
  transition: all var(--transition-fast);
  padding: var(--spacing-sm);
  border-radius: var(--radius-md);
}

.footer-link:hover {
  color: var(--color-miku-500);
  background: var(--color-miku-50);
}

@media (max-width: 768px) {
  .app-footer__container {
    flex-direction: column;
    gap: var(--spacing-md);
    text-align: center;
  }
}
</style>
