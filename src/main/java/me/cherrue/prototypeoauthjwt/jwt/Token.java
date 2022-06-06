package me.cherrue.prototypeoauthjwt.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class Token {
    private final String token;

    public Token(String token) {
        this.token = token;
    }
}
