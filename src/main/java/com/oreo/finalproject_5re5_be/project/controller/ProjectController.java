package com.oreo.finalproject_5re5_be.project.controller;

import com.oreo.finalproject_5re5_be.global.dto.response.ResponseDto;
import com.oreo.finalproject_5re5_be.member.entity.Member;
import com.oreo.finalproject_5re5_be.project.dto.response.ProjectResponse;
import com.oreo.finalproject_5re5_be.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@Validated
@CrossOrigin(origins = "*")
@RequestMapping("/api/project")
public class ProjectController {
    private ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }

    @Operation(
            summary = "Project 정보 검색",
            description = "회원 Seq로 프로젝트 정보를 가지고옵니다."
    )
    @GetMapping("")
    public ResponseEntity<ResponseDto<Map<String, List<ProjectResponse>>>> projectGet(
            @SessionAttribute("memberSeq") Long memberSeq){//session memberSeq값
        //회원정보로 프로젝트 추출
        List<ProjectResponse> projectResponses = projectService.projectFindAll(memberSeq);
        Map<String, List<ProjectResponse>> map = new HashMap<>();//맵 생성
        map.put("row", projectResponses);//row : [] 로 응답
        return ResponseEntity.ok()
                .body(new ResponseDto<>(HttpStatus.OK.value(), map));
    }
    @Operation(
            summary = "Project 저장",
            description = "회원 Seq로 프로젝트를 저장(생성)합니다."
    )
    @PostMapping("")
    public ResponseEntity<ResponseDto<Map<String,Object>>> projectSave(
            @SessionAttribute("memberSeq") Long memberSeq){//session memberSeq값
        //project 생성 
        Long projectSeq = projectService.projectSave(memberSeq);
        Map<String, Object> map = new HashMap<>();
        map.put("projectSeq", projectSeq);//프로젝트seq 응답에 추가
        map.put("msg", "프로젝트 생성 완료되었습니다.");//메시지 추가
        return ResponseEntity.ok()
                .body(new ResponseDto<>(HttpStatus.OK.value(),map));
    }
    @Operation(
            summary = "Project 이름 수정",
            description = "프로젝트 Seq와 변경할 이름을 받아 수정합니다."
    )
    @PutMapping("/{projectSeq}")
    public ResponseEntity<ResponseDto<String>> projectUpdate(@Valid @PathVariable Long projectSeq,
                                                             @Valid @RequestBody String text){
        projectService.projectUpdate(projectSeq, text);//프로젝트 수정
        return ResponseEntity.ok()
                .body(new ResponseDto<>(HttpStatus.OK.value(),
                        "Project 이름 변경 완료되었습니다.")); //응답 
    }
    @Operation(
            summary = "Project 삭제",
            description = "프로젝트 Seq를 받아 activate상태를 'N'으로 변경합니다."
    )
    @DeleteMapping("")
    public ResponseEntity<ResponseDto<String>> projectDelete(@RequestParam List<Long> projectSeq){
        projectService.projectDelete(projectSeq);//프로젝트 삭제 배열로 받음
        return ResponseEntity.ok()
                .body(new ResponseDto<>(HttpStatus.OK.value(),
                        "Project 삭제 완료되었습니다."));//모두 삭제
    }
}