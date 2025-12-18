package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.realty.DTO.Request.UserRequestDto;
import ssafy.realty.DTO.Response.SearchHistoryResponseDto;
import ssafy.realty.DTO.Response.UserResponseDto;
import ssafy.realty.Entity.SearchHistory;
import ssafy.realty.Entity.User;
import ssafy.realty.Mapper.UserMapper;
import ssafy.realty.util.JwtUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    // 회원가입 예외는 왜 없지? 나중에 추가
    @Transactional
    public UserResponseDto register(UserRequestDto userRequestDto) {
        User isExist = userMapper.findByEmail(userRequestDto.getEmail());
        if (isExist != null) {
            return null;
        }
        User user = new User();
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setName(userRequestDto.getName());
        user.setAge(userRequestDto.getAge());
        user.setBirthDate(userRequestDto.getBirthDate());

        userMapper.saveUser(user);

        return toUserResponseDto(user);
    }

    //프로필 조회
    public UserResponseDto getProfile(int userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return null;
        }
        return toUserResponseDto(user);
    }

    // 프로필 수정
    @Transactional
    public UserResponseDto updateProfile(int userId, UserRequestDto userRequestDto) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return null;
        }
        if(!userRequestDto.getEmail().equals(user.getEmail())){
            // 이메일은 변경할 수 없습니다.
            return null; // 나중에 refactoring 필요
        }
        user.setName(userRequestDto.getName());
        user.setAge(userRequestDto.getAge());
        user.setEmail(userRequestDto.getEmail());
        user.setBirthDate(userRequestDto.getBirthDate());

        userMapper.update(user);

        return toUserResponseDto(user);
    }

    // 내 검색 기록 조회
    public List<SearchHistoryResponseDto> getMySearchHistory(int userId) {
        List<SearchHistory> histories = userMapper.findSearchHistoryByUserId(userId);
        return userMapper.findSearchHistoryByUserId(userId)
                .stream()
                .map(history -> new SearchHistoryResponseDto(history.getId(), history.getText(), history.getCreatedDate()))
                .collect(Collectors.toList());
    }

    // 비밀번호 변경
    @Transactional
    public void changePw(int userId, UserRequestDto userRequestDto) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        // 1. 현재 비밀번호 확인 (DTO의 기존 password 필드 활용)
        // 보안을 위해 필수적이지만, 필요 없다면 이 if문을 주석 처리하세요.
//        if (!passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
//            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
//        }

        // 2. 새 비밀번호와 확인용 비밀번호 일치 여부 확인
        if (userRequestDto.getNewPassword() == null || !userRequestDto.getNewPassword().equals(userRequestDto.getNewPasswordCheck())) {
            throw new IllegalArgumentException("변경할 비밀번호가 일치하지 않습니다.");
        }

        // 3. 비밀번호 암호화 및 업데이트
        user.setPassword(passwordEncoder.encode(userRequestDto.getNewPassword()));
        userMapper.updatePassword(user);
    }


    // 엔티티 -> DTO 변환 메서드
    private UserResponseDto toUserResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setName(user.getName());
        userResponseDto.setAge(user.getAge());
        userResponseDto.setBirthDate(user.getBirthDate());
        return userResponseDto;
    }

    @Transactional
    public void saveSearchHistory(int userId, String query) {
        SearchHistory history = SearchHistory.builder()
                .userId(userId)
                .text(query)
                .build();
        userMapper.insertSearchHistory(history);
    }


}
