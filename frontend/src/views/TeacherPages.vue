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
                <span class="text-lg font-semibold mr-5" style="margin-left: 20px ; margin-right: 10px;">{{
                  this.userName }}</span>
                <span class="text-sm mr-4"
                  style="color: var(--el-text-color-regular); position: relative; top: 2px;margin-right: 10px;">{{
                    this.userId }}</span>
                <el-tag type="success" class="ml-2">老师</el-tag>
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
                <el-select v-model="semester" placeholder="请选择学期" @change="fetchCourses">
                  <el-option label="2023年秋季学期" value="202303"></el-option>
                  <el-option label="2023年冬季学期" value="202304"></el-option>
                  <el-option label="2024年春季学期" value="202401"></el-option>
                </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div class="main-content">

        <el-aside width="200px">
          <el-menu default-active="1" class="el-menu-vertical-demo">
            <el-menu-item index="1" @click="selectFunction('开课详情'); fetchCourses()">开课详情</el-menu-item>
            <el-menu-item index="2" @click="selectFunction('成绩录入')">成绩录入</el-menu-item>
          </el-menu>
        </el-aside>

        <div class="main-content-right">

          <div v-if="selectedFunction === '开课详情'">
            <el-table :data="myCourses" style="width: 100%">
              <el-table-column prop="course_id" label="课程号" />
              <el-table-column prop="course_name" label="课程名" />
              <el-table-column prop="teacher_id" label="教师号" />
              <el-table-column prop="teacher_name" label="教师姓名" />
              <el-table-column prop="time" label="上课时间" />
              <el-table-column prop="capacity" label="课程余量" />
            </el-table>
          </div>

          <div v-else-if="selectedFunction === '成绩录入'">
            <!--根据开课详情中的班级信息，可以进行筛选，录入成绩-->
            <!--根据courseInfo的课程名字做一个选择框-->
            <div style="margin: 20px;">
              <label for="course-select">选择课程班级：</label>
              <el-select v-model="selectedCourse" class="m-2" placeholder="">
                <el-option v-for="course in myCourses" :key="course.course_id" :label="course - select"
                  :value="course.course_id" />
              </el-select>

              <!--对选中的selectedCourse进行查询，返回数据给tableData-->
              <el-table :data="tableData" stripe style="width: 100%">
                <el-table-column prop="student_id" label="学号" width="180" />
                <el-table-column prop="student_name" label="姓名" width="180" />
                <el-table-column label="成绩">
                  <template v-slot="scope">
                    <template v-if="scope.row">
                      <el-row>
                        <el-col :span="8">
                          <el-input v-model="scope.row.score" class="w-80" placeholder="成绩" id="score-input" />
                        </el-col>
                        <el-col :span="4">
                          <el-button type="primary" @click="submitScore(scope.row)" style="margin-left: 20px;">保存</el-button>
                        </el-col>
                      </el-row>
                    </template>
                  </template>
                </el-table-column>
              </el-table>
            </div>

          </div>

          <div v-else-if="selectedFunction === '成绩分析'">
          </div>

          <div v-else-if="selectedFunction === '开设课程'">
          </div>

        </div>
      </div>
    </el-container>
  </div>
</template>
    
<script>
import axios from "axios";
axios.defaults.withCredentials = true;
import { ElMessage } from 'element-plus'



export default {
  name: "TeacherPages",
  components: {

  },

  // 来自父组件的数据
  props: {
  },

  // 在created生命周期钩子中访问路由参数
  created() {
    this.userId = this.$route.params.userId;
    this.userName = this.$route.params.userName;

    this.fetchCourses();
  },

  // data()函数部分
  data() {
    return {
      host: "http://127.0.0.1:9000",
      semester: "202401", // 默认学期
      selectedFunction: "开课详情", // 默认选中的功能
      selectedCourse: "请选择班级", // 默认选中的课程

      // 已经选的课程
      myCourses: [{
        course_id: "",
        course_name: "",
        teacher_id: "",
        teacher_name: "",
        capacity: 0,
        time: "time",
      }],

      // 上传成绩表格需要的信息
      tableData: [{
        course_id: "",
        teacher_id: "",
        student_id: "",
        student_name: "",
        score: 0,
      }],

      SubmitData: [
        {
          student_id: "",
          score: 0,
        },
      ]
    };
  },

  watch: {
    selectedCourse() {
      // 调用 fetchStudents 方法，更新 tableData 数组中的数据
      this.fetchStudents();
    }
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

    // 查询教师名/学生名
    async getUserName(userId) {
      const userNameApiUrl = `${this.host}/user/selectUserById`;
      const user = {
        id: userId,
        password: null,
        name: null,
        role: null,
      };
      const response = await axios.post(userNameApiUrl,user);
      if (response.data != null) {
        //ElMessage.success('教师名查询成功');
      }
      else {
        ElMessage.error('用户姓名查询失败');
      }
      return response.data.name;
    },


    // 查询该教师已经开设的课程
    async fetchCourses() {
      const apiUrl = `${this.host}/user/selectTeacherCourse`;
      const requestBody = {
        semester: this.semester,
        course_id: null,
        teacher_id: this.userId,
        student_id: null,
      };
      try {
        const response = await axios.post(apiUrl,requestBody);
        console.log("查询到的开课信息, response: ", response.data);
        const courseData = response.data;

        if(courseData != null) {
          console.log("courseData", courseData);
          console.log(typeof courseData);

          const courseNamePromises = [];
          const teacherNamePromises = [];

          courseData.forEach((course) => {
            courseNamePromises.push(this.getCourseName(course.course_id));
            teacherNamePromises.push(this.getUserName(course.teacher_id));
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
          ElMessage.error('开课信息查询失败');
        }
      } catch (error) {
        console.error("开设信息查询失败", error);
        ElMessage.error("开设信息查询失败");
      }
    },

    /* 
    通过watch部分关联，每次完成选择后自动调用 fetchStudents 方法，并更新 tableData 数组中的数据
    查询该教师和他开设的所有课程的信息返回到tableData中
    */
    async fetchStudents() {
      const apiUrl = `${this.host}/user/selectTeacherCourseScore`;
      console.log("this.selectedCourse", this.selectedCourse);
      const requestBody = {
        semester: this.semester,
        course_id: this.selectedCourse,
        teacher_id: this.userId,
        student_id: null,
      };

      try {
        const response = await axios.post(apiUrl, requestBody);
        const scoreData = response.data;
        console.log("查询选择该课程的所有学生接收到的response.data", response.data);

        if (scoreData != null) {
          ElMessage.success('学生信息查询成功');
          console.log("scoreData", scoreData);

          const studentNamePromises = [];
          scoreData.forEach((score) => {
            studentNamePromises.push(this.getUserName(score.student_id));
          });
          const studentNames = await Promise.all(studentNamePromises);
          const tableData = scoreData.map((score, index) => {
            return {
              course_id: score.course_id,
              teacher_id: this.userId,
              student_id: score.student_id,
              student_name: studentNames[index],
              score: score.score,
            };
          });
          this.tableData = tableData;
          console.log("tableData", tableData);
        } else {
          ElMessage.error('已选课程信息查询失败');
        }
      } catch (error) {
        console.error("课表信息查询失败", error);
        ElMessage.error("课表信息查询失败");
      }
    },

    // 上传成绩
    async submitScore(rawSco) {
      const apiUrl = `${this.host}/user/updateScore`; //调用的是update接口，因为选课的时候已经新增记录了
      try {
        const requestBody = {
            course_id: this.selectedCourse,
            student_id: rawSco.student_id,
            new_score: rawSco.score,
            semester: this.semester,
        };

        console.log("requestBody", requestBody);

        const response = await axios.post(apiUrl, requestBody);

        if (response.data != null) {
          console.log("return from fetchCourses, response:", response);
          ElMessage.success("成绩上传成功");
        }
        else {
          ElMessage.error("成绩上传失败");
        }
      }
      catch (error) {
        console.error("成绩上传失败", error);
        ElMessage.error("成绩上传失败");
      }
    },
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
    