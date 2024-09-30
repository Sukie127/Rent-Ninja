<template>
  <div class="home-page">
    <!-- Hero Banner -->
    <section class="hero-banner">
      <div class="hero-content">
        <h1>Welcome to the Student Housing Platform</h1>
        <p>Here you can find the best housing options for international students</p>
      </div>
    </section>

    <!-- 搜索功能区域 -->
    <section class="search-section">
      <h2>Find Your Ideal Housing</h2>
      
      <!-- 关键词搜索 -->
      <form class="search-form" @submit.prevent="searchByKeyword">
        <input type="text" v-model="searchQuery.keyword" placeholder="Enter keyword to search" />
        <button type="submit">Search</button>
      </form>

      <!-- 国家--州--城市 搜索 -->
      <div class="location-search">
        <h3>Search by Country, State, and City</h3>
        <div class="dropdowns">
          <select v-model="selectedCountry" @change="fetchStates">
            <option value="">Select Country</option>
            <option v-for="(country, index) in countries" :key="index" :value="country">{{ country }}</option>
          </select>

          <select v-model="selectedState" @change="fetchCities" :disabled="!selectedCountry">
            <option value="">Select State/Province</option>
            <option v-for="(state, index) in states" :key="index" :value="state">{{ state }}</option>
          </select>

          <select v-model="selectedCity" :disabled="!selectedState">
            <option value="">Select City</option>
            <option v-for="(city, index) in cities" :key="index" :value="city">{{ city }}</option>
          </select>

          <button @click="searchByLocation" :disabled="!selectedCity">Search</button>
        </div>
      </div>
    </section>

    <!-- 推荐房源部分 -->
    <section class="recommended-listings">
      <h2>Recommended Listings</h2>
      <div class="grid-layout">
        <div class="listing-item" v-for="(listing, index) in filteredListings" :key="index" @click="goToDetail(listing.id)">
          <img :src="listing.image" :alt="listing.title" />
          <h3>{{ listing.title }}</h3>
          <p>{{ listing.description }}</p>
        </div>
      </div>
    </section>

    <!-- 用户评价部分 -->
    <section class="user-reviews">
      <h2>User Reviews</h2>
      <div class="reviews-carousel">
        <div class="review-item" v-for="(review, index) in reviews" :key="index">
          <p>"{{ review.text }}"</p>
          <p>—— {{ review.user }}</p>
        </div>
      </div>
    </section>

    <!-- 如何运作部分 -->
    <section class="how-it-works">
      <h2>How It Works</h2>
      <div class="steps">
        <div class="step" v-for="(step, index) in steps" :key="index">
          <img :src="step.icon" :alt="step.title" />
          <h3>{{ step.title }}</h3>
          <p>{{ step.description }}</p>
        </div>
      </div>
    </section>

    <!-- 常见问题部分 -->
    <section class="faq">
      <h2>FAQ</h2>
      <div 
        class="faq-item" 
        v-for="(faq, index) in faqs" 
        :key="index" 
        @click="toggleFaq(index)" 
        :class="{ 'active': activeFaqIndex === index }">
        <h3>{{ faq.question }}</h3>
        <p v-if="activeFaqIndex === index">{{ faq.answer }}</p>
      </div>
    </section>
  </div>
</template>

<script>

export default {
  name: 'HomePage',
  data() {
    return {
      searchQuery: {
        keyword: '', // 关键词搜索
      },
      selectedCountry: '', // 选择的国家
      selectedState: '', // 选择的州
      selectedCity: '', // 选择的城市
      countries: ['China', 'USA', 'Canada'], // 国家列表
      states: [], // 州列表
      cities: [], // 城市列表
      listings: [
        {
          id: 1,
          image: require('@/assets/2.png'),
          title: 'Deluxe Apartment',
          description: 'Convenient location near the city center.',
          country: 'China',
          state: 'Zhejiang',
          city: 'Hangzhou',
          keyword: 'apartment',
        },
        {
          id: 2,
          image: require('@/assets/3.png'),
          title: 'Single Room for Rent',
          description: 'Quiet residential area, perfect for students.',
          country: 'USA',
          state: 'California',
          city: 'Los Angeles',
          keyword: 'single room',
        },
        {
          id: 3,
          image: require('@/assets/4.png'),
          title: 'Luxury Apartment',
          description: 'Modern facilities, ideal for long-term rental.',
          country: 'Canada',
          state: 'Ontario',
          city: 'Toronto',
          keyword: 'luxury',
        },
      ],
      filteredListings: [], // 用于存储过滤后的房源列表
      reviews: [
        {
          text: 'This is the best rental platform I have ever used. Finding a place is very easy!',
          user: 'User A',
        },
        {
          text: 'I found a very satisfactory apartment through this platform, highly recommended!',
          user: 'User B',
        },
      ],
      steps: [
        {
          icon: require('@/assets/5.png'),
          title: 'Search Listings',
          description: 'Choose a city and browse the list.',
        },
        {
          icon: require('@/assets/6.png'),
          title: 'Contact Landlord',
          description: 'Reach out to landlords through the platform.',
        },
        {
          icon: require('@/assets/7.png'),
          title: 'Sign Contract',
          description: 'Sign the contract online to secure your rental.',
        },
        {
          icon: require('@/assets/8.png'),
          title: 'Move In',
          description: 'Move into your new home and start a new life.',
        },
      ],
      faqs: [
        {
          question: 'How to contact the landlord?',
          answer: 'You can directly contact the landlord via the messaging system on the platform.',
        },
        {
          question: 'How much is the deposit?',
          answer: 'The deposit is usually one month’s rent. Please consult the landlord for specifics.',
        },
      ],
      activeFaqIndex: null, // 控制常见问题的显示与隐藏
    };
  },
  methods: {

   
    async fetchListingsFromBackend() {
      try {
        const token = localStorage.getItem('idToken'); // 从 localStorage 获取 idToken
        const response = await fetch('/api/get-listings', {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (!response.ok) {
          throw new Error('无法获取房源数据');
        }
        const data = await response.json();
        // 将后端的数据添加到 listings 中，并更新过滤后的数据
        this.listings = [...this.listings, ...data];
        this.filteredListings = this.listings;
      } catch (error) {
        console.error('获取房源数据失败:', error);
      }
    },
    
    // 关键词搜索并筛选房源
    searchByKeyword() {
      const keyword = this.searchQuery.keyword.toLowerCase();
      this.filteredListings = this.listings.filter((listing) => {
        return (
          listing.title.toLowerCase().includes(keyword) ||
          listing.description.toLowerCase().includes(keyword) ||
          listing.keyword.toLowerCase().includes(keyword)
        );
      });
    },

    // 根据国家、州和城市搜索
    searchByLocation() {
      this.filteredListings = this.listings.filter((listing) => {
        return (
          listing.country === this.selectedCountry &&
          listing.state === this.selectedState &&
          listing.city === this.selectedCity
        );
      });
    },

    // 模拟根据选择国家获取州
    fetchStates() {
      if (this.selectedCountry === 'China') {
        this.states = ['Zhejiang', 'Guangdong', 'Jiangsu'];
      } else if (this.selectedCountry === 'USA') {
        this.states = ['California', 'New York', 'Texas'];
      } else if (this.selectedCountry === 'Canada') {
        this.states = ['Ontario', 'British Columbia'];
      }
      this.selectedState = '';
      this.selectedCity = '';
      this.cities = [];
    },

    // 模拟根据选择州获取城市
    fetchCities() {
      if (this.selectedState === 'Zhejiang') {
        this.cities = ['Hangzhou', 'Ningbo', 'Wenzhou'];
      } else if (this.selectedState === 'California') {
        this.cities = ['Los Angeles', 'San Francisco', 'San Diego'];
      }
    },

    // 跳转到房源详情页
    goToDetail(id) {
      this.$router.push({ name: 'Detail', params: { id } });
    },

    // 控制常见问题的显示与隐藏
    toggleFaq(index) {
      this.activeFaqIndex = this.activeFaqIndex === index ? null : index;
    },
  },

  // 初始显示所有房源并从后端获取数据
  mounted() {
    this.filteredListings = this.listings;
    this.fetchListingsFromBackend(); // 获取后端数据并合并到现有静态数据中

  },
};
</script>

<style scoped>

</style>
