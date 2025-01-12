package com.oreo.finalproject_5re5_be.vc.service;

import com.oreo.finalproject_5re5_be.global.component.S3Service;
import com.oreo.finalproject_5re5_be.global.component.audio.AudioFileTypeConverter;
import com.oreo.finalproject_5re5_be.global.dto.response.AudioFileInfo;
import com.oreo.finalproject_5re5_be.project.entity.Project;
import com.oreo.finalproject_5re5_be.project.repository.ProjectRepository;
import com.oreo.finalproject_5re5_be.project.service.ProjectService;
import com.oreo.finalproject_5re5_be.vc.dto.request.VcAudioRequest;
import com.oreo.finalproject_5re5_be.vc.dto.request.VcResultsRequest;
import com.oreo.finalproject_5re5_be.vc.dto.request.VcRowRequest;
import com.oreo.finalproject_5re5_be.vc.dto.request.VcSrcRequest;
import com.oreo.finalproject_5re5_be.vc.dto.request.VcSrcsRequest;
import com.oreo.finalproject_5re5_be.vc.dto.request.VcTextRequest;
import com.oreo.finalproject_5re5_be.vc.dto.request.VcUrlRequest;
import com.oreo.finalproject_5re5_be.vc.dto.response.VcActivateResponse;
import com.oreo.finalproject_5re5_be.vc.dto.response.VcResponse;
import com.oreo.finalproject_5re5_be.vc.dto.response.VcRowResponse;
import com.oreo.finalproject_5re5_be.vc.dto.response.VcTextResponse;
import com.oreo.finalproject_5re5_be.vc.dto.response.VcUrlResponse;
import com.oreo.finalproject_5re5_be.vc.entity.Vc;
import com.oreo.finalproject_5re5_be.vc.entity.VcResultFile;
import com.oreo.finalproject_5re5_be.vc.entity.VcSrcFile;
import com.oreo.finalproject_5re5_be.vc.entity.VcText;
import com.oreo.finalproject_5re5_be.vc.entity.VcTrgFile;
import com.oreo.finalproject_5re5_be.vc.exception.VcNotFoundProjectException;
import com.oreo.finalproject_5re5_be.vc.exception.VcNotFoundResultException;
import com.oreo.finalproject_5re5_be.vc.exception.VcNotFoundSrcException;
import com.oreo.finalproject_5re5_be.vc.exception.VcNotFoundTextException;
import com.oreo.finalproject_5re5_be.vc.exception.VcNotFoundTrgException;
import com.oreo.finalproject_5re5_be.vc.exception.VcNotMemberException;
import com.oreo.finalproject_5re5_be.vc.repository.VcRepository;
import com.oreo.finalproject_5re5_be.vc.repository.VcResultFileRepository;
import com.oreo.finalproject_5re5_be.vc.repository.VcSrcFileRepository;
import com.oreo.finalproject_5re5_be.vc.repository.VcTextRepository;
import com.oreo.finalproject_5re5_be.vc.repository.VcTrgFileRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Validated
public class VcServiceImpl implements VcService {
    private VcRepository vcRepository;
    private VcSrcFileRepository vcSrcFileRepository;
    private VcTrgFileRepository vcTrgFileRepository;
    private VcResultFileRepository vcResultFileRepository;
    private VcTextRepository vcTextRepository;
    private ProjectRepository projectRepository;
    private S3Service s3Service;
    private ProjectService projectService;

    @Autowired
    public VcServiceImpl(
            VcRepository vcRepository,
            VcSrcFileRepository vcSrcFileRepository,
            VcTrgFileRepository vcTrgFileRepository,
            VcResultFileRepository vcResultFileRepository,
            VcTextRepository vcTextRepository,
            ProjectRepository projectRepository,
            S3Service s3Service,
            ProjectService projectService) {
        this.vcRepository = vcRepository;
        this.vcSrcFileRepository = vcSrcFileRepository;
        this.vcTrgFileRepository = vcTrgFileRepository;
        this.vcResultFileRepository = vcResultFileRepository;
        this.vcTextRepository = vcTextRepository;
        this.projectRepository = projectRepository;
        this.s3Service = s3Service;
        this.projectService = projectService;
    }

    private static final Logger log = LoggerFactory.getLogger(VcServiceImpl.class);

    /**
     * Vc SRC 파일 저장
     *
     * @param vcSrcRequest
     * @return VcUrlResponse
     */
    @Override
    @Transactional
    public VcUrlResponse saveSrc(@Valid @NotNull VcSrcRequest vcSrcRequest, Long proSeq) {
        // VC 찾기
        Vc vc = findProject(proSeq);
        if (vc == null) {
            throw new VcNotFoundProjectException("VC not found ProjectSeq : " + proSeq);
        }
        // vcSrcFileRepository를 통해 rowOrder 값 결정
        int rowOrder =
                Optional.ofNullable(vcSrcFileRepository.countByVc_ProjectSeq(vcSrcRequest.getSeq()))
                        .map(count -> count + 1)
                        .orElse(1);
        // SRC를 찾아서 있다면 1로 없다면 사이즈만큼에서 +1해서 저장
        // 프로젝트 조회한 값과 입력한 값 저장을 하기 위한 SRC 객체 생
        try {
            VcSrcFile save =
                    vcSrcFileRepository.save(
                            VcSrcFile.create(
                                    vc,
                                    rowOrder,
                                    vcSrcRequest.getName(),
                                    vcSrcRequest.getFileUrl(),
                                    vcSrcRequest.getLength(),
                                    vcSrcRequest.getSize(),
                                    vcSrcRequest.getExtension())); // SRC 객체 저장

            return VcUrlResponse.of(save.getSrcSeq(), save.getFileUrl());
        } catch (Exception e) {
            log.error("Error Save SRC 저장중 오류: {} ", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public List<VcUrlResponse> saveSrc(
            @Valid @NotNull List<VcSrcRequest> vcSrcRequests, Long proSeq) {
        // vcSrcRequests를 srcSave로 저장시키고 리턴
        try {
            return vcSrcRequests.stream()
                    .map(vcSrcRequest -> saveSrc(vcSrcRequest, proSeq))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error Save SRC 들 저장중 오류  : {} ", e.getMessage(), e);
            throw new RuntimeException("SRC 파일 저장 중 오류가 발생하였습니다.");
        }
    }

    /**
     * VC Trg 파일 저장
     *
     * @param vcAudioRequest
     * @return VcUrlResponse
     */
    @Override
    @Transactional
    public VcUrlResponse saveTrg(@Valid @NotNull VcAudioRequest vcAudioRequest) {
        Long proSeq = vcAudioRequest.getSeq();
        // 프로젝트 조회, 객체 생성후 저장
        Vc vc = findProject(proSeq);
        if (vc == null) {
            throw new VcNotFoundProjectException("VC Not Found projectSeq : " + proSeq);
        }

        try {
            // 프로젝트 조회한 값과 입력한 값을 저장하기 위한 TRG 객체 생성
            VcTrgFile save =
                    vcTrgFileRepository.save(
                            VcTrgFile.create(
                                    vc,
                                    vcAudioRequest.getName(),
                                    vcAudioRequest.getFileUrl(),
                                    vcAudioRequest.getLength(),
                                    vcAudioRequest.getSize(),
                                    vcAudioRequest.getExtension())); // TRG 객체 저장
            return VcUrlResponse.of(save.getTrgSeq(), save.getFileUrl());
        } catch (Exception e) {
            log.error("Error Save TRG  저장중 오류  : {} ", e.getMessage(), e);
            throw new RuntimeException("TRG 파일 저장 중 오류가 발생하였습니다.");
        }
    }

    /**
     * Vc Result 파일 저장 (vc 생성 파일)
     *
     * @param vcAudioRequest
     * @return VcUrlResponse
     */
    @Override
    @Transactional
    public VcUrlResponse saveResult(@Valid @NotNull VcAudioRequest vcAudioRequest) {
        // SRCFile 조회
        VcSrcFile srcFile = findVcSrcFile(vcAudioRequest.getSeq());
        if (srcFile == null) {
            throw new VcNotFoundSrcException("Src not found seq : " + vcAudioRequest.getSeq());
        }

        try {
            // 프로젝트 조회한 값과 SRC 조회한 값, 입력한 값을 저장하기 위한 ResultFile 객체 생성
            VcResultFile save =
                    vcResultFileRepository.save(
                            VcResultFile.create(
                                    srcFile,
                                    vcAudioRequest.getName(),
                                    vcAudioRequest.getFileUrl(),
                                    vcAudioRequest.getLength(),
                                    vcAudioRequest.getSize(),
                                    vcAudioRequest.getExtension())); // result 객체 저장
            return VcUrlResponse.of(save.getResSeq(), save.getFileUrl());
        } catch (Exception e) {
            log.error("Error Save Result  저장중 오류  : {} ", e.getMessage(), e);
            throw new RuntimeException("Result 파일 저장 중 오류가 발생하였습니다.");
        }
    }

    @Override
    @Transactional
    public List<VcUrlResponse> saveResult(@Valid @NotNull List<VcAudioRequest> vcAudioRequests) {
        // vcAudioRequests를 위에 resultSave를 적용
        try {
            return vcAudioRequests.stream().map(this::saveResult).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error Save Result들 저장중 오류  : {} ", e.getMessage(), e);
            throw new RuntimeException("Result 파일들 저장 중 오류가 발생하였습니다.");
        }
    }

    /**
     * Text 저장 기능
     *
     * @param vcTextRequest
     * @return VcTextResponse
     */
    @Override
    @Transactional
    public VcTextResponse saveText(@Valid @NotNull VcTextRequest vcTextRequest) {
        // SRC 조회
        VcSrcFile srcFile = findVcSrcFile(vcTextRequest.getSeq());
        if (srcFile == null) {
            throw new VcNotFoundSrcException("Src not found seq : " + vcTextRequest.getSeq());
        }

        try {
            // SRC 조회한 값과 프로젝트 조회한 값, 입력 값 저장하기 위한 TextFile 객체 생성
            VcText save =
                    vcTextRepository.save(
                            VcText.create(
                                    srcFile,
                                    vcTextRequest.getText(),
                                    String.valueOf(vcTextRequest.getText().length()))); // Text 객체 저장
            return VcTextResponse.of(save.getVtSeq(), save.getComment());
        } catch (Exception e) {
            log.error("Error Save Text 저장중 오류  : {} ", e.getMessage(), e);
            throw new RuntimeException("Text 저장 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 리스트로 텍스트 저장
     *
     * @param vcTextRequests
     * @return
     */
    @Override
    @Transactional
    public List<VcTextResponse> saveText(@Valid @NotNull List<VcTextRequest> vcTextRequests) {
        // vcTextRequest를 가지고 위에 textSave에 적용
        try {
            return vcTextRequests.stream().map(this::saveText).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error Save Text 저장중 오류  : {} ", e.getMessage(), e);
            throw new RuntimeException("Text 저장 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 프로젝트 VC 탭 조회 기능
     *
     * @param projectSeq
     * @return List<VcResponse>
     */
    @Override
    @Transactional(readOnly = true)
    public List<VcResponse> getVcResponse(@Valid @NotNull Long projectSeq) {
        // 프로젝트 seq 조회한 값
        List<VcSrcFile> vcSrcFileList = vcSrcFileRepository.findByVcProjectSeq(projectSeq);

        // 데이터가 많을수 있으므로 병렬 처리로 변경
        return vcSrcFileList.parallelStream()
                .sorted(Comparator.comparing(VcSrcFile::getRowOrder)) //
                .map(
                        vcSrcFile -> {
                            // src 요청 값 입력
                            VcSrcsRequest srcAudio =
                                    VcSrcsRequest.builder()
                                            .seq(vcSrcFile.getSrcSeq())
                                            .rowOrder(vcSrcFile.getRowOrder())
                                            .name(vcSrcFile.getFileName())
                                            .fileUrl(vcSrcFile.getFileUrl())
                                            .build();
                            // SRC 로 제일 최근에 저장한 Result 조회, 값이 없을 경우 null 처리
                            VcResultsRequest resultAudio = createVcSrcFile(vcSrcFile);
                            // 제일 최근에 저장한 텍스트 조회, 값이 없을 경우 null 처리
                            VcTextRequest text = createText(vcSrcFile);
                            // VcResponse 객체 생성 후 반환
                            return new VcResponse(vcSrcFile.getActivate(), srcAudio, resultAudio, text);
                        })
                .collect(Collectors.toList());
    }

    /**
     * VC SRC 파일 조회 기능
     *
     * @param seq
     * @return VcUrlResponse
     */
    @Override
    @Transactional(readOnly = true)
    public VcUrlResponse getSrcUrl(@Valid @NotNull Long seq) {
        // SRC seq 로 SRC 값 조회
        VcSrcFile srcFile = findVcSrcFile(seq);
        log.info("[vcService] getSrcFile srcFile 확인 : {} ", srcFile);
        // S3 SRC URL 값 출력
        return VcUrlResponse.of(srcFile.getSrcSeq(), srcFile.getFileUrl());
    }

    /**
     * VC Result 파일 조회 기능
     *
     * @param seq
     * @return VcUrlResponse
     */
    @Override
    @Transactional(readOnly = true)
    public VcUrlResponse getResultUrl(@Valid @NotNull Long seq) {
        // TRG seq 로 TRG 값 조회
        VcResultFile resultFile = findVcResult(seq);
        log.info("[vcService] getResultFile resultFile 확인 : {} ", resultFile);
        // S3 TRG URL 값 출력
        return VcUrlResponse.of(resultFile.getResSeq(), resultFile.getFileUrl());
    }

    /**
     * 텍스트 수정 기능
     *
     * @param seq
     * @param text
     */
    @Override
    @Transactional
    public VcTextResponse updateText(@Valid @NotNull Long seq, @Valid @NotNull String text) {
        // Text seq 로 Text 값 조회 검증
        VcText vcText = findVcText(seq);
        log.info("[vcService] updateText vcText 확인 : {} ", vcText);

        // 변경할 값과 seq 값 변경 객체 생성
        VcText save =
                vcTextRepository.save(
                        vcText.toBuilder().vtSeq(vcText.getVtSeq()).comment(text).build()); // 텍스트 값 변경
        log.info("[vcService] updateText save 확인 : {} ", save);
        return VcTextResponse.of(save.getVtSeq(), save.getComment());
    }

    /**
     * 행 수정 기능
     *
     * @param seq
     * @param rowOrder
     */
    @Override
    @Transactional
    public VcRowResponse updateRowOrder(@Valid @NotNull Long seq, @Valid @NotNull int rowOrder) {
        // 변경할 행순서 값과 SRC seq 값 변경 객체 생성
        VcSrcFile updateSrcFile = findVcSrcFile(seq).toBuilder().rowOrder(rowOrder).build();
        log.info("[vcService] updateRowOrder updateSrcFile 확인 : {} ", updateSrcFile);

        VcSrcFile save = vcSrcFileRepository.save(updateSrcFile); // 행순서 변경
        log.info("[vcService] updateRowOrder save 확인 : {} ", save);
        return VcRowResponse.builder().seq(save.getSrcSeq()).rowOrder(save.getRowOrder()).build();
    }

    /**
     * 행 순서 변경 리스트로
     *
     * @param row
     * @return
     */
    @Override
    @Transactional
    public List<VcRowResponse> updateRowOrder(List<VcRowRequest> row) {
        // row를 가지고 위에 updateRowOrder를 적용
        return row.stream()
                .map(vcRowRequest -> updateRowOrder(vcRowRequest.getSeq(), vcRowRequest.getRowOrder()))
                .collect(Collectors.toList());
    }

    /**
     * SRC 행 삭제 하는 기능(수정)
     *
     * @param seq
     */
    @Override
    @Transactional
    public VcActivateResponse deleteSrcFile(@Valid @NotNull Long seq) {
        // 활성화 상태 N로 변경
        VcSrcFile deleteSrcFile = findVcSrcFile(seq).toBuilder().activate('N').build();
        log.info("[vcService] deleteSrcFile deleteSrcFile 확인 : {} ", deleteSrcFile);
        // 활성화상태 변경
        VcSrcFile save = vcSrcFileRepository.save(deleteSrcFile);
        log.info("[vcService] deleteSrcFile save 확인 : {} ", save);
        return VcActivateResponse.builder().seq(save.getSrcSeq()).activate(save.getActivate()).build();
    }

    /**
     * 삭제 리스트로 변경
     *
     * @param seqs
     * @return
     */
    @Override
    @Transactional
    public List<VcActivateResponse> deleteSrcFile(@Valid @NotNull List<Long> seqs) {
        // seqs를 가지고 위에 deleteSrcFile를 적용
        return seqs.stream().map(this::deleteSrcFile).collect(Collectors.toList());
    }

    /**
     * VCSrcRequest 객체 생성
     *
     * @param audioFileInfos
     * @param upload
     * @param proSeq
     * @return
     */
    @Override
    public List<VcSrcRequest> requestBuilderVcSrc(
            List<AudioFileInfo> audioFileInfos, List<String> upload, Long proSeq) {
        // audioFileInfos 개수 만큼 VcSrcRequest audioinfo, url, proSeq 적용해서 builder 패턴으로 리턴
        return IntStream.range(0, audioFileInfos.size())
                .mapToObj(
                        i ->
                                VcSrcRequest.builder()
                                        .seq(proSeq)
                                        .rowOrder(1)
                                        .name(audioFileInfos.get(i).getName())
                                        .fileUrl(upload.get(i))
                                        .length(audioFileInfos.get(i).getLength())
                                        .size(audioFileInfos.get(i).getSize())
                                        .extension(audioFileInfos.get(i).getExtension())
                                        .build())
                .collect(Collectors.toList());
    }

    /**
     * VcAudioRequest 객체 생성
     *
     * @param proSeq
     * @param info
     * @param url
     * @return
     */
    @Override
    public VcAudioRequest requestBuilderAudio(Long proSeq, AudioFileInfo info, String url) {
        // static 정적 메서드로 리턴
        return VcAudioRequest.of(
                proSeq, info.getName(), url, info.getLength(), info.getSize(), info.getExtension());
    }

    /**
     * VCAudioRequest 객체 생성
     *
     * @param vcUrlRequest
     * @param info
     * @param url
     * @return
     */
    @Override
    public List<VcAudioRequest> requestBuilderAudio(
            List<VcUrlRequest> vcUrlRequest, List<AudioFileInfo> info, List<String> url) {
        // vcSrcUrlRequest 사이즈 만큼 반복 하여 생성자 리턴
        return IntStream.range(0, vcUrlRequest.size())
                .mapToObj(
                        i ->
                                VcAudioRequest.of(
                                        vcUrlRequest.get(i).getSeq(),
                                        info.get(i).getName(),
                                        url.get(i),
                                        info.get(i).getLength(),
                                        info.get(i).getSize(),
                                        info.get(i).getExtension()))
                .collect(Collectors.toList());
    }

    /**
     * VcTextRequest 객체 생성
     *
     * @param text
     * @return
     */
    @Override
    public List<VcTextRequest> responsesVcText(List<VcTextRequest> text) {
        // srcSeq 개수 가지고 생성자 리턴
        return IntStream.range(0, text.size())
                .mapToObj(i -> VcTextRequest.of(text.get(i).getSeq(), text.get(i).getText()))
                .collect(Collectors.toList());
    }

    /**
     * Srcseq로 url을 찾는 메서드
     *
     * @param srcSeq
     * @return List<VcUrlRequest>
     */
    @Override
    @Transactional(readOnly = true)
    public List<VcUrlRequest> requestsVcSrcUrl(List<Long> srcSeq) {
        // srcSeq를 가지고 vcSrcFile 찾고 없으면 예외던지고 있다면 생성자로 리턴
        return srcSeq.stream()
                .map(
                        seq -> {
                            VcSrcFile vcSrcFile =
                                    vcSrcFileRepository
                                            .findById(seq)
                                            .orElseThrow(() -> new IllegalArgumentException("vcSrcFile not found"));
                            return VcUrlRequest.of(vcSrcFile.getSrcSeq(), vcSrcFile.getFileUrl());
                        })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MultipartFile getTrgFile(Long trgSeq) throws IOException {
        try {
            MultipartFile multipartFile =
                    AudioFileTypeConverter.convertFileToMultipartFile(
                            AWSS3Service.downloadFile(
                                    "vc/trg/" + extractFileName(requestVcTrgUrl(trgSeq).getUrl())));
            log.info("[vcService] getTrgFile multipartFile 확인 : {} ", multipartFile);
            return multipartFile;
        } catch (Exception e) {
            throw new IOException("TRG File 오류", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MultipartFile> getSrcFile(List<Long> srcSeq) {
        List<VcUrlRequest> vcSrcUrlRequests = requestsVcSrcUrl(srcSeq);
        List<MultipartFile> collect =
                vcSrcUrlRequests.stream()
                        .map(
                                vcSrcUrlRequest -> {
                                    String srcFileName = extractFileName(vcSrcUrlRequest.getUrl());
                                    File srcFile = null;
                                    try {
                                        srcFile = AWSS3Service.downloadFile("vc/src/" + srcFileName);
                                        return AudioFileTypeConverter.convertFileToMultipartFile(srcFile);
                                    } catch (IOException e) {
                                        throw new RuntimeException("SRC File 오류 : ", e);
                                    }
                                })
                        .collect(Collectors.toList());
        return collect;
    }

    /**
     * SrcSeq 로 회원 확인 (단일)
     *
     * @param memberSeq
     * @param srcSeq
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public boolean checkSrc(Long memberSeq, Long srcSeq) {
        // 행 찾기
        VcSrcFile vcSrcFile = findVcSrcFile(srcSeq);
        // 행에서 프로젝트 찾기
        Project proSeq = vcSrcFile.getVc().getProSeq();
        // 프로젝트에서 회원정보로 회원확인
        if (projectService.projectCheck(memberSeq, proSeq.getProSeq())) {
            return true;
        }
        throw new VcNotMemberException();
    }

    /**
     * SrcSeq 로 회원 확인 (여러개)
     *
     * @param memberSeq
     * @param srcSeq
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public boolean checkSrc(Long memberSeq, List<Long> srcSeq) {
        // 위에 단일을 반복
        for (Long src : srcSeq) {
            checkSrc(memberSeq, src);
        }
        return true;
    }

    /**
     * resultSeq를 가지고 회원확인
     *
     * @param memberSeq
     * @param resSeq
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public boolean checkRes(Long memberSeq, Long resSeq) {
        // 결과물찾기
        VcResultFile resultFile = findVcResult(resSeq);
        // 결과물로 src찾기
        VcSrcFile srcSeq = resultFile.getSrcSeq();
        // 위에 src 단일로 회원 비교
        if (checkSrc(memberSeq, srcSeq.getSrcSeq())) {
            return true;
        }
        throw new VcNotMemberException();
    }

    /**
     * textSeq를 가지고 회원확인
     *
     * @param memberSeq
     * @param textSeq
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public boolean checkText(Long memberSeq, Long textSeq) {
        // Text 찾기
        VcText vcText = findVcText(textSeq);
        // SRC 찾기
        VcSrcFile srcSeq = vcText.getSrcSeq();
        // 위에 단일메서드 호출
        if (checkSrc(memberSeq, srcSeq.getSrcSeq())) {
            return true;
        }
        throw new VcNotMemberException();
    }

    // VcSrcFile 찾는 메서드
    private VcSrcFile findVcSrcFile(Long seq) {
        return vcSrcFileRepository
                .findById(seq)
                .orElseThrow(() -> new VcNotFoundSrcException("Src file not found"));
    }

    // Project 찾고 vc생성 저장하는 메서드
    private Vc findProject(Long seq) {
        return projectRepository
                .findById(seq)
                .map(
                        project ->
                                vcRepository
                                        .findById(project.getProSeq()) // VC를 찾고 존재하지 않으면 vcSave 실행
                                        .orElseGet(() -> vcRepository.save(Vc.builder().proSeq(project).build())))
                .orElseThrow(() -> new VcNotFoundProjectException("Project not found"));
    }

    // VcResultFile 찾는 메서드
    private VcResultFile findVcResult(Long seq) {
        return vcResultFileRepository
                .findById(seq)
                .orElseThrow(() -> new VcNotFoundResultException("ResultFile not found"));
    }

    // VcText 찾는 메서드
    private VcText findVcText(Long seq) {
        return vcTextRepository
                .findById(seq)
                .orElseThrow(() -> new VcNotFoundTextException("Text not found"));
    }

    private VcResultsRequest createVcSrcFile(VcSrcFile vcSrcFile) {
        // SRC 로 제일 최근에 저장한 Result 조회, 값이 없을 경우 null 처리
        VcResultFile vcResultFile =
                vcResultFileRepository.findFirstBySrcSeq_SrcSeqOrderBySrcSeqDesc(vcSrcFile.getSrcSeq());
        log.info("[vcService] getVcResponse vcResultFile 확인 : {} ", vcResultFile);
        return Optional.ofNullable(vcResultFile)
                .map(
                        file ->
                                VcResultsRequest.builder()
                                        .seq(file.getResSeq())
                                        .name(file.getFileName())
                                        .fileUrl(file.getFileUrl())
                                        .build())
                .orElse(null);
    }

    private VcTextRequest createText(VcSrcFile vcSrcFile) {
        // 제일 최근에 저장한 텍스트 조회, 값이 없을 경우 null 처리
        VcText vcText =
                vcTextRepository.findFirstBySrcSeq_SrcSeqOrderBySrcSeqDesc(vcSrcFile.getSrcSeq());
        log.info("[vcService] getVcResponse vcText 확인 : {} ", vcText);
        return Optional.ofNullable(vcText)
                .map(t -> VcTextRequest.of(t.getVtSeq(), t.getComment()))
                .orElse(null);
    }

    /**
     * trgSeq url을 찾는 메서드
     *
     * @param trgSeq
     * @return VcUrlRequest
     */
    private VcUrlRequest requestVcTrgUrl(Long trgSeq) {
        VcTrgFile trgFile =
                vcTrgFileRepository
                        .findById(trgSeq)
                        .orElseThrow(() -> new VcNotFoundTrgException("trgFile not found"));
        log.info("[vcService] vcTrgUrlRequest trgFile 확인 : {} ", trgFile);
        return VcUrlRequest.of(trgFile.getTrgSeq(), trgFile.getFileUrl());
    }

    private static String extractFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
