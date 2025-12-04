<template>
  <div>
    <h2 style="margin-bottom:20px;">🏢 추천 매물</h2>
    <div v-for="item in items" :key="item.id" class="card hover">
      <div style="margin-bottom:8px;">
        <span class="badge">{{ item.type }}</span>
        <span style="font-weight:bold; font-size:18px;">{{ item.name }}</span>
      </div>
      <p>{{ item.address }}</p>
      <h3 style="color:var(--t-blue); margin-top:10px;">
        {{ item.e_price }} <span v-if="item.month_price > 0">/ {{ item.month_price }}</span> 만원
      </h3>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { api } from '@/api/mock'; // 더미 API 호출

const items = ref([]);

onMounted(async () => {
  const res = await api.realty.getAll();
  items.value = res.data;
});
</script>