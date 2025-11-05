<template>
  <div class="cropped-avatar" :style="avatarStyle" @click="$emit('click')">
    <img 
      v-if="src" 
      :src="src" 
      :alt="alt" 
      :style="imageStyle"
      @load="onImageLoad"
      @error="onImageError"
    />
    <div v-else class="placeholder">
      <i :class="icon"></i>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CroppedAvatar',
  props: {
    src: {
      type: String,
      default: ''
    },
    size: {
      type: [Number, String],
      default: 40
    },
    alt: {
      type: String,
      default: 'Avatar'
    },
    icon: {
      type: String,
      default: 'el-icon-user'
    },
    borderColor: {
      type: String,
      default: '#FFB7C5'
    },
    borderWidth: {
      type: String,
      default: '2px'
    }
  },
  data() {
    return {
      imageLoaded: false
    }
  },
  computed: {
    avatarStyle() {
      const size = typeof this.size === 'number' ? `${this.size}px` : this.size
      return {
        width: size,
        height: size,
        borderRadius: '50%',
        border: `${this.borderWidth} solid ${this.borderColor}`,
        boxShadow: '0 2px 8px rgba(255, 183, 197, 0.3)',
        overflow: 'hidden',
        display: 'inline-block',
        position: 'relative'
      }
    },
    imageStyle() {
      return {
        width: '100%',
        height: '100%',
        objectFit: 'cover',
        objectPosition: 'center',
        display: this.imageLoaded ? 'block' : 'none'
      }
    }
  },
  methods: {
    onImageLoad() {
      this.imageLoaded = true
    },
    onImageError() {
      this.imageLoaded = false
      this.$emit('error')
    }
  }
}
</script>

<style scoped>
.cropped-avatar {
  transition: transform 0.3s ease;
}

.cropped-avatar:hover {
  transform: scale(1.05);
}

.placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 251, 235, 0.9);
  color: #FFB7C5;
}

.placeholder i {
  font-size: calc(v-bind(size) / 2);
}
</style>