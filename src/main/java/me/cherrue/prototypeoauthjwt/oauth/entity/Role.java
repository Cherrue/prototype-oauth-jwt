package me.cherrue.prototypeoauthjwt.oauth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_USER("ROLE_USER");
    private final String key;
}
