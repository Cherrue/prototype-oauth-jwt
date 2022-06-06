package me.cherrue.prototypeoauthjwt.oauth.dto;

import lombok.Builder;
import lombok.Getter;
import me.cherrue.prototypeoauthjwt.oauth.entity.Member;
import me.cherrue.prototypeoauthjwt.oauth.entity.Role;

import java.util.HashMap;
import java.util.Map;

@Getter
public class UserDto {
    private final String registrationId;
    private final String oAuthId;
    private final String email;
    private final String name;
    private final String image;

    @Builder
    public UserDto(String registrationId, String oAuthId, String email, String name, String image) {
        this.registrationId = registrationId;
        this.oAuthId = oAuthId;
        this.email = email;
        this.name = name;
        this.image = image;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("registrationId", registrationId);
        map.put("id", oAuthId);
        map.put("name", name);
        map.put("email", email);
        map.put("image", image);
        return map;
    }

    public Member toMember() {
        return Member.builder()
                .registrationId(registrationId)
                .oAuthId(oAuthId)
                .name(name)
                .email(email)
                .image(image)
                .role(Role.ROLE_USER)
                .build();
    }
}
