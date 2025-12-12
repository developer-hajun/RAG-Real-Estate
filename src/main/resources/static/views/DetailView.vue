<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import 'leaflet/dist/leaflet.css';
import { LMap, LTileLayer, LMarker } from '@vue-leaflet/vue-leaflet';
import api from '../services/api';

const route = useRoute();
const router = useRouter();
const id = route.params.id; // 매물 ID

const info = ref({});
const reviews = ref([]);
const myReview = ref({ rating: 5, text: '' });
const isFavorite = ref(false);

// 데이터 로드
const loadData = async () => {
  try {
    // 1. 매물 상세 정보
    // const detailRes = await api.getRealEstateDetail(id);
    // info.value = detailRes.data.data;
    
    // [Mock Data] 상세 정보 (API 연동 전 테스트용)
    info.value = { 
      name: '해운대 아이파크', 
      price: '12억 5천', 
      address: '부산 해운대구 마린시티2로 38', 
      lat: 35.1631, lng: 129.1384, 
      desc: '해운대 바다가 한눈에 보이는 최고급 뷰. 커뮤니티 시설 완비.',
      spec: '34평 · 15층 · 남향'
    };

    // 2. 리뷰 리스트 조회
    const reviewRes = await api.getReviews(id);
    if (reviewRes.data.statusCode === 200) {
      reviews.value = reviewRes.data.data || [];
    }
  } catch (e) {
    console.error("데이터 로드 실패", e);
  }
};

onMounted(loadData);

// 리뷰 작성 (실제 API 호출)
const submitReview = async () => {
  const userId = localStorage.getItem('userId');
  
  if (!userId) {
    if(confirm('로그인이 필요한 서비스입니다. 로그인 하시겠습니까?')) {
      router.push('/auth');
    }
    return;
  }

  if (!myReview.value.text.trim()) {
    alert('리뷰 내용을 입력해주세요.');
    return;
  }

  try {
    // API 호출: POST /api/real-estate/{id}/rate
    await api.postRate(id, {
      userId: Number(userId),
      rating: myReview.value.rating,
      text: myReview.value.text
    });

    alert('리뷰가 등록되었습니다!');
    myReview.value.text = ''; // 입력창 초기화
    loadData(); // 목록 새로고침
  } catch (e) {
    console.error(e);
    alert('리뷰 등록에 실패했습니다.');
  }
};

const toggleFav = () => { isFavorite.value = !isFavorite.value; };
</script>

<template>
  <div class="main-container">
    <div class="nav">
      <button @click="router.back()">← 뒤로가기</button>
    </div>

    <div class="card content-card">
      <div class="header-row">
        <div>
          <span class="badge blue">매매</span>
          <h1 class="title">{{ info.name }}</h1>
          <p class="price">{{ info.price }}</p>
        </div>
        <button class="fav-btn" :class="{ active: isFavorite }" @click="toggleFav">
          {{ isFavorite ? '♥' : '♡' }}
        </button>
      </div>

      <div class="info-grid">
        <div class="text-info">
          <p class="address">📍 {{ info.address }}</p>
          <p class="spec">{{ info.spec }}</p>
          <div class="divider"></div>
          <p class="desc">{{ info.desc }}</p>
        </div>
        <div class="map-box">
          <l-map v-if="info.lat" :zoom="15" :center="[info.lat, info.lng]" :use-global-leaflet="false">
            <l-tile-layer url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"></l-tile-layer>
            <l-marker :lat-lng="[info.lat, info.lng]"></l-marker>
          </l-map>
        </div>
      </div>
    </div>

    <div class="review-section">
      <h3>거주민 리뷰 {{ reviews.length }}개</h3>

      <div class="review-input card">
        <div class="rating-select">
          <span>별점</span>
          <select v-model="myReview.rating">
            <option :value="5">⭐⭐⭐⭐⭐ 5점</option>
            <option :value="4">⭐⭐⭐⭐ 4점</option>
            <option :value="3">⭐⭐⭐ 3점</option>
            <option :value="2">⭐⭐ 2점</option>
            <option :value="1">⭐ 1점</option>
          </select>
        </div>
        <textarea v-model="myReview.text" placeholder="솔직한 거주 후기를 남겨주세요." rows="3"></textarea>
        <button class="submit-btn" @click="submitReview">등록하기</button>
      </div>

      <div class="review-list">
        <div v-for="(review, idx) in reviews" :key="idx" class="card review-item">
          <div class="review-head">
            <span class="user">익명 입주민</span>
            <span class="stars">{{ '⭐'.repeat(review.rating) }}</span>
          </div>
          <p class="review-text">{{ review.text }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.nav { margin-bottom: 20px; }
.nav button { font-weight: 600; color: #8B95A1; cursor: pointer; }

.content-card { margin-bottom: 30px; }
.header-row { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; }
.title { font-size: 26px; font-weight: 800; margin: 8px 0; }
.price { font-size: 22px; font-weight: 800; color: #3182F6; }
.fav-btn { font-size: 24px; color: #D1D6DB; transition: color 0.2s; }
.fav-btn.active { color: #FF3B30; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
.address { font-size: 15px; color: #4E5968; margin-bottom: 4px; }
.spec { font-size: 14px; color: #8B95A1; }
.divider { height: 1px; background: #F2F4F6; margin: 16px 0; }
.desc { line-height: 1.6; color: #333; }

.map-box { height: 240px; border-radius: 16px; overflow: hidden; border: 1px solid #E5E8EB; z-index: 1; }

.review-section h3 { font-size: 20px; font-weight: 700; margin-bottom: 16px; }
.review-input { display: flex; flex-direction: column; gap: 12px; margin-bottom: 20px; background: #F9FAFB; border: 1px solid #E5E8EB; }
.rating-select { display: flex; align-items: center; gap: 10px; font-weight: 600; font-size: 14px; }
.rating-select select { width: auto; padding: 6px 12px; border-radius: 8px; border: 1px solid #ddd; }
textarea { width: 100%; border: 1px solid #ddd; border-radius: 12px; padding: 12px; font-size: 15px; resize: none; outline: none; }
.submit-btn { align-self: flex-end; background: #3182F6; color: white; padding: 10px 20px; border-radius: 8px; font-weight: 700; width: auto; }

.review-item { padding: 16px; margin-bottom: 12px; }
.review-head { display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 14px; color: #4E5968; font-weight: 600; }
.review-text { font-size: 15px; line-height: 1.5; }
</style>