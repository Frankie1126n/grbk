import * as friendApi from '@/api/friend'

const state = {
  friendList: [],
  friendRequests: [],
  sentFriendRequests: [],
  pendingFriendRequestCount: 0,
  friendListPagination: {
    current: 1,
    size: 10,
    total: 0
  },
  friendRequestsPagination: {
    current: 1,
    size: 10,
    total: 0
  },
  sentFriendRequestsPagination: {
    current: 1,
    size: 10,
    total: 0
  }
}

const mutations = {
  SET_FRIEND_LIST(state, { records, total, current, size }) {
    state.friendList = records
    state.friendListPagination = { current, size, total }
  },
  
  SET_FRIEND_REQUESTS(state, { records, total, current, size }) {
    state.friendRequests = records
    state.friendRequestsPagination = { current, size, total }
  },
  
  SET_SENT_FRIEND_REQUESTS(state, { records, total, current, size }) {
    state.sentFriendRequests = records
    state.sentFriendRequestsPagination = { current, size, total }
  },
  
  SET_PENDING_FRIEND_REQUEST_COUNT(state, count) {
    state.pendingFriendRequestCount = count
  },
  
  ADD_FRIEND_REQUEST(state, request) {
    // 检查是否已存在
    const exists = state.friendRequests.some(r => r.id === request.id)
    if (!exists) {
      state.friendRequests.unshift(request)
    }
  },
  
  REMOVE_FRIEND(state, friendId) {
    state.friendList = state.friendList.filter(friend => friend.id !== friendId)
  },
  
  REMOVE_FRIEND_REQUEST(state, requestId) {
    state.friendRequests = state.friendRequests.filter(request => request.id !== requestId)
  }
}

const actions = {
  // 发送好友申请
  async sendFriendRequest({ commit }, { receiverId, message }) {
    const response = await friendApi.sendFriendRequest(receiverId, message)
    return response
  },
  
  // 处理好友申请
  async handleFriendRequest({ commit }, { requestId, accept }) {
    const response = await friendApi.handleFriendRequest(requestId, accept)
    return response
  },
  
  // 获取好友列表
  async getFriendList({ commit }, { current = 1, size = 10 } = {}) {
    const response = await friendApi.getFriendList(current, size)
    commit('SET_FRIEND_LIST', response.data)
    return response
  },
  
  // 获取收到的好友申请列表
  async getReceivedFriendRequests({ commit }, { current = 1, size = 10 } = {}) {
    const response = await friendApi.getReceivedFriendRequests(current, size)
    commit('SET_FRIEND_REQUESTS', response.data)
    return response
  },
  
  // 获取发送的好友申请列表
  async getSentFriendRequests({ commit }, { current = 1, size = 10 } = {}) {
    const response = await friendApi.getSentFriendRequests(current, size)
    commit('SET_SENT_FRIEND_REQUESTS', response.data)
    return response
  },
  
  // 删除好友
  async deleteFriend({ commit }, friendId) {
    const response = await friendApi.deleteFriend(friendId)
    if (response.code === 200) {
      commit('REMOVE_FRIEND', friendId)
    }
    return response
  },
  
  // 获取未处理的好友申请数量
  async getPendingFriendRequestCount({ commit }) {
    const response = await friendApi.getPendingFriendRequestCount()
    commit('SET_PENDING_FRIEND_REQUEST_COUNT', response.data)
    return response
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}