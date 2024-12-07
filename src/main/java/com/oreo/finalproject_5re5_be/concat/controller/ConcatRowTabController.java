package com.oreo.finalproject_5re5_be.concat.controller;

import com.oreo.finalproject_5re5_be.concat.dto.RowAudioFileDto;
import com.oreo.finalproject_5re5_be.concat.dto.request.*;
import com.oreo.finalproject_5re5_be.concat.dto.response.ConcatTabResponseDto;
import com.oreo.finalproject_5re5_be.concat.dto.response.TabRowResponseDto;
import com.oreo.finalproject_5re5_be.concat.entity.BgmFile;
import com.oreo.finalproject_5re5_be.concat.service.*;
import com.oreo.finalproject_5re5_be.global.dto.response.ResponseDto;
import com.oreo.finalproject_5re5_be.member.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Concat", description = "Concat 관련 API")

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v2/concat")
public class ConcatRowTabController {
    private final ConcatRowService concatRowService;
    private final ConcatTabService concatTabService;
    private final AudioFileService audioFileService;
    private final BgmFileService bgmFileservice;
    private final ConcatRowTabService concatRowTabService;

    //탭 로우 조회
    //탭 로우 저장

    @Operation(
            summary = "ConcatRow, ConcatTab을 저장합니다.",
            description = ""
    )
    @PostMapping("save")
    public ResponseEntity<ResponseDto<Boolean>> saveRowAndTab(
            @RequestBody TabRowUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            boolean result = concatRowTabService.saveTabAndRows(dto, customUserDetails.getMember().getSeq());
            return new ResponseDto<>(HttpStatus.OK.value(), result).toResponseEntity();
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false).toResponseEntity();
        }
    }


    @Operation(
            summary = "ConcatRow, ConcatTab을 조회합니다.",
            description = ""
    )
    @GetMapping("read")
    public ResponseEntity<ResponseDto<TabRowResponseDto>> readRowAndTab(
            @RequestParam Long projectSeq,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        // Tab setting
        ConcatTabResponseDto concatTabResponseDto
                = concatTabService.readConcatTab(projectSeq, customUserDetails.getMember().getSeq());

        // Tabseq로 bgmFile 리스트 불러오기
        List<BgmFile> bgmFiles = bgmFileservice.getBgmFilesByTabSeq(concatTabResponseDto.getTabId());

        // BgmFile 리스트를 OriginAudioRequest 형태로 변환
        List<OriginAudioRequest> bgmFileList = bgmFiles.stream()
                .map(bgm -> OriginAudioRequest.builder()
                        .seq(bgm.getBgmFileSeq())
                        .audioUrl(bgm.getAudioUrl())
                        .fileName(bgm.getFileName())
                        .fileSize(bgm.getFileSize())
                        .fileLength(bgm.getFileLength())
                        .extension(bgm.getExtension())
                        .build())
                .toList();

        // ConcatTabResponseDto에 bgmFileList 추가
        concatTabResponseDto = ConcatTabResponseDto.builder()
                .tabId(concatTabResponseDto.getTabId())
                .frontSilence(concatTabResponseDto.getFrontSilence())
                .status(concatTabResponseDto.getStatus())
                .bgmFileList(bgmFileList) // BgmFile 리스트 추가
                .build();

        // Row setting
        // ConcatRow와 AudioFile 리스트 조회
        List<RowAudioFileDto> audioFiles = audioFileService.getAudioFilesByProjectAndStatusTrue(projectSeq);

        List<ConcatRowRequest> concatRowRequests = audioFiles.stream().map(x -> ConcatRowRequest.builder()
                .originAudioRequest(OriginAudioRequest.builder()
                        .seq(x.getAudioFileSeq())
                        .audioUrl(x.getAudioUrl())
                        .fileName(x.getFileName())
                        .fileSize(x.getFileSize())
                        .fileLength(x.getFileLength())
                        .extension(x.getExtension()).build()
                )
                .rowText(x.getConcatRow().getRowText())
                .rowIndex(x.getConcatRow().getRowIndex())
                .rowSilence(x.getConcatRow().getSilence())
                .status(x.getConcatRow().getStatus())
                .seq(x.getConcatRow().getConcatRowSequence())
                .build()).toList();


        return new ResponseDto<>(HttpStatus.OK.value(), new TabRowResponseDto(concatTabResponseDto,
                new ConcatRowSaveRequestDto(concatTabResponseDto.getTabId(), concatRowRequests)))
                .toResponseEntity();
    }
}

