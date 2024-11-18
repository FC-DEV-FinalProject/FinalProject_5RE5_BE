package com.oreo.finalproject_5re5_be.vc.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
@Slf4j
@Transactional
class VcApiServiceImplTest {
    @Autowired
    private VcApiService vcApiService;

    @Test
    @DisplayName("타겟파일을 ID로 변환 시키는 테스트")
    @Disabled//빌드 테스트 동작되지 않게 설정 API 요청되지 않게
    void trgIdCreate() throws IOException {
        FileInputStream inputStream = new FileInputStream("mario.mp3");

        // MockMultipartFile 생성
        MultipartFile multipartFile = new MockMultipartFile(
                "test",                       // 필드 이름
                "test.wav",                   // 파일 이름
                "text/plain",                 // MIME 타입
                inputStream                   // 파일 데이터
        );
        String s = vcApiService.trgIdCreate(multipartFile);
        log.info(s);
    }

    @Test
    @Disabled
    @DisplayName("타겟 ID와 소스파일로 결과 파일을 만드는 테스트 코드")
    void resultCreate() throws IOException {
        FileInputStream inputStream = new FileInputStream("ttsoutput.mp3");

        // MockMultipartFile 생성
        MultipartFile multipartFile = new MockMultipartFile(
                "test",                       // 필드 이름
                "test.wav",                   // 파일 이름
                "text/plain",                 // MIME 타입
                inputStream                   // 파일 데이터
        );
        MultipartFile file = vcApiService.resultFileCreate(multipartFile, "Yr8eh9CaB3tXeZD1ogAu");
        saveMultipartFileToProjectRoot(file, "result.mp3");
    }
    //파일 저장 테스트 확인용
    public String saveMultipartFileToProjectRoot(MultipartFile file, String fileName) throws IOException {
        // Spring 프로젝트 루트 디렉토리
        String projectRoot = System.getProperty("user.dir");

        // 파일 저장 경로 설정 (루트 디렉토리 바로 위에 저장)
        String savePath = projectRoot + File.separator + fileName;

        // 파일 객체 생성
        File destinationFile = new File(savePath);

        // 파일 저장
        file.transferTo(destinationFile);

        return destinationFile.getAbsolutePath();
    }
    @Test
    @Disabled
    @DisplayName("타겟파일을 ID로 만들고 ID와 소스파일로 결과 파일을 만드는 테스트 코드")
    public void resultTest() throws IOException {
        FileInputStream inputStream = new FileInputStream("mario.mp3");//Trg 파일 입력

        // MockMultipartFile 생성
        MultipartFile multipartFile = new MockMultipartFile(
                "test",                       // 필드 이름
                "test.wav",                   // 파일 이름
                "text/plain",                 // MIME 타입
                inputStream                   // 파일 데이터
        );
        String s = vcApiService.trgIdCreate(multipartFile);//trg ID 생성

        FileInputStream inputStream2 = new FileInputStream("ttsoutput.mp3");//소스파일 입력

        // MockMultipartFile 생성
        MultipartFile multipartFile2 = new MockMultipartFile(
                "test",                       // 필드 이름
                "test.wav",                   // 파일 이름
                "text/plain",                 // MIME 타입
                inputStream2                  // 파일 데이터
        );
        MultipartFile file = vcApiService.resultFileCreate(multipartFile2, s);//Trg Id 소스파일로 결과 파일 생성
        saveMultipartFileToProjectRoot(file, "result.mp3");
    }
}