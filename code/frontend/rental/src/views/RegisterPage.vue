<template>
  <div class="register-page">
    <h1>Register</h1>

    <!-- 注册表单 -->
    <form v-if="!isVerificationStep" class="register-form" @submit.prevent="register">
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
    <form v-else class="verification-form" @submit.prevent="confirmRegistration">
      <h2>Enter Verification Code</h2>
      <p>We have sent a verification code to your email. Please enter it below to complete the registration.</p>
      <div class="input-group">
        <label for="verificationCode">Verification Code:</label>
        <input type="text" v-model="verificationCode" id="verificationCode" class="input-field" required />
      </div>
      <button type="submit" class="btn-primary">Verify</button>

      <!-- 重新发送验证码 -->
      <p class="resend-code" @click="resendVerificationCode">Resend Verification Code</p>

      <!-- 错误信息 -->
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </form>

    <p class="login-prompt">
      Already have an account? <router-link to="/LoginPage" class="login-link">Login Here</router-link>
    </p>
  </div>
</template>

<script>
import { signUp, confirmSignUp, resendSignUpCode } from '@aws-amplify/auth';  // 使用 resendSignUpCode 重新发送验证码

export default {
  data() {
    return {
      email: '',             // 邮箱
      password: '',
      confirmPassword: '',
      verificationCode: '',  // 用户输入的验证码
      isVerificationStep: false,  // 控制是否显示验证码输入表单
      errorMessage: '',      // 存储错误信息
    };
  },
  created() {
    // 从 localStorage 中读取存储的邮箱，以防页面刷新后丢失邮箱
    const storedEmail = localStorage.getItem('userEmail');
    if (storedEmail) {
      this.email = storedEmail;
      this.isVerificationStep = true;  // 如果存在存储的邮箱，直接进入验证码步骤
    }
  },
  methods: {
    // 注册方法
    async register() {
      // 清除之前的错误消息
      this.errorMessage = '';

      // 检查密码和确认密码是否一致
      if (this.password !== this.confirmPassword) {
        this.errorMessage = 'Passwords do not match';
        return;
      }

      try {
        // 使用 Amplify 的 signUp 方法进行注册，使用邮箱作为用户名
        const { user } = await signUp({
          username: this.email,  // 使用邮箱作为用户名
          password: this.password,
          attributes: {
            email: this.email,    // 注册时传递的邮箱属性
          },
        });

        console.log('用户注册成功:', user);
        this.isVerificationStep = true;  // 显示验证码输入表单
        this.errorMessage = '';          // 清空错误信息

        // 将邮箱存储到 localStorage，以防页面刷新时丢失
        localStorage.setItem('userEmail', this.email);

      } catch (error) {
        console.error('注册时出错:', error);
        if (error.code === 'UsernameExistsException') {
          // 如果用户已存在，直接跳转到验证码输入页面
          this.isVerificationStep = true;
          this.errorMessage = 'User already exists. Please enter the verification code.';
          // 保存邮箱到 localStorage，以便验证码步骤中使用
          localStorage.setItem('userEmail', this.email);
          console.log('isVerificationStep 已设置为:', this.isVerificationStep);
        } else {
          this.errorMessage = error.message || '注册过程中发生错误';
        }
      }
    },

    // 确认验证码的方法
    async confirmRegistration() {
      // 清除之前的错误消息
      this.errorMessage = '';

      try {
        // 检查邮箱是否存在
        if (!this.email) {
          this.errorMessage = '邮箱不可为空。请重新注册。';
          return;
        }

        // 检查验证码是否为空
        if (!this.verificationCode) {
          this.errorMessage = '验证码不可为空。';
          return;
        }

        // 使用邮箱作为用户名进行确认
        await confirmSignUp(this.email, this.verificationCode);
        alert('注册已确认！您现在可以登录了。');
        this.$router.push('/LoginPage');  // 验证成功后跳转到登录页面
      } catch (error) {
        console.error('确认注册时出错:', error);
        this.errorMessage = error.message || '验证过程中发生错误';
      }
    },

    // 重新发送验证码的方法
    async resendVerificationCode() {
      // 清除之前的错误消息
      this.errorMessage = '';

      try {
        await resendSignUpCode(this.email);  // 调用 resendSignUpCode 方法
        alert('验证码已重新发送到您的邮箱。');
      } catch (error) {
        console.error('重新发送验证码时出错:', error);
        this.errorMessage = error.message || '重新发送验证码时发生错误';
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

/* 重新发送验证码的样式 */
.resend-code {
  margin-top: 10px;
  color: #2196F3;
  cursor: pointer;
  text-decoration: underline;
}
</style>
