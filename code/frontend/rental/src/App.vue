<template>
  <div id="app">
    <Navbar />
    <AppSidebar />
    <router-view />
  </div>
</template>

<script>
import Navbar from './components/Navbar.vue'
import {Amplify} from "aws-amplify";

const currentConfig = Amplify.getConfig();
Amplify.configure({
  ...currentConfig,
  API: {
    REST: {
      RentalNinja: {
        endpoint: "https://api.rentalninja.link",
        region: "us-east-2",
      },
    },
  },
});


export default {
  name: 'App',
  components: {
    Navbar,
    
  }
}
</script>
