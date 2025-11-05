<template>
  <div class="sidebar">
    <!-- User Card -->
    <div class="user-card card" @click="goToMyProfile">
      <div class="avatar-wrapper">
        <CroppedAvatar 
          :src="userInfo.avatarUrl || defaultAvatar" 
          :size="80" 
          class="clickable-avatar" 
          :border-width="'3px'"
        />
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
import CroppedAvatar from '@/components/CroppedAvatar'

export default {
  name: 'Sidebar',
  components: {
    CroppedAvatar
  },
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
  background: rgba(255, 251, 235, 0.95);
  backdrop-filter: blur(15px);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(255, 183, 197, 0.2),
              0 1px 3px rgba(163, 230, 53, 0.1);
  border: 2px solid rgba(255, 183, 197, 0.3);
  position: relative;
  overflow: hidden;
}

/* 课本装饰 */
.user-card::before {
  content: '';
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  bottom: 10px;
  border: 1px dashed rgba(163, 230, 53, 0.2);
  border-radius: 10px;
  pointer-events: none;
}

.user-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 28px rgba(255, 183, 197, 0.3),
              0 2px 8px rgba(163, 230, 53, 0.15);
  border-color: rgba(163, 230, 53, 0.4);
}

.clickable-avatar {
  transition: transform 0.3s ease;
  border: 3px solid #FFB7C5;
  box-shadow: 0 4px 15px rgba(255, 183, 197, 0.3);
  object-fit: cover;
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
  color: #4a4a4a;
  transition: all 0.3s ease;
  font-weight: 700;
}

.clickable-username:hover {
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.user-email {
  color: #9ca3af;
  font-size: 13px;
  margin: 0;
  font-weight: 500;
}

.card-title {
  font-size: 16px;
  margin: 0 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid rgba(255, 183, 197, 0.2);
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #FF9F43;
  font-weight: 700;
  position: relative;
}

/* 铅笔分隔线装饰 */
.card-title::after {
  content: '✏️';
  position: absolute;
  right: -5px;
  bottom: -15px;
  font-size: 16px;
  opacity: 0.5;
  transform: rotate(20deg);
}

.card-title .el-button--text {
  color: #FFB7C5;
  font-size: 12px;
  padding: 0;
  font-weight: 600;
}

.card-title .el-button--text:hover {
  color: #FF9F43;
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
  background: rgba(255, 183, 197, 0.1);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 183, 197, 0.2);
}

.category-item:hover {
  background: rgba(255, 183, 197, 0.2);
  transform: translateX(5px);
  border-color: rgba(255, 183, 197, 0.4);
}

.category-item.active {
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  border-color: transparent;
}

.category-item.active .category-name {
  color: white;
  font-weight: 600;
}

.category-item.active .category-count {
  color: white;
  font-weight: 700;
}

.category-name {
  color: #4a4a4a;
  font-size: 14px;
  font-weight: 500;
}

.category-count {
  color: #FFB7C5;
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
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  color: white;
  border-radius: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 12px;
  font-weight: 600;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.tag-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 183, 197, 0.4);
}

.tag-item.active {
  background: linear-gradient(135deg, #A3E635 0%, #87CEEB 100%);
  box-shadow: 0 4px 12px rgba(163, 230, 53, 0.4);
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
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  color: white;
}

.hot-2 {
  background: linear-gradient(135deg, #A3E635 0%, #87CEEB 100%);
  color: white;
}

.hot-3 {
  background: linear-gradient(135deg, #87CEEB 0%, #A3E635 100%);
  color: white;
}

.hot-title {
  flex: 1;
  color: #4a4a4a;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
}

.hot-blog-item:hover .hot-title {
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
</style>
