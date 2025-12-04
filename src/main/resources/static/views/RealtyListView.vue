<template>
  <div>
    <div class="flex-between" style="margin-bottom:20px;">
      <h2>🏢 매물 목록</h2>
      <button v-if="compareList.length > 0" class="btn btn-sm" @click="doCompare">
        비교하기 ({{ compareList.length }})
      </button>
    </div>

    <div v-for="item in items" :key="item.id" class="card hoverable" @click="router.push('/realty/'+item.id)">
      <div class="flex-between">
        <h3>{{ item.name }}</h3>
        <input type="checkbox" @click.stop="toggleCompare(item.id)" :checked="compareList.includes(item.id)">
      </div>
      <p>📍 {{ item.address }}</p>
      <p style="color:var(--t-blue); font-weight:bold;">{{ item.price }}만원 / {{ item.type }}</p>
    </div>
    
    <div v-if="showCompareModal" style="position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); z-index:2000; display:flex; justify-content:center; align-items:center;">
      <div class="card" style="width:90%; max-width:600px;">
        <h3>⚖️ 매물 비교 결과</h3>
        <div style="display:flex; gap:10px; margin-top:20px;">
          <div v-for="c in compareResult" :key="c.id" style="flex:1; background:#f0f0f0; padding:10px; border-radius:8px;">
            <h4>{{ c.name }}</h4>
            <p><strong>가격:</strong> {{ c.price }}만원</p>
            <p><strong>유형:</strong> {{ c.type }}</p>
            <p><strong>보증금:</strong> {{ c.deposit }}</p>
          </div>
        </div>
        <button class="btn" style="width:100%; margin-top:20px;" @click="showCompareModal=false">닫기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { api } from '@/api/mock';

const route = useRoute();
const router = useRouter();
const items = ref([]);
const compareList = ref([]); // 비교함 담긴 ID들
const showCompareModal = ref(false);
const compareResult = ref([]);

onMounted(async () => {
  items.value = await api.realty.getList({ keyword: route.query.q || '' });
});

const toggleCompare = (id) => {
  if (compareList.value.includes(id)) compareList.value = compareList.value.filter((i) => i !== id);
  else {
    if (compareList.value.length >= 2) alert("비교는 최대 2개까지 가능합니다.");
    else compareList.value.push(id);
  }
};

const doCompare = async () => {
  if (compareList.value.length < 2) return alert("2개를 선택해주세요");
  
  try {
      const res = await api.realty.compare(compareList.value);
      compareResult.value = res.comparisons;
      showCompareModal.value = true;
  } catch (e) {
      alert("비교 중 오류 발생: " + e.message);
  }
};
</script>