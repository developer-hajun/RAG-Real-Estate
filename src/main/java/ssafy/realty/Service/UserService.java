package ssafy.realty.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.realty.Entity.User;
import ssafy.realty.Mapper.UserMapper;
import ssafy.realty.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserMapper userMapper;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder ;

    // login 기능
    public Map<String, Object> login(User user) {
        User loginUser = userMapper.findByEmail(user.getEmail());

        if(loginUser == null||!passwordEncoder.matches(user.getPassword(), loginUser.getPassword())){
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtUtil.generateToken(loginUser.getEmail(), loginUser.getId());
        Map<String, Object> response = new HashMap<>();

        response.put("token", token);
        response.put("user", loginUser);
        return response;
    }

    @Transactional
    public void register(User user) {
        if(userMapper.findByEmail(user.getEmail()) != null){
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
        user.setPassword(user.getPassword()); // 나중에 password는 암호화 후 저장해야 함
        // Mapper에게 저장하는 것을 위임
        userMapper.saveUser(user);
    }

}
