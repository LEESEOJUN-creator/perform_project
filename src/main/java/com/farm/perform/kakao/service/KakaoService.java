package com.farm.perform.kakao.service;

import com.farm.perform.domain.auth.dto.response.SignInResponseDto;
import com.farm.perform.domain.auth.entity.Role;
import com.farm.perform.domain.auth.entity.User;
import com.farm.perform.domain.auth.repository.UserRepository;
import com.farm.perform.global.jwt.JwtProvider;
import com.farm.perform.kakao.dto.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_KEY_PREFIX = "refresh:";

    @Value("${jwt.refresh-token-expire-time}")
    private long refreshTokenExpireTime;

    @Transactional
    public SignInResponseDto kakaoLogin(KakaoUserInfo kakaoUserInfo) {

        User user = userRepository.findByKakaoId(kakaoUserInfo.getId())
                .orElseGet(() -> userRepository.save(
                        User.createKakaoUser(
                                kakaoUserInfo.getId(),
                                kakaoUserInfo.getNickname(),  // name
                                kakaoUserInfo.getNickname(),  // username
                                Role.USER
                        )

                ));

        String accessToken = jwtProvider.generateAccessToken(user.getUserId(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUserId(), user.getRole().name());

        String redisKey = REFRESH_KEY_PREFIX + user.getUserId();
        redisTemplate.opsForValue().set(redisKey, refreshToken, refreshTokenExpireTime, TimeUnit.MILLISECONDS);

        return new SignInResponseDto(accessToken, refreshToken);
    }
}
