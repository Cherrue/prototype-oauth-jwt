package me.cherrue.prototypeoauthjwt.jwt;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.cherrue.prototypeoauthjwt.oauth.entity.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;

@Getter
@Slf4j
@Service
public class TokenService {
    @Value("${private.key.jwt.secret}")
    private String secretKey; // 토큰 서명용 개인키. 그대로 쓰지 않고 Base64 인코딩하여 사용한다.
    private long tokenPeriod = 1000L * 60L * 10L; // 토큰 만료 시간

    @PostConstruct
    protected void init() {
        if (secretKey == null) { // Bean 이기 때문에 개인키 주입이 제대로 안 되면 최초 실행 중에 죽는다.
            throw new RuntimeException("Fail to inject configuration value");
        }
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * 토큰 생성
     * @param registrationId oauth 를 이용한 서비스 id. 복합키의 구성요소
     * @param oAuthId oauth 로그인 시 제공받은 키 값. registrationId와 조합하면 키 값이 된다.
     * @param role 사용자 권한. 우리는 일반 사용자만 있다.
     * @return 생성된 토큰
     */
    public Token generateToken(String registrationId, String oAuthId, Role role) {
        JwtSubject jwtSubject = new JwtSubject(registrationId, oAuthId);
        Claims claims = Jwts.claims().setSubject(jwtSubject.toString());
        claims.put("role", role.getKey());

        Date now = new Date();
        return new Token(
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + tokenPeriod))
                        .signWith(SignatureAlgorithm.HS256, secretKey)
                        .compact());
    }

    /**
     * 토큰이 유효한지 확인. 만료기한 뿐만 아니라, 토큰의 포맷, 사인, 토큰 내용 등을 확인한다.
     * @param token JWT 를 담은 문자열
     * @return 유효 여부
     */
    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return claimsJws.getBody().getExpiration().after(new Date());
        } catch (Exception e) {
            log.info("만료되었거나 올바르지 않은 토큰입니다" + token);
            return false;
        }
    }

    /**
     * token 에서 claim 의 subject를 가져온다.
     * 우리의 subject 는 registrationId + \t + oAuthId 의 구조를 갖는다.
     * @param token 우리가 만든 jwt token
     * @return token 에서 추출한 subject
     */
    public String getClaimsSubject(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

}
