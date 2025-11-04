import request from '@/utils/request'

/**
 * 发布评论
 */
export function addComment(data) {
  return request({
    url: '/comment/add',
    method: 'post',
    data
  })
}

/**
 * 获取文章评论列表
 */
export function getCommentList(blogId) {
  return request({
    url: `/comment/list/${blogId}`,
    method: 'get'
  })
}

/**
 * 删除评论
 */
export function deleteComment(id) {
  return request({
    url: `/comment/${id}`,
    method: 'delete'
  })
}

/**
 * 置顶/取消置顶评论
 */
export function pinComment(id, isPinned) {
  return request({
    url: `/comment/pin/${id}`,
    method: 'put',
    params: { isPinned }
  })
}
