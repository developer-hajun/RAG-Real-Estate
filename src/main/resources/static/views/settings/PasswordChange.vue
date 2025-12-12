<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
const router = useRouter();

const form = ref({ current: '', new: '', confirm: '' });

const isMatch = computed(() => form.value.new && form.value.new === form.value.confirm);
const canSubmit = computed(() => form.value.current && isMatch.value);

const submit = () => {
  // api.changePassword(...)
  alert('비밀번호가 성공적으로 변경되었습니다. 다시 로그인해주세요.');
  localStorage.clear();
  router.push('/auth');
};
</script>

<template>
  <div class="main-container">
    <div class="nav-header">
      <button @click="router.back()">← 뒤로</button>
      <h2>비밀번호 변경</h2>
    </div>

    <div class="card">
      <div class="input-group">
        <label>현재 비밀번호</label>
        <input type="password" v-model="form.current" placeholder="현재 비밀번호 입력">
      </div>
      
      <div class="input-group">
        <label>새 비밀번호</label>
        <input type="password" v-model="form.new" placeholder="새 비밀번호 입력">
      </div>

      <div class="input-group">
        <label>새 비밀번호 확인</label>
        <input type="password" v-model="form.confirm" placeholder="한 번 더 입력해주세요">
        <p v-if="form.new && !isMatch" class="error-msg">비밀번호가 일치하지 않습니다.</p>
        <p v-if="isMatch" class="success-msg">비밀번호가 일치합니다.</p>
      </div>

      <button class="btn" :disabled="!canSubmit" @click="submit" style="margin-top: 20px;">
        변경하기
      </button>
    </div>
  </div>
</template>

<style scoped>
.nav-header { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }
.input-group { margin-bottom: 20px; }
.input-group label { display: block; font-weight: 700; margin-bottom: 8px; font-size: 14px; color: #4E5968; }
.error-msg { color: #FF3B30; font-size: 13px; margin-top: 6px; }
.success-msg { color: #3182F6; font-size: 13px; margin-top: 6px; }
button:disabled { background: #D1D6DB; cursor: not-allowed; }
</style>