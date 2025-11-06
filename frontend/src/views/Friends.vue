<template>
  <div class="friends-container">
    <!-- Header -->
    <Header />
    
    <!-- Main Content -->
    <div class="content-wrapper">
      <div class="container">
        <el-card class="friends-card">
          <div slot="header" class="card-header">
            <span class="card-title">好友管理</span>
            <div class="header-actions">
              <el-button type="primary" size="small" @click="showAddFriendDialog">
                <i class="el-icon-plus"></i> 添加好友
              </el-button>
            </div>
          </div>
          
          <el-tabs v-model="activeTab" @tab-click="handleTabClick">
            <el-tab-pane label="好友列表" name="friends">
              <div v-if="friendList.length === 0" class="empty-state">
                <i class="el-icon-user-solid empty-icon"></i>
                <p>暂无好友</p>
              </div>
              <div v-else class="friends-grid">
                <el-card 
                  v-for="friend in friendList" 
                  :key="friend.id" 
                  class="friend-card"
                  @click.native="goToChat(friend)"
                >
                  <div class="friend-info">
                    <CroppedAvatar 
                      :src="friend.avatarUrl || defaultAvatar" 
                      :size="60" 
                      :border-width="'3px'"
                      @click.native.stop="goToProfile(friend.id)"
                    />
                    <div class="friend-details">
                      <h3 class="friend-name" @click.stop="goToProfile(friend.id)">{{ friend.username }}</h3>
                      <p class="friend-role" :class="friend.role === 'admin' ? 'admin-role' : 'user-role'">
                        {{ friend.role === 'admin' ? '管理员' : '用户' }}
                      </p>
                    </div>
                  </div>
                  <div class="friend-actions">
                    <el-button 
                      v-if="friend.role !== 'admin'" 
                      type="danger" 
                      size="mini" 
                      @click.stop="removeFriend(friend.id)"
                    >
                      删除好友
                    </el-button>
                    <el-button type="primary" size="mini" @click.stop="goToChat(friend)">
                      发消息
                    </el-button>
                  </div>
                </el-card>
              </div>
              
              <!-- Pagination -->
              <div class="pagination-container" v-if="friendList.length > 0">
                <el-pagination
                  @current-change="handleFriendPageChange"
                  :current-page="friendListPagination.current"
                  :page-size="friendListPagination.size"
                  :total="friendListPagination.total"
                  layout="prev, pager, next, jumper"
                  background
                />
              </div>
            </el-tab-pane>
            
            <el-tab-pane label="好友申请" name="requests">
              <el-tabs v-model="requestTab">
                <el-tab-pane label="收到的申请" name="received">
                  <div v-if="friendRequests.length === 0" class="empty-state">
                    <i class="el-icon-message empty-icon"></i>
                    <p>暂无好友申请</p>
                  </div>
                  <div v-else class="requests-list">
                    <el-card 
                      v-for="request in friendRequests" 
                      :key="request.id" 
                      class="request-card"
                    >
                      <div class="request-info">
                        <CroppedAvatar 
                          :src="request.senderAvatarUrl || defaultAvatar" 
                          :size="50" 
                          :border-width="'2px'"
                        />
                        <div class="request-details">
                          <h3 class="sender-name">{{ request.senderUsername }}</h3>
                          <p class="request-message" v-if="request.message">
                            申请消息: {{ request.message }}
                          </p>
                          <p class="request-time">
                            {{ request.createTime | formatDate }}
                          </p>
                        </div>
                      </div>
                      <div class="request-actions">
                        <el-button 
                          type="primary" 
                          size="mini" 
                          @click="processFriendRequest(request.id, true)"
                        >
                          接受
                        </el-button>
                        <el-button 
                          type="danger" 
                          size="mini" 
                          @click="processFriendRequest(request.id, false)"
                        >
                          拒绝
                        </el-button>
                      </div>
                    </el-card>
                  </div>
                  
                  <!-- Pagination -->
                  <div class="pagination-container" v-if="friendRequests.length > 0">
                    <el-pagination
                      @current-change="handleReceivedRequestPageChange"
                      :current-page="friendRequestsPagination.current"
                      :page-size="friendRequestsPagination.size"
                      :total="friendRequestsPagination.total"
                      layout="prev, pager, next, jumper"
                      background
                    />
                  </div>
                </el-tab-pane>
                
                <el-tab-pane label="发送的申请" name="sent">
                  <div v-if="sentFriendRequests.length === 0" class="empty-state">
                    <i class="el-icon-message empty-icon"></i>
                    <p>暂无发送的好友申请</p>
                  </div>
                  <div v-else class="requests-list">
                    <el-card 
                      v-for="request in sentFriendRequests" 
                      :key="request.id" 
                      class="request-card sent-request"
                    >
                      <div class="request-info">
                        <CroppedAvatar 
                          :src="request.senderAvatarUrl || defaultAvatar" 
                          :size="50" 
                          :border-width="'2px'"
                        />
                        <div class="request-details">
                          <h3 class="sender-name">{{ request.senderUsername }}</h3>
                          <p class="request-status">
                            状态: 
                            <span v-if="request.status === 0" class="status-pending">待处理</span>
                            <span v-else-if="request.status === 1" class="status-accepted">已接受</span>
                            <span v-else class="status-rejected">已拒绝</span>
                          </p>
                          <p class="request-message" v-if="request.message">
                            申请消息: {{ request.message }}
                          </p>
                          <p class="request-time">
                            {{ request.createTime | formatDate }}
                          </p>
                        </div>
                      </div>
                    </el-card>
                  </div>
                  
                  <!-- Pagination -->
                  <div class="pagination-container" v-if="sentFriendRequests.length > 0">
                    <el-pagination
                      @current-change="handleSentRequestPageChange"
                      :current-page="sentFriendRequestsPagination.current"
                      :page-size="sentFriendRequestsPagination.size"
                      :total="sentFriendRequestsPagination.total"
                      layout="prev, pager, next, jumper"
                      background
                    />
                  </div>
                </el-tab-pane>
              </el-tabs>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </div>
    </div>
    
    <!-- Footer -->
    <Footer />
    
    <!-- Add Friend Dialog -->
    <el-dialog
      title="添加好友"
      :visible.sync="addFriendDialogVisible"
      width="500px"
      :before-close="handleAddFriendDialogClose"
    >
      <el-form :model="addFriendForm" ref="addFriendForm" :rules="addFriendRules">
        <el-form-item label="搜索用户" prop="searchTerm">
          <el-input 
            v-model="addFriendForm.searchTerm" 
            placeholder="请输入用户名或邮箱搜索用户"
            @keyup.enter.native="searchUsers"
          >
            <el-button slot="append" icon="el-icon-search" @click="searchUsers"></el-button>
          </el-input>
        </el-form-item>
        
        <!-- 搜索结果 -->
        <div v-if="searchResults.length > 0" class="search-results">
          <div class="search-results-title">搜索结果：</div>
          <el-card 
            v-for="user in searchResults" 
            :key="user.id" 
            class="search-result-item"
            @click.native="selectUserForFriendRequest(user)"
          >
            <div class="user-info">
              <CroppedAvatar 
                :src="user.avatarUrl || defaultAvatar" 
                :size="40" 
                :border-width="'2px'"
              />
              <div class="user-details">
                <h4 class="user-name">{{ user.username }}</h4>
                <p class="user-email">{{ user.email }}</p>
              </div>
            </div>
          </el-card>
        </div>
        
        <div v-if="selectedUserForRequest" class="selected-user">
          <div class="selected-user-title">选择的用户：</div>
          <el-card class="selected-user-card">
            <div class="user-info">
              <CroppedAvatar 
                :src="selectedUserForRequest.avatarUrl || defaultAvatar" 
                :size="40" 
                :border-width="'2px'"
              />
              <div class="user-details">
                <h4 class="user-name">{{ selectedUserForRequest.username }}</h4>
                <p class="user-email">{{ selectedUserForRequest.email }}</p>
              </div>
              <el-button type="danger" icon="el-icon-close" circle size="mini" @click="clearSelectedUser"></el-button>
            </div>
          </el-card>
        </div>
        
        <el-form-item label="申请消息" prop="message">
          <el-input 
            v-model="addFriendForm.message" 
            type="textarea"
            placeholder="请输入申请消息（可选）"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addFriendDialogVisible = false">取 消</el-button>
        <el-button 
          type="primary" 
          @click="submitAddFriend" 
          :disabled="!selectedUserForRequest"
        >
          发送申请
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapActions, mapGetters } from 'vuex'
import Header from '@/components/Header'
import Footer from '@/components/Footer'
import CroppedAvatar from '@/components/CroppedAvatar'
import { getConfig } from '@/utils/config'

export default {
  name: 'Friends',
  components: {
    Header,
    Footer,
    CroppedAvatar
  },
  data() {
    return {
      activeTab: 'friends',
      requestTab: 'received',
      addFriendDialogVisible: false,
      addFriendForm: {
        searchTerm: '',
        message: ''
      },
      addFriendRules: {
        searchTerm: [
          { required: true, message: '请输入用户名或邮箱搜索用户', trigger: 'blur' }
        ]
      },
      searchResults: [],
      selectedUserForRequest: null,
      defaultAvatar: getConfig().DEFAULT_AVATAR
    }
  },
  computed: {
    ...mapState('friend', [
      'friendList',
      'friendRequests',
      'sentFriendRequests',
      'friendListPagination',
      'friendRequestsPagination',
      'sentFriendRequestsPagination'
    ]),
    ...mapGetters('user', ['isLogin'])
  },
  created() {
    // 只有在用户已登录时才初始化数据
    if (this.isLogin) {
      this.initData()
    }
  },
  methods: {
    ...mapActions('friend', [
      'getFriendList',
      'getReceivedFriendRequests',
      'getSentFriendRequests',
      'sendFriendRequest',
      'handleFriendRequest',
      'deleteFriend'
    ]),
    ...mapActions('user', ['getUserList']),
    
    async initData() {
      try {
        await Promise.all([
          this.getFriendList(),
          this.getReceivedFriendRequests()
        ])
      } catch (error) {
        console.error('初始化好友数据失败:', error)
        this.$message.error('加载数据失败')
      }
    },
    
    handleTabClick(tab) {
      if (tab.name === 'requests' && this.requestTab === 'received') {
        this.getReceivedFriendRequests()
      }
    },
    
    handleFriendPageChange(page) {
      this.getFriendList({ current: page })
    },
    
    handleReceivedRequestPageChange(page) {
      this.getReceivedFriendRequests({ current: page })
    },
    
    handleSentRequestPageChange(page) {
      this.getSentFriendRequests({ current: page })
    },
    
    showAddFriendDialog() {
      this.addFriendDialogVisible = true
      this.$nextTick(() => {
        this.$refs.addFriendForm.resetFields()
        this.searchResults = []
        this.selectedUserForRequest = null
        this.addFriendForm.searchTerm = ''
        this.addFriendForm.message = ''
      })
    },
    
    handleAddFriendDialogClose(done) {
      this.$refs.addFriendForm.resetFields()
      this.searchResults = []
      this.selectedUserForRequest = null
      done()
    },
    
    async searchUsers() {
      if (!this.addFriendForm.searchTerm.trim()) {
        this.$message.warning('请输入搜索关键词')
        return
      }
      
      try {
        // 调用用户搜索API（这里简化处理，实际应该有一个搜索用户的API）
        const response = await this.getUserList({ 
          current: 1, 
          size: 10, 
          username: this.addFriendForm.searchTerm 
        })
        
        if (response && response.data && response.data.records) {
          // 过滤掉自己和已有的好友
          const currentUser = this.$store.state.user.userInfo
          const friendIds = this.friendList.map(friend => friend.id)
          
          this.searchResults = response.data.records.filter(user => 
            user.id !== currentUser.id && !friendIds.includes(user.id)
          )
          
          if (this.searchResults.length === 0) {
            this.$message.info('没有找到符合条件的用户')
          }
        }
      } catch (error) {
        console.error('搜索用户失败:', error)
        this.$message.error('搜索用户失败')
      }
    },
    
    selectUserForFriendRequest(user) {
      this.selectedUserForRequest = user
    },
    
    clearSelectedUser() {
      this.selectedUserForRequest = null
    },
    
    async submitAddFriend() {
      if (!this.selectedUserForRequest) {
        this.$message.warning('请先选择要添加的好友')
        return
      }
      
      try {
        const response = await this.sendFriendRequest({
          receiverId: this.selectedUserForRequest.id,
          message: this.addFriendForm.message
        })
        
        if (response.code === 200) {
          this.$message.success('好友申请发送成功')
          this.addFriendDialogVisible = false
          // 刷新发送的申请列表
          this.getSentFriendRequests()
        } else {
          this.$message.error(response.message || '发送失败')
        }
      } catch (error) {
        console.error('发送好友申请失败:', error)
        this.$message.error('发送好友申请失败')
      }
    },
    
    async processFriendRequest(requestId, accept) {
      try {
        const response = await this.handleFriendRequest({ requestId, accept })
        if (response.code === 200) {
          this.$message.success(accept ? '已接受好友申请' : '已拒绝好友申请')
          
          // 立即从列表中移除已处理的申请
          this.$store.commit('friend/REMOVE_FRIEND_REQUEST', requestId)
          
          // 刷新数据
          await Promise.all([
            this.getReceivedFriendRequests(),
            this.getFriendList()
          ])
        } else {
          this.$message.error(response.message || '操作失败')
        }
      } catch (error) {
        console.error('处理好友申请失败:', error)
        this.$message.error('处理好友申请失败')
      }
    },
    
    async removeFriend(friendId) {
      try {
        await this.$confirm('确定要删除该好友吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const response = await this.deleteFriend(friendId)
        if (response.code === 200) {
          this.$message.success('好友删除成功')
          // 刷新好友列表
          this.getFriendList()
        } else {
          this.$message.error(response.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除好友失败:', error)
          this.$message.error('删除好友失败')
        }
      }
    },
    
    goToChat(friend) {
      this.$router.push(`/messages/${friend.id}`)
    },
    
    goToProfile(userId) {
      this.$router.push(`/user/${userId}`)
    }
  },
  
  filters: {
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.friends-container {
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

.friends-card {
  border-radius: 12px;
  border: 2px solid rgba(255, 183, 197, 0.3);
  background: rgba(255, 251, 235, 0.85);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(255, 183, 197, 0.2);
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

.header-actions {
  display: flex;
  gap: 10px;
}

.friends-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.friend-card {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid rgba(255, 183, 197, 0.2);
  background: rgba(255, 255, 255, 0.7);
  border-radius: 12px;
}

.friend-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(255, 183, 197, 0.3);
  border-color: rgba(255, 183, 197, 0.4);
}

.friend-info {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
}

.friend-details {
  flex: 1;
}

.friend-name {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: 600;
  color: #4a4a4a;
}

.friend-role {
  margin: 0;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
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

.friend-actions {
  display: flex;
  justify-content: flex-end;
  padding: 0 15px 15px;
  gap: 10px;
}

.requests-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-top: 20px;
}

.request-card {
  border: 2px solid rgba(255, 183, 197, 0.2);
  background: rgba(255, 255, 255, 0.7);
  border-radius: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
}

.request-card.sent-request {
  background: rgba(248, 203, 166, 0.1);
}

.request-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.request-details {
  flex: 1;
}

.sender-name {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: 600;
  color: #4a4a4a;
}

.request-message {
  margin: 5px 0;
  font-size: 14px;
  color: #666;
}

.request-status {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #666;
}

.request-time {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.status-pending {
  color: #FF9F43;
  font-weight: 600;
}

.status-accepted {
  color: #A3E635;
  font-weight: 600;
}

.status-rejected {
  color: #f56c6c;
  font-weight: 600;
}

.request-actions {
  display: flex;
  gap: 10px;
}

.empty-state {
  text-align: center;
  padding: 50px 20px;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 15px;
  color: #FFB7C5;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

/* Add Friend Dialog Styles */
.search-results {
  margin-bottom: 20px;
}

.search-results-title {
  font-weight: 600;
  margin-bottom: 10px;
  color: #FF9F43;
}

.search-result-item {
  cursor: pointer;
  margin-bottom: 10px;
  transition: all 0.2s ease;
  border: 1px solid rgba(255, 183, 197, 0.2);
}

.search-result-item:hover {
  border-color: #FF9F43;
  background: rgba(255, 183, 197, 0.05);
}

.selected-user {
  margin-bottom: 20px;
}

.selected-user-title {
  font-weight: 600;
  margin-bottom: 10px;
  color: #FF9F43;
}

.selected-user-card {
  border: 2px solid #FF9F43;
  background: rgba(255, 183, 197, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
  flex: 1;
  min-width: 0;
}

.user-name {
  margin: 0 0 3px 0;
  font-size: 14px;
  font-weight: 600;
  color: #4a4a4a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-email {
  margin: 0;
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .friends-grid {
    grid-template-columns: 1fr;
  }
  
  .request-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .request-actions {
    align-self: flex-end;
  }
}
</style>