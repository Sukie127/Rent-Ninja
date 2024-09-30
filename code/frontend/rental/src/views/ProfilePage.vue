<template>
  <div class="profile-page">
    <h1>Profile Page</h1>

    <!-- 个人资料 -->
    <section class="profile-section">
      <h2>Profile Information</h2>
      <div class="profile-info">
        <div class="avatar">
          <img :src="profile.avatar" alt="Avatar" class="profile-avatar" />
          <input type="file" @change="onAvatarChange" />
        </div>
        <div class="details">
          <label for="name">Name:</label>
          <input type="text" v-model="profile.name" class="input-field" />
          <label for="email">Email:</label>
          <input type="email" v-model="profile.email" class="input-field" />
          <button @click="saveProfile" class="btn-primary">Save Changes</button>
        </div>
      </div>
    </section>

    <!-- 已发布的房源 -->
    <section class="published-listings-section">
      <h2>Published Listings</h2>
      <div v-if="publishedListings.length === 0">
        <p>You haven't published any listings yet.</p>
      </div>
      <div v-else class="published-list">
        <div class="published-item" v-for="(listing, index) in publishedListings" :key="index">
          <img :src="listing.image" alt="Listing Image" />
          <h3>{{ listing.title }}</h3>
          <p>{{ listing.description }}</p>
          <div class="button-group">
            <button @click="editListing(listing.id)" class="btn-secondary">Edit</button>
            <button @click="removeListing(index)" class="btn-danger">Delete</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 发布新房源 -->
    <section class="new-listing-section">
      <h2 class="center-text">Post New Listing</h2>
      <form @submit.prevent="submitNewListing" class="new-listing-form">
        <div class="input-group">
          <label for="title">Title:</label>
          <input type="text" v-model="newListing.title" class="input-field" required />
        </div>

        <div class="input-group">
          <label for="description">Description:</label>
          <textarea v-model="newListing.description" class="input-field" required></textarea>
        </div>

        <div class="input-group">
          <label for="price">Price:</label>
          <input type="number" v-model="newListing.price" class="input-field" required />
        </div>

        <div class="input-group">
          <label for="location">Location:</label>
          <input type="text" v-model="newListing.location" class="input-field" required />
        </div>

        <div class="input-group">
          <label for="type">Listing Type:</label>
          <select v-model="newListing.type" class="input-field" required>
            <option value="">Select Type</option>
            <option value="Apartment">Apartment</option>
            <option value="House">House</option>
            <option value="Room">Room</option>
          </select>
        </div>

        <div class="input-group">
          <label for="image">Listing Image:</label>
          <input type="file" @change="onImageChange" />
        </div>

        <div class="input-group">
          <label for="contact-info">Contact Information:</label>
          <input type="text" v-model="newListing.contactInfo" class="input-field" required />
        </div>

        <button type="submit" class="btn-primary">Post Listing</button>
      </form>
    </section>

    <!-- Logout按钮 -->
    <button @click="logout" class="btn-logout">Logout</button> <!-- 挪到右上角并缩小 -->
  </div>
</template>

<script>
import { jwtDecode } from 'jwt-decode'; // 引入 jwt-decode 库
import { signOut } from '@aws-amplify/auth'; // 引入 Amplify 退出登录功能

export default {
  data() {
    return {
      profile: {
        avatar: require('@/assets/1.png'), // 默认头像
        name: 'User Name',
        email: 'user@example.com',
      },
      favoriteListings: [
        {
          id: 1,
          image: require('@/assets/1.png'),
          title: 'Deluxe Apartment',
          description: 'Conveniently located near the city center.',
        },
        {
          id: 2,
          image: require('@/assets/2.png'),
          title: 'Luxury Villa',
          description: 'A high-end villa with a private pool and garden.',
        },
      ],
      publishedListings: [
        {
          id: 3,
          image: require('@/assets/3.png'),
          title: 'Luxury Apartment in City Center',
          description: 'Modern renovation, fully equipped.',
        },
        {
          id: 4,
          image: require('@/assets/4.png'),
          title: 'Cozy Single Room Near Metro Station',
          description: 'Convenient location with nearby facilities.',
        },
      ],
      newListing: {
        title: '',
        description: '',
        price: '',
        location: '',
        type: '',
        contactInfo: '',
        image: null,
      },
    };
  },
  created() {
    this.fetchUserProfile(); // 组件创建时获取用户信息
  },
  methods: {
    // 获取用户信息
    fetchUserProfile() {
      const idToken = localStorage.getItem('idToken'); // 从 localStorage 获取 idToken
      if (idToken) {
        try {
          const decodedToken = jwtDecode(idToken); // 使用 jwtDecode 解码 token
          this.profile.name = decodedToken['cognito:username'] || 'User';
          this.profile.email = decodedToken.email || 'user@example.com';
        } catch (error) {
          console.error('Token 解码失败', error);
        }
      } else {
        console.error('idToken 不存在，请确保用户已登录');
      }
    },

    // 退出登录
    async logout() {
      try {
        await signOut(); // 调用 AWS Amplify 的 signOut 方法
        localStorage.removeItem('idToken'); // 清除本地存储的 idToken
        this.$router.push('/loginPage'); // 退出后跳转到登录页面
      } catch (error) {
        console.error('退出登录失败:', error);
      }
    },

    // 上传图片到预签名URL
    async uploadImage(file) {
      try {
        const token = localStorage.getItem('idToken'); // 获取 token
        if (!token) {
          throw new Error('请确保您已登录并获取 token');
        }

        // 请求预签名 URL
        const presignedResponse = await fetch('/api/get-presigned-url', { // 使用代理后的后端 URL
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`, // 添加 token 进行认证
          },
        });

        if (!presignedResponse.ok) {
          throw new Error('获取预签名URL失败');
        }

        const { url } = await presignedResponse.json(); // 获取预签名的URL
        const uploadResponse = await fetch(url, {
          method: 'PUT',
          body: file,
        });

        if (!uploadResponse.ok) {
          throw new Error('图片上传失败');
        }

        return url.split('?')[0]; // 返回上传后的图片URL

      } catch (error) {
        console.error('图片上传出错:', error);
        throw error;
      }
    },

    // 提交房源信息
    async submitNewListing() {
      try {
        const token = localStorage.getItem('idToken');  
        if (!token) {
          throw new Error('请确保您已登录并获取 token');
        }

        const decodedToken = jwtDecode(token);
        const userId = decodedToken['sub'];  // 获取用户ID

        let imageUrl = '';
        if (this.newListing.image) {
          imageUrl = await this.uploadImage(this.newListing.image);
        }

        const postData = {
          post_id: Date.now(),  
          area: this.newListing.location,  
          contact_info: this.newListing.contactInfo, 
          content: this.newListing.description, 
          pic_urls: [imageUrl], 
          title: this.newListing.title,  
          type: this.newListing.type,  
          price: this.newListing.price,  
          user_id: userId,  // 动态获取的用户ID
        };

        const response = await fetch('/api/upload-post', { // 使用代理后的后端 URL
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,  
          },
          body: JSON.stringify(postData),
        });

        if (!response.ok) {
          throw new Error('发布房源失败，请重试');
        }

        const newListing = await response.json();

        this.publishedListings.push({
          id: newListing.post_id,
          image: newListing.pic_urls[0],  // 返回的图片URL
          title: newListing.title,
          description: newListing.content,
          location: newListing.area,
          contactInfo: newListing.contact_info,
        });

        // 清空表单
        this.newListing = {
          title: '',
          description: '',
          price: '',
          location: '',
          type: '',
          contactInfo: '',
          image: null,
        };

        alert('Listing posted successfully');
      } catch (error) {
        console.error('房源发布出错:', error);
        alert(error.message || '发布房源时出现问题');
      }
    },

    // 处理图片选择
    onImageChange(event) {
      const file = event.target.files[0];
      this.newListing.image = file;
    },

    removeFromFavorites(index) {
      this.favoriteListings.splice(index, 1);
      alert('Removed from favorites');
    },

    editListing(id) {
      this.$router.push({ name: 'EditListing', params: { id } });
    },

    removeListing(index) {
      this.publishedListings.splice(index, 1);
      alert('Listing deleted');
    },
  },
};
</script>

<style scoped>


</style>
