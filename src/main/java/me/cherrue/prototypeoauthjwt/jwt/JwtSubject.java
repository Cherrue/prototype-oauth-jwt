package me.cherrue.prototypeoauthjwt.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtSubject {
    private final String registrationId;
    private final String oAuthId;

    public JwtSubject(String jwtSubjectString) {
        String[] strings = jwtSubjectString.split("\t");
        if(strings.length != 2) {
            throw new IllegalArgumentException("잘못된 형식의 subject 입니다. subject : " + jwtSubjectString);
        }
        registrationId = strings[0];
        oAuthId = strings[1];
    }

    public String toString() {
        return registrationId + "\t" + oAuthId;
    }
}
