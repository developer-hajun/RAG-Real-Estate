// /src/api/mock.js
import { reactive } from 'vue';

/* =================================================================
     1. MOCK DATABASE (데이터베이스 시뮬레이션)
     ================================================================= */
export const DB = reactive({
  users: [{ id: 1, email: "test@test.com", password: "123", name: "김토스", age: 30, birthDate: "1994-01-01" }],
  session: null, // 현재 로그인 유저
  realties: [
    {
      id: 1,
      name: "강남역 5분 풀옵션",
      address: "서울시 강남구 역삼동",
      price: 60,
      type: "월세",
      location: "강남",
      deposit: 1000,
    },
    {
      id: 2,
      name: "한강뷰 테라스 아파트",
      address: "서울시 서초구 반포동",
      price: 200000,
      type: "전세",
      location: "서초",
      deposit: 200000,
    },
    {
      id: 3,
      name: "대학가 신축 원룸",
      address: "서울시 관악구 신림동",
      price: 45,
      type: "월세",
      location: "관악",
      deposit: 500,
    },
    {
      id: 4,
      name: "판교 테크노밸리 오피스텔",
      address: "성남시 분당구 삼평동",
      price: 120,
      type: "월세",
      location: "판교",
      deposit: 3000,
    },
  ],
  reviews: [],
  favorites: [],
  posts: [
    {
      id: 1,
      title: "이 지역 치안 괜찮나요?",
      content: "밤에 다니기 어떤가요?",
      userId: 1,
      viewCount: 10,
      createdDate: "2024-11-28",
    },
    {
      id: 2,
      title: "전세 대출 금리 질문",
      content: "중기청 되나요?",
      userId: 1,
      viewCount: 5,
      createdDate: "2024-11-27",
    },
  ],
  comments: [],
  searchHistories: [],
  ragHistories: [],
});

/* =================================================================
     2. MOCK API CLIENT (API 명세서 구현)
     ================================================================= */
export const api = {
  // [Auth] 사용자 관련
  auth: {
    // POST /api/users/login
    login: async (email, password) => {
      const user = DB.users.find((u) => u.email === email && u.password === password);
      if (user) {
        DB.session = user;
        return { message: "로그인 성공" };
      }
      throw new Error("로그인 실패: 이메일 또는 비밀번호 확인");
    },
    // POST /api/users/logout
    logout: async () => {
      DB.session = null;
      return { message: "로그아웃 성공" };
    },
    // POST /api/users/register
    register: async (data) => {
      DB.users.push({ id: Date.now(), ...data });
      return { message: "회원가입 성공" };
    },
    // GET /api/users/profile
    getProfile: async () => {
      if (!DB.session) throw new Error("비로그인");
      return { ...DB.session };
    },
    // PUT /api/users/profile
    updateProfile: async (data) => {
      if (!DB.session) throw new Error("비로그인");
      const idx = DB.users.findIndex((u) => u.id === DB.session.id);
      DB.users[idx] = { ...DB.users[idx], ...data };
      DB.session = DB.users[idx];
      return { message: "수정 성공" };
    },
    // GET /api/users/searchHistory
    getHistory: async () => {
      if (!DB.session) return [];
      return DB.searchHistories.filter((h) => h.userId === DB.session.id);
    },
  },

  // [Realty] 부동산 관련
  realty: {
    // GET /api/real-estate (Filter)
    getList: async (params) => {
      let res = DB.realties;
      if (params.keyword)
        res = res.filter((r) => r.name.includes(params.keyword) || r.location.includes(params.keyword));
      return res;
    },
    // GET /api/real-estate/{id}
    getDetail: async (id) => {
      return DB.realties.find((r) => r.id == id);
    },
    // GET /api/real-estate/{id}/reviews
    getReviews: async (id) => {
      return DB.reviews
        .filter((r) => r.realtyId == id)
        .map((r) => {
          const u = DB.users.find((u) => u.id === r.userId);
          return { ...r, userName: u ? u.name : "익명" };
        });
    },
    // POST /api/real-estate/{id}/rate
    addReview: async (id, data) => {
      // rating, text
      if (!DB.session) throw new Error("로그인 필요");
      DB.reviews.push({ id: Date.now(), realtyId: id, userId: DB.session.id, ...data });
      return { message: "평점 등록 성공" };
    },
    // POST /api/real-estate/{id}/favorite
    toggleFavorite: async (id) => {
      if (!DB.session) throw new Error("로그인 필요");
      const idx = DB.favorites.findIndex((f) => f.realtyId == id && f.userId == DB.session.id);
      if (idx >= 0) {
        DB.favorites.splice(idx, 1);
        return { message: "찜 취소" };
      } else {
        DB.favorites.push({ id: Date.now(), realtyId: id, userId: DB.session.id });
        return { message: "찜 성공" };
      }
    },
    // POST /api/real-estate/compare
    compare: async (realtyIds) => {
      const targets = DB.realties.filter((r) => realtyIds.includes(r.id));
      return { comparisons: targets };
    },
  },

  // [Board] 게시판 관련
  board: {
    // GET /api/board/posts, GET /api/board/posts/filter, GET /api/board/posts/popular
    getPosts: async (filterType, keyword) => {
      let res = [...DB.posts];
      if (filterType === "popular") res.sort((a, b) => b.viewCount - a.viewCount);
      if (keyword) res = res.filter((p) => p.title.includes(keyword));
      return res;
    },
    // GET /api/board/posts/{id}
    getPost: async (id) => {
      const p = DB.posts.find((p) => p.id == id);
      const u = DB.users.find((u) => u.id === p.userId);
      return { ...p, writerName: u ? u.name : "알수없음" };
    },
    // POST /api/board/posts
    createPost: async (data) => {
      if (!DB.session) throw new Error("로그인 필요");
      DB.posts.unshift({
        id: Date.now(),
        userId: DB.session.id,
        viewCount: 0,
        createdDate: new Date().toLocaleDateString(),
        ...data,
      });
      return { message: "게시물 생성 성공" };
    },
    // DELETE /api/board/posts/{id}
    deletePost: async (id) => {
      if (!DB.session) throw new Error("로그인 필요");

      const idx = DB.posts.findIndex((p) => p.id == id);
      if (idx > -1) {
        // 작성자 ID와 현재 세션 ID 비교 (권한 검사)
        if (DB.posts[idx].userId !== DB.session.id) {
          throw new Error("삭제 권한이 없습니다.");
        }
        DB.posts.splice(idx, 1);
      }
      return { message: "게시물 삭제 성공" };
    },
    // POST /api/board/posts/{id}/comments
    addComment: async (id, content) => {
      if (!DB.session) throw new Error("로그인 필요");
      DB.comments.push({ id: Date.now(), postId: id, content, userId: DB.session.id });
      return { message: "댓글 작성 성공" };
    },
    // Helper for comments
    getComments: async (postId) => {
      return DB.comments
        .filter((c) => c.postId == postId)
        .map((c) => {
          const u = DB.users.find((user) => user.id === c.userId);
          return { ...c, writerName: u ? u.name : "익명" };
        });
    },
  },

  // [RAG] AI 검색
  rag: {
    // GET /api/rag/find
    find: async (findString) => {
      // 시뮬레이션: 검색어 저장
      if (DB.session) {
        DB.searchHistories.push({
          id: Date.now(),
          userId: DB.session.id,
          text: findString,
          createdDate: new Date().toLocaleString(),
        });
      }

      // 더미 응답 생성
      const answer = `"${findString}"에 대한 AI 분석 결과입니다.\n해당 지역은 최근 실거래가가 상승 추세이며, 교통 호재가 예상됩니다. 추천 매물은 3건 있습니다.`;

      // 기록 저장 (findList용)
      DB.ragHistories.push({ query: findString, answer, date: new Date().toLocaleTimeString() });

      return { message: "분석 완료", answer };
    },
    // GET /api/rag/findList
    findList: async () => {
      return { list: DB.ragHistories };
    },
  },
};