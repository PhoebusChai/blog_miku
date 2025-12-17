<template>
  <div id="app" class="app-container">
    <RouterView />
    <Live2DWidget />
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import Live2DWidget from './components/layout/Live2DWidget.vue'
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  background: linear-gradient(135deg, 
    var(--color-pink-50) 0%, 
    var(--color-blue-50) 100%);
}
</style>
