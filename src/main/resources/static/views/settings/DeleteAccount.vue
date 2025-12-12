<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
const router = useRouter();
const checked = ref(false);

const handleDelete = () => {
  // api.deleteUser()
  alert('탈퇴가 완료되었습니다. 그동안 이용해주셔서 감사합니다.');
  localStorage.clear();
  router.push('/');
};
</script>

<template>
  <div class="main-container">
    <div class="nav-header">
      <button @click="router.back()">← 뒤로</button>
      <h2>회원 탈퇴</h2>
    </div>

    <div class="card warning-card">
      <h3>⚠️ 정말 떠나시겠어요?</h3>
      <p>탈퇴하시면 찜한 매물, 작성한 게시글 등 모든 데이터가 영구적으로 삭제되며 복구할 수 없습니다.</p>
      
      <div class="check-area">
        <label>
          <input type="checkbox" v-model="checked">
          <span>위 내용을 모두 확인하였으며 이에 동의합니다.</span>
        </label>
      </div>

      <button class="btn-danger" :disabled="!checked" @click="handleDelete">
        탈퇴하기
      </button>
    </div>
  </div>
</template>

<style scoped>
.nav-header { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }
.warning-card { text-align: center; padding: 40px 24px; }
h3 { margin-bottom: 16px; font-size: 20px; }
p { color: #4E5968; font-size: 15px; line-height: 1.6; margin-bottom: 30px; }

.check-area { margin-bottom: 30px; background: #F9FAFB; padding: 16px; border-radius: 12px; text-align: left; }
.check-area label { display: flex; align-items: center; gap: 10px; cursor: pointer; font-size: 14px; font-weight: 600; }
.check-area input { width: 20px; height: 20px; }

.btn-danger { width: 100%; padding: 16px; background: #FF3B30; color: white; border-radius: 12px; font-weight: 700; font-size: 16px; transition: opacity 0.2s; }
.btn-danger:disabled { background: #FFD1D1; cursor: not-allowed; }
</style>