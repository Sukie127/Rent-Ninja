<template>
    <div class="sidebar" @mouseover="openSidebar" @mouseleave="closeSidebar">
      <router-link to="/">首页</router-link>
      <router-link to="/about">关于我们</router-link>
      <router-link to="/services">服务</router-link>
      <router-link to="/contact">联系方式</router-link>
    </div>
  </template>
  
  <script>
  export default {
    name: 'AppSidebar',
    data() {
      return {
        isOpen: false
      };
    },
    methods: {
      openSidebar() {
        this.isOpen = true;
      },
      closeSidebar() {
        this.isOpen = false;
      }
    }
  }
  </script>
  
  <style scoped>
  
  </style>
  