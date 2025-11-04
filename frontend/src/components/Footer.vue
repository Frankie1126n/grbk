<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <p class="copyright">
          © 2024 个人博客系统 | Powered by Vue2 + Spring Boot
        </p>
        <div class="footer-links">
          <a href="#">关于</a>
          <span>|</span>
          <a href="#">联系</a>
          <span>|</span>
          <a href="#">GitHub</a>
        </div>
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
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(79, 172, 254, 0.2);
  padding: 30px 0;
  margin-top: 40px;
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
  color: #666;
  font-size: 14px;
  margin: 0;
}

.footer-links {
  display: flex;
  gap: 15px;
  align-items: center;
}

.footer-links a {
  color: #4facfe;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.footer-links a:hover {
  color: #00f2fe;
}

.footer-links span {
  color: #ddd;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    text-align: center;
  }
}
</style>
