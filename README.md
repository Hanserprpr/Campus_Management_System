# 🎓 教务管理系统 Campus Management System

<div align="center">

![GitHub commit activity](https://img.shields.io/github/commit-activity/m/Hanserprpr/Campus_Management_System)
![GitHub last commit](https://img.shields.io/github/last-commit/Hanserprpr/Campus_Management_System)
![GitHub top language](https://img.shields.io/github/languages/top/Hanserprpr/Campus_Management_System)
![Repo size](https://img.shields.io/github/repo-size/Hanserprpr/Campus_Management_System)
![License](https://img.shields.io/github/license/Hanserprpr/Campus_Management_System)
![GitHub issues](https://img.shields.io/github/issues/Hanserprpr/Campus_Management_System)
![GitHub issues closed](https://img.shields.io/github/issues-closed/Hanserprpr/Campus_Management_System)

</div>

## 📌 项目仓库一览

<a href="https://github.com/Hanserprpr/Campus_Management_System">
  <img align="center" src="https://github-readme-stats.vercel.app/api/pin/?username=Hanserprpr&repo=Campus_Management_System" />
</a>

<a href="https://github.com/Tans-hub/Campus_Management_System_front">
  <img align="center" src="https://github-readme-stats.vercel.app/api/pin/?username=PycmgMagic&repo=Campus_Management_System_front" />
</a>

本项目是基于 **Spring Boot 后端 + JavaFX 前端** 的教务管理系统课设，具备学生管理、课程管理、选课与成绩录入等核心功能，~~适合高校教务管理场景的初步建模与实践~~。

---

## 📁 项目结构（后端）

```yaml
Campus_Management_System/
├── documents/           # 项目文档（设计说明、数据库文档等）
├── logs/                # 日志输出目录
├── src/
│   └── main/
│       ├── java/com.orbithy.cms/
│       │   ├── annotation/        # 自定义注解（如权限控制）
│       │   ├── aspect/            # AOP 切面处理
│       │   ├── cache/             # Redis 缓存封装
│       │   ├── config/            # 项目配置类（拦截器、跨域等）
│       │   ├── controller/        # 控制器，处理请求
│       │   ├── data/
│       │   │   ├── dto/           # 数据传输对象
│       │   │   ├── po/            # 持久化对象（数据库映射）
│       │   │   └── vo/            # 返回前端的封装对象
│       │   ├── exception/         # 自定义异常
│       │   ├── mapper/            # MyBatis Mapper 接口
│       │   ├── service/           # 业务逻辑层
│       │   └── utils/             # 工具类（如统一返回格式、密码加密）
│       └── resources/
│           ├── mapper/            # Mapper XML 文件
│           ├── application.yml    # 主配置文件
│           └── application.properties（备用）
├── test/                 # 单元测试（可选）
└── target/               # 编译生成目录（可忽略）
```

---

## ✨ 核心功能模块

### 🎓 学生中心

- 学生信息展示与修改（姓名、性别、电话、邮箱等）
- 修改登录密码

### 📘 学籍管理

- 学籍状态变更（休学、复学、转学、退学）
- 自动生成学籍卡片（PDF 或系统页面）

### 📊 成绩管理

- 按学期查询成绩
- 成绩排名查看（班级/年级排名）
- 成绩单导出/打印

### 📚 选课系统

- 支持课程冲突检测（时间、课程类型）
- 课程类型区分（必修 / 限选 / 任选）
- 学分上限控制（如：35 学分）
- 多条件搜索（教师姓名、课程名、课序号）
- 选课时间窗口控制
- 当前选课结果查询
- 课程退选

### 🧑‍🏫 培养计划管理

- 自动生成学生课表
- 教师课程评价功能（TODO）

### 🧑‍🤝‍🧑 班级管理

- 创建、修改、删除班级
- 查看班级成员、班级课程

### 🏫 课程与教室管理

- 教师创建课程后，教务管理员点击自动分配教室（计划）

---

## 🧰 技术栈

| 层级    | 技术              |
|-------|-----------------|
| 后端框架  | Spring Boot     |
| ORM   | MyBatis-Plus    |
| 数据库   | MariaDB / MySQL |
| 安全认证  | JWT / 自定义注解权限控制 |
| 前端客户端 | JavaFX（另建仓库）    |
| 缓存    | Redis           |

---

## 📄 使用说明

### ✅ 后端启动

暂无相关说明

### 💻 前端启动（JavaFX）

本项目的前端使用 JavaFX 实现，代码托管于独立仓库：

👉 [Tans-hub/Campus_Management_System_front](https://github.com/Tans-hub/Campus_Management_System_front)

请参考该仓库中的 `README.md` 获取前端的构建与运行方式。

---

## 🔗 API 文档

- 详见 [Apifox](https://cmsdoc.orbithy.com)

---

## 🧑‍💻 开发者

[![contributors](https://contrib.rocks/image?repo=Hanserprpr/Campus_Management_System)](https://github.com/Hanserprpr/Campus_Management_System/graphs/contributors)

<img src="http://github-profile-summary-cards.vercel.app/api/cards/profile-details?username=Hanserprpr&theme=default" />

<img src="http://github-profile-summary-cards.vercel.app/api/cards/profile-details?username=N2H4-xz&theme=default" />

---

## 📜 项目声明

本项目不用于商业用途，代码中部分功能和逻辑为课设简化实现。

---
