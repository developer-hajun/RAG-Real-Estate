<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
const router = useRouter();

const settings = ref({
  push: true,
  marketing: false,
  email: true
});

const save = () => {
  // api.updateSettings(settings.value);
  alert('알림 설정이 저장되었습니다.');
};
</script>

<template>
  <div class="main-container">
    <div class="nav-header">
      <button @click="router.back()">← 뒤로</button>
      <h2>알림 설정</h2>
    </div>

    <div class="card setting-list">
      <div class="setting-item">
        <div class="text">
          <h3>앱 푸시 알림</h3>
          <p>중요한 매물 정보와 댓글 알림을 받습니다.</p>
        </div>
        <label class="switch">
          <input type="checkbox" v-model="settings.push" @change="save">
          <span class="slider round"></span>
        </label>
      </div>

      <div class="setting-item">
        <div class="text">
          <h3>마케팅 정보 수신</h3>
          <p>이벤트 및 혜택 정보를 받습니다.</p>
        </div>
        <label class="switch">
          <input type="checkbox" v-model="settings.marketing" @change="save">
          <span class="slider round"></span>
        </label>
      </div>

      <div class="setting-item">
        <div class="text">
          <h3>이메일 뉴스레터</h3>
          <p>주간 부동산 동향 리포트를 받습니다.</p>
        </div>
        <label class="switch">
          <input type="checkbox" v-model="settings.email" @change="save">
          <span class="slider round"></span>
        </label>
      </div>
    </div>
  </div>
</template>

<style scoped>
.nav-header { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }
.nav-header button { font-size: 16px; color: #4E5968; cursor: pointer; }
.nav-header h2 { font-size: 20px; font-weight: 700; }

.setting-item { display: flex; justify-content: space-between; align-items: center; padding: 20px 0; border-bottom: 1px solid #F2F4F6; }
.setting-item:last-child { border-bottom: none; }
.text h3 { font-size: 16px; font-weight: 600; margin-bottom: 4px; }
.text p { font-size: 13px; color: #8B95A1; }

/* 토글 스위치 CSS */
.switch { position: relative; display: inline-block; width: 50px; height: 28px; }
.switch input { opacity: 0; width: 0; height: 0; }
.slider { position: absolute; cursor: pointer; top: 0; left: 0; right: 0; bottom: 0; background-color: #E5E8EB; transition: .4s; border-radius: 34px; }
.slider:before { position: absolute; content: ""; height: 24px; width: 24px; left: 2px; bottom: 2px; background-color: white; transition: .4s; border-radius: 50%; box-shadow: 0 2px 4px rgba(0,0,0,0.2); }
input:checked + .slider { background-color: #3182F6; }
input:checked + .slider:before { transform: translateX(22px); }
</style>