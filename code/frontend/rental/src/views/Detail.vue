<template>
  <div class="detail-page">
    <h1>{{ pos.title }}</h1>

    <div class="carousel-container">
      <button @click="prevSlide" class="carousel-btn prev-btn">‹</button>
      <div class="carousel">
        

        <div v-if="pos.picUrls == null">

          <img :src="require('@/assets/2.png')" :alt="pos.title" class="carousel-image" @click="viewFullImage(image)" />
        </div>
        <div v-else>
          <div v-for="(image, index) in pos.picUrls" :key="index" class="carousel-item">
          <img :src="image" :alt="pos.title" class="carousel-image" @click="viewFullImage(image)" />
        </div>
        </div>
      </div>
      <button @click="nextSlide" class="carousel-btn next-btn">›</button>
    </div>

    <div class="detail-content">
      <div class="detail-info">
        <p><strong>Price:</strong>{{ pos.price }}</p>
        <p><strong>Description:</strong>{{ pos.content }}</p>
        <p><strong>Country:</strong>{{ pos.countryCode }}</p>
        <p><strong>Location:</strong>{{ pos.cityCode }}</p>
        <p><strong>Contact:</strong>{{ pos.contactInfo }}</p>

    

        <button @click="toggleFavorite" :class="{ 'favorited': isFavorite }" class="favorite-button">
          {{ isFavorite ? 'Unfavorite' : 'Favorite' }}
        </button>

        
      </div>
    </div>

    <!-- 地图 -->
    <div id="map" class="map"></div>

    <div v-if="showModal" class="modal" @click="closeModal">
      <span class="close-btn">&times;</span>
      <img class="modal-content" :src="fullImageUrl" />
    </div>

    <router-link to="/listing" class="back-link">Back to Listings</router-link>
  </div>
</template>

<script>
import axios from 'axios';
import 'ol/ol.css';
import { Map, View } from 'ol';
import { Tile as TileLayer, Vector as VectorLayer } from 'ol/layer';
import { OSM, Vector as VectorSource } from 'ol/source';
import { fromLonLat } from 'ol/proj';
import Feature from 'ol/Feature';
import Point from 'ol/geom/Point';
import { Icon, Style } from 'ol/style';

export default {
  name: 'DetailPage',
  data() {
    return {
      pos:[],
      listing: {
        id: 1,
        images: [
          require('@/assets/1.png'),
          require('@/assets/2.png'),
          require('@/assets/3.png'),
        ],
        title: 'Deluxe Apartment',
        description: 'Convenient location near the city center.',
        price: '¥3000/month',
        location: 'Suburbs',
        type: 'Apartment',
        contact: 'Landlord Contact: 123-456-7891',
        features: ['Air conditioning', 'Private bathroom', 'Wi-Fi', '24-hour hot water'],
        coordinates: [120.130663, 30.240019], // 经纬度
        landlordId: 101,
      },
      isFavorite: false,
      showModal: false,
      fullImageUrl: '',
      map: null,
    };
  },
  mounted() {
    // 初始化地图
    this.initializeMap();
    this.get_postdetail();
  },
  methods: {

    async get_postdetail() {
      try {
        const idToken = localStorage.getItem('id');
        if (!idToken) {
          console.error('ID Token not found in localStorage.');
          return;
        }
        console.log(idToken);
        console.log(localStorage.getItem("postid"));
        const response = await axios.post('https://api.rentalninja.link/get-post-detail', {
          postId: localStorage.getItem("postid"),
        }, {
          headers: {
            'Authorization': `Bearer ${idToken}`, 
            'Content-Type': 'application/json'
          }
        });

        console.log('Full response:', response); 
       this.pos = response.data.responseBody.post;
      } catch (e) {
        console.error('GET call failed: ', e.message);
        if (e.response) {
          console.error('Response Error:', e.response);
        }
      }
    
  
    },
    initializeMap() {
      const coordinates = fromLonLat(this.listing.coordinates);

      // 在地图上放置标记
      const marker = new Feature({
        geometry: new Point(coordinates),
      });
      marker.setStyle(
        new Style({
          image: new Icon({
            src: 'https://openlayers.org/en/v4.6.5/examples/data/icon.png', // 标记图标
            scale: 1, // 控制图标大小
          }),
        })
      );

      // 向地图添加矢量层
      const vectorLayer = new VectorLayer({
        source: new VectorSource({
          features: [marker],
        }),
      });

      // 初始化地图
      this.map = new Map({
        target: 'map',
        layers: [
          new TileLayer({
            source: new OSM(),
          }),
          vectorLayer,
        ],
        view: new View({
          center: coordinates,
          zoom: 14,
        }),
      });
    },
    toggleFavorite() {
      this.isFavorite = !this.isFavorite;
      console.log(this.isFavorite ? 'Favorited' : 'Unfavorited');
    },
    goToLandlordPage() {
      this.$router.push({ name: 'LandlordPage', params: { landlordId: this.listing.landlordId } });
    },
    prevSlide() {
      const carousel = this.$el.querySelector('.carousel');
      carousel.scrollBy({
        left: -carousel.clientWidth,
        behavior: 'smooth',
      });
    },
    nextSlide() {
      const carousel = this.$el.querySelector('.carousel');
      carousel.scrollBy({
        left: carousel.clientWidth,
        behavior: 'smooth',
      });
    },
    viewFullImage(image) {
      this.fullImageUrl = image;
      this.showModal = true;
    },
    closeModal() {
      this.showModal = false;
    },
  },
};
</script>

<style scoped>
#map {
  width: 100%;
  height: 400px;
  margin-top: 20px;
}
</style>
