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
  created() {
    // 初始化数据
    this.initData()
    // 启动定时刷新
    this.startAutoRefresh()
  },
  beforeDestroy() {
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
    }
  }
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
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
