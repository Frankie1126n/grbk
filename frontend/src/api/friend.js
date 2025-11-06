import request from '@/utils/request'

// 发送好友申请
export function sendFriendRequest(receiverId, message) {
  return request({
    url: '/friend/request',
    method: 'post',
    params: { receiverId, message }
  })
}

// 处理好友申请
export function handleFriendRequest(requestId, accept) {
  return request({
    url: `/friend/request/${requestId}`,
    method: 'post',
    params: { accept }
  })
}

// 获取收到的好友申请列表
export function getReceivedFriendRequests(current = 1, size = 10) {
  return request({
    url: '/friend/requests/received',
    method: 'get',
    params: { current, size }
  })
}

// 获取发送的好友申请列表
export function getSentFriendRequests(current = 1, size = 10) {
  return request({
    url: '/friend/requests/sent',
    method: 'get',
    params: { current, size }
  })
}

// 获取好友列表
export function getFriendList(current = 1, size = 10) {
  return request({
    url: '/friend/list',
    method: 'get',
    params: { current, size }
  })
}

// 删除好友
export function deleteFriend(friendId) {
  return request({
    url: `/friend/${friendId}`,
    method: 'delete'
  })
}

// 获取未处理的好友申请数量
export function getPendingFriendRequestCount() {
  return request({
    url: '/friend/requests/pending/count',
    method: 'get'
  })
}