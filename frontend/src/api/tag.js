import request from '@/utils/request'

/**
 * 获取所有标签列表
 */
export function getTagList() {
  return request({
    url: '/tag/list',
    method: 'get'
  })
}

/**
 * 新增标签
 */
export function addTag(name) {
  return request({
    url: '/tag/add',
    method: 'post',
    params: { name }
  })
}

/**
 * 更新标签
 */
export function updateTag(id, name) {
  return request({
    url: `/tag/${id}`,
    method: 'put',
    params: { name }
  })
}

/**
 * 删除标签
 */
export function deleteTag(id) {
  return request({
    url: `/tag/${id}`,
    method: 'delete'
  })
}
