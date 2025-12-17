import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Article } from '@/types/article'

export const useArticleStore = defineStore('article', () => {
  const articles = ref<Article[]>([])
  const currentArticle = ref<Article | null>(null)

  function setArticles(data: Article[]) {
    articles.value = data
  }

  function setCurrentArticle(data: Article) {
    currentArticle.value = data
  }

  return {
    articles,
    currentArticle,
    setArticles,
    setCurrentArticle
  }
})
