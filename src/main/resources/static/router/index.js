// /src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import RealtyListView from '../views/RealtyListView.vue';
import RealtyDetailView from '../views/RealtyDetailView.vue';
import BoardView from '../views/BoardView.vue';
import PostDetailView from '../views/PostDetailView.vue';
import MyPageView from '../views/MyPageView.vue';
import LoginView from '../views/LoginView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/realty',
      name: 'realtyList',
      component: RealtyListView
    },
    {
      path: '/realty/:id',
      name: 'realtyDetail',
      component: RealtyDetailView
    },
    {
      path: '/board',
      name: 'board',
      component: BoardView
    },
    {
      path: '/board/:id',
      name: 'postDetail',
      component: PostDetailView
    },
    {
      path: '/mypage',
      name: 'mypage',
      component: MyPageView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    }
  ]
});

export default router;