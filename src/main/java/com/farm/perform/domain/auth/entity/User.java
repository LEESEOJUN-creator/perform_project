package com.farm.perform.domain.auth.entity;

import com.farm.perform.domain.auth.entity.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    @Column(nullable = false, unique = true)
    private String username; // 아이디

    private String password;

    @Column(unique = true)
    private Long kakaoId;

    @Column(nullable = false)
    private boolean deleted = false; // 디폴트값 false

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role; // 권한 (USER, ORGANIZER, ADMIN)

    public void delete() {
        this.deleted = true; // 탈퇴시 true로 접근 제한(soft delete)
    }

    public static User createUser(String name, String username, String encodedPassword, Role role) {
        User user = new User();
        user.name = name;
        user.username = username;
        user.password = encodedPassword;
        user.role = role; // 기본 권한
        return user;
    }

    public static User createKakaoUser(Long kakaoId, String nickname, String username, Role role) {
        User user = new User();
        user.kakaoId = kakaoId;
        user.name = nickname;
        user.username = username; // 추가
        user.role = role;
        user.deleted = false;
        return user;
    }


    public void changeData(String name) {
        this.name = name;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void changeRole(Role role) {
        this.role = role;
    }
}
