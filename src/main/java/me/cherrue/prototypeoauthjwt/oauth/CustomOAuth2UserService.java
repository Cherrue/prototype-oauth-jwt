package me.cherrue.prototypeoauthjwt.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.cherrue.prototypeoauthjwt.oauth.dto.UserDto;
import me.cherrue.prototypeoauthjwt.oauth.entity.Member;
import me.cherrue.prototypeoauthjwt.oauth.entity.Role;
import me.cherrue.prototypeoauthjwt.oauth.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 기본 동작
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        // 정보 제공 서비스의 id
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // 제공 서비스 별 키 값으로 사용되는 attribute 의 이름
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        // 수정할 수 있게 새로운 Map 에 담음
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("registrationId", registrationId); // 계속 필요한 정보라 담아버림

        // 회원 가입에 사용할 데이터 추출
        UserDto userDto = OAuthAttributes.extract(registrationId, attributes);
        Map<String, Object> memberAttributes = userDto.toMap();

        log.info(userDto.toString());
        log.info(memberAttributes.toString());

        // 우리 DB에 저장 == 회원가입
        Member member = saveOrUpdate(userDto);

        // 우리 서비스에 맞는 권한 부여
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.getKey())),
                memberAttributes, // 필요한 데이터만 전달
                userNameAttributeName
        );
    }


    private Member saveOrUpdate(UserDto userDto) {
        // OAuth 제공 서비스 측 데이터가 변경될 수 있기 때문에 name, email, picture 업데이트
        // registrationId, oAuthId 는 기본적으로 변경이 없는 데이터입니다.
        Member member = memberRepository.findByRegistrationIdAndOAuthId(userDto.getRegistrationId(), userDto.getOAuthId())
                .map(m -> m.update(userDto.getName(), userDto.getEmail(), userDto.getImage())) // OAuth 서비스 사이트에서 유저 정보 변경이 있을 수 있기 때문에 우리 DB에도 update
                .orElse(userDto.toMember());
        return memberRepository.save(member);
    }
}