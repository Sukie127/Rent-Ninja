<template>
  <div class="register-page">
    <h1>Register</h1>

    <!-- 注册表单 -->
    <form v-if="!isVerificationStep" class="register-form" @submit.prevent="register">
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

      <!-- 错误信息 -->
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </form>

    <!-- 验证码表单 -->
    <form v-if="isVerificationStep" class="verification-form" @submit.prevent="confirmRegistration">
      <h2>Enter Verification Code</h2>
      <p>We have sent a verification code to your email. Please enter it below to complete the registration.</p>
      <div class="input-group">
        <label for="verificationCode">Verification Code:</label>
        <input type="text" v-model="verificationCode" id="verificationCode" class="input-field" required />
      </div>
      <button type="submit" class="btn-primary">Verify</button>

      <!-- 错误信息 -->
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </form>

    <p class="login-prompt">
      Already have an account? <router-link to="/LoginPage" class="login-link">Login Here</router-link>
    </p>
  </div>
</template>

<script>
import { signUp, confirmSignUp } from '@aws-amplify/auth';  // 引入注册和确认注册的方法

export default {
  data() {
    return {
      username: '',  // 恢复使用用户名
      email: '',
      password: '',
      confirmPassword: '',
      verificationCode: '',  // 用于存储用户输入的验证码
      isVerificationStep: false,  // 控制是否显示验证码输入表单
      errorMessage: '',  // 用于存储错误信息
    };
  },
  methods: {
    // 注册方法
    async register() {
      // 检查密码和确认密码是否一致
      if (this.password !== this.confirmPassword) {
        this.errorMessage = 'Passwords do not match';
        return;
      }

      try {
        // 使用 Amplify 的 signUp 方法进行注册，恢复使用用户名
        const { user } = await signUp({
          username: this.username,  // 使用用户名进行注册
          password: this.password,
          attributes: {
            email: this.email,  // 注册时传递的邮箱属性
          },
        });

        console.log('User registered successfully:', user);
        this.isVerificationStep = true;  // 显示验证码输入表单
        this.errorMessage = '';  // 清空错误信息
      } catch (error) {
        console.error('Error during registration:', error);
        this.errorMessage = error.message || 'An error occurred during registration';
      }
    },

    // 确认验证码的方法
    async confirmRegistration() {
      try {
        // 使用 confirmSignUp 方法确认用户注册，使用用户名
        await confirmSignUp(this.username, this.verificationCode);
        alert('Registration confirmed! You can now log in.');
        this.$router.push('/LoginPage');  // 验证成功后跳转到登录页面
      } catch (error) {
        console.error('Error during confirmation:', error);
        this.errorMessage = error.message || 'An error occurred during verification';
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

/* 错误信息样式 */
.error-message {
  color: red;
  margin-top: 20px;
}
</style>
