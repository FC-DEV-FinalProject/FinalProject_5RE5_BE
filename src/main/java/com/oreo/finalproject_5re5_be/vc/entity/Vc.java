package com.oreo.finalproject_5re5_be.vc.entity;

import com.oreo.finalproject_5re5_be.project.entity.Project;
import com.oreo.finalproject_5re5_be.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "vc_table")
@Getter
@Setter
@ToString
public class Vc extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "vc_seq")
    private Long vcSeq;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pro_seq")
    private Project proSeq;

    @Column(nullable = false, name = "activate")
    private char activate = 'Y';

    @Column(nullable = false, name = "vc_up_date")
    @LastModifiedDate
    private LocalDateTime update;
}
