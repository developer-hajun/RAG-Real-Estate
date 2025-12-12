<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const keyword = ref('');

const goSearch = () => {
  if (keyword.value.trim()) {
    router.push({ path: '/map', query: { q: keyword.value } });
  }
};
</script>

<template>
  <div class="home-container">
    <section class="hero-section">
      <h1 class="main-title">
        어떤 집을 찾고 계신가요?<br>
        <span class="highlight">Toss Estate</span>에서 시작하세요.
      </h1>

      <div class="search-wrapper glass">
        <span class="search-icon">🔍</span>
        <input 
          v-model="keyword"
          class="search-input" 
          placeholder="지역, 지하철역, 아파트 검색"
          @keyup.enter="goSearch"
        >
        <button class="search-btn" @click="goSearch">검색</button>
      </div>
    </section>

    <section class="menu-section">
      <div class="card hoverable menu-card" @click="router.push('/map')">
        <div class="icon-box blue-bg">📍</div>
        <div class="text-box">
          <h3>지도 찾기</h3>
          <p>지도로 한눈에 매물 보기</p>
        </div>
      </div>

      <div class="card hoverable menu-card" @click="router.push('/community')">
        <div class="icon-box green-bg">💬</div>
        <div class="text-box">
          <h3>커뮤니티</h3>
          <p>실거주자의 생생 후기</p>
        </div>
      </div>

      <div class="card hoverable menu-card" @click="router.push('/my')">
        <div class="icon-box purple-bg">👤</div>
        <div class="text-box">
          <h3>마이페이지</h3>
          <p>내 자산과 관심 매물</p>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* 전체 컨테이너: 중앙 정렬을 위한 설정 */
.home-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 80px); /* 헤더 제외 높이 */
  padding: 0 20px;
  background: linear-gradient(180deg, #F2F4F6 0%, #FFFFFF 100%); /* 은은한 그라데이션 */
}

/* 히어로 섹션 (제목 + 검색창) */
.hero-section {
  text-align: center;
  margin-bottom: 60px;
  width: 100%;
  max-width: 720px;
  animation: slideUp 0.8s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.main-title {
  font-size: 42px;
  font-weight: 800;
  line-height: 1.3;
  margin-bottom: 40px;
  color: var(--c-text-1);
  letter-spacing: -0.5px;
}

.highlight {
  color: var(--c-brand);
}

/* 검색창 스타일 (둥근 Pill 모양) */
.search-wrapper {
  display: flex;
  align-items: center;
  background: white;
  padding: 8px 8px 8px 24px;
  border-radius: 100px; /* 완전 둥글게 */
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08); /* 부드러운 그림자 */
  transition: transform 0.3s, box-shadow 0.3s;
  border: 1px solid transparent;
}

.search-wrapper:focus-within {
  transform: translateY(-2px);
  box-shadow: 0 15px 40px rgba(0, 100, 255, 0.15);
  border-color: var(--c-brand-light);
}

.search-icon {
  font-size: 20px;
  margin-right: 12px;
  opacity: 0.5;
}

.search-input {
  flex: 1;
  font-size: 18px;
  border: none;
  outline: none;
  background: transparent;
  color: var(--c-text-1);
}
.search-input::placeholder {
  color: var(--c-text-4);
}

.search-btn {
  background: var(--c-brand);
  color: white;
  border: none;
  padding: 12px 28px;
  border-radius: 50px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
}
.search-btn:hover {
  background: var(--c-brand-dark);
}

/* 메뉴 카드 그리드 */
.menu-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); /* 반응형 그리드 */
  gap: 20px;
  width: 100%;
  max-width: 900px;
  animation: slideUp 0.8s 0.2s cubic-bezier(0.2, 0.8, 0.2, 1) backwards;
}

.menu-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  border-radius: 24px;
  background: white;
  box-shadow: 0 4px 20px rgba(0,0,0,0.03);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.menu-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0,0,0,0.08);
}

.icon-box {
  width: 56px;
  height: 56px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}
.blue-bg { background: #E8F3FF; }
.green-bg { background: #E6FCF5; }
.purple-bg { background: #F3F0FF; }

.text-box h3 {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 4px;
  color: var(--c-text-1);
}
.text-box p {
  font-size: 14px;
  color: var(--c-text-3);
}

/* 애니메이션 키프레임 */
@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 모바일 대응 */
@media (max-width: 600px) {
  .main-title { font-size: 28px; }
  .menu-section { grid-template-columns: 1fr; }
  .search-input { font-size: 15px; }
}
</style>