<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();

const terms = [
  { id: 1, title: '서비스 이용약관', required: true },
  { id: 2, title: '개인정보 처리방침', required: true },
  { id: 3, title: '위치기반 서비스 이용약관', required: true },
  { id: 4, title: '마케팅 정보 수신 동의', required: false },
];
</script>

<template>
  <div class="main-container">
    <div class="nav-header">
      <button @click="router.back()">← 뒤로</button>
      <h2>이용약관</h2>
    </div>

    <div class="card term-list">
      <div v-for="t in terms" :key="t.id" class="term-item">
        <span>{{ t.title }}</span>
        <div class="right">
          <span class="badge" :class="t.required ? 'req' : 'opt'">{{ t.required ? '필수' : '선택' }}</span>
          <span class="arrow">></span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.nav-header { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }
.term-item { padding: 16px 0; border-bottom: 1px solid #F2F4F6; display: flex; justify-content: space-between; align-items: center; cursor: pointer; }
.term-item:last-child { border-bottom: none; }
.right { display: flex; align-items: center; gap: 10px; }
.badge { font-size: 12px; padding: 4px 8px; border-radius: 4px; font-weight: 600; }
.badge.req { background: #E8F3FF; color: #3182F6; }
.badge.opt { background: #F2F4F6; color: #8B95A1; }
.arrow { color: #D1D6DB; }
</style>