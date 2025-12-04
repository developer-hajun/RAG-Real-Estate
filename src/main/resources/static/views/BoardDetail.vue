<template>
  <div v-if="post">
    <div class="card">
      <div class="flex-between">
        <h2>{{ post.title }}</h2>
        <button
            v-if="isMyPost"
            class="btn btn-sm btn-danger"
            @click="deletePost"
        >
          삭제
        </button>
      </div>
      <p style="margin-bottom:20px; color:#888;">
        {{ post.createdDate }} · {{ post.user?.name }}
      </p>
      <hr style="border:0; border-top:1px solid #eee; margin-bottom:20px;">
      <p style="white-space:pre-wrap; min-height:100px;">{{ post.text }}</p>
    </div>

    <div class="card">
      <h3>💬 댓글 ({{ post.commentList?.length || 0 }})</h3>

      <div class="flex-gap" style="margin-bottom:20px;">
        <input
            v-model="commentInput"
            class="input-box"
            placeholder="댓글을 남겨주세요."
            style="margin-bottom:0;"
            @keyup.enter="addComment"
        >
        <button class="btn btn-sm" @click="addComment">등록</button>
      </div>

      <ul v-if="post.commentList">
        <li
            v-for="c in post.commentList"
            :key="c.id"
            style="padding:12px 0; border-bottom:1px solid #f2f4f6;"
        >
          <div class="flex-between">
            <strong>{{ c.user?.name }}</strong>
            <span
                v-if="currentUserId === c.user?.id"
                style="font-size:12px; color:var(--t-red); cursor:pointer;"
                @click="deleteComment(c.id)"
            >
              삭제
            </span>
          </div>
          <p style="margin-top:4px; color:#333;">{{ c.content }}</p>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { api, DB } from '@/api/mock';

const route = useRoute();
const router = useRouter();
const post = ref(null);
const commentInput = ref('');

const currentUserId = computed(() => DB.session?.id);
const isMyPost = computed(() => post.value?.user?.id === currentUserId.value);

const load = async () => {
  try {
    const res = await api.post.getDetail(route.params.id);
    post.value = res.data;
  } catch (e) {
    alert("존재하지 않는 글입니다.");
    router.push('/board');
  }
};

const deletePost = async () => {
  if(confirm("정말 삭제하시겠습니까?")) {
    await api.post.delete(post.value.id);
    router.push('/board');
  }
};

const addComment = async () => {
  if (!DB.session) return alert("로그인이 필요합니다.");
  if (!commentInput.value.trim()) return;

  // API 명세 파라미터 구조 반영
  await api.comment.create({
    comment: { content: commentInput.value },
    postId: post.value.id,
    UserId: DB.session.id
  });

  commentInput.value = '';
  await load(); // 목록 갱신
};

const deleteComment = async (cid) => {
  if(confirm("댓글을 삭제하시겠습니까?")) {
    await api.comment.delete(cid, post.value.id);
    await load();
  }
};

onMounted(load);
</script>