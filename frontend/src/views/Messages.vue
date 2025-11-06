<template>
  <div class="messages-container">
    <!-- Header -->
    <Header />
    
    <!-- Main Content -->
    <div class="content-wrapper">
      <div class="container">
        <el-card class="messages-card">
          <div slot="header" class="card-header">
            <span class="card-title">私信</span>
            <div class="header-actions">
              <el-button type="primary" size="small" @click="refreshMessages">
                <i class="el-icon-refresh"></i> 刷新
              </el-button>
            </div>
          </div>
          
          <div class="messages-layout">
            <!-- Friends List -->
            <div class="friends-sidebar">
              <div class="friends-search">
                <el-input
                  v-model="friendSearch"
                  placeholder="搜索好友..."
                  prefix-icon="el-icon-search"
                  size="small"
                />
              </div>
              <div class="friends-list">
                <div 
                  v-for="friend in filteredFriends" 
                  :key="friend.id" 
                  class="friend-item"
                  :class="{ active: activeFriend && activeFriend.id === friend.id }"
                  @click="selectFriend(friend)"
                >
                  <CroppedAvatar 
                    :src="friend.avatarUrl || defaultAvatar" 
                    :size="40" 
                    :border-width="'2px'"
                    @click.native.stop="goToProfile(friend.id)"
                  />
                  <div class="friend-info">
                    <h4 class="friend-name">{{ friend.username }}</h4>
                    <p class="friend-role" :class="friend.role === 'admin' ? 'admin-role' : 'user-role'">
                      {{ friend.role === 'admin' ? '管理员' : '用户' }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Chat Area -->
            <div class="chat-area">
              <div v-if="!activeFriend" class="no-chat-selected">
                <i class="el-icon-chat-line-round empty-icon"></i>
                <p>选择一个好友开始聊天</p>
              </div>
              <div v-else class="chat-container">
                <div class="chat-header">
                  <CroppedAvatar 
                    :src="activeFriend.avatarUrl || defaultAvatar" 
                    :size="36" 
                    :border-width="'2px'"
                    @click.native.stop="goToProfile(activeFriend.id)"
                  />
                  <div class="chat-partner-info">
                    <h3 class="partner-name">{{ activeFriend.username }}</h3>
                    <p class="partner-status">在线</p>
                  </div>
                </div>
                
                <div class="chat-messages" ref="chatMessages">
                  <div 
                    v-for="message in chatHistory" 
                    :key="message.id" 
                    class="message"
                    :class="{ 'sent': message.senderId === currentUserId, 'received': message.senderId !== currentUserId }"
                  >
                    <div class="message-content">
                      <div class="message-text">{{ message.content }}</div>
                      <div class="message-time">{{ formatMessageTime(message.createTime) }}</div>
                    </div>
                  </div>
                </div>
                
                <div class="chat-input">
                  <el-input
                    type="textarea"
                    v-model="messageContent"
                    placeholder="输入消息..."
                    :rows="3"
                    @keyup.enter.native="handleSendMessage"
                  />
                  <div class="input-actions">
                    <el-button 
                      type="primary" 
                      @click="handleSendMessage" 
                      :disabled="!messageContent.trim()"
                    >
                      发送
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
    
    <!-- Footer -->
    <Footer />
  </div>
</template>

<script>
import { mapState, mapActions, mapGetters } from 'vuex'
import Header from '@/components/Header'
import Footer from '@/components/Footer'
import CroppedAvatar from '@/components/CroppedAvatar'

export default {
  name: 'Messages',
  components: {
    Header,
    Footer,
    CroppedAvatar
  },
  data() {
    return {
      friendSearch: '',
      activeFriend: null,
      messageContent: '',
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      messageRefreshTimer: null
    }
  },
  computed: {
    ...mapGetters('user', ['userInfo']),
    ...mapState('friend', ['friendList']),
    ...mapState('message', ['chatHistory', 'chatHistoryPagination']),
    
    currentUserId() {
      return this.userInfo ? this.userInfo.id : null
    },
    
    filteredFriends() {
      if (!this.friendSearch) {
        return this.friendList
      }
      return this.friendList.filter(friend => 
        friend.username.toLowerCase().includes(this.friendSearch.toLowerCase())
      )
    }
  },
  created() {
    this.initData()
  },
  mounted() {
    // 开始定时刷新消息
    this.startMessageRefresh()
  },
  beforeDestroy() {
    // 清除定时器
    this.stopMessageRefresh()
  },
  methods: {
    ...mapActions('friend', ['getFriendList']),
    ...mapActions('message', [
      'getChatHistory',
      'sendMessage',
      'markAsRead',
      'getUnreadMessageCount'
    ]),
    
    async initData() {
      try {
        await this.getFriendList()
        
        // 如果URL中有好友ID，直接选中该好友
        const friendId = this.$route.params.friendId
        if (friendId) {
          const friend = this.friendList.find(f => f.id === parseInt(friendId))
          if (friend) {
            this.selectFriend(friend)
          }
        }
      } catch (error) {
        console.error('初始化消息数据失败:', error)
        this.$message.error('加载数据失败')
      }
    },
    
    async selectFriend(friend) {
      this.activeFriend = friend
      this.messageContent = ''
      
      try {
        // 获取聊天记录
        const response = await this.getChatHistory({ friendId: friend.id })
        
        // 检查响应是否存在
        if (!response) {
          throw new Error('网络错误')
        }
        
        if (response.code === 200) {
          // 标记消息为已读 (即使失败也不影响聊天记录加载)
          try {
            await this.markAsRead(friend.id)
          } catch (markError) {
            console.warn('标记消息为已读失败:', markError)
            // 不中断流程，继续执行
          }
          
          // 滚动到最新消息
          this.$nextTick(() => {
            this.scrollToBottom()
          })
        } else {
          throw new Error(response.message || '获取聊天记录失败')
        }
      } catch (error) {
        console.error('加载聊天记录失败:', error)
        this.$message.error('加载聊天记录失败: ' + (error.message || ''))
      }
    },
    
    async handleSendMessage() {
      if (!this.messageContent.trim() || !this.activeFriend) {
        return
      }
      
      try {
        const response = await this.sendMessage({
          receiverId: this.activeFriend.id,
          content: this.messageContent.trim()
        })
        
        // 检查响应是否存在
        if (!response) {
          throw new Error('网络错误')
        }
        
        if (response.code === 200) {
          // 清空输入框
          this.messageContent = ''
          
          // 重新加载聊天记录
          const historyResponse = await this.getChatHistory({ friendId: this.activeFriend.id })
          
          // 检查聊天记录响应
          if (!historyResponse) {
            this.$message.warning('消息发送成功，但刷新聊天记录失败')
            return
          }
          
          if (historyResponse.code === 200) {
            // 滚动到最新消息
            this.$nextTick(() => {
              this.scrollToBottom()
            })
          } else {
            this.$message.warning('消息发送成功，但刷新聊天记录失败: ' + (historyResponse.message || ''))
          }
        } else {
          throw new Error(response.message || '发送失败')
        }
      } catch (error) {
        console.error('发送消息失败:', error)
        this.$message.error('发送消息失败: ' + (error.message || ''))
      }
    },
    
    scrollToBottom() {
      const container = this.$refs.chatMessages
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    },
    
    formatMessageTime(date) {
      if (!date) return ''
      const d = new Date(date)
      return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
    },
    
    async refreshMessages() {
      if (this.activeFriend) {
        try {
          const response = await this.getChatHistory({ friendId: this.activeFriend.id })
          
          // 检查响应是否存在
          if (!response) {
            this.$message.error('网络错误')
            return
          }
          
          if (response.code === 200) {
            this.$message.success('消息已刷新')
          } else {
            throw new Error(response.message || '刷新失败')
          }
        } catch (error) {
          console.error('刷新消息失败:', error)
          this.$message.error('刷新消息失败: ' + (error.message || ''))
        }
      }
    },
    
    startMessageRefresh() {
      // 每1分钟刷新一次消息
      this.messageRefreshTimer = setInterval(() => {
        if (this.activeFriend) {
          this.getChatHistory({ friendId: this.activeFriend.id })
        }
      }, 60000)
    },
    
    stopMessageRefresh() {
      if (this.messageRefreshTimer) {
        clearInterval(this.messageRefreshTimer)
        this.messageRefreshTimer = null
      }
    },
    
    goToProfile(userId) {
      this.$router.push(`/user/${userId}`)
    }
  },
  
  watch: {
    '$route'(to, from) {
      // 监听路由变化，如果URL中有好友ID，选中该好友
      const friendId = to.params.friendId
      if (friendId) {
        const friend = this.friendList.find(f => f.id === parseInt(friendId))
        if (friend) {
          this.selectFriend(friend)
        }
      }
    }
  }
}
</script>

<style scoped>
.messages-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #FFFBEB 0%, #F8CBA6 50%, #FFB7C5 100%);
  position: relative;
  overflow-x: hidden;
}

.content-wrapper {
  flex: 1;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.messages-card {
  border-radius: 12px;
  border: 2px solid rgba(255, 183, 197, 0.3);
  background: rgba(255, 251, 235, 0.85);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(255, 183, 197, 0.2);
  height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #FF9F43;
}

.messages-layout {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.friends-sidebar {
  width: 280px;
  border-right: 1px solid rgba(255, 183, 197, 0.2);
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.5);
}

.friends-search {
  padding: 15px;
  border-bottom: 1px solid rgba(255, 183, 197, 0.1);
}

.friends-list {
  flex: 1;
  overflow-y: auto;
}

.friend-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 15px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 1px solid rgba(255, 183, 197, 0.05);
}

.friend-item:hover {
  background: rgba(255, 183, 197, 0.1);
}

.friend-item.active {
  background: rgba(255, 183, 197, 0.2);
  border-left: 3px solid #FF9F43;
}

.friend-info {
  flex: 1;
  min-width: 0;
}

.friend-name {
  margin: 0 0 3px 0;
  font-size: 14px;
  font-weight: 600;
  color: #4a4a4a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.friend-role {
  margin: 0;
  font-size: 11px;
  padding: 1px 6px;
  border-radius: 8px;
  display: inline-block;
}

.admin-role {
  background: rgba(255, 159, 67, 0.2);
  color: #FF9F43;
}

.user-role {
  background: rgba(163, 230, 53, 0.2);
  color: #A3E635;
}

.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.no-chat-selected {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 15px;
  color: #FFB7C5;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px 20px;
  border-bottom: 1px solid rgba(255, 183, 197, 0.2);
  background: rgba(255, 255, 255, 0.7);
}

.chat-partner-info {
  flex: 1;
}

.partner-name {
  margin: 0 0 3px 0;
  font-size: 16px;
  font-weight: 600;
  color: #4a4a4a;
}

.partner-status {
  margin: 0;
  font-size: 12px;
  color: #A3E635;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  background: rgba(255, 251, 235, 0.3);
}

.message {
  display: flex;
  max-width: 70%;
}

.message.sent {
  align-self: flex-end;
}

.message.received {
  align-self: flex-start;
}

.message-content {
  padding: 10px 15px;
  border-radius: 18px;
  position: relative;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.message.sent .message-content {
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  color: white;
  border-bottom-right-radius: 5px;
}

.message.received .message-content {
  background: white;
  color: #4a4a4a;
  border-bottom-left-radius: 5px;
}

.message-text {
  font-size: 14px;
  line-height: 1.4;
  word-wrap: break-word;
}

.message-time {
  font-size: 11px;
  text-align: right;
  margin-top: 5px;
  opacity: 0.8;
}

.message.sent .message-time {
  color: rgba(255, 255, 255, 0.9);
}

.message.received .message-time {
  color: #999;
}

.chat-input {
  padding: 15px;
  border-top: 1px solid rgba(255, 183, 197, 0.2);
  background: rgba(255, 255, 255, 0.7);
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .messages-layout {
    flex-direction: column;
  }
  
  .friends-sidebar {
    width: 100%;
    height: 200px;
    border-right: none;
    border-bottom: 1px solid rgba(255, 183, 197, 0.2);
  }
  
  .message {
    max-width: 85%;
  }
}
</style>