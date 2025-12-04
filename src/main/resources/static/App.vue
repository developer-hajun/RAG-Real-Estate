<template>
  <header>
    <div class="header-inner">
      <router-link to="/" class="logo">부동산 AI</router-link>
      <nav>
        <router-link to="/">홈</router-link>
        <router-link to="/realty">매물</router-link>
        <router-link to="/board">커뮤니티</router-link>
        <router-link to="/mypage" v-if="isLoggedIn">마이</router-link>
        <a @click="logout" v-if="isLoggedIn">로그아웃</a>
        <router-link to="/login" v-else>로그인</router-link>
      </nav>
    </div>
  </header>

  <div class="main-container">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
// 모듈 파일에서 DB와 api를 가져옵니다.
import { DB, api } from '@/api/mock'; 

const router = useRouter();
const isLoggedIn = computed(() => !!DB.session);

const logout = async () => {
  await api.auth.logout();
  alert("로그아웃 되었습니다.");
  router.push('/login');
};
</script>