<template>
  <div class="comment-section">
    <h3 class="section-title">评论 ({{ totalCount }})</h3>

    <!-- Comment Input -->
    <div class="comment-input-box card">
      <CroppedAvatar 
        :src="userInfo.avatarUrl || defaultAvatar" 
        :size="40" 
        class="clickable-avatar" 
        @click.native="previewAvatar(userInfo.avatarUrl || defaultAvatar)"
      />
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
    <div class="comment-list" ref="commentList" v-loading="loading">
      <div v-for="comment in comments" :key="comment.id" class="comment-item card">
        <!-- Pinned Badge -->
        <div v-if="comment.isPinned === 1" class="pinned-badge">
          <i class="el-icon-top"></i> 置顶
        </div>

        <div class="comment-header">
          <CroppedAvatar 
            :src="comment.avatarUrl || defaultAvatar" 
            :size="36" 
            class="clickable-avatar" 
            @click.native="goToUserProfile(comment.userId)"
          />
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
              <CroppedAvatar 
                :src="reply.avatarUrl || defaultAvatar" 
                :size="28" 
                class="clickable-avatar" 
                @click.native="goToUserProfile(reply.userId)"
              />
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
      width="420px"
      top="10vh"
      :append-to-body="true"
      custom-class="image-preview-dialog"
    >
      <img :src="previewImageUrl" class="preview-image" alt="Preview" />
    </el-dialog>
  </div>
</template>

<script>
import { addComment, getCommentList, deleteComment, pinComment } from '@/api/comment'
import { mapGetters } from 'vuex'
import CroppedAvatar from '@/components/CroppedAvatar'

export default {
  name: 'CommentSection',
  components: {
    CroppedAvatar
  },
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
      uploadUrl: (process.env.VUE_APP_API_URL || '/api') + '/file/upload/content',
      uploadHeaders: {
        'Authorization': localStorage.getItem('token')
      },
      imagePreviewVisible: false,
      previewImageUrl: '',
      newCommentImages: [] // 添加这行来修复图片上传问题
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
      // 修复 forEach 错误：确保 comments 存在且是数组
      if (Array.isArray(this.comments)) {
        this.comments.forEach(c => {
          if (c.replies && Array.isArray(c.replies)) count += c.replies.length
        })
      }
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
    
    // 初始加载时触发动画
    this.$nextTick(() => {
      const commentListElement = this.$refs.commentList
      if (commentListElement) {
        commentListElement.classList.add('animate-enter')
      }
    })
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
        // 确保响应数据存在且是数组
        this.comments = (response.data && Array.isArray(response.data)) ? response.data : []
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
          const baseUrl = process.env.VUE_APP_API_URL || '/api'
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

    // 滚动处理函数
    handleScroll() {
      // 这里可以添加滚动相关的逻辑
      // 例如检测是否滚动到特定区域来触发动画
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
      const baseUrl = process.env.VUE_APP_API_URL || '/api'
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
      
      const baseUrl = process.env.VUE_APP_API_URL || '/api'
      
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
  background: rgba(255, 251, 235, 0.95);
  backdrop-filter: blur(15px);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(255, 183, 197, 0.2),
              0 1px 3px rgba(163, 230, 53, 0.1);
  border: 2px solid rgba(255, 183, 197, 0.3);
  position: relative;
}

.comment-section::before {
  content: '';
  position: absolute;
  top: 12px;
  left: 12px;
  right: 12px;
  bottom: 12px;
  border: 1px dashed rgba(163, 230, 53, 0.2);
  border-radius: 10px;
  pointer-events: none;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  padding-bottom: 10px;
  border-bottom: 2px solid rgba(255, 183, 197, 0.2);
}

.card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 183, 197, 0.2);
  border-radius: 12px;
  padding: 20px;
}

.comment-input-box {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 10px;
  border: 2px solid rgba(255, 183, 197, 0.2);
}

.comment-input-box >>> .el-textarea__inner {
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid rgba(255, 183, 197, 0.3);
  border-radius: 10px;
  color: #4a4a4a;
  transition: all 0.3s ease;
}

.comment-input-box >>> .el-textarea__inner:focus {
  background: rgba(255, 255, 255, 1);
  border-color: #FFB7C5;
  box-shadow: 0 0 0 3px rgba(255, 183, 197, 0.15),
              0 2px 8px rgba(255, 183, 197, 0.2);
}

.input-area {
  flex: 1;
}

.input-actions >>> .el-button--primary {
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  border: 2px solid rgba(255, 255, 255, 0.5);
  color: #fff;
  box-shadow: 0 4px 15px rgba(255, 183, 197, 0.4);
  border-radius: 18px;
  font-weight: 600;
}

.input-actions >>> .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 183, 197, 0.5);
  border-color: rgba(255, 255, 255, 0.8);
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 社团展示区域滚动触发效果 */
.comment-list.animate-enter .comment-item {
  opacity: 0;
  transform: translateY(20px);
  animation: popUpComment 0.3s ease-out forwards;
}

@keyframes popUpComment {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 为每个评论项设置延迟动画 */
.comment-list.animate-enter .comment-item:nth-child(1) { animation-delay: 0.1s; }
.comment-list.animate-enter .comment-item:nth-child(2) { animation-delay: 0.2s; }
.comment-list.animate-enter .comment-item:nth-child(3) { animation-delay: 0.3s; }
.comment-list.animate-enter .comment-item:nth-child(4) { animation-delay: 0.4s; }
.comment-list.animate-enter .comment-item:nth-child(5) { animation-delay: 0.5s; }

.comment-item {
  padding: 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  border: 2px solid rgba(255, 183, 197, 0.2);
  position: relative;
  transition: all 0.3s ease;
}

.comment-item:hover {
  background: rgba(255, 255, 255, 1);
  transform: translateX(5px);
  border-color: rgba(255, 183, 197, 0.4);
  box-shadow: 0 4px 12px rgba(255, 183, 197, 0.2);
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.comment-avatar {
  flex-shrink: 0;
}

.username {
  font-weight: 600;
  color: #333;
  margin: 0;
  font-size: 15px;
}

.time {
  color: #999;
  font-size: 12px;
  margin-left: auto;
}

.comment-content {
  color: #4a4a4a;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 15px;
  word-wrap: break-word;
}

.comment-actions {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #999;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.action-item i {
  font-size: 14px;
}

.action-item:hover {
  color: #FF9F43;
}

.reply-btn {
  margin-left: auto;
}

.pinned-badge {
  display: inline-block;
  padding: 2px 8px;
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  color: white;
  border-radius: 10px;
  font-size: 12px;
  font-weight: bold;
  margin-right: 10px;
  vertical-align: middle;
}

.expand-btn {
  color: #FFB7C5;
  cursor: pointer;
  margin-left: 20px;
  transition: all 0.3s;
  font-weight: 500;
}

.expand-btn:hover {
  color: #FF9F43;
}

.reply-input-box {
  margin-top: 15px;
  padding-left: 48px;
}

.reply-input-box >>> .el-textarea__inner {
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid rgba(255, 183, 197, 0.3);
  border-radius: 10px;
  color: #4a4a4a;
  transition: all 0.3s ease;
}

.reply-input-box >>> .el-textarea__inner:focus {
  background: rgba(255, 255, 255, 1);
  border-color: #FFB7C5;
  box-shadow: 0 0 0 3px rgba(255, 183, 197, 0.15),
              0 2px 8px rgba(255, 183, 197, 0.2);
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
  border-left: 2px solid rgba(255, 183, 197, 0.3);
}

.reply-item {
  padding: 15px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  margin-bottom: 10px;
  border: 1px solid rgba(255, 183, 197, 0.2);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state i {
  font-size: 64px;
  background: linear-gradient(135deg, #FFB7C5, #FF9F43, #A3E635);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
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
  border: 2px solid rgba(255, 183, 197, 0.3);
}

.comment-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(255, 183, 197, 0.3);
  border-color: #FFB7C5;
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
  box-shadow: 0 4px 12px rgba(255, 183, 197, 0.4);
}

/* Clickable Username */
.username.clickable {
  cursor: pointer;
  transition: all 0.3s;
}

.username.clickable:hover {
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

/* Image Preview Container */
.image-preview-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
  padding: 10px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 10px;
  border: 2px dashed rgba(255, 183, 197, 0.3);
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
  border: 2px solid rgba(255, 183, 197, 0.4);
}

.preview-thumbnail:hover {
  transform: scale(1.05);
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
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
  border: 2px solid white;
  box-shadow: 0 2px 6px rgba(255, 159, 67, 0.4);
}

.remove-image:hover {
  background: linear-gradient(135deg, #FFB7C5, #FF9F43);
  transform: scale(1.1);
  box-shadow: 0 3px 8px rgba(255, 159, 67, 0.5);
}

/* Image Preview Dialog */
.image-preview-dialog {
  background: rgba(0, 0, 0, 0.9);
}

.image-preview-dialog .el-dialog__header {
  display: none;
}

.image-preview-dialog .el-dialog__body {
  padding: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 420px;
}

.image-preview-dialog .el-dialog__body img {
  max-width: 100%;
  max-height: 70vh;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.preview-image {
  width: 400px;
  height: 400px;
  object-fit: contain;
  display: block;
  margin: 0 auto;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}
</style>