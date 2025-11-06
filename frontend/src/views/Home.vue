<template>
  <div class="home-container">
    <!-- Header -->
    <Header />

    <!-- Main Content -->
    <div class="content-wrapper">
      <div class="container">
        <div class="main-content">
          <!-- Left: Blog List -->
          <div class="blog-list-area">
            <BlogList />
          </div>

          <!-- Right: Sidebar -->
          <div class="sidebar-area">
            <div class="sidebar-section">
              <h3 class="section-title">最新消息</h3>
              <div class="messages-preview" v-if="unreadMessageCount > 0">
                <el-badge :value="unreadMessageCount" class="message-badge">
                  <el-button 
                    type="primary" 
                    plain 
                    size="small" 
                    @click="$router.push('/messages')"
                    class="messages-button"
                  >
                    <i class="el-icon-chat-dot-round"></i> 您有{{ unreadMessageCount }}条未读消息
                  </el-button>
                </el-badge>
              </div>
              <div class="messages-preview" v-else>
                <el-button 
                  type="info" 
                  plain 
                  size="small" 
                  @click="$router.push('/messages')"
                  class="messages-button"
                >
                  <i class="el-icon-chat-dot-round"></i> 查看私信
                </el-button>
              </div>
            </div>
            <Sidebar />
          </div>
        </div>
      </div>
    </div>

    <!-- Footer -->
    <Footer />

    <!-- Back to Top Button -->
    <BackToTop />
  </div>
</template>

<script>
import Header from '@/components/Header'
import BlogList from '@/components/BlogList'
import Sidebar from '@/components/Sidebar'
import Footer from '@/components/Footer'
import BackToTop from '@/components/BackToTop'
import { mapActions, mapState, mapGetters } from 'vuex'

export default {
  name: 'Home',
  components: {
    Header,
    BlogList,
    Sidebar,
    Footer,
    BackToTop
  },
  data() {
    return {
      refreshTimer: null,
      refreshInterval: 5000 // 5秒刷新一次
    }
  },
  computed: {
    ...mapState('message', ['unreadMessageCount']),
    ...mapGetters('user', ['isLogin'])
  },
  mounted() {
    // 添加滚动监听
    window.addEventListener('scroll', this.handleScroll)
    // 只有在用户已登录时才初始化消息数据
    if (this.isLogin) {
      this.initMessageData()
    }
  },
  created() {
    // 初始化数据
    this.initData()
    // 只有在用户已登录时才启动定时刷新
    if (this.isLogin) {
      this.startAutoRefresh()
    }
  },
  beforeDestroy() {
    // 移除滚动监听
    window.removeEventListener('scroll', this.handleScroll)
    // 组件销毁时清除定时器
    this.stopAutoRefresh()
  },
  methods: {
    ...mapActions('blog', ['getBlogList']),
    ...mapActions('category', ['getCategoryList']),
    ...mapActions('tag', ['getTagList']),
    ...mapActions('message', ['getUnreadMessageCount']),

    async initData() {
      try {
        await Promise.all([
          this.getBlogList(),
          this.getCategoryList(),
          this.getTagList()
        ])
      } catch (error) {
        console.error('数据加载失败:', error)
      }
    },

    async initMessageData() {
      // 只有在用户已登录时才初始化消息数据
      if (this.isLogin) {
        try {
          await this.getUnreadMessageCount()
          // 启动消息数据定时刷新
          this.startMessageRefresh()
        } catch (error) {
          console.error('初始化消息数据失败:', error)
        }
      }
    },

    startAutoRefresh() {
      // 定时刷新博客列表
      this.refreshTimer = setInterval(() => {
        this.getBlogList()
      }, this.refreshInterval)
    },

    stopAutoRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer)
        this.refreshTimer = null
      }
    },

    startMessageRefresh() {
      // 定时刷新消息数据
      this.messageRefreshTimer = setInterval(() => {
        this.getUnreadMessageCount()
      }, 10000) // 10秒刷新一次
    },

    stopMessageRefresh() {
      if (this.messageRefreshTimer) {
        clearInterval(this.messageRefreshTimer)
        this.messageRefreshTimer = null
      }
    },

    // 滚动处理函数
    handleScroll() {
      // 这里可以添加滚动相关的逻辑
      // 例如检测是否滚动到特定区域来触发动画
    }
  },
  
  beforeDestroy() {
    this.stopMessageRefresh()
  }
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #FFFBEB 0%, #F8CBA6 50%, #FFB7C5 100%);
  position: relative;
  overflow-x: hidden;
}

/* 樱花飘落背景 */
.home-container::before {
  content: '';
  position: fixed;
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

.content-wrapper {
  flex: 1;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.main-content {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 20px;
}

.blog-list-area {
  min-height: 400px;
}

.sidebar-area {
  position: sticky;
  top: 80px;
  height: fit-content;
}

.sidebar-section {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  border: 2px solid rgba(255, 183, 197, 0.2);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 12px rgba(255, 183, 197, 0.15);
}

.section-title {
  margin: 0 0 15px 0;
  font-size: 18px;
  font-weight: 600;
  color: #FF9F43;
  position: relative;
  padding-bottom: 8px;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 40px;
  height: 3px;
  background: linear-gradient(90deg, #FFB7C5, #FF9F43);
  border-radius: 2px;
}

.messages-preview {
  text-align: center;
}

.messages-button {
  width: 100%;
  border-radius: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid rgba(255, 183, 197, 0.3);
}

.messages-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 183, 197, 0.25);
  border-color: rgba(255, 183, 197, 0.5);
}

.message-badge >>> .el-badge__content {
  background-color: #FF9F43;
  border: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    grid-template-columns: 1fr;
  }

  .sidebar-area {
    position: static;
  }
}
</style>