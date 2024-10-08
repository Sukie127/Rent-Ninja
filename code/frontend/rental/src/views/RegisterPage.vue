<script setup>
import { Authenticator } from "@aws-amplify/ui-vue";
import { Amplify } from "aws-amplify";
import "@aws-amplify/ui-vue/styles.css";
import { useRouter } from "vue-router"; // 引入 Vue Router

const router = useRouter(); // 使用 Vue Router

// 当前 Amplify 配置
const currentConfig = Amplify.getConfig();
Amplify.configure({
  ...currentConfig,
  API: {
    endpoints: [
      {
        name: "RentalNinja",
        endpoint: "https://api.rentalninja.link",
        region: "us-east-2",
      },
    ],
  },
});

// 跳转到自定义登录页面
function redirectToCustomLogin() {
  console.log("Redirecting to LoginPage...");
  router.push("/LoginPage"); // 重定向到自定义的登录页面
}
</script>

<template>
  <div class="auth-container">
    <h2 class="register-title">Register</h2>
    <authenticator initial-state="signUp">
      <template v-slot="{ user, signOut }">
        <h1>Hello {{ user.username }}!</h1>
        <button @click="signOut" class="custom-button">Sign Out</button>
        <button @click="redirectToCustomLogin" class="custom-button">Sign In</button>
      </template>
    </authenticator>
  </div>
</template>
<style>
/* 页面整体布局 */
.auth-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  padding-top: 50px;
}

/* 标题样式 */
.register-title {
  font-size: 2rem;
  margin-bottom: 20px;
  color: #333;
  text-align: center;
}

/* 强制覆盖 Sign In 和 Create Account 选项卡的边框颜色 */
button.amplify-tabs__item {
  border-bottom: 3px solid #4CAF50 !important; /* 绿色边框 */
}

/* 自定义按钮样式 */
button.amplify-button {
  background-color: #4CAF50 !important;
  color: white !important;
  padding: 10px 20px !important;
  border: none !important;
  border-radius: 5px !important;
  cursor: pointer !important;
  margin-top: 10px !important;
  transition: background-color 0.3s ease !important;
}

button.amplify-button:hover {
  background-color: #45a049 !important;
}

/* 强制调整输入框的边框颜色 */
input.amplify-input {
  border: 2px solid #4CAF50 !important;
  border-radius: 5px !important;
  padding: 10px !important;
  width: 100%;
}

/* 修正眼睛图标的对齐与功能 */
button.amplify-field__show-password {
  background-color: #4CAF50 !important;
  border: none !important;
  border-radius: 5px !important;
  height: 40px !important;
  width: 40px !important;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

button.amplify-field__show-password:hover {
  background-color: #45a049 !important;
}
</style>