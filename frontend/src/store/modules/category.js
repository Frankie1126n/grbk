import { getCategoryList } from '@/api/category'

const state = {
  categoryList: []
}

const getters = {
  categoryList: state => state.categoryList
}

const mutations = {
  SET_CATEGORY_LIST(state, list) {
    state.categoryList = list
  }
}

const actions = {
  // 获取分类列表
  async getCategoryList({ commit }) {
    try {
      const response = await getCategoryList()
      commit('SET_CATEGORY_LIST', response.data)
      
      return response
    } catch (error) {
      return Promise.reject(error)
    }
  }
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}
