<template>
  <div class="forgot-password-container">
    <div class="forgot-password-card card">
      <h2 class="gradient-text">重置密码</h2>
      <p class="subtitle">请输入您的邮箱以接收验证码</p>

      <el-form ref="forgotPasswordForm" :model="forgotPasswordForm" :rules="forgotPasswordRules" class="forgot-password-form">
        <el-form-item prop="email">
          <el-input
            v-model="forgotPasswordForm.email"
            prefix-icon="el-icon-message"
            placeholder="请输入邮箱"
          />
        </el-form-item>

        <el-form-item prop="code" v-if="codeSent">
          <el-input
            v-model="forgotPasswordForm.code"
            prefix-icon="el-icon-key"
            placeholder="请输入验证码"
          >
            <el-button
              slot="append"
              :disabled="countdown > 0"
              @click="sendCode"
            >
              {{ countdown > 0 ? `${countdown}秒后重发` : '发送验证码' }}
            </el-button>
          </el-input>
        </el-form-item>

        <el-form-item prop="newPassword" v-if="codeSent">
          <el-input
            v-model="forgotPasswordForm.newPassword"
            type="password"
            prefix-icon="el-icon-lock"
            placeholder="请输入新密码（6-20个字符）"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            v-if="!codeSent"
            type="primary"
            class="send-code-btn"
            :loading="loading"
            @click="sendCode"
          >
            {{ loading ? '发送中...' : '发送验证码' }}
          </el-button>
          
          <el-button
            v-else
            type="primary"
            class="reset-btn"
            :loading="loading"
            @click="handleResetPassword"
          >
            {{ loading ? '重置中...' : '重置密码' }}
          </el-button>
        </el-form-item>

        <div class="links">
          <router-link to="/login" class="link">返回登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { forgotPassword, resetPassword } from '@/api/user'

export default {
  name: 'ForgotPassword',
  data() {
    return {
      forgotPasswordForm: {
        email: '',
        code: '',
        newPassword: ''
      },
      forgotPasswordRules: {
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入验证码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
        ]
      },
      loading: false,
      codeSent: false,
      countdown: 0,
      timer: null
    }
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    sendCode() {
      this.$refs.forgotPasswordForm.validateField('email', error => {
        if (!error) {
          this.loading = true
          
          forgotPassword(this.forgotPasswordForm.email)
            .then(() => {
              this.$message.success('验证码已发送，请查收邮件')
              this.codeSent = true
              this.startCountdown()
            })
            .catch(() => {
              // 错误已在全局拦截器处理
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    },
    
    startCountdown() {
      this.countdown = 60
      this.timer = setInterval(() => {
        this.countdown--
        if (this.countdown <= 0) {
          clearInterval(this.timer)
        }
      }, 1000)
    },
    
    handleResetPassword() {
      this.$refs.forgotPasswordForm.validate(valid => {
        if (valid) {
          this.loading = true
          
          const resetData = {
            email: this.forgotPasswordForm.email,
            code: this.forgotPasswordForm.code,
            newPassword: this.forgotPasswordForm.newPassword
          }
          
          resetPassword(resetData)
            .then(() => {
              this.$message.success('密码重置成功，请登录')
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
.forgot-password-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.forgot-password-card {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  text-align: center;
}

.forgot-password-card h2 {
  font-size: 28px;
  margin-bottom: 10px;
}

.subtitle {
  color: #666;
  margin-bottom: 30px;
  font-size: 14px;
}

.forgot-password-form {
  margin-top: 20px;
}

.forgot-password-form >>> .el-input__inner {
  height: 45px;
  border-radius: 22px;
  border: 1px solid rgba(79, 172, 254, 0.3);
  transition: all 0.3s ease;
}

.forgot-password-form >>> .el-input__inner:focus {
  border-color: #4facfe;
  box-shadow: 0 0 10px rgba(79, 172, 254, 0.3);
}

.forgot-password-form >>> .el-input-group__append {
  border-radius: 0 22px 22px 0;
  border: 1px solid rgba(79, 172, 254, 0.3);
  border-left: none;
  background: rgba(79, 172, 254, 0.1);
}

.send-code-btn,
.reset-btn {
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

.send-code-btn:hover,
.reset-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(79, 172, 254, 0.5);
}

.send-code-btn:active,
.reset-btn:active {
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
