package com.farm.perform.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // 예상치 못한 필드 무시
public class KakaoUserInfo {

    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    public Long getId() {
        return id;
    }

    public KakaoAccount getKakaoAccount() {
        return kakaoAccount;
    }

    public String getNickname() {
        if (kakaoAccount != null && kakaoAccount.profile != null) {
            return kakaoAccount.profile.nickname;
        }
        return "카카오유저";
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {
        private Profile profile;
        private String email;

        public Profile getProfile() {
            return profile;
        }

        public String getEmail() {
            return email;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {
        private String nickname;

        public String getNickname() {
            return nickname;
        }
    }
}
