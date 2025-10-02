package com.farm.perform.domain.auth.service;

import com.farm.perform.domain.auth.dto.request.ReissueRequestDto;
import com.farm.perform.domain.auth.dto.request.SignInRequestDto;
import com.farm.perform.domain.auth.dto.request.SignUpRequestDto;
import com.farm.perform.domain.auth.dto.response.SignInResponseDto;
import com.farm.perform.domain.auth.entity.Role;
import com.farm.perform.domain.auth.entity.User;
import com.farm.perform.domain.auth.repository.UserRepository;
import com.farm.perform.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String REFRESH_KEY_PREFIX = "refresh:";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refresh-token-expire-time}")
    private long refreshTokenExpireTime;

    /** 회원가입 */
    @Transactional
    public void signUp(SignUpRequestDto requestDTO) {
        if (!requestDTO.password().equals(requestDTO.passwordConfirm())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        if (userRepository.findByUsernameAndDeletedFalse(requestDTO.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.password());
        User user = User.createUser(requestDTO.name(), requestDTO.username(), encodedPassword, Role.USER); // 기본 USER 권한
        userRepository.save(user);
    }


    @Transactional(readOnly = true)
    public SignInResponseDto signIn(SignInRequestDto requestDTO) {
        User user = userRepository.findByUsernameAndDeletedFalse(requestDTO.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(requestDTO.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        String accessToken = jwtProvider.generateAccessToken(user.getUserId(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUserId(), user.getRole().name());

        String redisKey = REFRESH_KEY_PREFIX + user.getUserId();
        redisTemplate.opsForValue().set(
                redisKey,
                refreshToken,
                refreshTokenExpireTime,
                TimeUnit.MILLISECONDS
        );

        return new SignInResponseDto(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public SignInResponseDto reissue(ReissueRequestDto requestDto) {
        String refreshToken = requestDto.refreshtoken();

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다.");
        }

        String tokenType = jwtProvider.getTypeFromToken(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰만 허용됩니다.");
        }

        Long userId = jwtProvider.getUserIdFromToken(refreshToken);

        String redisKey = "refresh:" + userId;
        String storedToken = redisTemplate.opsForValue().get(redisKey);

        if (storedToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다. 다시 로그인 해주세요.");
        }

        if (!storedToken.equals(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 일치하지 않습니다.");
        }

        String newAccessToken = jwtProvider.generateAccessToken(userId, jwtProvider.getRoleFromToken(refreshToken));

        return new SignInResponseDto(newAccessToken, refreshToken);
    }

    public void logout(Long userId) {
        String redisKey = REFRESH_KEY_PREFIX + userId;
        redisTemplate.delete(redisKey);
    }

    @Transactional
    public void softDeleteUser(Long userId) {
        User user = userRepository.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        user.delete(); // soft delete
    }
}
