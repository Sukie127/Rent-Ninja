<template>
  <div class="profile-page">
    <h1>Profile Page</h1>

    <!-- 个人资料 -->
    <section class="profile-section">
      <h2>Profile Information</h2>
      <div class="profile-info">
        <div class="avatar">
          <img :src="profile.avatar" alt="Avatar" class="profile-avatar" />
          <input type="file" @change="onAvatarChange" />
        </div>
        <div class="details">
          <label for="name">Name:</label>
          <input type="text" v-model="profile.name" class="input-field" />

          <label for="email">Email:</label>
          <input type="email" v-model="profile.email" class="input-field" />
          <button @click="saveProfile" class="btn-primary">Save Changes</button>
        </div>
      </div>
    </section>

    <!-- 收藏的房源 -->
    <section class="favorites-section">
      <h2>Favorite Listings</h2>
      <div v-if="favoriteListings.length === 0">
        <p>You haven't saved any listings yet.</p>
      </div>
      <div v-else class="favorites-list">
        <div class="favorite-item" v-for="(listing, index) in favoriteListings" :key="index">
          <img :src="listing.image" alt="Listing Image" />
          <h3>{{ listing.title }}</h3>
          <p>{{ listing.description }}</p>
          <button @click="removeFromFavorites(index)" class="btn-secondary">Remove from Favorites</button>
        </div>
      </div>
    </section>

    <!-- 已发布的房源 -->
    <section class="published-listings-section">
      <h2>Published Listings</h2>
      <div v-if="publishedListings.length === 0">
        <p>You haven't published any listings yet.</p>
      </div>
      <div v-else class="published-list">
        <div class="published-item" v-for="(listing, index) in publishedListings" :key="index">
          <img :src="listing.image" alt="Listing Image" />
          <h3>{{ listing.title }}</h3>
          <p>{{ listing.description }}</p>
          <button @click="editListing(listing.id)" class="btn-secondary">Edit</button>
          <button @click="removeListing(index)" class="btn-danger">Delete</button>
        </div>
      </div>
    </section>

    <!-- 修改密码 -->
    <section class="change-password-section">
      <h2>Change Password</h2>
      <div class="password-form">
        <label for="current-password">Current Password:</label>
        <input type="password" v-model="currentPassword" class="input-field" />

        <label for="new-password">New Password:</label>
        <input type="password" v-model="newPassword" class="input-field" />

        <label for="confirm-password">Confirm New Password:</label>
        <input type="password" v-model="confirmPassword" class="input-field" />

        <button @click="changePassword" class="btn-primary">Change Password</button>
      </div>
    </section>

    <!-- 发布新房源 -->
    <section class="new-listing-section">
      <h2>Post New Listing</h2>
      <form @submit.prevent="submitNewListing" class="new-listing-form">
        <div class="input-group">
          <label for="title">Title:</label>
          <input type="text" v-model="newListing.title" class="input-field" required />
        </div>

        <div class="input-group">
          <label for="description">Description:</label>
          <textarea v-model="newListing.description" class="input-field" required></textarea>
        </div>

        <div class="input-group">
          <label for="price">Price:</label>
          <input type="number" v-model="newListing.price" class="input-field" required />
        </div>

        <div class="input-group">
          <label for="location">Location:</label>
          <input type="text" v-model="newListing.location" class="input-field" required />
        </div>

        <div class="input-group">
          <label for="image">Listing Image:</label>
          <input type="file" @change="onImageChange" />
        </div>

        <button type="submit" class="btn-primary">Post Listing</button>
      </form>
    </section>
  </div>
</template>

<script>
export default {
  data() {
    return {
      profile: {
        avatar: require('@/assets/1.png'), // Default avatar
        name: 'User Name',
        email: 'user@example.com',
      },
      favoriteListings: [
        {
          id: 1,
          image: require('@/assets/1.png'),
          title: 'Deluxe Apartment',
          description: 'Conveniently located near the city center.',
        },
        {
          id: 2,
          image: require('@/assets/2.png'),
          title: 'Luxury Villa',
          description: 'A high-end villa with a private pool and garden.',
        },
      ],
      publishedListings: [
        {
          id: 3,
          image: require('@/assets/3.png'),
          title: 'Luxury Apartment in City Center',
          description: 'Modern renovation, fully equipped.',
        },
        {
          id: 4,
          image: require('@/assets/4.png'),
          title: 'Cozy Single Room Near Metro Station',
          description: 'Convenient location with nearby facilities.',
        },
      ],
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
      newListing: {
        title: '',
        description: '',
        price: '',
        location: '',
        image: null,
      },
    };
  },
  methods: {
    onAvatarChange(event) {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.profile.avatar = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    },
    saveProfile() {
      alert('Profile updated');
    },
    removeFromFavorites(index) {
      this.favoriteListings.splice(index, 1);
      alert('Removed from favorites');
    },
    editListing(id) {
      this.$router.push({ name: 'EditListing', params: { id } });
    },
    removeListing(index) {
      this.publishedListings.splice(index, 1);
      alert('Listing deleted');
    },
    changePassword() {
      if (this.newPassword === this.confirmPassword) {
        alert('Password updated');
      } else {
        alert('New password and confirmation do not match');
      }
    },
    submitNewListing() {
      // 目前发布新房源只是展示在已发布的房源列表中
      this.publishedListings.push({
        id: Date.now(), // 用当前时间模拟唯一 ID
        image: this.newListing.image ? URL.createObjectURL(this.newListing.image) : require('@/assets/1.png'),
        title: this.newListing.title,
        description: this.newListing.description,
        price: this.newListing.price,
        location: this.newListing.location,
      });

      // 清空表单
      this.newListing = {
        title: '',
        description: '',
        price: '',
        location: '',
        image: null,
      };

      alert('Listing posted successfully');
    },
    onImageChange(event) {
      const file = event.target.files[0];
      this.newListing.image = file;
    },
  },
};
</script>

<style scoped>

.profile-page {
  padding: 60px 20px;
  max-width: 1000px;
  margin: 0 auto;
  text-align: center;
}


.profile-section,
.favorites-section,
.published-listings-section,
.change-password-section {
  margin-bottom: 40px;
}

.profile-info {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
}


.profile-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  margin-bottom: 10px;
}


.details,
.password-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
  align-items: center;
}


.favorites-list,
.published-list {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

.favorite-item,
.published-item {
  width: 220px;
  text-align: center;
}

.favorite-item img,
.published-item img {
  width: 100%;
  border-radius: 10px;
}


.input-field {
  padding: 10px;
  border: 2px solid #4CAF50;
  border-radius: 5px;
  width: 300px;
}


button {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #45a049;
}


.btn-primary {
  background-color: #4CAF50;
}

.btn-primary:hover {
  background-color: #45a049;
}

.btn-secondary {
  background-color: #2196F3;
}

.btn-secondary:hover {
  background-color: #1976D2;
}

.btn-danger {
  background-color: #F44336;
}

.btn-danger:hover {
  background-color: #D32F2F;
}


.new-listing-section {
  margin-top: 40px;
}

.new-listing-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
}

.input-group label {
  font-weight: bold;
}

.input-group input,
.input-group textarea {
  width: 100%;
  padding: 10px;
  border: 2px solid #4CAF50;
  border-radius: 5px;
}
</style>
