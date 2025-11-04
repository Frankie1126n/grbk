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
      <div class="upload-trigger">
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
      default: 'image', // image, avatar, cover, content
      validator: value => ['image', 'avatar', 'cover', 'content'].includes(value)
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
        content: '/file/upload/content'
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
  border: 2px dashed rgba(79, 172, 254, 0.5);
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.05);
}

.upload-trigger:hover {
  border-color: #4facfe;
  background: rgba(79, 172, 254, 0.1);
}

.upload-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.6);
}

.upload-placeholder i {
  font-size: 40px;
  margin-bottom: 10px;
}

.upload-placeholder span {
  font-size: 14px;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  text-align: center;
}
</style>
