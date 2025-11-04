import { getTagList } from '@/api/tag'

const state = {
  tagList: []
}

const getters = {
  tagList: state => state.tagList
}

const mutations = {
  SET_TAG_LIST(state, list) {
    state.tagList = list
  }
}

const actions = {
  // 获取标签列表
  async getTagList({ commit }) {
    try {
      const response = await getTagList()
      commit('SET_TAG_LIST', response.data)
      
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
