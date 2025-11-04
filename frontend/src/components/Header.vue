<template>
  <header class="header" :class="{ 'header-scrolled': scrolled }">
    <div class="container">
      <div class="header-content">
        <!-- Logo & Title -->
        <div class="logo-area">
          <h1 class="gradient-text">个人博客</h1>
        </div>
        
        <!-- Navigation -->
        <nav class="nav-menu">
          <router-link to="/home" class="nav-item" active-class="active">首页</router-link>
          <router-link to="/blog-editor" class="nav-item" active-class="active">写文章</router-link>
          <router-link to="/my-favorites" class="nav-item" active-class="active">
            <i class="el-icon-collection"></i> 我的收藏
          </router-link>
          <router-link to="/deleted-blogs" class="nav-item" active-class="active">回收站</router-link>
          <el-dropdown v-if="isAdmin" @command="handleManagementCommand">
            <span class="nav-item">
              管理 <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="category">分类管理</el-dropdown-item>
              <el-dropdown-item command="tag">标签管理</el-dropdown-item>
              <el-dropdown-item command="user">用户管理</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </nav>
        
        <!-- Search & User -->
        <div class="header-actions">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索文章..."
            prefix-icon="el-icon-search"
            class="search-input"
            @keyup.enter.native="handleSearch"
          />
          
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :src="userInfo.avatarUrl || defaultAvatar" :size="36" />
              <span class="username">{{ userInfo.username }}</span>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="favorites">
                <i class="el-icon-collection"></i> 我的收藏
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>
  </header>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'Header',
  data() {
    return {
      scrolled: false,
      searchKeyword: '',
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    }
  },
  computed: {
    ...mapGetters('user', ['userInfo']),
    isAdmin() {
      return this.userInfo && this.userInfo.role === 'admin'
    }
  },
  mounted() {
    window.addEventListener('scroll', this.handleScroll)
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.handleScroll)
  },
  methods: {
    ...mapActions('user', ['logout']),
    ...mapActions('blog', ['setSearchParams', 'getBlogList']),
    
    handleScroll() {
      this.scrolled = window.scrollY > 50
    },
    
    handleSearch() {
      this.setSearchParams({ title: this.searchKeyword })
      this.getBlogList({ current: 1 })
    },
    
    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.logout()
          this.$message.success('已退出登录')
          this.$router.push('/login')
        })
      } else if (command === 'profile') {
        this.$router.push('/profile')
      } else if (command === 'favorites') {
        this.$router.push('/my-favorites')
      }
    },

    handleManagementCommand(command) {
      const routes = {
        category: '/category-management',
        tag: '/tag-management',
        user: '/user-management'
      }
      if (routes[command]) {
        this.$router.push(routes[command])
      }
    }
  }
}
</script>

<style scoped>
.header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(79, 172, 254, 0.2);
  transition: all 0.3s ease;
}

.header-scrolled {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 15px rgba(79, 172, 254, 0.15);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 70px;
}

.logo-area h1 {
  font-size: 24px;
  margin: 0;
  cursor: pointer;
}

.nav-menu {
  display: flex;
  gap: 30px;
}

.nav-item {
  color: #333;
  text-decoration: none;
  font-size: 15px;
  transition: all 0.3s ease;
  position: relative;
}

.nav-item::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  transition: width 0.3s ease;
}

.nav-item:hover::after,
.nav-item.active::after {
  width: 100%;
}

.nav-item.active {
  color: #4facfe;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-input {
  width: 200px;
}

.search-input >>> .el-input__inner {
  border-radius: 20px;
  border: 1px solid rgba(79, 172, 254, 0.3);
  background: rgba(255, 255, 255, 0.5);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 5px 15px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(79, 172, 254, 0.1);
}

.username {
  font-size: 14px;
  color: #333;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .search-input {
    width: 150px;
  }
}
</style>
