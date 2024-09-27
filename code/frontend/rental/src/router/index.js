import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/views/HomePage.vue';
import Listing from '@/views/Listing.vue';
import Detail from '@/views/Detail.vue';
import ProfilePage from '@/views/ProfilePage.vue'
import LoginPage from '@/views/LoginPage.vue'
import RegisterPage from '@/views/RegisterPage.vue'
import FavoritesPage from '@/views/FavoritesPage.vue'
import MapPage from '@/views/MapPage.vue'
const routes = [
  {
    path: '/map',
    name: 'MapPage',
    component: MapPage
  },
  {
    path: '/',
    name: 'HomePage',
    component: HomePage
  },
  {
    path: '/listing',
    name: 'Listing',
    component: Listing
  },
  {
    path: '/RegisterPage',
    name: 'RegisterPage',
    component: RegisterPage
  },
  {
    path: '/LoginPage',
    name: 'LoginPage',
    component: LoginPage
  },
  {
    path: '/ProfilePage',
    name: 'ProfilePage',
    component: ProfilePage
  },
  {
    path: '/FavoritesPage',
    name: 'FavoritesPage',
    component: FavoritesPage
  },
  {
    path: '/detail/:id', // 在这里添加 :id 参数
    name: 'Detail',
    component: Detail
  },
  
  {
    path: '/landlord/:landlordId',
    name: 'LandlordPage',
    component: () => import('@/views/LandlordPage.vue'),
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

export default router;
