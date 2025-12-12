<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../services/api';

const route = useRoute();
const router = useRouter();
const postId = route.params.id;

const post = ref(null);
const comments = ref([]);
const newComment = ref('');
const loading = ref(true); // ⭐ 초기값 true

const loadData = async () => {
  loading.value = true;
  try {
    const res = await api.getPostDetail(postId);
    if (res.data.statusCode === 200) {
      post.value = res.data.data;
      if (res.data.data.commentDtos) {
        comments.value = res.data.data.commentDtos;
      }
    } else {
      alert('게시글을 찾을 수 없습니다.');
      router.back();
    }
  } catch (e) {
    console.error(e);
    // 에러 발생해도 로딩 상태는 꺼야 함 (또는 에러 화면 표시)
  } finally {
    loading.value = false; // ⭐ 로딩 종료
  }
};

onMounted(loadData);
// ... 댓글 등록 로직 (기존과 동일)
const submitComment = async () => {
  if (!newComment.value.trim()) return;
  try {
    await api.createComment({ postId: Number(postId), content: newComment.value, parentsId: null });
    newComment.value = '';
    loadData();
  } catch (e) { alert('댓글 등록 실패'); }
};
</script>

<template>
  <div class="main-container detail-view">
    <button @click="router.back()" style="margin-bottom:20px; font-weight:bold; cursor:pointer;">← 뒤로가기</button>

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p class="loading-text">내용을 불러오는 중입니다</p>
    </div>

    <div v-else-if="post" class="content-card">
      <h1 class="post-title">{{ post.title }}</h1>
      <div class="author-info"><span>작성자 ID: {{ post.userId }}</span></div>
      <div class="divider"></div>
      <div class="post-body">{{ post.text }}</div>
    </div>
    
    <div v-else class="empty-state">
      게시글이 존재하지 않습니다.
    </div>

    <div class="comments-section" v-if="!loading && post">
      <h3>댓글 {{ comments.length }}</h3>
      <div class="comment-list">
        <div v-for="cmt in comments" :key="cmt.id" class="comment-item">
          <p>{{ cmt.content }}</p>
          <span style="font-size:12px; color:#999;">{{ cmt.updatedDate }}</span>
        </div>
      </div>
    </div>

    <div class="input-area" v-if="!loading && post">
      <div class="input-wrapper">
        <input v-model="newComment" placeholder="댓글 작성" @keyup.enter="submitComment"/>
        <button class="send-btn" @click="submitComment">등록</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 기존 스타일 유지 + empty-state 추가 */
.empty-state { text-align: center; padding: 60px 0; color: #888; font-size: 16px; }
/* ... 나머지 스타일은 이전과 동일 ... */
.detail-view { padding-bottom: 90px; }
.content-card { background: white; border-radius: 24px; padding: 32px; box-shadow: 0 2px 12px rgba(0,0,0,0.04); }
.post-title { font-size: 24px; font-weight: 800; margin-bottom: 10px; }
.divider { height: 1px; background: #eee; margin: 20px 0; }
.post-body { font-size: 16px; line-height: 1.6; white-space: pre-wrap; }
.comment-item { background: white; padding: 16px; border-radius: 12px; margin-bottom: 10px; border:1px solid #eee; }
.input-area { position: fixed; bottom: 0; left: 0; right: 0; background: rgba(255,255,255,0.9); padding: 12px 20px; border-top: 1px solid #eee; }
.input-wrapper { max-width: 720px; margin: 0 auto; display: flex; gap: 10px; }
.input-wrapper input { flex: 1; background: #f2f4f6; padding: 12px; border-radius: 20px; border:none; }
.send-btn { background: #3182F6; color: white; padding: 0 20px; border-radius: 20px; font-weight: bold; }
</style>