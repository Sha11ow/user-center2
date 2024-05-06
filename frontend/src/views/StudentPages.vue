<template>
  <div class="common-layout">
    <el-container>

      <el-header class="top-bar">
        <el-row>

          <el-col :span="8">
            <div style="display: flex; align-items: center; justify-content: center; height: 100%;">
              <span class="text-xl font-bold">教务管理系统</span>
            </div>
          </el-col>

          <el-col :span="8" :offset="8">
            <div class="flex items-center justify-end h-full">
              <div style="display: flex; align-items: center; justify-content: center; height: 100%;">
                <el-avatar :size="32" class="mr-4"
                  src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                <span class="text-lg font-semibold mr-5" style="margin-left: 20px ; margin-right: 10px;">{{ this.userName
                }}</span>
                <span class="text-sm mr-4"
                  style="color: var(--el-text-color-regular); position: relative; top: 2px;margin-right: 10px;">{{
                    this.userId }}</span>
                <el-tag type="success" class="ml-2">学生</el-tag>
                <el-button type="text" style="margin-left: 20px;color: white; font-size: 16px;text-decoration: underline;" @click="logout">退出登录</el-button>
              </div>
            </div>
          </el-col>

        </el-row>
      </el-header>

      <el-form :model="semester" label-width="100px" style="margin-top: 20px;">
        <el-row>
          <el-col :span="8">
            <el-form-item label="学期" prop="semester">
                <el-select v-model="semester" placeholder="请选择学期" @change="fetchCourses(); fetchScores()">
                  <el-option label="2023年秋季学期" value="202303"></el-option>
                  <el-option label="2023年冬季学期" value="202304"></el-option>
                  <el-option label="2024年春季学期" value="202401"></el-option>
                </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-container>

        <el-aside width="200px">
          <el-menu default-active="1" class="el-menu-vertical-demo">
            <el-menu-item index="1" @click="selectFunction('选课')">选课</el-menu-item>
            <el-menu-item index="2" @click="selectFunction('退课'); fetchCourses()">退课</el-menu-item>
            <el-menu-item index="3" @click="selectFunction('成绩查询'); fetchCourses(); fetchScores()">成绩查询</el-menu-item>
            <el-menu-item index="4" @click="selectFunction('课表查询'); fetchCourses()">课表查询</el-menu-item>
          </el-menu>
        </el-aside>

        <el-main>
          <div class="main-content-right">

            <div v-if="selectedFunction === '选课'">
              <el-form :model="queryInfo" label-width="100px">
                <el-row>
                  <el-col :span="8">
                    <el-form-item label="课程号" prop="CourseId">
                      <el-input v-model="queryInfo.CourseId" placeholder="请输入课程号"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="教师号" prop="TeacherId">
                      <el-input v-model="queryInfo.TeacherId" placeholder="请输入教师号"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="上课时间" prop="CourseTime">
                      <el-input v-model="queryInfo.CourseTime" placeholder="请输入上课时间"></el-input>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>

              <div style="margin: 10px;">
                <el-button type="primary" @click="queryCourses">提交</el-button>
              </div>

              <div v-if="showForm">
                <el-table :data="courseInfo" style="width: 100%" @selection-change="handleSelectionChange">
                  <el-table-column type="selection"></el-table-column>
                  <el-table-column prop="course_id" label="课程号"></el-table-column>
                  <el-table-column prop="course_name" label="课程名"></el-table-column>
                  <el-table-column prop="teacher_id" label="教师号"></el-table-column>
                  <el-table-column prop="teacher_name" label="教师姓名"></el-table-column>
                  <el-table-column prop="time" label="上课时间"></el-table-column>
                  <el-table-column prop="capacity" label="课程余量"></el-table-column>
                </el-table>
                <div style="margin: 10px;">
                  <el-button type="primary" @click="selectCourses">确认选课</el-button>
                </div>
              </div>
            </div>

            <div v-else-if="selectedFunction === '退课'">
              <div>
                <el-table :data="myCourses" style="width: 100%" @selection-change="handleSelectionChange_delete">
                  <el-table-column type="selection"></el-table-column>
                  <el-table-column prop="course_id" label="课程号"></el-table-column>
                  <el-table-column prop="course_name" label="课程名"></el-table-column>
                  <el-table-column prop="teacher_id" label="教师号"></el-table-column>
                  <el-table-column prop="teacher_name" label="教师姓名"></el-table-column>
                  <el-table-column prop="time" label="上课时间"></el-table-column>
                  <el-table-column prop="capacity" label="课程余量"></el-table-column>
                </el-table>
                <div style="margin: 10px;">
                  <el-button type="primary" @click="dropCourses">退选所选课程</el-button>
                </div>
              </div>
            </div>

            <div v-else-if="selectedFunction === '成绩查询'">
              <StudentQueryScore :myCourses="myScores"></StudentQueryScore>
            </div>

            <div v-else-if="selectedFunction === '课表查询'">
              <CourseSchedule :myCourses="myCourses"></CourseSchedule>
            </div>

          </div>
        </el-main>

      </el-container>
    </el-container>
  </div>
</template>

<script>
import axios from "axios";
axios.defaults.withCredentials = true;

import StudentQueryScore from "./StudentQueryScore.vue";
import CourseSchedule from "../components/CourseSchedule.vue";

import { ElMessage } from 'element-plus'

export default {
  name: "StudentPages",
  components: {
    StudentQueryScore,
    CourseSchedule
  },

  // 在created生命周期钩子中访问路由参数
  created() {
    this.userId = this.$route.params.userId;
    this.userName = this.$route.params.userName;
  },

  // data()函数部分
  data() {
    return {
      host: "http://127.0.0.1:9000",
      selectedFunction: "选课", // 默认选中的功能
      semester: "202401", // 默认学期

      // 选课功能中的输入框
      showForm: false,

      //选课（查询）————可选课程的查询条件
      queryInfo: {
        Semester: "202401",
        CourseId: null,
        TeacherId: null,
        Time: null,
        Capacity: null,
      },

      // 选课（查询）————满足查询条件的课程信息
      courseInfo: [{
        semester: "202401", 
        course_id: "course_id",
        course_name: "course_name",
        teacher_id: "teacher_id",
        teacher_name: "teacher_name",
        time: "time",
        capacity: 0,
      }],

      // 选课(选择)————选中的课程号
      selectedCourse: [{
        course_id: "course_id",
        course_name: "course_name",
        teacher_id: "teacher_id",
        teacher_name: "teacher_name",
        time: "time",
        capacity: 0,
      }],

      // 退课（选择）————选中的课程号
      deletedCourse: [{
        course_id: "course_id",
        course_name: "course_name",
        teacher_id: "teacher_id",
        teacher_name: "teacher_name",
        time: "time",
        capacity: 0,
      }],

      // 学生已经选的课程
      myCourses: [{
        course_id: "",
        course_name: "",
        teacher_id: "",
        teacher_name: "",
        time: "",
        capacity: 0,
      }],

      // 学生成绩信息
      myScores: [{
        course_id: "",
        course_name: "",
        score: 0
      }]
    };
  },

  methods: {
    // 退出登录
    logout() {
      this.$router.push({ name: 'home' });  // 导航回首页
    },

    // 选择功能
    selectFunction(functionName) {
      this.selectedFunction = functionName;
    },

    // 查询课程名
    async getCourseName(courseId) {
      const courseNameApiUrl = `${this.host}/user/selectCourse`;
      const course = {
        course_id: courseId,
      };
      const response = await axios.post(courseNameApiUrl, course);
      if (response.data != null) {
          //ElMessage.success('课程名查询成功');
      }
      else {
        ElMessage.error('课程名查询失败');
      }
      return response.data.course_name;
    },

    // 查询教师名
    async getTeacherName(teacherId) {
      const teacherNameApiUrl = `${this.host}/user/selectUserById`;
      const user = {
        id: teacherId,
        password: null,
        name: null,
        role: null,
      };
      const response = await axios.post(teacherNameApiUrl,user);
      if (response.data != null) {
        //ElMessage.success('教师名查询成功');
      }
      else {
        ElMessage.error('教师名查询失败');
      }
      return response.data.name;
    },

    // 查询课程功能
    async queryCourses() {
      const semester = this.semester;
      const course_id = this.queryInfo.CourseId;
      const teacher_id = this.queryInfo.TeacherId;
      const time = this.queryInfo.Time;
      const capacity = this.queryInfo.Capacity;

      const apiUrl = `${this.host}/user/selectCourseSchedule`;
      const courseSchedule = {
        semester: semester,
        course_id: course_id,
        teacher_id: teacher_id,
        time: time,
        capacity: capacity,
      };

      try {
        const response = await axios.post(apiUrl, courseSchedule);
        const courseData = response.data;

        if (courseData != null) {
          ElMessage.success('选课信息查询成功');
          
          console.log("queryCourses method return response.data", courseData);
          console.log(typeof courseData);

          const courseNamePromises = [];
          const teacherNamePromises = [];

          courseData.forEach((course) => {
            courseNamePromises.push(this.getCourseName(course.course_id));
            teacherNamePromises.push(this.getTeacherName(course.teacher_id));
          });

          const courseNames = await Promise.all(courseNamePromises);
          const teacherNames = await Promise.all(teacherNamePromises);

          const courseInfo = courseData.map((course, index) => {
            return {
              semester: course.semester,
              course_id: course.course_id,
              course_name: courseNames[index],
              teacher_id: course.teacher_id,
              teacher_name: teacherNames[index],
              time: course.time,
              capacity: course.capacity,
            };
          });
          this.courseInfo = courseInfo;
          console.log("courseInfo", courseInfo);

          this.showForm = true;
        } else {
          ElMessage.error('选课信息查询失败');
        }
      } catch (error) {
        // 处理响应失败的情况
        console.error("选课信息查询失败", error);
        ElMessage.error('选课信息查询失败');
      }
    },

    // 更新选课功能中的选中课程到selectedCourse
    handleSelectionChange(selectedRows) {
      console.log("选中的课程 selectedRows:", selectedRows);
      this.selectedCourse = selectedRows;
      console.log("selectedCourse:", this.selectedCourse);
    },

    // 选课功能
    async selectCourses() {
      try {
        const requestBody = {
          semester: this.semester,
          student_id: this.userId,
          course_id: this.selectedCourse[0].course_id,
          teacher_id: this.selectedCourse[0].teacher_id,
          time: this.selectedCourse[0].time,
          capacity: this.selectedCourse[0].capacity,
        };
        console.log("选课请求发送的 requestBody", requestBody);

        const apiUrl = `${this.host}/user/courseSelect`;
        const response = await axios.post(apiUrl, requestBody);

        console.log("selectCourses return response: ", response);
        if (response.data === true) {
          ElMessage.success("选课成功");
          this.selectedCourse = []; // 清空已选课程
          this.fetchCourses(); // 重新查询课表
        }
        else {
          ElMessage.error("选课失败：" + response.message);
        }
      } catch (error) {
        console.error("选课操作失败", error);
        ElMessage.error("选课操作失败");
      }
    },

    // 查询已选课程
    async fetchCourses() {
      const apiUrl = `${this.host}/user/semesterCourseSelection`;
      const requestBody = {
        semester: this.semester,
        course_id: null,
        teacher_id: null,
        student_id: this.userId,
      };

      try {
        const response = await axios.post(apiUrl, requestBody);
        const courseData = response.data;
        console.log("查询已选课程接收到的response.data", response.data);

        if (courseData != null) {
          ElMessage.success('已选课程信息查询成功');
          console.log("courseData", courseData);

          const courseNamePromises = [];
          const teacherNamePromises = [];

          courseData.forEach((course) => {
            courseNamePromises.push(this.getCourseName(course.course_id));
            teacherNamePromises.push(this.getTeacherName(course.teacher_id));
          });

          const courseNames = await Promise.all(courseNamePromises);
          const teacherNames = await Promise.all(teacherNamePromises);

          const myCourses = courseData.map((course, index) => {
            return {
              course_id: course.course_id,
              course_name: courseNames[index],
              teacher_id: course.teacher_id,
              teacher_name: teacherNames[index],
              time: course.time,
              capacity: course.capacity,
            };
          });
          this.myCourses = myCourses;
          console.log("myCourses", myCourses);
        } else {
          ElMessage.error('已选课程信息查询失败');
        }
      } catch (error) {
        console.error("课表信息查询失败", error);
        ElMessage.error("课表信息查询失败");
      }
    },

    // 更新退课功能中的选中课程到deletedCourses
    handleSelectionChange_delete(selectedRow) {
      console.log("选中的课程 selectedRow:", selectedRow);
      this.deletedCourse = selectedRow;
    },

    // 退课功能
    async dropCourses() {
      try {
        const requestBody = {
          course_id: this.deletedCourse[0].course_id,
          semester: this.semester,
          student_id: this.userId,
          teacher_id: this.deletedCourse[0].teacher_id,
          time: this.deletedCourse[0].time,
          capacity: this.deletedCourse[0].capacity,
        };

        console.log("退课请求发送的 requestBody", requestBody);

        const apiUrl = `${this.host}/user/courseWithdraw`;
        const response = await axios.post(apiUrl, requestBody);

        console.log("courseWithdraw return response: ", response);
        
        if (response.data === true) {
          ElMessage.success("退课成功");
          this.deletedCourse = []; // 清空已选课程
          this.fetchCourses(); // 重新查询课表
        }
        else {
          ElMessage.error("退课失败：" + response.message);
        }
      } catch (error) {
        console.error("退课操作失败", error);
        ElMessage.error("退课操作失败");
      }
    },

    // 成绩查询功能
    async fetchScores() {
      const apiUrl = `${this.host}/user/selectScore`;

      const requestBody = {
        semester: this.semester,
        student_id: this.userId,
      };

      try {
        const response = await axios.post(apiUrl, requestBody);
        console.log("查询到的该学生这学期的所有成绩", response.data);
        const scoreData = response.data;
        
        if (scoreData != null) {
          ElMessage.success("成绩信息查询成功");
          console.log("scoreData", scoreData);

          const courseNamePromises = [];
          scoreData.forEach((score) => {
            courseNamePromises.push(this.getCourseName(score.course_id));
          });
          const courseNames = await Promise.all(courseNamePromises);
          const myScores = scoreData.map((score, index) => {
            return {
              course_id: score.course_id,
              course_name: courseNames[index],
              score: score.score,
            };
          });
          this.myScores = myScores;
          console.log("this.myScores", this.myScores);
        }
        else {
          ElMessage.error("成绩信息查询失败");
          return;
        }
      } catch (error) {
        console.error("成绩信息查询失败", error);
        ElMessage.error("成绩信息查询失败");
      }
    },

    mounted() {
      this.fetchCourses();
      this.showForm = false; // 隐藏表单组件
    }
  },
};
</script>

<style>
.top-bar {
  background: #208fcb;
  color: #fff;
  padding: 10px 20px;
  text-align: center;
  border-radius: 10px;
}

.top-bar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-name {
  font-weight: bold;
}

.main-content {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
}

.sidebar {
  margin-top: 10px;
  width: 100px;
  background: #aee3ed;
  padding: 10px;
  border-radius: 10px;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  cursor: pointer;
  padding: 5px;
  border-bottom: 1px solid #ccc;
}

.main-content-right {
  flex: 1;
  padding: 20px;
}

.input-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

label {
  font-weight: bold;
  font-size: 14px;
  color: #333;
}

input {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
}


.course-table {
  margin-top: 20px;
  margin-bottom: 20px;
  border-collapse: collapse;
  font-family: Arial, sans-serif;
  background-color: #f2f2f2;
  width: 100%;
}

.course-table th,
.course-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.course-table th {
  background-color: #8ac9e2;
  color: white;
}
</style>
  