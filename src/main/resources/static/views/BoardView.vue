<template>
  <div>
    <div v-if="mode==='list'">
      <div class="flex-between" style="margin-bottom:15px;">
        <h2>📢 게시판</h2>
        <button class="btn btn-sm" @click="mode='write'">글쓰기</button>
      </div>
      <div class="flex-gap" style="margin-bottom:15px;">
        <input v-model="filter" class="input-box" placeholder="검색어" style="margin-bottom:0;">
        <button class="btn btn-sm btn-secondary" @click="loadPosts">검색</button>
        <button class="btn btn-sm btn-secondary" @click="loadPopular">인기글</button>
      </div>

      <div v-for="p in posts" :key="p.id" class="card hoverable" @click="router.push('/board/'+p.id)">
        <div class="flex-between">
          <h3>{{ p.title }}</h3>
          <button class="btn btn-sm btn-danger" @click.stop="deletePost(p.id)">삭제</button>
        </div>
        <p>조회 {{ p.viewCount }} · {{ p.createdDate }}</p>
      </div>
    </div>

    <div v-if="mode==='write'">
      <h2>새 글 작성</h2>
      <input v-model="form.title" class="input-box" placeholder="제목">
      <textarea v-model="form.content" class="input-box" rows="5" placeholder="내용"></textarea>
      <div class="flex-gap">
        <button class="btn" @click="savePost">등록</button>
        <button class="btn btn-secondary" @click="mode='list'">취소</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { api } from '@/api/mock';

const router = useRouter();
const posts = ref([]);
const mode = ref("list"); // list, write
const form = reactive({ title: "", content: "" });
const filter = ref("");

const loadPosts = async () => {
  posts.value = await api.board.getPosts("all", filter.value);
};
const loadPopular = async () => {
  posts.value = await api.board.getPosts("popular");
};

onMounted(loadPosts);

const savePost = async () => {
  try {
    await api.board.createPost(form);
    alert("등록되었습니다.");
    mode.value = "list";
    form.title = "";
    form.content = "";
    loadPosts();
  } catch (e) {
    alert(e.message);
  }
};

const deletePost = async (id) => {
  if (confirm("삭제하시겠습니까?")) {
    try {
      await api.board.deletePost(id);
      loadPosts();
    } catch (e) {
      alert(e.message);
    }
  }
};
</script>