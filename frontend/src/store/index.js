import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'
import blog from './modules/blog'
import category from './modules/category'
import tag from './modules/tag'
import friend from './modules/friend'
import message from './modules/message'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    user,
    blog,
    category,
    tag,
    friend,
    message
  },
  strict: process.env.NODE_ENV !== 'production'
})