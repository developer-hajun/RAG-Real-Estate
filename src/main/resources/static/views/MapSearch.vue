<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import 'leaflet/dist/leaflet.css';
import { LMap, LTileLayer, LMarker, LPopup } from '@vue-leaflet/vue-leaflet';
import api from '../services/api';

const router = useRouter();
const zoom = ref(13);
const center = ref([37.5665, 126.9780]);
const properties = ref([]);
const loading = ref(false);

const searchParams = reactive({
  address: '',
  page: 0,
  size: 20
});

// ⭐ 1. 한국식 가격 포맷터 (만원 단위 가정)
// 예: 25000 -> 2억 5천 / 1000 -> 1000
const formatMoney = (money) => {
  if (!money) return '0';
  if (money >= 10000) {
    const uk = Math.floor(money / 10000);
    const rest = money % 10000;
    return rest > 0 ? `${uk}억 ${rest.toLocaleString()}` : `${uk}억`;
  }
  return money.toLocaleString(); // 1억 미만은 그냥 쉼표만
};

// ⭐ 2. 거래 종류 판별 (전세/월세)
const getDealType = (p) => {
  // month_price가 0이면 전세, 아니면 월세
  // 백엔드 필드명: month_price (혹은 카멜케이스 monthPrice 대응)
  const monthly = p.month_price ?? p.monthPrice ?? 0;
  return monthly === 0 ? '전세' : '월세';
};

// ⭐ 3. 최종 가격 문자열 생성
// 전세: "2억 5천"
// 월세: "2000 / 50"
const getPriceString = (p) => {
  const deposit = p.e_price ?? p.ePrice ?? 0;
  const monthly = p.month_price ?? p.monthPrice ?? 0;
  
  if (monthly === 0) {
    return formatMoney(deposit);
  } else {
    return `${formatMoney(deposit)} / ${formatMoney(monthly)}`;
  }
};

const validProperties = computed(() => {
  return properties.value.filter(p => {
    const lat = p.latitude || p.lat;
    const lng = p.longitude || p.lng;
    return lat && lng && !isNaN(lat) && !isNaN(lng);
  });
});

const fetchProperties = async () => {
  loading.value = true;
  try {
    const res = await api.getRealEstates(searchParams);
    if (res.data.statusCode === 200) {
      properties.value = res.data.data || [];
      
      // 검색 후 첫 번째 매물로 이동
      if (validProperties.value.length > 0) {
        const first = validProperties.value[0];
        center.value = [first.latitude || first.lat, first.longitude || first.lng];
      }
    }
  } catch (e) {
    console.error("매물 로드 실패", e);
  } finally {
    loading.value = false;
  }
};

const focusMap = (p) => {
  const lat = p.latitude || p.lat;
  const lng = p.longitude || p.lng;
  if(lat && lng) center.value = [lat, lng];
};

const goDetail = (id) => router.push(`/detail/${id}`);

onMounted(() => {
  fetchProperties();
});
</script>

<template>
  <div class="split-layout">
    <aside class="sidebar">
      <div class="search-area">
        <div class="input-wrap">
          <span>🔍</span>
          <input 
            v-model="searchParams.address" 
            placeholder="지역(동/구) 입력" 
            @keyup.enter="fetchProperties"
          >
          <button @click="fetchProperties" style="font-weight:bold; cursor:pointer;">검색</button>
        </div>
      </div>

      <div class="list-area">
        <div v-if="loading" class="loading-container">
          <div class="spinner"></div>
          <p class="loading-text">매물을 불러오고 있어요</p>
        </div>

        <div v-else-if="properties.length === 0" style="text-align:center; padding:40px; color:#888;">
          검색된 매물이 없습니다.
        </div>
        
        <div 
          v-else
          v-for="p in properties" :key="p.id" 
          class="property-card" 
          @click="focusMap(p)"
        >
          <div class="image-placeholder">🏠</div>
          <div class="info">
            <div class="tags">
              <span class="badge" :class="getDealType(p) === '전세' ? 'blue' : 'gray'">
                {{ getDealType(p) }}
              </span>
            </div>
            
            <h3 class="price">{{ getPriceString(p) }}</h3> 
            
            <p class="name">{{ p.name || p.aptName || '아파트명 없음' }}</p>
            <p class="spec">{{ p.address }}</p>
            <button class="btn-sm" style="margin-top:8px;" @click.stop="goDetail(p.id)">상세보기</button>
          </div>
        </div>
      </div>
    </aside>

    <div class="map-area">
      <l-map v-model:zoom="zoom" v-model:center="center" :use-global-leaflet="false">
        <l-tile-layer url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"></l-tile-layer>
        
        <l-marker 
          v-for="p in validProperties" 
          :key="p.id" 
          :lat-lng="[p.latitude || p.lat, p.longitude || p.lng]"
        >
          <l-popup>
            <div style="text-align:center;">
              <span class="badge" :class="getDealType(p) === '전세' ? 'blue' : 'gray'" style="font-size:10px; padding:2px 6px;">
                {{ getDealType(p) }}
              </span>
              <br>
              <b>{{ getPriceString(p) }}</b>
            </div>
          </l-popup>
        </l-marker>
      </l-map>
    </div>
  </div>
</template>

<style scoped>
/* 기존 스타일 유지 */
.split-layout { display: flex; height: 100vh; padding-top: var(--header-h); overflow: hidden; }
.sidebar { width: 400px; background: white; border-right: 1px solid var(--c-border); display: flex; flex-direction: column; z-index: 10; }
.search-area { padding: 20px; border-bottom: 1px solid var(--c-border); }
.input-wrap { background: #F2F4F6; padding: 12px; border-radius: 12px; display: flex; gap: 10px; align-items: center; }
.input-wrap input { background: transparent; border:none; width: 100%; }
.list-area { flex: 1; overflow-y: auto; padding: 20px; display: flex; flex-direction: column; gap: 16px; }

.property-card { background: white; border-radius: 16px; padding: 16px; display: flex; gap: 16px; box-shadow: var(--shadow-sm); border: 1px solid #eee; cursor: pointer; }
.property-card:hover { border-color: var(--c-brand); }
.image-placeholder { width: 80px; height: 80px; background: #eee; border-radius: 8px; display: flex; align-items: center; justify-content: center; }
.info { flex: 1; display:flex; flex-direction:column; justify-content:center; }

.price { font-size: 18px; font-weight: 800; color: var(--c-brand); margin-top: 4px; }
.name { font-weight: 700; margin-bottom: 2px; }
.spec { font-size: 13px; color: #888; }
.map-area { flex: 1; }
.btn-sm { background:#e8f3ff; color:#0064ff; padding:4px 8px; border-radius:4px; font-size:12px; border:none; cursor:pointer;}

/* 전세/월세 뱃지 스타일 */
.badge { display: inline-block; padding: 4px 8px; border-radius: 6px; font-size: 12px; font-weight: 700; }
.badge.blue { background: #E8F3FF; color: #3182F6; }
.badge.gray { background: #F2F4F6; color: #4E5968; }
</style>