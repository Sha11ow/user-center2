<template>
  <div class="common-layout">
    <el-container>
      <el-header></el-header>
      <el-container>

        <el-main width="40%">
          <el-image style="width: 100%; height: 80%; user-select: none; pointer-events: none; margin-top: -40px;"
            :src="'/cover.png'" />
        </el-main>

        <el-aside width="40%" class="aside-content">

          <div class="content">

            <div class="container">
              <h1>教务管理系统</h1>

              <div class="circle-icons" style="margin-bottom: 10px;">
                <svg xmlns="http://www.w3.org/2000/svg" width="21" height="21" viewBox="0 0 21 21" fill="none">
                  <circle cx="10.5" cy="10.5" r="10.5" fill="#F34B4B" />
                </svg>
                <svg xmlns="http://www.w3.org/2000/svg" width="21" height="21" viewBox="0 0 21 21" fill="none">
                  <circle cx="10.5" cy="10.5" r="10.5" fill="#FFC700" />
                </svg>
                <svg xmlns="http://www.w3.org/2000/svg" width="21" height="21" viewBox="0 0 21 21" fill="none">
                  <circle cx="10.5" cy="10.5" r="10.5" fill="#1097F9" />
                </svg>
              </div>

              <div class="input-fields">
                <el-input v-model="userId" prefix-icon="el-icon-user" placeholder="请输入账号" style="margin-bottom: 20px;">
                  <template #prepend>
                    <span style="font-size: small;">账 号</span>
                  </template>
                </el-input>

                <el-input v-model="password" prefix-icon="el-icon-lock" placeholder="请输入密码" show-password
                  style="margin-bottom: 30px;">
                  <template #prepend>
                    <span style="font-size: small;">密 码</span>
                  </template>
                </el-input>
              </div>

              <div>
                <el-button type="primary" round @click="login">登录</el-button>
              </div>

            </div>
          </div>
        </el-aside>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import axios from "axios";
import { ElMessage } from 'element-plus'


export default {
  // define a component
  name: "IndexLogin",
  components: {

  },

  // data of the component
  data() {
    return {
      userId: "21000",
      password: "wdps9lmxXj",
      userName: "default",
      host: "http://127.0.0.1:9000",
    };
  },

  // methods of the component
  methods: {
    async login() {
    //异步方法
      const id = this.userId;
      const password = this.password;

      const apiUrl = `${this.host}/user/login`;

      const requestBody = {
        id,
        password,
      };

      try {
        // 发送 POST 请求
        const response = await axios.post(apiUrl, requestBody);

        if (response.data) {//收到数据
          ElMessage.success("登录成功");
          console.log("登录成功", response.data.role);
          // 根据用户角色跳转到相应页面
          switch (response.data.role) {
            case 1:
              this.$router.push({ name: 'students', params: { userId: id, userName: response.data.name } });
              break;
            case 2:
              this.$router.push({ name: 'teachers', params: { userId: id, userName: response.data.name } });
              break;
            case 0:
              this.$router.push({ name: 'admin', params: { userId: id, userName: response.data.name } });
              break;
            default:
              // 处理未知角色
              ElMessage.error("未知的用户角色");
          }
        } else {
          // 登录失败，没有接收到用户信息
          ElMessage.error("登录失败，请检查账号和密码是否正确");
        } 
      }catch (error) {
          // 网络或其他错误
          console.error("登录请求失败", error);
          ElMessage.error("登录请求失败，请稍后再试");
      }
    }
  },

}
</script>

<style scoped>
html,
body {
  height: 100%;
  overflow: hidden;
}


.common-layout {
  height: 90vh;  /* Adjust this value as needed */

  overflow: hidden;
}

.common-layout::-webkit-scrollbar {
  display: none;
}

::-webkit-scrollbar {
  width: 0 !important;
}

::-webkit-scrollbar {
  width: 0 !important;
  height: 0;
}

.aside-content {
  display: flex;
  flex-direction: column;
  margin-top: 4%;
}

.content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: #fff;
  padding: 20px;
  border-radius: 10px;
}

.container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-radius: 20px;
}

.input-fields {
  display: flex;
  flex-direction: column;
  width: 80%;
}



.circle-icons {
  display: flex;
  gap: 10px;
  align-items: center;
  justify-content: center;
  margin: 20px;
  margin-left: 15px;
  margin-bottom: 5px;
}
</style>
