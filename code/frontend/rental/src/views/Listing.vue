<template>
  <div class="listing-page">
    <h1>Listing Page</h1>

    <!-- 过滤器 -->
    <div class="filter-section">
      <label for="location">Filter by Location:</label>
      <select v-model="selectedLocation" @change="applyFilters">
        <option value="">All Locations</option>
        <option value="City Center">City Center</option> <!-- 使用英文以匹配posts数据 -->
        <option value="Suburbs">Suburbs</option> <!-- 使用英文以匹配posts数据 -->
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
        <option value="Apartment">Apartment</option> <!-- 使用英文以匹配posts数据 -->
        <option value="Single Room">Single Room</option> <!-- 使用英文以匹配posts数据 -->
      </select>

      <!-- 国家、州、省过滤器 -->
      <label for="country">Filter by Country:</label>
      <select v-model="selectedCountry" @change="applyFilters">
        <option value="">All Countries</option>
        <option v-for="country in countries" :key="country" :value="country">{{ country }}</option>
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
      <div class="listing-card" v-for="(post, index) in filteredPosts" :key="index">
        <img :src="post.image" :alt="post.title" @click="goToDetail(post.id)" class="clickable-image" />
        <h3>{{ post.title }}</h3>
        <p>{{ post.description }}</p>
        <p class="price">{{ post.price }}</p>
        <router-link :to="{ name: 'Detail', params: { id: post.id } }">
          View Details
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ListingPage',
  data() {
    return {
      selectedLocation: '',  // 按地区过滤
      selectedPriceRange: '', // 按价格过滤
      selectedType: '',  // 按类型过滤
      selectedCountry: '',  // 按国家过滤
      selectedState: '',  // 按州过滤

      countries: ['China', 'USA', 'Canada'], // 模拟的国家列表
      states: [],  // 根据国家选择的州列表

      posts: [
        {
          id: 1,
          image: require('@/assets/2.png'),
          title: 'Deluxe Apartment',
          description: 'Convenient location near the city center.',
          price: 3000,
          location: 'City Center', // 确保与下拉框中的值一致
          type: 'Apartment', // 确保与下拉框中的值一致
          country: 'China',
          state: 'Zhejiang',
        },
        {
          id: 2,
          image: require('@/assets/3.png'),
          title: 'Single Room for Rent',
          description: 'Quiet residential area, perfect for students.',
          price: 2000,
          location: 'Suburbs', // 确保与下拉框中的值一致
          type: 'Single Room', // 确保与下拉框中的值一致
          country: 'USA',
          state: 'California',
        },
        {
          id: 3,
          image: require('@/assets/4.png'),
          title: 'Luxury Apartment',
          description: 'Modern facilities, ideal for long-term rental.',
          price: 5500,
          location: 'City Center', // 确保与下拉框中的值一致
          type: 'Apartment', // 确保与下拉框中的值一致
          country: 'Canada',
          state: 'Ontario',
        },
      ],

      filteredPosts: [], // 保存过滤后的房源列表
    };
  },
  methods: {
    applyFilters() {
      // 基于过滤器应用条件
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
        const matchesType = this.selectedType === '' || post.type === this.selectedType;
        const matchesCountry = this.selectedCountry === '' || post.country === this.selectedCountry;
        const matchesState = this.selectedState === '' || post.state === this.selectedState;

        return matchesLocation && matchesPrice && matchesType && matchesCountry && matchesState;
      });
    },
    goToDetail(id) {
      this.$router.push({ name: 'Detail', params: { id } });
    }
  },
  mounted() {
    // 初始化过滤列表
    this.applyFilters();
  }
};
</script>


<style scoped>
.listing-page {
  padding: 40px;
  padding-top: 100px;
  padding-left: 300px;
}

.filter-section {
  margin-bottom: 20px;
  display: flex;
  gap: 20px;
  align-items: center;
  justify-content: center;
  padding: 10px 20px;
  background: linear-gradient(90deg, rgba(76, 175, 80, 0.2), rgba(255, 255, 255, 0.2));
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  flex-wrap: wrap; /* 使筛选框适应不同屏幕宽度 */
  max-width: 400px;
  height: 50px;
  position: relative;
  overflow: hidden;
}

/* 在筛选框还未展开时显示 "Filter" 的提示文字 */
.filter-section::before {
  content: "Filter"; /* 提示文字 */
  font-size: 18px;
  color: #333;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  opacity: 1;
  transition: opacity 0.3s ease;
}

.filter-section:hover::before {
  opacity: 0;
  transition: opacity 0.3s ease;
}

/* 当鼠标悬停时，筛选框会展开 */
.filter-section:hover {
  max-width: 100%;
  height: 100px;
  background: linear-gradient(90deg, rgba(76, 175, 80, 0.4), rgba(255, 255, 255, 0.4));
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  transform: scale(1.02);
  transition: all 0.5s ease;
}

.filter-section select {
  padding: 10px 20px;
  border: 2px solid #4CAF50;
  border-radius: 5px;
  background-color: #fff;
  font-size: 16px;
  color: #333;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  width: 200px; /* 增大选择框宽度 */
  opacity: 0;
  transform: translateY(-20px);
  visibility: hidden;
}

/* 鼠标悬停时显示选择框 */
.filter-section:hover select {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
  transition: all 0.5s ease;
}

.filter-section label {
  font-size: 16px;
  color: #333;
  margin-right: 10px;
  opacity: 0;
  transform: translateY(-20px);
  visibility: hidden;
  transition: all 0.3s ease;
}

/* 鼠标悬停时显示标签 */
.filter-section:hover label {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
  transition: all 0.5s ease;
}

/* 增加对选择框内部的文本样式支持 */
.filter-section option {
  padding: 10px;
  font-size: 16px;
  color: #333;
}

.clickable-image {
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.clickable-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.listing-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.listing-card {
  background-color: #fff;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.listing-card img {
  width: 100%;
  height: auto;
  border-radius: 10px;
}

.listing-card h3 {
  margin: 10px 0;
  font-size: 18px;
}

.listing-card p {
  margin: 5px 0;
}

.price {
  font-weight: bold;
  color: #4CAF50;
  margin-bottom: 10px;
}

.listing-card a {
  display: inline-block;
  margin-top: 10px;
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  text-decoration: none;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

.listing-card a:hover {
  background-color: #45a049;
}

.no-results {
  text-align: center;
  padding: 20px;
  color: #999;
}

</style>
