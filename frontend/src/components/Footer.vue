<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <p class="copyright">
          © 2025 个人博客系统
        </p>
        <div class="footer-links">
          <a href="https://github.com/Frankie1126n/grbk/blob/master/README.md" target="_blank" rel="noopener noreferrer">关于</a>
          <span>|</span>
          <a href="https://github.com/Frankie1126n/grbk" target="_blank" rel="noopener noreferrer" >GitHub</a>
        </div>
      </div>
      <div class="footer-content">
        <p class="copyright">
        联系我:frankie1126n@qq.com
        </p>
      </div>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'Footer'
}
</script>

<style scoped>
.footer {
  background: rgba(255, 251, 235, 0.95);
  backdrop-filter: blur(15px);
  border-top: 2px solid rgba(255, 183, 197, 0.3);
  padding: 30px 0;
  margin-top: 40px;
  position: relative;
}

/* 课本装饰 */
.footer::before {
  content: '';
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  bottom: 10px;
  border: 1px dashed rgba(163, 230, 53, 0.2);
  border-radius: 10px;
  pointer-events: none;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.copyright {
  color: #6b6b6b;
  font-size: 14px;
  margin: 0;
  font-weight: 500;
}

.footer-links {
  display: flex;
  gap: 15px;
  align-items: center;
}

.footer-links a {
  color: #FF9F43;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
  position: relative;
  padding: 5px 10px;
}

/* 小三角旗效果 */
.footer-links a.active::before {
  content: '';
  position: absolute;
  top: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-bottom: 8px solid #FF9F43;
}

/* 从书包里拿出的动效 */
.footer-links a:hover {
  color: #FFB7C5;
  animation: takeOut 0.2s ease;
}

@keyframes takeOut {
  0% { transform: scale(0.8); box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
  50% { transform: scale(1.1); box-shadow: 0 4px 8px rgba(0,0,0,0.2); }
  100% { transform: scale(1); box-shadow: 0 2px 6px rgba(0,0,0,0.15); }
}

.footer-links span {
  color: #dcdcdc;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    text-align: center;
  }
}
</style>
