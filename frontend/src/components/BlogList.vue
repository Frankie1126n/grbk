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
        v-for="(blog, index) in blogList"
        :key="blog.id"
        class="blog-card card"
        :data-index="index + 1"
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
        <div v-else-if="canDelete(blog)" class="blog-actions">
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
  mounted() {
    // 添加滚动监听
    window.addEventListener('scroll', this.handleScroll)
    
    // 初始加载时触发动画
    this.$nextTick(() => {
      const blogListElement = this.$el
      if (blogListElement) {
        blogListElement.classList.add('animate-enter')
      }
    })
  },
  beforeDestroy() {
    // 移除滚动监听
    window.removeEventListener('scroll', this.handleScroll)
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
      // 用户只能编辑自己的文章（即使是管理员也不能编辑其他用户的文章）
      return blog.username === this.userInfo.username
    },

    canDelete(blog) {
      // admin可以删除所有文章，其他用户只能删除自己的文章
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
.blog-list {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

/* 课程列表区域滚动触发效果 */
.blog-list.animate-enter .blog-card {
  opacity: 0;
  transform: translateX(-20px);
  animation: slideInFromLeft 0.3s ease-out forwards;
}

@keyframes slideInFromLeft {
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 为每个卡片设置延迟动画 */
.blog-list.animate-enter .blog-card:nth-child(1) { animation-delay: 0.1s; }
.blog-list.animate-enter .blog-card:nth-child(2) { animation-delay: 0.2s; }
.blog-list.animate-enter .blog-card:nth-child(3) { animation-delay: 0.3s; }
.blog-list.animate-enter .blog-card:nth-child(4) { animation-delay: 0.4s; }
.blog-list.animate-enter .blog-card:nth-child(5) { animation-delay: 0.5s; }

/* 课本页码效果 */
.blog-card::after {
  content: 'P' attr(data-index);
  position: absolute;
  bottom: 10px;
  right: 10px;
  font-size: 12px;
  color: rgba(255, 183, 197, 0.6);
  opacity: 0;
  transition: opacity 0.3s;
}

.blog-list.animate-enter .blog-card:hover::after {
  opacity: 1;
  animation: fadeOutPageNumber 0.3s 0.3s forwards;
}

@keyframes fadeOutPageNumber {
  to { opacity: 0; }
}

.skeleton-wrapper {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.skeleton-item {
  padding: 25px;
  background: rgba(255, 251, 235, 0.95);
  border: 2px solid rgba(255, 183, 197, 0.2);
}

.skeleton-line {
  height: 18px;
  background: linear-gradient(90deg, 
    rgba(255, 183, 197, 0.15) 25%, 
    rgba(163, 230, 53, 0.2) 50%, 
    rgba(255, 183, 197, 0.15) 75%);
  background-size: 200% 100%;
  animation: shimmer 1.6s ease-in-out infinite;
  border-radius: 10px;
  margin-bottom: 15px;
}

.skeleton-title {
  height: 28px;
  width: 65%;
}

.skeleton-short {
  width: 45%;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.blog-card {
  cursor: pointer;
  overflow: hidden;
  display: flex;
  gap: 25px;
  position: relative;
  background: rgba(255, 251, 235, 0.95);
  backdrop-filter: blur(15px) saturate(150%);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(255, 183, 197, 0.2),
              0 1px 3px rgba(163, 230, 53, 0.1);
  border: 2px solid rgba(255, 183, 197, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.blog-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 28px rgba(255, 183, 197, 0.3),
              0 2px 8px rgba(163, 230, 53, 0.15);
  border-color: rgba(163, 230, 53, 0.4);
}

.blog-actions {
  position: absolute;
  top: 24px;
  right: 24px;
  display: flex;
  gap: 12px;
  z-index: 10;
}

.blog-actions >>> .el-button {
  border-radius: 18px;
  font-weight: 600;
  border: 2px solid;
  backdrop-filter: blur(10px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: auto;
  padding: 9px 18px;
}

.blog-actions >>> .el-button--primary {
  background: linear-gradient(135deg, #FFB7C5, #FF9F43);
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0 3px 12px rgba(255, 183, 197, 0.35);
}

.blog-actions >>> .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 18px rgba(255, 183, 197, 0.45);
}

.blog-actions >>> .el-button--danger {
  background: linear-gradient(135deg, #FF9F43, #FFB7C5);
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0 3px 12px rgba(255, 159, 67, 0.35);
}

.blog-actions >>> .el-button--danger:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 18px rgba(255, 159, 67, 0.45);
}

.cover-image {
  flex-shrink: 0;
  width: 220px;
  height: 165px;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 3px 12px rgba(255, 183, 197, 0.25);
  border: 2px solid rgba(255, 183, 197, 0.2);
  position: relative;
}

.cover-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
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
  font-size: 22px;
  color: #4a4a4a;
  margin: 0 0 12px 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 700;
  line-height: 1.4;
}

.blog-card:hover .blog-title {
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.blog-title >>> .el-tag {
  border-radius: 10px;
  font-weight: 600;
  background: rgba(255, 159, 67, 0.15);
  border-color: rgba(255, 159, 67, 0.3);
  color: #FF9F43;
}

.blog-summary {
  color: #6b6b6b;
  font-size: 15px;
  line-height: 1.7;
  margin: 0 0 18px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.blog-meta {
  display: flex;
  gap: 24px;
  margin-top: auto;
  flex-wrap: wrap;
}

.meta-item {
  color: #9ca3af;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.meta-item i {
  color: #FFB7C5;
  font-size: 16px;
  transition: all 0.3s;
}

.meta-item.clickable {
  cursor: pointer;
}

.meta-item.clickable:hover,
.meta-item:hover {
  color: #FF9F43;
}

.meta-item.clickable:hover i,
.meta-item:hover i {
  transform: scale(1.15);
  color: #FF9F43;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 15px;
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li {
  background: rgba(255, 251, 235, 0.9);
  border: 2px solid rgba(255, 183, 197, 0.25);
  border-radius: 10px;
  margin: 0 5px;
  font-weight: 600;
  color: #FF9F43;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li:not(.disabled):hover {
  background: rgba(255, 183, 197, 0.2);
  border-color: #FFB7C5;
  transform: translateY(-2px);
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li:not(.disabled).active {
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  border-color: transparent;
  color: white;
  box-shadow: 0 3px 12px rgba(255, 183, 197, 0.4);
  transform: translateY(-2px);
}

.pagination-wrapper >>> .el-pagination button {
  background: rgba(255, 251, 235, 0.9);
  border: 2px solid rgba(255, 183, 197, 0.25);
  border-radius: 10px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.pagination-wrapper >>> .el-pagination button:not(:disabled):hover {
  background: rgba(255, 183, 197, 0.2);
  border-color: #FFB7C5;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #9ca3af;
  background: rgba(255, 251, 235, 0.9);
  border: 2px solid rgba(255, 183, 197, 0.2);
}

.empty-state i {
  font-size: 80px;
  background: linear-gradient(135deg, #FFB7C5, #FF9F43, #A3E635);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  margin-bottom: 20px;
}

.empty-state p {
  font-size: 18px;
  margin: 0;
  color: #6b6b6b;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .blog-card {
    flex-direction: column;
    padding: 20px;
  }
  
  .cover-image {
    width: 100%;
    height: 200px;
  }
  
  .blog-actions {
    position: static;
    margin-top: 15px;
    justify-content: center;
  }
  
  .blog-title {
    font-size: 20px;
  }
  
  .blog-meta {
    gap: 15px;
  }
  
  .meta-item {
    font-size: 13px;
  }
}

/* Small mobile devices */
@media (max-width: 480px) {
  .blog-card {
    padding: 15px;
  }
  
  .cover-image {
    height: 150px;
  }
  
  .blog-title {
    font-size: 18px;
  }
  
  .blog-summary {
    font-size: 14px;
  }
  
  .blog-meta {
    gap: 12px;
  }
  
  .meta-item {
    font-size: 12px;
    gap: 4px;
  }
  
  .meta-item i {
    font-size: 14px;
  }
}
</style>