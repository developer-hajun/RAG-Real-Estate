import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import MapSearch from '../views/MapSearch.vue';
import Community from '../views/Community.vue';
import PostDetail from '../views/PostDetail.vue';
import AuthView from '../views/AuthView.vue';
import MyPage from '../views/MyPage.vue';

// ⚙️ 새로 추가될 설정 페이지들
import NotificationSetting from '../views/settings/NotificationSetting.vue';
import PasswordChange from '../views/settings/PasswordChange.vue';
import TermsList from '../views/settings/TermsList.vue';
import DeleteAccount from '../views/settings/DeleteAccount.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', component: HomeView },
    { path: '/map', component: MapSearch },
    { path: '/community', component: Community },
    { path: '/community/:id', component: PostDetail },
    { path: '/auth', component: AuthView },
    
    // 마이페이지
    { path: '/my', component: MyPage, meta: { requiresAuth: true } },

    // ⚙️ 설정 하위 페이지 (전부 로그인 필요)
    { path: '/my/notification', component: NotificationSetting, meta: { requiresAuth: true } },
    { path: '/my/password', component: PasswordChange, meta: { requiresAuth: true } },
    { path: '/my/terms', component: TermsList, meta: { requiresAuth: true } },
    { path: '/my/leave', component: DeleteAccount, meta: { requiresAuth: true } },
  ]
});

// 문지기 (로그인 체크)
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !localStorage.getItem('accessToken')) {
    alert('로그인이 필요한 서비스입니다.');
    next('/auth');
  } else {
    next();
  }
});

export default router;