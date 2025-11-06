// Environment Configuration Utility
export const getConfig = () => {
  return {
    API_BASE_URL: process.env.VUE_APP_API_BASE_URL || '/api',
    DEFAULT_AVATAR: process.env.VUE_APP_DEFAULT_AVATAR || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  }
}