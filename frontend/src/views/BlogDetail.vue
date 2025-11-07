<template>
  <div class="blog-detail-container">
    <div class="container">
      <div v-if="loading" class="loading-wrapper">
        <div class="loading"></div>
      </div>

      <div v-else-if="currentBlog" class="blog-detail card">
        <!-- Header -->
        <div class="blog-header">
          <h1 class="blog-title">{{ currentBlog.title }}</h1>
          <div class="blog-meta" ref="blogMeta">
            <span class="meta-item clickable" @click="goToUserProfile(currentBlog.userId)">
              <i class="el-icon-user"></i>
              {{ currentBlog.username }}
            </span>
            <span class="meta-item">
              <i class="el-icon-folder"></i>
              {{ currentBlog.categoryName }}
            </span>
            <span class="meta-item">
              <i class="el-icon-view"></i>
              {{ currentBlog.viewCount }}
            </span>
            <span class="meta-item">
              <i class="el-icon-star-off"></i>
              {{ currentBlog.likeCount || 0 }}
            </span>
            <span class="meta-item">
              <i class="el-icon-collection"></i>
              {{ currentBlog.favoriteCount || 0 }}
            </span>
            <span class="meta-item">
              <i class="el-icon-time"></i>
              {{ currentBlog.createTime }}
            </span>
          </div>

          <!-- Tags -->
          <div v-if="currentBlog.tags && currentBlog.tags.length" class="blog-tags">
            <span
              v-for="tag in currentBlog.tags"
              :key="tag.id"
              class="tag"
            >
              {{ tag.name }}
            </span>
          </div>
        </div>

        <!-- Content -->
        <div class="blog-content" v-html="formattedContent"></div>

        <!-- Interaction Buttons -->
        <div class="interaction-bar">
          <el-button
            :type="hasLiked ? 'primary' : 'default'"
            :icon="hasLiked ? 'el-icon-star-on' : 'el-icon-star-off'"
            @click="toggleLike"
          >
            {{ hasLiked ? '已点赞' : '点赞' }} ({{ currentBlog.likeCount || 0 }})
          </el-button>
          <el-button
            :type="hasFavorited ? 'warning' : 'default'"
            :icon="hasFavorited ? 'el-icon-collection' : 'el-icon-collection-tag'"
            @click="toggleFavorite"
          >
            {{ hasFavorited ? '已收藏' : '收藏' }} ({{ currentBlog.favoriteCount || 0 }})
          </el-button>
        </div>

        <!-- Actions -->
        <div class="blog-actions">
          <el-button @click="goBack">返回列表</el-button>
        </div>
      </div>

      <!-- Comment Section -->
      <CommentSection
        v-if="currentBlog"
        :blog-id="currentBlog.id"
        :blog-author-id="currentBlog.userId"
      />
    </div>

    <!-- Image Preview Dialog -->
    <el-dialog
      :visible.sync="imagePreviewVisible"
      width="450px"
      top="20vh"
      :append-to-body="true"
      custom-class="image-preview-dialog"
    >
      <img :src="previewImageUrl" style="width: 400px; height: 400px; object-fit: contain;" alt="Preview" />
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import CommentSection from '@/components/CommentSection'
import { likeBlog, unlikeBlog, checkLikeStatus, favoriteBlog, unfavoriteBlog, checkFavoriteStatus, getBlogDetailById } from '@/api/blog'

export default {
  name: 'BlogDetail',
  components: {
    CommentSection
  },
  data() {
    return {
      loading: false,
      imagePreviewVisible: false,
      previewImageUrl: '',
      refreshTimer: null,
      refreshInterval: 10000, // 10秒刷新一次
      hasLiked: false,
      hasFavorited: false
    }
  },
  mounted() {
    // 添加滚动监听
    window.addEventListener('scroll', this.handleScroll)

    // 初始加载时触发动画
    this.$nextTick(() => {
      const blogMetaElement = this.$refs.blogMeta
      if (blogMetaElement) {
        blogMetaElement.classList.add('animate-enter')
      }
    })
  },
  beforeDestroy() {
    // 移除滚动监听
    window.removeEventListener('scroll', this.handleScroll)
    // 清理全局方法
    delete window.previewBlogImage
    // 清除定时器
    this.stopAutoRefresh()
  },
  computed: {
    ...mapGetters('blog', ['currentBlog']),

    formattedContent() {
      if (!this.currentBlog || !this.currentBlog.content) {
        return ''
      }

      const baseUrl = process.env.VUE_APP_API_URL || '/api'
      let content = this.currentBlog.content

      // 转换 Markdown 图片语法为 HTML img 标签
      content = content.replace(/!\[([^\]]*)\]\(([^)]+)\)/g, (match, alt, src) => {
        // 如果是相对路径，添加完整 URL
        if (!src.startsWith('http')) {
          src = baseUrl + src
        }
        return `<img src="${src}" alt="${alt}" onclick="window.previewBlogImage('${src}')" style="width: auto; max-height: 50px; border-radius: 8px; margin: 15px 0; cursor: pointer; display: inline-block; vertical-align: middle;" />`
      })

      // 转换换行符为 <br>
      content = content.replace(/\n/g, '<br>')

      return content
    }
  },
  created() {
    this.loadBlogDetail()
    // 注册全局图片预览方法
    window.previewBlogImage = (src) => {
      this.previewImageUrl = src
      this.imagePreviewVisible = true
    }
    // 启动定时刷新
    this.startAutoRefresh()
  },
  methods: {
    ...mapActions('blog', ['getBlogById']),

    async loadBlogDetail() {
      const id = this.$route.params.id
      if (!id) {
        this.$message.error('文章不存在')
        this.$router.push('/home')
        return
      }

      this.loading = true
      try {
        // 首次加载使用常规接口，会增加阅读量
        await this.getBlogById(id)
        // 加载点赞和收藏状态
        await this.loadInteractionStatus()
      } catch (error) {
        this.$message.error('文章加载失败')
        this.$router.push('/home')
      } finally {
        this.loading = false
      }
    },

    async loadInteractionStatus() {
      try {
        const blogId = this.$route.params.id
        const [likeRes, favoriteRes] = await Promise.all([
          checkLikeStatus(blogId),
          checkFavoriteStatus(blogId)
        ])
        this.hasLiked = likeRes.data
        this.hasFavorited = favoriteRes.data
      } catch (error) {
        // 静默失败，不影响主流程
      }
    },

    async toggleLike() {
      try {
        const blogId = this.$route.params.id
        if (this.hasLiked) {
          await unlikeBlog(blogId)
          this.$message.success('取消点赞成功')
          this.hasLiked = false
        } else {
          await likeBlog(blogId)
          this.$message.success('点赞成功')
          this.hasLiked = true
        }
        // 刷新博客详情（不增加阅读量）
        const res = await getBlogDetailById(blogId)
        if (res.code === 200) {
          // 更新 Vuex 中的博客数据
          this.$store.commit('blog/SET_CURRENT_BLOG', res.data)
        }
      } catch (error) {
        this.$message.error(error.message || '操作失败')
      }
    },

    async toggleFavorite() {
      try {
        const blogId = this.$route.params.id
        if (this.hasFavorited) {
          await unfavoriteBlog(blogId)
          this.$message.success('取消收藏成功')
          this.hasFavorited = false
        } else {
          await favoriteBlog(blogId)
          this.$message.success('收藏成功')
          this.hasFavorited = true
        }
        // 刷新博客详情（不增加阅读量）
        const res = await getBlogDetailById(blogId)
        if (res.code === 200) {
          // 更新 Vuex 中的博客数据
          this.$store.commit('blog/SET_CURRENT_BLOG', res.data)
        }
      } catch (error) {
        this.$message.error(error.message || '操作失败')
      }
    },

    goBack() {
      this.$router.push('/home')
    },

    goToUserProfile(userId) {
      this.$router.push(`/user/${userId}`)
    },

    startAutoRefresh() {
      // 定时刷新博客详情和评论
      this.refreshTimer = setInterval(() => {
        const id = this.$route.params.id
        if (id) {
          // 使用不增加阅读量的接口进行定时刷新
          getBlogDetailById(id).then(res => {
            if (res.code === 200) {
              // 更新 Vuex 中的博客数据
              this.$store.commit('blog/SET_CURRENT_BLOG', res.data)
            }
          }).catch(error => {
            // 静默失败，不影响主流程
          })
        }
      }, this.refreshInterval)
    },

    stopAutoRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer)
        this.refreshTimer = null
      }
    },

    // 滚动处理函数
    handleScroll() {
      // 这里可以添加滚动相关的逻辑
      // 例如检测是否滚动到特定区域来触发动画
    }
  }
}
</script>

<style scoped>
.blog-detail-container {
  min-height: 100vh;
  padding: 40px 0;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 20px;
}

.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.blog-detail {
  padding: 40px;
}

.blog-header {
  border-bottom: 2px solid rgba(79, 172, 254, 0.2);
  padding-bottom: 20px;
  margin-bottom: 30px;
}

.blog-title {
  font-size: 32px;
  color: #333;
  margin: 0 0 20px 0;
  line-height: 1.4;
}

.blog-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 15px;
}

/* 社团展示区域滚动触发效果 */
.blog-meta.animate-enter .meta-item {
  opacity: 0;
  transform: translateY(10px);
  animation: popUpMeta 0.3s ease-out forwards;
}

@keyframes popUpMeta {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 为每个元数据项设置延迟动画 */
.blog-meta.animate-enter .meta-item:nth-child(1) { animation-delay: 0.1s; }
.blog-meta.animate-enter .meta-item:nth-child(2) { animation-delay: 0.2s; }
.blog-meta.animate-enter .meta-item:nth-child(3) { animation-delay: 0.3s; }
.blog-meta.animate-enter .meta-item:nth-child(4) { animation-delay: 0.4s; }
.blog-meta.animate-enter .meta-item:nth-child(5) { animation-delay: 0.5s; }
.blog-meta.animate-enter .meta-item:nth-child(6) { animation-delay: 0.6s; }

.meta-item {
  color: #9ca3af;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
  font-weight: 500;
}

.meta-item i {
  color: #FFB7C5;
}

.meta-item.clickable {
  cursor: pointer;
  transition: color 0.3s ease;
}

.meta-item.clickable:hover {
  color: #FF9F43;
}

.blog-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag {
  padding: 4px 12px;
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  color: white;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.blog-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  min-height: 200px;
}

.blog-content >>> h1,
.blog-content >>> h2,
.blog-content >>> h3 {
  margin: 25px 0 15px 0;
  color: #333;
}

.blog-content >>> p {
  margin: 15px 0;
}

.blog-content >>> code {
  background: rgba(255, 183, 197, 0.15);
  padding: 2px 6px;
  border-radius: 4px;
  color: #FF9F43;
  font-weight: 500;
}

.blog-content >>> pre {
  background: #f5f5f5;
  padding: 15px;
  border-radius: 8px;
  overflow-x: auto;
}

.blog-content >>> img {
  width: auto;
  max-height: 50px;
  border-radius: 8px;
  margin: 15px 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  display: inline-block;
  vertical-align: middle;
  transition: transform 0.3s;
}

.blog-content >>> img:hover {
  transform: scale(1.05);
}

.interaction-bar {
  margin-top: 30px;
  padding: 20px 0;
  border-top: 2px solid rgba(255, 183, 197, 0.2);
  border-bottom: 2px solid rgba(255, 183, 197, 0.2);
  display: flex;
  gap: 15px;
  justify-content: center;
}

.interaction-bar .el-button {
  min-width: 120px;
  border-radius: 20px;
  font-weight: 600;
  border: 2px solid;
}

.interaction-bar .el-button--primary {
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0 4px 15px rgba(255, 183, 197, 0.4);
}

.interaction-bar .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 183, 197, 0.5);
  border-color: rgba(255, 255, 255, 0.8);
}

.interaction-bar .el-button--warning {
  background: linear-gradient(135deg, #A3E635 0%, #87CEEB 100%);
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0 4px 15px rgba(163, 230, 53, 0.4);
}

.interaction-bar .el-button--warning:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(163, 230, 53, 0.5);
  border-color: rgba(255, 255, 255, 0.8);
}

.blog-actions {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 2px solid rgba(255, 183, 197, 0.2);
}

.blog-actions >>> .el-button {
  border-radius: 20px;
  font-weight: 600;
  border: 2px solid;
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(255, 183, 197, 0.4);
  color: #FF9F43;
}

.blog-actions >>> .el-button:hover {
  background: rgba(255, 255, 255, 1);
  border-color: #FFB7C5;
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .blog-detail-container {
    padding: 20px 0;
  }
  
  .container {
    padding: 0 15px;
  }
  
  .blog-detail {
    padding: 20px;
  }

  .blog-title {
    font-size: 24px;
  }

  .blog-content {
    font-size: 14px;
  }
  
  .blog-meta {
    gap: 15px;
  }
  
  .meta-item {
    font-size: 13px;
  }
  
  .interaction-bar {
    flex-direction: column;
    align-items: center;
    gap: 10px;
  }
  
  .interaction-bar .el-button {
    width: 100%;
    max-width: 200px;
  }
}

/* Small mobile devices */
@media (max-width: 480px) {
  .blog-detail {
    padding: 15px;
  }
  
  .blog-title {
    font-size: 20px;
  }
  
  .blog-content {
    font-size: 13px;
  }
  
  .blog-meta {
    gap: 10px;
  }
  
  .meta-item {
    font-size: 12px;
  }
  
  .tag {
    font-size: 11px;
    padding: 3px 10px;
  }
}

/* Image Preview Dialog */
.image-preview-dialog {
  background: rgba(0, 0, 0, 0.9);
}

.image-preview-dialog .el-dialog__header {
  display: none;
}

.image-preview-dialog .el-dialog__body {
  padding: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

@media (max-width: 480px) {
  .image-preview-dialog {
    width: 95% !important;
    margin: 10px auto !important;
  }
  
  .image-preview-dialog img {
    width: 100%;
    height: auto;
    max-height: 300px;
  }
}
</style>