<template>
  <div class="login-container">
    <div class="login-card card">
      <h2 class="gradient-text">个人博客系统</h2>
      <p class="subtitle">欢迎登录</p>

      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input
              v-model="loginForm.username"
              prefix-icon="el-icon-user"
              placeholder="请输入用户名"
              :class="{ 'shake': errors.username }"
              @focus="errors.username = false"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              prefix-icon="el-icon-lock"
              placeholder="请输入密码"
              :class="{ 'shake': errors.password }"
              @focus="errors.password = false"
              @keyup.enter.native="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>

        <div class="links">
          <router-link to="/register" class="link">注册账号</router-link>
          <router-link to="/forgot-password" class="link">忘记密码？</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 50, message: '用户名长度为3-50个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
        ]
      },
      loading: false,
      errors: {
        username: false,
        password: false
      }
    }
  },
  methods: {
    ...mapActions('user', ['login']),

    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true

          this.login(this.loginForm)
              .then(() => {
                this.$message.success('登录成功')

                // 跳转到首页或原页面
                const redirect = this.$route.query.redirect || '/home'
                this.$router.push(redirect)
              })
              .catch(() => {
                this.errors.username = true
                this.errors.password = true
              })
              .finally(() => {
                this.loading = false
              })
        } else {
          this.errors.username = true
          this.errors.password = true
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 樱花飘落动效 */
.login-container::before {
  content: '★ ☆ ✧ ✿';
  position: absolute;
  top: 10%;
  left: 0;
  width: 100%;
  font-size: 30px;
  color: #FFB7C5;
  opacity: 0.3;
  animation: sakuraDrift 15s linear infinite;
  letter-spacing: 100px;
}

@keyframes sakuraDrift {
  0% { transform: translateX(-100%) translateY(0) rotate(0deg); }
  100% { transform: translateX(100%) translateY(50px) rotate(360deg); }
}

.login-card {
  width: 100%;
  max-width: 420px;
  padding: 50px 40px;
  text-align: center;
  background: rgba(255, 251, 235, 0.98);
  backdrop-filter: blur(25px) saturate(150%);
  box-shadow: 0 15px 40px rgba(255, 183, 197, 0.25),
  0 5px 15px rgba(163, 230, 53, 0.1);
  border: 2px solid rgba(255, 183, 197, 0.4);
  border-radius: 16px;
  animation: floatIn 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

/* 课本边框装饰 */
.login-card::before {
  content: '';
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  bottom: 10px;
  border: 1px dashed rgba(163, 230, 53, 0.2);
  border-radius: 12px;
  pointer-events: none;
}

@keyframes floatIn {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.9);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.login-card h2 {
  font-size: 32px;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 50%, #A3E635 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-weight: 800;
  letter-spacing: 2px;
  position: relative;
}

/* 小装饰 */
.login-card h2::after {
  content: '★';
  position: absolute;
  top: -5px;
  right: -15px;
  font-size: 16px;
  color: #FF9F43;
}

.subtitle {
  color: #7c7c9a;
  margin-bottom: 35px;
  font-size: 15px;
  font-weight: 500;
}

.login-form {
  margin-top: 20px;
}

.login-form >>> .el-input__inner {
  height: 50px;
  border-radius: 25px;
  border: 2px solid rgba(255, 183, 197, 0.3);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding-left: 45px;
  font-size: 15px;
  color: #4a4a4a;
}

.login-form >>> .el-input__inner:focus {
  border-color: #FFB7C5;
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 0 0 4px rgba(255, 183, 197, 0.1),
  0 4px 15px rgba(255, 183, 197, 0.2);
  transform: translateY(-1px);
}

.login-form >>> .el-input__prefix {
  left: 15px;
}

.login-form >>> .el-input__prefix i {
  color: #FFB7C5;
  font-size: 18px;
}

.login-btn {
  width: 100%;
  height: 50px;
  border-radius: 25px;
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  border: 2px solid rgba(255, 255, 255, 0.5);
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 1px;
  box-shadow: 0 6px 20px rgba(255, 183, 197, 0.4),
  0 2px 8px rgba(255, 159, 67, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

/* 彩色波纹效果 */
.login-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.5), transparent);
  transform: translate(-50%, -50%);
  transition: width 0.5s, height 0.5s;
}

.login-btn:active::before {
  width: 300px;
  height: 300px;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 183, 197, 0.5),
  0 3px 12px rgba(255, 159, 67, 0.3);
  border-color: rgba(255, 255, 255, 0.8);
}

.login-btn:active {
  transform: translateY(0) scale(0.98);
}

.links {
  display: flex;
  justify-content: space-between;
  margin-top: 25px;
}

.link {
  color: #FF9F43;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

/* 铅笔下划线 */
.link::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #FFB7C5, #FF9F43);
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.link:hover::after {
  width: 100%;
}

.link:hover {
  color: #FFB7C5;
}
</style>
