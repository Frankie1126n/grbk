<template>
  <div class="register-container">
    <div class="register-card card">
      <h2 class="gradient-text">创建账号</h2>
      <p class="subtitle">加入我们的博客社区</p>

      <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form">
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            prefix-icon="el-icon-user"
            placeholder="请输入用户名（3-50个字符）"
          />
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            prefix-icon="el-icon-message"
            placeholder="请输入邮箱"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            prefix-icon="el-icon-lock"
            placeholder="请输入密码（6-20个字符）"
            @input="checkPasswordStrength"
          />
          <div v-if="registerForm.password" class="password-strength">
            <span>密码强度：</span>
            <span :class="'strength-' + passwordStrength">{{ passwordStrengthText }}</span>
          </div>
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            prefix-icon="el-icon-lock"
            placeholder="请再次输入密码"
            @keyup.enter.native="handleRegister"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="register-btn"
            :loading="loading"
            @click="handleRegister"
          >
            {{ loading ? '注册中...' : '注册' }}
          </el-button>
        </el-form-item>

        <div class="links">
          <router-link to="/login" class="link">已有账号？立即登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  name: 'Register',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      registerForm: {
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
      },
      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 50, message: '用户名长度为3-50个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      loading: false,
      passwordStrength: 0,
      passwordStrengthText: '弱'
    }
  },
  methods: {
    ...mapActions('user', ['register']),
    
    checkPasswordStrength() {
      const password = this.registerForm.password
      let strength = 0
      
      if (password.length >= 6) strength++
      if (password.length >= 10) strength++
      if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++
      if (/\d/.test(password)) strength++
      if (/[^a-zA-Z0-9]/.test(password)) strength++
      
      if (strength <= 1) {
        this.passwordStrength = 1
        this.passwordStrengthText = '弱'
      } else if (strength <= 3) {
        this.passwordStrength = 2
        this.passwordStrengthText = '中'
      } else {
        this.passwordStrength = 3
        this.passwordStrengthText = '强'
      }
    },
    
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          
          const { confirmPassword, ...registerData } = this.registerForm
          
          this.register(registerData)
            .then(() => {
              this.$message.success('注册成功，请登录')
              this.$router.push('/login')
            })
            .catch(() => {
              // 错误已在全局拦截器处理
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  text-align: center;
}

.register-card h2 {
  font-size: 28px;
  margin-bottom: 10px;
}

.subtitle {
  color: #666;
  margin-bottom: 30px;
  font-size: 14px;
}

.register-form {
  margin-top: 20px;
}

.register-form >>> .el-input__inner {
  height: 45px;
  border-radius: 22px;
  border: 1px solid rgba(79, 172, 254, 0.3);
  transition: all 0.3s ease;
}

.register-form >>> .el-input__inner:focus {
  border-color: #4facfe;
  box-shadow: 0 0 10px rgba(79, 172, 254, 0.3);
}

.password-strength {
  margin-top: 5px;
  font-size: 12px;
  text-align: left;
}

.strength-1 {
  color: #f56c6c;
}

.strength-2 {
  color: #e6a23c;
}

.strength-3 {
  color: #67c23a;
}

.register-btn {
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

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(79, 172, 254, 0.5);
}

.register-btn:active {
  transform: scale(0.98);
}

.links {
  margin-top: 20px;
  text-align: center;
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
