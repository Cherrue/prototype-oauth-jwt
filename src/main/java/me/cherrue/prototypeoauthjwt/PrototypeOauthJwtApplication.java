package me.cherrue.prototypeoauthjwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@Slf4j
@SpringBootApplication
public class PrototypeOauthJwtApplication {

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        log.info(principal.toString());
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    public static void main(String[] args) {
        SpringApplication.run(PrototypeOauthJwtApplication.class, args);
    }

}
