# 数据库设计文档

## 创建数据库

```sql
CREATE DATABASE CMS
CHARACTER SET utf8mb4
COLLATE utf8mb4_bin;
```

---

## 用户表 (user)

### 表结构

| 字段名        | 数据类型                         | 约束                                        | 描述                |
|--------------|--------------------------------|--------------------------------------------|-------------------|
| **id**       | `INT AUTO_INCREMENT PRIMARY KEY` | 主键，自动递增                             | 用户唯一 ID       |
| **username** | `VARCHAR(50) NOT NULL UNIQUE`   | 非空，唯一                                 | 用户名            |
| **sex**      | `ENUM('男','女','未设定')`                | 默认未设定                                | 性别             |
| **email**    | `VARCHAR(100) NOT NULL UNIQUE`          | 可为空，唯一                               | 邮箱              |
| **phone**    | `VARCHAR(11) UNIQUE`           | 可为空，唯一                               | 手机号            |
| **password** | `CHAR(128)`            | 无特殊约束                                      | 加密后的密码      |
| **SDUId** | `CHAR(20) NOT NULL UNIQUE`    | 非空，唯一                                 | 学号              |
| **major**    | `ENUM('0', '1', '2', '3')` | 非空，默认为0                                   | 专业 (`0` 软工 / `1` 树莓 / `2` 大数据 / `3` AI) |
| **permission** | `TINYINT DEFAULT 2`          | 默认 2                                    | 权限 (`0` 教务 / `1` 教师 / `2` 学生) |
| **nation**   | `VARCHAR(100) DEFAULT 'China' NOT NULL` | 默认 `China`，非空                     | 国籍/地区        |
| **ethnic**   | `VARCHAR(50) DEFAULT '汉族' NOT NULL` | 默认 `汉族`，非空                        | 民族              |
| **PoliticsStatus**   | `VARCHAR(50) DEFAULT '群众' NOT NULL` | 默认`群众`，非空                   | 政治面貌         |
| **college** | `VARCHAR(20) DEFAULT '软件学院' NOT NULL` | 默认`软件学院`，非空                   | 所属学院         |
| **created_at** | `TIMESTAMP DEFAULT CURRENT_TIMESTAMP` | 自动生成，默认当前时间                   | 注册时间          |
| **updated_at** | `TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 自动更新为当前时间 | 最后更新时间       |
| **last_login_at** | `TIMESTAMP DEFAULT NULL`  | 可为空                                    | 最后登录时间      |

---

### 创建指令

```sql
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户唯一ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    sex ENUM('男','女','未设定') COMMENT '性别',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    phone VARCHAR(11) UNIQUE COMMENT '手机号',
    password CHAR(128) NOT NULL COMMENT '加密后的密码',
    SDUId CHAR(20) NOT NULL UNIQUE COMMENT '学号',
    major ENUM('0','1','2','3') DEFAULT '0' COMMENT '专业0软工/1树莓/2大数据/3AI',
    permission TINYINT DEFAULT 2 COMMENT '教务牢师0/教师1/学生2',
    nation VARCHAR(100) DEFAULT 'China' NOT NULL COMMENT '国籍/地区',
    ethnic VARCHAR(50) DEFAULT '汉族' NOT NULL COMMENT '民族',
    PoliticsStatus VARCHAR(50) DEFAULT '群众' NOT NULL COMMENT '政治面貌',
    college VARCHAR(20) DEFAULT '软件学院' NOT NULL COMMENT '院系',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    last_login_at TIMESTAMP DEFAULT NULL COMMENT '最后登录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

---

## 学籍表(status)

### 表结构

---

| 字段名     | 数据类型                     | 约束                     | 描述                        |
|-----------|------------------------------|--------------------------|-----------------------------|
| **id**    | `INT`                         | 外键，关联 `user` 表     | 对应用户 ID                 |
| **grade** | `VARCHAR(4) NOT NULL`            | 非空                     | 年级                        |
| **section** | `TINYINT`  | 关联班级表     | 班级                        |
| **status** | `TINYINT NOT NULL DEFAULT 0` | 非空，默认0'             | 学籍状态 (`0` 在读 / `1` 休学 / `2` 降转 / `3` 退学) |
| **admission** | `INT`  | 非空     | 入学时间                        |
| **graduation** | `INT` | 非空             | 毕业时间                |

---

### 创建指令

```sql
CREATE TABLE status(
    id INT NOT NULL,
    grade VARCHAR(4) NOT NULL COMMENT '年级',
    section INT COMMENT '班级',
    status TINYINT NOT NULl DEFAULT 0 COMMENT '学生状态(`0` 在读 / `1` 休学 / `2` 降转 / `3` 退学)',
    admission INT NOT NULL COMMENT '入学时间',
    graduation INT NOT NULL COMMENT '毕业时间',
    FOREIGN KEY (id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (section) REFERENCES section(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学籍表';
```

## 课程表(classes)

### 表结构

---

| 字段名           | 数据类型                                                    | 约束                                       | 描述                         |
|------------------|-----------------------------------------------------------|-------------------------------------------|------------------------------|
| **id**           | `INT AUTO_INCREMENT PRIMARY KEY`                          | 主键，自动递增                            | 课程唯一ID                   |
| **name**         | `VARCHAR(100) NOT NULL`                                   | 非空                                     | 课程名称                     |
| **category**     | `VARCHAR(100)`                                            | 可为空                                   | 课程类别（如体育小项）       |
| **point**        | `TINYINT NOT NULL`                                        | 非空                                     | 学分                         |
| **teacher_id**   | `INT NOT NULL`                                            | 非空，外键，关联 `user` 表               | 教师ID                       |
| **classroom**    | `VARCHAR(50)`                                             | 可为空                                   | 上课教室                     |
| **week_start**   | `TINYINT DEFAULT 1 NOT NULL`                              | 默认值 1，非空                            | 起始周                       |
| **week_end**     | `TINYINT DEFAULT 17 NOT NULL`                             | 默认值 17，非空                           | 结束周                       |
| **period**       | `TINYINT NOT NULL`                                        | 非空                                     | 课时                         |
| **time**         | `SET('0', '1', ..., '24')`                                | 非空                                     | 上课时间段                   |
| **college**      | `VARCHAR(50)`                                             | 可为空                                   | 开课学院                     |
| **term**         | `VARCHAR(15) NOT NULL`                                    | 非空                                     | 开课学期                     |
| **class_num**    | `VARCHAR(50) NOT NULL`                                    | 无特殊约束                              | 课序号                       |
| **type**         | `ENUM('必修', '限选', '任选') NOT NULL`                   | 非空                                     | 课程类型                     |
| **capacity**     | `TINYINT NOT NULL`                                        | 非空                                     | 课程容量                     |
| **status**       | `TINYINT DEFAULT 0 NOT NULL`                              | 默认 0，非空                              | 课程状态（0申请、1审批通过、2拒绝、3已结课） |
| **f_reason**     | `VARCHAR(50)`                                             | 可为空                                   | 退回原因                     |
| **published**    | `BOOLEAN DEFAULT 0`                                       | 默认 0                                   | 成绩是否发布                 |
| **regular_ratio**| `DECIMAL(3,2) NOT NULL`                                   | 非空                                     | 平时分占比                   |
| **final_ratio**  | `DECIMAL(3,2) NOT NULL`                                   | 非空                                     | 期末分占比                   |
| **intro**        | `VARCHAR(50) NOT NULL`                                    | 非空                                     | 课程简介                     |
| **examination**  | `TINYINT(1) NOT NULL`                                     | 非空                                     | 考核方式（考查0，考试1）     |

---

### 创建指令

```sql
CREATE TABLE classes(
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '课程唯一ID',
    name VARCHAR(100) NOT NULL COMMENT '课程名称',
    category VARCHAR(100) COMMENT '类别，如体育小项',
    point TINYINT NOT NULL COMMENT '学分',
    teacher_id INT NOT NULL COMMENT '教师id',
    classroom VARCHAR(50) COMMENT '上课教室',
    week_start TINYINT DEFAULT 1 NOT NULL COMMENT '起始周',
    week_end TINYINT DEFAULT 17 NOT NULL COMMENT '结束周',
    period TINYINT NOT NULL COMMENT '课时',
    time SET('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24') COMMENT '上课时间段',
    college VARCHAR(50) COMMENT '开课学院',
    term VARCHAR(15) NOT NULL COMMENT '开课学期',
    class_num VARCHAR(50) NOT NULL COMMENT '课序号',
    type ENUM('必修', '限选', '任选') NOT NULL COMMENT '课程类型',
    capacity TINYINT NOT NULL COMMENT '课容量',
    status TINYINT DEFAULT 0  NOT NULL COMMENT '课程状态',
    f_reason VARCHAR(50) COMMENT '退回原因',
    published BOOLEAN DEFAULT 0 COMMENT '成绩是否发布',
    regular_ratio DECIMAL(3,2) NOT NULL COMMENT '平时分占比',
    final_ratio   DECIMAL(3,2) NOT NULL COMMENT '期末分占比',
    intro VARCHAR(50) NOT NULL COMMENT '课程简介',
    examination TINYINT(1) NOT NULL COMMENT '考核方式，考查0 考核1',
    FOREIGN KEY (teacher_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';
```

## 选课结果表(course_reg)

### 表结构

---

| 字段名        | 数据类型                     | 约束                                        | 描述           |
|--------------|------------------------------|--------------------------------------------|--------------|
| **id**       | `INT AUTO_INCREMENT PRIMARY KEY` | 主键，自动递增                             | 选课唯一 ID   |
| **student_id** | `INT NOT NULL`                | 非空，外键，关联 `user` 表                 | 学生 ID      |
| **course_id** | `INT NOT NULL`                | 非空，外键，关联 `classes` 表                | 课程 ID      |
| **class_num** | `VARCHAR(50) NOT NULL`        | 非空，关联 `class_num` 表            | 课序号       |

---

### 创建指令

```sql
CREATE TABLE course_reg(
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '选课唯一ID',
    student_id INT NOT NULL COMMENT '学生id',
    course_id INT NOT NULL COMMENT '课程id',
    class_num VARCHAR(50) NOT NULL COMMENT '课序号',
    FOREIGN KEY (student_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES classes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课结果表';
```

## 成绩表(grade)

### 表结构

---

| 字段名        | 数据类型                     | 约束                                        | 描述           |
|--------------|------------------------------|--------------------------------------------|--------------|
| **id**       | `INT AUTO_INCREMENT PRIMARY KEY` | 主键，自动递增                             | 成绩唯一 ID   |
| **student_id** | `INT NOT NULL`                | 非空，外键，关联 `user` 表                 | 学生 ID      |
| **course_id** | `INT NOT NULL`                | 非空，外键，关联 `classes` 表                | 课程 ID      |
| **grade**    | `TINYINT NOT NULL`             | 非空                                      | 成绩         |
| **term**     | `VARCHAR(15) NOT NULL`         | 非空                                      | 开课学期     |
| **rank**     | `TINYINT NOT NULL`             | 非空                                      | 排名         |

---

### 创建指令

```sql
CREATE TABLE grade(
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '成绩唯一ID',
    student_id INT NOT NULL COMMENT '学生id',
    course_id INT NOT NULL COMMENT '课程id',
    regular INT NOT NULL COMMENT '平时分',
    final INT NOT NULL COMMENT '期末分',
    grade TINYINT NOT NULL COMMENT '成绩',
    term VARCHAR(15) NOT NULL COMMENT '开课学期',
    rank TINYINT NOT NULL COMMENT '排名',
    FOREIGN KEY (student_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES classes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';
```

## 班级(section)

### 表结构

| 字段名       | 数据类型                          | 约束                                    | 描述                                      |
|-------------|----------------------------------|----------------------------------------|-----------------------------------------|
| **id**       | `INT AUTO_INCREMENT PRIMARY KEY` | 主键，自动递增                        | 班级唯一 ID                             |
| **grade**    | `YEAR NOT NULL`              | 非空                                  | 年级                                    |
| **number**   | `INT NOT NULL`              | 非空                                  | 班级号                                  |
| **major**    | `ENUM('0', '1', '2', '3') NOT NULL` | 非空                              | 专业 (`0` 软工 / `1` 树莓 / `2` 大数据 / `3` AI) |
| **advisor_id** | `INT`                         | 外键，关联 `user` 表                  | 班主任 ID                              |

---

### 创建指令

```sql
CREATE TABLE section (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '班级唯一ID',
    grade YEAR NOT NULL COMMENT '年级',
    number INT NOT NULL COMMENT '班级号',
    major ENUM('0', '1', '2', '3') NOT NULL COMMENT '专业0软工/1树莓/2大数据/3AI',
    advisor_id INT COMMENT '导员ID',
    UNIQUE KEY unique_grade_number (grade, number, major),
    FOREIGN KEY (advisor_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';
```
