<template>
  <div class="listing-page">
    <h1>Listing Page</h1>

   <!-- 搜索功能区域 -->
   <section class="search-section">
      <h2>Find Your Ideal Housing</h2>
      
      <!-- 关键词搜索 -->
      <form class="search-form" @submit.prevent="searchByKeyword">
        <input type="text" v-model="skeyword" placeholder="Enter keyword to search" />
        <button type="submit" @click="get_post">Search</button>
      </form>

      <!-- 国家--州--城市 搜索 -->
      <div class="location-search">
        <h3>Search by Country, State, and City</h3>
        <div class="dropdowns">
          <select v-model="selectedCountry" @change="fetchStates">
            <option value="">Select Country</option>
            <option>USA</option>
          </select>

          <select v-model="selectedState" @change="fetchCities" :disabled="!selectedCountry">
            <option value="">Select State/Province</option>
            <option v-for="(state, index) in states" :key="index" :value="state">{{ state }}</option>
          </select>

          <select v-model="selectedCity" :disabled="!selectedState">
            <option value="">Select City</option>
            <option v-for="(city, index) in cities" :key="index" :value="city">{{ city }}</option>
          </select>

          <button @click="get_post">Search</button>
        </div>
      </div>
    </section>





 







    <!-- 房源列表 -->
    <div  class="listing-grid">
      <div class="listing-card" v-for="post in pos" :key="post.postid">
        <div v-if="!post.picUrls || !post.picUrls.includes('https://rentalninja.s3.us-east-2.amazonaws.com/')">
         <img :src="require('@/assets/2.png')" :alt="post.title" @click="goToDetail(post.postId)" class="clickable-image" />
        </div>
        <div v-else>
          <img :src= post.picUrls :alt="post.title" @click="goToDetail(post.postId)" class="clickable-image" />
        </div>
        <h3>{{ post.title }}</h3>
        <p>{{ post.price }}</p>
        <router-link :to="{ name: 'Detail', params: { id: post.postId } }">
          View Details
        </router-link>
      </div>
      
    </div>
    <div class="pagination">
      <button @click="prevPage" :disabled="num === 0">Prev</button>
      <span>{{ this.num }}</span>
      <button @click="nextPage">Next</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { signIn } from '@aws-amplify/auth';
import { signOut } from '@aws-amplify/auth';
import "@aws-amplify/ui-vue/styles.css";
import { fetchAuthSession } from "aws-amplify/auth";
import { Amplify } from "aws-amplify";
export default {
  name: 'ListingPage',
  data() {
    return {
      skeyword: '',
      state: '',
      city: '',
      selectedCity: '',
      selectedCountry: '',
      selectedState: '',
      pos: [],
      num: 0,

      countries: ['USA'],
      states: ['MA'],
      cities: ['Boston'],

      posts: [
        {
          post_id: "0d46de9f-a068-4c27-923e-cdcb852946d3",
          title: 'Test Title',
          content: 'Convenient location near the city center.',
  
        },
        {
          post_id: "0d46de9f-a068-4c27-923e-cdcb852946d3",
          title: 'Test Title',
          content: 'Convenient location near the city center.',
        },
        {
          post_id: "0d46de9f-a068-4c27-923e-cdcb852946d3",
          title: 'Test Title',
          content: 'Convenient location near the city center.',
        },
      ],

      filteredPosts: [],
    };
  },
  methods: {
    async login() {
      try {
        const user = await signIn({
          username: 'ding874946686@gmail.com',
          password: 'D13607161848@jc',
        });
        console.log('Login successful:', user);
        const currentConfig = Amplify.getConfig();
        Amplify.configure({
          ...currentConfig,
          API: {
            REST: {
              RentalNinja: {
                endpoint: "https://api.rentalninja.link",
                region: "us-east-2",
              },
            },
          },
        });

        const { accessToken, idToken } = (await fetchAuthSession()).tokens ?? {};
        localStorage.setItem('idToken', idToken);
        localStorage.setItem('ac', accessToken);
        console.log(localStorage.getItem('idToken'));
        await signOut();
        
      } catch (error) {
        console.error('Login error:', error);
        this.errorMessage = 'Login failed: ' + error.message; // 设置错误反馈
      }
    },
    async get_post() {
      try {
        const idToken = localStorage.getItem('idToken');
        if (!idToken) {
          console.error('ID Token not found in localStorage.');
          return;
        }

        const response = await axios.post('https://api.rentalninja.link/get-post-list', {
          pageNum: this.num,
          pageSize: 9,
          keyword: this.skeyword,
          stateCode: this.selectedState,
          cityCode: this.selectedCity
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
    searchByKeyword(){

    },
    nextPage() {
      this.num++;
      this.pos = [];
      this.get_post();
    },
    prevPage() {
      if (this.num > 0) {
        this.num--;
        this.pos = [];
        this.get_post();
      }
    },
    applyFilters() {
      this.filteredPosts = this.posts.filter(post => {
        const matchesLocation = this.selectedLocation === '' || post.location === this.selectedLocation;
        let matchesPrice = true;
        if (this.selectedPriceRange === 'low') {
          matchesPrice = post.price < 3000;
        } else if (this.selectedPriceRange === 'medium') {
          matchesPrice = post.price >= 3000 && post.price <= 5000;
        } else if (this.selectedPriceRange === 'high') {
          matchesPrice = post.price > 5000;
        }
        const matchesState = this.selectedState === '' || post.state === this.selectedState;

        return matchesLocation && matchesPrice && matchesState;
      });
    },
    goToDetail(id) {
      localStorage.setItem("postid", id);
      this.$router.push({ name: 'Detail', params: { id } });
      
    },
   
  },
  mounted() {
    this.applyFilters();
    this.get_post();
    this.login();
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