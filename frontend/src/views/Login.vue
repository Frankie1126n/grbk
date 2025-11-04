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
}

.login-card {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  text-align: center;
}

.login-card h2 {
  font-size: 28px;
  margin-bottom: 10px;
}

.subtitle {
  color: #666;
  margin-bottom: 30px;
  font-size: 14px;
}

.login-form {
  margin-top: 20px;
}

.login-form >>> .el-input__inner {
  height: 45px;
  border-radius: 22px;
  border: 1px solid rgba(79, 172, 254, 0.3);
  transition: all 0.3s ease;
}

.login-form >>> .el-input__inner:focus {
  border-color: #4facfe;
  box-shadow: 0 0 10px rgba(79, 172, 254, 0.3);
}

.login-btn {
  width: 100%;
  height: 45px;
  border-radius: 22px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
  font-size: 16px;
  font-weight: bold;
  box-shadow: 0 4px 15px rgba(79, 172, 254, 0.3);
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(79, 172, 254, 0.5);
}

.login-btn:active {
  transform: scale(0.98);
}

.links {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.link {
  color: #4facfe;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
}

.link:hover {
  color: #00f2fe;
  text-decoration: underline;
}
</style>
