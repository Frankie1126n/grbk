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
          <router-link to="/friends" class="nav-item" active-class="active">
            <i class="el-icon-user"></i> 好友
            <el-badge v-if="pendingFriendRequestCount > 0" :value="pendingFriendRequestCount" class="friend-badge" />
          </router-link>
          <router-link to="/messages" class="nav-item" active-class="active">
            <i class="el-icon-chat-dot-round"></i> 私信
            <el-badge v-if="unreadMessageCount > 0" :value="unreadMessageCount" class="message-badge" />
          </router-link>
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
              <CroppedAvatar 
                :src="userInfo.avatarUrl || defaultAvatar" 
                :size="36" 
                :border-width="'2px'"
              />
              <span class="username">{{ userInfo.username }}</span>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="friends">好友管理</el-dropdown-item>
              <el-dropdown-item command="favorites">
                <i class="el-icon-collection"></i> 我的收藏
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
        
        <!-- Mobile Menu Toggle -->
        <div class="mobile-menu-toggle" @click="toggleMobileMenu">
          <i class="el-icon-menu"></i>
        </div>
      </div>
      
      <!-- Mobile Navigation Menu -->
      <transition name="slide-down">
        <nav v-show="mobileMenuVisible" class="mobile-nav-menu">
          <router-link to="/home" class="mobile-nav-item" active-class="active" @click.native="hideMobileMenu">首页</router-link>
          <router-link to="/blog-editor" class="mobile-nav-item" active-class="active" @click.native="hideMobileMenu">写文章</router-link>
          <router-link to="/my-favorites" class="mobile-nav-item" active-class="active" @click.native="hideMobileMenu">
            <i class="el-icon-collection"></i> 我的收藏
          </router-link>
          <router-link to="/friends" class="mobile-nav-item" active-class="active" @click.native="hideMobileMenu">
            <i class="el-icon-user"></i> 好友
            <el-badge v-if="pendingFriendRequestCount > 0" :value="pendingFriendRequestCount" class="friend-badge" />
          </router-link>
          <router-link to="/messages" class="mobile-nav-item" active-class="active" @click.native="hideMobileMenu">
            <i class="el-icon-chat-dot-round"></i> 私信
            <el-badge v-if="unreadMessageCount > 0" :value="unreadMessageCount" class="message-badge" />
          </router-link>
          <div v-if="isAdmin" class="mobile-nav-dropdown">
            <span class="mobile-nav-item" @click="toggleManagementMenu">
              管理 <i class="el-icon-arrow-down" :class="{ 'rotated': managementMenuVisible }"></i>
            </span>
            <transition name="slide-down">
              <div v-show="managementMenuVisible" class="management-menu">
                <router-link to="/category-management" class="management-item" @click.native="hideMobileMenu">分类管理</router-link>
                <router-link to="/tag-management" class="management-item" @click.native="hideMobileMenu">标签管理</router-link>
                <router-link to="/user-management" class="management-item" @click.native="hideMobileMenu">用户管理</router-link>
              </div>
            </transition>
          </div>
          <div class="mobile-user-actions">
            <div class="mobile-user-info" @click="goToProfile">
              <CroppedAvatar 
                :src="userInfo.avatarUrl || defaultAvatar" 
                :size="36" 
                :border-width="'2px'"
              />
              <span class="username">{{ userInfo.username }}</span>
            </div>
            <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
          </div>
        </nav>
      </transition>
    </div>
  </header>
</template>

<script>
import { mapGetters, mapActions, mapState } from 'vuex'
import CroppedAvatar from '@/components/CroppedAvatar'
import { getConfig } from '@/utils/config'

export default {
  name: 'Header',
  components: {
    CroppedAvatar
  },
  data() {
    return {
      scrolled: false,
      searchKeyword: '',
      defaultAvatar: getConfig().DEFAULT_AVATAR,
      mobileMenuVisible: false,
      managementMenuVisible: false
    }
  },
  computed: {
    ...mapGetters('user', ['userInfo']),
    ...mapState('friend', ['pendingFriendRequestCount']),
    ...mapState('message', ['unreadMessageCount']),
    isAdmin() {
      return this.userInfo && this.userInfo.role === 'admin'
    },
    isLogin() {
      return this.$store.getters['user/isLogin']
    }
  },
  watch: {
    // 监听路由变化，只在特定页面启动实时刷新
    $route(to, from) {
      this.handleRouteChange(to)
      // Hide mobile menu when route changes
      this.mobileMenuVisible = false
    }
  },
  mounted() {
    window.addEventListener('scroll', this.handleScroll)
    // 只有在用户已登录时才初始化社交功能数据
    if (this.isLogin) {
      this.initSocialData()
      // 检查当前路由是否需要实时刷新
      this.handleRouteChange(this.$route)
    }
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.handleScroll)
    // 停止所有定时刷新
    this.stopAllRefresh()
  },
  methods: {
    ...mapActions('user', ['logout']),
    ...mapActions('blog', ['setSearchParams', 'getBlogList']),
    ...mapActions('friend', ['getPendingFriendRequestCount']),
    ...mapActions('message', ['getUnreadMessageCount']),
    
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
      } else if (command === 'friends') {
        this.$router.push('/friends')
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
    },
    
    async initSocialData() {
      try {
        // 只初始化一次数据，不启动定时刷新
        await Promise.all([
          this.getPendingFriendRequestCount(),
          this.getUnreadMessageCount()
        ])
      } catch (error) {
        console.error('初始化社交数据失败:', error)
      }
    },
    
    handleRouteChange(route) {
      // 停止所有之前的定时刷新
      this.stopAllRefresh()
      
      // 只有在用户已登录的情况下，在Home页面和Messages页面启动实时刷新
      if (this.isLogin && route.path === '/home') {
        this.startHomeRefresh()
      } else if (this.isLogin && (route.path === '/messages' || route.path.startsWith('/messages/'))) {
        this.startMessagesRefresh()
      }
    },
    
    startHomeRefresh() {
      // Home页面每1分钟刷新一次消息数据
      this.homeRefreshTimer = setInterval(async () => {
        try {
          await this.getUnreadMessageCount()
        } catch (error) {
          console.error('刷新Home页面数据失败:', error)
        }
      }, 60000)
    },
    
    startMessagesRefresh() {
      // Messages页面每1分钟刷新一次消息数据
      this.messagesRefreshTimer = setInterval(async () => {
        try {
          await Promise.all([
            this.getPendingFriendRequestCount(),
            this.getUnreadMessageCount()
          ])
        } catch (error) {
          console.error('刷新Messages页面数据失败:', error)
        }
      }, 60000)
    },
    
    stopAllRefresh() {
      // 清除所有定时器
      if (this.homeRefreshTimer) {
        clearInterval(this.homeRefreshTimer)
        this.homeRefreshTimer = null
      }
      
      if (this.messagesRefreshTimer) {
        clearInterval(this.messagesRefreshTimer)
        this.messagesRefreshTimer = null
      }
    },
    
    toggleMobileMenu() {
      this.mobileMenuVisible = !this.mobileMenuVisible
      // Close management menu when closing mobile menu
      if (!this.mobileMenuVisible) {
        this.managementMenuVisible = false
      }
    },
    
    hideMobileMenu() {
      this.mobileMenuVisible = false
      this.managementMenuVisible = false
    },
    
    toggleManagementMenu() {
      this.managementMenuVisible = !this.managementMenuVisible
    },
    
    goToProfile() {
      this.$router.push('/profile')
      this.hideMobileMenu()
    },
    
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.logout()
        this.$message.success('已退出登录')
        this.$router.push('/login')
        this.hideMobileMenu()
      })
    }
  }
}
</script>

<style scoped>
.header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: rgba(255, 251, 235, 0.92);
  backdrop-filter: blur(20px) saturate(150%);
  border-bottom: 2px solid rgba(255, 183, 197, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 15px rgba(255, 183, 197, 0.15),
              0 1px 3px rgba(163, 230, 53, 0.1);
}

.header-scrolled {
  background: rgba(255, 251, 235, 0.98);
  backdrop-filter: blur(25px) saturate(180%);
  box-shadow: 0 4px 20px rgba(255, 183, 197, 0.2),
              0 2px 8px rgba(163, 230, 53, 0.12);
  border-bottom-color: rgba(163, 230, 53, 0.4);
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
  font-size: 26px;
  margin: 0;
  cursor: pointer;
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 50%, #A3E635 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-weight: 800;
  letter-spacing: 2px;
  position: relative;
  transition: transform 0.2s ease;
}

/* 课本翻页效果 */
.logo-area h1:hover {
  transform: rotate(3deg);
  animation: pageFlip 0.3s ease;
}

@keyframes pageFlip {
  0% { transform: rotate(0deg); }
  50% { transform: rotate(5deg); }
  100% { transform: rotate(3deg); }
}

/* 小装饰 - 星星贴纸 */
.logo-area h1::after {
  content: '★';
  position: absolute;
  top: -8px;
  right: -20px;
  font-size: 14px;
  color: #FF9F43;
  animation: twinkle 2s ease-in-out infinite;
}

@keyframes twinkle {
  0%, 100% { opacity: 1; transform: scale(1) rotate(0deg); }
  50% { opacity: 0.6; transform: scale(1.2) rotate(20deg); }
}

.nav-menu {
  display: flex;
  gap: 35px;
}

.nav-item {
  color: #FF9F43;
  text-decoration: none;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  padding: 8px 0;
  cursor: pointer;
  display: flex;
  align-items: center;
}

/* 课本翻页效果 */
.nav-item::before {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #FFB7C5, #FF9F43);
  transform: translateX(-50%);
  transition: width 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 2px;
  box-shadow: 0 1px 3px rgba(255, 183, 197, 0.4);
}

.nav-item:hover::before,
.nav-item.active::before {
  width: 100%;
}

/* 粉笔收尾的轻颤效果 */
.nav-item.active::before {
  animation: chalkEndShake 0.3s ease-in-out;
}

@keyframes chalkEndShake {
  0%, 100% { transform: translateX(-50%); }
  25% { transform: translateX(-52%); }
  75% { transform: translateX(-48%); }
}

.nav-item:hover {
  color: #FFB7C5;
  transform: translateY(-1px);
}

.nav-item.active {
  color: #FFB7C5;
}

.friend-badge, .message-badge {
  margin-left: 5px;
}

.friend-badge >>> .el-badge__content, .message-badge >>> .el-badge__content {
  background-color: #FF9F43;
  border: none;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-input {
  width: 220px;
}

.search-input >>> .el-input__inner {
  border-radius: 20px;
  border: 2px solid rgba(255, 183, 197, 0.3);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding-left: 40px;
  color: #4a4a4a;
  font-size: 14px;
}

.search-input >>> .el-input__inner:focus {
  border-color: #FFB7C5;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 0 0 3px rgba(255, 183, 197, 0.15),
              0 2px 8px rgba(255, 183, 197, 0.2);
}

.search-input >>> .el-input__inner::placeholder {
  color: #ffb7c5;
}

.search-input >>> .el-input__prefix {
  color: #FFB7C5;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 6px 18px;
  border-radius: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 183, 197, 0.1);
  border: 2px solid rgba(255, 183, 197, 0.2);
}

.user-info:hover {
  background: rgba(255, 183, 197, 0.15);
  border-color: rgba(255, 183, 197, 0.4);
  transform: translateY(-2px);
  box-shadow: 0 3px 10px rgba(255, 183, 197, 0.25);
}

.user-info >>> .el-avatar {
  border: 2px solid #FFB7C5;
  box-shadow: 0 2px 8px rgba(255, 183, 197, 0.3);
  object-fit: cover;
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: #FF9F43;
}

/* Mobile Menu Toggle */
.mobile-menu-toggle {
  display: none;
  font-size: 24px;
  color: #FF9F43;
  cursor: pointer;
  padding: 10px;
}

/* Mobile Navigation Menu */
.mobile-nav-menu {
  display: none;
  flex-direction: column;
  background: rgba(255, 251, 235, 0.95);
  backdrop-filter: blur(15px);
  border-top: 2px solid rgba(255, 183, 197, 0.3);
  padding: 15px 0;
  box-shadow: 0 4px 12px rgba(255, 183, 197, 0.2);
}

.mobile-nav-item {
  padding: 15px 20px;
  color: #FF9F43;
  text-decoration: none;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
  border-bottom: 1px solid rgba(255, 183, 197, 0.1);
  cursor: pointer;
  transition: all 0.2s ease;
}

.mobile-nav-item:last-child {
  border-bottom: none;
}

.mobile-nav-item:hover,
.mobile-nav-item.active {
  background: rgba(255, 183, 197, 0.1);
  color: #FFB7C5;
}

.mobile-nav-dropdown {
  display: flex;
  flex-direction: column;
}

.management-menu {
  display: flex;
  flex-direction: column;
  background: rgba(255, 240, 220, 0.8);
  margin: 10px 20px;
  border-radius: 10px;
  overflow: hidden;
}

.management-item {
  padding: 12px 20px;
  color: #FF9F43;
  text-decoration: none;
  font-size: 15px;
  border-bottom: 1px solid rgba(255, 183, 197, 0.1);
  transition: all 0.2s ease;
}

.management-item:last-child {
  border-bottom: none;
}

.management-item:hover {
  background: rgba(255, 183, 197, 0.1);
  color: #FFB7C5;
}

.mobile-user-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-top: 1px solid rgba(255, 183, 197, 0.1);
}

.mobile-user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

/* Rotated arrow for dropdown */
.el-icon-arrow-down.rotated {
  transform: rotate(180deg);
  transition: transform 0.3s ease;
}

/* Slide down transition */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
  max-height: 1000px;
  overflow: hidden;
}

.slide-down-enter,
.slide-down-leave-to {
  max-height: 0;
  opacity: 0;
  transform: translateY(-10px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .search-input {
    width: 150px;
  }
  
  .header-content {
    height: 60px;
  }
  
  .header-actions {
    display: none;
  }
  
  .mobile-menu-toggle {
    display: block;
  }
  
  .mobile-nav-menu {
    display: flex;
  }
  
  .logo-area h1 {
    font-size: 22px;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 15px;
  }
  
  .mobile-nav-item {
    font-size: 15px;
    padding: 12px 15px;
  }
  
  .management-item {
    font-size: 14px;
    padding: 10px 15px;
  }
  
  .mobile-user-actions {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
}
</style>