<template>
  <div class="user-management">
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="search-box">
        <el-input
          v-model="searchUsername"
          placeholder="搜索用户名"
          prefix-icon="el-icon-search"
          clearable
          @keyup.enter.native="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
    </div>

    <div class="table-container card">
      <el-table :data="userList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column label="头像" width="100">
          <template slot-scope="scope">
            <el-avatar :src="scope.row.avatarUrl || defaultAvatar" :size="40" />
          </template>
        </el-table-column>
        <el-table-column label="角色" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.role === 'admin' ? 'danger' : 'primary'">
              {{ scope.row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="small"
              icon="el-icon-edit"
              @click="showEditDialog(scope.row)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-wrapper">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- Edit Dialog -->
    <el-dialog
      title="编辑用户"
      :visible.sync="dialogVisible"
      width="500px"
    >
      <el-form :model="form" ref="userForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="form.role">
            <el-radio label="user">普通用户</el-radio>
            <el-radio label="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getUserList, updateUserRoleAndStatus } from '@/api/user'

export default {
  name: 'UserManagement',
  data() {
    return {
      userList: [],
      loading: false,
      dialogVisible: false,
      submitting: false,
      searchUsername: '',
      currentPage: 1,
      pageSize: 10,
      total: 0,
      form: {
        userId: null,
        username: '',
        role: 'user',
        status: 1
      },
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    }
  },
  mounted() {
    this.loadUserList()
  },
  methods: {
    async loadUserList() {
      this.loading = true
      try {
        const response = await getUserList({
          current: this.currentPage,
          size: this.pageSize,
          username: this.searchUsername
        })
        this.userList = response.data.records || []
        this.total = response.data.total || 0
      } catch (error) {
        this.$message.error(error.message || '加载用户列表失败')
      } finally {
        this.loading = false
      }
    },

    handleSearch() {
      this.currentPage = 1
      this.loadUserList()
    },

    handlePageChange(page) {
      this.currentPage = page
      this.loadUserList()
    },

    showEditDialog(row) {
      this.form = {
        userId: row.id,
        username: row.username,
        role: row.role,
        status: row.status
      }
      this.dialogVisible = true
    },

    async handleSubmit() {
      this.submitting = true
      try {
        await updateUserRoleAndStatus({
          userId: this.form.userId,
          role: this.form.role,
          status: this.form.status
        })
        this.$message.success('更新成功')
        this.dialogVisible = false
        this.loadUserList()
      } catch (error) {
        this.$message.error(error.message || '更新失败')
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.search-box {
  display: flex;
  gap: 10px;
}

.search-box .el-input {
  width: 250px;
}

.table-container {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(79, 172, 254, 0.15);
}

.card {
  backdrop-filter: blur(10px);
  border: 1px solid rgba(79, 172, 254, 0.2);
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}
</style>
