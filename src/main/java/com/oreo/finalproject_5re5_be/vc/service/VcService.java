package com.oreo.finalproject_5re5_be.vc.service;

import com.oreo.finalproject_5re5_be.global.dto.response.AudioFileInfo;
import com.oreo.finalproject_5re5_be.vc.dto.request.*;
import com.oreo.finalproject_5re5_be.vc.dto.response.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface VcService {
    // SRC 단일, 리스트 저장
    VcUrlResponse srcSave(@Valid @NotNull VcSrcRequest vcSrcRequest, Long proSeq);

    List<VcUrlResponse> srcSave(@Valid @NotNull List<VcSrcRequest> vcSrcRequest, Long proSeq);

    // TRG 단일 저장
    VcUrlResponse trgSave(@Valid @NotNull VcAudioRequest vcAudioRequest);

    // Text 단일, 리스트 저장
    VcTextResponse textSave(@Valid @NotNull VcTextRequest vcTextRequest);

    List<VcTextResponse> textSave(@Valid @NotNull List<VcTextRequest> vcTextRequest);

    // Result 단일, 리스트 저장
    VcUrlResponse resultSave(@Valid @NotNull VcAudioRequest vcAudioRequest);

    List<VcUrlResponse> resultSave(@Valid @NotNull List<VcAudioRequest> vcAudioRequests);

    // VC 응답값 추출
    List<VcResponse> getVcResponse(@Valid @NotNull Long projectSeq);

    // Src, Result 파일 S3 URL 추출
    VcUrlResponse getSrcUrl(@Valid @NotNull Long seq);

    VcUrlResponse getResultUrl(@Valid @NotNull Long seq);

    // VC 테스트 변경
    VcTextResponse updateText(@Valid @NotNull Long seq, @Valid @NotNull String text);

    // 행순서 단일, 여러개 변경
    VcRowResponse updateRowOrder(@Valid @NotNull Long seq, @Valid @NotNull int rowOrder);

    List<VcRowResponse> updateRowOrder(@Valid @NotNull List<VcRowRequest> row);

    // SRC 파일 삭제 단일, 리스ㅡㅌ
    VcActivateResponse deleteSrcFile(@Valid @NotNull Long seq);

    List<VcActivateResponse> deleteSrcFile(@Valid @NotNull List<Long> seqs);

    // 요청값 Builder 로 객체 생성
    List<VcSrcRequest> vcSrcRequestBuilder(
            List<AudioFileInfo> audioFileInfos, List<String> upload, Long proSeq);

    VcAudioRequest audioRequestBuilder(Long proSeq, AudioFileInfo info, String url);

    List<VcAudioRequest> audioRequestBuilder(
            List<VcUrlRequest> vcUrlRequest, List<AudioFileInfo> info, List<String> url);

    // VC Text Request 객체 생성
    List<VcTextRequest> vcTextResponses(List<VcTextRequest> text);

    // SRC URL 추출
    List<VcUrlRequest> vcSrcUrlRequests(List<Long> srcSeq);

    // Src, TRG S3에서 파일 다운로드
    MultipartFile getTrgFile(Long trgSeq) throws IOException;

    List<MultipartFile> getSrcFile(List<Long> srcSeq);

    // 회원의 정보인지 체크하는 기능
    boolean srcCheck(Long memberSeq, Long srcSeq);

    boolean srcCheck(Long memberSeq, List<Long> srcSeq);

    boolean resCheck(Long memberSeq, Long resSeq);

    boolean textCheck(Long memberSeq, Long textSeq);
}
