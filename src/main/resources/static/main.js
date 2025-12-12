import { createApp } from 'vue'
import App from './App.vue'
import router from './router' // router/index.js를 불러옵니다.
import './assets/style.css'   // 전역 스타일 불러오기

const app = createApp(App)

// ⚠️ 이 부분이 누락되어 에러가 발생했습니다.
// 라우터를 Vue 앱에 설치합니다.
app.use(router)

app.mount('#app')