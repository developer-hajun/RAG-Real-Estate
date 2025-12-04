<template>
  <div v-if="post.id">
    <div class="card">
      <h2>{{ post.title }}</h2>
      <p>작성자: {{ post.writerName }}</p>
      <hr style="margin:15px 0; border:0; border-top:1px solid #eee;">
      <p style="white-space:pre-wrap;">{{ post.content }}</p>
    </div>

    <div class="card">
      <h3>댓글 ({{ comments.length }})</h3>
      <div class="flex-gap" style="margin-bottom:15px;">
        <input v-model="commentText" class="input-box" placeholder="댓글 작성" style="margin:0;">
        <button class="btn btn-sm" @click="addComment">등록</button>
      </div>
      <div v-for="c in comments" :key="c.id" style="border-bottom:1px solid #eee; padding:10px 0;">
        <strong>{{ c.writerName }}</strong>: {{ c.content }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { api } from '@/api/mock';

const route = useRoute();
const post = ref({});
const comments = ref([]);
const commentText = ref("");

const load = async () => {
  const id = route.params.id;
  post.value = await api.board.getPost(id);
  comments.value = await api.board.getComments(id);
};
onMounted(load);

const addComment = async () => {
  try {
    await api.board.addComment(post.value.id, commentText.value);
    commentText.value = "";
    load();
  } catch (e) {
    alert(e.message);
  }
};
</script>