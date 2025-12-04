import { createRouter, createWebHistory } from 'vue-router';

const routes = [
    { path: '/', component: () => import('@/views/RealtyList.vue') },
    { path: '/board', component: () => import('@/views/BoardList.vue') },
    { path: '/board/write', component: () => import('@/views/BoardWrite.vue') },
    { path: '/board/:id', component: () => import('@/views/BoardDetail.vue') },
    { path: '/login', component: () => import('@/views/Login.vue') }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;