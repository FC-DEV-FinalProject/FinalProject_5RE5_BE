package com.oreo.finalproject_5re5_be.project.service;

import com.oreo.finalproject_5re5_be.concat.repository.ConcatTabRepository;
import com.oreo.finalproject_5re5_be.member.entity.Member;
import com.oreo.finalproject_5re5_be.member.exception.MemberNotFoundException;
import com.oreo.finalproject_5re5_be.member.repository.MemberRepository;
import com.oreo.finalproject_5re5_be.project.dto.response.ProjectResponse;
import com.oreo.finalproject_5re5_be.project.entity.Project;
import com.oreo.finalproject_5re5_be.project.exception.InvalidProjectNameException;
import com.oreo.finalproject_5re5_be.project.exception.ProjectNotFoundException;
import com.oreo.finalproject_5re5_be.project.exception.ProjectNotMemberException;
import com.oreo.finalproject_5re5_be.project.repository.ProjectRepository;
import com.oreo.finalproject_5re5_be.tts.repository.TtsSentenceRepository;
import com.oreo.finalproject_5re5_be.vc.repository.VcRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
@Transactional
public class ProjectServiceImpl implements ProjectService {
    private ProjectRepository projectRepository;
    private MemberRepository memberRepository;
    private TtsSentenceRepository ttsSentenceRepository;
    private VcRepository vcRepository;
    private ConcatTabRepository concatTabRepository;

    @Autowired
    public ProjectServiceImpl(
            ProjectRepository projectRepository,
            MemberRepository memberRepository,
            TtsSentenceRepository ttsSentenceRepository,
            VcRepository vcRepository,
            ConcatTabRepository concatTabRepository) {
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.ttsSentenceRepository = ttsSentenceRepository;
        this.vcRepository = vcRepository;
        this.concatTabRepository = concatTabRepository;
    }

    /**
     * 프로젝트 회원 조회
     *
     * @return List<ProjectResponse>
     */
    @Override
    @Transactional
    public List<ProjectResponse> findAllProject(Long memberSeq) {
        log.info("[projectService] projectFindAll - memberSeq : {} ", memberSeq);
        Member member = findMember(memberSeq);
        log.info("[projectService] projectFindAll - member : {} ", member.toString());
        // member 확인후 예외 던지기
        //        memberSeqCheck(member.getSeq());

        // 회원 정보로 전체 조회
        List<Project> project = projectRepository.findByMemberSeq(member.getSeq());
        // 정보를 저장할 리스트 생성
        List<ProjectResponse> projectResponses = new ArrayList<>();
        List<Long> projectSeqs = new ArrayList<>();
        // project 정보를 모두 넣고
        for (Project p : project) {
            ProjectResponse projectResponse =
                    ProjectResponse.builder()
                            .projectSeq(p.getProSeq())
                            .projectName(p.getProName())
                            .projectContent(p.getProName())
                            .projectDate(p.getProDate())
                            .projectUpdateDate(p.getProUpDate())
                            .tts(ttsSentenceRepository.existsByProject_ProSeq(p.getProSeq()))
                            .vc(vcRepository.existsById(p.getProSeq()))
                            .concat(concatTabRepository.existsById(p.getProSeq()))
                            .projectActivate(p.getProActivate())
                            .build();
            projectResponses.add(projectResponse);
            projectSeqs.add(p.getProSeq());
        }
        log.info("회원이 조회한 Project Seqs : {}", projectSeqs);
        // Response 로 추출
        return projectResponses;
    }

    /**
     * 프로젝트 생성
     *
     * @return Long
     */
    @Override
    public Long saveProject(Long memberSeq) {
        // 회원정보 추출
        Member member = findMember(memberSeq);
        checkMemberSeq(member.getSeq());
        // 회원정보로 프로젝트 객체 생성
        Project project = Project.builder().member(member).build();
        try {
            // 저장
            Project save = projectRepository.save(project);
            log.info("Save project : {}", save);
            // 생성된 프로젝트 ID 정보 추출
            return save.getProSeq();
        } catch (DataAccessException e) {
            log.error("Error Save project 저장중 오류 : {} ", e.getMessage(), e);
            throw new RuntimeException("Project 저장 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 프로젝트 업데이트
     *
     * @param projectSeq
     * @param projectName
     */
    @Override
    public void updateProject(Long projectSeq, String projectName) {
        // 프로젝트 길이 제한
        validateProjectName(projectName);
        // 프로젝트 번호로 프로젝트 찾기
        Project projectFind = findProject(projectSeq);
        // 프로젝트 찾은 번호로 받은 프로젝트명으로 변경
        Project project = projectFind.toBuilder().proSeq(projectSeq).proName(projectName).build();
        // 수정
        try {
            projectRepository.save(project);
        } catch (DataAccessException e) {
            log.error("Error update Porject 프로젝트 이름 수정 오류: {} ", e.getMessage(), e);
            throw new RuntimeException("Porject 이름 수정 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 프로젝트 삭제(수정)
     *
     * @param projectSeq
     */
    @Override
    @Retryable(
            value = {DataAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    @Transactional
    public void deleteProject(List<Long> projectSeq) {
        // 리스트로 받은 프로젝트 번호를 조회
        try {
            for (int i = 0; i < projectSeq.size(); i++) {
                Project projectFind = findProject(projectSeq.get(i));
                // 프로젝트들의 상태를 N으로 변경
                Project project =
                        projectFind.toBuilder().proSeq(projectSeq.get(i)).proActivate('N').build();
                // 저장
                projectRepository.save(project);
            }
        } catch (DataAccessException e) {
            log.error("Error Save Porject 삭제(수정) 오류: {} ", e.getMessage(), e);
            throw new RuntimeException("Porject 삭제(수정) 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 회원의 프로젝트인지 확인 단일
     *
     * @param memberSeq
     * @param projectSeq
     * @return
     */
    @Override
    public boolean checkProject(Long memberSeq, Long projectSeq) {
        log.info(
                "[projectService] projectcheck - memberSeq,  projectSeq : {} | {}", memberSeq, projectSeq);
        Project project = findProject(projectSeq);
        log.info("[projectService] projectcheck - project : {} ", project.toString());
        Long seq = project.getMember().getSeq();
        log.info("[projectService] projectcheck - seq : {} ", seq);
        if (seq.equals(memberSeq)) {
            return true;
        }
        throw new ProjectNotMemberException();
    }

    /**
     * 회원의 프로젝트인지 확인 여러개
     *
     * @param memberSeq
     * @param projectSeq
     * @return
     */
    @Override
    public boolean checkProject(Long memberSeq, List<Long> projectSeq) {
        log.info(
                "[projectService] projectCheck - memberSeq, projectSeq : {} , {} ",
                memberSeq,
                projectSeq.toString());
        for (Long pro : projectSeq) {
            boolean b = checkProject(memberSeq, pro);
            log.info("[projectService] projectCheck - boolean : {} ", b);
        }
        return true;
    }

    // 길이 제한 메서드
    private void validateProjectName(String projectName) {
        if (projectName == null || projectName.length() < 3 || projectName.length() > 50) {
            throw new InvalidProjectNameException("프로젝트 이름은 3자 이상, 50자 이하여야 합니다.");
        }
    }

    private Project findProject(Long seq) {
        return projectRepository
                .findById(seq)
                .orElseThrow(() -> new ProjectNotFoundException("project not found"));
    }

    private Member findMember(Long seq) {
        return memberRepository
                .findById(seq)
                .orElseThrow(() -> new MemberNotFoundException("Member not found"));
    }

    private void checkMemberSeq(Long seq) {
        if (seq == null) {
            throw new MemberNotFoundException();
        }
    }
}
