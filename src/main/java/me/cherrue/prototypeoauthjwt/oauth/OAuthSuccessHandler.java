package me.cherrue.prototypeoauthjwt.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.cherrue.prototypeoauthjwt.jwt.Token;
import me.cherrue.prototypeoauthjwt.jwt.TokenService;
import me.cherrue.prototypeoauthjwt.oauth.dto.UserDto;
import me.cherrue.prototypeoauthjwt.oauth.entity.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();

        // CustomOAuth2UserService 에서 UserDto.toMap 해서 넘겼기 때문에 키 값이 고정이다.
        UserDto userDto = UserDto.builder()
                .registrationId((String) attributes.get("registrationId"))
                .oAuthId(String.valueOf(attributes.get("id"))) // Integer
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .image((String) attributes.get("image"))
                .build();

        Token token = tokenService.generateToken(userDto.getRegistrationId(), userDto.getOAuthId(), Role.ROLE_USER);
        log.info(token.getToken());

        writeTokenResponse(response, token);
    }

    private void writeTokenResponse(HttpServletResponse response, Token token)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        response.addHeader("Auth", token.getToken());
        response.setContentType("application/json;charset=UTF-8");

        var writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(token));
        writer.flush();
    }
}
