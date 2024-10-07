<template>
  <div class="listing-page">
    <h1>My Favorite Listings</h1>

    <!-- 收藏列表 -->
    <div class="listing-grid">
      <div class="listing-card" v-for="post in collections" :key="post.postid">
        <div v-if="post.picUrls == null">
          <img :src="require('@/assets/2.png')" :alt="post.title" @click="goToDetail(post.postId)" class="clickable-image" />

        </div>
        <div v-else>
          <img :src= post.picUrls :alt="post.title" @click="goToDetail(post.postId)" class="clickable-image" />
        </div>
        <h3>{{ post.title }}</h3>
        <p>{{ post.content }}</p>
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

const headers = {
          Authorization: `Bearer ${localStorage.getItem('idToken')}`,  
          'Content-Type': 'application/json',  
        };

export default {
  data() {
    return {
      collections: [], 
      error: null,  
    };
  },
  mounted() {
    this.getCollectionsFromAPI();
  },
  methods: {
    async getCollectionsFromAPI() {
      try {
        // Make the request with headers
        console.log("header:",headers)
        const response = await axios.post('https://api.rentalninja.link/get-collection-list', {}, { headers });
        
        // Update collections with the API response
        this.collections = response.data.responseBody.list;
        console.log("response:", response)
      } catch (error) {
        console.error('Error fetching collections:', error);
        this.error = 'Failed to load collections. Please try again later.';
      }
    },
    removeFromCollection(postId) {
      // User is unfavoriting the post, call the "remove from favorites" API
      axios.post('https://api.rentalninja.link/add-collection', {
        postId: postId,
        isAdd: 0,
      }, { headers })
      .then(response => {
        console.log('Removed from favorites:', response.data);
      })
      .catch(error => {
        console.error('Error removing from favorites:', error);
      });
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
    goToDetail(id) {
      localStorage.setItem("postid", id);
      this.$router.push({ name: 'Detail', params: { id } });
      
    },
  }
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
