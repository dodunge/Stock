package com.zerobase.stock.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
// OncePerRequestFilter -> 요청이 있을 때마다 filter가 수행됨.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // token은 http 프로토콜에서 header에 포함이 되는데, 어떤 key를 기준으로 토큰을 주고받을 지에 대한 키 값
    public static final String TOKEN_HEADER = "Authorization";
    // 인증 타입을 나타내기 위해 사용 -> JWT 토큰을 사용할 때에는 토큰 앞에 "Bearer"이 붙어야 함
    public static final String TOKEN_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.resolveTokenFromRequest(request);

        // 토큰 유효성 검증
        if(StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {
            Authentication auth = this.tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.info(String.format("[%s] -> %s", this.tokenProvider.getUsername(token), request.getRequestURI()));
        }

        // 필터가 연속적으로 수행될 수 있도록 필터체인을 걸어줌
        filterChain.doFilter(request, response);
    }

    // request에 있는 헤더로 부터 토큰을 꺼내오는 메서드
    public String resolveTokenFromRequest(HttpServletRequest request) {
        // key로 value가져오기
        String token = request.getHeader(TOKEN_HEADER);
        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
