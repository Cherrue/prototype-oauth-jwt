package me.cherrue.prototypeoauthjwt.oauth.entity;

import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String registrationId;

    private String oAuthId;

    private String email;

    private String name;

    private String image;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(String registrationId, String oAuthId, String email, String name, String image, Role role) {
        this.registrationId = registrationId;
        this.oAuthId = oAuthId;
        this.email = email;
        this.name = name;
        this.image = image;
        this.role = role;
    }

    public Member update(String name, String email, String image) {
        this.name = name;
        this.email = email;
        this.image = image;

        return this;
    }
}
