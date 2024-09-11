<template>
  <div class="register-page">
    <h1>Register</h1>

    <form class="register-form" @submit.prevent="register">
      <!-- 用户名 -->
      <div class="input-group">
        <label for="username">Username:</label>
        <input type="text" v-model="username" id="username" class="input-field" required />
      </div>

      <!-- 邮箱 -->
      <div class="input-group">
        <label for="email">Email:</label>
        <input type="email" v-model="email" id="email" class="input-field" required />
      </div>

      <!-- 密码 -->
      <div class="input-group">
        <label for="password">Password:</label>
        <input type="password" v-model="password" id="password" class="input-field" required />
      </div>

      <!-- 确认密码 -->
      <div class="input-group">
        <label for="confirmPassword">Confirm Password:</label>
        <input type="password" v-model="confirmPassword" id="confirmPassword" class="input-field" required />
      </div>

      <button type="submit" class="btn-primary">Register</button>
    </form>

    <p class="login-prompt">
      Already have an account? <router-link to="/LoginPage" class="login-link">Login Here</router-link>
    </p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
    };
  },
  methods: {
    register() {
      // 表单验证逻辑
      if (this.password !== this.confirmPassword) {
        alert('Passwords do not match');
        return;
      }

      // 注册逻辑
      if (this.username && this.email && this.password) {
        alert('Registration successful');
        this.$router.push('/login'); // 注册成功后跳转到登录页面
      } else {
        alert('Please fill in all fields');
      }
    },
  },
};
</script>

<style scoped>
/* 页面布局，内容居中 */
.register-page {
  padding: 60px 20px;
  max-width: 400px;
  margin: 0 auto;
  text-align: center;
}

/* 输入组的布局 */
.input-group {
  margin-bottom: 20px;
}

/* 统一的输入框样式 */
.input-field {
  padding: 10px;
  border: 2px solid #4CAF50;
  border-radius: 5px;
  width: 100%;
}

/* 统一的按钮样式 */
button {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #45a049;
}

/* 注册提示信息 */
.login-prompt {
  margin-top: 20px;
}

.login-link {
  color: #2196F3;
  text-decoration: none;
}

.login-link:hover {
  text-decoration: underline;
}
</style>
