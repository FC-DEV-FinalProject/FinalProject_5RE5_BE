package com.oreo.finalproject_5re5_be.concat.controller;

import com.oreo.finalproject_5re5_be.concat.dto.ConcatResponseDto;
import com.oreo.finalproject_5re5_be.concat.dto.request.OriginAudioRequest;
import com.oreo.finalproject_5re5_be.concat.dto.request.SelectedConcatRowRequest;
import com.oreo.finalproject_5re5_be.concat.dto.response.ConcatUrlResponse;
import com.oreo.finalproject_5re5_be.concat.entity.AudioFile;
import com.oreo.finalproject_5re5_be.concat.repository.BgmFileRepository;
import com.oreo.finalproject_5re5_be.concat.service.AudioFileService;
import com.oreo.finalproject_5re5_be.concat.service.AudioStreamService;
import com.oreo.finalproject_5re5_be.concat.service.ConcatResultService;
import com.oreo.finalproject_5re5_be.concat.service.MaterialAudioService;
import com.oreo.finalproject_5re5_be.concat.service.bgm.BgmProcessor;
import com.oreo.finalproject_5re5_be.concat.service.concatenator.AudioProperties;
import com.oreo.finalproject_5re5_be.concat.service.concatenator.IntervalConcatenator;
import com.oreo.finalproject_5re5_be.concat.service.concatenator.StereoIntervalConcatenator;
import com.oreo.finalproject_5re5_be.global.component.S3Service;
//import com.oreo.finalproject_5re5_be.global.component.SqsService;
import com.oreo.finalproject_5re5_be.global.component.audio.AudioFormats;
import com.oreo.finalproject_5re5_be.global.component.audio.AudioResample;
import com.oreo.finalproject_5re5_be.global.dto.response.ResponseDto;
import com.oreo.finalproject_5re5_be.member.dto.CustomUserDetails;
import com.oreo.finalproject_5re5_be.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


@Tag(name = "Concat", description = "Concat 관련 API")
@RestController
@Log4j2
@RequestMapping("/api/concat")
@RequiredArgsConstructor
public class ConcatWithBgmController {

    private final S3Service s3Service;
    private final MaterialAudioService materialAudioService;
    private final ConcatResultService concatResultService;
//    private final SqsService sqsService;
    private final AudioFileService audioFileService;
    private final AudioStreamService audioStreamService; // 추가된 서비스
    private final AudioResample audioResample = new AudioResample(); // 리샘플링 유틸. Bean이 아니라 new로 생성
    private final AudioFormat defaultAudioFormat = AudioFormats.STEREO_FORMAT_SR441_B32; // 기본 포맷
    private final ProjectService projectService;
    private final BgmFileRepository bgmFileRepository;

    @Operation(
            summary = "Row 오디오와 BGM 파일 병합",
            description = "선택된 Row 오디오 파일과 BGM 파일을 병합하여 S3에 업로드합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 병합된 오디오 URL을 반환합니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ConcatResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "병합 작업 중 오류 발생",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDto.class)
                            )
                    )
            }
    )
    @PostMapping("/execute-with-bgm")
    public ResponseEntity<ResponseDto<ConcatResponseDto>> executeConcatWithBgm(
            @Parameter(description = "결과물이 나온 concatTab", required = true) @RequestParam Long concatTabSeq,
            @Parameter(description = "bgm으로 쓸 오디오파일의 url", required = true) @RequestParam String bgmFileUrl,
            @Parameter(description = "저장할 결과파일 이름", required = true) @RequestParam String concatResultFileName,
//            @RequestBody BgmFunctionRequestDto bgmFunctionRequestDto,
            @RequestBody SelectedConcatRowRequest selectedRows,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        projectService.projectCheck(customUserDetails.getMember().getSeq(), concatTabSeq);

        try {

//            //SQS로 메세지 보내기. 각각 messageBody와 messageAttribute로 들어갈 내용
//            Message message = sqsService.sendMessage(bgmFunctionRequestDto, MessageType.CONCAT_BGM_MAKE);

            log.info("Request Parameters: concatTabSeq={}, bgmFileUrl={}, concatResultFileName={}",
                    concatTabSeq, bgmFileUrl, concatResultFileName);
            log.info("Selected Rows: {}", selectedRows);

            IntervalConcatenator intervalConcatenator = new StereoIntervalConcatenator(defaultAudioFormat);

            log.info("IntervalConcatenator 초기화 성공");

            // Concat 작업: 1. Row 오디오 파일 로드 및 무음 처리
            List<AudioProperties> audioProperties = audioStreamService.loadAudioFiles(selectedRows);

            log.info("AudioProperties 로드 성공: {}", audioProperties);

            // 2. 병합된 오디오 생성
            ByteArrayOutputStream concatenatedAudioBuffer = intervalConcatenator.intervalConcatenate(audioProperties, selectedRows.getInitialSilence());

            log.info("병합된 오디오 버퍼 생성 성공");

            // Bgm 작업: 1. 병합된 오디오를 AudioInputStream으로 변환
            AudioInputStream concatenatedAudioStream = audioStreamService.createAudioInputStream(concatenatedAudioBuffer, defaultAudioFormat);

            // 2. BGM 스트림 로드 및 버퍼링
            AudioInputStream bufferedBgmStream = s3Service.loadAsBufferedStream(bgmFileUrl);

            log.info("BGM 파일 로드 성공");

            // 3. BGM 길이 조정
            long targetFrames = audioStreamService.getValidFrameLength(concatenatedAudioStream);
            long bgmFrames = audioStreamService.getValidFrameLength(bufferedBgmStream);
            bufferedBgmStream = BgmProcessor.adjustBgmLength(bufferedBgmStream, targetFrames, bgmFrames);

            log.info("BGM 길이 조정 성공");

            // 4. 믹싱
            AudioInputStream mixedAudioStream = BgmProcessor.mixAudio(concatenatedAudioStream, bufferedBgmStream);

            log.info("오디오 믹싱 성공");

            // 결과파일 S3 업로드
            String resultAudioUrl = s3Service.uploadAudioStream(mixedAudioStream, "concat/result", concatResultFileName);

            log.info("S3 업로드 성공: {}", resultAudioUrl);

//            String audioUrl = "";
//            String concatResultFileName = "";
//            AudioInputStream mixedAudioStream = null;
//            SelectedConcatRowRequest selectedRows = null;

            // DB ConcatResult테이블에 결과 저장
            ConcatUrlResponse concatResultResponse = concatResultService.saveConcatResult(concatTabSeq, resultAudioUrl, concatResultFileName, mixedAudioStream);

            log.info("ConcatResult 저장 성공: {}", concatResultResponse);

            // Material 데이터 저장 (재료 파일, 결과파일 저장되어 있는 상태로 교차테이블에 데이터 저장)
            materialAudioService.saveMaterialsForSelectedRows(selectedRows, concatResultResponse);

            log.info("Material 데이터 저장 성공");

            // Concat 재료 파일 정보 생성
            List<OriginAudioRequest> concatRowFiles = selectedRows.getRows().stream()
                    .map(row -> audioFileService.getAudioFileByUrl(row.getAudioUrl()))
                    .map(this::convertToOriginAudioRequest)
                    .toList();

            // BGM 파일 정보 생성
            AudioFile bgmAudioFile = audioFileService.getAudioFileByUrl(bgmFileUrl); // URL로 AudioFile 조회
            OriginAudioRequest bgmFile = OriginAudioRequest.builder()
                    .seq(bgmAudioFile.getAudioFileSeq()) // AudioFile의 식별 ID
                    .audioUrl(bgmAudioFile.getAudioUrl()) // BGM 파일 URL
                    .extension(bgmAudioFile.getExtension()) // BGM 파일 확장자
                    .fileSize(bgmAudioFile.getFileSize()) // 파일 크기
                    .fileLength(bgmAudioFile.getFileLength()) // 파일 길이
                    .fileName(bgmAudioFile.getFileName()) // 파일 이름
                    .build();

            // 응답 생성
            ConcatResponseDto responseDto = ConcatResponseDto.builder()
                    .audioUrl(resultAudioUrl)
                    .bgmFile(bgmFile)
                    .concatRowFiles(concatRowFiles)
                    .build();

            return new ResponseDto<>(HttpStatus.OK.value(), responseDto).toResponseEntity();
        } catch (Exception e) {
            log.error("ConcatWithBgmController execute 실패", e);
            // 실패 응답 생성
            return createErrorResponse();
        }
    }


    private OriginAudioRequest convertToOriginAudioRequest(AudioFile audioFile) {
        return OriginAudioRequest.builder()
                .seq(audioFile.getAudioFileSeq())
                .audioUrl(audioFile.getAudioUrl())
                .extension(audioFile.getExtension())
                .fileSize(audioFile.getFileSize())
                .fileLength(audioFile.getFileLength())
                .fileName(audioFile.getFileName())
                .build();
    }

    private ResponseEntity<ResponseDto<ConcatResponseDto>> createErrorResponse() {
        return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ConcatResponseDto.builder()
                        .audioUrl(null) // 결과 파일 URL 없음
                        .bgmFile(null) // BGM 파일 정보 없음
                        .concatRowFiles(new ArrayList<>()) // ConcatRow 파일 정보 없음
                        .build()
        ).toResponseEntity();
    }
}