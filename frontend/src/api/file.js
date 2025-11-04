import request from '@/utils/request'

/**
 * 上传图片
 */
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/file/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传头像
 */
export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/file/upload/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传文章封面
 */
export function uploadCover(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/file/upload/cover',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传文章内容图片
 */
export function uploadContent(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/file/upload/content',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
