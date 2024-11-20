package com.oreo.finalproject_5re5_be.concat.service;

//@AllArgsConstructor
//@NoArgsConstructor
//@Service
//public class ConcatTabService {
//    private ConcatTabRepository concatTabRepository;
//    private ProjectRepository projectRepository;
//    private ConcatTabHelper concatTabHelper;
//
//    /**
//     * @param projectSeq
//     * @return boolean
//     */
//    @Transactional
//    public boolean createConcatTab(long projectSeq) {
//        Optional<Project> projectOpt = projectRepository.findById(projectSeq);
//        if (concatTabRepository.existsById(projectSeq) || projectOpt.isEmpty()) {
//            return false;
//        }
//        Project project = projectOpt.get();
//        ConcatTab concatTab = new ConcatTab(project.getProSeq(), project, null, 'Y', 0.0f);
//        concatTabRepository.save(concatTab);
//        return true;
//    }
//
//    //중복 조회를 방지하기 위한 오버로드
//    @Transactional
//    public boolean createConcatTab(Project project) {
//        if (concatTabRepository.existsById(project.getProSeq())) {
//            return false;
//        }
//        ConcatTab concatTab = new ConcatTab(project.getProSeq(), project, null, 'Y', 0.0f);
//        concatTabRepository.save(concatTab);
//        return true;
//    }
//
//    //projectSeq가 사용자가 소유한 프로젝트의 id가 맞는지 확인 해야함
//    @Transactional
//    public ConcatTabResponseDto readConcatTab(long projectSeq, String memberSeq) {
//        // ConcatTab, Project 조회
//        Optional<ConcatTab> concatOpt = concatTabRepository.findById(projectSeq);
//        Optional<Project> projectOpt = projectRepository.findById(projectSeq);
//
//
//        if (projectOpt.isEmpty()) {
//            throw new NoSuchElementException("프로젝트를 찾을 수 없습니다.");
//        }
//
//        // 데이터가 존재할 경우 처리
//        if (concatOpt.isPresent()) {
//            return concatTabHelper.prepareConcatTab(concatOpt.get(), memberSeq);
//        }
//
//        // ConcatTab 생성 시도
//        if (createConcatTab(projectOpt.get())) {
//            // 생성 후 다시 조회
//            Optional<ConcatTab> newConcatTab = concatTabRepository.findById(projectSeq);
//            if (newConcatTab.isPresent()) {
//                return concatTabHelper.prepareConcatTab(newConcatTab.get(), memberSeq);
//            }
//        }
//
//        // 데이터가 없으면 예외 처리
//        throw new NoSuchElementException("해당하는 구성요소를 찾을 수 없습니다.");
//    }
//
//
//    @Transactional
//    public boolean updateConcatTab(ConcatUpdateRequestDto concatUpdateRequestDto) {
//        Member member = concatUpdateRequestDto.getMember();
//        if (member == null) {
//            return false;
//        }
//
//        // ConcatTab 및 Project 조회
//        ConcatTab concatTab = concatTabRepository.findById(concatUpdateRequestDto.getTabId())
//                .orElse(null);
//        Project project = projectRepository.findById(concatUpdateRequestDto.getTabId())
//                .orElse(null);
//
//        // 조회 실패 처리
//        if (concatTab == null || project == null) {
//            return false;
//        }
//
//        // 권한 확인
//        if (project.getMember().getId().equals(member.getId())) {
//            // 저장
//            ConcatTab updatedTab = new ConcatTab(
//                    concatUpdateRequestDto.getTabId(),
//                    project,
//                    concatUpdateRequestDto.getConcatOption(),
//                    concatUpdateRequestDto.getStatus(),
//                    concatUpdateRequestDto.getFrontSilence()
//            );
//
//            concatTabRepository.save(updatedTab);
//            return true;
//        }
//        return false;
//    }
//}


import com.oreo.finalproject_5re5_be.concat.dto.request.ConcatUpdateRequestDto;
import com.oreo.finalproject_5re5_be.concat.dto.response.ConcatTabResponseDto;
import com.oreo.finalproject_5re5_be.concat.entity.ConcatTab;
import com.oreo.finalproject_5re5_be.concat.repository.ConcatTabRepository;
import com.oreo.finalproject_5re5_be.concat.service.helper.ConcatTabHelper;
import com.oreo.finalproject_5re5_be.member.entity.Member;
import com.oreo.finalproject_5re5_be.project.entity.Project;
import com.oreo.finalproject_5re5_be.project.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ConcatTabService {

    private final ConcatTabRepository concatTabRepository;
    private final ProjectRepository projectRepository;
    private final ConcatTabHelper concatTabHelper;

    /**
     * 프로젝트에 대한 ConcatTab 생성
     *
     * @param projectSeq 프로젝트 ID
     * @return 생성 성공 여부
     */
    @Transactional
    public boolean createConcatTab(long projectSeq) {
        Optional<Project> projectOpt = projectRepository.findById(projectSeq);
        if (concatTabRepository.existsById(projectSeq) || projectOpt.isEmpty()) {
            return false;
        }

        Project project = projectOpt.get();

        // 빌더를 사용해 ConcatTab 생성
        ConcatTab concatTab = ConcatTab.builder()
                .projectId(project.getProSeq())
                .project(project)
                .status('Y')
                .frontSilence(0.0f)
                .build();

        concatTabRepository.save(concatTab);
        return true;
    }

    /**
     * 중복 조회를 방지하기 위한 오버로드
     *
     * @param project 프로젝트 객체
     * @return 생성 성공 여부
     */
    @Transactional
    public boolean createConcatTab(Project project) {
        if (concatTabRepository.existsById(project.getProSeq())) {
            return false;
        }

        // 빌더를 사용해 ConcatTab 생성
        ConcatTab concatTab = ConcatTab.builder()
                .projectId(project.getProSeq())
                .project(project)
                .status('Y')
                .frontSilence(0.0f)
                .build();

        concatTabRepository.save(concatTab);
        return true;
    }

    /**
     * 프로젝트 ID와 회원 ID로 ConcatTab 조회
     *
     * @param projectSeq 프로젝트 ID
     * @param memberSeq  회원 ID
     * @return ConcatTabResponseDto
     */
    @Transactional
    public ConcatTabResponseDto readConcatTab(long projectSeq, String memberSeq) {
        Optional<ConcatTab> concatOpt = concatTabRepository.findById(projectSeq);
        Optional<Project> projectOpt = projectRepository.findById(projectSeq);

        if (projectOpt.isEmpty()) {
            throw new NoSuchElementException("프로젝트를 찾을 수 없습니다.");
        }

        if (concatOpt.isPresent()) {
            return concatTabHelper.prepareConcatTab(concatOpt.get(), memberSeq);
        }

        // ConcatTab 생성 시도
        if (createConcatTab(projectOpt.get())) {
            Optional<ConcatTab> newConcatTab = concatTabRepository.findById(projectSeq);
            if (newConcatTab.isPresent()) {
                return concatTabHelper.prepareConcatTab(newConcatTab.get(), memberSeq);
            }
        }

        throw new NoSuchElementException("해당하는 구성요소를 찾을 수 없습니다.");
    }

    /**
     * ConcatTab 업데이트
     *
     * @param concatUpdateRequestDto 업데이트 요청 DTO
     * @return 업데이트 성공 여부
     */
    @Transactional
    public boolean updateConcatTab(ConcatUpdateRequestDto concatUpdateRequestDto) {
        Member member = concatUpdateRequestDto.getMember();
        if (member == null) {
            return false;
        }

        ConcatTab concatTab = concatTabRepository.findById(concatUpdateRequestDto.getTabId())
                .orElse(null);
        Project project = projectRepository.findById(concatUpdateRequestDto.getTabId())
                .orElse(null);

        if (concatTab == null || project == null) {
            return false;
        }

        if (project.getMember().getId().equals(member.getId())) {
            // 빌더를 사용해 업데이트된 ConcatTab 생성
            ConcatTab updatedTab = ConcatTab.builder()
                    .projectId(concatUpdateRequestDto.getTabId())
                    .project(project)
                    .option(concatUpdateRequestDto.getConcatOption())
                    .status(concatUpdateRequestDto.getStatus())
                    .frontSilence(concatUpdateRequestDto.getFrontSilence())
                    .build();

            concatTabRepository.save(updatedTab);
            return true;
        }

        return false;
    }
}
