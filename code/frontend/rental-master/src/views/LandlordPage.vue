<template>
    <div class="landlord-page">
      <div class="landlord-info">
        <img :src="landlord.avatar" alt="房东头像" class="landlord-avatar" />
        <h1>{{ landlord.name }}</h1>
        <p>联系方式: {{ landlord.contact }}</p>
        <p>房东介绍: {{ landlord.bio }}</p>
      </div>
  
      <div class="landlord-listings">
        <h2>房东的房源</h2>
        <div v-if="landlordListings.length">
          <div class="listing-card" v-for="(listing, index) in landlordListings" :key="index">
            <img :src="listing.image" :alt="listing.title" class="listing-image"/>
            <h3>{{ listing.title }}</h3>
            <p>{{ listing.description }}</p>
            <p class="price">{{ listing.price }}</p>
            <router-link :to="{ name: 'Detail', params: { id: listing.id } }">
              查看详情
            </router-link>
          </div>
        </div>
        <p v-else>暂无房源</p>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'LandlordPage',
    data() {
      return {
        landlord: {
          id: this.$route.params.landlordId,
          name: '张三',
          contact: '123-456-7890',
          bio: '我是一个拥有多年经验的房东，管理多套优质房源。',
          avatar: require('@/assets/1.png'), // 替换为实际头像路径
        },
        landlordListings: [
          {
            id: 1,
            image: require('@/assets/2.png'), // 替换为实际图片路径
            title: '精致公寓',
            description: '靠近市中心，交通便利。',
            price: '¥3000/月',
          },
          {
            id: 2,
            image: require('@/assets/3.png'),
            title: '单人间出租',
            description: '安静的住宅区，非常适合学生。',
            price: '¥2000/月',
          },
        ],
      };
    },
  };
  </script>
  
  <style scoped>
  
  </style>
  