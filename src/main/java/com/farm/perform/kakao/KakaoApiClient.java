package com.farm.perform.kakao;

import com.farm.perform.kakao.dto.KakaoUserInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoApiClient {

    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String USERINFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final KakaoProps kakaoProps;
    private final RestTemplate restTemplate;

    public KakaoApiClient(KakaoProps kakaoProps) {
        this.kakaoProps = kakaoProps;
        this.restTemplate = new RestTemplate();
    }

    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoProps.getClientId());
        params.add("redirect_uri", kakaoProps.getRedirectUri());
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
                    TOKEN_URL,
                    HttpMethod.POST,
                    entity,
                    KakaoTokenResponse.class
            );

            KakaoTokenResponse body = response.getBody();
            if (body == null || body.accessToken() == null) {
                throw new IllegalStateException("카카오에서 access_token을 받지 못했습니다.");
            }
            return body.accessToken();

        } catch (Exception e) {
            throw new RuntimeException("카카오 토큰 요청 실패", e);
        }
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                USERINFO_URL,
                HttpMethod.GET,
                entity,
                KakaoUserInfo.class
        ).getBody();
    }

    public record KakaoTokenResponse(
            @JsonProperty("access_token") String accessToken
    ) {}
}
