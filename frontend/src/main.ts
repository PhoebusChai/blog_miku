import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// Element Plus Message 组件
import { ElMessage } from 'element-plus'
import 'element-plus/es/components/message/style/css'

import '@/assets/styles/variables.css'
import '@/assets/styles/glass.css'
import '@/assets/styles/global.css'

const app = createApp(App)

// 将 ElMessage 挂载到全局
app.config.globalProperties.$message = ElMessage

app.use(createPinia())
app.use(router)

app.mount('#app')
