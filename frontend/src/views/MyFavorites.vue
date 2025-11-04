<template>
  <div class="my-favorites-container">
    <div class="container">
      <!-- Header -->
      <div class="page-header card">
        <h1 class="gradient-text">
          <i class="el-icon-collection"></i>
          我的收藏
        </h1>
        <p class="subtitle">共收藏 {{ total }} 篇文章</p>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="skeleton-wrapper">
        <div v-for="i in 3" :key="i" class="skeleton-item card">
          <div class="skeleton-line skeleton-title"></div>
          <div class="skeleton-line"></div>
          <div class="skeleton-line"></div>
          <div class="skeleton-line skeleton-short"></div>
        </div>
      </div>

      <!-- Favorites List -->
      <div v-else-if="favoriteList.length > 0">
        <div
          v-for="blog in favoriteList"
          :key="blog.id"
          class="blog-card card"
          @click="goToBlogDetail(blog.id)"
        >
          <div v-if="blog.coverImage" class="cover-image">
            <img :src="blog.coverImage" :alt="blog.title" />
          </div>
          
          <div class="blog-content">
            <h3 class="blog-title">
              {{ blog.title }}
              <el-tag v-if="blog.isPublic === 0" size="mini" type="warning" effect="dark">
                <i class="el-icon-lock"></i> 私密
              </el-tag>
            </h3>
            <p class="blog-summary">{{ blog.summary || '暂无摘要' }}</p>
            
            <div class="blog-meta">
              <span class="meta-item" @click.stop="goToUserProfile(blog.userId)">
                <i class="el-icon-user"></i>
                {{ blog.username }}
              </span>
              <span class="meta-item">
                <i class="el-icon-folder"></i>
                {{ blog.categoryName }}
              </span>
              <span class="meta-item">
                <i class="el-icon-view"></i>
                {{ blog.viewCount }}
              </span>
              <span class="meta-item">
                <i class="el-icon-star-off"></i>
                {{ blog.likeCount || 0 }}
              </span>
              <span class="meta-item">
                <i class="el-icon-collection"></i>
                {{ blog.favoriteCount || 0 }}
              </span>
              <span class="meta-item">
                <i class="el-icon-time"></i>
                {{ formatTime(blog.createTime) }}
              </span>
            </div>
          </div>

          <div class="favorite-actions">
            <el-button
              type="danger"
              size="small"
              icon="el-icon-delete"
              @click.stop="confirmUnfavorite(blog.id)"
            >
              取消收藏
            </el-button>
          </div>
        </div>

        <!-- Pagination -->
        <div class="pagination-wrapper">
          <el-pagination
            background
            layout="prev, pager, next, total"
            :total="total"
            :page-size="pageSize"
            :current-page="currentPage"
            @current-change="handlePageChange"
          />
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="empty-state card">
        <i class="el-icon-collection-tag"></i>
        <p>还没有收藏任何文章</p>
        <el-button type="primary" @click="goToHome">去首页看看</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { getUserFavorites, unfavoriteBlog } from '@/api/blog'

export default {
  name: 'MyFavorites',
  data() {
    return {
      loading: false,
      favoriteList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0
    }
  },
  created() {
    this.loadFavorites()
  },
  methods: {
    async loadFavorites() {
      this.loading = true
      try {
        const res = await getUserFavorites({
          current: this.currentPage,
          size: this.pageSize
        })
        this.favoriteList = res.data.records
        this.total = res.data.total
      } catch (error) {
        this.$message.error('加载收藏列表失败')
      } finally {
        this.loading = false
      }
    },

    async handlePageChange(page) {
      this.currentPage = page
      await this.loadFavorites()
    },

    goToBlogDetail(id) {
      this.$router.push(`/blog/${id}`)
    },

    goToUserProfile(userId) {
      this.$router.push(`/user/${userId}`)
    },

    goToHome() {
      this.$router.push('/home')
    },

    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
      if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
      if (diff < 2592000000) return `${Math.floor(diff / 86400000)}天前`
      
      return time.split(' ')[0]
    },

    async confirmUnfavorite(blogId) {
      try {
        await this.$confirm('确定要取消收藏这篇文章吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await unfavoriteBlog(blogId)
        this.$message.success('取消收藏成功')
        
        // 刷新列表
        await this.loadFavorites()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '操作失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.my-favorites-container {
  min-height: 100vh;
  padding: 40px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  padding: 40px 20px;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 32px;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.subtitle {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.skeleton-wrapper {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.skeleton-item {
  padding: 20px;
}

.skeleton-line {
  height: 16px;
  background: linear-gradient(90deg, rgba(79, 172, 254, 0.1) 25%, rgba(79, 172, 254, 0.2) 50%, rgba(79, 172, 254, 0.1) 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
  margin-bottom: 12px;
}

.skeleton-title {
  height: 24px;
  width: 60%;
}

.skeleton-short {
  width: 40%;
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

.blog-card {
  cursor: pointer;
  overflow: hidden;
  display: flex;
  gap: 20px;
  position: relative;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.blog-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(79, 172, 254, 0.3);
}

.favorite-actions {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  gap: 10px;
  z-index: 10;
}

.cover-image {
  flex-shrink: 0;
  width: 200px;
  height: 150px;
  border-radius: 8px;
  overflow: hidden;
}

.cover-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.blog-card:hover .cover-image img {
  transform: scale(1.1);
}

.blog-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.blog-title {
  font-size: 20px;
  color: #333;
  margin: 0 0 10px 0;
  transition: color 0.3s ease;
  display: flex;
  align-items: center;
  gap: 10px;
}

.blog-card:hover .blog-title {
  color: #4facfe;
}

.blog-summary {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 15px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.blog-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-top: auto;
}

.meta-item {
  color: #999;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.meta-item i {
  color: #4facfe;
}

.meta-item:hover {
  color: #4facfe;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li:not(.disabled).active {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #999;
}

.empty-state i {
  font-size: 80px;
  color: rgba(79, 172, 254, 0.3);
  margin-bottom: 20px;
}

.empty-state p {
  font-size: 16px;
  margin: 0 0 30px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .blog-card {
    flex-direction: column;
  }
  
  .cover-image {
    width: 100%;
  }

  .favorite-actions {
    position: static;
    margin-top: 15px;
    justify-content: center;
  }
}
</style>
