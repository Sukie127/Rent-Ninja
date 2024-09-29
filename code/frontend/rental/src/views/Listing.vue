<template>
  <div class="listing-page">
    <h1>Listing Page</h1>

    <!-- 过滤器 -->
    <div class="filter-section">
      <label for="location">Filter by Location:</label>
      <select v-model="selectedLocation" @change="applyFilters">
        <option value="">All Locations</option>
        <option value="City Center">City Center</option>
        <option value="Suburbs">Suburbs</option>
      </select>

      <label for="price">Filter by Price Range:</label>
      <select v-model="selectedPriceRange" @change="applyFilters">
        <option value="">All Prices</option>
        <option value="low">Below ¥3000</option>
        <option value="medium">¥3000 - ¥5000</option>
        <option value="high">Above ¥5000</option>
      </select>

      <label for="type">Filter by Type:</label>
      <select v-model="selectedType" @change="applyFilters">
        <option value="">All Types</option>
        <option value="Apartment">Apartment</option>
        <option value="Single Room">Single Room</option>
      </select>



      <label for="state">Filter by State:</label>
      <select v-model="selectedState" @change="applyFilters" :disabled="!selectedCountry">
        <option value="">All States</option>
        <option v-for="state in states" :key="state" :value="state">{{ state }}</option>
      </select>
    </div>

    <!-- 如果没有找到任何匹配的房源，显示提示信息 -->
    <div v-if="filteredPosts.length === 0" class="no-results">
      <p>Sorry, no matching listings were found.</p>
    </div>

    <!-- 房源列表 -->
    <div v-else class="listing-grid">
      <div class="listing-card" v-for="post in pos" :key="post.postid">
        <div v-if="post.picUrls == null">
          <img :src="require('@/assets/2.png')" :alt="post.title" @click="goToDetail(post.post_id)" class="clickable-image" />

        </div>
        <div v-else>
          <img :src= post.picUrls :alt="post.title" @click="goToDetail(post.post_id)" class="clickable-image" />
        </div>
        <h3>{{ post.title }}</h3>
        <p>{{ post.content }}</p>
        <!-- <router-link :to="{ name: 'Detail', params: { id: post.postid } }">
          View Details
        </router-link> -->
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
export default {
  name: 'ListingPage',
  data() {
    return {
      selectedLocation: '',
      selectedPriceRange: '',
      selectedType: '',
      selectedCountry: '',
      selectedState: '',
      pos: [],
      num: 0,

      countries: ['USA'],
      states: [],

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
    async get_post() {
      try {
        const idToken = localStorage.getItem('id');
        if (!idToken) {
          console.error('ID Token not found in localStorage.');
          return;
        }

        const response = await axios.post('https://api.rentalninja.link/get-post-list', {
          pageNum: this.num,
          pageSize: 9,
          keyword: "",
          stateCode: "",
          cityCode: ""
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
      this.$router.push({ name: 'Detail', params: { id } });
    },
  },
  mounted() {
    this.applyFilters();
    this.get_post();
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