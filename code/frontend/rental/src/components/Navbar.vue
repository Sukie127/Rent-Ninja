<template>
  <nav class="navbar">
    <div class="nav-links">
      <router-link to="/">Home</router-link>
      <router-link to="/listing">Listings</router-link>
      <router-link to="/FavoritesPage">Favorites</router-link>
      <router-link to="/map">MapPage</router-link>
      <router-link to="/contact">Contact Us</router-link>
      <router-link to="/ProfilePage">Profile</router-link>
    </div>
    <div class="auth-links">
      <router-link to="/LoginPage">Login</router-link>
      <router-link to="/RegisterPage">Register</router-link>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'SiteNavbar'
}
</script>

  <style scoped>

  </style>
  