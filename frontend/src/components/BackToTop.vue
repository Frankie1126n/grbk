<template>
  <transition name="fade">
    <div
      v-show="visible"
      class="back-to-top pulse"
      @click="scrollToTop"
    >
      <i class="el-icon-top"></i>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'BackToTop',
  data() {
    return {
      visible: false
    }
  },
  mounted() {
    window.addEventListener('scroll', this.handleScroll)
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.handleScroll)
  },
  methods: {
    handleScroll() {
      this.visible = window.scrollY > 300
    },
    
    scrollToTop() {
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      })
    }
  }
}
</script>

<style scoped>
.back-to-top {
  position: fixed;
  right: 30px;
  bottom: 30px;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
  transition: all 0.3s ease;
  z-index: 999;
}

.back-to-top:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 20px rgba(79, 172, 254, 0.6);
}

.back-to-top i {
  font-size: 24px;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter, .fade-leave-to {
  opacity: 0;
}
</style>
