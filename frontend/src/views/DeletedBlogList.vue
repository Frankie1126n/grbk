<template>
  <div class="deleted-blog-list">
    <div class="page-header">
      <h2>回收站</h2>
      <p class="subtitle">{{ isAdmin ? '所有已删除的文章' : '我删除的文章' }}</p>
    </div>

    <div class="table-container card" v-loading="loading">
      <el-table :data="blogList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="username" label="作者" width="120" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="deleteTime" label="删除时间" width="180">
          <template slot-scope="scope">
            {{ formatTime(scope.row.deleteTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="success"
              size="small"
              icon="el-icon-refresh-left"
              @click="handleRestore(scope.row.id)"
            >
              恢复
            </el-button>
            <el-button
              type="danger"
              size="small"
              icon="el-icon-delete"
              @click="handlePermanentDelete(scope.row.id)"
            >
              彻底删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>

      <!-- Empty State -->
      <div v-if="!loading && blogList.length === 0" class="empty-state">
        <i class="el-icon-delete"></i>
        <p>回收站为空</p>
      </div>
    </div>
  </div>
</template>

<script>
import { getDeletedBlogList, restoreBlog, permanentDeleteBlog } from '@/api/blog'
import { mapGetters } from 'vuex'

export default {
  name: 'DeletedBlogList',
  data() {
    return {
      blogList: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0
    }
  },
  computed: {
    ...mapGetters('user', ['userInfo']),
    isAdmin() {
      return this.userInfo && this.userInfo.role === 'admin'
    }
  },
  mounted() {
    this.loadDeletedBlogList()
  },
  methods: {
    async loadDeletedBlogList() {
      this.loading = true
      try {
        const response = await getDeletedBlogList({
          current: this.currentPage,
          size: this.pageSize
        })
        this.blogList = response.data.records || []
        this.total = response.data.total || 0
      } catch (error) {
        this.$message.error(error.message || '加载失败')
      } finally {
        this.loading = false
      }
    },

    handlePageChange(page) {
      this.currentPage = page
      this.loadDeletedBlogList()
    },

    async handleRestore(id) {
      try {
        await this.$confirm('确定要恢复这篇文章吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await restoreBlog(id)
        this.$message.success('恢复成功')
        this.loadDeletedBlogList()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '恢复失败')
        }
      }
    },

    async handlePermanentDelete(id) {
      try {
        await this.$confirm('确定要彻底删除这篇文章吗？删除后无法恢复！', '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'error'
        })

        await permanentDeleteBlog(id)
        this.$message.success('彻底删除成功')
        this.loadDeletedBlogList()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '删除失败')
        }
      }
    },

    formatTime(time) {
      if (!time) return ''
      return time.replace('T', ' ')
    }
  }
}
</script>

<style scoped>
.deleted-blog-list {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 5px 0;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.table-container {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(79, 172, 254, 0.15);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(79, 172, 254, 0.2);
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state i {
  font-size: 64px;
  color: #ddd;
  margin-bottom: 20px;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
}
</style>
