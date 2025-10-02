package com.farm.perform.global;

import com.farm.perform.domain.auth.entity.Role;
import com.farm.perform.domain.auth.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {

    private final User user;

    private UserPrincipal(User user) {
        this.user = user;
    }
    public static UserPrincipal create(User user) {
        return new UserPrincipal(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // User의 Role 값을 Spring Security 권한으로 변환
        Role role = user.getRole();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 만료 정책 없으면 true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠금 정책 없으면 true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 정책 없으면 true
    }

    @Override
    public boolean isEnabled() {
        return !user.isDeleted(); // soft delete 된 경우 비활성화
    }

    /** User 엔티티 주요 값 접근용 헬퍼 */
    public Long getUserId() {
        return user.getUserId();
    }

    public String getName() {
        return user.getName();
    }

    public Role getRole() {
        return user.getRole();
    }
}
