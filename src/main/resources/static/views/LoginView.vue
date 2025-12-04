<template>
  <div style="max-width:400px; margin:0 auto; padding-top:40px;">
    <h2 style="text-align:center; margin-bottom:20px;">{{ isRegister ? '회원가입' : '로그인' }}</h2>
    <input v-model="form.email" class="input-box" placeholder="이메일">
    <input v-model="form.password" type="password" class="input-box" placeholder="비밀번호">
    
    <div v-if="isRegister">
      <input v-model="form.name" class="input-box" placeholder="이름">
      <input v-model="form.age" class="input-box" placeholder="나이">
      <input v-model="form.birthDate" class="input-box" placeholder="생년월일 (YYYY-MM-DD)">
    </div>

    <button class="btn" style="width:100%; margin-top:10px;" @click="submit">
      {{ isRegister ? '가입하기' : '로그인' }}
    </button>
    <p style="text-align:center; margin-top:20px; color:#888; cursor:pointer;" @click="isRegister = !isRegister">
      {{ isRegister ? '이미 계정이 있으신가요?' : '회원가입 하기' }}
    </p>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { api } from '@/api/mock';

const router = useRouter();
const form = reactive({ email: "", password: "", name: "", age: "", birthDate: "" });
const isRegister = ref(false);

const submit = async () => {
  try {
    if (isRegister.value) {
      await api.auth.register(form);
      alert("가입 완료! 로그인해주세요.");
      isRegister.value = false;
      form.email = '';
      form.password = '';
    } else {
      await api.auth.login(form.email, form.password);
      alert("로그인 성공!");
      router.push("/");
    }
  } catch (e) {
    alert(e.message);
  }
};
</script>