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
        <div class="user-blogs" ref="blogList">
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
  mounted() {
    // 添加滚动监听
    window.addEventListener('scroll', this.handleScroll)

    // 初始加载时触发动画
    this.$nextTick(() => {
      const blogListElement = this.$refs.blogList
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
.user-profile-page {
  min-height: 100vh;
  padding: 40px 20px;
  position: relative;
}

.user-profile-page::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
    radial-gradient(circle at 20% 30%, rgba(255, 183, 197, 0.1) 0%, transparent 40%),
    radial-gradient(circle at 80% 70%, rgba(163, 230, 53, 0.08) 0%, transparent 40%),
    radial-gradient(circle at 50% 50%, rgba(135, 206, 235, 0.06) 0%, transparent 50%);
  animation: sakuraFloat 20s ease-in-out infinite;
  z-index: -1;
  pointer-events: none;
}

@keyframes sakuraFloat {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  33% { transform: translate(20px, -15px) rotate(3deg); }
  66% { transform: translate(-15px, 10px) rotate(-2deg); }
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
  background: rgba(255, 251, 235, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(255, 183, 197, 0.25),
              0 4px 12px rgba(163, 230, 53, 0.1);
  border: 2px solid rgba(255, 183, 197, 0.3);
  position: relative;
}

/* 课本装饰 */
.profile-content::before {
  content: '';
  position: absolute;
  top: 12px;
  left: 12px;
  right: 12px;
  bottom: 12px;
  border: 1px dashed rgba(163, 230, 53, 0.25);
  border-radius: 10px;
  pointer-events: none;
}

.profile-header {
  text-align: center;
  padding-bottom: 30px;
  border-bottom: 2px solid rgba(255, 183, 197, 0.2);
  margin-bottom: 30px;
}

.profile-header >>> .el-avatar {
  border: 3px solid #FFB7C5;
  box-shadow: 0 4px 15px rgba(255, 183, 197, 0.3);
}

.username {
  font-size: 28px;
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  margin: 20px 0 10px 0;
  font-weight: 700;
}

.email {
  color: #6b6b6b;
  font-size: 14px;
  margin: 0 0 15px 0;
  font-weight: 500;
}

.role-badge {
  display: inline-block;
  padding: 5px 15px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: bold;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.role-admin {
  background: linear-gradient(135deg, #FF9F43 0%, #FFB7C5 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(255, 159, 67, 0.3);
}

.role-user {
  background: linear-gradient(135deg, #A3E635 0%, #87CEEB 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(163, 230, 53, 0.3);
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
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #9ca3af;
  font-weight: 500;
}

.user-blogs {
  margin-bottom: 30px;
}

/* 社团展示区域滚动触发效果 */
.user-blogs.animate-enter .blog-item {
  opacity: 0;
  transform: translateY(20px);
  animation: popUpFromBottom 0.3s ease-out forwards;
}

@keyframes popUpFromBottom {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 为每个卡片设置延迟动画 */
.user-blogs.animate-enter .blog-item:nth-child(1) { animation-delay: 0.1s; }
.user-blogs.animate-enter .blog-item:nth-child(2) { animation-delay: 0.2s; }
.user-blogs.animate-enter .blog-item:nth-child(3) { animation-delay: 0.3s; }
.user-blogs.animate-enter .blog-item:nth-child(4) { animation-delay: 0.4s; }
.user-blogs.animate-enter .blog-item:nth-child(5) { animation-delay: 0.5s; }

.section-title {
  font-size: 20px;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid rgba(255, 183, 197, 0.2);
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-weight: 700;
}

.blog-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.blog-item {
  padding: 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid rgba(255, 183, 197, 0.2);
}

.blog-item:hover {
  background: rgba(255, 255, 255, 0.95);
  transform: translateX(5px);
  border-color: rgba(255, 183, 197, 0.4);
  box-shadow: 0 4px 12px rgba(255, 183, 197, 0.2);
}

.blog-title {
  font-size: 16px;
  color: #4a4a4a;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.blog-summary {
  font-size: 14px;
  color: #6b6b6b;
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
  color: #9ca3af;
}

.blog-info span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.blog-info i {
  color: #FFB7C5;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #9ca3af;
}

.empty-state i {
  font-size: 64px;
  background: linear-gradient(135deg, #FFB7C5, #FF9F43, #A3E635);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  margin-bottom: 20px;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
  color: #6b6b6b;
  font-weight: 500;
}

.actions {
  text-align: center;
}

.actions >>> .el-button {
  padding: 12px 40px;
  font-size: 16px;
  border-radius: 20px;
  transition: all 0.3s ease;
  font-weight: 600;
  border: 2px solid;
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(255, 183, 197, 0.4);
  color: #FF9F43;
}

.actions >>> .el-button:hover {
  background: rgba(255, 255, 255, 1);
  border-color: #FFB7C5;
  transform: translateY(-2px);
}
</style>
