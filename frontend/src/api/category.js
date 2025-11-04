import request from '@/utils/request'

/**
 * 获取所有分类列表
 */
export function getCategoryList() {
  return request({
    url: '/category/list',
    method: 'get'
  })
}

/**
 * 新增分类
 */
export function addCategory(name) {
  return request({
    url: '/category/add',
    method: 'post',
    params: { name }
  })
}

/**
 * 更新分类
 */
export function updateCategory(id, name) {
  return request({
    url: `/category/${id}`,
    method: 'put',
    params: { name }
  })
}

/**
 * 删除分类
 */
export function deleteCategory(id) {
  return request({
    url: `/category/${id}`,
    method: 'delete'
  })
}
