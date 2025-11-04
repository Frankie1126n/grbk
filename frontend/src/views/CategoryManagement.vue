<template>
  <div class="category-management">
    <div class="page-header">
      <h2>分类管理</h2>
      <el-button type="primary" icon="el-icon-plus" @click="showAddDialog">添加分类</el-button>
    </div>

    <div class="table-container card">
      <el-table :data="categoryList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="articleCount" label="文章数量" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
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
            <el-button
              type="danger"
              size="small"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Add/Edit Dialog -->
    <el-dialog
      :title="dialogMode === 'add' ? '添加分类' : '编辑分类'"
      :visible.sync="dialogVisible"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="categoryForm" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
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
import { getCategoryList, addCategory, updateCategory, deleteCategory } from '@/api/category'

export default {
  name: 'CategoryManagement',
  data() {
    return {
      categoryList: [],
      loading: false,
      dialogVisible: false,
      dialogMode: 'add',
      submitting: false,
      form: {
        id: null,
        name: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入分类名称', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.loadCategoryList()
  },
  methods: {
    async loadCategoryList() {
      this.loading = true
      try {
        const response = await getCategoryList()
        this.categoryList = response.data || []
      } catch (error) {
        this.$message.error(error.message || '加载分类列表失败')
      } finally {
        this.loading = false
      }
    },

    showAddDialog() {
      this.dialogMode = 'add'
      this.form = { id: null, name: '' }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.categoryForm && this.$refs.categoryForm.clearValidate()
      })
    },

    showEditDialog(row) {
      this.dialogMode = 'edit'
      this.form = { id: row.id, name: row.name }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.categoryForm && this.$refs.categoryForm.clearValidate()
      })
    },

    handleSubmit() {
      this.$refs.categoryForm.validate(async (valid) => {
        if (!valid) return

        this.submitting = true
        try {
          if (this.dialogMode === 'add') {
            await addCategory(this.form.name)
            this.$message.success('添加成功')
          } else {
            await updateCategory(this.form.id, this.form.name)
            this.$message.success('更新成功')
          }
          this.dialogVisible = false
          this.loadCategoryList()
        } catch (error) {
          this.$message.error(error.message || '操作失败')
        } finally {
          this.submitting = false
        }
      })
    },

    async handleDelete(row) {
      try {
        await this.$confirm(`确定要删除分类"${row.name}"吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await deleteCategory(row.id)
        this.$message.success('删除成功')
        this.loadCategoryList()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '删除失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.category-management {
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
</style>
