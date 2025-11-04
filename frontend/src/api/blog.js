import request from '@/utils/request'

/**
 * 分页查询博客列表
 */
export function getBlogList(params) {
  return request({
    url: '/blog/list',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询博客详情
 */
export function getBlogById(id) {
  return request({
    url: `/blog/${id}`,
    method: 'get'
  })
}

/**
 * 保存博客（新增或更新）
 */
export function saveBlog(data) {
  return request({
    url: '/blog/save',
    method: 'post',
    data
  })
}

/**
 * 删除博客
 */
export function deleteBlog(id) {
  return request({
    url: `/blog/${id}`,
    method: 'delete'
  })
}

/**
 * 查询已删除的博客列表
 */
export function getDeletedBlogList(params) {
  return request({
    url: '/blog/deleted/list',
    method: 'get',
    params
  })
}

/**
 * 恢复已删除的博客
 */
export function restoreBlog(id) {
  return request({
    url: `/blog/restore/${id}`,
    method: 'put'
  })
}

/**
 * 彻底删除博客
 */
export function permanentDeleteBlog(id) {
  return request({
    url: `/blog/permanent/${id}`,
    method: 'delete'
  })
}

/**
 * 点赞博客
 */
export function likeBlog(blogId) {
  return request({
    url: `/blog/${blogId}/like`,
    method: 'post'
  })
}

/**
 * 取消点赞
 */
export function unlikeBlog(blogId) {
  return request({
    url: `/blog/${blogId}/like`,
    method: 'delete'
  })
}

/**
 * 检查点赞状态
 */
export function checkLikeStatus(blogId) {
  return request({
    url: `/blog/${blogId}/like/status`,
    method: 'get'
  })
}

/**
 * 收藏博客
 */
export function favoriteBlog(blogId) {
  return request({
    url: `/blog/${blogId}/favorite`,
    method: 'post'
  })
}

/**
 * 取消收藏
 */
export function unfavoriteBlog(blogId) {
  return request({
    url: `/blog/${blogId}/favorite`,
    method: 'delete'
  })
}

/**
 * 检查收藏状态
 */
export function checkFavoriteStatus(blogId) {
  return request({
    url: `/blog/${blogId}/favorite/status`,
    method: 'get'
  })
}

/**
 * 获取用户的收藏列表
 */
export function getUserFavorites(params) {
  return request({
    url: '/blog/favorites',
    method: 'get',
    params
  })
}
