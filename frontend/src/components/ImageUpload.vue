<template>
  <div class="upload-container">
    <el-upload
      :action="uploadUrl"
      :headers="headers"
      :show-file-list="false"
      :on-success="handleSuccess"
      :on-error="handleError"
      :before-upload="beforeUpload"
      :accept="accept"
      :disabled="disabled"
    >
      <div ref="uploadTrigger" class="upload-trigger">
        <img v-if="imageUrl" :src="imageUrl" class="upload-image" />
        <div v-else class="upload-placeholder">
          <i :class="icon"></i>
          <span>{{ placeholder }}</span>
        </div>
      </div>
    </el-upload>
    <div v-if="tip" class="upload-tip">{{ tip }}</div>
  </div>
</template>

<script>
export default {
  name: 'ImageUpload',
  props: {
    value: {
      type: String,
      default: ''
    },
    uploadType: {
      type: String,
      default: 'image', // image, avatar, cover, content, background
      validator: value => ['image', 'avatar', 'cover', 'content', 'background'].includes(value)
    },
    placeholder: {
      type: String,
      default: '点击上传图片'
    },
    icon: {
      type: String,
      default: 'el-icon-plus'
    },
    tip: {
      type: String,
      default: ''
    },
    maxSize: {
      type: Number,
      default: 10 // MB
    },
    accept: {
      type: String,
      default: 'image/jpeg,image/jpg,image/png,image/gif'
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      imageUrl: this.value
    }
  },
  computed: {
    uploadUrl() {
      const baseUrl = process.env.VUE_APP_API_URL || 'http://localhost:8080/api'
      const typeMap = {
        image: '/file/upload/image',
        avatar: '/file/upload/avatar',
        cover: '/file/upload/cover',
        content: '/file/upload/content',
        background: '/file/upload/background'
      }
      return baseUrl + typeMap[this.uploadType]
    },
    headers() {
      const token = this.$store.state.user.token
      return {
        Authorization: token ? `Bearer ${token}` : ''
      }
    }
  },
  watch: {
    value(newVal) {
      this.imageUrl = newVal
    }
  },
  methods: {
    beforeUpload(file) {
      const isValidType = file.type.startsWith('image/')
      const isValidSize = file.size / 1024 / 1024 < this.maxSize

      if (!isValidType) {
        this.$message.error('只能上传图片文件！')
        return false
      }
      if (!isValidSize) {
        this.$message.error(`图片大小不能超过 ${this.maxSize}MB！`)
        return false
      }
      return true
    },
    handleSuccess(response) {
      if (response.code === 200) {
        // 确保 URL是完整的
        let imageUrl = response.data
        if (!imageUrl.startsWith('http')) {
          const baseUrl = process.env.VUE_APP_API_URL || 'http://localhost:8080/api'
          imageUrl = baseUrl + imageUrl
        }
        this.imageUrl = imageUrl
        this.$emit('input', imageUrl)
        this.$emit('success', imageUrl)
        
        // 添加笔记本翻开动画
        const uploadTrigger = this.$refs.uploadTrigger
        if (uploadTrigger) {
          uploadTrigger.classList.add('notebook-flip')
          
          // 移除动画类以便下次使用
          setTimeout(() => {
            uploadTrigger.classList.remove('notebook-flip')
          }, 500)
        }
        
        this.$message.success('上传成功')
      } else {
        this.$message.error(response.message || '上传失败')
      }
    },
    handleError(error) {
      console.error('Upload error:', error)
      this.$message.error('上传失败，请重试')
    }
  }
}
</script>

<style scoped>
.upload-container {
  display: inline-block;
}

.upload-trigger {
  width: 150px;
  height: 150px;
  border: 2px dashed rgba(255, 183, 197, 0.4);
  border-radius: 12px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.8);
  position: relative;
}

/* 铅笔装饰 */
.upload-trigger::after {
  content: '✏️';
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 20px;
  opacity: 0.3;
}

.upload-trigger:hover {
  border-color: #FFB7C5;
  background: rgba(255, 255, 255, 0.95);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 183, 197, 0.25);
}

.upload-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 10px;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #FF9F43;
  background: rgba(255, 251, 235, 0.9);
}

.upload-placeholder i {
  font-size: 40px;
  margin-bottom: 10px;
  color: #FFB7C5;
}

.upload-placeholder span {
  font-size: 14px;
  color: #6b6b6b;
  font-weight: 500;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #9ca3af;
  text-align: center;
}

/* 笔记本翻开动画 */
.notebook-flip {
  animation: notebookFlip 0.5s ease-in-out;
  transform-origin: center;
}

@keyframes notebookFlip {
  0% {
    transform: rotateX(0deg);
    box-shadow: 0 4px 15px rgba(255, 183, 197, 0.25);
  }
  50% {
    transform: rotateX(90deg);
    box-shadow: 0 0 0 rgba(255, 183, 197, 0);
  }
  100% {
    transform: rotateX(0deg);
    box-shadow: 0 4px 15px rgba(255, 183, 197, 0.25);
  }
}
</style>
