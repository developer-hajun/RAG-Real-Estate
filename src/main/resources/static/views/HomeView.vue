<template>
  <div class="hero-section">
    <h1 class="hero-title">실거래 데이터 기반<br>부동산 AI 플랫폼</h1>
    
    <div class="search-wrapper">
      <div class="tabs">
        <div 
          class="tab-indicator" 
          :style="{ transform: `translateX(${currentTab * 100}%)`, width: `calc(100% / 3 - 8px)` }"
        ></div>
        <button class="tab-btn" :class="{active: currentTab===0}" @click="currentTab=0; ragResult=''">동별</button>
        <button class="tab-btn" :class="{active: currentTab===1}" @click="currentTab=1; ragResult=''">아파트별</button>
        <button class="tab-btn" :class="{active: currentTab===2}" @click="currentTab=2">AI 검색</button>
      </div>
      
      <div class="search-bar">
        <input 
          v-model="searchText" 
          class="input-box" 
          type="text" 
          :placeholder="placeholders[currentTab]" 
          @keyup.enter="handleSearch" 
          style="margin-bottom:0;"
        >
        <button class="btn" @click="handleSearch" style="width:100px;">검색</button>
      </div>

      <div v-if="currentTab === 2 && ragResult" style="margin-top:20px; text-align:left; background:#f9f9f9; padding:15px; border-radius:12px; white-space:pre-wrap;">
        <strong>🤖 AI 답변:</strong><br>{{ ragResult }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { api } from '@/api/mock';

const router = useRouter();
const currentTab = ref(0); // 0:동, 1:아파트, 2:AI(RAG)
const searchText = ref("");
const ragResult = ref("");

const placeholders = ["동 이름 (예: 역삼동)", "아파트 이름 (예: 자이)", "AI에게 무엇이든 물어보세요"];

const handleSearch = async () => {
  if (!searchText.value) return;

  if (currentTab.value === 2) {
    // RAG 검색 실행
    ragResult.value = "AI가 분석중입니다...";
    const res = await api.rag.find(searchText.value);
    ragResult.value = res.answer;
  } else {
    // 일반 검색 -> 매물 리스트로 이동
    router.push({ path: "/realty", query: { q: searchText.value } });
  }
};
</script>