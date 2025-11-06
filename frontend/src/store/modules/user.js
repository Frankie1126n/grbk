import { login, register, getCurrentUser, getUserList } from '@/api/user'

const state = {
  token: localStorage.getItem('token') || '',
  userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
  isLogin: !!localStorage.getItem('token')
}

const getters = {
  token: state => state.token,
  userInfo: state => state.userInfo,
  isLogin: state => state.isLogin,
  username: state => state.userInfo.username || ''
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
    state.isLogin = !!token
    if (token) {
      localStorage.setItem('token', token)
    } else {
      localStorage.removeItem('token')
    }
  },
  
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
  },
  
  LOGOUT(state) {
    state.token = ''
    state.userInfo = {}
    state.isLogin = false
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }
}

const actions = {
  // 用户登录
  async login({ commit }, loginData) {
    try {
      console.log('Making login API call with data:', loginData)
      const response = await login(loginData)
      console.log('Login API response:', response)
      // response.data is the entire data object from backend
      const { token, ...userInfo } = response.data
      
      commit('SET_TOKEN', token)
      commit('SET_USER_INFO', userInfo)
      
      return response
    } catch (error) {
      console.error('Login API error:', error)
      // Better error handling
      if (error.response && error.response.data) {
        return Promise.reject(new Error(error.response.data.message || '登录失败'))
      }
      return Promise.reject(error)
    }
  },
  
  // 用户注册
  async register({ commit }, registerData) {
    try {
      const response = await register(registerData)
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },
  
  // 获取当前用户信息
  async getCurrentUser({ commit }) {
    try {
      const response = await getCurrentUser()
      commit('SET_USER_INFO', response.data)
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },
  
  // 获取用户列表
  async getUserList({ commit }, params) {
    try {
      const response = await getUserList(params)
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },
  
  // 退出登录
  logout({ commit }) {
    commit('LOGOUT')
  }
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}