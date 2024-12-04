package com.oreo.finalproject_5re5_be.member.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberTermsHistoryRepositoryTest {

    @Autowired
    private MemberTermsHistoryRepository memberTermsHistoryRepository;


//    @BeforeEach
//    void setUp() {
//        // 자동주입 확인
//        assertNotNull(memberTermsHistoryRepository);
//    }
}