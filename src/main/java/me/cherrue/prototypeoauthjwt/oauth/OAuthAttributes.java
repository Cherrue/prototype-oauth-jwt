package me.cherrue.prototypeoauthjwt.oauth;

import me.cherrue.prototypeoauthjwt.oauth.dto.UserDto;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {
    GITHUB("github", (attributes) -> UserDto.builder()
            .registrationId((String) attributes.get("registrationId"))
            .oAuthId(String.valueOf(attributes.get("id"))) // Integer
            .name((String) attributes.get("name"))
            .email((String) attributes.get("email"))
            .image((String) attributes.get("avatar_url"))
            .build());

    private final String registrationId;
    private final Function<Map<String, Object>, UserDto> of;

    OAuthAttributes(String registrationId, Function<Map<String, Object>, UserDto> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static UserDto extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values()) // values 는 enum 의 values 이다. 이 enum 개체를 반복문 돌린다고 보면 된다.
                .filter(provider -> registrationId.equals(provider.registrationId)) // 이 enum 객체가 반복문을 돌고 있으니 provider 는 OAuthAttributes 이다.
                .findFirst() // 일치하는 registrationId를 찾는다.
                .orElseThrow(IllegalArgumentException::new) // 없으면 에러를 발생시킨다.
                .of.apply(attributes); // 이 of 가 UserDto.builder 로 값을 매핑해주는 부분이다.
    }
}
