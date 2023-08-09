package com.zerobase.stock.security;

import com.zerobase.stock.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1 hour
    private static final String KEY_ROLES = "roles";

    private final MemberService memberService;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    /**
     * 토큰 생성(발급)
     * @param username
     * @param roles
     * @return
     */
    public String generateToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles);

        var now = new Date();
        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 생성 시간
                .setExpiration(expiredDate) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey) // 사용할 암호화 알고리즘(HS512), 비밀키
                .compact();
    }

    // 토큰으로 부터 인증정보를 가져오는 메서드
    public Authentication getAuthentication(String jwt) {
        UserDetails userDetails = this.memberService.loadUserByUsername(this.getUsername(jwt));
        // spring에서 지원해주는 토큰 형식
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 username값을 가져오는 메서드
    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();
    }

    // 토큰이 유효한지 확인하는 메서드
    public boolean validateToken(String token) {
        // 토큰의 값이 빈 값이라면 토큰이 유효하지 않기 때문에 false리턴
       if(!StringUtils.hasText(token)) return false;

        var claims = this.parseClaims(token);
        // 토큰이 유효기간의 이전인지 아닌지에 대한 결과 리턴
        return !claims.getExpiration().before(new Date());
    }

    // 토큰으로 부터 claim 정보들을 가져오는 메서드
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            // TODO
            return e.getClaims();
        }
    }
}
