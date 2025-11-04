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
          <div class="blog-meta">
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
      refreshInterval: 10000 // 10秒刷新一次
    }
  },
  computed: {
    ...mapGetters('blog', ['currentBlog']),
    
    formattedContent() {
      if (!this.currentBlog || !this.currentBlog.content) {
        return ''
      }
      
      const baseUrl = process.env.VUE_APP_API_URL || 'http://localhost:8080/api'
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
  beforeDestroy() {
    // 清理全局方法
    delete window.previewBlogImage
    // 清除定时器
    this.stopAutoRefresh()
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
        await this.getBlogById(id)
      } catch (error) {
        this.$message.error('文章加载失败')
        this.$router.push('/home')
      } finally {
        this.loading = false
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
          this.getBlogById(id)
        }
      }, this.refreshInterval)
    },
    
    stopAutoRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer)
        this.refreshTimer = null
      }
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

.meta-item {
  color: #999;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.meta-item i {
  color: #4facfe;
}

.meta-item.clickable {
  cursor: pointer;
  transition: color 0.3s ease;
}

.meta-item.clickable:hover {
  color: #4facfe;
}

.blog-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag {
  padding: 4px 12px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  border-radius: 12px;
  font-size: 12px;
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
  background: rgba(79, 172, 254, 0.1);
  padding: 2px 6px;
  border-radius: 4px;
  color: #4facfe;
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

.blog-actions {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid rgba(79, 172, 254, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .blog-detail {
    padding: 20px;
  }
  
  .blog-title {
    font-size: 24px;
  }
  
  .blog-content {
    font-size: 14px;
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
</style>
