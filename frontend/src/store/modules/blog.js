import { getBlogList, getBlogById } from '@/api/blog'

const state = {
  blogList: [],
  currentPage: 1,
  pageSize: 10,
  total: 0,
  currentBlog: null,
  searchParams: {
    title: '',
    categoryId: null,
    tagId: null
  }
}

const getters = {
  blogList: state => state.blogList,
  currentPage: state => state.currentPage,
  pageSize: state => state.pageSize,
  total: state => state.total,
  currentBlog: state => state.currentBlog,
  searchParams: state => state.searchParams
}

const mutations = {
  SET_BLOG_LIST(state, data) {
    state.blogList = data.records || []
    state.total = data.total || 0
    state.currentPage = data.current || 1
  },
  
  SET_CURRENT_PAGE(state, page) {
    state.currentPage = page
  },
  
  SET_PAGE_SIZE(state, size) {
    state.pageSize = size
  },
  
  SET_CURRENT_BLOG(state, blog) {
    state.currentBlog = blog
  },
  
  SET_SEARCH_PARAMS(state, params) {
    state.searchParams = { ...state.searchParams, ...params }
  },
  
  RESET_SEARCH_PARAMS(state) {
    state.searchParams = {
      title: '',
      categoryId: null,
      tagId: null
    }
  }
}

const actions = {
  // 获取博客列表
  async getBlogList({ commit, state }, params = {}) {
    try {
      const requestParams = {
        current: params.current || state.currentPage,
        size: params.size || state.pageSize,
        title: params.title !== undefined ? params.title : state.searchParams.title,
        categoryId: params.categoryId !== undefined ? params.categoryId : state.searchParams.categoryId,
        tagId: params.tagId !== undefined ? params.tagId : state.searchParams.tagId
      }
      
      const response = await getBlogList(requestParams)
      commit('SET_BLOG_LIST', response.data)
      
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },
  
  // 获取博客详情
  async getBlogById({ commit }, id) {
    try {
      const response = await getBlogById(id)
      commit('SET_CURRENT_BLOG', response.data)
      
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  },
  
  // 设置搜索参数
  setSearchParams({ commit }, params) {
    commit('SET_SEARCH_PARAMS', params)
  },
  
  // 重置搜索参数
  resetSearchParams({ commit }) {
    commit('RESET_SEARCH_PARAMS')
  }
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}
