<script setup>
import { ref, watch } from 'vue';
import { RouterView, RouterLink, useRoute, useRouter } from 'vue-router';
import './assets/style.css';

const route = useRoute();
const router = useRouter();
const isLoggedIn = ref(false);

// 로그인 상태 확인 함수
const checkLoginStatus = () => {
  const token = localStorage.getItem('accessToken');
  isLoggedIn.value = !!token; // 토큰이 있으면 true, 없으면 false
};

// 라우트가 변경될 때마다 로그인 상태를 다시 확인
// (로그인 -> 홈 이동, 로그아웃 -> 홈 이동 시 UI 갱신을 위해)
watch(route, () => {
  checkLoginStatus();
});

// 로그아웃 처리
const handleLogout = () => {
  if (confirm('로그아웃 하시겠습니까?')) {
    localStorage.clear();
    isLoggedIn.value = false;
    router.push('/'); // 홈으로 이동
  }
};
</script>

<template>
  <header class="glass">
    <div class="header-inner">
      <RouterLink to="/" class="logo">
        <span class="logo-icon">🏠</span> Toss Estate
      </RouterLink>
      
      <nav>
        <RouterLink to="/">홈</RouterLink>
        <RouterLink to="/map">지도</RouterLink>
        <RouterLink to="/community">커뮤니티</RouterLink>
        
        <RouterLink v-if="!isLoggedIn" to="/auth">로그인</RouterLink>
        
        <template v-else>
          <RouterLink to="/my">MY</RouterLink>
          <a @click.prevent="handleLogout" class="logout-link">로그아웃</a>
        </template>
      </nav>
    </div>
  </header>

  <main>
    <RouterView v-slot="{ Component }">
      <transition name="page" mode="out-in">
        <component :is="Component" />
      </transition>
    </RouterView>
  </main>
</template>

<style scoped>
header {
  position: fixed; top: 0; left: 0; right: 0;
  height: var(--header-h);
  z-index: 1000;
  display: flex; justify-content: center;
  background: rgba(255, 255, 255, 0.85); /* Glass 효과 보강 */
}

.header-inner {
  width: 100%; max-width: 1024px; padding: 0 24px;
  display: flex; justify-content: space-between; align-items: center;
}

.logo { font-size: 20px; font-weight: 800; color: var(--c-brand); display: flex; align-items: center; gap: 6px; }
.logo-icon { font-size: 24px; }

nav { display: flex; gap: 24px; align-items: center; }
nav a {
  font-size: 15px; font-weight: 600; color: var(--c-text-2);
  position: relative; padding: 8px 0; cursor: pointer;
  transition: color 0.2s;
}
nav a:hover, nav a.router-link-active { color: var(--c-brand); }

/* 로그아웃 버튼 스타일 (링크처럼 보이지만 기능 수행) */
.logout-link {
  color: var(--c-text-3);
}
.logout-link:hover {
  color: var(--c-danger); /* 마우스 올리면 빨간색 */
}

/* 네비게이션 밑줄 애니메이션 */
nav a:not(.logout-link)::after {
  content: ''; position: absolute; bottom: 0; left: 0; width: 0%; height: 2px;
  background: var(--c-brand); transition: width 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}
nav a.router-link-active::after { width: 100%; }
</style>