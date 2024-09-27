<template>
  <div class="favorites-page">
    <h1>My Favorite Listings</h1>

    <!-- 收藏夹创建部分 -->
    <section class="favorites-section">
      <h2>Collections</h2>

      <!-- 收藏夹列表（正方形并排小框） -->
      <div class="collection-grid">
        <div
          class="collection-item"
          v-for="(collection, index) in collections"
          :key="index"
          @click="toggleCollection(index)"
          :class="{ expanded: selectedCollection === index, collapsed: selectedCollection !== null && selectedCollection !== index }"
        >
          <div class="collection-header clickable">
            <h3>{{ collection.name }}</h3>
          </div>

          <!-- 展开收藏夹，显示收藏房源 -->
          <div class="collection-body fullscreen" v-if="selectedCollection === index">
            <div class="fullscreen-header">
              <button @click.stop="closeCollection" class="close-button">Close</button>
            </div>
            <div class="grid-layout">
              <div class="listing-item clickable" v-for="(listing, idx) in collection.listings" :key="idx" @click="goToDetail(listing.id)">
                <img :src="listing.image" :alt="listing.title" class="clickable-image" />
                <button @click.stop="removeFromCollection(index, idx)" class="remove-button">Remove</button>
              </div>
            </div>
          </div>

          <!-- 如果是新建的空文件夹，显示添加房源按钮 -->
          <div v-if="collection.listings.length === 0 && selectedCollection === index" class="add-listing">
            <button @click.stop="goToListing" class="add-button clickable">Add New Listing</button>
          </div>
        </div>
      </div>

      <!-- 创建新收藏夹 -->
      <div class="new-collection">
        <h3>Create a New Collection</h3>
        <input type="text" v-model="newCollectionName" placeholder="Collection Name" />
        <button @click="createNewCollection" class="create-button">Create Collection</button>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  data() {
    return {
      collections: [
        {
          name: 'Luxury Apartments',
          listings: [
            {
              id: 1,
              image: require('@/assets/2.png'),
              title: 'Deluxe Apartment',
            },
            {
              id: 2,
              image: require('@/assets/3.png'),
              title: 'Single Room for Rent',
            },
          ],
        },
        {
          name: 'Affordable Housing',
          listings: [
            {
              id: 3,
              image: require('@/assets/4.png'),
              title: 'Luxury Apartment',
            },
          ],
        },
        {
          name: 'Student Rentals',
          listings: [
            {
              id: 4,
              image: require('@/assets/5.png'),
              title: 'Budget Room',
            },
          ],
        },
        {
          name: 'New Collection',
          listings: [],
        },
      ],
      selectedCollection: null, // 当前展开的收藏夹
      newCollectionName: '',
    };
  },
  methods: {
    // 切换收藏夹展开状态
    toggleCollection(index) {
      this.selectedCollection = index;
    },

    // 关闭收藏夹
    closeCollection() {
      this.selectedCollection = null;
    },

    // 跳转到房源详情页面
    goToDetail(id) {
      this.$router.push({ name: 'Detail', params: { id } });
    },

    // 移除收藏
    removeFromCollection(collectionIndex, listingIndex) {
      this.collections[collectionIndex].listings.splice(listingIndex, 1);
    },

    // 创建新收藏夹
    createNewCollection() {
      if (this.newCollectionName.trim() !== '') {
        this.collections.push({
          name: this.newCollectionName,
          listings: [],
        });
        this.newCollectionName = ''; // 清空输入框
      }
    },

    // 跳转到房源列表页面
    goToListing() {
      this.$router.push('/listing');
    },
  },
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
