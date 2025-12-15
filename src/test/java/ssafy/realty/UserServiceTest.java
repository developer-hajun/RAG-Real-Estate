package ssafy.realty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ssafy.realty.DTO.Request.UserRequestDto;
import ssafy.realty.DTO.Response.SearchHistoryResponseDto;
import ssafy.realty.DTO.Response.UserResponseDto;
import ssafy.realty.Entity.SearchHistory;
import ssafy.realty.Entity.User;
import ssafy.realty.Mapper.UserMapper;
import ssafy.realty.Service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("register: 성공적으로 회원가입하고 DTO 반환 및 저장 확인")
    void register_Success() {
        UserRequestDto req = new UserRequestDto();
        req.setEmail("t@e.com");
        req.setPassword("pw");
        req.setName("TName");
        req.setAge(20);
        req.setBirthDate(LocalDateTime.of(2000,1,1,0,0));

        when(passwordEncoder.encode("pw")).thenReturn("encodedPw");

        UserResponseDto res = userService.register(req);

        assertNotNull(res);
        assertEquals("t@e.com", res.getEmail());
        assertEquals("TName", res.getName());
        verify(passwordEncoder).encode("pw");
        verify(userMapper).saveUser(any(User.class));
    }

    @Test
    @DisplayName("getProfile: 사용자가 있으면 DTO 반환")
    void getProfile_Found() {
        User user = new User();
        user.setId(9);
        user.setEmail("p@p.com");
        user.setName("P");
        user.setAge(40);
        user.setBirthDate(LocalDateTime.of(1985,5,5,0,0));

        when(userMapper.findById(9)).thenReturn(user);

        UserResponseDto res = userService.getProfile(9);

        assertNotNull(res);
        assertEquals(9, res.getId());
        assertEquals("p@p.com", res.getEmail());
    }

    @Test
    @DisplayName("getProfile: 사용자가 없으면 null 반환")
    void getProfile_NotFound() {
        when(userMapper.findById(10)).thenReturn(null);

        assertNull(userService.getProfile(10));
    }

    @Test
    @DisplayName("updateProfile: 사용자가 있으면 업데이트 후 DTO 반환")
    void updateProfile_Found() {
        User existing = new User();
        existing.setId(15);
        existing.setEmail("old@e.com");
        existing.setName("Old");

        when(userMapper.findById(15)).thenReturn(existing);

        UserRequestDto req = new UserRequestDto();
        // 이메일은 변경 불가이므로 기존 이메일과 동일하게 설정
        req.setEmail("old@e.com");
        req.setName("New");
        req.setAge(28);
        req.setBirthDate(LocalDateTime.of(1997,7,7,0,0));

        UserResponseDto res = userService.updateProfile(15, req);

        assertNotNull(res);
        assertEquals("old@e.com", res.getEmail());
        assertEquals("New", res.getName());
        verify(userMapper).update(existing);
    }

    @Test
    @DisplayName("updateProfile: 사용자가 없으면 null 반환")
    void updateProfile_NotFound() {
        when(userMapper.findById(20)).thenReturn(null);

        UserRequestDto req = new UserRequestDto();
        assertNull(userService.updateProfile(20, req));
    }

    @Test
    @DisplayName("getMySearchHistory: 검색기록 DTO 리스트 반환")
    void getMySearchHistory_ReturnsMapped() {
        SearchHistory h1 = new SearchHistory();
        h1.setId(1);
        h1.setText("area1");
        h1.setCreatedDate(LocalDateTime.of(2024,1,1,0,0));

        SearchHistory h2 = new SearchHistory();
        h2.setId(2);
        h2.setText("area2");
        h2.setCreatedDate(LocalDateTime.of(2024,2,2,0,0));

        when(userMapper.findSearchHistoryByUserId(3)).thenReturn(Arrays.asList(h1, h2));

        List<SearchHistoryResponseDto> res = userService.getMySearchHistory(3);

        assertEquals(2, res.size());
        assertEquals("area1", res.get(0).getText());
    }
}
