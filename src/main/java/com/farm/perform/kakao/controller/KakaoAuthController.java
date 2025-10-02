package com.farm.perform.kakao.controller;

import com.farm.perform.domain.auth.dto.response.SignInResponseDto;
import com.farm.perform.kakao.KakaoApiClient;
import com.farm.perform.kakao.dto.KakaoUserInfo;
import com.farm.perform.kakao.service.KakaoService;
import com.farm.perform.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class KakaoAuthController {

    private final KakaoApiClient kakaoApiClient;
    private final KakaoService kakaoService;

    @PostMapping("/kakao")
    public ResponseEntity<CommonResponse<SignInResponseDto>> kakaoLogin(@RequestParam String code) {

        String kakaoAccessToken = kakaoApiClient.getAccessToken(code);

        KakaoUserInfo kakaoUserInfo = kakaoApiClient.getUserInfo(kakaoAccessToken);

        SignInResponseDto response = kakaoService.kakaoLogin(kakaoUserInfo);

        return ResponseEntity.ok(CommonResponse.success("카카오 로그인 성공", response));
    }
}
