import request from '@/utils/request'

/**
 * 用户登录
 */
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 */
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

/**
 * 获取当前用户信息
 */
export function getCurrentUser() {
  return request({
    url: '/user/current',
    method: 'get'
  })
}

/**
 * 根据ID获取用户公开信息
 */
export function getUserById(userId) {
  return request({
    url: `/user/${userId}`,
    method: 'get'
  })
}

/**
 * 忘记密码 - 发送验证码
 */
export function forgotPassword(email) {
  return request({
    url: '/user/forgot-password',
    method: 'post',
    params: { email }
  })
}

/**
 * 重置密码
 */
export function resetPassword(data) {
  return request({
    url: '/user/reset-password',
    method: 'post',
    params: data
  })
}

/**
 * 更新用户资料
 */
export function updateProfile(data) {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

/**
 * 获取用户列表（管理员）
 */
export function getUserList(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

/**
 * 更新用户角色和状态（管理员）
 */
export function updateUserRoleAndStatus(data) {
  return request({
    url: '/user/management',
    method: 'put',
    data
  })
}
