<template>
  <div class="profile-page">
    <section class="new-listing-section">
    <h2 class="center-text">Post Update</h2>
    <form @submit.prevent="updatePost" class="new-listing-form">
      <div class="input-group">
        <label for="title">Title:</label>
        <input type="text" v-model="listing.title" class="input-field" required />
      </div>

      <div class="input-group">
        <label for="description">Content:</label>
        <textarea v-model="listing.content" class="input-field" required></textarea>
      </div>

      <div class="input-group">
        <label for="price">Price:</label>
        <input type="number" v-model="listing.price" class="input-field" required />
      </div>

      <div class="location">
        <h3>Location</h3>
        <div class="dropdowns">
          <select v-model="selectedCountry" @change="fetchStates">
            <option value="">Select Country</option>
            <option>USA</option>
          </select>

          <select v-model="selectedState" @change="fetchCities" :disabled="!selectedCountry">
            <option value="">Select State/Province</option>
            <option v-for="(state, index) in states" :key="index" :value="state">{{ state }}</option>
          </select>

          <select v-model="selectedCity" @change="fetchCities" :disabled="!selectedState">
            <option value="">Select City</option>
            <option v-for="(city, index) in cities" :key="index" :value="city">{{ city }}</option>
            
          </select>
        </div>
      </div>

      <div v-if="listing.picUrls && isValidURL(listing.picUrls)">
        <h4>Current Images:</h4>
          <img :src="listing.picUrls" class="carousel-image" />
      </div>
      <div v-else>
        <h4>Current Images:</h4>
        <img :src="require('@/assets/1.png')" class="carousel-image" />
      </div>

      <div>
        <label for="newImage">Upload New Image:</label>
        <input type="file" id="newImage" @change="onImageChange" />
      </div>

      <div class="input-group">
        <label for="contact-info">Contact Information:</label>
        <input type="text" v-model="listing.contactInfo" class="input-field" required />
      </div>

      <button type="submit" class="btn-primary">Submit Update</button>
    </form>
  </section>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: "EditingPage",
  data() {
    return {
      state: '',
      city: '',
      selectedCity: '',
      selectedCountry: '',
      selectedState: '',
      pos:[],
      countries: ['USA'],
      states: ['MA', 'NY', 'CA', 'TX', 'FL', 'IL', 'PA', 'OH', 'MI', 'GA'], // 10个州
      citiesByState: {
        'MA': ['Boston', 'Cambridge', 'Springfield', 'Worcester', 'Lowell', 'Quincy', 'Lynn', 'Newton', 'Somerville', 'Lawrence'],
        'NY': ['New York City', 'Buffalo', 'Rochester', 'Albany', 'Syracuse', 'Yonkers', 'Schenectady', 'Utica', 'White Plains', 'Ithaca'],
        'CA': ['Los Angeles', 'San Francisco', 'San Diego', 'Sacramento', 'Fresno', 'Oakland', 'San Jose', 'Bakersfield', 'Anaheim', 'Long Beach'],
        'TX': ['Houston', 'Austin', 'Dallas', 'San Antonio', 'Fort Worth', 'El Paso', 'Arlington', 'Corpus Christi', 'Plano', 'Laredo'],
        'FL': ['Miami', 'Orlando', 'Tampa', 'Jacksonville', 'St. Petersburg', 'Hialeah', 'Tallahassee', 'Fort Lauderdale', 'Cape Coral', 'Pembroke Pines'],
        'IL': ['Chicago', 'Springfield', 'Peoria', 'Naperville', 'Rockford', 'Joliet', 'Evanston', 'Cicero', 'Champaign', 'Elgin'],
        'PA': ['Philadelphia', 'Pittsburgh', 'Allentown', 'Harrisburg', 'Erie', 'Scranton', 'Lancaster', 'Bethlehem', 'Reading', 'York'],
        'OH': ['Columbus', 'Cleveland', 'Cincinnati', 'Toledo', 'Akron', 'Dayton', 'Parma', 'Canton', 'Youngstown', 'Lorain'],
        'MI': ['Detroit', 'Ann Arbor', 'Grand Rapids', 'Lansing', 'Flint', 'Dearborn', 'Warren', 'Sterling Heights', 'Kalamazoo', 'Livonia'],
        'GA': ['Atlanta', 'Savannah', 'Augusta', 'Athens', 'Macon', 'Columbus', 'Roswell', 'Albany', 'Marietta', 'Sandy Springs']
      },

      cities: [],
      listing: {
        postId: "",
        title: "",
        content: "",
        contactInfo: "",
        picUrls: "",
        countryCode: "",
        stateCode: "",
        cityCode: "",
        price: 0,
        area:"",
        image:"",
      }
    };
  },
  created() {
    this.getPostDetail();
  },
  methods: {
    // get the post detail
    async getPostDetail() {
      const postId = this.$route.params.postId;
      const idToken = localStorage.getItem('idToken');
      if (!idToken) {
        console.error('ID Token not found in localStorage.');
        return;
      }
      console.log(idToken);
      try {
        const response = await axios.post('https://api.rentalninja.link/get-post-detail', {
        postId: postId,
      }, {
        headers: {
          'Authorization': `Bearer ${idToken}`, 
          'Content-Type': 'application/json'
        }
      });

      console.log('Editing page getPostDetail response:', response); 
      this.listing = response.data.responseBody.post;
      } catch (error) {
        console.error('Error fetching post details:', error);
      }
    },
    fetchCities() {
      console.log('Selected State:', this.selectedState);
      this.cities = this.citiesByState[this.selectedState] || [];
      console.log('Available Cities:', this.cities);
      console.log("Citycode:" , this.selectedCity)
      this.listing.stateCode = this.selectedState;
      this.listing.cityCode = this.selectedCity;
      this.listing.countryCode = this.selectedCountry;
      console.log(this.selectedCity, this.selectedCountry, this.selectedState);
    },


    // upload image handler
    async uploadImage(file) {
      try {
        const token = localStorage.getItem('idToken'); 
        if (!token) {
          throw new Error('token is null');
        }

        // get presign 
        const presignedResponse = await fetch('https://api.rentalninja.link/get-presigned-url', { 
          method: 'POST',
          headers: {
            Authorization: `Bearer ${token}`, 
          },
        });

        if (!presignedResponse.ok) {
          throw new Error('get presign failed');
        }

        const responseBody = await presignedResponse.json();
        console.log('Response Body:', responseBody);
        const { presignedUrl } = await responseBody.responseBody; 
        console.log('URL: ', presignedUrl);

        const uploadResponse = await fetch(presignedUrl, {
          method: 'PUT',
          body: file,
          headers: {
            'Content-Type': file.type, 
          },
        });

        if (!uploadResponse.ok) {
          throw new Error('upload image failed');
        }

        return presignedUrl.split('?')[0]; 

      } catch (error) {
        console.error('upload image error:', error);
        throw error;
      }
    },

    
    // Update the post detail
    async updatePost() {
      //const postId = this.$route.params.postId;
      try {
        const idToken = localStorage.getItem('idToken');
        if (!idToken) {
          console.error('ID Token not found in localStorage.');
          return;
        }
        console.log(idToken);

        //let imageUrls = [];
        let imageUrl = "";
        if (this.listing.image) {
          imageUrl = await this.uploadImage(this.listing.image);
          //imageUrls.push(imageUrl);
        }

        this.listing.picUrls = imageUrl;
        
        const response = await axios.post(`https://api.rentalninja.link/update-my-post`, 
        this.listing,
        { headers: {
          'Authorization': `Bearer ${idToken}`, 
          'Content-Type': 'application/json'
        }}
      );
        if (response.status === 200) {
          alert('post updated successfully!');
          this.$router.push('/ProfilePage'); 
          console.error('Failed to update the post.');
        }
      } catch (error) {
        console.error('Error updating the post:', error);
      }
    },


    // image change handler
    onImageChange(event) {
      const file = event.target.files[0];
      this.listing.image = file;
    },

    isValidURL(url) {
      try {
        new URL(url); 
        return true;  
      } catch (e) {
        return false;
      }
    },
  }
};
</script>

<style scoped>
.pagination {
display: flex;             
justify-content: center;    
align-items: center;         
gap: 10px;                  
}

button {

padding: 10px 25px;
width: auto;
cursor: pointer;
border: 2px solid #3498db;   
background-color: white;      
color: #3498db;             
transition: background-color 0.3s, color 0.3s, border-color 0.3s; 
}

button:hover {
background-color: #3498db;   
color: white;                
border-color: #2980b9;        
}

button:disabled {
cursor: not-allowed;        
opacity: 0.5;               
}

span {
font-size: 1.2em;            
font-weight: bold;        
}

</style>