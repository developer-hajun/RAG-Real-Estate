package ssafy.realty.Mapper;

import org.apache.ibatis.annotations.Mapper;
import ssafy.realty.Entity.SearchHistory;
import ssafy.realty.Entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    //회원가입
    void saveUser(User user);

    // 이메일 중복 체크 및 로그인용 조회
    User findByEmail(String email);

    // ID로 회원 조회 (프로필)
    User findById(int id);

    // 회원 정보 수정
    int update(User user);

    // 검색 기록 조회
    List<SearchHistory> findSearchHistoryByUserId(int userId);

    // 비밀 번호 변경
    void updatePassword(User user);

    // 검색 기록 저장
    void insertSearchHistory(SearchHistory searchHistory);

}
