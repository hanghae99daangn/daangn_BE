package com.sparta.market.global.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.market.domain.user.dto.LoginRequestDto;
import com.sparta.market.domain.user.entity.UserRoleEnum;
import com.sparta.market.global.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/user/login"); // POST 사용
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        String email = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();
        Long userId = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getId(); // userId 추출
        String nickname = (((UserDetailsImpl) authResult.getPrincipal()).getUser().getNickname());

        String token = jwtUtil.createToken(email, role);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        log.info("사용자 '{}'의 로그인 성공", email);

        response.setCharacterEncoding("UTF-8");

        String responseBody = "로그인을 완료했습니다. " + "\n"
                + "사용자 아이디: " + userId + "\n"
                + "사용자 email: " + email + "\n"
                + "사용자 nickname: " + nickname + "\n"
                + "사용자 role: " + role;
        response.getWriter().write(responseBody);
    }

    @SneakyThrows
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setCharacterEncoding("UTF-8");
        String responseDto = new ObjectMapper().writeValueAsString(Map.of("data", "BAD_REQUEST","message", "아이디 혹은 비밀번호를 확인해주세요.", "resultCode", HttpServletResponse.SC_BAD_REQUEST));
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.getWriter().write(responseDto);
        response.getWriter().flush();
        response.getWriter().close();
        log.info("로그인 실패: {}", failed.getMessage());
    }
}
