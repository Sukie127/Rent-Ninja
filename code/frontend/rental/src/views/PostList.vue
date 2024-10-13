<template>
  <div class="profile-page">
    <h1>Post</h1>


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

        <div class="location">
        <h3>Location</h3>
        <div class="dropdowns">
          <select v-model="selectedCountry" @change="fetchStates">
            <option value="">Select Country</option>
            <option>USA</option>
          </select>

          <select v-model="selectedState" @change="fetchCities" :disabled="!selectedCountry">
            <option value="">Select State/Province</option>
            <option v-for="(state, index) in states" :key="index" :value="state">{{ state }}</option>
          </select>

          <select v-model="selectedCity" @change="fetchCities" :disabled="!selectedState">
            <option value="">Select City</option>
            <option v-for="(city, index) in cities" :key="index" :value="city">{{ city }}</option>
            
          </select>
        </div>
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
import axios from 'axios';

export default {
  data() {
    return {
      userId:null,
      state: '',
      city: '',
      selectedCity: '',
      selectedCountry: '',
      selectedState: '',
      pos:[],
      countries: ['USA'],
      states: ['MA', 'NY', 'CA', 'TX', 'FL', 'IL', 'PA', 'OH', 'MI', 'GA'], // 10个州
      citiesByState: {
        'MA': ['Boston', 'Cambridge', 'Springfield', 'Worcester', 'Lowell', 'Quincy', 'Lynn', 'Newton', 'Somerville', 'Lawrence'],
        'NY': ['New York City', 'Buffalo', 'Rochester', 'Albany', 'Syracuse', 'Yonkers', 'Schenectady', 'Utica', 'White Plains', 'Ithaca'],
        'CA': ['Los Angeles', 'San Francisco', 'San Diego', 'Sacramento', 'Fresno', 'Oakland', 'San Jose', 'Bakersfield', 'Anaheim', 'Long Beach'],
        'TX': ['Houston', 'Austin', 'Dallas', 'San Antonio', 'Fort Worth', 'El Paso', 'Arlington', 'Corpus Christi', 'Plano', 'Laredo'],
        'FL': ['Miami', 'Orlando', 'Tampa', 'Jacksonville', 'St. Petersburg', 'Hialeah', 'Tallahassee', 'Fort Lauderdale', 'Cape Coral', 'Pembroke Pines'],
        'IL': ['Chicago', 'Springfield', 'Peoria', 'Naperville', 'Rockford', 'Joliet', 'Evanston', 'Cicero', 'Champaign', 'Elgin'],
        'PA': ['Philadelphia', 'Pittsburgh', 'Allentown', 'Harrisburg', 'Erie', 'Scranton', 'Lancaster', 'Bethlehem', 'Reading', 'York'],
        'OH': ['Columbus', 'Cleveland', 'Cincinnati', 'Toledo', 'Akron', 'Dayton', 'Parma', 'Canton', 'Youngstown', 'Lorain'],
        'MI': ['Detroit', 'Ann Arbor', 'Grand Rapids', 'Lansing', 'Flint', 'Dearborn', 'Warren', 'Sterling Heights', 'Kalamazoo', 'Livonia'],
        'GA': ['Atlanta', 'Savannah', 'Augusta', 'Athens', 'Macon', 'Columbus', 'Roswell', 'Albany', 'Marietta', 'Sandy Springs']
      },

      cities: [],
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
        countryCode: '',
        stateCode: '',
        cityCode: '',
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

    fetchCities() {
    console.log('Selected State:', this.selectedState);
    this.cities = this.citiesByState[this.selectedState] || [];
    console.log('Available Cities:', this.cities);
    console.log("Citycode:" , this.selectedCity)
    this.newListing.stateCode = this.selectedState;
    this.newListing.cityCode = this.selectedCity;
    this.newListing.countryCode = this.selectedCountry;
    console.log(this.selectedCity, this.selectedCountry, this.selectedState);
    },


    async get_post() {
      try {
        const idToken = localStorage.getItem('idToken');
        if (!idToken) {
          console.error('ID Token not found in localStorage.');
          return;
        }
        console.log("Token: ", idToken);
        const response = await axios.post('https://api.rentalninja.link/get-post-list', {
          pageNum: 0,
          pageSize: 10000,
          keyword: "",
          stateCode: "",
          cityCode: "",
        }, {
          headers: {
            'Authorization': `Bearer ${idToken}`, 
            'Content-Type': 'application/json'
          }
        });

        console.log('Full response:', response); 
        if (response.data && response.data.responseBody && response.data.responseBody.post_list) {
          this.pos = response.data.responseBody.post_list;
          console.log("Posts received:", this.pos);
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
        console.log(presignedResponse);
        console.log('Response Body:', responseBody);
        const presignedUrl = await responseBody.responseBody.presignedUrl; // 获取预签名的URL
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
          picUrls: [imageUrl].join(','), 
          countryCode: this.newListing.countryCode,
          stateCode: this.newListing.stateCode,
          cityCode: this.newListing.cityCode, 
          price: this.newListing.price,  
          area: '',  
        };

        console.log("Postdata: ", postData);
        
        // const response = axios.post('https://api.rentalninja.link/upload-post', postData, {
        //   headers: {
        //     'Content-Type': 'application/json',
        //     Authorization: `Bearer ${token}`  // Make sure token is correctly set
        //   }
        // })
        // .then(response => {
        //   console.log('Post successful:', response.data);
        // })
        // .catch(error => {
        //   console.error('Error posting data:', error);
        // });
        
        //Stringify the postData object
        const jsonpostdata = JSON.stringify(postData);

        // Log the JSON string to the console
        console.log('Stringified postData:', jsonpostdata);

        const response = await fetch('https://api.rentalninja.link/upload-post', { // 使用代理后的后端 URL
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,  
          },
          body: jsonpostdata,
        });

        console.log("Response: ", response);

        if (!response.ok) {
          throw new Error('发布房源失败，请重试');
        }

        const newListing = await response.json();
        console.log("NewListing: ", newListing);

        this.publishedListings.push({
          userId: userId,
          image: postData.picUrls, 
          title: postData.title,
          description: postData.content,
          location: postData.area,
          countryCode: postData.countryCode,
          stateCode: postData.stateCode,
          cityCode: postData.cityCode, 
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
          countryCode: '',
          stateCode: '',
          cityCode: '',
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
  mounted() {
    this.get_post();
    this.userId = localStorage.getItem('userId');
  },
};
</script>

<style scoped>


</style>
