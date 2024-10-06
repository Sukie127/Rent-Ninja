<template>
  <div class="detail-page">
    <h1>{{ pos.title }}</h1>

    <div class="carousel-container">
      
      <div class="carousel">
        

        <div v-if="pos.picUrls == null">
          <img :src="require('@/assets/2.png')" :alt="pos.title" class="carousel-image" @click="viewFullImage(currentImage)" />
        </div>
        <div v-else>

        <div class="carousel-item">
          <img
            :src="currentImage"
            :alt="pos.title"
            class="carousel-image"
            @click="viewFullImage(currentImage)"
          />
        </div>


        <div class="button-container">
          <button @click="prevImage" class="prev-button">Previous</button>
          <button @click="nextImage" class="next-button">Next</button>
        </div>
      </div>

      
      </div>
      
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

// Retrieve the tokens from localStorage
const idToken = localStorage.getItem('idToken');
console.log('Added to favorites request data:',idToken);
if (!idToken) {
  alert('You must be logged in to perform this action');
}

// Set up the headers with the token
const headers = {
  Authorization: `Bearer ${idToken}`,
  'Content-Type': 'application/json'
};


export default {
  name: 'DetailPage',
  data() {
    return {
      currentImageIndex: 0, 
      pos:{},
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
  computed: {
    currentImage() {
      if (!this.pos.picUrls || !this.pos.picUrls.length) {
        return require('@/assets/2.png');
      }

      const image = this.pos.picUrls[this.currentImageIndex];
      if (!image || !image.includes('https://rentalninja.s3.us-east-2.amazonaws.com/')) {
        return require('@/assets/2.png');
      }

   
      return image;
    }
  },
  mounted() {
    // 初始化地图
    this.initializeMap();
    this.get_postdetail();
  },
  methods: {

    prevImage() {
      // 切换到前一张图片，如果已经是第一张，则回到最后一张
      if (this.currentImageIndex > 0) {
        this.currentImageIndex--;
      } else {
        this.currentImageIndex = this.pos.picUrls.length - 1;
      }
    },
    nextImage() {
      // 切换到下一张图片，如果已经是最后一张，则回到第一张
      if (this.currentImageIndex < this.pos.picUrls.length - 1) {
        this.currentImageIndex++;
      } else {
        this.currentImageIndex = 0;
      }
    },
    viewFullImage(image) {
      // 点击图片后处理全屏查看的逻辑
      console.log('View full image:', image);
    },



    async get_postdetail() {
      try {
        const idToken = localStorage.getItem('idToken');
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
       this.isFavorite = !!this.pos.isFavorite;
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
      // Toggle the favorite status
      this.isFavorite = !this.isFavorite;

      // Call the favorite API based on the favorite state
      if (this.isFavorite) {
        // User is favoriting the post, call the "add to favorites" API
        axios.post('https://api.rentalninja.link/add-collection', {
          postId: this.pos.postId,
          isAdd: 1,
        }, { headers })
        .then(response => {
          console.log('Added to favorites:', response.data);
        })
        .catch(error => {
          console.error('Error adding to favorites:', error);
          // Optionally revert the state in case of error
          this.isFavorite = false;
        });
      } else {
        console.log('Removed to favorites request data:', {
          postId: this.pos.postId,
          isAdd: 0,
        });
        // User is unfavoriting the post, call the "remove from favorites" API
        axios.post('https://api.rentalninja.link/add-collection', {
          postId: this.pos.postId,
          isAdd: 0,
        }, { headers })
        .then(response => {
          console.log('Removed from favorites:', response.data);
        })
        .catch(error => {
          console.error('Error removing from favorites:', error);
          // Optionally revert the state in case of error
          this.isFavorite = true;
        });
      }
    },

    goToLandlordPage() {
      this.$router.push({ name: 'LandlordPage', params: { landlordId: this.listing.landlordId } });
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
.button-container {
  display: flex;
  justify-content: space-between;
  width: 100%; /* 你可以根据需要调整宽度 */
  margin-top: 20px; /* 可以根据需要调整间距 */
}

.prev-button,
.next-button {
  padding: 10px 20px; /* 设置按钮的内边距 */
  font-size: 16px; /* 调整按钮的字体大小 */
  cursor: pointer; /* 鼠标悬停时显示为手型 */
}

</style>
