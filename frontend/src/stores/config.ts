import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getPublicConfig, type SiteConfig } from '@/api/config'

export const useConfigStore = defineStore('config', () => {
  const config = ref<SiteConfig>({})
  const loaded = ref(false)
  const loading = ref(false)

  // 基本设置
  const siteName = computed(() => config.value.site_name || '我的博客')
  const siteDescription = computed(() => config.value.site_description || '分享技术、记录生活')
  const siteKeywords = computed(() => config.value.site_keywords || '')
  const siteLogo = computed(() => config.value.site_logo || '/logo.png')
  const siteFavicon = computed(() => config.value.site_favicon || '/favicon.ico')
  const siteStartDate = computed(() => config.value.site_start_date || '2024-01-01')
  const siteCopyright = computed(() => config.value.site_copyright || `© ${new Date().getFullYear()} 我的博客`)

  // 博主信息
  const bloggerAvatar = computed(() => config.value.blogger_avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix')
  const bloggerName = computed(() => config.value.blogger_name || '博主')
  const bloggerRole = computed(() => config.value.blogger_role || '开发者')
  const bloggerLocation = computed(() => config.value.blogger_location || '')
  const bloggerBio = computed(() => config.value.blogger_bio || '热爱技术，喜欢分享。')
  const bloggerSkills = computed(() => {
    const skills = config.value.blogger_skills || ''
    return skills.split(',').map(s => s.trim()).filter(Boolean)
  })
  const bloggerInterests = computed(() => {
    const interests = config.value.blogger_interests || ''
    return interests.split(',').map(s => s.trim()).filter(Boolean)
  })

  // 社交媒体
  const socialGithub = computed(() => config.value.social_github || '')
  const socialBilibili = computed(() => config.value.social_bilibili || '')
  const socialEmail = computed(() => config.value.social_email || '')
  const socialTwitter = computed(() => config.value.social_twitter || '')
  const socialWeibo = computed(() => config.value.social_weibo || '')
  const socialZhihu = computed(() => config.value.social_zhihu || '')
  const socialWechat = computed(() => config.value.social_wechat || '')

  // 加载配置
  async function loadConfig() {
    if (loaded.value || loading.value) return
    
    loading.value = true
    try {
      const data = await getPublicConfig()
      config.value = data
      loaded.value = true
    } catch (error) {
      console.error('加载网站配置失败:', error)
    } finally {
      loading.value = false
    }
  }

  // 更新配置（用于管理后台保存后刷新）
  function updateConfig(newConfig: SiteConfig) {
    config.value = { ...config.value, ...newConfig }
  }

  // 重置配置
  function resetConfig() {
    config.value = {}
    loaded.value = false
  }

  return {
    config,
    loaded,
    loading,
    // 基本设置
    siteName,
    siteDescription,
    siteKeywords,
    siteLogo,
    siteFavicon,
    siteStartDate,
    siteCopyright,
    // 博主信息
    bloggerAvatar,
    bloggerName,
    bloggerRole,
    bloggerLocation,
    bloggerBio,
    bloggerSkills,
    bloggerInterests,
    // 社交媒体
    socialGithub,
    socialBilibili,
    socialEmail,
    socialTwitter,
    socialWeibo,
    socialZhihu,
    socialWechat,
    // 方法
    loadConfig,
    updateConfig,
    resetConfig
  }
})
