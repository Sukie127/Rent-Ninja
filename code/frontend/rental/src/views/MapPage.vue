<template>
  <div class="map-page-container">
    <!-- 搜索和过滤栏 -->
    <div class="search-filter-section">
      <div class="search-controls">
        <input type="text" v-model="searchQuery" placeholder="Search by title..." class="search-box" />
        <button @click="applyFilters" class="search-button">Search</button>
      </div>
      <div class="filter-controls">
        <label for="location">Filter by Location:</label>
        <select v-model="selectedLocation" class="filter-select">
          <option value="">All Locations</option>
          <option value="City Center">City Center</option>
          <option value="Suburbs">Suburbs</option>
        </select>

        <label for="price">Filter by Price Range:</label>
        <select v-model="selectedPriceRange" class="filter-select">
          <option value="">All Prices</option>
          <option value="low">Below ¥3000</option>
          <option value="medium">¥3000 - ¥5000</option>
          <option value="high">Above ¥5000</option>
        </select>
      </div>
    </div>

    <div class="content-section">
      <!-- 静态数据展示 -->
      <div class="listing-container">
        <div v-for="listing in filteredListings" :key="listing.id" class="listing-card">
          <img :src="listing.image" alt="listing" />
          <h3>{{ listing.title }}</h3>
          <p>{{ listing.description }}</p>
          <p class="price">${{ listing.price }}</p>
          <router-link :to="{ name: 'Detail', params: { id: listing.id } }" class="view-button">View</router-link>
        </div>
      </div>

      <!-- 地图部分 -->
      <div class="map-container" id="map"></div>
    </div>
  </div>
</template>

<script>
import L from "leaflet";
import "leaflet/dist/leaflet.css";

// 设置自定义 marker 图标
delete L.Icon.Default.prototype._getIconUrl;

L.Icon.Default.mergeOptions({
  iconUrl: require("@/assets/marker.png"),
});

export default {
  data() {
    return {
      map: null,
      markers: [],
      searchQuery: "",
      selectedLocation: "",
      selectedPriceRange: "",
      listings: [
        {
          id: 1,
          title: "Deluxe Apartment",
          description: "Convenient location near the city center.",
          price: 3000,
          image: require("@/assets/2.png"),
          location: "City Center",
          coordinates: [42.3601, -71.0589],
        },
        {
          id: 2,
          title: "Single Room for Rent",
          description: "Quiet residential area, perfect for students.",
          price: 2000,
          image: require("@/assets/3.png"),
          location: "Suburbs",
          coordinates: [42.3611, -71.0570],
        },
        {
          id: 3,
          title: "Luxury Apartment",
          description: "Modern facilities, ideal for long-term rental.",
          price: 5500,
          image: require("@/assets/4.png"),
          location: "City Center",
          coordinates: [42.3621, -71.0560],
        },
        {
          id: 4,
          title: "Cozy Studio Apartment",
          description: "Perfect for singles, close to amenities.",
          price: 2500,
          image: require("@/assets/2.png"),
          location: "City Center",
          coordinates: [42.3631, -71.0540],
        },
        {
          id: 5,
          title: "Modern Apartment",
          description: "Newly renovated, ready for move-in.",
          price: 4500,
          image: require("@/assets/3.png"),
          location: "Suburbs",
          coordinates: [42.3641, -71.0530],
        },
        {
          id: 6,
          title: "Spacious Loft",
          description: "Large space, ideal for professionals.",
          price: 5000,
          image: require("@/assets/4.png"),
          location: "City Center",
          coordinates: [42.3651, -71.0520],
        },
      ],
      filteredListings: [],
    };
  },
  mounted() {
    this.filteredListings = this.listings; // 初始化
    this.initMap();
    this.addMarkers();
  },
  methods: {
    applyFilters() {
      this.filteredListings = this.listings.filter((listing) => {
        const matchesSearch = listing.title.toLowerCase().includes(this.searchQuery.toLowerCase());
        const matchesLocation = this.selectedLocation === "" || listing.location === this.selectedLocation;
        let matchesPrice = true;
        if (this.selectedPriceRange === "low") {
          matchesPrice = listing.price < 3000;
        } else if (this.selectedPriceRange === "medium") {
          matchesPrice = listing.price >= 3000 && listing.price <= 5000;
        } else if (this.selectedPriceRange === "high") {
          matchesPrice = listing.price > 5000;
        }
        return matchesSearch && matchesLocation && matchesPrice;
      });
    },
    initMap() {
      this.map = L.map("map", { zoomControl: false }).setView([42.3601, -71.0589], 13);
      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: '© OpenStreetMap contributors',
      }).addTo(this.map);
    },
    addMarkers() {
      this.markers.forEach((marker) => this.map.removeLayer(marker));
      this.markers = [];

      this.listings.forEach((listing) => {
        const markerIcon = L.icon({
          iconUrl: require("@/assets/marker.png"),
          iconSize: [30, 30],
          iconAnchor: [15, 45],
        });

        const marker = L.marker(listing.coordinates, { icon: markerIcon }).addTo(this.map);

        marker.bindPopup(`<b>${listing.title}</b><br>${listing.description}`);

        marker.on("mouseover", () => {
          marker.setIcon(
            L.icon({
              iconUrl: require("@/assets/marker.png"),
              iconSize: [40, 40],
              iconAnchor: [20, 60],
            })
          );
        });

        marker.on("mouseout", () => {
          marker.setIcon(markerIcon);
        });

        marker.on("click", () => {
          this.$router.push({ name: "Detail", params: { id: listing.id } });
        });

        this.markers.push(marker);
      });
    },
  },
};
</script>

<style scoped>
.map-page-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding-top: 70px; /* 预留导航栏的空间 */
}

.search-filter-section {
  text-align: center;
  padding: 40px;
  background: linear-gradient(135deg, #f9f9f9, #e0f7fa);
  border-radius: 15px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-controls {
  display: flex;
  align-items: center;
  gap: 10px; /* 调整按钮和搜索框之间的距离 */
}

.filter-controls {
  display: flex;
  gap: 20px; /* 调整两个过滤框之间的距离 */
}

.search-box {
  padding: 15px;
  font-size: 16px;
  width: 300px;
  border-radius: 5px;
  border: 2px solid #4CAF50;
  transition: all 0.3s ease;
}

.search-box:focus {
  outline: none;
  border-color: #45a049;
  box-shadow: 0 0 15px rgba(76, 175, 80, 0.6);
}

.filter-select {
  padding: 15px;
  font-size: 16px;
  border-radius: 5px;
  border: 2px solid #4CAF50;
  transition: all 0.3s ease;
}

.filter-select:focus {
  outline: none;
  border-color: #45a049;
  box-shadow: 0 0 15px rgba(76, 175, 80, 0.6);
}

/* 搜索按钮样式 */
.search-button {
  padding: 15px 30px;
  background-color: #4CAF50;
  color: white;
  font-size: 16px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.3s ease;
}

.search-button:hover {
  background-color: #45a049;
  transform: scale(1.05);
}

.content-section {
  display: flex;
  flex-grow: 1;
}

.listing-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  padding: 20px;
  overflow-y: scroll; /* 使房源列表支持滚动 */
  border-right: 1px solid #ddd;
  width: 40%;
  max-height: calc(100vh - 160px); /* 根据可视区域动态调整房源列表的高度 */
}

.listing-card {
  background-color: #fff;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.listing-card img {
  width: 100%;
  height: 150px;
  border-radius: 8px;
  margin-bottom: 10px;
  object-fit: cover;
}

.price {
  font-weight: bold;
  color: #4CAF50;
}

.view-button {
  display: inline-block;
  padding: 5px 10px;
  margin-top: 10px;
  background-color: #4CAF50;
  color: #fff;
  text-decoration: none;
  border-radius: 5px;
}

.map-container {
  width: 60%;
  height: 100%;
}

#map {
  width: 100%;
  height: 100%;
}
</style>
