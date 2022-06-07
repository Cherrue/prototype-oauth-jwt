package me.cherrue.prototypeoauthjwt.config;

import lombok.RequiredArgsConstructor;
import me.cherrue.prototypeoauthjwt.jwt.JwtFilter;
import me.cherrue.prototypeoauthjwt.jwt.TokenService;
import me.cherrue.prototypeoauthjwt.oauth.CustomOAuth2UserService;
import me.cherrue.prototypeoauthjwt.oauth.OAuthSuccessHandler;
import me.cherrue.prototypeoauthjwt.oauth.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final TokenService tokenService;
    private final MemberRepository memberRepository;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeRequests(a -> a
                        .antMatchers("/", "/error", "/webjars/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .logout(l -> l
                        .clearAuthentication(true)
                        .logoutSuccessUrl("/").permitAll()
                )
                .csrf(c -> c
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringAntMatchers("/h2-console/**").disable()
                )
                .httpBasic(withDefaults())
                .oauth2Login(o -> o
                        .successHandler(oAuthSuccessHandler)
                        .userInfoEndpoint()
                        .userService(customOAuth2UserService));
        // @formatter:on

        http.addFilterBefore(new JwtFilter(tokenService, memberRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/h2-console/**");
    }

}