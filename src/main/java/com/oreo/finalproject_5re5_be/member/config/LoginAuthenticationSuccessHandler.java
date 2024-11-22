package com.oreo.finalproject_5re5_be.member.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oreo.finalproject_5re5_be.member.dto.CustomUserDetails;
import com.oreo.finalproject_5re5_be.member.entity.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

// 로그인 성공시 처리되는 핸들러
@Component
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        // 쿠키 처리
        handleSession(request, authentication);
        // 세션 처리
        handleCookie(request, response, authentication);

        // 로그인 성공시 유저 정보 반환
        // 사용자 정보 추출
        Object principal = authentication.getPrincipal();
        Map<String, Object> memberInfo = new HashMap<>();


        if (principal instanceof CustomUserDetails) {
            CustomUserDetails memberDetails = (CustomUserDetails) principal;
            Member member = memberDetails.getMember();

            memberInfo.put("seq", member.getSeq());
            memberInfo.put("id", member.getId());
            memberInfo.put("name", member.getName());
            memberInfo.put("email", member.getEmail());
        } else {
            memberInfo.put("name", principal.toString());
        }

        // JSON으로 응답
        response.setContentType("application/json;charset=UTF-8");
        new ObjectMapper().writeValue(response.getWriter(), memberInfo);
    }

    // 세션 등록
    private void handleSession(HttpServletRequest request, Authentication authentication) {
        // Authentication에서 회원 아이디 조회
        String memberId = authentication.getName();
        // 세션 조회
        HttpSession session = request.getSession();
        // 세션에 아이디 등록
        session.setAttribute("memberId", memberId);
    }

    // 쿠키 등록
    // 만약 쿠키 체크가 rememberMe로 되어 있다고 가정. 이 부분 추후에 프론트랑 얘기해야함
    private void handleCookie(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        // Authentication에서 회원 아이디 조회
        String memberId = authentication.getName();
        // request에서 아이디 체크 유무 조회
        String rememberMe = request.getParameter("rememberMe");

        // 아이디 체크가 되어 있음
        if (rememberMe != null) {
            // 쿠키 생성
            Cookie cookie = new Cookie("memberId", memberId);
            // 1일 간 유지
            cookie.setMaxAge(60 * 60 * 24 * 1);
            // 쿠키 등록
            response.addCookie(cookie);
        } else {
            // 아이디 체크가 되어 있지 않음
            Cookie cookie = new Cookie("memberId", "");
            // 쿠키 수동 삭제
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }

    }

}
