<template>
  <div class="user-profile-page">
    <div class="profile-container">
      <div v-if="loading" class="loading-wrapper">
        <div class="loading"></div>
      </div>
      
      <div v-else-if="userProfile" class="profile-content card">
        <!-- User Header -->
        <div class="profile-header">
          <el-avatar :src="userProfile.avatarUrl || defaultAvatar" :size="120" />
          <h2 class="username">{{ userProfile.username }}</h2>
          <p class="email">{{ userProfile.email }}</p>
          <span class="role-badge" :class="'role-' + userProfile.role">
            {{ userProfile.role === 'admin' ? '管理员' : '普通用户' }}
          </span>
        </div>
        
        <!-- User Stats -->
        <div class="user-stats">
          <div class="stat-item">
            <div class="stat-number">{{ userBlogs.length }}</div>
            <div class="stat-label">文章数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ totalViews }}</div>
            <div class="stat-label">总阅读</div>
          </div>
        </div>
        
        <!-- User Blogs -->
        <div class="user-blogs">
          <h3 class="section-title gradient-text">Ta的文章</h3>
          <div v-if="userBlogs.length > 0" class="blog-list">
            <div
              v-for="blog in userBlogs"
              :key="blog.id"
              class="blog-item"
              @click="goToBlogDetail(blog.id)"
            >
              <h4 class="blog-title">{{ blog.title }}</h4>
              <p class="blog-summary">{{ blog.summary || '暂无摘要' }}</p>
              <div class="blog-info">
                <span><i class="el-icon-view"></i> {{ blog.viewCount }}</span>
                <span><i class="el-icon-time"></i> {{ formatTime(blog.createTime) }}</span>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <i class="el-icon-document"></i>
            <p>该用户暂无文章</p>
          </div>
        </div>
        
        <!-- Back Button -->
        <div class="actions">
          <el-button @click="goBack">返回</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getUserById } from '@/api/user'
import { getBlogList } from '@/api/blog'

export default {
  name: 'UserProfile',
  data() {
    return {
      loading: false,
      userProfile: null,
      userBlogs: [],
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    }
  },
  computed: {
    totalViews() {
      return this.userBlogs.reduce((sum, blog) => sum + (blog.viewCount || 0), 0)
    }
  },
  created() {
    this.loadUserProfile()
  },
  methods: {
    async loadUserProfile() {
      const userId = this.$route.params.userId
      if (!userId) {
        this.$message.error('用户不存在')
        this.$router.push('/home')
        return
      }
      
      this.loading = true
      try {
        // 获取用户信息
        const userRes = await getUserById(userId)
        this.userProfile = userRes.data
        
        // 获取用户文章
        const blogsRes = await getBlogList({
          current: 1,
          size: 100,
          userId: userId
        })
        this.userBlogs = blogsRes.data.records || []
      } catch (error) {
        this.$message.error('加载失败')
        this.$router.push('/home')
      } finally {
        this.loading = false
      }
    },
    
    goToBlogDetail(id) {
      this.$router.push(`/blog/${id}`)
    },
    
    goBack() {
      this.$router.go(-1)
    },
    
    formatTime(time) {
      if (!time) return ''
      return time.split(' ')[0]
    }
  }
}
</script>

<style scoped>
.user-profile-page {
  min-height: 100vh;
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
}

.user-profile-page::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    linear-gradient(rgba(79, 172, 254, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(79, 172, 254, 0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  pointer-events: none;
}

.profile-container {
  max-width: 900px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.profile-content {
  padding: 40px;
}

.profile-header {
  text-align: center;
  padding-bottom: 30px;
  border-bottom: 2px solid rgba(79, 172, 254, 0.2);
  margin-bottom: 30px;
}

.username {
  font-size: 28px;
  color: #333;
  margin: 20px 0 10px 0;
}

.email {
  color: #666;
  font-size: 14px;
  margin: 0 0 15px 0;
}

.role-badge {
  display: inline-block;
  padding: 5px 15px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: bold;
}

.role-admin {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e8e 100%);
  color: white;
}

.role-user {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.user-stats {
  display: flex;
  justify-content: center;
  gap: 60px;
  margin-bottom: 40px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #4facfe;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.user-blogs {
  margin-bottom: 30px;
}

.section-title {
  font-size: 20px;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid rgba(79, 172, 254, 0.2);
}

.blog-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.blog-item {
  padding: 20px;
  background: rgba(79, 172, 254, 0.05);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(79, 172, 254, 0.1);
}

.blog-item:hover {
  background: rgba(79, 172, 254, 0.1);
  transform: translateX(5px);
}

.blog-title {
  font-size: 16px;
  color: #333;
  margin: 0 0 10px 0;
}

.blog-summary {
  font-size: 14px;
  color: #666;
  margin: 0 0 10px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.blog-info {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #999;
}

.blog-info span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.blog-info i {
  color: #4facfe;
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

.actions {
  text-align: center;
}
</style>
