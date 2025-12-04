<template>
  <header>
    <div class="header-inner">
      <router-link to="/" class="logo">부동산 AI</router-link>
      <nav>
        <router-link to="/" class="nav-link">매물</router-link>
        <router-link to="/board" class="nav-link">커뮤니티</router-link>
        <a v-if="isLoggedIn" @click="logout" class="nav-link">로그아웃</a>
        <router-link v-else to="/login" class="nav-link">로그인</router-link>
      </nav>
    </div>
  </header>

  <div class="container">
    <router-view />
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { DB, api } from '@/api/mock';

const router = useRouter();
const isLoggedIn = computed(() => !!DB.session);

const logout = async () => {
  await api.auth.logout();
  alert("로그아웃 되었습니다.");
  router.push('/login');
};
</script>

<style src="@/assets/style.css"></style>