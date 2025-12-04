// /src/main.js
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import './assets/style.css'; // 전역 CSS import

const app = createApp(App);

app.use(router);

app.mount('#app');