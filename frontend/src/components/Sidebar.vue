<template>
  <div class="sidebar">
    <!-- User Card -->
    <div class="user-card card" @click="goToMyProfile">
      <div class="avatar-wrapper">
        <el-avatar :src="userInfo.avatarUrl || defaultAvatar" :size="80" class="clickable-avatar" />
      </div>
      <h3 class="clickable-username">{{ userInfo.username }}</h3>
      <p class="user-email">{{ userInfo.email }}</p>
    </div>
    
    <!-- Categories -->
    <div class="categories-card card">
      <h4 class="card-title gradient-text">
        文章分类
        <el-button v-if="searchParams.categoryId" type="text" size="mini" @click="clearFilters">清空</el-button>
      </h4>
      <div class="category-list">
        <div
          v-for="category in categoryList"
          :key="category.id"
          class="category-item"
          :class="{ active: searchParams.categoryId === category.id }"
          @click="filterByCategory(category.id)"
        >
          <span class="category-name">{{ category.name }}</span>
          <span class="category-count">{{ category.blogCount || 0 }}</span>
        </div>
      </div>
    </div>
    
    <!-- Tags Cloud -->
    <div class="tags-card card">
      <h4 class="card-title gradient-text">
        标签云
        <el-button v-if="searchParams.tagId" type="text" size="mini" @click="clearFilters">清空</el-button>
      </h4>
      <div class="tags-cloud">
        <span
          v-for="tag in tagList"
          :key="tag.id"
          class="tag-item"
          :class="{ active: searchParams.tagId === tag.id }"
          :style="getTagStyle(tag.blogCount || 0)"
          @click="filterByTag(tag.id)"
        >
          {{ tag.name }}
        </span>
      </div>
    </div>
    
    <!-- Hot Blogs -->
    <div class="hot-blogs-card card">
      <h4 class="card-title gradient-text">热门文章</h4>
      <div class="hot-blog-list">
        <div
          v-for="(blog, index) in hotBlogs"
          :key="blog.id"
          class="hot-blog-item"
          @click="goToBlogDetail(blog.id)"
        >
          <span class="hot-number" :class="'hot-' + (index + 1)">{{ index + 1 }}</span>
          <span class="hot-title">{{ blog.title }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'Sidebar',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      hotBlogs: []
    }
  },
  computed: {
    ...mapGetters('user', ['userInfo']),
    ...mapGetters('category', ['categoryList']),
    ...mapGetters('tag', ['tagList']),
    ...mapGetters('blog', ['blogList', 'searchParams'])
  },
  watch: {
    blogList: {
      immediate: true,
      handler(list) {
        // 获取阅读量最高的5篇文章
        this.hotBlogs = [...list]
          .sort((a, b) => b.viewCount - a.viewCount)
          .slice(0, 5)
      }
    }
  },
  methods: {
    ...mapActions('blog', ['setSearchParams', 'getBlogList', 'resetSearchParams']),
    
    filterByCategory(categoryId) {
      this.setSearchParams({ categoryId, tagId: null, title: '' })
      this.getBlogList({ current: 1 })
    },
    
    filterByTag(tagId) {
      this.setSearchParams({ tagId, categoryId: null, title: '' })
      this.getBlogList({ current: 1 })
    },
    
    clearFilters() {
      this.resetSearchParams()
      this.getBlogList({ current: 1 })
    },
    
    goToBlogDetail(id) {
      this.$router.push(`/blog/${id}`)
    },
    
    goToMyProfile() {
      console.log('=== Sidebar Click Debug ===')
      console.log('Full userInfo object:', JSON.stringify(this.userInfo, null, 2))
      console.log('userInfo.id:', this.userInfo && this.userInfo.id)
      console.log('userInfo keys:', Object.keys(this.userInfo))
      console.log('LocalStorage userInfo:', localStorage.getItem('userInfo'))
      
      if (this.userInfo && this.userInfo.id) {
        console.log('Navigating to profile:', `/user/${this.userInfo.id}`)
        this.$router.push(`/user/${this.userInfo.id}`)
      } else {
        console.error('User info or user ID is missing')
        console.error('userInfo:', this.userInfo)
        this.$message.warning('无法获取用户信息，请重新登录')
      }
    },
    
    getTagStyle(count) {
      const baseSize = 12
      const maxSize = 20
      const size = Math.min(baseSize + count * 2, maxSize)
      
      return {
        fontSize: size + 'px'
      }
    }
  }
}
</script>

<style scoped>
.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.user-card {
  text-align: center;
  padding: 30px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(79, 172, 254, 0.3);
}

.clickable-avatar {
  transition: transform 0.3s ease;
}

.user-card:hover .clickable-avatar {
  transform: scale(1.1);
}

.avatar-wrapper {
  display: inline-block;
  pointer-events: none;
}

.user-card h3 {
  margin: 15px 0 5px 0;
  font-size: 18px;
  color: #333;
  transition: color 0.3s ease;
}

.clickable-username:hover {
  color: #4facfe;
}

.user-email {
  color: #999;
  font-size: 13px;
  margin: 0;
}

.card-title {
  font-size: 16px;
  margin: 0 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid rgba(79, 172, 254, 0.2);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title .el-button--text {
  color: #4facfe;
  font-size: 12px;
  padding: 0;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background: rgba(79, 172, 254, 0.05);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.category-item:hover {
  background: rgba(79, 172, 254, 0.15);
  transform: translateX(5px);
}

.category-item.active {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.category-item.active .category-name {
  color: white;
}

.category-item.active .category-count {
  color: white;
}

.category-name {
  color: #333;
  font-size: 14px;
}

.category-count {
  color: #4facfe;
  font-size: 13px;
  font-weight: bold;
}

.tags-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-item {
  padding: 5px 15px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  border-radius: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 12px;
}

.tag-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(79, 172, 254, 0.4);
}

.tag-item.active {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e8e 100%);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
  transform: translateY(-2px) scale(1.1);
}

.hot-blog-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-blog-item {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 0;
  transition: all 0.3s ease;
}

.hot-blog-item:hover {
  transform: translateX(5px);
}

.hot-number {
  flex-shrink: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #e6e6e6;
  color: #999;
  font-size: 12px;
  font-weight: bold;
}

.hot-1 {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e8e 100%);
  color: white;
}

.hot-2 {
  background: linear-gradient(135deg, #ffd93d 0%, #ffe66d 100%);
  color: white;
}

.hot-3 {
  background: linear-gradient(135deg, #6bcf7f 0%, #84d68f 100%);
  color: white;
}

.hot-title {
  flex: 1;
  color: #333;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-blog-item:hover .hot-title {
  color: #4facfe;
}
</style>
