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

    <!-- posted list -->
    <section class="published-listings-section">
      <h2>My Posted Listings</h2>
      
      <!-- Display message when there are no published listings -->
      <div v-if="postedList.length === 0">
        <p>You haven't published any listings yet.</p>
      </div>

      <!-- Published Listings -->
      <div v-else class="listing-grid">
        <div class="listing-card" v-for="listing in postedList" :key="listing.postId">
          
        <div v-if="listing.picUrls && isValidURL(listing.picUrls)">
          <h4>Current Images:</h4>
            <img :src="listing.picUrls" class="carousel-image" />
        </div>
        <div v-else>
          <h4>Current Images:</h4>
          <img :src="require('@/assets/1.png')" class="carousel-image" />
        </div>

          <!-- Listing Title and Content -->
          <h3>{{ listing.title }}</h3>
          <p>{{ listing.content }}</p>

          <!-- Edit and Delete Buttons -->
          <div class="button-group">
            <button @click="editPost(listing.postId)" class="btn-secondary">Edit</button>
            <button @click="removePost(listing.postId)" class="btn-danger">Delete</button>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div class="pagination">
        <button @click="prevPage" :disabled="num === 0">Prev</button>
        <span>{{ num }}</span>
        <button @click="nextPage">Next</button>
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

        <!-- <div class="input-group">
          <label for="type">Listing Type:</label>
          <select v-model="newListing.type" class="input-field" required>
            <option value="">Select Type</option>
            <option value="Apartment">Apartment</option>
            <option value="House">House</option>
            <option value="Room">Room</option>
          </select>
        </div> -->

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
import axios from 'axios';

export default {
  data() {
    return {
      userId:null,
      pos:[],
      postedList: [],
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
        // type: '',
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
      console.log("IdToken: ", idToken);
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

    // get my posted list
    async get_my_post() {
      try {
        const idToken = localStorage.getItem('idToken');
        if (!idToken) {
          console.error('ID Token not found in localStorage.');
          return;
        }
        console.log("Token: ", idToken);
        const decodedToken = jwtDecode(idToken);
        const userId = decodedToken['sub'];  // 获取用户ID

        console.log("UserId: ", userId);
        const response = await axios.post('https://api.rentalninja.link/get-my-list', {
          pageNum: 0,
          pageSize: 10,
        }, {
          headers: {
            'Authorization': `Bearer ${idToken}`, 
            'Content-Type': 'application/json'
          }
        });

        console.log('postedList full response:', response); 
        if (response.data && response.data.responseBody && response.data.responseBody.post_list) {
          this.postedList = response.data.responseBody.post_list;
          console.log("Posts received:", this.postedList);
        } else {
          console.error("Unexpected response structure:", response.data);
        }
      } catch (e) {
        console.error('GET call failed: ', e.message);
        if (e.response) {
          console.error('Response Error:', e.response);
        }
      }
    },
  
    // 退出登录
    async logout() {
      try {
        await signOut(); // 调用 AWS Amplify 的 signOut 方法
        localStorage.removeItem('idToken'); // 清除本地存储的 idToken
        localStorage.removeItem('userId');
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
        const presignedResponse = await fetch('https://api.rentalninja.link/get-presigned-url', { // 使用代理后的后端 URL
          method: 'POST',
          headers: {
            Authorization: `Bearer ${token}`, // 添加 token 进行认证
          },
        });

        if (!presignedResponse.ok) {
          throw new Error('获取预签名URL失败');
        }

        const responseBody = await presignedResponse.json();
        console.log('Response Body:', responseBody);
        const { presignedUrl } = await responseBody.responseBody; // 获取预签名的URL
        console.log('URL: ', presignedUrl);

        const uploadResponse = await fetch(presignedUrl, {
          method: 'PUT',
          body: file,
          headers: {
            'Content-Type': file.type, // Ensure the correct content type
          },
        });

        if (!uploadResponse.ok) {
          throw new Error('图片上传失败');
        }

        return presignedUrl.split('?')[0]; // 返回上传后的图片URL

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

        console.log("UserId: ", userId);

        let imageUrl = '';
        if (this.newListing.image) {
          imageUrl = await this.uploadImage(this.newListing.image);
        }

        console.log("Image:", imageUrl);

        const postData = {
          userId: userId,  // 动态获取的用户ID
          title: this.newListing.title, 
          content: this.newListing.description, 
          contactInfo: this.newListing.contactInfo, 
          picUrls: [imageUrl], 
          countryCode: "",
          stateCode: "",
          cityCode: "",
          //type: this.newListing.type,  
          price: this.newListing.price,  
          area: this.newListing.location,  
        };

        console.log("Postdata: ", postData);


        const response = await fetch('https://api.rentalninja.link/upload-post', { // 使用代理后的后端 URL
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,  
          },
          body: JSON.stringify({"userId": "userId_3002d4ff47b3",
            "title": "title_dd5e858c2ee9",
            "content": "content_74521f30486b",
            "contactInfo": "contactInfo_68bd734c79a6",
            "picUrls": "picUrls_958272bf91fc",
            "countryCode": "country_fc3d13f854c1",
            "stateCode": "state_bac4a8e3fad4",
            "cityCode": "city_895a00e96818",
            "price": 100,
            "area": "area_31b5a51ea565",      
          }),
        });

        console.log("Response: ", response);

        if (!response.ok) {
          throw new Error('发布房源失败，请重试');
        }

        const newListing = await response.json();
        console.log("NewListing: ", newListing);

        this.publishedListings.push({
          id: userId,
          image: postData.picUrls, 
          title: postData.title,
          description: postData.content,
          location: postData.area,
          contactInfo: postData.contactInfo,
          price: postData.price,
        });

        console.log("publishedListings" , this.publishedListings);

        // 清空表单
        this.newListing = {
          title: '',
          description: '',
          price: '',
          location: '',
          //type: '',
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

    editPost(postId) {
      this.$router.push({ name: 'EditingPage', params: { postId } });
    },

    // delete the post
    removePost(postId) {
      const idToken = localStorage.getItem('idToken');  
        if (!idToken) {
          throw new Error('token is null');
        }
      axios.post('https://api.rentalninja.link/delete-post', {
        postId: postId,
      }, { headers: {
            'Authorization': `Bearer ${idToken}`, 
            'Content-Type': 'application/json'
          } })
      .then(response => {
        console.log('deleting my post result:', response.data);
      })
      .catch(error => {
        console.error('Error deleting my post:', error);
      });
      alert('successfully deleted my post!');
      this.$router.push({ name: 'ProfilePage'});
    },

    nextPage() {
      this.num++;
      this.pos = [];
      this.getCollectionsFromAPI();
    },
    prevPage() {
      if (this.num > 0) {
        this.num--;
        this.pos = [];
        this.getCollectionsFromAPI();
      }
    },

    isValidURL(url) {
        try {
          new URL(url); 
          return true;  
        } catch (e) {
          return false;
        }
      },
  },
  mounted() {
    this.get_my_post();
    this.userId = localStorage.getItem('userId');
  },
};
</script>

<style scoped>
.pagination {
  display: flex;             
  justify-content: center;    
  align-items: center;         
  gap: 10px;                  
}

button {
  
  padding: 10px 25px;
  width: auto;
  cursor: pointer;
  border: 2px solid #3498db;   
  background-color: white;      
  color: #3498db;             
  transition: background-color 0.3s, color 0.3s, border-color 0.3s; 
}

button:hover {
  background-color: #3498db;   
  color: white;                
  border-color: #2980b9;        
}

button:disabled {
  cursor: not-allowed;        
  opacity: 0.5;               
}

span {
  font-size: 1.2em;            
  font-weight: bold;        
}

</style>
