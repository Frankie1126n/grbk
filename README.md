# 个人博客系统 (Personal Blog System)

一个基于 **Vue2 + Spring Boot + MySQL** 的全栈个人博客系统，采用蓝色渐变科技风格设计。

## 📋 项目简介

本项目是一个功能完整的个人博客系统，包含用户认证、博客文章管理、分类标签等核心功能。

### 技术栈

**前端:**
- Vue 2.6.14
- Vue Router 3.5.3
- Vuex 3.6.2
- Axios 0.27.2
- Element UI 2.15.13

**后端:**
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3
- JWT (jjwt 0.11.5)
- MySQL 8.0
- Swagger/OpenAPI 3.0

## 🎯 核心功能

### 用户模块
- ✅ 用户登录 (JWT认证)
- ✅ 用户注册 (密码BCrypt加密)
- ✅ 密码重置 (邮箱验证码)
- ✅ 获取当前用户信息

### 博客模块
- ✅ 分页查询博客列表
- ✅ 博客详情查看 (自动增加阅读量)
- ✅ 发布/编辑博客
- ✅ 删除博客
- ✅ 按标题搜索
- ✅ 按分类筛选

### 分类标签模块
- ✅ 分类列表查询 (含文章数量)
- ✅ 标签列表查询 (含文章数量)
- ✅ 标签云展示
- ✅ 热门文章推荐

## 🗄️ 数据库设计

### 核心表结构 (5张表)

1. **user** - 用户表
   - 支持用户名/邮箱唯一性验证
   - 密码BCrypt加密存储
   - 用户状态管理

2. **blog** - 博客文章表
   - 支持HTML/Markdown内容
   - 发布状态控制 (草稿/已发布)
   - 阅读量统计
   - 外键关联用户和分类

3. **category** - 分类表
   - 分类名称唯一
   - 与博客一对多关系

4. **tag** - 标签表
   - 标签名称唯一
   - 与博客多对多关系

5. **blog_tag** - 文章标签关联表
   - 处理博客与标签的多对多关系
   - 联合唯一索引防止重复关联

### 索引设计
- 主键索引: 所有表的id字段
- 唯一索引: username, email, category.name, tag.name
- 普通索引: user_id, category_id, create_time, blog_id, tag_id

## 🚀 快速开始

### 1. 数据库初始化

```bash
# 创建数据库并导入SQL脚本
mysql -u root -p < database/blog_system.sql
```

### 2. 后端启动

```bash
cd backend

# 修改配置文件 (application.yml)
# 配置数据库连接信息:
# spring.datasource.username=your_username
# spring.datasource.password=your_password

# 启动Spring Boot应用
mvn spring-boot:run

# 或使用IDE直接运行 BlogApplication.java
```

后端服务将在 `http://localhost:8080/api` 启动

API文档访问: `http://localhost:8080/api/swagger-ui/`

### 3. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端应用将在 `http://localhost:8081` 启动

### 4. 默认账号

系统已预置测试账号:
- 用户名: `admin`
- 密码: `123456`
- 邮箱: `admin@blog.com`

## 📁 项目结构

```
grbk/
├── database/                    # 数据库SQL脚本
│   └── blog_system.sql         # 建表及初始数据
│
├── backend/                     # Spring Boot后端
│   ├── src/main/java/com/blog/
│   │   ├── config/             # 配置类 (JWT, CORS, MyBatis等)
│   │   ├── controller/         # 控制器层
│   │   ├── service/            # 服务层
│   │   ├── mapper/             # 数据访问层
│   │   ├── entity/             # 实体类
│   │   ├── dto/                # 数据传输对象
│   │   ├── exception/          # 异常处理
│   │   ├── interceptor/        # 拦截器
│   │   └── common/             # 公共类
│   ├── src/main/resources/
│   │   ├── mapper/             # MyBatis XML映射文件
│   │   └── application.yml     # 配置文件
│   └── pom.xml                 # Maven依赖
│
└── frontend/                    # Vue2前端
    ├── src/
    │   ├── api/                # API接口封装
    │   ├── assets/             # 静态资源
    │   ├── components/         # 公共组件
    │   ├── router/             # 路由配置
    │   ├── store/              # Vuex状态管理
    │   ├── utils/              # 工具类
    │   ├── views/              # 页面组件
    │   ├── App.vue             # 根组件
    │   └── main.js             # 入口文件
    ├── index.html              # HTML模板
    └── package.json            # NPM依赖
```

## 🎨 UI设计特色

### 蓝色渐变科技风
- 主色调: `#4facfe` → `#00f2fe`
- 背景: 浅蓝渐变 + 科技网格纹理
- 玻璃拟物卡片效果 (`backdrop-filter: blur`)
- 霓虹光效边框 (`box-shadow`)
- 渐变文字特效 (`background-clip: text`)

### 交互动效
- ✨ 路由切换渐入渐出动画
- ✨ 卡片hover悬浮效果
- ✨ 按钮点击缩放反馈
- ✨ 骨架屏加载动画
- ✨ 回到顶部脉冲按钮
- ✨ 滚动时Header背景渐变

## 📡 API接口文档

### 用户接口
- `POST /user/login` - 用户登录
- `POST /user/register` - 用户注册
- `GET /user/current` - 获取当前用户 (需认证)
- `POST /user/forgot-password` - 发送密码重置验证码
- `POST /user/reset-password` - 重置密码

### 博客接口
- `GET /blog/list` - 分页查询博客列表
- `GET /blog/{id}` - 查询博客详情
- `POST /blog/save` - 保存博客 (需认证)
- `DELETE /blog/{id}` - 删除博客 (需认证)

### 分类/标签接口
- `GET /category/list` - 获取所有分类
- `POST /category/add` - 新增分类 (需认证)
- `GET /tag/list` - 获取所有标签
- `POST /tag/add` - 新增标签 (需认证)

## 🔒 安全特性

1. **JWT认证**: 基于Token的无状态认证
2. **密码加密**: BCrypt加密算法
3. **CORS配置**: 跨域请求安全控制
4. **参数校验**: @Valid注解 + 全局异常处理
5. **SQL注入防护**: MyBatis预编译参数
6. **XSS防护**: 前端输入过滤

## 📝 开发说明

### 后端开发规范
- 分层架构: Controller → Service → Mapper
- RESTful API设计
- 统一响应格式 `Result<T>`
- 全局异常处理
- Swagger接口文档

### 前端开发规范
- Vue Options API
- Vuex模块化状态管理
- Axios请求/响应拦截
- 路由守卫权限控制
- 组件化开发

## 🛠️ 扩展功能建议

- [ ] 评论系统
- [ ] 点赞收藏
- [ ] 文章编辑器 (Markdown)
- [ ] 图片上传 (OSS)
- [ ] 邮件服务集成
- [ ] 用户头像上传
- [ ] 文章草稿箱
- [ ] 文章归档
- [ ] 友情链接
- [ ] 访问统计

## 📄 许可证

本项目仅供学习交流使用。

## 👨‍💻 作者

Blog System Development Team

## 🙏 致谢

感谢 Vue.js、Spring Boot 及相关开源项目的贡献者。
