<template>
  <div>
    <div class="card">
      <div class="flex-between">
        <h2>👤 내 프로필</h2>
        <button class="btn btn-sm" @click="editMode = !editMode">{{ editMode ? '취소' : '수정' }}</button>
      </div>
      <div v-if="!editMode">
        <p><strong>이름:</strong> {{ profile.name }}</p>
        <p><strong>이메일:</strong> {{ profile.email }}</p>
        <p><strong>나이:</strong> {{ profile.age }}</p>
        <p><strong>생년월일:</strong> {{ profile.birthDate }}</p>
      </div>
      <div v-else style="margin-top:10px;">
        <input v-model="profile.name" class="input-box" placeholder="이름">
        <input v-model="profile.age" class="input-box" placeholder="나이">
        <input v-model="profile.birthDate" class="input-box" placeholder="생년월일 (YYYY-MM-DD)">
        <button class="btn" style="width:100%" @click="saveProfile">저장하기</button>
      </div>
    </div>

    <div class="card">
      <h3>🕐 최근 검색 기록 ({{ histories.length }})</h3>
      <ul>
        <li v-for="h in histories" :key="h.id" style="padding:5px 0; color:#555;">
          {{ h.text }} <small style="color:#aaa;">({{ h.createdDate }})</small>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { api } from '@/api/mock';

const profile = reactive({});
const histories = ref([]);
const editMode = ref(false);

onMounted(async () => {
  try {
    const data = await api.auth.getProfile();
    // profile 객체에 데이터 할당
    Object.assign(profile, data); 
    histories.value = await api.auth.getHistory();
  } catch (e) {
    alert("로그인이 필요합니다.");
  }
});

const saveProfile = async () => {
  try {
    await api.auth.updateProfile(profile);
    alert("수정되었습니다.");
    editMode.value = false;
  } catch (e) {
    alert(e.message);
  }
};
</script>