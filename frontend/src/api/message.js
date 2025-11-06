import request from '@/utils/request'

// 发送私信
export function sendMessage(receiverId, content) {
  return request({
    url: '/message/send',
    method: 'post',
    params: { receiverId, content }
  })
}

// 获取聊天记录
export function getChatHistory(friendId, current = 1, size = 10) {
  return request({
    url: `/message/history/${friendId}`,
    method: 'get',
    params: { current, size }
  })
}

// 获取未读消息列表
export function getUnreadMessages() {
  return request({
    url: '/message/unread',
    method: 'get'
  })
}

// 标记消息为已读
export function markAsRead(senderId) {
  return request({
    url: `/message/mark-read/${senderId}`,
    method: 'post'
  })
}

// 获取未读消息数量
export function getUnreadMessageCount() {
  return request({
    url: '/message/unread/count',
    method: 'get'
  })
}