<template>
  <div class="blog-editor-page">
    <div class="editor-container">
      <div class="editor-header">
        <h2>{{ isEdit ? '编辑文章' : '发布文章' }}</h2>
      </div>
      
      <el-form
        ref="blogForm"
        :model="blogForm"
        :rules="blogRules"
        label-width="100px"
        class="blog-form"
      >
        <el-form-item label="文章标题" prop="title">
          <el-input
            v-model="blogForm.title"
            placeholder="请输入文章标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="封面图片">
          <ImageUpload
            v-model="blogForm.coverImage"
            upload-type="cover"
            placeholder="点击上传封面"
            tip="建议尺寸 800x450，支持 jpg、png、gif 格式"
          />
        </el-form-item>

        <el-form-item label="文章分类" prop="categoryId">
          <el-select
            v-model="blogForm.categoryId"
            placeholder="请选择分类"
            style="width: 100%"
          >
            <el-option
              v-for="category in categoryList"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="文章标签" prop="tagIds">
          <el-select
            v-model="blogForm.tagIds"
            multiple
            placeholder="请选择标签"
            style="width: 100%"
          >
            <el-option
              v-for="tag in tagList"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="文章摘要" prop="summary">
          <el-input
            v-model="blogForm.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入文章摘要"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="文章内容" prop="content">
          <div class="editor-toolbar">
            <el-button
              size="small"
              icon="el-icon-picture"
              @click="handleUploadImage"
            >
              插入图片
            </el-button>
            <input
              ref="imageInput"
              type="file"
              accept="image/*"
              style="display: none"
              @change="handleImageChange"
            />
          </div>
          <el-input
            ref="contentTextarea"
            v-model="blogForm.content"
            type="textarea"
            :rows="15"
            placeholder="请输入文章内容，支持 Markdown 格式"
          />
          <!-- Image Preview for Blog Content -->
          <div v-if="getImagesFromContent(blogForm.content).length > 0" class="image-preview-container">
            <div v-for="(img, index) in getImagesFromContent(blogForm.content)" :key="index" class="preview-image-wrapper">
              <img :src="getFullImageUrl(img)" class="preview-thumbnail" @click="openImagePreview(getFullImageUrl(img))" />
              <i class="el-icon-close remove-image" @click="removeContentImage(img)"></i>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="发布状态">
          <el-radio-group v-model="blogForm.publishStatus">
            <el-radio :label="1">立即发布</el-radio>
            <el-radio :label="0">保存草稿</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="是否公开">
          <el-radio-group v-model="blogForm.isPublic">
            <el-radio :label="1">
              <i class="el-icon-view"></i> 公开（所有人可见）
            </el-radio>
            <el-radio :label="0">
              <i class="el-icon-lock"></i> 私密（仅自己可见）
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            @click="handleSubmit"
            :loading="loading"
          >
            {{ isEdit ? '更新文章' : '发布文章' }}
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
          <el-button @click="handlePreview">预览</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 预览对话框 -->
    <el-dialog
      title="文章预览"
      :visible.sync="previewVisible"
      width="80%"
      class="preview-dialog"
    >
      <div class="preview-content">
        <h1>{{ blogForm.title }}</h1>
        <div class="preview-meta">
          <span>分类: {{ getCategoryName(blogForm.categoryId) }}</span>
          <span>标签: {{ getTagNames(blogForm.tagIds).join(', ') }}</span>
        </div>
        <div v-if="blogForm.coverImage" class="preview-cover">
          <img :src="blogForm.coverImage" alt="封面">
        </div>
        <div class="preview-summary">{{ blogForm.summary }}</div>
        <div class="preview-body" v-html="formatContent(blogForm.content)"></div>
      </div>
    </el-dialog>

    <!-- Image Preview Dialog -->
    <el-dialog
      :visible.sync="imagePreviewVisible"
      width="450px"
      top="20vh"
      :append-to-body="true"
      custom-class="image-preview-dialog"
    >
      <img :src="previewImageUrl" style="width: 400px; height: 400px; object-fit: contain;" alt="Preview" />
    </el-dialog>
  </div>
</template>

<script>
import ImageUpload from '@/components/ImageUpload'
import { uploadContent } from '@/api/file'
import { getBlogById, saveBlog } from '@/api/blog'
import { mapState } from 'vuex'

export default {
  name: 'BlogEditor',
  components: {
    ImageUpload
  },
  data() {
    return {
      blogForm: {
        id: null,
        title: '',
        content: '',
        summary: '',
        coverImage: '',
        categoryId: null,
        tagIds: [],
        publishStatus: 1,
        isPublic: 1 // 默认公开
      },
      blogRules: {
        title: [
          { required: true, message: '请输入文章标题', trigger: 'blur' },
          { max: 200, message: '标题长度不能超过200个字符', trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: '请选择分类', trigger: 'change' }
        ],
        content: [
          { required: true, message: '请输入文章内容', trigger: 'blur' }
        ]
      },
      loading: false,
      previewVisible: false,
      isEdit: false,
      imagePreviewVisible: false,
      previewImageUrl: ''
    }
  },
  computed: {
    ...mapState('category', ['categoryList']),
    ...mapState('tag', ['tagList'])
  },
  mounted() {
    this.loadData()
    
    // 如果有 id 参数，则是编辑模式
    if (this.$route.params.id) {
      this.isEdit = true
      this.loadBlog(this.$route.params.id)
    }
  },
  methods: {
    async loadData() {
      await Promise.all([
        this.$store.dispatch('category/getCategoryList'),
        this.$store.dispatch('tag/getTagList')
      ])
    },
    async loadBlog(id) {
      try {
        const res = await getBlogById(id)
        if (res.code === 200) {
          this.blogForm = {
            id: res.data.id,
            title: res.data.title,
            content: res.data.content,
            summary: res.data.summary || '',
            coverImage: res.data.coverImage || '',
            categoryId: res.data.categoryId,
            tagIds: res.data.tags ? res.data.tags.map(t => t.id) : [],
            publishStatus: res.data.publishStatus,
            isPublic: res.data.isPublic !== undefined ? res.data.isPublic : 1
          }
        }
      } catch (error) {
        this.$message.error('加载文章失败')
        this.$router.push('/home')
      }
    },
    handleUploadImage() {
      this.$refs.imageInput.click()
    },
    async handleImageChange(e) {
      const file = e.target.files[0]
      if (!file) return

      try {
        const res = await uploadContent(file)
        if (res.code === 200) {
          // 在光标位置插入图片 Markdown 语法
          const imageMarkdown = `![图片](${res.data})`
          this.insertTextAtCursor(imageMarkdown)
          this.$message.success('图片上传成功')
        }
      } catch (error) {
        this.$message.error('图片上传失败')
      }
      
      // 清空 input
      e.target.value = ''
    },
    insertTextAtCursor(text) {
      const textarea = this.$refs.contentTextarea.$el.querySelector('textarea')
      const startPos = textarea.selectionStart
      const endPos = textarea.selectionEnd
      const content = this.blogForm.content
      
      this.blogForm.content = 
        content.substring(0, startPos) + 
        text + 
        content.substring(endPos)
      
      // 设置光标位置
      this.$nextTick(() => {
        const newPos = startPos + text.length
        textarea.setSelectionRange(newPos, newPos)
        textarea.focus()
      })
    },

    getImagesFromContent(content) {
      if (!content) return []
      const regex = /!\[([^\]]*)\]\(([^)]+)\)/g
      const images = []
      let match
      while ((match = regex.exec(content)) !== null) {
        images.push(match[2])
      }
      return images
    },

    getFullImageUrl(src) {
      const baseUrl = process.env.VUE_APP_API_URL || 'http://localhost:8080/api'
      if (!src.startsWith('http')) {
        return baseUrl + src
      }
      return src
    },

    removeContentImage(imageSrc) {
      // 移除 Markdown 语法
      const markdownPattern = new RegExp(`!\\[([^\\]]*)\\]\\(${imageSrc.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}\\)`, 'g')
      this.blogForm.content = this.blogForm.content.replace(markdownPattern, '').trim()
    },

    openImagePreview(url) {
      this.previewImageUrl = url
      this.imagePreviewVisible = true
    },
    async handleSubmit() {
      this.$refs.blogForm.validate(async (valid) => {
        if (!valid) {
          return false
        }

        this.loading = true
        try {
          const res = await saveBlog(this.blogForm)
          if (res.code === 200) {
            this.$message({
              message: this.isEdit ? '更新成功' : '发布成功',
              type: 'success',
              duration: 1500
            })
            // 等待消息显示后跳转
            setTimeout(() => {
              this.$router.push('/home')
            }, 1500)
          }
        } catch (error) {
          this.$message.error(error.message || '操作失败')
        } finally {
          this.loading = false
        }
      })
    },
    handleCancel() {
      this.$router.push('/home')
    },
    handlePreview() {
      if (!this.blogForm.title || !this.blogForm.content) {
        this.$message.warning('请填写标题和内容后再预览')
        return
      }
      this.previewVisible = true
    },
    getCategoryName(id) {
      const category = this.categoryList.find(c => c.id === id)
      return category ? category.name : ''
    },
    getTagNames(ids) {
      return this.tagList
        .filter(t => ids.includes(t.id))
        .map(t => t.name)
    },
    formatContent(content) {
      // 简单的 Markdown 渲染（实际项目可使用 marked 等库）
      return content
        .replace(/\n/g, '<br>')
        .replace(/!\[([^\]]*)\]\(([^)]+)\)/g, '<img src="$2" alt="$1" style="max-width: 100%;">')
    }
  }
}
</script>

<style scoped>
.blog-editor-page {
  min-height: 100vh;
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
}

.blog-editor-page::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    linear-gradient(rgba(79, 172, 254, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(79, 172, 254, 0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  pointer-events: none;
}

.editor-container {
  max-width: 1000px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 1;
}

.editor-header {
  text-align: center;
  margin-bottom: 40px;
}

.editor-header h2 {
  color: #fff;
  font-size: 32px;
  font-weight: 600;
  margin: 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.blog-form >>> .el-form-item__label {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

.blog-form >>> .el-input__inner,
.blog-form >>> .el-textarea__inner {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #fff;
  transition: all 0.3s ease;
}

.blog-form >>> .el-input__inner::placeholder,
.blog-form >>> .el-textarea__inner::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.blog-form >>> .el-input__inner:focus,
.blog-form >>> .el-textarea__inner:focus {
  background: rgba(255, 255, 255, 0.15);
  border-color: #4facfe;
  box-shadow: 0 0 0 2px rgba(79, 172, 254, 0.2);
}

.blog-form >>> .el-select {
  width: 100%;
}

.blog-form >>> .el-radio__label {
  color: rgba(255, 255, 255, 0.9);
}

.editor-toolbar {
  margin-bottom: 10px;
}

.blog-form >>> .el-button {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.blog-form >>> .el-button--primary {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
  color: #fff;
  box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
}

.blog-form >>> .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(79, 172, 254, 0.6);
}

.blog-form >>> .el-button--default {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #fff;
}

.blog-form >>> .el-button--default:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.4);
}

.preview-content {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}

.preview-content h1 {
  color: #333;
  margin-bottom: 15px;
}

.preview-meta {
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
}

.preview-meta span {
  margin-right: 20px;
}

.preview-cover {
  margin-bottom: 20px;
}

.preview-cover img {
  max-width: 100%;
  border-radius: 8px;
}

.preview-summary {
  color: #666;
  font-size: 16px;
  line-height: 1.8;
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 8px;
}

.preview-body {
  color: #333;
  font-size: 16px;
  line-height: 1.8;
}

/* Image Preview Container */
.image-preview-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
  padding: 10px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.preview-image-wrapper {
  position: relative;
  display: inline-block;
}

.preview-thumbnail {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 6px;
  cursor: pointer;
  transition: transform 0.3s;
  border: 2px solid rgba(79, 172, 254, 0.3);
}

.preview-thumbnail:hover {
  transform: scale(1.05);
  border-color: #4facfe;
}

.remove-image {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #ff4d4f;
  color: white;
  border-radius: 50%;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.remove-image:hover {
  background: #ff7875;
  transform: scale(1.1);
}

/* Image Preview Dialog */
.image-preview-dialog {
  background: rgba(0, 0, 0, 0.9);
}

.image-preview-dialog .el-dialog__header {
  display: none;
}

.image-preview-dialog .el-dialog__body {
  padding: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
