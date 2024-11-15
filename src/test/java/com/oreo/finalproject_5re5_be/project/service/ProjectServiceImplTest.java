package com.oreo.finalproject_5re5_be.project.service;

import com.oreo.finalproject_5re5_be.member.entity.Member;
import com.oreo.finalproject_5re5_be.member.repository.MemberRepository;
import com.oreo.finalproject_5re5_be.project.dto.response.ProjectResponse;
import com.oreo.finalproject_5re5_be.project.entity.Project;
import com.oreo.finalproject_5re5_be.project.exception.InvalidProjectNameException;
import com.oreo.finalproject_5re5_be.project.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProjectServiceImplTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Member member;
    private Project project;

    @BeforeEach
    void setUp() {
        member = Member.builder().id("akbvFQMtnk2WFjgngJH").build();
        project = Project.builder().proSeq(1L).proName("Test Project").member(member).build();
    }
    @Test
    void projectFindAll() {
        List<Project> projects = List.of(project);
        when(projectRepository.findAll()).thenReturn(projects);

        List<ProjectResponse> projectResponses = projectService.projectFindAll();

        assertEquals(1, projectResponses.size());
        assertEquals("Test Project", projectResponses.get(0).getProjectName());
        verify(projectRepository, times(1)).findAll();

    }

    @Test
    void projectSave() {
        when(memberRepository.findById(any(String.class))).thenReturn(member);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Long projectSeq = projectService.projectSave();

        assertEquals(1L, projectSeq);
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(memberRepository, times(1)).findById(any(String.class));
    }

    @Test
    void should_throw_username_not_found_exception_when_member_not_found_in_project_save() {
        when(memberRepository.findById(any(String.class))).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> projectService.projectSave());
        verify(memberRepository, times(1)).findById(any(String.class));
    }


    @Test
    void projectUpdate() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        projectService.projectUpdate(1L, "Updated Project Name");

        verify(projectRepository, times(1)).save(any(Project.class));
        verify(projectRepository, times(1)).findById(anyLong());

    }
    @Test
    void should_throw_invalid_project_name_exception_when_name_is_invalid() {
        assertThrows(InvalidProjectNameException.class, () -> projectService.projectUpdate(1L, "No"));
        assertThrows(InvalidProjectNameException.class, () -> projectService.projectUpdate(1L, ""));
    }
    @Test
    void projectDelete() {
        List<Long> projectSeqList = List.of(1L);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        projectService.projectDelete(projectSeqList);

        verify(projectRepository, times(1)).save(any(Project.class));
        verify(projectRepository, times(1)).findById(anyLong());
    }
    @Test
    void should_throw_exception_when_project_not_found_in_delete() {
        List<Long> projectSeqList = List.of(1L);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> projectService.projectDelete(projectSeqList));
        verify(projectRepository, times(1)).findById(anyLong());
    }
}