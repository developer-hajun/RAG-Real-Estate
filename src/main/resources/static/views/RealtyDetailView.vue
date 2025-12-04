<template>
  <div v-if="item.id">
    <div class="card">
      <span class="badge badge-blue">{{ item.type }}</span>
      <h2 style="margin:10px 0;">{{ item.name }}</h2>
      <p style="font-size:16px;">{{ item.address }}</p>
      <h1 style="color:var(--t-blue); margin: 20px 0;">{{ item.price }}만원</h1>
      <div class="flex-gap">
        <button class="btn" style="flex:1" @click="addFav">❤️ 찜하기</button>
      </div>
    </div>

    <div class="card">
      <h3>⭐ 리뷰 및 평점</h3>
      <div style="margin-bottom:15px; border-bottom:1px solid #eee; padding-bottom:15px;">
        <select v-model="newReview.rating" style="padding:5px; margin-right:5px; border:1px solid var(--t-border); border-radius:4px;">
          <option value="5">5점</option><option value="4">4점</option><option value="3">3점</option>
        </select>
        <input v-model="newReview.text" placeholder="리뷰 내용을 입력하세요" style="width:70%; padding:5px; border:1px solid var(--t-border); border-radius:4px;">
        <button @click="submitReview" class="btn btn-sm" style="margin-left:5px;">등록</button>
      </div>
      <div v-for="r in reviews" :key="r.id" style="border-bottom:1px solid #eee; padding:10px 0;">
        <strong>{{ r.userName }}</strong> ({{ r.rating }}점): {{ r.text }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { api } from '@/api/mock';

const route = useRoute();
const item = ref({});
const reviews = ref([]);
const newReview = reactive({ rating: 5, text: "" });

const load = async () => {
  const id = route.params.id;
  item.value = await api.realty.getDetail(id);
  reviews.value = await api.realty.getReviews(id);
};
onMounted(load);

const addFav = async () => {
  try {
    const res = await api.realty.toggleFavorite(item.value.id);
    alert(res.message);
  } catch (e) {
    alert(e.message);
  }
};

const submitReview = async () => {
  try {
    await api.realty.addReview(item.value.id, newReview);
    alert("리뷰 등록 완료");
    newReview.text = "";
    load();
  } catch (e) {
    alert(e.message);
  }
};
</script>