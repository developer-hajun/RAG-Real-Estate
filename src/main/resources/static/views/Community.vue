<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const activeBoardId = ref(0);
const boards = ref([]);
const posts = ref([]);
const loading = ref(false); // ⭐ 로딩 변수

const fetchData = async () => {
  loading.value = true; // ⭐ 로딩 시작
  try {
    const boardRes = await api.getBoards();
    if (boardRes.data.statusCode === 200) boards.value = boardRes.data.data || [];

    const postRes = await api.getPosts();
    if (postRes.data.statusCode === 200) posts.value = postRes.data.data || [];
  } catch (e) {
    console.error("데이터 로드 실패", e);
  } finally {
    loading.value = false; // ⭐ 로딩 종료
  }
};

const filteredPosts = computed(() => {
  if (activeBoardId.value === 0) return posts.value;
  return posts.value.filter(p => p.boardId === activeBoardId.value);
});

onMounted(fetchData);

const goDetail = (id) => router.push(`/community/${id}`);
</script>

<template>
  <div class="main-container">
    <div class="header">
      <h1>부동산 커뮤니티</h1>
    </div>

    <div class="tabs">
      <button class="tab" :class="{ active: activeBoardId === 0 }" @click="activeBoardId = 0">전체</button>
      <button 
        v-for="b in boards" :key="b.id" 
        class="tab" :class="{ active: activeBoardId === b.id }" 
        @click="activeBoardId = b.id"
      >
        {{ b.title }}
      </button>
    </div>

    <div class="post-grid">
      <div v-if="loading" style="grid-column: 1/-1;">
        <div class="loading-container">
          <div class="spinner"></div>
          <p class="loading-text">게시글을 불러오고 있어요</p>
        </div>
      </div>

      <div v-else-if="filteredPosts.length === 0" style="text-align:center; grid-column: 1/-1; padding:40px; color:#888;">
        작성된 글이 없습니다.
      </div>

      <div 
        v-else
        v-for="post in filteredPosts" :key="post.id" 
        class="card hoverable post-card" 
        @click="goDetail(post.id)"
      >
        <h3>{{ post.title }}</h3>
        <p>{{ post.text }}</p>
        <div class="card-bottom">
          <span class="author">작성자 {{ post.userId }}</span>
          <div class="stats">
            <span>댓글 {{ post.commentDtos ? post.commentDtos.length : 0 }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 기존 스타일 유지 */
.header { text-align: center; margin-bottom: 30px; }
.tabs { display: flex; justify-content: center; gap: 8px; margin-bottom: 30px; flex-wrap:wrap; }
.tab { padding: 8px 16px; border-radius: 20px; background: white; border:1px solid #eee; cursor:pointer; }
.tab.active { background: #191F28; color: white; border-color:#191F28; }
.post-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 16px; min-height: 200px; }
.post-card { display: flex; flex-direction: column; height: 180px; justify-content: space-between; cursor: pointer; }
.post-card h3 { font-size: 18px; font-weight: 700; margin-bottom: 8px; }
.post-card p { font-size: 14px; color: #666; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; }
.card-bottom { margin-top: auto; display:flex; justify-content:space-between; font-size:13px; color:#999; border-top:1px solid #eee; padding-top:10px; }
</style>