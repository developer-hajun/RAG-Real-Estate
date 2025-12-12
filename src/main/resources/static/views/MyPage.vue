<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();

// 상태 변수
const profile = ref(null);
const activeTab = ref('favorites'); // favorites | history | comments
const favorites = ref([]);     // 🟢 임의 데이터 (Mock)
const searchHistory = ref([]); // 🔵 실제 API 데이터
const myComments = ref([]);    // 🔵 실제 API 데이터

onMounted(async () => {
  // 1. 로그인 체크
  const token = localStorage.getItem('accessToken');
  if (!token) {
    alert('로그인이 필요한 서비스입니다.');
    router.replace('/auth');
    return;
  }

  // 2. 🟢 찜한 목록 (API가 없으므로 임의 데이터 사용)
  favorites.value = [
    { id: 101, name: '해운대 아이파크', price: '12억 5천', type: '매매', spec: '34평 · 15층', location: '부산 해운대구' },
    { id: 102, name: '서면 더샵 센트럴', price: '6억', type: '전세', spec: '24평 · 5층', location: '부산 부산진구' },
    { id: 103, name: '광안리 자이', price: '8억 2천', type: '매매', spec: '30평 · 20층', location: '부산 수영구' },
  ];

  // 3. 🔵 나머지 데이터 (실제 API 호출)
  try {
    // 프로필 조회
    const profileRes = await api.getUserProfile();
    if(profileRes.data.statusCode === 200) {
      profile.value = profileRes.data.data;
    }

    // 검색 기록 조회
    const historyRes = await api.getSearchHistory();
    if(historyRes.data.statusCode === 200) {
      searchHistory.value = historyRes.data.data || [];
    }

    // 내 댓글 조회
    const commentRes = await api.getMyComments();
    if(commentRes.data.statusCode === 200) {
      myComments.value = commentRes.data.data || [];
    }
  } catch (e) {
    console.error("데이터 로드 실패:", e);
    // API 에러 시에도 프로필 기본값은 보여주기 위해
    if(!profile.value) {
      profile.value = { 
        name: localStorage.getItem('userName') || '사용자', 
        email: 'user@ssafy.com' 
      };
    }
  }
});

// 페이지 이동
const goProperty = () => router.push('/map'); 
const goLogout = () => {
  if (confirm('로그아웃 하시겠습니까?')) {
    localStorage.clear();
    router.push('/');
  }
};
</script>

<template>
  <div class="main-container">
    <div class="profile-card" v-if="profile">
      <div class="profile-content">
        <div class="avatar-lg">{{ profile.name ? profile.name[0] : 'U' }}</div>
        <div class="text-info">
          <h2>{{ profile.name }}님</h2>
          <p>{{ profile.email }}</p>
        </div>
      </div>
      <button class="logout-btn" @click="goLogout">로그아웃</button>
    </div>

    <div class="tab-menu">
      <button 
        class="tab-item" 
        :class="{ active: activeTab === 'favorites' }"
        @click="activeTab = 'favorites'"
      >
        찜한 매물
      </button>
      <button 
        class="tab-item" 
        :class="{ active: activeTab === 'history' }"
        @click="activeTab = 'history'"
      >
        검색 기록
      </button>
      <button 
        class="tab-item" 
        :class="{ active: activeTab === 'comments' }"
        @click="activeTab = 'comments'"
      >
        내 댓글
      </button>
    </div>

    <div class="content-area">
      
      <div v-if="activeTab === 'favorites'" class="list-group">
        <div v-if="favorites.length === 0" class="empty-state">
          찜한 매물이 없습니다.
        </div>
        <div 
          v-else
          v-for="fav in favorites" 
          :key="fav.id" 
          class="card hoverable property-item"
          @click="goProperty"
        >
          <div class="img-placeholder">🏠</div>
          <div class="prop-info">
            <div class="flex-row">
              <span class="badge" :class="fav.type === '매매' ? 'blue' : ''">{{ fav.type }}</span>
              <span class="prop-price">{{ fav.price }}</span>
            </div>
            <h3 class="prop-name">{{ fav.name }}</h3>
            <p class="prop-loc">{{ fav.location }} · {{ fav.spec }}</p>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'history'" class="list-group">
        <div v-if="searchHistory.length === 0" class="empty-state">
          최근 검색 기록이 없습니다.
        </div>
        <div v-for="(item, idx) in searchHistory" :key="idx" class="card history-item">
          <span class="history-icon">🔍</span>
          <span class="history-text">{{ item }}</span> </div>
      </div>

      <div v-if="activeTab === 'comments'" class="list-group">
        <div v-if="myComments.length === 0" class="empty-state">
          작성한 댓글이 없습니다.
        </div>
        <div v-for="cmt in myComments" :key="cmt.id" class="card comment-item">
          <p class="cmt-content">"{{ cmt.content }}"</p>
          <div class="cmt-meta">
            <span>게시글 ID: {{ cmt.postId }}</span>
            <span class="date">{{ cmt.updatedDate || cmt.createdDate }}</span>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
/* 프로필 카드 */
.profile-card {
  display: flex; justify-content: space-between; align-items: center;
  background: white; padding: 24px; border-radius: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05); margin-bottom: 24px;
}
.profile-content { display: flex; align-items: center; gap: 16px; }
.avatar-lg {
  width: 60px; height: 60px; background: #3182F6; color: white;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-size: 24px; font-weight: 800;
}
.text-info h2 { font-size: 20px; font-weight: 800; color: #191F28; }
.text-info p { color: #8B95A1; font-size: 14px; margin-top: 4px; }
.logout-btn {
  background: #F2F4F6; color: #4E5968; padding: 8px 16px; border-radius: 8px; font-weight: 600; font-size: 13px;
  cursor: pointer; transition: background 0.2s;
}
.logout-btn:hover { background: #FFEAEA; color: #FF3B30; }

/* 탭 메뉴 */
.tab-menu {
  display: flex; gap: 8px; margin-bottom: 20px;
}
.tab-item {
  padding: 10px 18px; border-radius: 20px; background: white;
  color: #4E5968; font-weight: 600; font-size: 15px; cursor: pointer;
  border: 1px solid #E5E8EB; transition: all 0.2s;
}
.tab-item.active {
  background: #191F28; color: white; border-color: #191F28;
}

/* 리스트 공통 */
.list-group { display: flex; flex-direction: column; gap: 12px; }
.empty-state { text-align: center; padding: 40px; color: #8B95A1; font-size: 15px; }

/* 찜한 매물 스타일 */
.property-item { display: flex; align-items: center; gap: 16px; padding: 20px; cursor: pointer; }
.img-placeholder {
  width: 70px; height: 70px; background: #F2F4F6; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; font-size: 24px;
  flex-shrink: 0;
}
.prop-info { flex: 1; }
.flex-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px; }
.prop-price { font-size: 18px; font-weight: 800; color: #3182F6; }
.prop-name { font-size: 16px; font-weight: 700; margin-bottom: 4px; }
.prop-loc { font-size: 13px; color: #8B95A1; }
.badge.blue { background: #E8F3FF; color: #3182F6; }

/* 검색 기록 스타일 */
.history-item { display: flex; align-items: center; gap: 10px; padding: 16px; }
.history-icon { font-size: 18px; opacity: 0.6; }
.history-text { font-size: 15px; font-weight: 500; }

/* 댓글 스타일 */
.comment-item { padding: 16px; display: flex; flex-direction: column; gap: 8px; }
.cmt-content { font-size: 15px; color: #191F28; line-height: 1.4; }
.cmt-meta { font-size: 12px; color: #8B95A1; display: flex; justify-content: space-between; }
</style>