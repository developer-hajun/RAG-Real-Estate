<template>
  <div>
    <div class="flex-between" style="margin-bottom:20px;">
      <h2>📢 커뮤니티</h2>
      <button class="btn btn-sm" @click="$router.push('/board/write')">글쓰기</button>
    </div>

    <div v-if="posts.length === 0" class="card">
      <p style="text-align:center;">등록된 게시글이 없습니다.</p>
    </div>

    <div
        v-for="p in posts"
        :key="p.id"
        class="card hover"
        @click="$router.push(`/board/${p.id}`)"
    >
      <h3>{{ p.title }}</h3>
      <p style="overflow:hidden; text-overflow:ellipsis; white-space:nowrap; margin-bottom:10px;">
        {{ p.text }}
      </p>
      <div style="font-size:13px; color:#888;">
        <span>{{ p.user?.name }}</span> ·
        <span>{{ p.createdDate }}</span> ·
        <span>댓글 {{ p.commentList?.length || 0 }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { api } from '@/api/mock';

const posts = ref([]);

onMounted(async () => {
  const res = await api.post.getAll();
  posts.value = res.data;
});
</script>