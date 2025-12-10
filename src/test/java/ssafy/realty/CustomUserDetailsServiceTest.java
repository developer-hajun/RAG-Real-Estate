package ssafy.realty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ssafy.realty.DTO.CustomUserDetails;
import ssafy.realty.Entity.User;
import ssafy.realty.Mapper.UserMapper;
import ssafy.realty.Service.CustomUserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    @DisplayName("loadUserByUsername: 사용자가 없으면 예외 발생")
    void loadUserByUsername_NotFound() {
        when(userMapper.findByEmail("no@user.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("no@user.com"));

        verify(userMapper).findByEmail("no@user.com");
    }

    @Test
    @DisplayName("loadUserByUsername: 사용자가 있으면 CustomUserDetails 반환")
    void loadUserByUsername_Found() {
        User user = new User();
        user.setId(7);
        user.setEmail("a@b.com");
        user.setPassword("enc");

        when(userMapper.findByEmail("a@b.com")).thenReturn(user);

        UserDetails ud = service.loadUserByUsername("a@b.com");

        assertNotNull(ud);
        assertTrue(ud instanceof CustomUserDetails);
        CustomUserDetails cud = (CustomUserDetails) ud;
        assertEquals(7, cud.getUser().getId());
        assertEquals("a@b.com", cud.getUsername());

        verify(userMapper).findByEmail("a@b.com");
    }
}
