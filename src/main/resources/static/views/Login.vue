<template>
  <div style="max-width:400px; margin:40px auto; text-align:center;">
    <h2 style="margin-bottom:20px;">로그인</h2>
    <div class="card">
      <input v-model="form.email" class="input-box" placeholder="이메일 (user@test.com)">
      <input
          v-model="form.pw"
          type="password"
          class="input-box"
          placeholder="비밀번호 (123)"
          @keyup.enter="doLogin"
      >
      <button class="btn" style="width:100%; margin-top:10px;" @click="doLogin">
        로그인
      </button>
    </div>
    <p style="font-size:13px; color:#888; margin-top:10px;">
      테스트 계정: user@test.com / 123
    </p>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { api } from '@/api/mock';

const router = useRouter();
const form = reactive({ email: '', pw: '' });

const doLogin = async () => {
  try {
    await api.auth.login(form.email, form.pw);
    router.push('/');
  } catch(e) {
    alert(e.message);
  }
};
</script>