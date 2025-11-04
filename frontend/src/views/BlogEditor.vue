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
  position: relative;
}

.editor-container {
  max-width: 1000px;
  margin: 0 auto;
  background: rgba(255, 251, 235, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(255, 183, 197, 0.25),
              0 4px 12px rgba(163, 230, 53, 0.1);
  border: 2px solid rgba(255, 183, 197, 0.3);
  position: relative;
  z-index: 1;
}

/* 课本装饰 */
.editor-container::before {
  content: '';
  position: absolute;
  top: 12px;
  left: 12px;
  right: 12px;
  bottom: 12px;
  border: 1px dashed rgba(163, 230, 53, 0.25);
  border-radius: 10px;
  pointer-events: none;
}

.editor-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
}

.editor-header h2 {
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 50%, #A3E635 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-size: 32px;
  font-weight: 700;
  margin: 0;
  letter-spacing: 1px;
}

/* 铅笔装饰 */
.editor-header h2::before {
  content: '✏️';
  position: absolute;
  left: 20%;
  top: -5px;
  font-size: 24px;
}

.blog-form >>> .el-form-item__label {
  color: #4a4a4a;
  font-weight: 600;
  font-size: 15px;
}

.blog-form >>> .el-input__inner,
.blog-form >>> .el-textarea__inner {
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid rgba(255, 183, 197, 0.3);
  border-radius: 10px;
  color: #4a4a4a;
  transition: all 0.3s ease;
  font-size: 14px;
}

.blog-form >>> .el-input__inner::placeholder,
.blog-form >>> .el-textarea__inner::placeholder {
  color: rgba(255, 183, 197, 0.5);
}

.blog-form >>> .el-input__inner:focus,
.blog-form >>> .el-textarea__inner:focus {
  background: rgba(255, 255, 255, 1);
  border-color: #FFB7C5;
  box-shadow: 0 0 0 3px rgba(255, 183, 197, 0.15),
              0 2px 8px rgba(255, 183, 197, 0.2);
}

.blog-form >>> .el-select {
  width: 100%;
}

.blog-form >>> .el-select .el-input__inner {
  padding-left: 15px;
}

.blog-form >>> .el-radio__label {
  color: #4a4a4a;
  font-weight: 500;
}

.blog-form >>> .el-radio__input.is-checked .el-radio__inner {
  background: #FFB7C5;
  border-color: #FFB7C5;
}

.blog-form >>> .el-radio__input.is-checked + .el-radio__label {
  color: #FF9F43;
}

.blog-form >>> .el-checkbox__input.is-checked .el-checkbox__inner {
  background: #FFB7C5;
  border-color: #FFB7C5;
}

.blog-form >>> .el-checkbox__input.is-checked + .el-checkbox__label {
  color: #FF9F43;
}

.editor-toolbar {
  margin-bottom: 10px;
}

.blog-form >>> .el-button {
  border-radius: 18px;
  transition: all 0.3s ease;
  font-weight: 600;
  border: 2px solid;
}

.blog-form >>> .el-button--primary {
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  border-color: rgba(255, 255, 255, 0.5);
  color: #fff;
  box-shadow: 0 4px 15px rgba(255, 183, 197, 0.4);
}

.blog-form >>> .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 183, 197, 0.5);
  border-color: rgba(255, 255, 255, 0.8);
}

.blog-form >>> .el-button--default {
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(255, 183, 197, 0.4);
  color: #FF9F43;
}

.blog-form >>> .el-button--default:hover {
  background: rgba(255, 255, 255, 1);
  border-color: #FFB7C5;
  transform: translateY(-2px);
}

.blog-form >>> .el-button--success {
  background: linear-gradient(135deg, #A3E635 0%, #87CEEB 100%);
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0 4px 15px rgba(163, 230, 53, 0.4);
}

.blog-form >>> .el-button--success:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(163, 230, 53, 0.5);
}

.preview-content {
  padding: 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  border: 2px solid rgba(255, 183, 197, 0.2);
}

.preview-content h1 {
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  margin-bottom: 15px;
  font-weight: 700;
}

.preview-meta {
  color: #6b6b6b;
  font-size: 14px;
  margin-bottom: 20px;
}

.preview-meta span {
  margin-right: 20px;
  color: #FF9F43;
}

.preview-cover {
  margin-bottom: 20px;
}

.preview-cover img {
  max-width: 100%;
  border-radius: 10px;
  border: 2px solid rgba(255, 183, 197, 0.3);
}

.preview-summary {
  color: #6b6b6b;
  font-size: 16px;
  line-height: 1.8;
  margin-bottom: 20px;
  padding: 15px;
  background: rgba(255, 183, 197, 0.1);
  border-radius: 10px;
  border-left: 4px solid #FFB7C5;
}

.preview-body {
  color: #4a4a4a;
  font-size: 16px;
  line-height: 1.8;
}

/* Image Preview Container */
.image-preview-container {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 10px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 10px;
  border: 2px dashed rgba(255, 183, 197, 0.3);
}

.preview-image-wrapper {
  position: relative;
  display: inline-block;
}

.preview-thumbnail {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s;
  border: 2px solid rgba(255, 183, 197, 0.4);
}

.preview-thumbnail:hover {
  transform: scale(1.08);
  border-color: #FFB7C5;
  box-shadow: 0 4px 12px rgba(255, 183, 197, 0.3);
}

.remove-image {
  position: absolute;
  top: -8px;
  right: -8px;
  background: linear-gradient(135deg, #FF9F43, #FFB7C5);
  color: white;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  border: 2px solid white;
  box-shadow: 0 2px 8px rgba(255, 159, 67, 0.4);
}

.remove-image:hover {
  transform: scale(1.15);
  box-shadow: 0 3px 12px rgba(255, 159, 67, 0.5);
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

/* 移动端适配 */
@media (max-width: 768px) {
  .editor-container {
    padding: 30px 20px;
  }
  
  .editor-header h2 {
    font-size: 26px;
  }
  
  .editor-header h2::before {
    left: 10%;
  }
}
</style>
