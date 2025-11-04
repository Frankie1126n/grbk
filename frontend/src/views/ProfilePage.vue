<template>
  <div class="profile-page">
    <div class="profile-container">
      <div class="profile-header">
        <h2>个人资料</h2>
      </div>
      
      <el-form
        ref="profileForm"
        :model="profileForm"
        :rules="profileRules"
        label-width="100px"
        class="profile-form"
      >
        <el-form-item label="头像">
          <ImageUpload
            v-model="profileForm.avatarUrl"
            upload-type="avatar"
            placeholder="点击上传头像"
            tip="支持 jpg、png、gif 格式，大小不超过 10MB"
          />
        </el-form-item>

        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="profileForm.username"
            placeholder="请输入用户名"
            maxlength="50"
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="profileForm.email"
            type="email"
            placeholder="请输入邮箱"
          />
        </el-form-item>

        <div class="password-section">
          <h3>修改密码</h3>
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input
              v-model="profileForm.oldPassword"
              type="password"
              placeholder="如需修改密码，请输入旧密码"
              show-password
            />
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="profileForm.newPassword"
              type="password"
              placeholder="请输入新密码（6-20个字符）"
              show-password
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="profileForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
            />
          </el-form-item>
        </div>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            保存修改
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import ImageUpload from '@/components/ImageUpload'
import { getCurrentUser, updateProfile } from '@/api/user'

export default {
  name: 'ProfilePage',
  components: {
    ImageUpload
  },
  data() {
    const validatePassword = (rule, value, callback) => {
      if (value && (value.length < 6 || value.length > 20)) {
        callback(new Error('密码长度必须在6-20个字符之间'))
      } else {
        callback()
      }
    }
    const validateConfirmPassword = (rule, value, callback) => {
      if (value && value !== this.profileForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      profileForm: {
        username: '',
        email: '',
        avatarUrl: '',
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      profileRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 50, message: '用户名长度在3-50个字符之间', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        newPassword: [
          { validator: validatePassword, trigger: 'blur' }
        ],
        confirmPassword: [
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      loading: false,
      originalData: {}
    }
  },
  mounted() {
    this.loadUserInfo()
  },
  methods: {
    async loadUserInfo() {
      try {
        const res = await getCurrentUser()
        if (res.code === 200) {
          this.profileForm.username = res.data.username
          this.profileForm.email = res.data.email
          this.profileForm.avatarUrl = res.data.avatarUrl || ''
          
          // 保存原始数据
          this.originalData = { ...this.profileForm }
        }
      } catch (error) {
        this.$message.error('获取用户信息失败')
      }
    },
    async handleSubmit() {
      this.$refs.profileForm.validate(async (valid) => {
        if (!valid) {
          return false
        }

        // 检查密码修改逻辑
        if (this.profileForm.newPassword && !this.profileForm.oldPassword) {
          this.$message.warning('修改密码需要输入旧密码')
          return
        }

        this.loading = true
        try {
          const updateData = {
            username: this.profileForm.username,
            email: this.profileForm.email,
            avatarUrl: this.profileForm.avatarUrl
          }

          // 如果要修改密码
          if (this.profileForm.oldPassword && this.profileForm.newPassword) {
            updateData.oldPassword = this.profileForm.oldPassword
            updateData.newPassword = this.profileForm.newPassword
          }

          const res = await updateProfile(updateData)
          if (res.code === 200) {
            this.$message.success('资料更新成功')
            
            // 更新 Vuex 中的用户信息（保留原有的 id 和 role）
            this.$store.commit('user/SET_USER_INFO', {
              ...this.$store.state.user.userInfo,
              username: this.profileForm.username,
              email: this.profileForm.email,
              avatarUrl: this.profileForm.avatarUrl
            })
            
            // 直接跳转回首页
            this.$router.push('/home')
          }
        } catch (error) {
          this.$message.error(error.message || '更新失败')
        } finally {
          this.loading = false
        }
      })
    },
    handleReset() {
      this.profileForm = { ...this.originalData }
      this.profileForm.oldPassword = ''
      this.profileForm.newPassword = ''
      this.profileForm.confirmPassword = ''
      this.$refs.profileForm.clearValidate()
    }
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  padding: 40px 20px;
  position: relative;
}

.profile-container {
  max-width: 700px;
  margin: 0 auto;
  background: rgba(255, 251, 235, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(255, 183, 197, 0.25),
              0 4px 12px rgba(163, 230, 53, 0.1);
  border: 2px solid rgba(255, 183, 197, 0.3);
  position: relative;
  z-index: 1;
}

/* 课本装饰 */
.profile-container::before {
  content: '';
  position: absolute;
  top: 12px;
  left: 12px;
  right: 12px;
  bottom: 12px;
  border: 1px dashed rgba(163, 230, 53, 0.25);
  border-radius: 10px;
  pointer-events: none;
}

.profile-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
}

.profile-header h2 {
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 50%, #A3E635 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-size: 32px;
  font-weight: 700;
  margin: 0;
  letter-spacing: 1px;
}

/* 小装饰 */
.profile-header h2::after {
  content: '★';
  position: absolute;
  top: -5px;
  right: 30%;
  font-size: 18px;
  color: #FF9F43;
}

.profile-form {
  margin-top: 30px;
}

.profile-form >>> .el-form-item__label {
  color: #4a4a4a;
  font-weight: 600;
  font-size: 15px;
}

.profile-form >>> .el-input__inner {
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid rgba(255, 183, 197, 0.3);
  border-radius: 10px;
  color: #4a4a4a;
  transition: all 0.3s ease;
  padding: 12px 15px;
  font-size: 14px;
}

.profile-form >>> .el-input__inner::placeholder {
  color: rgba(255, 183, 197, 0.5);
}

.profile-form >>> .el-input__inner:focus {
  background: rgba(255, 255, 255, 1);
  border-color: #FFB7C5;
  box-shadow: 0 0 0 3px rgba(255, 183, 197, 0.15),
              0 2px 8px rgba(255, 183, 197, 0.2);
}

.password-section {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 2px solid rgba(255, 183, 197, 0.2);
  position: relative;
}

/* 分割线装饰 */
.password-section::before {
  content: '✧';
  position: absolute;
  top: -12px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(255, 251, 235, 0.95);
  padding: 0 10px;
  color: #A3E635;
  font-size: 20px;
}

.password-section h3 {
  color: #FF9F43;
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 20px 0;
  text-align: center;
}

.profile-form >>> .el-button {
  padding: 12px 40px;
  font-size: 16px;
  border-radius: 20px;
  transition: all 0.3s ease;
  font-weight: 600;
  border: 2px solid;
}

.profile-form >>> .el-button--primary {
  background: linear-gradient(135deg, #FFB7C5 0%, #FF9F43 100%);
  border-color: rgba(255, 255, 255, 0.5);
  color: #fff;
  box-shadow: 0 4px 15px rgba(255, 183, 197, 0.4);
}

.profile-form >>> .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 183, 197, 0.5);
  border-color: rgba(255, 255, 255, 0.8);
}

.profile-form >>> .el-button--default {
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(255, 183, 197, 0.4);
  color: #FF9F43;
}

.profile-form >>> .el-button--default:hover {
  background: rgba(255, 255, 255, 1);
  border-color: #FFB7C5;
  transform: translateY(-2px);
}

/* 移动端适配 */
@media (max-width: 768px) {
  .profile-container {
    padding: 30px 20px;
  }
  
  .profile-header h2 {
    font-size: 26px;
  }
}
</style>
