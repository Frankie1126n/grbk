<template>
  <div class="blog-list">
    <!-- Loading Skeleton -->
    <div v-if="loading" class="skeleton-wrapper">
      <div v-for="i in 3" :key="i" class="skeleton-item card">
        <div class="skeleton-line skeleton-title"></div>
        <div class="skeleton-line"></div>
        <div class="skeleton-line"></div>
        <div class="skeleton-line skeleton-short"></div>
      </div>
    </div>
    
    <!-- Blog Cards -->
    <div v-else-if="blogList.length > 0">
      <div
        v-for="blog in blogList"
        :key="blog.id"
        class="blog-card card"
      >
        <div v-if="blog.coverImage" class="cover-image" @click="goToBlogDetail(blog.id)">
          <img :src="blog.coverImage" :alt="blog.title" />
        </div>
        
        <div class="blog-content" @click="goToBlogDetail(blog.id)">
          <h3 class="blog-title">
            {{ blog.title }}
            <el-tag v-if="blog.isPublic === 0" size="mini" type="warning" effect="dark">
              <i class="el-icon-lock"></i> 私密
            </el-tag>
          </h3>
          <p class="blog-summary">{{ blog.summary || '暂无摘要' }}</p>
          
          <div class="blog-meta">
            <span class="meta-item clickable" @click.stop="goToUserProfile(blog.userId)">
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
        
        <div v-if="canEdit(blog)" class="blog-actions">
          <el-button
            type="primary"
            size="small"
            icon="el-icon-edit"
            @click.stop="editBlog(blog.id)"
          >
            编辑
          </el-button>
          <el-button
            type="danger"
            size="small"
            icon="el-icon-delete"
            @click.stop="confirmDelete(blog.id)"
          >
            删除
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
      <i class="el-icon-document"></i>
      <p>暂无文章</p>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { deleteBlog } from '@/api/blog'

export default {
  name: 'BlogList',
  data() {
    return {
      loading: false
    }
  },
  computed: {
    ...mapGetters('blog', ['blogList', 'currentPage', 'pageSize', 'total']),
    ...mapGetters('user', ['userInfo'])
  },
  methods: {
    ...mapActions('blog', ['getBlogList']),
    
    async handlePageChange(page) {
      this.loading = true
      try {
        await this.getBlogList({ current: page })
      } finally {
        this.loading = false
      }
    },
    
    goToBlogDetail(id) {
      this.$router.push(`/blog/${id}`)
    },
    
    goToUserProfile(userId) {
      this.$router.push(`/user/${userId}`)
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
    
    canEdit(blog) {
      // admin可以编辑所有文章，其他用户只能编辑自己的文章
      return this.userInfo.role === 'admin' || blog.username === this.userInfo.username
    },
    
    editBlog(id) {
      this.$router.push(`/blog-editor/${id}`)
    },
    
    async confirmDelete(id) {
      try {
        await this.$confirm('确定要删除这篇文章吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await deleteBlog(id)
        this.$message.success('删除成功')
        
        // 刷新列表
        await this.getBlogList({ current: this.currentPage })
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '删除失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.blog-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
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
}

.blog-actions {
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
  gap: 20px;
  margin-top: auto;
}

.meta-item {
  color: #999;
  font-size: 13px;
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

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li:not(.disabled).active {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state i {
  font-size: 64px;
  color: rgba(79, 172, 254, 0.3);
  margin-bottom: 20px;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .blog-card {
    flex-direction: column;
  }
  
  .cover-image {
    width: 100%;
  }
}
</style>
