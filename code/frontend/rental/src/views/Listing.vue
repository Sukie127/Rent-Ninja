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
          location: 'City Center',
          type: 'Apartment',
          country: 'China',
          state: 'Zhejiang',
        },
        {
          id: 2,
          image: require('@/assets/3.png'),
          title: 'Single Room for Rent',
          description: 'Quiet residential area, perfect for students.',
          price: 2000,
          location: 'Suburbs',
          type: 'Single Room',
          country: 'USA',
          state: 'California',
        },
        {
          id: 3,
          image: require('@/assets/4.png'),
          title: 'Luxury Apartment',
          description: 'Modern facilities, ideal for long-term rental.',
          price: 5500,
          location: 'City Center',
          type: 'Apartment',
          country: 'Canada',
          state: 'Ontario',
        },
      ],

      filteredPosts: [], // 保存过滤后的房源列表
    };
  },
  methods: {
    async fetchListingsFromBackend() {
      try {
        const token = localStorage.getItem('idToken');
        const response = await fetch('/api/get-listings', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error('无法获取房源数据');
        }

        const backendListings = await response.json();
        // 合并后端数据和现有静态数据
        this.posts = [...this.posts, ...backendListings];
        this.applyFilters(); // 重新应用过滤器
      } catch (error) {
        console.error('获取房源数据失败:', error);
      }
    },

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
    // 初始化过滤列表并从后端获取数据
    this.applyFilters();
    this.fetchListingsFromBackend();
  }
};
</script>

<style scoped>
/* 添加相关样式 */
</style>
