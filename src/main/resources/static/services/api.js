import axios from 'axios';

// 백엔드 서버 주소 (로컬 기준)
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 보낼 때마다 토큰이 있으면 자동으로 헤더에 끼워넣음
apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
    config.headers['Refresh-Token'] = localStorage.getItem('refreshToken');
  }
  return config;
});

export default {
  // ============================
  // 1. 인증 (Auth)
  // ============================
  login(data) {
    // 백엔드 필터 경로: /api/auth/login
    return apiClient.post('/auth/login', data);
  },
  register(data) {
    return apiClient.post('/auth/register', data);
  },
  logout() {
    return apiClient.post('/auth/logout');
  },
  reissue() {
    return apiClient.post('/auth/reissue');
  },

  // ============================
  // 2. 부동산 (Real Estate)
  // ============================
  // 매물 목록 조회 (검색)
  getRealEstates(params) {
    // params: { address, name, min_e_price, page, size ... }
    return apiClient.get('/real-estate', { params });
  },
  // 매물 상세 조회
  getRealEstateDetail(id, userId = 0) {
    return apiClient.get(`/real-estate/${id}`, { params: { user_id: userId } });
  },
  // 리뷰 조회
  getReviews(id) {
    return apiClient.get(`/real-estate/${id}/reviews`);
  },
  // 리뷰/별점 등록
  postRate(id, data) {
    return apiClient.post(`/real-estate/${id}/rate`, data);
  },
  // 찜하기 (좋아요)
  toggleFavorite(id, data) {
    return apiClient.post(`/real-estate/${id}/favorite`, data);
  },
  // 매물 비교
  compareRealEstates() {
    return apiClient.post('/real-estate/compare');
  },

  // ============================
  // 3. 게시판 & 커뮤니티 (Board & Post)
  // ============================
  // 게시판 목록 (카테고리)
  getBoards() {
    return apiClient.get('/boards');
  },
  // 게시글 전체 조회
  getPosts() {
    return apiClient.get('/post');
  },
  // 게시글 상세 조회
  getPostDetail(postId) {
    return apiClient.get(`/post/${postId}`);
  },
  // 게시글 작성
  createPost(data) {
    return apiClient.post('/post', data);
  },
  // 게시글 수정
  updatePost(data) {
    return apiClient.patch('/post', data);
  },
  // 게시글 삭제
  deletePost(postId) {
    return apiClient.delete(`/post/${postId}`);
  },

  // ============================
  // 4. 댓글 (Comment)
  // ============================
  createComment(data) {
    return apiClient.post('/comment', data);
  },
  updateComment(data) {
    return apiClient.patch('/comment', data);
  },
  deleteComment(commentId) {
    return apiClient.delete(`/comment/${commentId}`);
  },
  // 내가 쓴 댓글 조회
  getMyComments() {
    return apiClient.get('/comment/my-comments');
  },

  // ============================
  // 5. 유저 (User)
  // ============================
  getUserProfile() {
    return apiClient.get('/users/profile');
  },
  updateUserProfile(data) {
    return apiClient.put('/users/profile', data);
  },
  getSearchHistory() {
    return apiClient.get('/users/search-history');
  }
};