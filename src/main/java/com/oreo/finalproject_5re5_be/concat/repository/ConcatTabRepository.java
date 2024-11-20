package com.oreo.finalproject_5re5_be.concat.repository;

import com.oreo.finalproject_5re5_be.concat.entity.ConcatTab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConcatTabRepository extends JpaRepository<ConcatTab, Long> {
    @Query("SELECT c FROM concat_tab c WHERE c.project.proSeq = :projectId")
    Optional<ConcatTab> findByProjectId(@Param("projectId") Long projectId);
}
