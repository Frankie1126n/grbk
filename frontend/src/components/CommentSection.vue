<template>
  <div class="comment-section">
    <h3 class="section-title">评论 ({{ totalCount }})</h3>

    <!-- Comment Input -->
    <div class="comment-input-box card">
      <el-avatar :src="userInfo.avatarUrl || defaultAvatar" :size="40" class="clickable-avatar" @click.native="previewAvatar(userInfo.avatarUrl || defaultAvatar)" />
      <div class="input-area">
        <el-input
          v-model="newComment"
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          maxlength="500"
          show-word-limit
        />
        <!-- Image Preview for Comment -->
        <div v-if="getImagesFromContent(newComment).length > 0" class="image-preview-container">
          <div v-for="(img, index) in getImagesFromContent(newComment)" :key="index" class="preview-image-wrapper">
            <img :src="getFullImageUrl(img)" class="preview-thumbnail" @click="previewAvatar(getFullImageUrl(img))" />
            <i class="el-icon-close remove-image" @click="removeImageFromContent('newComment', img)"></i>
          </div>
        </div>
        <div class="input-actions">
          <el-upload
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :on-success="(res) => handleImageSuccess(res, 'comment')"
            accept="image/*"
            class="image-uploader"
          >
            <el-button size="small" icon="el-icon-picture">插入图片</el-button>
          </el-upload>
          <el-button type="primary" size="small" @click="handleSubmitComment" :loading="submitting">
            发布评论
          </el-button>
        </div>
      </div>
    </div>

    <!-- Comment List -->
    <div class="comment-list" v-loading="loading">
      <div v-for="comment in comments" :key="comment.id" class="comment-item card">
        <!-- Pinned Badge -->
        <div v-if="comment.isPinned === 1" class="pinned-badge">
          <i class="el-icon-top"></i> 置顶
        </div>

        <div class="comment-header">
          <el-avatar :src="comment.avatarUrl || defaultAvatar" :size="36" class="clickable-avatar" @click.native="goToUserProfile(comment.userId)" />
          <div class="user-info">
            <span class="username clickable" @click="goToUserProfile(comment.userId)">{{ comment.username }}</span>
            <span class="time">{{ formatTime(comment.createTime) }}</span>
          </div>
          <div class="comment-actions" v-if="canManageComment(comment)">
            <el-dropdown @command="(cmd) => handleCommand(cmd, comment)">
              <span class="action-trigger">
                <i class="el-icon-more"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="pin" v-if="canPinComment(comment)">
                  {{ comment.isPinned === 1 ? '取消置顶' : '置顶' }}
                </el-dropdown-item>
                <el-dropdown-item command="delete">删除</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>

        <div class="comment-content" v-html="formatContent(comment.content)"></div>

        <div class="comment-footer">
          <span class="reply-btn" @click="showReplyInput(comment)">
            <i class="el-icon-chat-line-round"></i> 回复
          </span>
          <span v-if="comment.replyCount > 0" class="expand-btn" @click="toggleReplies(comment.id)">
            <i :class="isExpanded(comment.id) ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
            {{ isExpanded(comment.id) ? '收起' : '查看' }}回复 ({{ comment.replyCount }})
          </span>
        </div>

        <!-- Reply Input -->
        <div v-if="replyingTo === comment.id" class="reply-input-box">
          <el-input
            v-model="replyContent"
            type="textarea"
            :rows="2"
            :placeholder="`回复 @${comment.username}...`"
            maxlength="500"
            show-word-limit
          />
          <!-- Image Preview for Reply -->
          <div v-if="getImagesFromContent(replyContent).length > 0" class="image-preview-container">
            <div v-for="(img, index) in getImagesFromContent(replyContent)" :key="index" class="preview-image-wrapper">
              <img :src="getFullImageUrl(img)" class="preview-thumbnail" @click="previewAvatar(getFullImageUrl(img))" />
              <i class="el-icon-close remove-image" @click="removeImageFromContent('replyContent', img)"></i>
            </div>
          </div>
          <div class="reply-actions">
            <el-upload
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :on-success="(res) => handleImageSuccess(res, 'reply')"
              accept="image/*"
              class="image-uploader"
            >
              <el-button size="small" icon="el-icon-picture">插入图片</el-button>
            </el-upload>
            <el-button size="small" @click="cancelReply">取消</el-button>
            <el-button type="primary" size="small" @click="handleSubmitReply(comment)" :loading="submitting">
              发布
            </el-button>
          </div>
        </div>

        <!-- Replies -->
        <div v-if="comment.replies && comment.replies.length > 0 && isExpanded(comment.id)" class="replies">
          <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
            <div class="reply-header">
              <el-avatar :src="reply.avatarUrl || defaultAvatar" :size="28" class="clickable-avatar" @click.native="goToUserProfile(reply.userId)" />
              <div class="user-info">
                <span class="username clickable" @click="goToUserProfile(reply.userId)">{{ reply.username }}</span>
                <span v-if="reply.replyToUsername" class="reply-to">
                  回复 @{{ reply.replyToUsername }}
                </span>
                <span class="time">{{ formatTime(reply.createTime) }}</span>
              </div>
              <div class="comment-actions" v-if="canDeleteComment(reply)">
                <i class="el-icon-delete" @click="handleDeleteComment(reply.id)"></i>
              </div>
            </div>
            <div class="reply-content" v-html="formatContent(reply.content)"></div>
            <div class="reply-footer">
              <span class="reply-btn" @click="showReplyToReply(comment, reply)">
                <i class="el-icon-chat-line-round"></i> 回复
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="!loading && comments.length === 0" class="empty-state">
        <i class="el-icon-chat-line-square"></i>
        <p>暂无评论，快来发表第一条评论吧！</p>
      </div>
    </div>

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
import { addComment, getCommentList, deleteComment, pinComment } from '@/api/comment'
import { mapGetters } from 'vuex'

export default {
  name: 'CommentSection',
  props: {
    blogId: {
      type: Number,
      required: true
    },
    blogAuthorId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      comments: [],
      loading: false,
      newComment: '',
      replyContent: '',
      replyingTo: null,
      replyToUser: null,
      submitting: false,
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      expandedComments: new Set(), // 记录展开的评论 ID
      uploadUrl: (process.env.VUE_APP_API_URL || 'http://localhost:8080/api') + '/file/upload/content',
      uploadHeaders: {
        'Authorization': localStorage.getItem('token')
      },
      imagePreviewVisible: false,
      previewImageUrl: ''
    }
  },
  computed: {
    ...mapGetters('user', ['userInfo']),
    isAdmin() {
      return this.userInfo && this.userInfo.role === 'admin'
    },
    isBlogAuthor() {
      return this.userInfo && this.userInfo.id === this.blogAuthorId
    },
    totalCount() {
      let count = this.comments.length
      this.comments.forEach(c => {
        if (c.replies) count += c.replies.length
      })
      return count
    }
  },
  mounted() {
    this.loadComments()
    // 注册全局图片预览方法
    window.previewCommentImage = (src) => {
      this.previewImageUrl = src
      this.imagePreviewVisible = true
    }
  },
  beforeDestroy() {
    // 清理全局方法
    delete window.previewCommentImage
  },
  methods: {
    async loadComments() {
      this.loading = true
      try {
        const response = await getCommentList(this.blogId)
        this.comments = response.data || []
      } catch (error) {
        this.$message.error(error.message || '加载评论失败')
      } finally {
        this.loading = false
      }
    },

    async handleSubmitComment() {
      if (!this.newComment.trim() && this.newCommentImages.length === 0) {
        this.$message.warning('请输入评论内容或上传图片')
        return
      }

      this.submitting = true
      try {
        // 构建包含文本和图片的内容
        let content = this.newComment.trim()
        this.newCommentImages.forEach(img => {
          const baseUrl = process.env.VUE_APP_API_URL || 'http://localhost:8080/api'
          const relativePath = img.startsWith(baseUrl) ? img.replace(baseUrl, '') : img
          content += `\n![image](${relativePath})`
        })

        await addComment({
          blogId: this.blogId,
          content: content,
          parentId: null,
          replyToUserId: null
        })
        this.$message.success('评论成功')
        this.newComment = ''
        this.newCommentImages = []
        this.loadComments()
      } catch (error) {
        this.$message.error(error.message || '评论失败')
      } finally {
        this.submitting = false
      }
    },

    showReplyInput(comment) {
      this.replyingTo = comment.id
      this.replyToUser = { id: comment.userId, username: comment.username }
    },

    showReplyToReply(parentComment, reply) {
      this.replyingTo = parentComment.id
      this.replyToUser = { id: reply.userId, username: reply.username }
    },

    cancelReply() {
      this.replyingTo = null
      this.replyToUser = null
      this.replyContent = ''
    },

    async handleSubmitReply(comment) {
      if (!this.replyContent.trim()) {
        this.$message.warning('请输入回复内容')
        return
      }

      this.submitting = true
      try {
        await addComment({
          blogId: this.blogId,
          content: this.replyContent.trim(),
          parentId: comment.id,
          replyToUserId: this.replyToUser.id
        })
        this.$message.success('回复成功')
        this.cancelReply()
        this.loadComments()
      } catch (error) {
        this.$message.error(error.message || '回复失败')
      } finally {
        this.submitting = false
      }
    },

    async handleCommand(command, comment) {
      if (command === 'delete') {
        this.handleDeleteComment(comment.id)
      } else if (command === 'pin') {
        this.handlePinComment(comment)
      }
    },

    async handleDeleteComment(id) {
      try {
        await this.$confirm('确定要删除这条评论吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await deleteComment(id)
        this.$message.success('删除成功')
        this.loadComments()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '删除失败')
        }
      }
    },

    async handlePinComment(comment) {
      const isPinned = comment.isPinned === 1 ? 0 : 1
      try {
        await pinComment(comment.id, isPinned)
        this.$message.success(isPinned === 1 ? '置顶成功' : '取消置顶成功')
        this.loadComments()
      } catch (error) {
        this.$message.error(error.message || '操作失败')
      }
    },

    canManageComment(comment) {
      // 管理员、文章作者、评论作者可以管理评论
      return this.isAdmin || this.isBlogAuthor || (this.userInfo && this.userInfo.id === comment.userId)
    },

    canDeleteComment(comment) {
      // 管理员、文章作者、评论作者可以删除评论
      return this.isAdmin || this.isBlogAuthor || (this.userInfo && this.userInfo.id === comment.userId)
    },

    canPinComment(comment) {
      // 只有文章作者和管理员可以置顶评论，且只能置顶顶级评论
      return (this.isBlogAuthor || this.isAdmin) && !comment.parentId
    },

    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const now = new Date()
      const diff = now - date

      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
      if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
      if (diff < 2592000000) return `${Math.floor(diff / 86400000)}天前`

      return time.split(' ')[0]
    },

    toggleReplies(commentId) {
      if (this.expandedComments.has(commentId)) {
        this.expandedComments.delete(commentId)
      } else {
        this.expandedComments.add(commentId)
      }
      // 触发响应式更新
      this.$forceUpdate()
    },

    isExpanded(commentId) {
      return this.expandedComments.has(commentId)
    },

    previewAvatar(avatarUrl) {
      this.previewImageUrl = avatarUrl
      this.imagePreviewVisible = true
    },

    goToUserProfile(userId) {
      this.$router.push(`/user/${userId}`)
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

    removeImageFromContent(field, imageSrc) {
      // 移除 Markdown 语法
      const markdownPattern = new RegExp(`!\\[([^\\]]*)\\]\\(${imageSrc.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}\\)`, 'g')
      
      if (field === 'newComment') {
        this.newComment = this.newComment.replace(markdownPattern, '').trim()
      } else if (field === 'replyContent') {
        this.replyContent = this.replyContent.replace(markdownPattern, '').trim()
      }
    },

    beforeUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt10M = file.size / 1024 / 1024 < 10

      if (!isImage) {
        this.$message.error('只能上传图片文件！')
        return false
      }
      if (!isLt10M) {
        this.$message.error('图片大小不能超过 10MB！')
        return false
      }
      return true
    },

    handleImageSuccess(response, type) {
      if (response.code === 200 && response.data) {
        const imageUrl = response.data
        const imageMarkdown = `![image](${imageUrl})`
        
        if (type === 'comment') {
          // 在光标位置插入 Markdown
          const textarea = document.querySelector('.comment-input-box textarea')
          const startPos = textarea.selectionStart
          const endPos = textarea.selectionEnd
          const beforeText = this.newComment.substring(0, startPos)
          const afterText = this.newComment.substring(endPos)
          this.newComment = beforeText + imageMarkdown + afterText
          
          // 设置光标位置
          this.$nextTick(() => {
            const newPos = startPos + imageMarkdown.length
            textarea.setSelectionRange(newPos, newPos)
            textarea.focus()
          })
        } else if (type === 'reply') {
          // 在光标位置插入 Markdown
          const textarea = document.querySelector('.reply-input-box textarea')
          const startPos = textarea.selectionStart
          const endPos = textarea.selectionEnd
          const beforeText = this.replyContent.substring(0, startPos)
          const afterText = this.replyContent.substring(endPos)
          this.replyContent = beforeText + imageMarkdown + afterText
          
          // 设置光标位置
          this.$nextTick(() => {
            const newPos = startPos + imageMarkdown.length
            textarea.setSelectionRange(newPos, newPos)
            textarea.focus()
          })
        }
        
        this.$message.success('图片上传成功')
      } else {
        this.$message.error('图片上传失败')
      }
    },

    formatContent(content) {
      if (!content) return ''
      
      const baseUrl = process.env.VUE_APP_API_URL || 'http://localhost:8080/api'
      
      // 转换 Markdown 图片语法为 HTML img 标签
      let formattedContent = content.replace(/!\[([^\]]*)\]\(([^)]+)\)/g, (match, alt, src) => {
        // 如果是相对路径，添加完整 URL
        if (!src.startsWith('http')) {
          src = baseUrl + src
        }
        return `<img src="${src}" alt="${alt}" class="comment-image" onclick="window.previewCommentImage('${src}')" style="max-width: 120px; max-height: 120px; width: auto; height: auto; object-fit: contain;" />`
      })
      
      // 转换换行符为 <br>
      formattedContent = formattedContent.replace(/\n/g, '<br>')
      
      return formattedContent
    }
  }
}
</script>

<style scoped>
.comment-section {
  margin-top: 40px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(79, 172, 254, 0.2);
  border-radius: 12px;
  padding: 20px;
}

.comment-input-box {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
}

.input-area {
  flex: 1;
}

.input-actions {
  margin-top: 10px;
  text-align: right;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  position: relative;
}

.pinned-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  color: #fff;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.comment-header,
.reply-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.user-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.username {
  font-weight: 600;
  color: #333;
}

.reply-to {
  color: #4facfe;
  font-size: 14px;
}

.time {
  font-size: 12px;
  color: #999;
}

.comment-actions {
  cursor: pointer;
  color: #666;
}

.action-trigger {
  padding: 5px 10px;
  border-radius: 4px;
  transition: all 0.3s;
}

.action-trigger:hover {
  background: rgba(79, 172, 254, 0.1);
}

.comment-content,
.reply-content {
  margin-bottom: 12px;
  line-height: 1.6;
  color: #333;
  word-wrap: break-word;
  overflow-wrap: break-word;
}

.comment-footer,
.reply-footer {
  display: flex;
  gap: 20px;
}

.reply-btn {
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: color 0.3s;
}

.reply-btn:hover {
  color: #4facfe;
}

.expand-btn {
  font-size: 14px;
  color: #4facfe;
  cursor: pointer;
  margin-left: 20px;
  transition: all 0.3s;
  font-weight: 500;
}

.expand-btn:hover {
  color: #00f2fe;
}

.reply-input-box {
  margin-top: 15px;
  padding-left: 48px;
}

.input-actions {
  margin-top: 10px;
  text-align: right;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.reply-actions {
  margin-top: 10px;
  text-align: right;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.image-uploader {
  display: inline-block;
}

.replies {
  margin-top: 15px;
  padding-left: 48px;
  border-left: 2px solid rgba(79, 172, 254, 0.2);
}

.reply-item {
  padding: 15px;
  background: rgba(79, 172, 254, 0.05);
  border-radius: 8px;
  margin-bottom: 10px;
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

.comment-image {
  max-width: 120px;
  max-height: 120px;
  width: auto;
  height: auto;
  object-fit: contain;
  border-radius: 8px;
  margin: 10px 0;
  cursor: pointer;
  transition: transform 0.3s;
  display: inline-block;
  vertical-align: middle;
}

.comment-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(79, 172, 254, 0.3);
}

/* 回复中的图片保持相同尺寸 */
.reply-content .comment-image {
  max-width: 120px;
  max-height: 120px;
}

/* Clickable Avatar */
.clickable-avatar {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.clickable-avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(79, 172, 254, 0.4);
}

/* Clickable Username */
.username.clickable {
  cursor: pointer;
  transition: color 0.3s;
}

.username.clickable:hover {
  color: #4facfe;
}

/* Image Preview Container */
.image-preview-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
  padding: 10px;
  background: rgba(79, 172, 254, 0.05);
  border-radius: 8px;
}

.preview-image-wrapper {
  position: relative;
  display: inline-block;
}

.preview-thumbnail {
  width: 80px;
  height: 80px;
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
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 12px;
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
