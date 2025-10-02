package com.farm.perform.domain.auth.controller;

import com.farm.perform.domain.auth.dto.request.ReissueRequestDto;
import com.farm.perform.domain.auth.dto.request.SignInRequestDto;
import com.farm.perform.domain.auth.dto.request.SignUpRequestDto;
import com.farm.perform.domain.auth.dto.response.SignInResponseDto;
import com.farm.perform.domain.auth.service.UserService;
import com.farm.perform.global.UserPrincipal;
import com.farm.perform.global.common.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<Void>> signUp(@Valid @RequestBody SignUpRequestDto requestDto) {
        userService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success("회원가입이 완료되었습니다.", null));
    }

    @PostMapping("/signin")
    public ResponseEntity<CommonResponse<SignInResponseDto>> signIn(@Valid @RequestBody SignInRequestDto requestDto) {
        SignInResponseDto response = userService.signIn(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success("로그인 성공", response));
    }

    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<SignInResponseDto>> reissue(@Valid @RequestBody ReissueRequestDto requestDto) {
        SignInResponseDto response = userService.reissue(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success("토큰 재발급 성공", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserPrincipal userDetails) {
        userService.logout(userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> softDeleteUser(@AuthenticationPrincipal UserPrincipal userDetails) {
        userService.softDeleteUser(userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}
