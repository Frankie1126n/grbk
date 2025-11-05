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
import { mapActions } from 'vuex'

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
  mounted() {
    // 添加滚动监听
    window.addEventListener('scroll', this.handleScroll)
  },
  created() {
    // 初始化数据
    this.initData()
    // 启动定时刷新
    this.startAutoRefresh()
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

    // 滚动处理函数
    handleScroll() {
      // 这里可以添加滚动相关的逻辑
      // 例如检测是否滚动到特定区域来触发动画
    }
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
