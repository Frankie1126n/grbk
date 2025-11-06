import * as messageApi from '@/api/message'

const state = {
  chatHistory: [],
  chatHistoryPagination: {
    current: 1,
    size: 10,
    total: 0
  },
  unreadMessages: [],
  unreadMessageCount: 0
}

const mutations = {
  SET_CHAT_HISTORY(state, { records, total, current, size }) {
    state.chatHistory = records
    state.chatHistoryPagination = { current, size, total }
  },
  
  CLEAR_CHAT_HISTORY(state) {
    state.chatHistory = []
    state.chatHistoryPagination = {
      current: 1,
      size: 10,
      total: 0
    }
  },
  
  SET_UNREAD_MESSAGES(state, messages) {
    state.unreadMessages = messages
  },
  
  SET_UNREAD_MESSAGE_COUNT(state, count) {
    state.unreadMessageCount = count
  },
  
  REMOVE_UNREAD_MESSAGE(state, senderId) {
    state.unreadMessages = state.unreadMessages.filter(msg => msg.senderId !== senderId)
  }
}

const actions = {
  // 发送私信
  async sendMessage({ commit }, { receiverId, content }) {
    try {
      const response = await messageApi.sendMessage(receiverId, content)
      return response || { code: 500, message: '网络错误' }
    } catch (error) {
      console.error('发送消息API错误:', error)
      return { code: 500, message: error.message || '发送失败' }
    }
  },
  
  // 获取聊天记录
  async getChatHistory({ commit }, { friendId, current = 1, size = 10 } = {}) {
    commit('CLEAR_CHAT_HISTORY')
    try {
      const response = await messageApi.getChatHistory(friendId, current, size)
      if (response && response.code === 200) {
        commit('SET_CHAT_HISTORY', response.data)
      }
      return response || { code: 500, message: '网络错误' }
    } catch (error) {
      console.error('获取聊天记录API错误:', error)
      return { code: 500, message: error.message || '获取聊天记录失败' }
    }
  },
  
  // 获取未读消息列表
  async getUnreadMessages({ commit }) {
    try {
      const response = await messageApi.getUnreadMessages()
      if (response && response.code === 200) {
        commit('SET_UNREAD_MESSAGES', response.data)
      }
      return response || { code: 500, message: '网络错误' }
    } catch (error) {
      console.error('获取未读消息API错误:', error)
      return { code: 500, message: error.message || '获取未读消息失败' }
    }
  },
  
  // 标记消息为已读
  async markAsRead({ commit }, senderId) {
    try {
      const response = await messageApi.markAsRead(senderId)
      if (response && response.code === 200) {
        commit('REMOVE_UNREAD_MESSAGE', senderId)
      }
      return response || { code: 500, message: '网络错误' }
    } catch (error) {
      console.error('标记消息为已读API错误:', error)
      return { code: 500, message: error.message || '标记消息为已读失败' }
    }
  },
  
  // 获取未读消息数量
  async getUnreadMessageCount({ commit }) {
    try {
      const response = await messageApi.getUnreadMessageCount()
      if (response && response.code === 200) {
        commit('SET_UNREAD_MESSAGE_COUNT', response.data)
      }
      return response || { code: 500, message: '网络错误' }
    } catch (error) {
      console.error('获取未读消息数量API错误:', error)
      return { code: 500, message: error.message || '获取未读消息数量失败' }
    }
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}