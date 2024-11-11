package com.oreo.finalproject_5re5_be.member.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

@ExtendWith(MockitoExtension.class)
class LoginAuthenticationSuccessHandlerTest {

    @InjectMocks
    private LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler;

    @Mock
    private Authentication authentication;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;


    @DisplayName("목객체 생성 및 테스트 환경 구축")
    @BeforeEach
    void setUp() {
        assertNotNull(loginAuthenticationSuccessHandler);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        User user = new User("qwefghnm1212", "dqwesf1212@!", Collections.emptyList());
        when(authentication.getName()).thenReturn(user.getUsername());
    }


    @DisplayName("쿠키와 세션 생성 테스트")
    @Test
    void 로그인_성공_세션_생성() throws ServletException, IOException {
        // 아이디 기억하기 체크
        request.setParameter("rememberMe", "true");

        // 로그인 성공 핸들러 실행
        loginAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // 세션 및 쿠키 등록 여부 확인
        HttpSession session = request.getSession();
        assertNotNull(session);

        String foundMemberIdOnSession = (String) session.getAttribute("memberId");
        assertEquals("qwefghnm1212", foundMemberIdOnSession);

        boolean check = false;

        for (Cookie cookie : response.getCookies()) {
            if ("memberId".equals(cookie.getName()) && "qwefghnm1212".equals(cookie.getValue())) {
                check = true;
                break;
            }
        }

        assertTrue(check);
    }

    @DisplayName("쿠키 삭제, 세션 생성 테스트")
    @Test
    void 로그인_성공_쿠키_삭제_세션_생성() throws ServletException, IOException {
        // 로그인 성공 핸들러 실행
        loginAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // 세션 등록, 쿠키 삭제 여부 확인
        HttpSession session = request.getSession();
        assertNotNull(session);

        String foundMemberIdOnSession = (String) session.getAttribute("memberId");
        assertEquals("qwefghnm1212", foundMemberIdOnSession);

        boolean check = true;
        for (Cookie cookie : response.getCookies()) {
            if ("memberId".equals(cookie.getName()) && "qwefghnm1212".equals(cookie.getValue())) {
                check = false;
                break;
            }
        }

        assertTrue(check);
    }
}