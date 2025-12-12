<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const isLoginMode = ref(true); // true: 로그인, false: 회원가입
const isLoading = ref(false);

const form = reactive({
  email: '',
  password: '',
  name: '',
  age: null
});

// 로그인/회원가입 처리
const handleSubmit = async () => {
  if (!form.email || !form.password) {
    alert('이메일과 비밀번호를 입력해주세요.');
    return;
  }

  isLoading.value = true;
  try {
    if (isLoginMode.value) {
      const res = await api.login({ email: form.email, password: form.password });
      
      if (res.data.statusCode === 200) {
        const data = res.data.data;
        
        localStorage.setItem('accessToken', data.accessToken);
        localStorage.setItem('refreshToken', data.refreshToken);
        localStorage.setItem('userId', data.user.id);     // ⭐ 중요
        localStorage.setItem('userName', data.user.name); 
        
        router.push('/'); 
      }
    } else {
      await api.register(form);
      alert('가입이 완료되었습니다! 로그인해주세요.');
      isLoginMode.value = true; 
    }
  } catch (e) {
    console.error(e);
    alert('로그인/가입에 실패했습니다. 정보를 확인해주세요.');
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="title-area">
        <h1>{{ isLoginMode ? '다시 만나서 반가워요!' : '회원가입' }}</h1>
        <p>{{ isLoginMode ? 'Toss Estate로 내 집 마련의 꿈을.' : '간편하게 가입하고 매물을 확인하세요.' }}</p>
      </div>

      <div class="form-area">
        <div class="input-group">
          <label>이메일</label>
          <input 
            v-model="form.email" 
            type="email" 
            placeholder="example@toss.im"
            class="styled-input"
          >
        </div>

        <div class="input-group">
          <label>비밀번호</label>
          <input 
            v-model="form.password" 
            type="password" 
            placeholder="비밀번호를 입력해주세요"
            class="styled-input"
            @keyup.enter="handleSubmit"
          >
        </div>

        <div v-if="!isLoginMode" class="input-group slide-in">
          <label>이름</label>
          <input 
            v-model="form.name" 
            placeholder="실명을 입력해주세요"
            class="styled-input"
          >
          <label style="margin-top: 10px;">나이</label>
          <input 
            v-model="form.age" 
            type="number" 
            placeholder="나이"
            class="styled-input"
          >
        </div>
      </div>

      <button 
        class="submit-btn" 
        :disabled="isLoading" 
        @click="handleSubmit"
      >
        <span v-if="isLoading">로딩 중...</span>
        <span v-else>{{ isLoginMode ? '로그인' : '동의하고 가입하기' }}</span>
      </button>

      <div class="footer-link">
        <span @click="isLoginMode = !isLoginMode">
          {{ isLoginMode ? '계정이 없으신가요? 회원가입' : '이미 계정이 있으신가요? 로그인' }}
        </span>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 화면 중앙 정렬 */
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 60px);
  background-color: #F2F4F6; /* 배경색 확실하게 지정 */
  padding: 20px;
}

/* 카드 스타일 */
.auth-card {
  width: 100%;
  max-width: 460px;
  background: white;
  padding: 40px;
  border-radius: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08); /* 그림자 진하게 */
}

/* 타이틀 영역 */
.title-area {
  text-align: center;
  margin-bottom: 30px;
}
.title-area h1 {
  font-size: 26px;
  font-weight: 800;
  color: #191F28;
  margin-bottom: 8px;
}
.title-area p {
  font-size: 15px;
  color: #8B95A1;
}

/* 입력창 스타일 (강제 적용) */
.input-group {
  margin-bottom: 16px;
  text-align: left;
}
.input-group label {
  display: block;
  font-size: 14px;
  font-weight: 700;
  color: #4E5968;
  margin-bottom: 8px;
  margin-left: 4px;
}
.styled-input {
  width: 100%;
  padding: 14px 16px;
  font-size: 16px;
  border: 1px solid #E5E8EB !important; /* 테두리 강제 */
  border-radius: 12px;
  background-color: #F9FAFB;
  outline: none;
  transition: all 0.2s;
  color: #191F28;
}
.styled-input:focus {
  border-color: #3182F6 !important;
  background-color: #FFFFFF;
  box-shadow: 0 0 0 3px rgba(49, 130, 246, 0.1);
}

/* 버튼 스타일 */
.submit-btn {
  width: 100%;
  padding: 16px;
  margin-top: 20px;
  background-color: #3182F6;
  color: white;
  font-size: 16px;
  font-weight: 700;
  border-radius: 12px;
  border: none;
  cursor: pointer;
  transition: background 0.2s;
}
.submit-btn:hover {
  background-color: #1B64DA;
}
.submit-btn:disabled {
  background-color: #D1D6DB;
  cursor: not-allowed;
}

/* 하단 링크 */
.footer-link {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: #8B95A1;
}
.footer-link span {
  cursor: pointer;
  text-decoration: underline;
}

/* 애니메이션 */
.slide-in {
  animation: slideDown 0.3s ease-out;
}
@keyframes slideDown {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>