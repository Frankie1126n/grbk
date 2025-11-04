import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 15000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是200，则判断为错误
    if (res.code !== 200) {
      Message({
        message: res.message || '请求失败',
        type: 'error',
        duration: 3000
      })
      
      // 401: 未登录或登录过期
      if (res.code === 401) {
        Message({
          message: '登录已过期，请重新登录',
          type: 'warning',
          duration: 2000
        })
        // 清除token
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        // 跳转到登录页
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    
    let message = '网络错误，请稍后重试'
    
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = error.response.data.message || '请求参数错误'
          break
        case 401:
          message = '未授权，请登录'
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          router.push('/login')
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求资源不存在'
          break
        case 500:
          message = '服务器错误'
          break
        default:
          message = error.response.data.message || '请求失败'
      }
    }
    
    Message({
      message: message,
      type: 'error',
      duration: 3000
    })
    
    return Promise.reject(error)
  }
)

export default request
