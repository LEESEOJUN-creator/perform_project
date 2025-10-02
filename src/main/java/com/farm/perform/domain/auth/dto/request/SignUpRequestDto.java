package com.farm.perform.domain.auth.dto.request;

public record SignUpRequestDto(
        String name,
        String username,
        String password,
        String passwordConfirm) {
}
