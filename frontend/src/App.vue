<template>
  <div id="app" :style="backgroundStyle">
    <transition name="fade" mode="out-in">
      <router-view/>
    </transition>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'App',
  computed: {
    ...mapState('user', ['userInfo']),
    backgroundStyle() {
      if (this.userInfo && this.userInfo.backgroundImageUrl) {
        return {
          backgroundImage: `url(${this.userInfo.backgroundImageUrl})`
        }
      }
      return {}
    }
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  min-height: 100vh;
  position: relative;
  z-index: 0;
  /* Responsive background styling */
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
  background-attachment: fixed;
}

/* 路由切换动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}

/* Mobile-specific optimizations */
@media (max-width: 768px) {
  #app {
    background-attachment: scroll; /* Better performance on mobile */
    background-size: cover;
  }
}

/* Small mobile devices */
@media (max-width: 480px) {
  #app {
    background-size: cover;
    background-position: center center;
  }
}

/* Tablet devices */
@media (min-width: 769px) and (max-width: 1024px) {
  #app {
    background-size: cover;
    background-position: center center;
  }
}

/* Large screens */
@media (min-width: 1025px) {
  #app {
    background-size: cover;
    background-position: center center;
    background-attachment: fixed;
  }
}
</style>