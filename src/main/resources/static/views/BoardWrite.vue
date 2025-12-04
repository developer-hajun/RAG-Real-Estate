<template>
  <div>
    <h2 style="margin-bottom:20px;">새 글 작성</h2>
    <div class="card">
      <input v-model="form.title" class="input-box" placeholder="제목">
      <textarea
          v-model="form.text"
          class="input-box"
          rows="10"
          placeholder="내용을 입력하세요."
      ></textarea>

      <div class="flex-gap">
        <button class="btn" style="flex:1" @click="submit">등록하기</button>
        <button class="btn btn-secondary" style="flex:1" @click="$router.go(-1)">취소</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { api, DB } from '@/api/mock';

const router = useRouter();
const form = reactive({ title: '', text: '' });

const submit = async () => {
  if (!DB.session) return alert("로그인이 필요합니다.");
  if (!form.title || !form.text) return alert("제목과 내용을 입력해주세요.");

  try {
    // API 명세대로 text 필드 전송
    await api.post.create(form);
    router.push('/board');
  } catch (e) {
    alert(e.message);
  }
};
</script>