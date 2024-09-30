<template>
  <section class="favorites-section">
    <h2>Collections</h2>
    
    <div class="collection-grid">
      <div v-if="error">{{ error }}</div>
      
      <!-- Loop through each collection in the collections array -->
      <div
        class="collection-item"
        v-for="(collection, index) in collections"
        :key="collection.postId"
        @click="toggleCollection(index)"
        :class="{ expanded: selectedCollection === index, collapsed: selectedCollection !== index }"
      >
        <!-- Display the title as collection header -->
        <div class="collection-header clickable">
          <h3>{{ collection.title }}</h3>
        </div>

        <!-- Show the collection body only if this collection is selected -->
        <div class="collection-body fullscreen" v-if="selectedCollection === index">
          <div class="fullscreen-header">
            <button @click.stop="closeCollection" class="close-button">Close</button>
          </div>

          <!-- Display listing information in grid layout -->
          <div class="grid-layout">
            <div class="listing-item clickable" :key="collection.postId">
              <!-- Assuming picUrls contains the image URL -->
              <img :src="collection.picUrls" :alt="collection.title" class="clickable-image" />
              <h3>{{ collection.title }}</h3>
              <p>{{ collection.content }}</p>
              <p>{{ collection.contactInfo }}</p>
              <p>{{ collection.createTime }}</p>
              <button @click.stop="removeFromCollection(collection.postId)" class="remove-button">Remove</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
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
        // Make the GET request with headers
        const response = await axios.get('https://api.rentalninja.link/get-collection-list', { headers });
        
        // Update collections with the API response
        this.collections = response.data.list;
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
    }
  }
};

</script>

<style scoped>
.favorites-page {
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  position: relative;
}

.collection-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* 两列布局 */
  gap: 40px;
  justify-content: center;
}

.collection-item {
  background-color: #e0f7fa;
  border-radius: 10px;
  padding: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  text-align: center;
  height: 250px;
  width: 250px;
  box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.3); /* 阴影效果 */
}

.collection-item.expanded {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: auto; 
  min-width: 80vw;
  height: auto; 
  min-height: 70vh; 
  background-color: rgba(255, 255, 255, 0.85);
  z-index: 1000;
  padding: 40px;
  animation: expand 0.5s ease-out;
  box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.3); /* 阴影效果 */
}

@keyframes expand {
  0% {
    transform: translate(-50%, -50%) scale(0.8);
    opacity: 0;
  }
  100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 1;
  }
}

.collection-item.collapsed {
  filter: blur(4px);
  opacity: 0.5;
  pointer-events: none;
}

.fullscreen-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.close-button {
  background-color: #ff4c4c;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
}

.grid-layout {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); 
  gap: 20px;
}

.listing-item {
  background-color: #ffffff;
  border-radius: 10px;
  padding: 10px;
  text-align: center;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  height: 200px;
  width: 200px;
}

.listing-item:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.remove-button {
  background-color: #ff4c4c;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  margin-top: 10px;
  cursor: pointer;
}

.remove-button:hover {
  background-color: #e63946;
}

.add-listing {
  margin-top: 20px;
}

.add-button {
  background-color: #4CAF50;
  color: white;
  padding: 10px 20px;
  width: 150px;
  border-radius: 5px;
  cursor: pointer;
}

.add-button:hover {
  background-color: #45a049;
}

.new-collection {
  margin-top: 40px;
  text-align: center;
}

.new-collection input {
  margin-right: 15px; 
}

.create-button {
  background-color: #4CAF50;
  color: white;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
}

.create-button:hover {
  background-color: #45a049;
}

.new-collection {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 40px;
}

.new-collection input {
  margin-bottom: 15px;
  padding: 10px;
  font-size: 16px;
  width: 300px;
  box-sizing: border-box;
}

.new-collection .create-button {
  padding: 10px 30px;
  font-size: 16px;
  width: 280px;
  text-align: center;
  margin-left: -10px;
}
</style>
