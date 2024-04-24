前端

# 一、环境配置

下载`node.js`

https://www.cnblogs.com/lgx5/p/10732016.html

# 二、运行

1. `IntelliJ Idea`打开项目文件夹，连接好本地数据库，`application.yml`配置数据源

2. 运行后端代码（`UserCenter2Application.java`）
3. 运行前端代码

​		新建终端——

```
cd frontend
npm install
npm run serve
```

打开浏览器，地址栏输入“`127.0.0.1:8080`”并跳转（不能用`localhost`）

# 三、实现功能

## `IndexPage.vue`

实现所有用户的登录和退出，跳转到对应页面

## `StudentPage.vue`

### √选课

```
查询
method: queryCourses()
apiUrl = ${this.host}/user/selectCourseSchedule
```

```
选课：
method: selectCourses()
apiUrl = ${this.host}/user/courseSelect
```

### √退课

```
查询已选课程
method: fetchCourses()
apiUrl = ${this.host}/user/semesterCourseSelection
```

```
退课
method: dropCourses()
apiUrl = ${this.host}/user/courseWithdraw
```

### √查询成绩

```
查询成绩
method: fetchScores()
apiUrl = ${this.host}/user/selectScore
```

得到成绩数据后跳转到↓

#### `StudentQueryScore.vue`

展示学生成绩（包括分数转换成绩点，计算均绩【直接算平均值了】）

### √查询课表

```
查询已选课程
method: fetchCourses()
apiUrl = ${this.host}/user/semesterCourseSelection
```

得到课程数据后跳转到↓

#### `CourseSchedule.vue`

展示学生课表（转换成表格形式）

## `TeacherPage.vue`

### √查询开课详情

```
查询教师某学期开设的课程
method: fetchCourses()
apiUrl = ${this.host}/user/selectTeacherCourse
```

### √成绩登入

```
查询某门课程的所有学生
method: fetchStudents()
apiUrl = ${this.host}/user/selectTeacherCourseScore
```

```
更新成绩表
method: submitScore(rawSco)
apiUrl = ${this.host}/user/updateScore
```

## `AdminPages.vue`

管理员接口很多，选几个重要的写吧

### 添加用户

### 添加课程

### 添加开课计划（课程表）

### 查询用户

### 查询课程

### 查询开课计划（课程表）

### 选课

### 退课

### 更改成绩

### 查看某学生成绩

### 查看某教师某学期所带课程

