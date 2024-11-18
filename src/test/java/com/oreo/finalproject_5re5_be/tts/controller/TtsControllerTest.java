package com.oreo.finalproject_5re5_be.tts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oreo.finalproject_5re5_be.global.constant.BatchProcessType;
import com.oreo.finalproject_5re5_be.project.entity.Project;
import com.oreo.finalproject_5re5_be.tts.dto.request.TtsAttributeInfo;
import com.oreo.finalproject_5re5_be.tts.dto.request.TtsSentenceBatchInfo;
import com.oreo.finalproject_5re5_be.tts.dto.request.TtsSentenceBatchRequest;
import com.oreo.finalproject_5re5_be.tts.dto.request.TtsSentenceRequest;
import com.oreo.finalproject_5re5_be.tts.dto.response.SentenceInfo;
import com.oreo.finalproject_5re5_be.tts.dto.response.TtsSentenceDto;
import com.oreo.finalproject_5re5_be.tts.dto.response.TtsSentenceListDto;
import com.oreo.finalproject_5re5_be.tts.entity.Style;
import com.oreo.finalproject_5re5_be.tts.entity.TtsSentence;
import com.oreo.finalproject_5re5_be.tts.entity.Voice;
import com.oreo.finalproject_5re5_be.global.exception.EntityNotFoundException;
import com.oreo.finalproject_5re5_be.global.exception.ErrorCode;
import com.oreo.finalproject_5re5_be.tts.service.TtsSentenceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TtsController.class)
class TtsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TtsSentenceService ttsSentenceService;

    @Autowired
    private ObjectMapper objectMapper;

    /*
    TtsController 테스트 클래스
    - TtsController의 addSentence 메서드에 대한 테스트 케이스를 작성
    - 다양한 유효성 검증과 에러 상황을 포함하여 테스트 진행

    Test Scenarios

    1. 성공적인 문장 생성 요청 테스트
    - 유효한 projectSeq와 TtsSentenceRequest 입력 시
    - HTTP 상태 200과 올바른 JSON 응답을 반환하는지 확인

    2. 텍스트 필드 누락으로 인한 유효성 검증 에러 테스트
    - text 필드가 누락된 TtsSentenceRequest 입력 시
    - HTTP 상태 400과 텍스트 필드 관련 에러 메시지 반환을 확인

    3. voiceSeq 필드 누락으로 인한 유효성 검증 에러 테스트
    - voiceSeq 필드가 누락된 TtsSentenceRequest 입력 시
    - HTTP 상태 400과 voiceSeq 관련 에러 메시지 반환을 확인

    4. 잘못된 projectSeq로 인한 유효성 검증 에러 테스트
    - 유효하지 않은 projectSeq 입력 시
    - HTTP 상태 400과 projectSeq 관련 에러 메시지 반환을 확인

    5. voiceSeq로 Voice 엔티티를 찾을 수 없는 경우
    - 유효한 projectSeq와 없는 voiceSeq 입력 시
    - HTTP 상태 400과 voiceSeq 관련 에러 메시지 반환을 확인

    6. styleSeq로 Style 엔티티를 찾을 수 없는 경우
    - 유효한 projectSeq와 없는 styleSeq 입력 시
    - HTTP 상태 400과 styleSeq 관련 에러 메시지 반환을 확인

    7. 잘못된 속성 값으로 인한 유효성 검증 에러 테스트
    - 유효 범위를 벗어난 속성 값 (예: volume)을 가진 TtsSentenceRequest 입력 시
    - HTTP 상태 400과 속성 관련 에러 메시지 반환을 확인

    8. 내부 서버 에러 테스트
    - 유효한 입력값이지만 메서드 내부에서 예외 발생 시
    - HTTP 상태 500과 예상치 못한 에러 메시지 반환을 확인
 */

    // 1. 성공적인 문장 생성 요청 테스트
    @WithMockUser
    @Test
    @DisplayName("성공적인 문장 생성 요청 테스트")
    public void registerSentence_successfulCreation() throws Exception {
        // given - 유효한 프로젝트 ID와 문장 생성 요청 객체 초기화
        Long projectSeq = 1L;
        Long voiceSeq = 1L;

        // 1. 요청 객체 생성
        // 속성 정보 객체 생성
        TtsAttributeInfo attributeInfo = createAttributeInfo();
        // 문장 생성 요청 객체 생성
        TtsSentenceRequest requestBody = createSentenceRequest(attributeInfo);

        // 2. 응답 객체 생성
        // voice 객체 생성
        Voice voice = createVoice(voiceSeq);
        // project 객체 생성
        Project project = createProject(projectSeq);
        // TtsSentence 객체 생성
        TtsSentence ttsSentence = createTtsSentence(voice, project);
        // TtsSentenceDto 객체 생성
        TtsSentenceDto response = TtsSentenceDto.of(ttsSentence);

        // 3. TtsSentenceService의 addSentence 메서드에 대한 모의 동작 설정
        when(ttsSentenceService.addSentence(eq(projectSeq), any(TtsSentenceRequest.class))).thenReturn(response); // 응답 객체 반환

        // when
        // 4. 컨트롤러의 addSentence 메서드에 요청을 전송하여 테스트 수행
        mockMvc.perform(post("/api/project/{projectSeq}/tts/sentence", projectSeq) // URL 설정
                        .contentType(MediaType.APPLICATION_JSON)                // 요청 본문 타입 설정
                        .content(objectMapper.writeValueAsString(requestBody))      // 요청 본문으로 JSON 데이터를 직렬화하여 전송
                        .with(csrf()))                                          // CSRF 토큰 추가
                // Then - 예상 응답 상태와 JSON 구조 확인
                .andExpect(status().isCreated())                                   // HTTP 상태 201 확인
                .andExpect(jsonPath("$.status", is(HttpStatus.CREATED.value())))// 응답의 상태가 200인지 확인
                .andExpect(jsonPath("$.response.sentence").exists());         // JSON 응답에 sentence 필드가 존재하는지 확인
    }

    // 2. 텍스트 필드 누락으로 인한 유효성 검증 에러 테스트
    @Test
    @WithMockUser
    @DisplayName("유효성 검증 에러 - 텍스트 필드 누락")
    public void registerSentence_validationErrorForMissingTextField() throws Exception {
        // given - 유효한 프로젝트 ID와 텍스트 필드가 누락된 요청 객체 초기화
        Long projectSeq = 1L;

        // 1. 요청 객체 생성
        // 속성 정보 객체 생성
        TtsAttributeInfo attributeInfo = createAttributeInfo();

        // 텍스트 필드가 없는 요청 객체 생성
        TtsSentenceRequest requestBody = TtsSentenceRequest.builder().voiceSeq(1L) // 유효한 voiceSeq 설정
                .styleSeq(1L) // 유효한 styleSeq 설정
                .text(null)   // 텍스트 필드 누락
                .order(1)     // 표시 순서 설정
                .attribute(attributeInfo) // 빈 속성 정보 설정
                .build();

        // when
        // 2. 컨트롤러의 addSentence 메서드에 요청을 전송하여 테스트 수행
        mockMvc.perform(post("/api/project/{projectSeq}/tts/sentence", projectSeq) // URL 설정
                        .contentType(MediaType.APPLICATION_JSON)              // 요청 본문 타입 설정
                        .content(objectMapper.writeValueAsString(requestBody)) // 요청 본문으로 JSON 데이터를 직렬화하여 전송
                        .with(csrf()))
                // Then - 예상 응답 상태와 오류 메시지 확인
                .andExpect(status().is(ErrorCode.INVALID_INPUT_VALUE.getStatus())).andExpect(jsonPath("$.response.message", is(ErrorCode.INVALID_INPUT_VALUE.getMessage())));
    }

    // 3. voiceSeq 필드 누락으로 인한 유효성 검증 에러 테스트
    @Test
    @DisplayName("유효성 검증 에러 - voiceSeq 필드 누락")
    @WithMockUser
    public void registerSentence_validationErrorForMissingVoiceSeqField() throws Exception {
        // Given - 유효한 프로젝트 ID와 voiceSeq 필드가 누락된 요청 객체 초기화
        Long projectSeq = 1L;

        TtsAttributeInfo attributeInfo = createAttributeInfo();

        // 텍스트 필드가 없는 요청 객체 생성
        TtsSentenceRequest requestBody = TtsSentenceRequest.builder().voiceSeq(null) // voiceSeq 필드 누락
                .styleSeq(1L) // 유효한 styleSeq 설정
                .text("sample text")   // 텍스트 필드 누락
                .order(1)     // 표시 순서 설정
                .attribute(attributeInfo) // 빈 속성 정보 설정
                .build();

        // When - 컨트롤러의 addSentence 메서드에 요청을 전송하여 테스트 수행
        mockMvc.perform(post("/api/project/{projectSeq}/tts/sentence", projectSeq) // URL 설정
                        .contentType(MediaType.APPLICATION_JSON)              // 요청 본문 타입 설정
                        .content(objectMapper.writeValueAsString(requestBody)) // 요청 본문으로 JSON 데이터를 직렬화하여 전송
                        .with(csrf()))
                // Then - 예상 응답 상태와 오류 메시지 확인
                .andExpect(status().is(ErrorCode.INVALID_INPUT_VALUE.getStatus()))                            // HTTP 상태 400 확인
                .andExpect(jsonPath("$.response.message", is(ErrorCode.INVALID_INPUT_VALUE.getMessage())));
    }

    // 4. 잘못된 projectSeq로 인한 유효성 검증 에러 테스트
    @Test
    @DisplayName("유효성 검증 에러 - 잘못된 projectSeq")
    @WithMockUser
    public void registerSentence_validationErrorForInvalidProjectSeq() throws Exception {
        // Given - 잘못된 projectSeq 설정
        Long invalidProjectSeq = -100L;

        // 1. 요청 객체 생성
        // 속성 정보 객체 생성
        TtsAttributeInfo attributeInfo = createAttributeInfo();
        // 문장 생성 요청 객체 생성
        TtsSentenceRequest requestBody = createSentenceRequest(attributeInfo);


        // When - 서비스에서 예외 발생을 설정
        when(ttsSentenceService.addSentence(eq(invalidProjectSeq), any(TtsSentenceRequest.class))).thenThrow(new IllegalArgumentException("projectSeq is invalid"));

        // Then - 요청 전송 및 응답 검증
        mockMvc.perform(post("/api/project/{projectSeq}/tts/sentence", invalidProjectSeq).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestBody)).with(csrf()))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.response.message", is("projectSeq is invalid")));
                .andExpect(status().is(ErrorCode.INVALID_INPUT_VALUE.getStatus()))                            // HTTP 상태 400 확인
                .andExpect(jsonPath("$.response.message", is(ErrorCode.INVALID_INPUT_VALUE.getMessage())));
    }

    // 5. voiceSeq 로 Voice 엔티티를 찾을 수 없는 경우
    @Test
    @DisplayName("유효성 검증 에러 - voiceSeq 로 Voice 엔티티를 찾을 수 없는 경우")
    @WithMockUser
    public void registerSentence_notFoundVoiceEntity() throws Exception {
        // Given
        Long projectSeq = 1L;

        // 속성 정보 객체 생성
        TtsAttributeInfo attributeInfo = createAttributeInfo();

        // 문장 생성 요청 객체 생성
        TtsSentenceRequest requestBody = TtsSentenceRequest.builder().voiceSeq(-1L).text("Valid text").attribute(attributeInfo).build();

        // When - 서비스에서 예외 발생을 설정
        when(ttsSentenceService.addSentence(eq(projectSeq), any(TtsSentenceRequest.class))).thenThrow(new EntityNotFoundException());

        // Then - 요청 전송 및 응답 검증
        mockMvc.perform(post("/api/project/{projectSeq}/tts/sentence", projectSeq).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestBody)).with(csrf())).andExpect(status().is(ErrorCode.ENTITY_NOT_FOUND.getStatus())).andExpect(jsonPath("$.response.message", is(ErrorCode.ENTITY_NOT_FOUND.getMessage())));
    }

    // 6. styleSeq로 Style 엔티티를 찾을 수 없는 경우
    @Test
    @DisplayName("유효성 검증 에러 - 잘못된 styleSeq")
    @WithMockUser
    public void registerSentence_notFoundStyleEntity() throws Exception {
        // Given - 유효한 projectSeq와 잘못된 styleSeq 설정
        Long projectSeq = 1L;
        TtsSentenceRequest request = TtsSentenceRequest.builder().voiceSeq(1L).styleSeq(-1L).text("Valid text").build();

        // When - 서비스에서 예외 발생을 설정
        when(ttsSentenceService.addSentence(eq(projectSeq), any(TtsSentenceRequest.class))).thenThrow(new EntityNotFoundException());

        // Then - 요청 전송 및 응답 검증
        mockMvc.perform(post("/api/project/{projectSeq}/tts/sentence", projectSeq).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)).with(csrf())).andExpect(status().is(ErrorCode.ENTITY_NOT_FOUND.getStatus())).andExpect(jsonPath("$.response.message", is(ErrorCode.ENTITY_NOT_FOUND.getMessage())));
    }

    // 7. 잘못된 속성 값으로 인한 유효성 검증 에러 테스트
    @Test
    @DisplayName("유효성 검증 에러 - 잘못된 속성 값")
    @WithMockUser
    public void registerSentence_validationErrorForInvalidAttributeFields() throws Exception {
        // Given - 유효한 projectSeq와 잘못된 속성 값 설정
        Long projectSeq = 1L;

        // 속성 정보 객체 생성
        TtsAttributeInfo attributeInfo = TtsAttributeInfo.builder().volume(200) // 유효 범위 초과 설정
                .speed(1.0f) // 유효한 speed 설정
                .stPitch(0)  // 유효한 stPitch 설정
                .emotion("neutral") // 유효한 emotion 설정
                .emotionStrength(100) // 유효한 emotionStrength 설정
                .sampleRate(16000) // 유효한 sampleRate 설정
                .alpha(0) // 유효한 alpha 설정
                .endPitch(0.0f) // 유효한 endPitch 설정
                .audioFormat("wav") // 유효한 audioFormat 설정
                .build(); // 유효한 속성 정보 초기화

        TtsSentenceRequest requestBody = TtsSentenceRequest.builder().voiceSeq(1L).text("Valid text").attribute(attributeInfo) // 유효 범위 초과 설정
                .build();

        // When - 서비스에서 예외 발생을 설정
        // Then - 요청 전송 및 응답 검증
        mockMvc.perform(post("/api/project/{projectSeq}/tts/sentence", projectSeq).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestBody)).with(csrf())).andExpect(status().is(ErrorCode.INVALID_INPUT_VALUE.getStatus())).andExpect(jsonPath("$.response.message", is(ErrorCode.INVALID_INPUT_VALUE.getMessage())));
    }

    // 8. 내부 서버 에러 테스트
    @Test
    @DisplayName("내부 서버 에러 테스트")
    @WithMockUser
    public void registerSentence_internalServerError() throws Exception {
        // Given - 유효한 projectSeq와 요청 객체 설정
        Long projectSeq = 1L;
        TtsSentenceRequest requestBody = TtsSentenceRequest.builder().voiceSeq(1L).text("Valid text").build();

        // When - 서비스에서 런타임 예외 발생을 설정
        when(ttsSentenceService.addSentence(eq(projectSeq), any(TtsSentenceRequest.class))).thenThrow(new RuntimeException("Unexpected error"));

        // Then - 요청 전송 및 응답 검증
        mockMvc.perform(post("/api/project/{projectSeq}/tts/sentence", projectSeq).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestBody)).with(csrf())).andExpect(status().isInternalServerError()).andExpect(jsonPath("$.response.message", is(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())));
    }

    /*
    테스트 시나리오: TTS 문장 수정 요청 (PUT /sentence/{tsSeq})

    1. 성공 케이스
    - 조건:
    - projectSeq와 tsSeq가 유효하고 존재.
    - updateRequest의 필드들이 모두 유효한 값.
    - 결과:
    - 상태 코드 200 OK 반환.
    - 수정된 TtsSentenceDto 응답.

    2. 유효성 검증 실패
    2.1. PathVariable 유효성 검증 실패
    - projectSeq가 null 또는 1 미만.
    - tsSeq가 null 또는 1 미만.
    - 결과: 400 Bad Request 반환, 에러 메시지 포함.

    2.2. RequestBody 유효성 검증 실패
    - updateRequest가 null.
    - updateRequest의 필드 중 하나라도 유효성 조건(@NotNull 등)을 만족하지 않음.
    - 결과: 400 Bad Request 반환, 에러 메시지 포함.

    3. 존재하지 않는 리소스
    3.1. 존재하지 않는 projectSeq
    - projectSeq가 DB에 존재하지 않음.
    - 결과: 404 Not Found 반환, "Project not found with id: <projectSeq>" 에러 메시지.

    3.2. 존재하지 않는 tsSeq
    - tsSeq가 DB에 존재하지 않음.
    - 결과: 404 Not Found 반환, "TtsSentence not found with id: <tsSeq>" 에러 메시지.

    3.3. 존재하지 않는 voiceSeq
    - updateRequest.getVoiceSeq()에 해당하는 Voice가 존재하지 않음.
    - 결과: 404 Not Found 반환, "Voice not found with id: <voiceSeq>" 에러 메시지.

    3.4. 존재하지 않는 styleSeq
    - updateRequest.getStyleSeq()에 해당하는 Style이 존재하지 않음.
    - 결과: 404 Not Found 반환, "Style not found with id: <styleSeq>" 에러 메시지.

    4. 예외 처리
    - updateRequest가 null인 경우 IllegalArgumentException 발생.
    - DB 저장(save) 과정에서 예외 발생 (예: 데이터 무결성 위반).
    - 결과: 500 Internal Server Error 반환.

    5. 연관 데이터 처리
    - TtsSentence에 연관된 ttsAudioFile이 존재할 경우, null로 설정 후 성공적으로 저장.
    - 결과: 상태 코드 200 OK 반환, 수정된 데이터 확인.
    */

    // 1. 성공적인 문장 수정 요청 테스트
    @WithMockUser
    @Test
    @DisplayName("성공적인 문장 수정 요청 테스트")
    public void updateSentence_successfulUpdate() throws Exception {
        // given
        Long projectSeq = 1L;
        Long tsSeq = 1L;
        Long voiceSeq = 1L;
        String updatedText = "Updated text";

        // 1. 요청 객체 생성
        TtsAttributeInfo attributeInfo = createAttributeInfo();
        TtsSentenceRequest requestBody = createSentenceRequest(attributeInfo);

        // 2. 응답 객체 생성
        Voice voice = createVoice(voiceSeq);
        Project project = createProject(projectSeq);
        TtsSentence sentence = createTtsSentence(project, tsSeq, voice, updatedText);
        TtsSentenceDto response = TtsSentenceDto.of(sentence);

        // mock 서비스 동작 설정
        when(ttsSentenceService.updateSentence(eq(projectSeq), eq(tsSeq), any(TtsSentenceRequest.class)))
                .thenReturn(response);

        // when
        mockMvc.perform(put("/api/project/{projectSeq}/tts/sentence/{tsSeq}", projectSeq, tsSeq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .with(csrf()))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.response.sentence.text", is(sentence.getText())))
                .andExpect(jsonPath("$.response.sentence.order", is(sentence.getSortOrder())))
                .andDo(print());

    }

    // 2.1. PathVariable 유효성 검증 실패 - projectSeq 또는 tsSeq가 1 미만
    @WithMockUser
    @Test
    @DisplayName("유효성 검증 실패 - PathVariable이 1 미만")
    public void updateSentence_validationErrorForInvalidPathVariable() throws Exception {
        // given
        Long invalidProjectSeq = 0L;
        Long invalidTsSeq = 0L;
        TtsAttributeInfo attributeInfo = createAttributeInfo();
        TtsSentenceRequest requestBody = createSentenceRequest(attributeInfo);

        // when, then
        mockMvc.perform(put("/api/project/{projectSeq}/tts/sentence/{tsSeq}", invalidProjectSeq, invalidTsSeq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())));
    }

    // 2.2. RequestBody 유효성 검증 실패 - 필드 누락
    @WithMockUser
    @Test
    @DisplayName("유효성 검증 실패 - RequestBody 필드 누락")
    public void updateSentence_validationErrorForInvalidRequestBody() throws Exception {
        // given
        Long projectSeq = 1L;
        Long tsSeq = 1L;
        TtsSentenceRequest invalidRequestBody = TtsSentenceRequest.builder().build(); // 필드가 없는 요청 객체

        // when, then
        mockMvc.perform(put("/api/project/{projectSeq}/tts/sentence/{tsSeq}", projectSeq, tsSeq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestBody))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())));
    }

    // 3.1. 존재하지 않는 projectSeq
    @WithMockUser
    @Test
    @DisplayName("존재하지 않는 projectSeq로 인한 예외")
    public void updateSentence_notFoundProjectSeq() throws Exception {
        // given
        Long nonExistentProjectSeq = 99999L;
        Long tsSeq = 1L;
        TtsAttributeInfo attributeInfo = createAttributeInfo();
        TtsSentenceRequest requestBody = createSentenceRequest(attributeInfo);

        // mock 서비스 동작 설정
        when(ttsSentenceService.updateSentence(eq(nonExistentProjectSeq), eq(tsSeq), any(TtsSentenceRequest.class)))
                .thenThrow(new EntityNotFoundException("Project not found with id: " + nonExistentProjectSeq));

        // when, then
        mockMvc.perform(put("/api/project/{projectSeq}/tts/sentence/{tsSeq}", nonExistentProjectSeq, tsSeq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .with(csrf()))
                .andExpect(status().is(ErrorCode.ENTITY_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("$.status", is(ErrorCode.ENTITY_NOT_FOUND.getStatus())))
                .andExpect(jsonPath("$.response.message", is("Project not found with id: " + nonExistentProjectSeq)));
    }

    // 4. 예외 처리 - RuntimeException 발생
    @WithMockUser
    @Test
    @DisplayName("예외 처리 - RuntimeException 발생")
    public void updateSentence_internalServerError() throws Exception {
        // given
        Long projectSeq = 1L;
        Long tsSeq = 1L;

        TtsAttributeInfo attributeInfo = createAttributeInfo();
        TtsSentenceRequest requestBody = createSentenceRequest(attributeInfo);

        // mock 서비스 동작 설정
        when(ttsSentenceService.updateSentence(eq(projectSeq), eq(tsSeq), any(TtsSentenceRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // when, then
        mockMvc.perform(put("/api/project/{projectSeq}/tts/sentence/{tsSeq}", projectSeq, tsSeq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .with(csrf()))
                .andExpect(status().is(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()))
                .andExpect(jsonPath("$.status", is(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())))
                .andExpect(jsonPath("$.response.message", is(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())));
    }


    /*
    테스트 시나리오: batchSave 메서드 테스트

    1. 성공 케이스
    - 조건:
    - 유효한 projectSeq와 batchRequest가 주어짐.
    - batchRequest의 sentenceList가 유효한 데이터를 포함함.
    - 기대 결과:
    - HTTP 상태 201(Created) 반환.
    - JSON 응답에 올바른 TtsSentenceListDto 데이터 포함.

    2. 유효성 검증 실패
    2.1 잘못된 projectSeq
    - 조건:
    - projectSeq가 1 미만으로 설정됨.
    - 기대 결과:
    - HTTP 상태 400(Bad Request) 반환.
    - JSON 응답에 유효성 검증 실패 메시지 포함.

    2.2 sentenceList가 null
    - 조건:
    - batchRequest의 sentenceList가 null로 설정됨.
    - 기대 결과:
    - HTTP 상태 400(Bad Request) 반환.
    - JSON 응답에 예외 메시지 포함.

    3. Mock 데이터 생성
    - Mock BatchRequest:
    - 유효한 sentenceList와 데이터를 포함한 TtsSentenceBatchRequest를 생성.
    - Mock BatchInfo:
    - 유효한 SentenceInfo와 BatchProcessType을 포함한 TtsSentenceBatchInfo를 생성.
    - Mock TtsSentenceListDto:
    - Mock TtsSentenceDto를 포함하는 TtsSentenceListDto를 생성하여 응답 데이터를 시뮬레이션.
    */

    // 1. 성공 케이스
    @Test
    @DisplayName("batchSave - 성공 케이스")
    @WithMockUser
    void batchSave_Success() throws Exception {
        // given: 유효한 projectSeq와 batchRequest 생성
        Long projectSeq = 1L;
        Long voiceSeq = 1L;
        Long styleSeq = 1L;
        String text = "Test text";

        Project project = createProject(projectSeq);
        Voice voice = createVoice(voiceSeq);
        Style style = createStyle(styleSeq);
        TtsAttributeInfo attributeInfo = createAttributeInfo();

        // 1. BatchRequest 만들기
        List<TtsSentenceBatchInfo> batchList = IntStream.range(0, 10)
                .mapToObj(repeatCount -> {
                    Long tsSeq = (long) repeatCount;
                    SentenceInfo sentenceInfo = createSentenceInfo(tsSeq, voiceSeq, styleSeq, text, repeatCount, attributeInfo);

                    // TtsSentenceBatchInfo 생성 후 batchList에 추가
                    // batchList에 추가할 때마다 processType을 번갈아가며 설정
                    BatchProcessType processType = repeatCount % 2 == 0 ? BatchProcessType.CREATE : BatchProcessType.UPDATE;
                    return createBatchInfo(processType, sentenceInfo);
                })
                .toList();

        TtsSentenceBatchRequest batchRequest = createBatchRequest(batchList);

        // 2. 응답 생성
        List<TtsSentenceDto> SentenceList = IntStream.range(0, 10)
                .mapToObj(repeatCount -> {
                    Long tsSeq = (long) repeatCount;
                    SentenceInfo sentenceInfo = createSentenceInfo(tsSeq, voiceSeq, styleSeq, text, repeatCount, attributeInfo);
                    return TtsSentenceDto.builder()
                            .sentence(sentenceInfo)
                            .build();
                })
                .toList();

        TtsSentenceListDto response = TtsSentenceListDto.builder()
                .sentenceList(SentenceList)
                .build();

        // Mock 서비스 동작 설정
        when(ttsSentenceService.batchSaveSentence(eq(projectSeq), any(TtsSentenceBatchRequest.class)))
                .thenReturn(response);

        // when: 컨트롤러 호출
        mockMvc.perform(post("/api/project/{projectSeq}/tts/batch", projectSeq)
                        .contentType(MediaType.APPLICATION_JSON) // JSON 타입 설정
                        .content(objectMapper.writeValueAsString(batchRequest)) // 요청 본문 데이터
                        .with(csrf())) // CSRF 추가
                // then: 예상 응답 검증
                .andExpect(status().isOk()) // HTTP 상태 201 확인
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value())) // 응답 상태 코드 확인
                .andExpect(jsonPath("$.response.sentenceList").exists()) // 응답에 sentences 필드가 존재하는지 확인
                .andExpect(jsonPath("$.response.sentenceList[0].sentence.text").value(text)); // 반환된 데이터 검증
    }

    @Test
    @DisplayName("batchSave - 유효성 검증 실패 (잘못된 projectSeq)")
    @WithMockUser
    void batchSave_InvalidProjectSeq() throws Exception {
        // given: 잘못된 projectSeq 설정
        Long invalidProjectSeq = 0L;
        Long voiceSeq = 1L;
        Long styleSeq = 1L;
        String text = "Test text";
        TtsAttributeInfo attributeInfo = createAttributeInfo();

        // BatchRequest 생성
        List<TtsSentenceBatchInfo> batchList = IntStream.range(0, 10)
                .mapToObj(repeatCount -> {
                    Long tsSeq = (long) repeatCount;
                    SentenceInfo sentenceInfo = createSentenceInfo(tsSeq, voiceSeq, styleSeq, text, repeatCount, attributeInfo);

                    // TtsSentenceBatchInfo 생성 후 batchList에 추가
                    // batchList에 추가할 때마다 processType을 번갈아가며 설정
                    BatchProcessType processType = repeatCount % 2 == 0 ? BatchProcessType.CREATE : BatchProcessType.UPDATE;
                    return createBatchInfo(processType, sentenceInfo);
                })
                .toList();

        TtsSentenceBatchRequest batchRequest = createBatchRequest(batchList);

        // when: 컨트롤러 호출
        mockMvc.perform(post("/api/project/{projectSeq}/tts/batch", invalidProjectSeq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest))
                        .with(csrf()))
                // then: 유효성 검증 실패 상태 및 메시지 확인
                .andExpect(status().is(ErrorCode.INVALID_INPUT_VALUE.getStatus())) // HTTP 상태 400 확인
                .andExpect(jsonPath("$.response").exists())// 에러 메시지 필드 확인
                .andExpect(jsonPath("$.response.message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage())); // 에러 메시지 확인
    }

    @Test
    @DisplayName("batchSave - 서비스 예외 발생 (존재하지 않는 Project)")
    @WithMockUser
    void batchSave_NonExistentProject() throws Exception {
        // given: 존재하지 않는 projectSeq 설정
        Long projectSeq = 9999L;
        Long voiceSeq = 1L;
        Long styleSeq = 1L;
        String text = "Test text";
        TtsAttributeInfo attributeInfo = createAttributeInfo();

        // BatchRequest 생성
        List<TtsSentenceBatchInfo> batchList = IntStream.range(0, 10)
                .mapToObj(repeatCount -> {
                    Long tsSeq = (long) repeatCount;
                    SentenceInfo sentenceInfo = createSentenceInfo(tsSeq, voiceSeq, styleSeq, text, repeatCount, attributeInfo);

                    // TtsSentenceBatchInfo 생성 후 batchList에 추가
                    // batchList에 추가할 때마다 processType을 번갈아가며 설정
                    BatchProcessType processType = repeatCount % 2 == 0 ? BatchProcessType.CREATE : BatchProcessType.UPDATE;
                    return createBatchInfo(processType, sentenceInfo);
                })
                .toList();

        TtsSentenceBatchRequest batchRequest = createBatchRequest(batchList);

        // 서비스에서 예외 발생하도록 설정
        when(ttsSentenceService.batchSaveSentence(eq(projectSeq), any(TtsSentenceBatchRequest.class)))
                .thenThrow(new EntityNotFoundException());

        // when: 컨트롤러 호출
        mockMvc.perform(post("/api/project/{projectSeq}/tts/batch", projectSeq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest))
                        .with(csrf()))
                // then: 예상 에러 상태 및 메시지 확인
                .andExpect(status().is(ErrorCode.ENTITY_NOT_FOUND.getStatus())) // HTTP 상태 404 확인
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.response.message").value(ErrorCode.ENTITY_NOT_FOUND.getMessage())); // 에러 메시지 확인
    }


    private static TtsAttributeInfo createAttributeInfo() {
        return TtsAttributeInfo.builder().volume(100) // 유효한 volume 설정
                .speed(1.0f) // 유효한 speed 설정
                .stPitch(0)  // 유효한 stPitch 설정
                .emotion("neutral") // 유효한 emotion 설정
                .emotionStrength(100) // 유효한 emotionStrength 설정
                .sampleRate(16000) // 유효한 sampleRate 설정
                .alpha(0) // 유효한 alpha 설정
                .endPitch(0.0f) // 유효한 endPitch 설정
                .audioFormat("wav") // 유효한 audioFormat 설정
                .build(); // 유효한 속성 정보 초기화
    }

    private static TtsSentenceRequest createSentenceRequest(TtsAttributeInfo attributeInfo) {
        return TtsSentenceRequest.builder().voiceSeq(1L) // 유효한 voiceSeq 설정
                .styleSeq(1L) // 유효한 styleSeq 설정
                .text("Valid text") // 유효한 텍스트 설정
                .order(1) // 표시 순서 설정
                .attribute(attributeInfo) // 유효한 속성 정보 설정
                .build(); // 유효한 요청 객체 초기화
    }

    private static Voice createVoice(Long voiceSeq) {
        return Voice.builder().voiceSeq(voiceSeq) // 목소리 ID 설정
                .name("Valid voice") // 목소리 이름 설정
                .build();
    }

    private static Project createProject(Long projectSeq) {
        return Project.builder().proSeq(projectSeq) // 프로젝트 ID 설정
                .proName("Valid project") // 프로젝트 이름 설정
                .build();
    }

    private static Style createStyle(Long styleSeq) {
        return Style.builder().styleSeq(styleSeq) // 스타일 ID 설정
                .name("Valid style") // 스타일 이름 설정
                .build();
    }

    private static TtsSentence createTtsSentence(Voice voice, Project project) {
        return TtsSentence.builder().text("Sample TtsSentence").sortOrder(1).volume(50).speed(1.0f).voice(voice).project(project).build();
    }

    private static TtsSentence createTtsSentence(Project project, Long tsSeq, Voice voice, String text) {
        return TtsSentence.builder().tsSeq(tsSeq).text(text).sortOrder(1).volume(50).speed(1.0f).voice(voice).project(project).build();
    }


    private static TtsSentenceBatchRequest createBatchRequest(List<TtsSentenceBatchInfo> batchList) {
        return TtsSentenceBatchRequest.builder()
                .sentenceList(batchList)
                .build();
    }

    private static SentenceInfo createSentenceInfo(Long tsSeq, Long voiceSeq, Long styleSeq, String text, int order, TtsAttributeInfo attributeInfo) {
        return SentenceInfo.builder()
                .tsSeq(tsSeq)
                .voiceSeq(voiceSeq)
                .styleSeq(styleSeq)
                .text(text)
                .order(order)
                .ttsAttributeInfo(attributeInfo)
                .build();
    }

    private static TtsSentenceBatchInfo createBatchInfo(BatchProcessType batchProcessType, SentenceInfo sentenceInfo) {
        return TtsSentenceBatchInfo.builder()
                .batchProcessType(batchProcessType)
                .sentence(sentenceInfo)
                .build();

    }
}